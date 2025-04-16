package org.aa8426.lib.hardware.motors;

import java.util.ArrayList;
import java.util.List;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import com.revrobotics.spark.config.SparkMaxConfig;

public class SparkMaxMotor extends Motor {
    
    public final SparkMax motor;
    private final List<SparkMaxMotor> followers = new ArrayList<>();
    protected SparkMaxMotor parent = null;
    

    public SparkMaxConfig ezConfig(int stallLimit, int freeLimit, double countsPerRev) {
        SparkMaxConfig config = new SparkMaxConfig();
        //config.idleMode(IdleMode.kBrake);
        config.idleMode(IdleMode.kCoast); 
        config.encoder.positionConversionFactor(countsPerRev);
        config.encoder.velocityConversionFactor(countsPerRev);
        config.smartCurrentLimit(stallLimit, freeLimit);        

        //config.closedLoop.feedbackSensor(null)
        
        System.out.println("**************** "+this.countsPerRev);
        return config;
    }       
        
    public SparkMaxMotor(MotorTypeName type, int id, int stallLimit, int freeLimit, double countsPerRev) {
        super(type, id, countsPerRev, stallLimit, freeLimit);

        if (countsPerRev < 0.0001) {
            System.out.println("****************** YOU PROBABLY DID NOT MEAN TO SET COUNTS PER REV TO 0 or less than 1/10000. Check for accidental conversion to integer.");
            System.out.println("****************** YOU PROBABLY DID NOT MEAN TO SET COUNTS PER REV TO 0 or less than 1/10000. Check for accidental conversion to integer.");
            System.out.println("****************** YOU PROBABLY DID NOT MEAN TO SET COUNTS PER REV TO 0 or less than 1/10000. Check for accidental conversion to integer.");
        }
        motor = new SparkMax(id, MotorType.kBrushless);
        motor.clearFaults();
        SparkMaxConfig cfg = ezConfig(stallLimit, freeLimit, countsPerRev);
        motor.configure(cfg, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);        
        resetPosition();
    }

    public SparkMaxMotor(int id, boolean inverted, SparkMaxMotor parent) {
        super(parent.type, id, parent.countsPerRev, parent.stallLimit, parent.freeLimit);

        motor = new SparkMax(id, MotorType.kBrushless);

        parent.followers.add(this);        
        this.parent = parent;

        
        SparkMaxConfig cfg = ezConfig(parent.stallLimit, parent.freeLimit, countsPerRev);
        cfg.follow(parent.id, inverted);
        motor.configure(cfg, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters); 
    }

    @Override
    public void set(double power) {
        if (Math.abs(power) < 0.01) { 
            power = 0;
        }
        if (debug) {
            System.out.println("power: "+power);
        }
        this.power = power;
        motor.set(power);        
    }

    @Override
    public double get() {
        return motor.get();
    }

    @Override
    public double getPosition() {        
        return motor.getEncoder().getPosition();
    }    

    @Override
    public void setPosition(double value) {        
        motor.getEncoder().setPosition(value);
    }

    @Override
    public double getVelocity() {        
        return motor.getEncoder().getVelocity();
    }

    @Override
    public void setBrakeMode(boolean useBrakeMode) {
        brakeMode = useBrakeMode;
        SparkMaxConfig config = new SparkMaxConfig();
        if (useBrakeMode) {
            config.idleMode(IdleMode.kBrake); 
        } else {
            config.idleMode(IdleMode.kCoast); 
        }
        motor.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
    }

    @Override
    public double getCurrent() {
        return motor.getOutputCurrent();
    }

    @Override
    public double getVoltage() {
        return motor.getBusVoltage();
        
    }

    @Override
    public void resetPosition() {
        setPosition(0);
    }

    @Override
    public Motor addFollower(int id, boolean inverted) {
        followers.add(new SparkMaxMotor(id, inverted, this));
        return this;        
    }

    @Override
    public double getTemp() {
        return motor.getMotorTemperature();
    }
    
}
