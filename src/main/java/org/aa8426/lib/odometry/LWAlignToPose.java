package org.aa8426.lib.odometry;

import org.aa8426.lib.PIDControl;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.Command;
import swervelib.SwerveDrive;

public class LWAlignToPose extends Command {
  private PIDControl xController, yController, rotController;
  private SwerveDrive swerveDrive;
  private boolean atSetpoint;
  private ChassisSpeeds noSpeeds;
  private double lastHeading;

  public LWAlignToPose(SwerveDrive swerveDrive, Pose2d pose) {
    this.swerveDrive = swerveDrive;
    noSpeeds = new ChassisSpeeds();

    // 0.0254 inches per meter, so to get within 0.5, it's nearly 0.01 -- a minimum is a must -- 0.05 * 3. 10 inches should yield 0.762, a lot of power. 
    xController = new PIDControl(3.0, 0.0, 0.0).setMinMax(0.1, 0.3); // Vertical movement
    yController = new PIDControl(3, 0, 0).setMinMax(0.1, 0.3); // Horitontal movement
    rotController = new PIDControl(0.04, 0, 0); // Rotationca

    rotController.setTarget(pose.getRotation().getDegrees());
    rotController.setTolerance(1);
    lastHeading = pose.getRotation().getDegrees();

    xController.setTarget(pose.getX());
    xController.setTolerance(Units.inchesToMeters(0.5));

    yController.setTarget(pose.getY());
    yController.setTolerance(Units.inchesToMeters(0.5));
  }

  static public LWAlignToPose getAlignToPoseCmd(SwerveDrive swerveSubsystem, Pose2d pose) {
    return new LWAlignToPose(swerveSubsystem, pose);
  }

  @Override
  public void initialize() {
    atSetpoint = false;
  }

  @Override
  public void execute() {
    Pose2d pose = swerveDrive.getPose();
    double xSpeed = xController.calc(pose.getX());
    double ySpeed = yController.calc(pose.getY());

    // handle 360 circle problem
    double curHeading = pose.getRotation().getDegrees();
    double centeredHeading =
        MathUtil.inputModulus(curHeading, lastHeading - 180, lastHeading + 180);
    double rotValue = rotController.calc(centeredHeading);
    lastHeading = centeredHeading;

    // handle field oriented
    /*if (UtilFunctions.getAlliance() == Alliance.Red) {
      xSpeed *= -1;
      ySpeed *= -1;
    }*/
    ChassisSpeeds speeds = new ChassisSpeeds(xSpeed, ySpeed, rotValue);

    if (rotController.atSetpoint() && yController.atSetpoint() && xController.atSetpoint()) {
      atSetpoint = true;
    }

    if (!atSetpoint) {
      swerveDrive.driveFieldOriented(speeds);
    } else {
      swerveDrive.driveFieldOriented(noSpeeds);
    }
  }

  @Override
  public boolean isFinished() {
    return atSetpoint;
  }

  @Override
  public void end(boolean interrupted) {
    swerveDrive.setChassisSpeeds(noSpeeds);
  }
}
