package org.aa8426.examples.odometry;

import java.util.List;

import org.aa8426.RobotContainer;

import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;

public class OdometryExample extends TimedRobot {
    
    Field2d field = new Field2d();
    public static final AprilTagFieldLayout fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2025Reefscape);
    
    RobotContainer rc = new RobotContainer();
    int aprilTagIdx = 0;

    @Override
    public void robotInit() {        
        SmartDashboard.putData(field);        
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {
        CommandScheduler.getInstance().getDefaultButtonLoop().clear();
        
        field.setRobotPose(new Pose2d(5, 5, Rotation2d.fromDegrees(0)));

        rc.driverPad.a().onTrue(Commands.runOnce(() -> {
            List<AprilTag> tags = fieldLayout.getTags();
            
            AprilTag tag = tags.get(aprilTagIdx);
            
            Pose2d pose = new Pose2d(tag.pose.getX(), tag.pose.getY(), tag.pose.getRotation().toRotation2d());
            OdometryHelper oh = new OdometryHelper(pose);

            // Move the robot forward half the length of the robot away from the april tag (or else we'll be embeded 
            // in the wall) The default robot length is 0.82 meters, we'll give it a little breathing room.
            // We could probably come up with some way to provide an "official robot length", but this is just
            // a demonstration.
            oh.moveForward((0.82 / 2)+0.1);

            // Face the april tag. April tags by default face the field. We want the robot to face the april tag.
            oh.flip();

            // Used to test mirroring poses for 2025 game (which was a full mirror, not just across the field)
            // oh.mirrorPoseForAlliance(false);

            System.out.println(aprilTagIdx+":"+OdometryHelper.AprilTagNamesForIds.values()[aprilTagIdx].name());
            field.setRobotPose(oh.get()); 

            aprilTagIdx++;
            if (aprilTagIdx >= tags.size()) {
                aprilTagIdx = 0;
            }
            
        }));
        
    }

    @Override
    public void teleopPeriodic() {        
        
    }
}
