package org.aa8426.lib.Odometry;

import java.util.List;

import org.aa8426.lib.Utils;

import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.util.Units;

public class Reefscape extends PoseHelp {
    public static final AprilTagFieldLayout fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2025ReefscapeAndyMark);
    public static List<AprilTag> tags = fieldLayout.getTags();    

    public enum AprilTagNames {        
        RED_LEFT_FEEDER, // 1
        RED_RIGHT_FEEDER, // 2
        RED_PROCESSOR, // 3
        RED_ENEMY_BARGE, // 4
        RED_ALLIANCE_BARGE, // 5
        RED_REEF_K, // 6
        RED_REEF_A, // 7
        RED_REEF_C, // 8
        RED_REEF_E, // 9
        RED_REEF_G, // 10
        RED_REEF_I, // 11
        BLUE_RIGHT_FEEDER, //12
        BLUE_LEFT_FEEDER, //13
        BLUE_ALLIANCE_BARGE, //14
        BLUE_ENEMY_BARGE, //15
        BLUE_PROCESSOR, //16
        BLUE_REEF_C, //17
        BLUE_REEF_A,//18
        BLUE_REEF_K,//19
        BLUE_REEF_I,//20
        BLUE_REEF_G,//21
        BLUE_REEF_E,//22
    }

    public static Pose2d getPose(AprilTagNames tag) {
        return Reefscape.tags.get(tag.ordinal()).pose.toPose2d();
    }

    public static PoseHelp getPoseWithHelp(AprilTagNames tag) {
        return new PoseHelp(Reefscape.tags.get(tag.ordinal()).pose.toPose2d());
    }

    public static PoseHelp getRobotPose(AprilTagNames tag, double robotOffset) {
        return new PoseHelp(Reefscape.tags.get(tag.ordinal()).pose.toPose2d()).flip().moveBackward(robotOffset);
    }        

    static private Pose2d transformCoral(AprilTagNames reef_tag, boolean isLeft, double robotOffset) {
        Pose2d pose2d = getPose(reef_tag);
        if (isLeft) {
            return new PoseHelp(pose2d).moveForward(robotOffset).flip().moveLeft(Units.inchesToMeters(6)).pose2d;
        } else {
            return new PoseHelp(pose2d).moveForward(robotOffset).flip().moveRight(Units.inchesToMeters(6)).pose2d;
        }
    }

    static private Pose2d transformAlgae(AprilTagNames reef_tag, double robotOffset) {
        Pose2d pose2d = getPose(reef_tag);
        return new PoseHelp(pose2d).moveForward(robotOffset).flip().pose2d;        
    }

    

    static public Pose2d getAlgaeLocation(String letter, double robotOffset) { 

        if (Utils.amRedAlliance()) {
            //if (true) {
                switch (letter.toUpperCase()) {
                    case "A": 
                    case "B": return transformAlgae(AprilTagNames.RED_REEF_A, robotOffset);                    
                    case "C": 
                    case "D": return transformAlgae(AprilTagNames.RED_REEF_C, robotOffset);                                        
                    case "E": 
                    case "F": return transformAlgae(AprilTagNames.RED_REEF_E, robotOffset);                    
                    case "G": 
                    case "H": return transformAlgae(AprilTagNames.RED_REEF_G, robotOffset);
                    case "I": 
                    case "J": return transformAlgae(AprilTagNames.RED_REEF_I, robotOffset);                                        
                    case "K": 
                    case "L": return transformAlgae(AprilTagNames.RED_REEF_K, robotOffset);
                    default: return null;
                }
            } else {
                switch (letter.toUpperCase()) {
                    case "A": 
                    case "B": return transformAlgae(AprilTagNames.BLUE_REEF_A, robotOffset);                    
                    case "C": 
                    case "D": return transformAlgae(AprilTagNames.BLUE_REEF_C, robotOffset);                                        
                    case "E": 
                    case "F": return transformAlgae(AprilTagNames.BLUE_REEF_E, robotOffset);                    
                    case "G": 
                    case "H": return transformAlgae(AprilTagNames.BLUE_REEF_G, robotOffset);
                    case "I": 
                    case "J": return transformAlgae(AprilTagNames.BLUE_REEF_I, robotOffset);                                        
                    case "K": 
                    case "L": return transformAlgae(AprilTagNames.BLUE_REEF_K, robotOffset);
                    default: return null;                
                }
            }    
    }

    static public Pose2d getCoralPosition(String letter, double robotOffset, int level) {
        //System.out.println(letter+","+AprilTagNamesFor2025Ids.RED_REEF_A.ordinal());
        if (level == 1) {
            return getAlgaeLocation(letter, robotOffset);
        }
        if (Utils.amRedAlliance()) {
        //if (true) {
            switch (letter.toUpperCase()) {
                case "A": return transformCoral(AprilTagNames.RED_REEF_A, true, robotOffset);
                case "B": return transformCoral(AprilTagNames.RED_REEF_A, false, robotOffset);
                case "C": return transformCoral(AprilTagNames.RED_REEF_C, true, robotOffset);
                case "D": return transformCoral(AprilTagNames.RED_REEF_C, false, robotOffset);
                case "E": return transformCoral(AprilTagNames.RED_REEF_E, true, robotOffset);
                case "F": return transformCoral(AprilTagNames.RED_REEF_E, false, robotOffset);
                case "G": return transformCoral(AprilTagNames.RED_REEF_G, true, robotOffset);
                case "H": return transformCoral(AprilTagNames.RED_REEF_G, false, robotOffset);
                case "I": return transformCoral(AprilTagNames.RED_REEF_I, true, robotOffset);
                case "J": return transformCoral(AprilTagNames.RED_REEF_I, false, robotOffset);
                case "K": return transformCoral(AprilTagNames.RED_REEF_K, true, robotOffset);
                case "L": return transformCoral(AprilTagNames.RED_REEF_K, false, robotOffset);
                default: return null;                
            }
        } else {
            switch (letter.toUpperCase()) {
                case "A": return transformCoral(AprilTagNames.BLUE_REEF_A, true, robotOffset);
                case "B": return transformCoral(AprilTagNames.BLUE_REEF_A, false, robotOffset);
                case "C": return transformCoral(AprilTagNames.BLUE_REEF_C, true, robotOffset);
                case "D": return transformCoral(AprilTagNames.BLUE_REEF_C, false, robotOffset);
                case "E": return transformCoral(AprilTagNames.BLUE_REEF_E, true, robotOffset);
                case "F": return transformCoral(AprilTagNames.BLUE_REEF_E, false, robotOffset);
                case "G": return transformCoral(AprilTagNames.BLUE_REEF_G, true, robotOffset);
                case "H": return transformCoral(AprilTagNames.BLUE_REEF_G, false, robotOffset);
                case "I": return transformCoral(AprilTagNames.BLUE_REEF_I, true, robotOffset);
                case "J": return transformCoral(AprilTagNames.BLUE_REEF_I, false, robotOffset);
                case "K": return transformCoral(AprilTagNames.BLUE_REEF_K, true, robotOffset);
                case "L": return transformCoral(AprilTagNames.BLUE_REEF_K, false, robotOffset);
                default: return null;                
            }
        }
    }    
    
    public static Pose2d getFeederStation(String station, double robotOffset) {        
        switch (station.toLowerCase()) {
            case "leftfront" : return new PoseHelp(NonTagLocations.LEFT_FRONT.getAllianceCorrectPose2d()).moveBackward(robotOffset).get();
            case "leftback": return new PoseHelp(NonTagLocations.LEFT_BACK.getAllianceCorrectPose2d()).moveBackward(robotOffset).get();
            case "leftcenter": return new PoseHelp(NonTagLocations.LEFT_CENTER.getAllianceCorrectPose2d()).moveBackward(robotOffset).get();
            case "rightfront" : return new PoseHelp(NonTagLocations.RIGHT_FRONT.getAllianceCorrectPose2d()).moveBackward(robotOffset).get();
            case "rightback": return new PoseHelp(NonTagLocations.RIGHT_BACK.getAllianceCorrectPose2d()).moveBackward(robotOffset).get();
            case "rightcenter": return new PoseHelp(NonTagLocations.RIGHT_CENTER.getAllianceCorrectPose2d()).moveBackward(robotOffset).get();
            default: return new PoseHelp(NonTagLocations.RIGHT_FRONT.getAllianceCorrectPose2d()).moveBackward(robotOffset).get();
        }                
    }    

    public static Pose2d getAllianceProcessor(double robotOffset) {
        if (Utils.amRedAlliance()) {
            return PoseHelp.getFacingAprilTag(AprilTagNames.RED_PROCESSOR, robotOffset);
        } else {
            return PoseHelp.getFacingAprilTag(AprilTagNames.BLUE_PROCESSOR, robotOffset);
        }
    }    
}
