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
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import locale.SR;
import util.ClipBoard;

/**
 *
 * @author [AD]
 */
public class info implements CommandListener, ItemCommandListener {

    private Object item;
    private Display display;
    private Displayable parentView;
    
    private Form form;

    private String jid;

    private Contact contact;

    private String statusMess;

    private String data;
    
    private ClipBoard clipboard;  // The clipboard class
    
    protected Command cmdCopy = new Command("Copy text", Command.SCREEN, 7);  

    private String type;
    
    /** Creates a new instance of info */
    public info(String type, String data, Display display, Object item) {
        this.item=item;
        this.display=display;
        this.data=data;
        this.type=type;
        parentView=display.getCurrent();
        
        form=new Form("info");
        form.addCommand(new Command(SR.MS_CLOSE, Command.BACK, 99));
        
        
        if (data!=null) {
            Item stringitem=null;
            stringitem=new StringItem (type, data);
            stringitem.addCommand(cmdCopy);
            stringitem.setItemCommandListener(this);       
            form.append(stringitem);
        } else {
            boolean isContact=( item instanceof Contact );

            if (isContact) {
                contact=(Contact)item;
                jid=contact.bareJid;
                
                Item stringitem=null;
                stringitem=new StringItem ("jid", contact.bareJid);
                stringitem.addCommand(cmdCopy);
                stringitem.setItemCommandListener(this);
                
                form.append(stringitem);
                
                Item stringitem2=null;
                stringitem2=new StringItem ("status", contact.presence);
                stringitem2.addCommand(cmdCopy);
                stringitem2.setItemCommandListener(this);
                
                form.append(stringitem2);
            }
        }
        
        form.setCommandListener(this);
        display.setCurrent(form);
        
    }

    public void commandAction(Command c, Displayable d) {
        display.setCurrent(parentView);
    }

    public void commandAction(Command command, Item item) {
        if (command == cmdCopy)
        {
          try {
            String text=((StringItem) item).getText();
            CopyText(text);
          } catch (Exception e) {/*no messages*/}
        }
    }
    
    private void CopyText(String string) {
        clipboard.s=string;
    }
    
}
