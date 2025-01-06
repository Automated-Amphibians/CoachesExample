// PACKAGE DECLARATION
package org.aa8426.examples.basicjava;

// IMPORTS
import java.time.LocalDate;

// CLASS DECLARATION
public class SuperSimpleExampleMain {

    // MEMBER VARIABLE DECLARATIONS
    protected int state;    

    // CONSTRUCTOR
    public SuperSimpleExampleMain(int state) {
        this.state = state;
    }

    // INSTANCE METHOD (or just METHOD)
    public void exampleInstanceMethod(String parameter) {
        System.out.println(parameter+":"+state);
    }

    // STATIC METHOD
    public static void exampleStaticCall(String parameter) {
        System.out.println(parameter);
    }

    // MAIN
    public static void main(String[] args) {

        LocalDate today = LocalDate.now(); // Using the imported LocalDate class
        System.out.println("Today's date is " + today + ".");        

        exampleStaticCall("Hey there!");
    }
}
