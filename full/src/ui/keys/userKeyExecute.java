/*
 * userKeyExecute.java
 *
 * Created on 14 Сентябрь 2007 г., 13:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.keys;

import Client.ConfigForm;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author User
 */
public class userKeyExecute {

    private Display display;
    
    
    

    public userKeyExecute(Display display, int command) {
        this.display=display;
        
        switch (command) {
            case 0:
                //setWobble("bl!");
                break;
            case 1: 
                //setWobble("bl!");
                break;
            case 2: 
                //setWobble("bl!");
                break;
            case 3: 
                //setWobble("bl!");
                break;
            case 4: 
                new ConfigForm(display);
                break;
            case 5: 
                //setWobble("bl!");
                break;
            case 6: 
                //setWobble("bl!");
                break;
            case 7: 
                //setWobble("bl!");
                break;
            case 8: 
                //setWobble("bl!");
                break;
            case 9: 
                //setWobble("bl!");
                break;
            case 10: 
                new userKeysList(display);
                break;
        }
    } 
}
