package com.example.plugin;


import com.example.plugin.annotation.MyAnnotation;

/**
 * Created by zeal on 2019/2/13.
 */
@MyAnnotation(getClz = IDemo.class)
public class MyDemo implements IDemo {

    @Override
    public void demo() {
        System.out.println("demo....");
    }
}
