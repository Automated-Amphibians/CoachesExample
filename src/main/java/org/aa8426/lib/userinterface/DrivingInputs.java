package org.aa8426.lib.userinterface;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import org.aa8426.Robot;
import org.aa8426.lib.PIDControl;
import org.aa8426.lib.Utils;
import org.aa8426.lib.dashboard.SendableFluent;
import org.aa8426.lib.dashboard.SendableFluent.ISendableFluent;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;

public class DrivingInputs implements ISendableFluent {

    public static final class DriveConstants {
        public static final double drivePadMaxMetersPerSec = 5; // was
        public static final double drivePadMaxRadsPerSec = 1 * 2 * Math.PI;
        public static final double drivePadMaxAccel = 1.5; // was 3 
        public static final double drivePadMaxAngularAccel = 3;
    }

    public double rot; // rotation speed requested, may be from supplier, target heading, or target location (-1 to 1)
    public double x; // x translation speed, may be from supplier, or povsupplier (-1 to 1)
    public double y; // y translation speed, may be from supplier, or povsupplier (-1 to 1)
    public Integer pov; // translation expressed as angle (comes from pov supplier, not directly used in chassis speeds) (0 to 360)
    public DoubleSupplier xSupplier; // x translation speed from external controller (-1 to 1), typically joystick
    public DoubleSupplier ySupplier; // y translation speed from external controller (-1 to 1), typically joystick
    public DoubleSupplier rotSupplier; // rotation speed from external controller (-1 to 1), typically joystick
    public Supplier<Integer> povSupplier; // pov from external controller (0 to 360), typically joystick
    public Supplier<Pose2d> robotPoseSupplier;
    public Supplier<Rotation2d> targetHeadingSupplier; 
    public double deadband = 0.1;
    public double flipAndScale = 0.5;
    public double rotFlipped = -1; // always flipped
    private Double targetHeading = null;
    private Pose2d targetLocation = null;    
    // anything over 90 degrees should be full power or 1. You can never be more than 180. Basically, 90*0.1, yeah, roughly?
    //public PIDControl headingPID = new PIDControl(0.08, 0.0, 0.005);
    public PIDControl headingPID;

    private SlewRateLimiter xLimiter = null;
    private SlewRateLimiter yLimiter = null;
    private SlewRateLimiter rotLimiter = null;

    public double xSpeed = 0;
    public double ySpeed = 0;
    public double rotSpeed = 0;
    

    public boolean robotOriented = false;

    boolean stopped = true;

    public DrivingInputs(DoubleSupplier xSupplier, DoubleSupplier ySupplier, DoubleSupplier rotSupplier) {
        if (Robot.isSimulation()) {
            headingPID = new PIDControl(0.06, 0.0, 0.005);
            headingPID.setMinMax(0.001, 0.3);
        } else {
            headingPID = new PIDControl(0.05, 0.0, 0.005);
            headingPID.setMinMax(0.001, 0.3);
        }        
        this.headingPID.setTolerance(3);
        this.headingPID.enableContinuousInput(0, 360);
        //this.headingPID.invert = -1;
        this.xSupplier = xSupplier;
        this.ySupplier = ySupplier;
        this.rotSupplier = rotSupplier;        
        //addSendables(SendableFluent.getInstance());
    }

    public void setSlews(double x, double rot) {
        xLimiter = new SlewRateLimiter(x);
        yLimiter = new SlewRateLimiter(x);    
        rotLimiter = new SlewRateLimiter(rot);
    }

    public void setTargetHeading(Double targetHeading) {
        this.targetHeading = flipAndScale < 0 ? targetHeading : targetHeading + 180;        
        this.targetLocation = null;
        this.headingPID.setTarget(this.targetHeading);
        if (robotPoseSupplier == null) {
            throw new RuntimeException("Robot pose supplier required for target heading. (I have to know where I am to decide how to get where I'm going)");
        }
    }

    public void setTargetLocation(Pose2d targetLocation) {
        this.targetHeading = null;
        this.targetLocation = targetLocation;
        if (robotPoseSupplier == null) {
            throw new RuntimeException("Robot pose supplier required for target location. (I have to know where I am to decide how to get where I'm going)");
        }
    }
    
    public Double getTargetHeading() {
        return targetHeading;
    }

    public ChassisSpeeds getChassisSpeeds() {
        // looking for translation (POV or stick)
        double localFlipAndScale = flipAndScale;
        if (robotOriented) {
            localFlipAndScale = Math.abs(localFlipAndScale);
        }
        
        pov = povSupplier == null ? null : povSupplier.get();        
        if ((pov != null) && (pov > -1)) {
            x = Rotation2d.fromDegrees(pov).getCos() * localFlipAndScale;
            y = Rotation2d.fromDegrees(pov - 180).getSin() * localFlipAndScale;
        } else {
            x = MathUtil.applyDeadband(xSupplier.getAsDouble(), deadband) * -localFlipAndScale;
            y = MathUtil.applyDeadband(ySupplier.getAsDouble(), deadband) * -localFlipAndScale;        
        }

        // looking for rotation
        rot = MathUtil.applyDeadband(rotSupplier.getAsDouble(), deadband) * Math.abs(localFlipAndScale) * rotFlipped; // always flipped.
        // if the joystick is being moved, it take precendence and we clear other targeting methods
        if (Math.abs(rot) > 0) {
            targetHeading = null;            
            targetLocation = null;
        } else if (targetHeadingSupplier != null) {
            Pose2d pose = robotPoseSupplier.get();
            Rotation2d targetHeading = targetHeadingSupplier.get();
            if (pose == null) {
                System.err.println("Robot pose supplier given, but no pose returned.");
                rot = 0; 
            } else {
                // if the reading off the gyro is goofy, we'll have to address that here.
                headingPID.setTarget(targetHeading.getDegrees());
                rot = headingPID.calc(Math.abs(pose.getRotation().getDegrees()+180)); 
                //System.out.println("rot: " + rot);
            }
        } else if (targetHeading != null) { 
            Pose2d pose = robotPoseSupplier.get();
            if (pose == null) {
                System.err.println("Robot pose supplier given, but no pose returned.");
                rot = 0; 
            } else {
                // if the reading off the gyro is goofy, we'll have to address that here.
                rot = headingPID.calc(Math.abs(pose.getRotation().getDegrees()+180)); 
                System.out.println("rot: " + rot);
            }
        } else if (targetLocation != null) { 
            Pose2d pose = robotPoseSupplier.get();
            if (pose == null) {
                System.err.println("Robot pose supplier given, but no pose returned.");
                rot = 0; 
            } else {
                //Translation2d relativeTrl = speakerAprilTagPose.toPose2d().relativeTo(getPose()).getTranslation();
                double targetAngle = getAngle(targetLocation, pose);                
                headingPID.setTarget(targetAngle);
                rot = headingPID.calc(Math.abs(pose.getRotation().getDegrees()+180)); 
            }        
        }                

        ChassisSpeeds chassisSpeeds = getChassisSpeedsFromInputs(x, y, rot, robotOriented);
        return chassisSpeeds;
        //return new Transform2d(xSpeed, ySpeed, Rotation2d.fromRadians(rotSpeed));
    }

    /**
     * Returns robot centric chassis speeds from an angle and a rotation speed.
     * 
     * @param angle The desired angle to translate at.
     * @param speed The desired speed. -1 to 1
     * @param rot The desired rotation speed. -1 to 1
     * @return ChassisSpeeds 
     */
    public ChassisSpeeds getChassisSpeedsFrom(Rotation2d angle, double speed, double rot) {
        x = angle.getCos() * speed;
        y = Rotation2d.fromDegrees(angle.getDegrees() - 180).getSin() * speed;
        return getChassisSpeedsFromInputs(x, y, rot, true);
    }

    /** 
     * Takes an x, y, and rotation value expressed in -1 to 1 and turns it into
     * a ChassisSpeeds object. This method also applies slew rate limiting to the
     * inputs.
     */
    private ChassisSpeeds getChassisSpeedsFromInputs(double x, double y, double rot, boolean robotOriented) {
        // convert to m/s, minding slew
        double maxDriveSpeed = DriveConstants.drivePadMaxMetersPerSec;
        double maxTurnSpeed = DriveConstants.drivePadMaxRadsPerSec;                
        xSpeed = xLimiter == null ? x * maxDriveSpeed : xLimiter.calculate(x) * maxDriveSpeed;
        ySpeed = yLimiter == null ? y * maxDriveSpeed : yLimiter.calculate(y) * maxDriveSpeed;
        rotSpeed = rotLimiter == null ? rot * maxTurnSpeed : rotLimiter.calculate(rot) * maxTurnSpeed;

         if ((Math.abs(x) < 0.01) && (Math.abs(y) < 0.01) && (Math.abs(rot) < 0.01)) {
             xSpeed = 0;
             ySpeed = 0;
             rotSpeed = 0;
         }
        //     if (stopped) {
        //         return null;
        //     }
        //     stopped = true;
        // } else {
        //     stopped = false;
        // }
        
        return getChassisSpeedsFromSpeeds(xSpeed, ySpeed, rotSpeed, robotOriented);
    }

    public ChassisSpeeds getChassisSpeedsFromSpeeds(double xSpeed, double ySpeed, double rotSpeed, boolean robotOriented) {
        ChassisSpeeds chassisSpeeds;
        if (robotOriented) {           
            chassisSpeeds = new ChassisSpeeds(xSpeed, ySpeed, rotSpeed);
        } else {      
            Rotation2d robotRotation = robotPoseSupplier.get().getRotation();
            // this is confusing -- we are always in blue alliance coordinates, but we need to flip the robot rotation if we are red alliance 
            // because fromFieldRelativeSpeeds expects 0 for robot rotation always facing away from the driver station.
            if (Utils.amRedAlliance()) {
               robotRotation = robotRotation.rotateBy(Rotation2d.fromDegrees(180));
            }            
            chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rotSpeed, robotRotation);            
            chassisSpeeds = ChassisSpeeds.discretize(chassisSpeeds, 0.02);
        }
        return chassisSpeeds;
    }

    public ChassisSpeeds getChassisSpeedsFromSpeedsX(double xSpeed, double ySpeed, double rotSpeed, boolean robotOriented) {
        ChassisSpeeds chassisSpeeds;
        if (robotOriented) {           
            chassisSpeeds = new ChassisSpeeds(xSpeed, ySpeed, rotSpeed);
        } else {      
            Rotation2d robotRotation = robotPoseSupplier.get().getRotation();
            // this is confusing -- we are always in blue alliance coordinates, but we need to flip the robot rotation if we are red alliance 
            // because fromFieldRelativeSpeeds expects 0 for robot rotation always facing away from the driver station.          
            chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rotSpeed, robotRotation);            
            chassisSpeeds = ChassisSpeeds.discretize(chassisSpeeds, 0.02);
        }
        return chassisSpeeds;
    }

    public double getAngle(Pose2d p1, Pose2d p2) {
        return getAngle(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public double getAngle(double x1, double y1, double x2, double y2) {
        double dy = y2 - y1;
        double dx = x2 - x1;
        double theta = Math.atan2(dy, dx);
        theta = theta * (180 / Math.PI);
        theta = theta + 180;
        return theta;
    }

    public void setFlipAndScaleBasedOnAlliance() {
        if (Utils.amBlueAlliance()) {
            flipAndScale = Math.abs(flipAndScale);
        } else {
            flipAndScale = Math.abs(flipAndScale);
        }
    }

    public void flipScaleAndFlip() {
        flipAndScale = flipAndScale * -1;
    }
    
    @Override
    public SendableFluent addSendables(SendableFluent s) {                
        s.addDefaultKey("drivingInputs");
        headingPID.addSendables(s);
        s.removeDefaultKey();        
        return s;
    }   


}
