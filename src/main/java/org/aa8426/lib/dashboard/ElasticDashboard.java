package org.aa8426.lib.dashboard;

import edu.wpi.first.wpilibj.RobotController;


public class ElasticDashboard {
    
    

    EzSendableChooser ezSendableChooser;    
    
    public ElasticDashboard() {        
        //ezSendableChooser = new EzSendableChooser("Auto", rc.autos.getNames());                
    }

    public String getAutoSelected() {
        return ezSendableChooser.getSelected();
    }

    public static void updateRobotStats(double absoluteEncoderValue) {
        // Get battery voltage
        double batteryVoltage = RobotController.getBatteryVoltage();

        // Create a notification for the robot stats
        Elastic.Notification notification = new Elastic.Notification()
                .withTitle("Robot Stats")
                .withDescription("Battery Voltage: " + batteryVoltage + "V\n" +
                        "Absolute Encoder: " + absoluteEncoderValue)
                .withLevel(Elastic.Notification.NotificationLevel.INFO)
                .withDisplaySeconds(5);

        // Send the notification to the Elastic dashboard
        Elastic.sendNotification(notification);
    }

}
