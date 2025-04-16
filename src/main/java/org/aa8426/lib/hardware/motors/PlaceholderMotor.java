package org.aa8426.lib.hardware.motors;

public class PlaceholderMotor extends Motor {
            
    double position = 0.0;
    
    public PlaceholderMotor(MotorTypeName type, int id, int stallLimit, int freeLimit, double countsPerRev) {
        super(type, id, countsPerRev, stallLimit, freeLimit);
    }

    @Override
    public void set(double power) {
        this.power = power;
    }

    @Override
    public double get() {
        return power;
    }

    @Override
    public double getPosition() {
        return position;
    }

    @Override
    public void setPosition(double value) {
        position = value;
    }

    @Override
    public double getVelocity() {
        return power * 3000;
    }

    @Override
    public void setBrakeMode(boolean useBrakeMode) {
        brakeMode = useBrakeMode;
    }

    @Override
    public double getCurrent() {
        return 0.0;
    }

    @Override
    public double getVoltage() {        
        return 12.0;
    }

    @Override
    public void resetPosition() {
        setPosition(0);
    }

    @Override
    public Motor addFollower(int id, boolean inverted) {
        return this;
    }

    @Override
    public double getTemp() {
        return 32;
    }

  
}
