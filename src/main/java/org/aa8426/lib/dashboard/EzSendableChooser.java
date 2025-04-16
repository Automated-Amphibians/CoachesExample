package org.aa8426.lib.dashboard;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class EzSendableChooser {
    private final SendableChooser<String> chooser = new SendableChooser<>();    

    public EzSendableChooser(String label, String[] args) {
        for(int x=0;x<args.length;x++) {
            if (x == 0) {
                chooser.setDefaultOption(args[x], args[x]);
            } else {
                chooser.addOption(args[x], args[x]);
            }
        }        
        // Put the chooser on the SmartDashboard
        SmartDashboard.putData(label, chooser);
    }

    public String getSelected() {
        return chooser.getSelected();
    }
    
}
