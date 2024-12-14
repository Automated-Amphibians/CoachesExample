// PACKAGE DECLARATION
package org.aa8426.examples.basicjava;

// IMPORTS
import java.time.LocalDate;

// CLASS DELCARATION
public class SimpleExampleMain {

    // MEMBER VARIABLE DECLARATIONS
    protected int state;
    protected String another_thing;

    // CONSTRUCTOR
    public SimpleExampleMain(int state) {
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

        int y = 2;
        for(int x=0;x<10;x++) {
            System.out.println("Hello: "+x*y);
        }

        LocalDate today = LocalDate.now(); // Using the imported LocalDate class
        System.out.println("Today's date is " + today + ".");        

        exampleStaticCall("Hey there!");
    }
}
