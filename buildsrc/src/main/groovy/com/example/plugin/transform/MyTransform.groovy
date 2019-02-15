package com.example.plugin.transform

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.example.plugin.transform.visitor.MyAnnotationVisitor
import com.example.plugin.transform.visitor.MyClassVisitor
import com.example.plugin.transform.visitor.PluginManagerVisitor
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Sets
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import groovy.io.FileType
import org.apache.commons.compress.utils.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.apache.commons.codec.digest.DigestUtils
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class MyTransform extends Transform {

    private HashMap<String, String> plugins = new HashMap<>();

    @Override
    String getName() {
        return MyTransform.simpleName
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
//
    }

    @Override
    boolean isIncremental() {
        return false//是否开启增量编译
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        //在这里得到的 TransformInput 有两种类型，分别是 Jar 和 Directory
        transformInvocation.inputs.each {
            TransformInput transformInput ->

                transformInput.directoryInputs.each { DirectoryInput directoryInput ->
                    if (directoryInput.file.isDirectory()) {

                        directoryInput.file.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                            File classFile ->

                                def name = classFile.name;



                                if (name.endsWith(".class") && !name.startsWith("R\$") && !"R.class".equals(name) && !"BuildConfig.class".equals(name)) {
//                                    println("要操作的类：" + name)
                                    ClassReader cr = new ClassReader(classFile.bytes)
                                    ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                                    MyClassVisitor cv = new MyClassVisitor(cw)
                                    cr.accept(cv, ClassReader.EXPAND_FRAMES)

                                    //扫描注解
                                    scan(cv);

                                }
                        }
                    }
                    //在transform方法中，我们将每个jar包和class文件复制到dest路径，这个dest路径就是下一个Transform的输入数据
                    def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                    com.android.utils.FileUtils.copyDirectory(directoryInput.file, dest)

                    println(">>>src:"+directoryInput.file+",dest:"+dest)

                }

                //遍历 Jar
                transformInput.jarInputs.each {
                    JarInput jarInput ->
//                        println "jar:" + jarInput.name + "," + jarInput.getFile()

                        //得到 jar 的绝对路径
                        String jarPath = jarInput.file.getAbsolutePath();

                        //过滤
                        //Android 的jar 就过滤掉
                        if (jarPath.endsWith(".jar") && !jarPath.contains("com.android.support") && !jarPath.contains("/com/android/support")) {

                            JarFile jarFile = new JarFile(jarInput.file)
                            Enumeration enumeration = jarFile.entries()
                            while (enumeration.hasMoreElements()) {
                                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                                String entryName = jarEntry.getName()
//                                println "========= jarInput class entryName = " + entryName
                                if (entryName.endsWith(".class") && isIgonre(name)) {
                                    InputStream inputStream = jarFile.getInputStream(jarEntry)
                                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                                    MyClassVisitor cv = new MyClassVisitor(classWriter)
                                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                                    //扫描注解
                                    scan(cv);
                                    inputStream.close()
                                }
                            }
                            jarFile.close()
                        }


                        //处理jar进行字节码注入处理
                        def dest = transformInvocation.outputProvider.getContentLocation(jarInput.file.absolutePath, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                        com.android.utils.FileUtils.copyFile(jarInput.file, dest)

                        println("src:"+jarInput.file+",dest:"+dest)
                }
        }

        //打印plugins扫描结果
        println "扫描结果："
        plugins.each {
            key, value ->
                println key + "==" + value
        }

        File meta_file = transformInvocation.outputProvider.getContentLocation("neacy", getOutputTypes(), getScopes(), Format.JAR)
        if (!meta_file.getParentFile().exists()) {
            meta_file.getParentFile().mkdirs()
        }
        if (meta_file.exists()) {
            meta_file.delete()
        }

        FileOutputStream fos = new FileOutputStream(meta_file)
        JarOutputStream jarOutputStream = new JarOutputStream(fos)
        NeacyRouterWriter neacyRouterWriter = new NeacyRouterWriter()
        ZipEntry zipEntry = new ZipEntry("com/example/plugins/PluginManager.class")
        jarOutputStream.putNextEntry(zipEntry)
        jarOutputStream.write(neacyRouterWriter.generateClass("com/example/plugins/PluginManager", plugins))
        jarOutputStream.closeEntry()
        jarOutputStream.close()
        fos.close()
    }

    private void scan(MyClassVisitor visitor) {
        MyAnnotationVisitor myAnnotationVisitor = visitor.getMyAnnotationVisitor();
        if (myAnnotationVisitor != null && myAnnotationVisitor.value != null && myAnnotationVisitor.className != null) {
            plugins.put(myAnnotationVisitor.value,myAnnotationVisitor.className)
        }
    }

    /**
     * 应该忽略的class
     */
    private boolean isIgonre(String name) {
        return (!name.endsWith("R.class")
                && !name.endsWith("BuildConfig.class")
                && !name.contains("R\$")
        )
    }

}