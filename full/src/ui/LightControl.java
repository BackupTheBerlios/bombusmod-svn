/*
 * LightControl.java
 *
 * Created on 27 Ноябрь 2006 г., 23:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui;

import Client.Config;
import Info.Version;

/**
 *
 * @author [AD]
 */
public class LightControl {
    
    private static Config cf=Config.getInstance();
    
    /** Creates a new instance of LightControl */
    public LightControl() {
    }
    
    public static void setLight(boolean state) {
            if (state==true) {
                com.siemens.mp.game.Light.setLightOn();
            } else {
                com.siemens.mp.game.Light.setLightOff();    
            }
            cf.lightState=state;
            cf.saveToStorage();
    }
}
