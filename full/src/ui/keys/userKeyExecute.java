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
import Client.StaticData;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Display;

/**
 *
 * @author User
 */
public class userKeyExecute {

    private Display display;

    private Vector commandsList;
    
    private boolean initiated = false;
    
    
    private void initCommands() {
        //System.out.println("start init commands");
        
        commandsList=new Vector();
        
        userKey u;

        int index=0;
        do {
            u=userKey.createFromStorage(index);
            if (u!=null) {
                commandsList.addElement(u);
                index++;
             }
       } while (u!=null);
        
        initiated=true;
        //System.out.println("commands initiated");
    }
 

    private int getCommandByKey(int key) {
        if (!initiated)
            initCommands();
        
        int commandNum = -1;
         for (Enumeration commands=commandsList.elements(); commands.hasMoreElements(); ) 
         {
            userKey userKeyItem=(userKey) commands.nextElement();
            if (userKeyItem.getKey()==key) {
                if (userKeyItem.getActive()) {
                    commandNum=userKeyItem.getCommandId();
                }
                //System.out.println("command found "+userKeysList.COMMANDS_DESC[commandNum]);
            }
            
         }
        return commandNum;
    }
    
    public userKeyExecute(Display display, int command) {
        this.display=display;
        
        if (command==10) { //TEMP REMOVE BEFORE TESTS!
            new userKeysList(display);
            return;
        }

        int commandId=getCommandByKey(command);
        
        //System.out.println("commandId "+commandId);

        switch (commandId) {
            /*
            "none",
            "config",
            "clear all",
            "reconnect",
            "statistics",
            "status",
            "",
            "",
            "",
            "",
            "user keys"
             */
            case -1: // ky-ky?
                break;
            case 0:
                // do nothing
                break;
            case 1: 
                new ConfigForm(display);
                break;
            case 2: 
                //setWobble("bl!");
                break;
            case 3: 
                //setWobble("bl!");
                break;
            case 4: 
                break;
            case 5:
                StaticData.getInstance().roster.cmdStatus();
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
            case 10: //key pound
                new userKeysList(display);
                break;
        }
    } 
}
