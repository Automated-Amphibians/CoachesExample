//package declaration
package org.aa8426.examples.basicjava.doggies;

// class declaration
public class Dog {
    
    // Property or "member variable" when it is an instance
    protected String name; 

    // Constructor to initialize the property (or member variable)
    public Dog(String name) {
        this.name = name;
    }

    public void jump() {
        System.out.println(this.name + " jumps");
    }

    // Method to simulate the dog barking
    public void bark() {        
        System.out.println(name + " says: Woof! Woof!");
    }

}
