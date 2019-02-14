package com.example.plugin.transform.visitor

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

    MyClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean b) {

        if (Type.getDescriptor(MyAnnotation.class).equals(desc)) {
            isInject = true;
        }
        println("visitAnnotation desc = " + desc);
        return super.visitAnnotation(desc, b)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        println("visit name = " + name);
    }


    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        println("visitMethod name=" + name + ",desc=" + desc + ",signature=" + signature);


        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        MyMethodVisitor mMethodVisitor = new MyMethodVisitor(Opcodes.ASM5, mv, access, name, desc);
        return mMethodVisitor;
    }

    @Override
    void visitEnd() {
        super.visitEnd()
        println("visitEnd")
    }
}
