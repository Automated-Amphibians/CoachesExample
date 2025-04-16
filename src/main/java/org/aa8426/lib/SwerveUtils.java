package org.aa8426.lib;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import swervelib.SwerveDrive;

public class SwerveUtils {
    
    private SwerveDrive swerveDrive;
    private int idx = 0;

    public int getIdx() {return idx;} 
    public void setIdx(int moduleIdx) {
        idx = moduleIdx % 4;
    }
    
    public SwerveUtils(SwerveDrive swerveDrive) {
        this.swerveDrive = swerveDrive;
    }

    public Command setWheelAngles(Integer leftFront, Integer rightFront, Integer leftBack, Integer rightBack ) {
        return Commands.run(() -> {            
            swerveDrive.setModuleStates(new SwerveModuleState[] {  
                    new SwerveModuleState(0, Rotation2d.fromDegrees(leftFront)),
                    new SwerveModuleState(0, Rotation2d.fromDegrees(rightFront)),
                    new SwerveModuleState(0, Rotation2d.fromDegrees(leftBack)),
                    new SwerveModuleState(0, Rotation2d.fromDegrees(rightBack))
            }, false);
        });
    }

    public void setWheelsToXOut() {
        swerveDrive.setModuleStates(new SwerveModuleState[] {
            // 45, 135, 225, 315
                new SwerveModuleState(0, Rotation2d.fromDegrees(45)),
                new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
                new SwerveModuleState(0, Rotation2d.fromDegrees(135)),
                new SwerveModuleState(0, Rotation2d.fromDegrees(-135))
        }, false);
    }

    public Command setWheelsOneDirection(int angleInDegrees) {
        return Commands.runOnce(() -> {
            swerveDrive.setModuleStates(new SwerveModuleState[] {
                // 45, 135, 225, 315
                    new SwerveModuleState(0, Rotation2d.fromDegrees(angleInDegrees)),
                    new SwerveModuleState(0, Rotation2d.fromDegrees(angleInDegrees)),
                    new SwerveModuleState(0, Rotation2d.fromDegrees(angleInDegrees)),
                    new SwerveModuleState(0, Rotation2d.fromDegrees(angleInDegrees))
            }, false);
        });
    }

    // remember, positive rates means turning ccw when standing overhead of the robot. If the robot is flipped, then it is to the right.
    public Command rotateModule(int rateInDegreesPerSec) {
        final double angleDegreeChange = rateInDegreesPerSec / 50; // This assume 50 hertz refresh rate!
        return Commands.run(() -> {
            //SwerveModuleState[] moduleStates = swerve.swerveDrive.getModuleStates();
            SwerveModuleState[] moduleStates = swerveDrive.getStates();
            if ((idx >= 0) && (idx < moduleStates.length)) {
                // 
                moduleStates[idx].angle = Rotation2d.fromDegrees(moduleStates[idx].angle.getDegrees() + angleDegreeChange);
                swerveDrive.setModuleStates(moduleStates, false);
            }            
        });
    }

    public Command setModuleAngle(int angleInDegrees) {        
        return Commands.runOnce(() -> {
            SwerveModuleState[] moduleStates = swerveDrive.getStates();
            if ((idx >= 0) && (idx < moduleStates.length)) {                
                moduleStates[idx].angle = Rotation2d.fromDegrees(angleInDegrees);
                swerveDrive.setModuleStates(moduleStates, false);
            }            
        });
    }

    // though it would be nice to calculate this as rotations per second
    // gotta think through that one through
    public Command setModuleSpeed(double speedMetersPerSecond) {        
        return Commands.runOnce(() -> {
            SwerveModuleState[] moduleStates = swerveDrive.getStates();
            if ((idx >= 0) && (idx < moduleStates.length)) {                
                moduleStates[idx].speedMetersPerSecond = speedMetersPerSecond;
                swerveDrive.setModuleStates(moduleStates, false);
            }            
        });
    }

}
