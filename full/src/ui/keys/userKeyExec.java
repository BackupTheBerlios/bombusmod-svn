/*
 * userKeyExecute.java
 *
 * Created on 14.09.2007, 13:38
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ui.keys;

import Client.Config;
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
import locale.SR;
import midlet.BombusMod;

public class userKeyExec {
    
    private static userKeyExec instance;
    
    static Config cf;
    
    public static userKeyExec getInstance(){
	if (instance==null) {
	    instance=new userKeyExec();
            cf=Config.getInstance();
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
            }
            
         }
        return commandNum;
    }
    
    public boolean commandExecute(Display display, int command) { //return false if key not executed
        this.display=display;

        int commandId=getCommandByKey(command);
        
        boolean connected= ( StaticData.getInstance().roster.isLoggedIn() );

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
//#if (FILE_IO && FILE_TRANSFER)
//#             case 6: 
//#                 new io.file.transfer.TransferManager(display);
//#                 return true;
//#endif
//#ifdef ARCHIVE
//#             case 7: 
//#                 roster.cmdArchive();
//#                 return true;
//#endif
//#ifdef SERVICE_DISCOVERY
//#             case 8: 
//#                 if (connected) {
//#                     new ServiceDiscovery(display, null, null);
//#                     return true;
//#                 }
//#                 break;
//#endif
//#ifdef PRIVACY
//#             case 9: 
//#                 if (connected) new PrivacySelect(display);
//#                 return true;
//#endif
            case 10: //key pound
                new userKeysList(display);
                return true;
//#ifdef POPUPS
//#             case 11:
//#                 roster.cmdClearPopups();
//#                 return true;
//#endif
            case 12:
                cf.lightState=!cf.lightState;
                roster.setLight(cf.lightState);
                cf.saveToStorage();
                return true;
            case 13:
                new Info.InfoWindow(display);
                return true;
            case 14:
                if (cf.allowMinimize) {
                    BombusMod.getInstance().hideApp(true);
                    return true;
                }
                break;
            case 15:
                roster.connectionTerminated(new Exception("Simulated break"));
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
            SR.MS_NO,
            SR.MS_OPTIONS,
            SR.MS_CLEAN_ALL_MESSAGES,
            SR.MS_RECONNECT,
            SR.MS_STATS,
            SR.MS_STATUS_MENU,
            SR.MS_FILE_TRANSFERS,
            SR.MS_ARCHIVE,
            SR.MS_DISCO,
            SR.MS_PRIVACY_LISTS,
            SR.MS_CUSTOM_KEYS,
            SR.MS_CLEAR_POPUPS,
            SR.MS_FLASHLIGHT,
            SR.MS_ABOUT,
            SR.MS_APP_MINIMIZE,
            SR.MS_BREAK_CONECTION
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
