package org.aa8426.lib.hardware.motors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import org.aa8426.lib.PIDControl;
import org.aa8426.lib.TimedBooleanSupplier;
import org.aa8426.lib.dashboard.SendableFluent;
import org.aa8426.lib.dashboard.SendableFluent.ISendableFluent;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.Subsystem;


public abstract class Motor implements ISendableFluent {
    
    protected PIDControl pid = null;
    
    protected int id;        
    protected MotorTypeName type;    
    protected double countsPerRev;
    protected int stallLimit;
    protected int freeLimit;
    protected double power = 0.0;
    protected double simRate = 50.0; // the rate at which the position moves. 500 is a good speed
    public Boolean brakeMode = null;
    protected double lastSimulate;
    public boolean debug = false;
    public List<Motor> followers = new ArrayList<>();

    static List<Motor> motors = new ArrayList<>();
    
    public Motor(MotorTypeName type, int id, double countsPerRev, int stallLimit, int freeLimit) {
            this.type = type;
            this.id = id;
            this.countsPerRev = countsPerRev;
            this.stallLimit = stallLimit;
            this.freeLimit = freeLimit;
            //motors.add(this);
    }

    static public enum MotorTypeName {
        UNKNOWN(new String[] {}),
        TALON_KRAKEN(new String[] {"KRAKEN", "TALON", "TALONFX"}),
        SPARKMAX_NEO(new String[] {"NEO", "SPARKMAX"}),
        PLACEHOLDER(new String[] {"PLACEHOLDER"});
        
        private String[] matches;

        MotorTypeName(String[] matches) {
            this.matches = matches;
        }

        static public MotorTypeName fromValue(String motorTypeAsString) {
            if (motorTypeAsString == null) {
                return UNKNOWN;
            }
            for(MotorTypeName test:MotorTypeName.values()) {
                for(int x=0;x<test.matches.length;x++) {
                    if (test.matches[x].equals(motorTypeAsString.toUpperCase())) {
                        return test;
                    }
                }
            }
            return MotorTypeName.UNKNOWN;
        }
    }
    
    public static void startMotorSimulation() {
        if (RobotBase.isSimulation()) {
            Commands.run(() -> {
                for(Motor m:motors) {
                    m.simulate();
                }
            }).schedule();
         }
    }

    public static Motor create(MotorTypeName motorType, int id, int stallLimit, int freeLimit, double countsPerRev) {
        Motor m;
        switch(motorType) {
           case SPARKMAX_NEO: m = new SparkMaxMotor(motorType, id, stallLimit, freeLimit, countsPerRev); break;
           case TALON_KRAKEN: m = new TalonMotor(motorType, id, stallLimit, freeLimit, countsPerRev); break;
           case PLACEHOLDER: m = new PlaceholderMotor(motorType, id, stallLimit, freeLimit, countsPerRev); break;                                
           default: return null;
        }
        motors.add(m);
        return m;
    }

    public static Motor create(String type, int id, int stallLimit, int freeLimit, int countsPerRev) {
        return create(MotorTypeName.fromValue(type), id, stallLimit, freeLimit, countsPerRev);        
    }

    abstract public void set(double power);
    private void simulate() {                
        if (Math.abs(power) < 0.005) {
            power = 0;
        }
        double time = Timer.getFPGATimestamp();
        setPosition(getPosition() + (power * (simRate * (time - lastSimulate))));
        lastSimulate = time;            
    }
    abstract public double get();    
    abstract public double getPosition();    
    abstract public void setPosition(double value);
    abstract public void resetPosition();
    abstract public double getVelocity();
    public void setBrakeMode(boolean useBrakeMode) {
        brakeMode = useBrakeMode;
    }
    abstract public double getCurrent();
    abstract public double getVoltage();
    abstract public double getTemp();
    abstract public Motor addFollower(int id, boolean inverted);

    public void useBrakeMode() {
        setBrakeMode(true);
    }

    public void useCoastMode() {
        setBrakeMode(false);
    }

    public Command useBrakeModeCmd() {
        return Commands.runOnce(this::useBrakeMode);
    }

    public Command useCoastModeCmd() {
        return Commands.runOnce(this::useCoastModeCmd);
    }

    public Command setPowerUntilCmd(double power, BooleanSupplier untilCond, Subsystem... requirements) {
        Command c = Commands.sequence(
                Commands.sequence(
                    setPowerCmd(power),
                    Commands.idle().withTimeout(0.2)
                ),
                Commands.idle().until(untilCond),
                setPowerCmd(0)
        );
        c.addRequirements(requirements);
        return c;
    }

    /** WARNING: THIS MUST BE USED IN A DEFER IF YOU WANT THE POWER VALUE TO UPDATE AND YOU AREN'T
     * RECREATING THE COMMAND OVER THE LIFETIME OF THE ROBOT (or between uses)
     * 
     * If you want a non-static power source, use updatePowerSource(BooleanSupplier)
     */
    public Command setPowerCmd(double power) {
        return Commands.sequence(
            Commands.runOnce(() -> {
                set(power);
                if (debug) System.out.println("motor power:"+power);
            })
        );
        // return Commands.runOnce(() -> {
        //     set(power);
        // }, requirements);
    }

    public Command updatePowerCmd(DoubleSupplier ds) {
        return Commands.sequence(
            Commands.runOnce(() -> {
                set(ds.getAsDouble());
                if (debug) System.out.println("motor power:"+power);
            })
        );
        // return Commands.runOnce(() -> {
        //     set(power);
        // }, requirements);
    }

    public boolean isMotorMoving(double stallVelocity) {
        return Math.abs(getVelocity()) < stallVelocity; // I'm not sure if reverse is a negative velocity, but just in case
    }

    public Command runUntilStall(double power, double stallVelocity, double secondsUntilConsideredStalled, Subsystem... requirements) {        
        Command c = setPowerUntilCmd(power, 
                 new TimedBooleanSupplier(() -> isMotorMoving(stallVelocity), secondsUntilConsideredStalled), requirements);
        if (RobotBase.isSimulation()) {
            //c.withTimeout(0.1);
        }
        return c;
    }
    
    public Command runUntilStallOr(double power, double stallVelocity, double secondsUntilConsideredStalled, BooleanSupplier orCond, Subsystem... requirements) {
        TimedBooleanSupplier tbs = 
                    new TimedBooleanSupplier(() -> !isMotorMoving(stallVelocity), secondsUntilConsideredStalled)
                            .addImmediateEnd(orCond);
        return setPowerUntilCmd(power, tbs, requirements);
    }    

    public Command resetPositionCmd() {
        return Commands.runOnce(() -> {
            resetPosition();            
        });
    }

    public String getBrakeMode() {
        if (brakeMode == null) {
            return "UNKNOWN";
        }
        return brakeMode ? "brake" : "coast";
    }

    @Override
    public SendableFluent addSendables(SendableFluent s) {                
        s.addDefaultKey("motor");

            s.addDouble("id", () -> this.id, null);
            s.addString("type", () -> this.type.name(), null);
            s.addDouble("stallLimit", () -> this.stallLimit, null);
            s.addDouble("freeLimit", () -> this.freeLimit, null);
            

            s.addDouble("position", this::getPosition, this::setPosition);
            s.addString("brakeMode", this::getBrakeMode, null);
            s.addDouble("velocity", this::getVelocity, null);
            s.addDouble("current", this::getCurrent, null);
            s.addDouble("power", () -> this.get(), null);
            s.addDouble("temp", () -> this.getTemp(), null);
                                                                        
        s.removeDefaultKey();
        return s;
    }

    
}
