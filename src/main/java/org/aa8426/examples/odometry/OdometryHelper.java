package org.aa8426.examples.odometry;


import java.util.List;

import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

/**
 * Does basic robot-centric movement of a pose using CCW positive measurements. 
 * 
 * Some aspect of this exists in Transform
 */
public class OdometryHelper {
    
    Pose2d pose2d;
    static final AprilTagFieldLayout fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2025Reefscape);
    static List<AprilTag> tags = fieldLayout.getTags();

    public enum AprilTagNamesFor2025Ids {        
        RED_LEFT_FEEDER,
        RED_RIGHT_FEEDER,
        RED_PROCESSOR,
        RED_ENEMY_BARGE,
        RED_ALLIANCE_BARGE,
        RED_REEF_K,
        RED_REEF_A,
        RED_REEF_C,
        RED_REEF_E,
        RED_REEF_G,
        RED_REEF_I,
        BLUE_RIGHT_FEEDER,
        BLUE_LEFT_FEEDER,
        BLUE_ALLIANCE_BARGE,
        BLUE_ENEMY_BARGE,
        BLUE_PROCESSOR,
        BLUE_REEF_C,
        BLUE_REEF_A,
        BLUE_REEF_K,
        BLUE_REEF_I,
        BLUE_REEF_G,
        BLUE_REEF_E,
    }

    public OdometryHelper(Pose2d pose2d) {
        System.out.println(pose2d);
        this.pose2d = pose2d;        
    }

    static public String getCoralLetter(int idx) {
        idx = idx % 12;
        return "ABCDEFGHIJKL".substring(idx, idx+1);
    }

    static public String getAlgaeLetter(int idx) {
        idx = idx % 6;
        return "ACEGIK".substring(idx, idx+1);
    }

    static private Pose2d transformCoral(AprilTagNamesFor2025Ids reef_tag, boolean isLeft, double robotOffset) {
        Pose2d pose2d = getPoseFromAprilTag(reef_tag);
        if (isLeft) {
            return new OdometryHelper(pose2d).moveForward(robotOffset).flip().moveLeft(0.2).pose2d;
        } else {
            return new OdometryHelper(pose2d).moveForward(robotOffset).flip().moveRight(0.2).pose2d;
        }
    }

    static private Pose2d transformAlgae(AprilTagNamesFor2025Ids reef_tag, double robotOffset) {
        Pose2d pose2d = getPoseFromAprilTag(reef_tag);
        return new OdometryHelper(pose2d).moveForward(robotOffset).flip().pose2d;        
    }

    static public Pose2d getAlgaePosition(boolean forRedAlliance, String letter, double robotOffset) {
        if (forRedAlliance) {
            //if (true) {
                switch (letter.toUpperCase()) {
                    case "A": return transformAlgae(AprilTagNamesFor2025Ids.RED_REEF_A, robotOffset);
                    case "B": return transformAlgae(AprilTagNamesFor2025Ids.RED_REEF_A, robotOffset);
                    case "C": return transformAlgae(AprilTagNamesFor2025Ids.RED_REEF_C, robotOffset);
                    case "D": return transformAlgae(AprilTagNamesFor2025Ids.RED_REEF_C, robotOffset);
                    case "E": return transformAlgae(AprilTagNamesFor2025Ids.RED_REEF_E, robotOffset);
                    case "F": return transformAlgae(AprilTagNamesFor2025Ids.RED_REEF_E, robotOffset);
                    case "G": return transformAlgae(AprilTagNamesFor2025Ids.RED_REEF_G, robotOffset);
                    case "H": return transformAlgae(AprilTagNamesFor2025Ids.RED_REEF_G, robotOffset);
                    case "I": return transformAlgae(AprilTagNamesFor2025Ids.RED_REEF_I, robotOffset);
                    case "J": return transformAlgae(AprilTagNamesFor2025Ids.RED_REEF_I, robotOffset);
                    case "K": return transformAlgae(AprilTagNamesFor2025Ids.RED_REEF_K, robotOffset);
                    case "L": return transformAlgae(AprilTagNamesFor2025Ids.RED_REEF_K, robotOffset);
                    default: return null;                
                }
            } else {
                switch (letter.toUpperCase()) {
                    case "A": return transformAlgae(AprilTagNamesFor2025Ids.BLUE_REEF_A, robotOffset);
                    case "B": return transformAlgae(AprilTagNamesFor2025Ids.BLUE_REEF_A, robotOffset);
                    case "C": return transformAlgae(AprilTagNamesFor2025Ids.BLUE_REEF_C, robotOffset);
                    case "D": return transformAlgae(AprilTagNamesFor2025Ids.BLUE_REEF_C, robotOffset);
                    case "E": return transformAlgae(AprilTagNamesFor2025Ids.BLUE_REEF_E, robotOffset);
                    case "F": return transformAlgae(AprilTagNamesFor2025Ids.BLUE_REEF_E, robotOffset);
                    case "G": return transformAlgae(AprilTagNamesFor2025Ids.BLUE_REEF_G, robotOffset);
                    case "H": return transformAlgae(AprilTagNamesFor2025Ids.BLUE_REEF_G, robotOffset);
                    case "I": return transformAlgae(AprilTagNamesFor2025Ids.BLUE_REEF_I, robotOffset);
                    case "J": return transformAlgae(AprilTagNamesFor2025Ids.BLUE_REEF_I, robotOffset);
                    case "K": return transformAlgae(AprilTagNamesFor2025Ids.BLUE_REEF_K, robotOffset);
                    case "L": return transformAlgae(AprilTagNamesFor2025Ids.BLUE_REEF_K, robotOffset);
                    default: return null;                
                }
            }    
    }

    static public Pose2d getCoralPosition(boolean forRedAlliance, String letter, double robotOffset) {
        //System.out.println(letter+","+AprilTagNamesFor2025Ids.RED_REEF_A.ordinal());
        if (forRedAlliance) {
        //if (true) {
            switch (letter.toUpperCase()) {
                case "A": return transformCoral(AprilTagNamesFor2025Ids.RED_REEF_A, true, robotOffset);
                case "B": return transformCoral(AprilTagNamesFor2025Ids.RED_REEF_A, false, robotOffset);
                case "C": return transformCoral(AprilTagNamesFor2025Ids.RED_REEF_C, true, robotOffset);
                case "D": return transformCoral(AprilTagNamesFor2025Ids.RED_REEF_C, false, robotOffset);
                case "E": return transformCoral(AprilTagNamesFor2025Ids.RED_REEF_E, true, robotOffset);
                case "F": return transformCoral(AprilTagNamesFor2025Ids.RED_REEF_E, false, robotOffset);
                case "G": return transformCoral(AprilTagNamesFor2025Ids.RED_REEF_G, true, robotOffset);
                case "H": return transformCoral(AprilTagNamesFor2025Ids.RED_REEF_G, false, robotOffset);
                case "I": return transformCoral(AprilTagNamesFor2025Ids.RED_REEF_I, true, robotOffset);
                case "J": return transformCoral(AprilTagNamesFor2025Ids.RED_REEF_I, false, robotOffset);
                case "K": return transformCoral(AprilTagNamesFor2025Ids.RED_REEF_K, true, robotOffset);
                case "L": return transformCoral(AprilTagNamesFor2025Ids.RED_REEF_K, false, robotOffset);
                default: return null;                
            }
        } else {
            switch (letter.toUpperCase()) {
                case "A": return transformCoral(AprilTagNamesFor2025Ids.BLUE_REEF_A, true, robotOffset);
                case "B": return transformCoral(AprilTagNamesFor2025Ids.BLUE_REEF_A, false, robotOffset);
                case "C": return transformCoral(AprilTagNamesFor2025Ids.BLUE_REEF_C, true, robotOffset);
                case "D": return transformCoral(AprilTagNamesFor2025Ids.BLUE_REEF_C, false, robotOffset);
                case "E": return transformCoral(AprilTagNamesFor2025Ids.BLUE_REEF_E, true, robotOffset);
                case "F": return transformCoral(AprilTagNamesFor2025Ids.BLUE_REEF_E, false, robotOffset);
                case "G": return transformCoral(AprilTagNamesFor2025Ids.BLUE_REEF_G, true, robotOffset);
                case "H": return transformCoral(AprilTagNamesFor2025Ids.BLUE_REEF_G, false, robotOffset);
                case "I": return transformCoral(AprilTagNamesFor2025Ids.BLUE_REEF_I, true, robotOffset);
                case "J": return transformCoral(AprilTagNamesFor2025Ids.BLUE_REEF_I, false, robotOffset);
                case "K": return transformCoral(AprilTagNamesFor2025Ids.BLUE_REEF_K, true, robotOffset);
                case "L": return transformCoral(AprilTagNamesFor2025Ids.BLUE_REEF_K, false, robotOffset);
                default: return null;                
            }
        }
    }

    public OdometryHelper move(double distance, Rotation2d angle) {        
        double xNew = pose2d.getX() + distance * Math.cos(angle.getRadians());
        double yNew = pose2d.getY() + distance * Math.sin(angle.getRadians());
        
        this.pose2d = new Pose2d(xNew, yNew, pose2d.getRotation());        
        return this;
    }

    public OdometryHelper flip() {                
        this.pose2d = new Pose2d(pose2d.getX(), pose2d.getY(), Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()+180));
        return this;
    }

    public OdometryHelper moveForward(double distance) {
        move(distance, Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()+0));
        return this;
    }

    public OdometryHelper moveRight(double distance) {
        move(distance, Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()-90));
        return this;
    }

    public OdometryHelper moveLeft(double distance) {
        move(distance, Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()+90));
        return this;
    }

    public OdometryHelper moveBackward(double distance) {
        move(distance, Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()+180));
        return this;
    }

    public OdometryHelper face(Pose2d pose2d) {
        this.pose2d = new Pose2d(this.pose2d.getX(), this.pose2d.getY(), Rotation2d.fromDegrees(getAngle(pose2d, this.pose2d)));
        return this;
    }

    private double getAngle(Pose2d p1, Pose2d p2) {
        return getAngle(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    private double getAngle(double x1, double y1, double x2, double y2) {
        double dy = y2 - y1;
        double dx = x2 - x1;
        double theta = Math.atan2(dy, dx);
        theta = theta * (180 / Math.PI);
        theta = theta + 180;
        return theta;
    }

    public Pose2d getPose2d() {
        return this.pose2d;
    }

    public OdometryHelper setPose2d(Pose2d pose2d) {
        this.pose2d = pose2d;
        return this;
    }

    public Pose2d get() {
        return this.pose2d;
    }

    public OdometryHelper mirrorPoseForAlliance(boolean mirrorOnlyLength) {
        double x, y;
        Rotation2d rot;
        if (this.pose2d.getX() > (fieldLayout.getFieldLength() / 2)) {
            x = Math.abs(this.pose2d.getX() - fieldLayout.getFieldLength());
        } else {
            x = fieldLayout.getFieldLength() - this.pose2d.getX();
        }        
        if (this.pose2d.getY() > (fieldLayout.getFieldWidth() / 2)) {
            y = Math.abs(this.pose2d.getY() - fieldLayout.getFieldWidth());
        } else {
            y = fieldLayout.getFieldWidth() - this.pose2d.getY();
        }
        if (mirrorOnlyLength) {
            if (this.pose2d.getRotation().getDegrees() < 180) {
                rot = Rotation2d.fromDegrees(180 - this.pose2d.getRotation().getDegrees());
            } else {
                rot = Rotation2d.fromDegrees((360 - this.pose2d.getRotation().getDegrees()) + 180);            
            }  
        } else {
            rot = Rotation2d.fromDegrees(this.pose2d.getRotation().getDegrees()+180);
        }
        this.pose2d = new Pose2d(x, y, rot);
        return this;
    }    

    public static Pose2d getPoseFromAprilTag(AprilTagNamesFor2025Ids tag) {
        return tags.get(tag.ordinal()).pose.toPose2d();
    }

    public static void main(String[] args) {        
        //System.out.println(OdometryHelper.AprilTagNamesFor2025Ids.RED_LEFT_FEEDER.ordinal());
        System.out.println(OdometryHelper.AprilTagNamesFor2025Ids.RED_REEF_K.ordinal());
        System.out.println(OdometryHelper.AprilTagNamesFor2025Ids.RED_REEF_A.ordinal());
        System.out.println(OdometryHelper.AprilTagNamesFor2025Ids.RED_REEF_C.ordinal());
        //for(int x=0;x<30;x++) {
        //    System.out.println(getCoralLetter(x));
        //}
        System.out.println(tags.get(AprilTagNamesFor2025Ids.RED_REEF_A.ordinal()));
        //System.out.println(getCoralPosition("A"));

        OdometryHelper oh = new OdometryHelper(new Pose2d(0, 0, Rotation2d.fromDegrees(45)));
        oh.move(5,Rotation2d.fromDegrees(53.13));
        System.out.println(oh.get());

        oh = new OdometryHelper(new Pose2d(0, 0, Rotation2d.fromDegrees(90)));
        oh.moveRight(4)
          .moveForward(3);
        System.out.println(oh.get());

        oh = new OdometryHelper(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));
        oh.face(new Pose2d(4, 3, Rotation2d.fromDegrees(0)));
        System.out.println(oh.get());

    }

    
}
