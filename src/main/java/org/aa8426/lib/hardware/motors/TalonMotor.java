package org.aa8426.lib.hardware.motors;


import java.util.ArrayList;
import java.util.List;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TalonMotor extends Motor {
    
    public TalonFX motor;

    protected final List<TalonMotor> followers = new ArrayList<>();
    protected TalonMotor parent = null;    
    protected TalonFXConfiguration cfg;
    protected double position = 0;

    // https://v6.docs.ctr-electronics.com/en/latest/docs/api-reference/device-specific/talonfx/basic-pid-control.html
    public TalonFXConfiguration getTalonConfig() {
        TalonFXConfiguration cfg = new TalonFXConfiguration();        
        //currentLimitsConfigs.set
        cfg.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        //cfg.ClosedLoopGeneral.ContinuousWrap = true; // probably not necessary
        cfg.CurrentLimits.withStatorCurrentLimit(stallLimit)            
            .withStatorCurrentLimitEnable(true);
        cfg.MotorOutput.withInverted(InvertedValue.Clockwise_Positive);
        cfg.MotorOutput.NeutralMode = NeutralModeValue.Coast;                
        //cfg.
        //cfg.Feedback.RotorToSensorRatio.
        //cfg.Feedback.withRotorToSensorRatio(countsPerRev);
        cfg.Feedback.withSensorToMechanismRatio(countsPerRev);
        //cfg.MotorOutput.withInverted(InvertedValue.Clockwise_Positive)        
        return cfg;
    }

    public TalonMotor(MotorTypeName type, int id, int stallLimit, int freeLimit, double countsPerRev) {
        super(type, id, countsPerRev, stallLimit, freeLimit);        

        motor = new TalonFX(id);
        motor.clearStickyFaults();
        this.cfg = getTalonConfig();                
        motor.getConfigurator().apply(cfg);                
        //cfg.Feedback.withRotorToSensorRatio(countsPerRev)        
    }

    public TalonMotor(int id, boolean inverted, TalonMotor parent) {
        super(parent.type, id, parent.countsPerRev, parent.stallLimit, parent.freeLimit);

        motor = new TalonFX(id);
        motor.clearStickyFaults();

        parent.followers.add(this);        
        this.parent = parent;
            
        motor.setControl(new Follower(parent.id, inverted));
        //motor.configure(cfg, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters); 
    }


    @Override
    public void set(double power) {
        if (Math.abs(power) < 0.01) { 
            power = 0;
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
        if (RobotBase.isReal()) {
            return motor.getPosition().getValueAsDouble();
        } else {
            return position;
        }        
    }

    @Override
    public void setPosition(double value) {      
        // calling setPosition over and over in simulation (or for real) is bad for performance 
        // so, we just pretend. In real life, we don't set position directly very often.
        if (RobotBase.isReal()) {
            motor.setPosition(value);
        } else {          
            position = value;
        }        
    }

    @Override
    public double getVelocity() {
        return motor.getVelocity().getValueAsDouble();
    }

    @Override
    public void setBrakeMode(boolean useBrakeMode) {
        brakeMode = useBrakeMode;
        TalonFXConfiguration cfg = new TalonFXConfiguration();        
        if (useBrakeMode) {
            cfg.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        } else {
            cfg.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        }
        motor.getConfigurator().apply(cfg);                
    }

    @Override
    public double getCurrent() {
        SmartDashboard.putString("current-unit", motor.getStatorCurrent().getValue().baseUnit().name());
        return motor.getStatorCurrent().getValue().baseUnitMagnitude();
    }


    @Override
    public double getVoltage() {
        return motor.getSupplyVoltage().getValueAsDouble();
    }    

    public void test() {
        /*
        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10); // Set the motor direction if necessary 
        motor.setInverted(false); // Set to true if motor is wired in reverse 
        // Set the motor controller in position control mode (PID mode) 
        motor.configNominalOutputForward(0, 10); 
        motor.configNominalOutputReverse(0, 10); 
        motor.configPeakOutputForward(1, 10); 
        motor.configPeakOutputReverse(-1, 10); 
        // PID coefficients, tweak these for your specific robot 
        motor.config_kP(0, 0.5, 10); 
        // Proportional gain 
        motor.config_kI(0, 0.0, 10); 
        // Integral gain (if needed) 
        motor.config_kD(0, 0.0, 10); // Derivative gain (if needed)
        */
    }

    @Override
    public void resetPosition() {
        setPosition(0);
    }

    @Override
    public Motor addFollower(int id, boolean inverted) {
        new TalonMotor(id, inverted, this);
        return this;
    }

    @Override
    public double getTemp() {
        return motor.getDeviceTemp().getValueAsDouble();
    }



}
