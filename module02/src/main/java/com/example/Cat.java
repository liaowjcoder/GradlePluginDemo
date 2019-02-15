package com.example;

import com.example.module.Animal;
import com.example.plugin.annotation.MyAnnotation;

/**
 * Created by zeal on 2019/2/14.
 */

@MyAnnotation(getClz = Animal.class)
public class Cat implements Animal {
    @Override
    public void eat() {
        System.out.println("Cat eat...");
    }
}
