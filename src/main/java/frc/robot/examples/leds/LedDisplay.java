package frc.robot.examples.leds;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

public class LedDisplay {
    
    private AddressableLED m_led;
    private AddressableLEDBuffer m_ledBuffer;        
    public int size;
    private LedSegment entireLed;

    public LedDisplay(int port, int size) {                
        this.size = size;

        m_led = new AddressableLED(port);        
        
        // Reuse buffer
        // Default to a length of 60, start empty output
        // Length is expensive to set, so only set it once, then just update data
        m_ledBuffer = new AddressableLEDBuffer(size);
        m_led.setLength(m_ledBuffer.getLength());
    
        // Set the data
        clear();
        m_led.start();        
        entireLed = new LedSegment(this, 0, size);
    }

    public void clear() {
        for (var i = 0; i < m_ledBuffer.getLength(); i++) {
          m_ledBuffer.setLED(i, Color.kBlack);
        }        
    }

    public LedSegment leds(int idx, int len) {
        return new LedSegment(this, idx, len);
    }

    public LedSegment entireLed() {
        return entireLed;
    }

    /** Call this function only once at the end */
    public void setData() {                
        m_led.setData(m_ledBuffer);        
    }        
        
    protected void setLedState(int start, int len, Color c, boolean setData) {
        for(int x=0;x<len;x++) {
            m_ledBuffer.setLED(start+x, c);            
        }
        if (setData) {
            setData();
        }
    }

    protected void setLedState(int start, int len, String hexColor, boolean setData) {
        Color c = new Color(hexColor);
        setLedState(start, len, c, setData);        
    }

    public void setVisionSignal(Color c) {
        setLedState(0, 32, c, false);
    }

    public void setIntakeSignal(Color c) {
        setLedState(32, 32, c, false);
    }

    public void setShooterReadySignal(Color c) {
        setLedState(64, 32, c, false);
    }
    
}
