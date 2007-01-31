/*
 * MessageList.java
 *
 * Created on 11 Декабрь 2005 г., 3:02
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Messages;

import Client.Config;
import Client.Msg;
//import Messages.MessageView;
import images.SmilesIcons;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.ComplexString;
import ui.VirtualElement;
import ui.VirtualList;
import ui.FontCache;

/**
 *
 * @author EvgS
 */
public abstract class MessageList 
    extends VirtualList
    implements CommandListener
{
    
    protected Vector messages;
    
    protected Command cmdBack = new Command(SR.MS_BACK, Command.BACK, 99);
    protected Command cmdUrl = new Command(SR.MS_GOTO_URL, Command.SCREEN, 80);
    
    /** Creates a new instance of MessageList */
  
    public MessageList() {
        super();
	messages=new Vector();
        smiles=Config.getInstance().smiles;
        //sd.config.updateTime();
    
	enableListWrapping(false);
	
        cursor=0;//activate
        
        addCommand(cmdBack);
        //addCommand(cmdUrl);
        stringHeight=FontCache.getMsgFont().getHeight();
    }

    public MessageList(Display display) {
        this();
        attachDisplay(display);
    }
    
    
    public abstract int getItemCount(); // из protected сделали public

    protected VirtualElement getItemRef(int index) {
	if (messages.size()<getItemCount()) messages.setSize(getItemCount());
	MessageItem mi=(MessageItem) messages.elementAt(index);
	if (mi==null) {
	    mi=new MessageItem(getMessage(index), this, smiles);
            mi.setEven( (index & 1) == 0);
            mi.getColor();
	    messages.setElementAt(mi, index);
	}
	return mi;
    }
    
    public abstract Msg getMessage(int index);
    
    public void markRead(int msgIndex) {}
    
    protected boolean smiles;

    public void commandAction(Command c, Displayable d) {
        if (c==cmdBack) destroyView();
        if (c==cmdUrl) {
            try {
                Vector urls=((MessageItem) getFocusedObject()).getUrlList();
                new MessageUrl(display, urls); //throws NullPointerException if no urls
            } catch (Exception e) {/* no urls found */}
        }
    }

    protected void keyPressed(int keyCode) { // overriding this method to avoid autorepeat
        super.keyPressed(keyCode);
        if (keyCode=='*') 
            try {
                ((MessageItem)getFocusedObject()).toggleSmiles();
            } catch (Exception e){}
    }

    public void keyGreen() { eventOk(); }
   
}