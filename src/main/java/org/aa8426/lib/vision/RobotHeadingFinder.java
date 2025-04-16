package frc.aa8426.utils.vision;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.aa8426.subsystems.ClawIvator.ClawIvatorPositions;
import frc.aa8426.utils.Utils;
import frc.aa8426.utils.Odometry.NonTagLocations;
import frc.aa8426.utils.Odometry.PoseHelp;
import frc.aa8426.utils.Odometry.Reefscape;

/**
 * 
 * Calls of interest 
 * 
 * RobotHeadingFinder.getIntelligentHeading() - Returns the best heading for the robot based on the location and current intake status of the robot.
 * RobotHeadingFinder.getIntelligentHeadingX() - returns only the X component of getIntelligentHeading() - feed this as a Driver Input for chassis speed calculations
 * RobotHeadingFinder.getIntelligentHeadingY() - returns only the Y component of getIntelligentHeading() - feed this as a Driver Input for chassis speed calculations
 * 
 */
public class RobotHeadingFinder {
    
    static int[] reefSectionIds = new int[] {0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 0};    
    public static String[] reefSectionsLeft = new String[] {"A", "C", "E", "G", "I", "K"};
    public static String[] reefSectionsRight = new String[] {"B", "D", "F", "H", "J", "L"};    
    public static int[] redHeadingBySection = new int[] {0, 60, 120, 180, 240, 300};    
    public static int[] blueHeadingBySection = new int[] {180, 240, 300, 0, 60, 120};    
    //public static int[] headingBySection = new int[] {180, 120, 60, 0, 320, 240};
    //public static int[] blueHeadingBySection = new int[] {180, 120, 60, 0, 300, 240};
    //public static int[] redHeadingBySection = new int[] {0, 300, 240, 180, 120, 60};
    public static int[] redAprilTagIds = new int[] {7, 8, 9, 10, 11, 12, 6};
    public static int[] blueAprilTagIds = new int[] {18, 17, 22, 21, 20, 19};

    public static Pose2d blueReefCenter = new Pose2d(4.5, 4 , Rotation2d.fromDegrees(0));
    public static Pose2d redReefCenter = PoseHelp.mirrorPose(blueReefCenter);

    public static int getReefSection(double x2, double y2) {        
        double angle;        
        if (Utils.amBlueAlliance()) {
            angle = (int)Math.round(Math.toDegrees(Math.atan2(y2 - blueReefCenter.getY(), x2 - blueReefCenter.getX())))+180;
        } else {
            angle = (int)Math.round(Math.toDegrees(Math.atan2(y2 - redReefCenter.getY(), x2 - redReefCenter.getX())));
        }                
        angle = (angle < 0) ? angle + 360 : angle;                
        double angle2 = angle / 30.0;        
        int section = (int)Math.floor(angle2);
        //System.out.print(String.format("x=%.2f y=%.2f, angle=%4.2f, angle2=%3.2f, section=%d ", x2, y2, angle, angle2, section));
        section = section % 12;
        return reefSectionIds[section];
    }

    /**
     * Get the most intelligent heading to face based on the state of the robot. A little surprised that the reef section works.
     */
    private static int getHeadingByDegree(Pose2d robotPose, boolean haveCoral, boolean haveAlgae, boolean wantAlgae) {
        if (haveAlgae) {
            return Utils.amBlueAlliance() ? 90 : 270;
        }
        if (haveCoral || wantAlgae) {
            int reefSection = getReefSection(robotPose.getX(), robotPose.getY());
            return Utils.amBlueAlliance() ? blueHeadingBySection[reefSection] : redHeadingBySection[reefSection];
        } 
            
        //System.out.println(robotPose.getY()+","+(OdometryHelper.fieldLayout.getFieldWidth() / 2.0));
        int result;
        if (robotPose.getY() > (Reefscape.fieldLayout.getFieldWidth() / 2.0)) {
            // "top" of the field -- but it's not?!?!?!?            
            //return Utils.amRedAlliance() ? 90+35 : 90-35;
            //return 90;            
            result = Utils.amBlueAlliance() ? 270+35 : 270-35;
        } else {            
            // "bottom" of the field                        
            result = Utils.amBlueAlliance() ? 90-35 : 90+35;
        }
        return result;
    }        

    public static Rotation2d getIntelligentHeading(Pose2d robotPose, boolean haveCoral, boolean haveAlgae, boolean wantAlgae) {
        return Rotation2d.fromDegrees(getHeadingByDegree(robotPose, haveCoral, haveAlgae, wantAlgae));
    }

    public static double getIntelligentHeadingX(Pose2d robotPose, boolean haveCoral, boolean haveAlgae, boolean wantAlgae) {
        return getIntelligentHeading(robotPose, haveCoral, haveAlgae, wantAlgae).getSin();
    }

    public static double getIntelligentHeadingY(Pose2d robotPose, boolean haveCoral, boolean haveAlgae, boolean wantAlgae) {
        return getIntelligentHeading(robotPose, haveCoral, haveAlgae, wantAlgae).getCos();
    }

    public static Pose2d getCoralIntakePose(double x, double y, double robotOffset, boolean front) {
        // goto intake coral
        if (front) {
            if (y < Reefscape.fieldLayout.getFieldWidth() / 2) {
                return Utils.amBlueAlliance() ? NonTagLocations.RIGHT_FRONT.getAllianceCorrectPose2d() : NonTagLocations.LEFT_FRONT.getAllianceCorrectPose2d();
            } else {
                return Utils.amBlueAlliance() ? NonTagLocations.LEFT_FRONT.getAllianceCorrectPose2d() : NonTagLocations.RIGHT_FRONT.getAllianceCorrectPose2d();
            }
        } else {
            if (y < Reefscape.fieldLayout.getFieldWidth() / 2) {
                return Utils.amBlueAlliance() ? NonTagLocations.RIGHT_BACK.getAllianceCorrectPose2d() : NonTagLocations.LEFT_BACK.getAllianceCorrectPose2d();
            } else {
                return Utils.amBlueAlliance() ? NonTagLocations.LEFT_BACK.getAllianceCorrectPose2d() : NonTagLocations.RIGHT_BACK.getAllianceCorrectPose2d();
            }
        }
    }
    
    public static Pose2d getCoralBranchFromCurrentSection(boolean left, double x, double y, double robotOffset, int level) {
        int reefSectionId = getReefSection(x, y);
        String reefSection = left ? reefSectionsLeft[reefSectionId] : reefSectionsRight[reefSectionId];
        return Reefscape.getCoralPosition(reefSection, robotOffset, level);                        
    }        

    public static Pair<ClawIvatorPositions, Pose2d> getAlgaeFromCurrentSection(double x, double y, double robotOffset) {
        int reefSectionId = getReefSection(x, y);
        String reefSection = reefSectionsLeft[reefSectionId];
        ClawIvatorPositions clawPosition = (reefSectionId % 2 == 0) ? ClawIvatorPositions.ALGAE_L3 : ClawIvatorPositions.ALGAE_L2;        
        return Pair.of(clawPosition, Reefscape.getAlgaeLocation(reefSection, robotOffset));        
    }

    static public String getReefSectionLetter(double x, double y) {
        int reefSectionId = getReefSection(x, y);
        return reefSectionsLeft[reefSectionId];        
    }

    static public int getReefSectionAprilTagId(double x, double y) {
        int reefSectionId = getReefSection(x, y);        
        if (Utils.amRedAlliance()) {            
            return redAprilTagIds[reefSectionId];
        } else {
            return blueAprilTagIds[reefSectionId];
        }
    }

}