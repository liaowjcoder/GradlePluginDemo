package com.example.plugin.transform

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class MyTransformPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {


        AppExtension appExtension =   project.extensions.getByType(AppExtension.class)

        appExtension.registerTransform(new MyTransform())


    }
}