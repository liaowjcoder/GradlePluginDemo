package com.example.plugin.transform.visitor

import com.example.plugin.annotation.MyAnnotation
import org.objectweb.asm.*

/**
 * Created by zeal on 2019/2/13.
 */


class MyAnnotationVisitor extends AnnotationVisitor {
    public String value;
    public String className;

    MyAnnotationVisitor(String className, AnnotationVisitor av) {
        super(Opcodes.ASM5, av)

        this.className=className.replaceAll("/",".")

    }


    @Override
    void visit(String name, Object value) {
        super.visit(name, value)
        // MyAnnotationVisitor name = getClz,value = Lcom/example/plugin/IDemo;,className=com/example/plugin/MyDemo
        println("MyAnnotationVisitor name = " + name + ",value = " + value + ",className=" + className)

        def clzName = value.toString() as String

        if (value.toString().startsWith("L") && value.toString().endsWith(";")) {
            this.value = clzName.substring(1, clzName.length() - 1).replaceAll("/", ".")
        }

    }

}
