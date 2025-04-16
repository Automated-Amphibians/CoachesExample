package org.aa8426.lib;

public class NaiveLogger {
    
    private String lastVal = null;
    
    public void log(String value) {  
        if (value.equals(lastVal)) {
            return;
        }
        lastVal = value;
        System.out.println(value);
    }
    
}
