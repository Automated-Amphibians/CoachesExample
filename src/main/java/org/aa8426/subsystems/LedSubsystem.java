package org.aa8426.subsystems;

import org.aa8426.examples.leds.LedDisplay;
import org.aa8426.examples.leds.LedSegment;

import edu.wpi.first.wpilibj.util.Color;

public class LedSubsystem {
    // left public as an example
    public LedDisplay ledDisplay = new LedDisplay(0, 256);
    private LedSegment visionSignal = ledDisplay.leds(0, 32);
    private LedSegment intakeSignal = ledDisplay.leds(32, 32);

    public LedSubsystem() {

    }

    public void setVisionSignal(Color c, boolean setData) {
        visionSignal.setLedState(c, setData);
    }

    public void setIntakeSignal(Color c, boolean setData) {
        intakeSignal.setLedState(c, setData);
    }

    public void setAll(Color c, boolean setData) {
        ledDisplay.entireLed().setLedState(c, setData);
    }


}
