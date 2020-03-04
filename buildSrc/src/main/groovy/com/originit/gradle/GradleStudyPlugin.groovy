package com.originit.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class GradleStudyPlugin implements Plugin<Project>{
    @Override
    void apply(Project project) {
        project.extensions.create('helloExt',HelloExtension)
        project.tasks.create("sayHello",HelloTask)
        println "hello 123321"
    }
}
