package org.aa8426.examples.basicjava;

import org.aa8426.examples.basicjava.doggies.Dog;

public class ClassExampleMain {
    public static void main(String[] args) {
        Dog buddy = new Dog("Buddy");
        buddy.jump();
        buddy.bark();
    }
}
