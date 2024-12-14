package org.aa8426.examples.basicjava.doggies;

import java.time.LocalDate;

public class BorderCollie extends Dog {
    
    public BorderCollie(String name) {
        super(name);
    }

    public void rollover() {
        System.out.println(this.name + " rolls over");
    }

    public void jump() {
        System.out.println(this.name + " jumps");
    }

    @Override
    public void bark() {
        LocalDate today = LocalDate.now(); // Using the imported LocalDate class
        System.out.println(name + " says: Woof! Woof! Today's date is " + today + ".");        
    }

}
