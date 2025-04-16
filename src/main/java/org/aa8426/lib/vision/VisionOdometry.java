package frc.aa8426.utils.vision;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import frc.aa8426.RobotContainer;
import frc.aa8426.utils.RollingAverage;
import frc.aa8426.utils.Utils;
import frc.aa8426.utils.dashboard.SendableFluent;
import frc.aa8426.utils.dashboard.SendableFluent.ISendableFluent;

public class VisionOdometry implements ISendableFluent {

    RobotContainer rc;

    static public class CameraSpecificInfo {
        public RollingAverage xAvg = new RollingAverage(5, 1000); 
        public RollingAverage yAvg = new RollingAverage(5, 1000);
        public RollingAverage zAvg = new RollingAverage(5, 1000);
        public RollingAverage rotAvg = new RollingAverage(5, 1000);
         public int lastTargetAprilTag = -1;
         public int failedToFindTargetCount = 0;
         public PhotonVisionCameras camera;

         public CameraSpecificInfo(PhotonVisionCameras camera) {
            this.camera = camera;
         }
    }   

    //public CameraSpecificInfo leftCamInfo = new CameraSpecificInfo(Cameras.LEFT_CAM);
    //public CameraSpecificInfo rightCamInfo = new CameraSpecificInfo(PhotonVisionCameras.RIGHT_CAM);

    
    public VisionOdometry(RobotContainer rc) {
        // camera = new PhotonCamera("cam1");
        //this.camera = new PhotonCamera("Left"); // check if capitalized
        this.rc = rc;
        addSendables(SendableFluent.getInstance());
    }

    // public boolean updateTargetAprilTagData(int targetAprilTag, CameraSpecificInfo cameraSpecificInfo) {
    //     if (cameraSpecificInfo.lastTargetAprilTag != targetAprilTag) {
    //         cameraSpecificInfo.xAvg.reset();
    //         cameraSpecificInfo.yAvg.reset();
    //         cameraSpecificInfo.zAvg.reset();
    //         cameraSpecificInfo.rotAvg.reset();
                
    //         cameraSpecificInfo.failedToFindTargetCount = 0;
    //         cameraSpecificInfo.lastTargetAprilTag = targetAprilTag;
    //     }
    //     PhotonTrackedTarget target = rc.drivebase.photonvision.getTargetFromId(targetAprilTag, cameraSpecificInfo.camera);
    //     if (target == null) {
    //         cameraSpecificInfo.failedToFindTargetCount++;
    //         if (cameraSpecificInfo.failedToFindTargetCount > 8) {}
    //             cameraSpecificInfo.failedToFindTargetCount = 8;
    //         return false;
    //     }        
                
    //     cameraSpecificInfo.failedToFindTargetCount = cameraSpecificInfo.failedToFindTargetCount - 2;
    //     if (cameraSpecificInfo.failedToFindTargetCount < 0) {
    //         cameraSpecificInfo.failedToFindTargetCount = 0;
    //     }

    //     Transform3d t = target.getBestCameraToTarget().plus(PhotonVisionCameras.RIGHT_CAM.robotToCamTransform.inverse());
    //     //Cameras.LEFT_CAM.robotToCamTransform
    //     cameraSpecificInfo.xAvg.addValue(t.getMeasureX().baseUnitMagnitude());
    //     cameraSpecificInfo.yAvg.addValue(t.getMeasureY().baseUnitMagnitude());       
    //     cameraSpecificInfo.rotAvg.addValue(t.getRotation().getAngle()*57.324);
    //     return true;
    // }

    
    //public Pair<Rotation2d, Double> getTransformToTargetedAprilTag() {
    public Transform2d getTransformToTargetedAprilTag(CameraSpecificInfo cameraSpecificInfo) {
        double x= rc.drivebase.getPose().getX();
        double y =rc.drivebase.getPose().getY();
 
        int reefSectionId = RobotHeadingFinder.getReefSection(x, y);
        //int targetAprilTag = RobotHeadingFinder.getReefSectionAprilTagId(x, y);
        //int targetHeading = Utils.amRobotHeadingFinder.headingBySection[reefSectionId];
        int targetHeading = Utils.amBlueAlliance() ? RobotHeadingFinder.blueHeadingBySection[reefSectionId] : RobotHeadingFinder.redHeadingBySection[reefSectionId];
        
        //updateTargetAprilTagData(targetAprilTag, cameraSpecificInfo);
        // TODO: Fix?

        if (cameraSpecificInfo.failedToFindTargetCount > 5) {
            return null;
        }
        //return Utils.angleAndDistance(0, 0, xAvg.getAverage(), yAvg.getAverage());                
        return new Transform2d(
            cameraSpecificInfo.xAvg.getAverage(), 
            cameraSpecificInfo.yAvg.getAverage(), 
                    Rotation2d.fromDegrees(targetHeading));
    }

    @Override
    public SendableFluent addSendables(SendableFluent s) { 
        s.addDefaultKey("vision");   
        for(PhotonVisionCameras cam:PhotonVisionCameras.values()) {
            cam.addSendables(SendableFluent.getInstance());
        }
                 
        if (s.debugMode) {
            

            s.addString("trying to see", () -> {
                double x= rc.drivebase.getPose().getX();
                double y =rc.drivebase.getPose().getY();
                return RobotHeadingFinder.getReefSectionAprilTagId(x, y) + "";
            }, null);

            // s.addString("left_info", () -> {
            //         this.getTransformToTargetedAprilTag(leftCamInfo);
            //         return String.format("x=%.2f, y=%.2f, rot=%.2f", 
            //         leftCamInfo.xAvg.getAverage(), 
            //         leftCamInfo.yAvg.getAverage(), 
            //         leftCamInfo.rotAvg.getAverage());
            //     }, null);
                // s.addString("right_info", () -> {
                //     this.getTransformToTargetedAprilTag(rightCamInfo);
                //     return String.format("x=%.2f, y=%.2f, rot=%.2f", 
                //     rightCamInfo.xAvg.getAverage(), 
                //     rightCamInfo.yAvg.getAverage(), 
                //     rightCamInfo.rotAvg.getAverage());
                // }, null);
            
        }
        s.removeDefaultKey();
        return s;
    }   


}
