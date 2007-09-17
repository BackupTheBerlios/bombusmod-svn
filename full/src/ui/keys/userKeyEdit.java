/*
 * userKeyEdit.java
 *
 * Created on 14 �������� 2007 �., 11:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.keys;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import locale.SR;

/**
 *
 * @author User
 */
class userKeyEdit implements CommandListener {
//#ifdef USER_KEYS
//#     private final userKeysList keysList;
//#     
//#     private Display display;
//#     private Displayable parentView;
//#     
//#     private Form f;
//#     private ChoiceGroup active;
//#     private ChoiceGroup keyDesc;
//#     private ChoiceGroup keyCode;
//#     
//#     Command cmdOk = new Command(SR.MS_OK , Command.OK, 1);
//#     Command cmdCancel = new Command(SR.MS_BACK , Command.BACK, 99);
//#     
//#     userKey u;
//#     
//#     boolean newKey;
//#endif
    public userKeyEdit(userKeysList keysList, Display display, userKey u) {
//#ifdef USER_KEYS
//# 	this.keysList = keysList;
//# 	this.display=display;
//# 	parentView=display.getCurrent();
//# 	
//# 	newKey=(u==null);
//# 	if (newKey) u=new userKey();
//# 	this.u=u;
//# 	
//# 	String mainbar = (newKey)?SR.MS_ADD_CUSTOM_KEY:(u.toString());
//# 	f = new Form(mainbar);
//#      
//# 	active = new ChoiceGroup(null, Choice.MULTIPLE);
//# 	active.append(SR.MS_ENABLED,null);
//# 	boolean a[] = {u.getActive()};
//# 	
//# 	active.setSelectedFlags(a);
//#         f.append(active);
//#         
//#         keyDesc=new ChoiceGroup(SR.MS_KEYS_ACTION, ChoiceGroup.POPUP);
//#         for (int i=0;i<userKeyExec.getInstance().COMMANDS_DESC.length;i++) {
//#             keyDesc.append(userKeyExec.getInstance().COMMANDS_DESC[i], null);
//#         }
//#         keyDesc.setSelectedIndex(u.getCommandId(), true);
//#         f.append(keyDesc);
//#         
//#         keyCode=new ChoiceGroup(SR.MS_KEY, ChoiceGroup.POPUP);
//#         for (int i=0;i<userKeyExec.getInstance().KEYS_NAME.length;i++) {
//#             keyCode.append(userKeyExec.getInstance().KEYS_NAME[i], null);
//#         }
//#         keyCode.setSelectedIndex(u.getKey(), true);
//#         f.append(keyCode);
//#         
//# 	f.addCommand(cmdOk);
//# 
//# 	f.addCommand(cmdCancel);
//# 	
//# 	f.setCommandListener(this);
//# 	
//# 	display.setCurrent(f);
//#endif
    }
    
    public void commandAction(Command c, Displayable d) {
//#ifdef USER_KEYS
//# 	if (c==cmdOk) {
//#             boolean a[] = new boolean[1];
//# 	    active.getSelectedFlags(a);
//#             
//#             u.setActive(a[0]);
//#             u.setCommand(keyDesc.getSelectedIndex());
//#             u.setKey(keyCode.getSelectedIndex());
//# 
//#             if (newKey) {
//#                 keysList.commandsList.addElement(u);
//#             }
//#             
//# 	    keysList.rmsUpdate();
//# 	    keysList.commandState();
//# 	}
//#         destroyView();
//#endif
    }
    
    public void destroyView()	{
//#ifdef USER_KEYS
//# 	if (display!=null)   display.setCurrent(parentView);
//#endif
    }
}