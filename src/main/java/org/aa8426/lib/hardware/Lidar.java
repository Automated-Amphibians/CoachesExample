package org.aa8426.lib.hardware;

import java.util.Optional;

import org.aa8426.lib.dashboard.SendableFluent;
import org.aa8426.lib.dashboard.SendableFluent.ISendableFluent;

import au.grapplerobotics.LaserCan;
import au.grapplerobotics.interfaces.LaserCanInterface.Measurement;
import au.grapplerobotics.interfaces.LaserCanInterface.RangingMode;
import au.grapplerobotics.interfaces.LaserCanInterface.RegionOfInterest;
import au.grapplerobotics.interfaces.LaserCanInterface.TimingBudget;

/**
 * This class is a wrapper for the Grapple Robotics LaserCan Lidar. It provides
 * a simple interface to the Lidar and allows for easy configuration of the Lidar
 * settings.
 * 
 * https://grapplerobotics.au/product/lasercan/
 * 
 * 0 – 4000mm laser ranging sensor, FRC CAN compatible, adjustable FoV in a 25x25mm region.
 * 
 * Smaller regions will lower the ambient noise, but may reduce the maximum range.
 *   
 */
public class Lidar implements ISendableFluent {

    private LaserCan lc;
    private RegionOfInterest regionOfInterest;
    private RangingMode rangingMode;
    private TimingBudget timingBudget;
    private Measurement lastMeasurement;
    private boolean enabled = true;
    private int canId;

    static private enum LASERCAN_STATUS {
        LASERCAN_STATUS_UNKNOWN(-1),
        LASERCAN_STATUS_VALID_MEASUREMENT(0),
        LASERCAN_STATUS_NOISE_ISSUE(1),
        LASERCAN_STATUS_WEAK_SIGNAL(2),
        LASERCAN_STATUS_OUT_OF_BOUNDS(4),
        LASERCAN_STATUS_WRAPAROUND(7);
    
        int id;
    
        LASERCAN_STATUS(int id) {
          this.id = id;
        }
    
        static public LASERCAN_STATUS getStatusFromId(int id) {
          for(LASERCAN_STATUS lcs:LASERCAN_STATUS.values()) {
            if (lcs.id == id) {
              return lcs;
            }
          }
          return LASERCAN_STATUS_UNKNOWN;
        }
    }

    /**
     * Create an instance and set parameters for the Lidar
     * 
     * @param canId CAN ID of the LaserCan
     * @param rangingMode LaserCAN.RangingMode.SHORT or LaserCAN.RangingMode.LONG
     * @param regionSize size from 1-8
     * @param timingBudget e.g. LaserCan.TimingBudget.TIMING_BUDGET_33MS or LaserCan.TimingBudget.TIMING_BUDGET_100MS
     */
    public Lidar(int canId, LaserCan.RangingMode rangingMode, int regionSize, LaserCan.TimingBudget timingBudget) {
        //CanBridge.runTCP

        // lc.setRangingMode(LaserCan.RangingMode.SHORT);
        // lc.setRegionOfInterest(new LaserCan.RegionOfInterest(8, 8, 16, 16));
        // lc.setTimingBudget(LaserCan.TimingBudget.TIMING_BUDGET_33MS);
        lc = new LaserCan(canId);
        this.canId = canId;
        setRangingMode(rangingMode);
        setRegionOfInterest(createRegionOfInterest(regionSize));
        setTimingBudget(timingBudget);
        //addSendables(SendableFluent.getInstance());
    }

    public Lidar(int canId) {
        this(canId, LaserCan.RangingMode.SHORT, 4, LaserCan.TimingBudget.TIMING_BUDGET_33MS);
    }

    public void setRangingMode(LaserCan.RangingMode rangingMode) {
        try {
            lc.setRangingMode(rangingMode);
            this.rangingMode = rangingMode;
        } catch (au.grapplerobotics.ConfigurationFailedException e) {
            System.out.println("Configuration failed! " + e);
        }
    }

    public LaserCan.RangingMode getRangingMode() {
        return rangingMode;
    }

    public void setRegionOfInterest(LaserCan.RegionOfInterest regionOfInterest) {
        try {
            lc.setRegionOfInterest(regionOfInterest);
            this.regionOfInterest = regionOfInterest;
        } catch (au.grapplerobotics.ConfigurationFailedException e) {
            System.out.println("Configuration failed! " + e);
        }
    }

    /**
     * Set the region of interest for the Lidar
     * 
     * @param size 1-8  
     */
    public RegionOfInterest createRegionOfInterest(int size) {
        size = (size > 1) && (size < 9) ? size * 2 : 4; 
        return new RegionOfInterest(8, 8, size, size); // not sure why anyone would want anything except dead center 
    }

    public LaserCan.RegionOfInterest getRegionOfInterest() {
        return regionOfInterest;
    } 

    public void setTimingBudget(LaserCan.TimingBudget timingBudget) {
        try {
            lc.setTimingBudget(timingBudget);
            this.timingBudget = timingBudget;
        } catch (au.grapplerobotics.ConfigurationFailedException e) {
            System.out.println("Configuration failed! " + e);
        }
    }

    public LaserCan.TimingBudget getTimingBudget() {
        return timingBudget;
    }
    
    public Measurement getMeasurement() {
        if (!enabled) {
            return null;
        }
        lastMeasurement = lc.getMeasurement();
        return lastMeasurement;
    }

    public boolean lastMeasurementValid() {
        return lastMeasurement != null && lastMeasurement.status == LASERCAN_STATUS.LASERCAN_STATUS_VALID_MEASUREMENT.id;
    }

    public Optional<Integer> getDistance() {        
        getMeasurement();
        if (lastMeasurementValid()) {
            return Optional.of(lastMeasurement.distance_mm);
        } else {
            return Optional.empty();
        }        
    }

    @Override
    public SendableFluent addSendables(SendableFluent s) {
      s.addDefaultKey("laserCan-"+canId);
      if (s.debugMode) {
          s.addString("rangingMode", () -> getRangingMode().name(), null);
          s.addString("regionOfInterest", () -> {
            RegionOfInterest roi = getRegionOfInterest();
            return "x="+roi.x + "," + "y="+roi.y + "," + "w="+roi.w + "," + "h="+roi.h;
          }, null);
          s.addString("timingBudget", () -> getTimingBudget().name(), null);
      }
       
      
      s.addString("measureAmbient", () -> {return lastMeasurement == null ? "null" : ""+lastMeasurement.ambient;}, null);
      s.addString("measureDistance", () -> {return lastMeasurement == null ? "null" : ""+lastMeasurement.distance_mm;}, null);
      s.addString("measureStatus", () -> {return lastMeasurement == null ? "null" : LASERCAN_STATUS.getStatusFromId(lastMeasurement.status).name();}, null);
             
      s.removeDefaultKey();                
      return s;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }
}
