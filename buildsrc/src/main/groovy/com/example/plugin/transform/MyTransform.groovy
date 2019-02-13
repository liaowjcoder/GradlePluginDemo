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
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.internal.FileUtils;


class MyTransform extends Transform {


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
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        //在这里得到的 TransformInput 有两种类型，分别是 Jar 和 Directory
        transformInvocation.inputs.each {
            TransformInput transformInput ->

                transformInput.directoryInputs.each { DirectoryInput directoryInput ->
                    if (directoryInput.file.isDirectory()) {
                        directoryInput.file.eachFileRecurse { File file ->
                            // ...对目录进行插入字节码
                            println "dir = " + file
                        }
                    }


                    def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                    com.android.utils.FileUtils.copyDirectory(directoryInput.file, dest)

                }

                //遍历 Jar
                transformInput.jarInputs.each {
                    JarInput jarInput ->
                        println "jar:" + jarInput.name + "," + jarInput.getFile()
                        def jarName = jarInput.name
                        def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                        if (jarName.endsWith(".jar")) {
                            jarName = jarName.substring(0, jarName.length() - 4)
                        }
                        //处理jar进行字节码注入处理
                        def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                        com.android.utils.FileUtils.copyFile(jarInput.file, dest)
                }
        }

    }
}