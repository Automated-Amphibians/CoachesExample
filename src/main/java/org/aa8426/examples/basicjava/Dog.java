package org.aa8426.examples.basicjava;

public class Dog {
    // Property or "member variable" when it is an instance
    protected String name;    

    // Constructor to initialize the property
    public Dog(String name) {
        this.name = name;
    }

    // Method to simulate the dog barking
    public void bark() {        
        System.out.println(name + " says: Woof! Woof!");
    }

}
