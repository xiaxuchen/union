package com.originit.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class HelloTask extends DefaultTask{

    HelloTask() {
        group = "hello"
        description = "print Hello info"
    }

    @TaskAction
    void doAction () {
        def name = project.extensions.helloExt.name
        def age = project.extensions.helloExt.age
        println "${name} is ${age} years old"
    }
}
