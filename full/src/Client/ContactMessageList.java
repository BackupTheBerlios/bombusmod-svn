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
import images.SmilesIcons;
import locale.SR;
import ui.controls.InputBox;
import vcard.VCard;
import ui.*;
import java.util.*;
import javax.microedition.lcdui.*;
import util.ClipBoard;
/**
 *
 * @author Eugene Stahov
 */
public class ContactMessageList extends MessageList
{
    
    Contact contact;
    Command cmdSubscribe=new Command(SR.MS_SUBSCRIBE, Command.SCREEN, 1);
    Command cmdMessage=new Command(SR.MS_NEW_MESSAGE,Command.SCREEN,2);
    Command cmdQuoteNick=new Command("Quote nickname",Command.SCREEN,3);
    Command cmdResume=new Command(SR.MS_RESUME,Command.SCREEN,1);
   
    Command cmdQuote=new Command(SR.MS_QUOTE,Command.SCREEN,4);
    Command cmdArch=new Command(SR.MS_ADD_ARCHIVE,Command.SCREEN,5);
    Command cmdPurge=new Command(SR.MS_CLEAR_LIST, Command.SCREEN, 6);
    Command cmdContact=new Command(SR.MS_CONTACT,Command.SCREEN,7);
    Command cmdActive=new Command(SR.MS_ACTIVE_CONTACTS,Command.SCREEN,7);
    Command cmdCopy = new Command("Copy", Command.SCREEN, 8);
     
    private ClipBoard clipboard;
    
    Vector activeContacts;

    StaticData sd;
    
    boolean newmessage=false;

    private boolean startMessage;

    private String text="";
    
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
        addCommand(cmdQuoteNick);
        addCommand(cmdPurge);
        addCommand(cmdContact);
	addCommand(cmdActive);
        //if (getItemCount()>0) {
            addCommand(cmdQuote);
            addCommand(cmdArch);
	//}
        addCommand(cmdCopy);
        setCommandListener(this);
        moveCursorTo(contact.firstUnread(), true);
        //setRotator();
        //InputBox bottom=new InputBox(contact.getJid());
        //setInputBoxItem(bottom);
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
	//Msg msg=getMessage(msgIndex);
        //if (msg.unread) contact.resetNewMsgCnt();
        //msg.unread=false;
        if (msgIndex<contact.lastUnread) return;
        //if (contact.needsCount())
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
        /*try {
            Msg msg=(Msg) contact.msgs.elementAt(index); 
            if (msg.messageType==Msg.MESSAGE_TYPE_AUTH) addCommand(cmdSubscribe);
            else removeCommand(cmdSubscribe);
        } catch (Exception e) {}*/
    }
        
    public void commandAction(Command c, Displayable d){
        super.commandAction(c,d);
        /*if (c==cmdBack) {
            //contact.lastReaded=contact.msgs.size();
            //contact.resetNewMsgCnt();            
            destroyView();
            return;
        }*/
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
        if (c==cmdArch) {
            try {
                MessageArchive.store(getMessage(cursor));
            } catch (Exception e) {/*no messages*/}
        }
        if (c==cmdPurge) {
            clearMessageList();
        }
        if (c==cmdContact) {
            if (sd.roster.theStream!=null)
                new RosterItemActions(display, contact);
        }
	
	if (c==cmdActive) {
	    new ActiveContacts(display, contact);
	}
        
        if (c==cmdQuoteNick) {
            try {
                String Body=getMessage(cursor).getBody();
                String nickname=Body.substring(0,Body.indexOf('>'));
                new MessageEdit(display,contact,nickname+": ");
            } catch (Exception e) {/*no messages*/}
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
            
            String to=contact.getBareJid();
            
            if (subscribed) sd.roster.sendPresence(to,"subscribed", null);
            if (subscribe) sd.roster.sendPresence(to,"subscribe", null);

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
        if (!startMessage) {
            super.userKeyPressed(keyCode);
            if (keyCode==KEY_NUM9) nextContact();
            if (keyCode==KEY_STAR) {
                if (Config.getInstance().altInput) {
                        startMessage=true;
                        updateBottom(-1);
                }
            }
            if (keyCode==keyClear) {
                new YesNoAlert(display, this, SR.MS_CLEAR_LIST, SR.MS_SURE_CLEAR){
                    public void yes() { clearMessageList(); }
                };
            }
        } else {
           
            super.userKeyPressed(keyCode);
            if (keyCode==KEY_NUM1) updateBottom(1);
            if (keyCode==KEY_NUM2) updateBottom(2);
            if (keyCode==KEY_NUM3) updateBottom(3);
            if (keyCode==KEY_NUM4) updateBottom(4);
            if (keyCode==KEY_NUM5) updateBottom(5);
            if (keyCode==KEY_NUM6) updateBottom(6);
            if (keyCode==KEY_NUM7) updateBottom(7);
            if (keyCode==KEY_NUM8) updateBottom(8);
            if (keyCode==KEY_NUM9) updateBottom(9);
            if (keyCode==KEY_NUM0) updateBottom(0);
            if (keyCode==KEY_STAR) {
                startMessage=false;
                try {
                    if (text!=null){
                        if (contact.origin!=Contact.ORIGIN_GROUPCHAT) {
                            String from=StaticData.getInstance().account.toString();
                            Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,null,text);
                            contact.addMessage(msg);
                        }
                        Roster r=StaticData.getInstance().roster;
                        r.sendMessage(contact, text, null, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                redraw();
            }
        }
    }
    
    public InputBox getBottomItem() {return (InputBox)bottom;}
    public void setInputBoxItem(InputBox bottom) { this.bottom=bottom; }
    
    private void updateBottom(int key){
        if (startMessage) {
                InputBox bottom=new InputBox(text, key);
                setInputBoxItem(bottom);
        } else {
            bottom=null;
        }
    }
    
    private void nextContact() {
	activeContacts=new Vector();
        int nowContact = -1, contacts=-1;
	for (Enumeration r=StaticData.getInstance().roster.getHContacts().elements(); 
	    r.hasMoreElements(); ) 
	{
	    Contact c=(Contact)r.nextElement();
	    if (c.active()) {
                contacts=contacts+1;
                if (c==contact) nowContact=contacts;
                activeContacts.addElement(c);
            }
	}
        
	if (getActiveCount()==0) return;
        
        if(nowContact<0) {
            nowContact=0;
        } else {
            if (nowContact+1>getActiveCount()) {
                nowContact=0;
            } else {
                nowContact=nowContact+1;                
            }
        }
        
        if (nowContact+1>getActiveCount()) nowContact=0;
        
	Contact c=(Contact)activeContacts.elementAt(nowContact);
	new ContactMessageList((Contact)c,display).setParentView(StaticData.getInstance().roster);
    }
    
    protected int getActiveCount() { return activeContacts.size(); }

}
