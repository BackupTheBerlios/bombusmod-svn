/*
 * userKeyEdit.java
 *
 * Created on 14 Сентябрь 2007 г., 11:01
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
import javax.microedition.lcdui.ItemStateListener;
import locale.SR;

/**
 *
 * @author User
 */
class userKeyEdit implements CommandListener {
    
    private final userKeysList keysList;
    
    private Display display;
    private Displayable parentView;
    
    private Form f;
    private ChoiceGroup active;
    private ChoiceGroup keyDesc;
    private ChoiceGroup keyCode;
    
    Command cmdOk = new Command(SR.MS_OK , Command.OK, 1);
    Command cmdCancel = new Command(SR.MS_BACK , Command.BACK, 99);
    
    userKey u;
    
    boolean newKey;
    
    public userKeyEdit(userKeysList keysList, Display display, userKey u) {
	this.keysList = keysList;
	this.display=display;
	parentView=display.getCurrent();
	
	newKey=(u==null);
	if (newKey) u=new userKey();
	this.u=u;
	
	String mainbar = (newKey)?"New key":(u.toString());
	f = new Form(mainbar);
     
	active = new ChoiceGroup(null, Choice.MULTIPLE);
	active.append("active",null);
	boolean a[] = {u.getActive()};
	
	active.setSelectedFlags(a);
        f.append(active);
        
        keyDesc=new ChoiceGroup("Key action", ChoiceGroup.POPUP);
        for (int i=0;i<keysList.COMMANDS_DESC.length;i++) {
            keyDesc.append(keysList.COMMANDS_DESC[i], null);
        }
        keyDesc.setSelectedIndex(u.getCommandId(), true);
        f.append(keyDesc);
        
        keyCode=new ChoiceGroup("Key", ChoiceGroup.POPUP);
        for (int i=0;i<keysList.KEYS_NAME.length;i++) {
            keyCode.append(keysList.KEYS_NAME[i], null);
        }
        keyCode.setSelectedIndex(u.getKey(), true);
        f.append(keyCode);
        
	f.addCommand(cmdOk);

	f.addCommand(cmdCancel);
	
	f.setCommandListener(this);
	
	display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d) {
	if (c==cmdOk) {
            boolean a[] = new boolean[1];
	    active.getSelectedFlags(a);
            
            u.setActive(a[0]);
            u.setCommand(keyDesc.getSelectedIndex());
            u.setKey(keyCode.getSelectedIndex());

            if (newKey) 
                keysList.commandsList.addElement(u);
            
	    keysList.rmsUpdate();
	    keysList.commandState();
	}
        destroyView();
    }
    
    public void destroyView()	{
	if (display!=null)   display.setCurrent(parentView);
    }
}