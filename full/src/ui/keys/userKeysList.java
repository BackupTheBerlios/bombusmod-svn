/*
 * userKeysList.java
 *
 * Created on 14 �������� 2007 �., 10:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.keys;

import Client.Config;
import Client.StaticData;
import io.NvStorage;
import java.io.DataOutputStream;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;

public class userKeysList
//#ifdef USER_KEYS
//#         extends VirtualList 
//#endif
        implements CommandListener{
//#ifdef USER_KEYS
//#     Vector commandsList;
//#     
//#     Command cmdOK=new Command(SR.MS_OK, Command.OK,1);
//#     Command cmdAdd=new Command(SR.MS_ADD_CUSTOM_KEY, Command.SCREEN,3);
//#     Command cmdEdit=new Command(SR.MS_EDIT,Command.ITEM,3);
//#     Command cmdDel=new Command(SR.MS_DELETE,Command.ITEM,4);
//#     Command cmdCancel=new Command(SR.MS_BACK,Command.BACK,99);
//#     
//#     private Config cf=Config.getInstance();
//#endif
    
    /** Creates a new instance of AccountPicker */
    public userKeysList(Display display) {
//#ifdef USER_KEYS
//#         super();
//#         setMainBarItem(new MainBar(SR.MS_CUSTOM_KEYS));
//#         
//#         commandsList=userKeyExec.getInstance().commandsList;
//#        
//#         attachDisplay(display);
//#         addCommand(cmdAdd);
//#         
//#         commandState();
//#         setCommandListener(this);
//#endif
    }

//#ifdef USER_KEYS
//#     void commandState(){
//#         if (commandsList.isEmpty()) {
//#             removeCommand(cmdEdit);
//#             removeCommand(cmdDel);
//#             addCommand(cmdOK);
//#             addCommand(cmdCancel);
//#         } else {
//#             addCommand(cmdEdit);
//#             addCommand(cmdDel);
//#             addCommand(cmdOK);
//#             addCommand(cmdCancel);
//#         }
//# 
//#     }
//# 
//#     public VirtualElement getItemRef(int Index) { 
//#         
//#         return (VirtualElement)commandsList.elementAt(Index); 
//#     }
//#     protected int getItemCount() { return commandsList.size();  }
//#endif
    
    public void commandAction(Command c, Displayable d){
//#ifdef USER_KEYS
//#         VirtualList.canBack=true;
//#         if (c==cmdCancel) {
//#             destroyView();
//#         }
//#         if (c==cmdOK) {
//#             rmsUpdate();
//#             parentView=StaticData.getInstance().roster;
//#             destroyView();    
//#         }
//#         if (c==cmdEdit) 
//#             new userKeyEdit(this, display,(userKey)getFocusedObject());
//#         if (c==cmdAdd)
//#             new userKeyEdit(this, display, null);
//#         if (c==cmdDel) {
//#             userKeyExec.getInstance().commandsList.removeElement(getFocusedObject());
//#             
//#             rmsUpdate();
//#             moveCursorHome();
//#             commandState();
//#             redraw();
//#         }
//#endif
    }
    
    public void eventOk(){
//#ifdef USER_KEYS
//#         new userKeyEdit(this, display,(userKey)getFocusedObject());
//#endif
    }
    
    void rmsUpdate(){
//#ifdef USER_KEYS
//#         DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
//#         
//#         for (int i=0;i<commandsList.size();i++) {
//#             ((userKey)commandsList.elementAt(i)).saveToDataOutputStream(outputStream);
//#         }
//#         
//#         NvStorage.writeFileRecord(outputStream, userKey.storage, 0, true);
//#endif
    }    
}