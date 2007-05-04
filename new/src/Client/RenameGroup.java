/*
 * RenameGroup.java
 *
 * Created on 25 јпрель 2007 г., 11:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Client;

import com.alsutton.jabber.datablocks.IqQueryRoster;
import java.util.Enumeration;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.TextFieldCombo;

/**
 *
 * @author User
 */
public class RenameGroup implements CommandListener{

    private Display display;
    private Form f;
    private TextFieldCombo groupName;
    private Group group;
    
    private Command cmdOk=new Command(SR.MS_OK, Command.SCREEN, 1);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);

    Roster roster;

    /**
     * Creates a new instance of ConferenceQuickPrivelegeModify
     */
    public RenameGroup(Display display, Group group) {

        this.group=group;
        this.display=display;
        
        f=new Form(SR.MS_NEWGROUP);
        
        groupName=new TextFieldCombo(SR.MS_NEWGROUP, group.getName(), 256, TextField.ANY, "group", display);
        f.append(groupName);
        
        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        
        f.setCommandListener(this);
        
        display.setCurrent(f);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdOk) 
            StaticData.getInstance().roster.changeGroup(group.getName(), groupName.getString()); 
        display.setCurrent(StaticData.getInstance().roster);
    }
}
