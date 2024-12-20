package org.aa8426.examples.leds;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class LedSegment {
    
    private int idx;
    private int len;
    private LedDisplay leds;    

    public LedSegment(LedDisplay leds, int idx, int len) {
        this.leds = leds;        
        this.idx = idx;        
        this.len = len;
    }

    
    public void setLedState(Color c, boolean setData) {
        leds.setLedState(idx, len, c, setData);
    }

    public Command setLedStateCmd(Color c) {
        return Commands.runOnce(() -> {
            setLedState(c, true);
        });
    }    
    
}
