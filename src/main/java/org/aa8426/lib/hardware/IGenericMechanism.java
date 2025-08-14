package org.aa8426.lib.hardware;

public interface IGenericMechanism {
    
    void setManualPower(Double power);    
    double incrementManualPower(double amount);

    void stop();
    void start();

    void setTarget(Double target);
    double incrementTarget(double amount);
    double getMinTarget();
    double getMaxTarget();
    double getCurrentPosition();
    boolean atTarget();
    double diffFromTarget();
    
}
