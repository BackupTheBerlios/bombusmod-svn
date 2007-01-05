/*
 * NewTemplate.java
 */

 package templates;

import Client.Msg;
import Client.Roster;
import Client.StaticData;
import javax.microedition.lcdui.*;
import locale.SR;

public class NewTemplate implements CommandListener, ItemStateListener 
{
    
    private Display display;
    private Displayable parentView;
    Form form;
    TextField templatebox;
    
    private Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    private Command cmdCancel = new Command(SR.MS_BACK, Command.BACK, 99);

    public NewTemplate(Display display) {
        this.display=display;
        parentView=display.getCurrent();         
        form=new Form(SR.MS_NEW_TEMPLATE);
        templatebox=new TextField(SR.MS_NEW_TEMPLATE, null, 1024, TextField.ANY);
        
        form.append(templatebox);
        form.addCommand(cmdOk);
        form.addCommand(cmdCancel);
	form.setCommandListener(this);
	form.setItemStateListener(this);
        
        display.setCurrent(form);
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) display.setCurrent(StaticData.getInstance().roster);
	if (c==cmdOk) {
            final Roster roster=StaticData.getInstance().roster;
	    String reason = templatebox.getString();
            Msg m=new Msg(Msg.MESSAGE_TYPE_OUT, "0", "", reason);
            TemplateContainer.store(m);
            display.setCurrent(parentView);
	}
    }

    public void itemStateChanged(Item item) {
    }
}