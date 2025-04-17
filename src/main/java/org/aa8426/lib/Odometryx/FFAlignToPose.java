package org.aa8426.lib.Odometry;

import java.util.function.Supplier;

import org.aa8426.lib.Utils;
import org.aa8426.lib.userinterface.DrivingInputs;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import swervelib.SwerveDrive;

public class FFAlignToPose extends Command {

    // -- this would end up being a lightweight path follower, which is fine, but I don't have to time to implement.
    // static public class Pose2dWithEndTargets {
    //     Pose2d pose;
    //     PIDConstants pidConstants = new PIDConstants(0, 0, 0);
    //     double maxAcceleration;
    //     double endVelocity; // what speed we want to end up at (basically minimum speed)
    //     double tolerance;
    //     double maxAngleSlew; // maximum we want to adjust angle 
    //     double timeout; // 
    //     boolean endOnOvershoot; // if we hit a situation where we are at 90 degrees of the 
    // }

    private final Supplier<Pose2d> robotPoseSupplier;
    private final Supplier<Pose2d> target;
    private SwerveDrive swerveDrive;
    private DrivingInputs drivingInputs;

    private ProfiledPIDController driveController =
        new ProfiledPIDController(
            3.0, 0.1, 0.0, new TrapezoidProfile.Constraints(0.5, .3));

    private PIDController thetaController =
        new PIDController(4.0, 0.0, 0.0);
            
    private double driveErrorAbs = 0.0;
    private double thetaErrorAbs = 0.0;        
    private double maxSpeed;

    public FFAlignToPose(DrivingInputs drivingInputs, SwerveDrive swerDrive, Supplier<Pose2d> robotPoseSupplier, Supplier<Pose2d> target, double maxSpeed) {        
        this.swerveDrive = swerveDrive;
        this.drivingInputs = drivingInputs;
        this.maxSpeed = maxSpeed; // cause the constraint is being difficult
        this.robotPoseSupplier = getFallbackPoseSupplier(robotPoseSupplier);
        this.target = target;        

        // Set tolerances
        driveController.setTolerance(0.05);
        thetaController.setTolerance(Math.toRadians(1));

        // Enable continuous input for theta controller
        thetaController.enableContinuousInput(-Math.PI, Math.PI);

        //addRequirements(rc.drivebase);
    }
    
    private Supplier<Pose2d> getFallbackPoseSupplier(Supplier<Pose2d> firstSupplier) {
        return new FallbackPoseSupplier()
            .add(firstSupplier)
            .add(robotPoseSupplier).get();
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {        
        // Get current pose and target pose
        Pose2d currentPose = robotPoseSupplier.get();
        Pose2d targetPose = target.get();

        Translation2d delta = currentPose.getTranslation().minus(targetPose.getTranslation());

        driveErrorAbs = delta.getNorm();
        thetaErrorAbs = Math.abs(currentPose.getRotation().minus(targetPose.getRotation()).getRadians());
        
        SmartDashboard.putNumber("auton-distance", delta.getNorm());
        double speed = driveController.calculate(delta.getNorm(), 0);
        
        SmartDashboard.putNumber("Speed", speed);
        if (speed > maxSpeed) {
            speed = maxSpeed;
        } else {
            if (speed < -maxSpeed) {
                speed = -maxSpeed;
            }
        }
        SmartDashboard.putNumber("Angle", delta.getAngle().getRadians());

        Translation2d movement = new Translation2d(speed, delta.getAngle());
        movement = Utils.cleanseSpeeds(movement);

        double omega = thetaController.calculate(currentPose.getRotation().getRadians(), targetPose.getRotation().getRadians());

        ChassisSpeeds chassisSpeedsFromSpeeds = drivingInputs.getChassisSpeedsFromSpeedsX(movement.getX(), movement.getY(), omega, false); // false I think!??!
        swerveDrive.drive(chassisSpeedsFromSpeeds);

        // RobotContainer.drive.acceptSwerveCommand(
        //     new SwerveCommand.FieldCentric()
        //         .withVelocityX(movement.getX())
        //         .withVelocityY(movement.getY())
        //         .withRotationalRate(omega)
        // );

        // Logger.recordOutput("Command DriveToPose Is Finished", isFinished());
        // Logger.recordOutput("Command DriveToPose X Error", delta.getX());
        // Logger.recordOutput("Command DriveToPose Y Error", delta.getY());
        // Logger.recordOutput("Command DriveToPose Theta Error", thetaErrorAbs);
    }

    @Override
    public boolean isFinished() {
        return withinTolerance(Units.inchesToMeters(1.0), Rotation2d.fromDegrees(1.0));
    }

    @Override
    public void end(boolean interrupted) {
        swerveDrive.drive(new ChassisSpeeds()); // stop        
    }
    
    /** Checks if the robot is stopped at the final pose. */
    public boolean atGoal() {
        return driveController.atGoal() && thetaController.atSetpoint();
    }

    /** Checks if the robot pose is within the allowed drive and theta tolerances. */
    public boolean withinTolerance(double driveTolerance, Rotation2d thetaTolerance) {        
        return Math.abs(driveErrorAbs) < driveTolerance
            && Math.abs(thetaErrorAbs) < thetaTolerance.getRadians();
    }
}