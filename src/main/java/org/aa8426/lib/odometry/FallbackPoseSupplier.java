package org.aa8426.lib.odometry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;

/** 
 * This class will be removed or improved.
 * 
 */
public class FallbackPoseSupplier {
    
    private List<Supplier<Pose2d>> suppliers = new ArrayList<>();
    
    /** First in is first used. */
    public FallbackPoseSupplier() {        
    }

    /** First in is first used. */
    public FallbackPoseSupplier add(Supplier<Pose2d> supplier) {
        suppliers.add(supplier);
        return this;
    }

    public Supplier<Pose2d> get() {
        return () -> {
            for(Supplier<Pose2d> supplier:suppliers) {
                Pose2d pose = supplier.get();
                if (pose != null) {
                    return pose;
                }
            }
            return null;
        };
    }
}
