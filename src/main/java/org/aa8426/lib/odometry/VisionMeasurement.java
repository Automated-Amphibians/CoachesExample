package org.aa8426.lib.odometry;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

public class VisionMeasurement {    
    public Pose2d pose2d;
    public double timestamp;
    public Matrix<N3, N1> stdDevs;

    public VisionMeasurement(Pose2d pose2d, double bestTargetAge) {
        this.pose2d = pose2d;
        this.timestamp = bestTargetAge;
    }    
    
}
