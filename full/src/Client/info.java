/*
 * info.java
 *
 * Created on 11 Ноябрь 2006 г., 22:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Client;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import locale.SR;

/**
 *
 * @author [AD]
 */
public class info implements CommandListener {

    private Object item;
    private Display display;
    private Displayable parentView;
    
    private Form form;

    private String jid;

    private Contact contact;

    private String statusMess;

    private String data;
    
    /** Creates a new instance of info */
    public info(String data, Display display, Object item) {
        this.item=item;
        this.display=display;
        this.data=data;
        parentView=display.getCurrent();
        
        form=new Form("info");
        form.addCommand(new Command(SR.MS_CLOSE, Command.BACK, 99));
        
        StringBuffer Info=new StringBuffer();
        
        if (data!=null) {
            Info.append(data);    
        } else {
            boolean isContact=( item instanceof Contact );

            if (isContact) {
                contact=(Contact)item;
                jid=contact.bareJid;
                statusMess=jid+"\n"+contact.presence; 
            }
            Info.append(statusMess);
        }

        form.append(Info.toString());

        form.setCommandListener(this);
        display.setCurrent(form);
        
    }

    public void commandAction(Command c, Displayable d) {
        display.setCurrent(parentView);
    }
    
}
