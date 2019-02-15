package com.example.plugin.transform.visitor

import apple.laf.JRSUIConstants
import com.example.plugin.annotation.MyAnnotation
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

import org.objectweb.asm.Type

/**
 * Created by zeal on 2019/2/13.
 */


class MyClassVisitor extends ClassVisitor {
    private boolean isInject;
    private String className = null;
    private MyAnnotationVisitor myAnnotationVisitor;

    MyClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        println("visit name = " + name);//com/example/plugin/MyDemo
        className = name;
    }

    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean b) {

        if (Type.getDescriptor(MyAnnotation.class).equals(desc)) {
            isInject = true;
        } else {
            return cv.visitAnnotation(desc, b);
        }
        //apple.laf.JRSUIConstants$Animating
        //println "===" + JRSUIConstants.Animating.class.getName()
        //apple.laf.JRSUIConstants.Animating
        //println ">>>>"+JRSUIConstants.Animating.class.canonicalName

        //Lcom/example/plugin/annotation/MyAnnotation;
        println("visitAnnotation desc = " + desc);

        //得到类上的注解MyAnnotation，得到其value值即可得到实现的接口


        AnnotationVisitor av = cv.visitAnnotation(desc, b)

        myAnnotationVisitor = new MyAnnotationVisitor(className, av)
        return myAnnotationVisitor
    }


    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//        println("visitMethod name=" + name + ",desc=" + desc + ",signature=" + signature);
//        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
//        MyMethodVisitor mMethodVisitor = new MyMethodVisitor(Opcodes.ASM5, mv, access, name, desc);
        return cv.visitMethod(access,name,desc,signature,exceptions);
    }

    @Override
    void visitEnd() {
        super.visitEnd()
//        println("visitEnd")
    }

    public MyAnnotationVisitor getMyAnnotationVisitor() {
        return myAnnotationVisitor;
    }
}
