package com.example.plugin.transform.visitor

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter


class MyMethodVisitor extends AdviceAdapter {

    protected MyMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc) {
        super(api, mv, access, name, desc)
    }

    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {

        println("MyMethodVisitor visitAnnotation:" + desc)

        return super.visitAnnotation(desc, visible)
    }

    @Override
    void visitEnd() {
        super.visitEnd()

        println("MyMethodVisitor visitEnd" )
    }
}
