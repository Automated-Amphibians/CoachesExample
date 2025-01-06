package org.aa8426.examples.basicjava.doggies;

import java.time.LocalDate;

// By extending Dog, BorderCollie inherits fields and methods from Dog
public class BorderCollie extends Dog {
    
    public BorderCollie(String name) {
        super(name);
    }

    // lets introduce new behaviors
    public void rollover() {
        System.out.println(this.name + " rolls over");
    }

    // or enhance existing behaviors
    @Override
    public void bark() {        
        super.bark();
        LocalDate today = LocalDate.now(); // Using the imported LocalDate class        
        System.out.println(name + " says: Today's date is " + today + ".");        
    }

}
