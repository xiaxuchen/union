package com.originit.gradle

class HelloExtension {

    String name
    Integer age


    @Override
    public String toString() {
        return "HelloExtension{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}'
    }
}
