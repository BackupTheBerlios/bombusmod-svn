/*
 * ContactMessageList.java
 *
 * Created on 19 Февраль 2005 г., 23:54
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import Messages.MessageList;
import Messages.MessageParser;
import archive.MessageArchive;
import images.RosterIcons;
import locale.SR;
import util.ClipBoard;
import ui.*;
import java.util.*;
import javax.microedition.lcdui.*;
/**
 *
 * @author Eugene Stahov
 */
public class ContactMessageList extends MessageList
{
    
    Contact contact;
    Command cmdSubscribe=new Command(SR.MS_SUBSCRIBE, Command.SCREEN, 1);
    Command cmdMessage=new Command(SR.MS_NEW_MESSAGE,Command.SCREEN,2);
    Command cmdResume=new Command(SR.MS_RESUME,Command.SCREEN,1);
    Command cmdQuote=new Command(SR.MS_QUOTE,Command.SCREEN,3);
    Command cmdReply=new Command(SR.MS_REPLY,Command.SCREEN,4);
    Command cmdArch=new Command(SR.MS_ADD_ARCHIVE,Command.SCREEN,5);
    Command cmdCopy = new Command("Copy", Command.SCREEN, 6);
    Command cmdPurge=new Command(SR.MS_CLEAR_LIST, Command.SCREEN, 7);
    Command cmdActive=new Command(SR.MS_ACTIVE_CONTACTS,Command.SCREEN, 8);
    
    private ClipBoard clipboard;
    
    StaticData sd;
    
    /** Creates a new instance of MessageList */
    public ContactMessageList(Contact contact, Display display) {
        super(display);
        this.contact=contact;
        sd=StaticData.getInstance();
        
        Title title=new Title(contact);
        setTitleItem(title);
        
        title.addRAlign();
        title.addElement(null);
        title.addElement(null);
        //setTitleLine(title);

        cursor=0;//activate
        
        addCommand(cmdMessage);
        addCommand(cmdPurge);
	addCommand(cmdActive);
        addCommand(cmdQuote);
        addCommand(cmdArch);
        addCommand(cmdCopy);

        setCommandListener(this);
        moveCursorTo(contact.firstUnread(), true);
        //setRotator();
    }
    
    public void showNotify(){
        super.showNotify();
        if (cmdResume==null) return;
        if (contact.msgSuspended==null) removeCommand(cmdResume);
        else addCommand(cmdResume);
        
        if (cmdSubscribe==null) return;
        try {
            Msg msg=(Msg) contact.msgs.elementAt(cursor); 
            if (msg.messageType==Msg.MESSAGE_TYPE_AUTH) addCommand(cmdSubscribe);
            else removeCommand(cmdSubscribe);
        } catch (Exception e) {}
        
    }
    
    protected void beginPaint(){
        getTitleItem().setElementAt(sd.roster.getEventIcon(), 2);
        //getTitleItem().setElementAt(contact.incomingComposing, 3);
    }
    
    public void markRead(int msgIndex) {
	if (msgIndex>=getItemCount()) return;
        if (msgIndex<contact.lastUnread) return;
        sd.roster.countNewMsgs();
    }
    
    
    public int getItemCount(){ return contact.msgs.size(); }
    //public Element getItemRef(int Index){ return (Element) contact.msgs.elementAt(Index); }

    public Msg getMessage(int index) { 
	Msg msg=(Msg) contact.msgs.elementAt(index); 
	if (msg.unread) contact.resetNewMsgCnt();
	msg.unread=false;
	return msg;
    }
    
    public void focusedItem(int index){ 
        markRead(index); 
    }
        
    public void commandAction(Command c, Displayable d){
        super.commandAction(c,d);
        if (c==cmdMessage) { 
            contact.msgSuspended=null; 
            keyGreen(); 
        }
        if (c==cmdResume) { keyGreen(); }
        if (c==cmdQuote) {
            try {
                new MessageEdit(display,contact,getMessage(cursor).toString());
            } catch (Exception e) {/*no messages*/}
        }
        if (c==cmdReply) {
            try {
                if (getMessage(cursor).messageType <= Msg.MESSAGE_TYPE_HISTORY) return;
                
                String body=getMessage(cursor).toString();
                int nickLen=body.indexOf(">");
                if (nickLen<0) nickLen=body.indexOf(" ");
                if (nickLen<0) return;
                
                new MessageEdit(display,contact,body.substring(0, nickLen)+": ");
            } catch (Exception e) {/*no messages*/}
        }
        if (c==cmdArch) {
            try {
                MessageArchive.store(getMessage(cursor));
            } catch (Exception e) {/*no messages*/}
        }
        if (c==cmdPurge) {
            clearMessageList();
        }
	
	if (c==cmdActive) {
	    new ActiveContacts(display, contact);
	}
        if (c == cmdCopy)
        {
            try {
                clipboard.s=getMessage(cursor).getBody();
            } catch (Exception e) {/*no messages*/}
        }
        
        if (c==cmdSubscribe) {
            if (contact.subscr==null) return;
            boolean subscribe = 
                    contact.subscr.startsWith("none") || 
                    contact.subscr.startsWith("from");
            if (contact.ask_subscribe) subscribe=false;

            boolean subscribed = 
                    contact.subscr.startsWith("none") || 
                    contact.subscr.startsWith("to");
                    //getMessage(cursor).messageType==Msg.MESSAGE_TYPE_AUTH;

        }
    }

    private void clearMessageList() {
        //TODO: fix scrollbar size
        moveCursorHome();
        contact.purge();
        messages=new Vector();
        System.gc();
        redraw();
    }
    
    public void keyGreen(){
        (new MessageEdit(display,contact,contact.msgSuspended)).setParentView(this);
        contact.msgSuspended=null;
    }
    
    public void keyRepeated(int keyCode) {
	if (keyCode==KEY_NUM3) new ActiveContacts(display, contact);
        if (keyCode==KEY_NUM0) clearMessageList();
	else super.keyRepeated(keyCode);
    }

    public void userKeyPressed(int keyCode) {
        super.userKeyPressed(keyCode);
        if (keyCode==keyClear) clearMessageList();
    }
}
