/*
 * userKeyExecute.java
 *
 * Created on 14 �������� 2007 �., 13:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.keys;

import Client.ConfigForm;
import Client.Roster;
import Client.StaticData;
//#ifdef PRIVACY
//# import PrivacyLists.PrivacySelect;
//#endif
//#ifdef SERVICE_DISCOVERY
//# import ServiceDiscovery.ServiceDiscovery;
//#endif
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Display;

public class userKeyExec {
    
    private static userKeyExec instance;
    
    public static userKeyExec getInstance(){
	if (instance==null) {
	    instance=new userKeyExec();
            instance.initCommands();
	}
	return instance;
    }

    private Display display;

    public Vector commandsList;
    
    private void initCommands() {
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
    }

    private int getCommandByKey(int key) {
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
    
    public boolean commandExecute(Display display, int command) { //return false if key not executed
        this.display=display;
        
        if (command==10) { //TEMP REMOVE BEFORE TESTS!
            new userKeysList(display);
            return true;
        }

        int commandId=getCommandByKey(command);
        
        boolean connected= ( StaticData.getInstance().roster.isLoggedIn() );
        
        //System.out.println("commandId "+commandId);

        Roster roster=StaticData.getInstance().roster;
        switch (commandId) {
            case -1: // ky-ky?
                break;
            case 0:
                // do nothing
                break;
            case 1: 
                new ConfigForm(display);
                return true;
            case 2: 
                roster.cleanupAllHistories();
                return true;
            case 3: 
                roster.connectionTerminated(new Exception("reconnect by user"));
                return true;
            case 4: 
                roster.showStats();
                return true;
            case 5:
                roster.cmdStatus();
                return true;
            case 6: 
                new io.file.transfer.TransferManager(display);
                return true;
            case 7: 
                roster.cmdArchive();
                return true;
//#ifdef SERVICE_DISCOVERY
//#             case 8: 
//#                 if (connected) {
//#                     new ServiceDiscovery(display, null, null);
//#                     return true;
//#                 }
//#endif
//#ifdef PRIVACY
//#             case 9: 
//#                 if (connected) new PrivacySelect(display);
//#                 return true;
//#endif
            case 10: //key pound
                new userKeysList(display);
                return true;
        }
        
        return false;
    } 


    static String getDesc(int descId) {
        return COMMANDS_DESC[descId];
    }
    
    static String getKeyDesc(int commandId) {
        return KEYS_NAME[commandId];
    }

    public static final String[] COMMANDS_DESC = {
            "none",
            "config",
            "clear all",
            "reconnect",
            "statistics",
            "status",
            "filetransfer",
            "archive",
            "service discovery",
            "privacy lists",
            "",
            "user keys"
    };
    
    public static final String[] KEYS_NAME = {
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "#"
    };
}
