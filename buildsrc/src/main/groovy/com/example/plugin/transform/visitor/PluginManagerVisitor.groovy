package com.example.plugin.transform.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class PluginManagerVisitor extends ClassVisitor {

    PluginManagerVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv)
    }

    @Override
    void visitEnd() {
        super.visitEnd()


        if (cv instanceof ClassWriter) {
            ClassWriter classWriter = cv;

            FieldVisitor fv = classWriter.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "map", "Ljava/util/HashMap;", "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/String;>;", null);
            fv.visitEnd();

            classWriter.visitEnd()


        }

    }

}
