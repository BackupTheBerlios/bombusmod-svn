/*
 * ContactMessageList.java
 *
 * Created on 19.02.2005, 23:54
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package Client;
import Conference.MucContact;
import Messages.MessageList;
import archive.MessageArchive;
import images.RosterIcons;
import images.SmilesIcons;
import io.NvStorage;
import locale.SR;
//#if TEMPLATES
//# import templates.TemplateContainer;
//# import History.HistoryStorage;
//# import java.io.DataInputStream;
//#endif
import ui.MainBar;

//#ifdef ALT_INPUT
//# import ui.inputbox.Box;
//#endif

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
    Command cmdUnsubscribed=new Command(SR.MS_DECLINE, Command.SCREEN, 2);
    Command cmdMessage=new Command(SR.MS_NEW_MESSAGE,Command.SCREEN,3);
    Command cmdResume=new Command(SR.MS_RESUME,Command.SCREEN,1);
    Command cmdReply=new Command(SR.MS_REPLY,Command.SCREEN,4);
    Command cmdQuote=new Command(SR.MS_QUOTE,Command.SCREEN,5);
    Command cmdArch=new Command(SR.MS_ADD_ARCHIVE,Command.SCREEN,6);
    Command cmdPurge=new Command(SR.MS_CLEAR_LIST, Command.SCREEN, 7);
//#if LAST_MESSAGES
//#     Command cmdRecent=new Command("Last Messages",Command.SCREEN,8);
//#endif
    //Command cmdContact=new Command(SR.MS_CONTACT,Command.SCREEN,9);
    Command cmdActive=new Command(SR.MS_ACTIVE_CONTACTS,Command.SCREEN,10);
    Command cmdCopy = new Command(SR.MS_COPY, Command.SCREEN, 11);
    Command cmdCopyPlus = new Command("+ "+SR.MS_COPY, Command.SCREEN, 11);
//#if TEMPLATES
//#     Command cmdTemplate=new Command(SR.MS_SAVE_TEMPLATE,Command.SCREEN,13);
//#endif
    Command cmdSendBuffer=new Command("Send Buffer", Command.SCREEN, 14);
    
     
    private ClipBoard clipboard;

    StaticData sd;
    
    private Config cf=Config.getInstance();
//#if LAST_MESSAGES
//#     private boolean hisStorage=(cf.lastMessages)?true:false;    
//#endif
    
//#ifdef ALT_INPUT
//#     private boolean startMessage=false;
//#     private String text="";
//#endif
    
    private boolean composing=true;
  
    /** Creates a new instance of MessageList */
    public ContactMessageList(Contact contact, Display display) {
        super(display);
        this.contact=contact;
        sd=StaticData.getInstance();
        
        MainBar mainbar=new MainBar(contact);
        setMainBarItem(mainbar);
        
        mainbar.addRAlign();
        mainbar.addElement(null);
        mainbar.addElement(null);

        cursor=0;//activate
//#if LAST_MESSAGES      
//#         if (hisStorage && contact instanceof MucContact==false) addCommand(cmdRecent);
//#endif        
        addCommand(cmdMessage);
        if (contact instanceof MucContact && contact.origin==Contact.ORIGIN_GROUPCHAT) {
            addCommand(cmdReply);
        }
        addCommand(cmdPurge);
        //addCommand(cmdContact);
	addCommand(cmdActive);
        addCommand(cmdQuote);
        addCommand(cmdArch);
//#if TEMPLATES
//#         addCommand(cmdTemplate);
//#endif
        addCommand(cmdCopy);

        setCommandListener(this);

        moveCursorTo(contact.firstUnread(), true);
        
        contact.setViewing(false);
        contact.setAppearing(false);
    }
    
    public void showNotify(){
        super.showNotify();
        if (cmdResume==null) return;
        if (contact.msgSuspended==null) removeCommand(cmdResume);
        else addCommand(cmdResume);
        
        if (cmdSubscribe==null) return;
        try {
            Msg msg=(Msg) contact.msgs.elementAt(cursor); 
            if (msg.messageType==Msg.MESSAGE_TYPE_AUTH) {
                addCommand(cmdSubscribe);
                addCommand(cmdUnsubscribed);
            }
            else {
                removeCommand(cmdSubscribe);
                removeCommand(cmdUnsubscribed);
            }
        } catch (Exception e) {}
        
        if (!clipboard.isEmpty()) {
            addCommand(cmdCopyPlus);
            addCommand(cmdSendBuffer);
        }
    }
    
    protected void beginPaint(){
        getMainBarItem().setElementAt((contact.vcard==null)?null:RosterIcons.iconHasVcard, 3);
        
        if (cursor==(messages.size()-1)) {
            markRead(cursor);
            if (contact.moveToLatest) {
                contact.moveToLatest=false;
                moveCursorEnd();
            }
        }
        getMainBarItem().setElementAt(sd.roster.getEventIcon(), 2);
    }    
    
    public void markRead(int msgIndex) {
	if (msgIndex>=getItemCount()) return;
        if (msgIndex<contact.lastUnread) return;
        
        sd.roster.countNewMsgs();
    }
    
    public int getItemCount(){ return contact.msgs.size(); }

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
		
        /** login-insensitive commands */
        if (c==cmdArch) {
            try {
                MessageArchive.store(getMessage(cursor));
            } catch (Exception e) {/*no messages*/}
        }
        if (c==cmdPurge) {
            if (messages.isEmpty()) return;
            clearReadedMessageList();
        }
        
        /** login-critical section */
        if (!sd.roster.isLoggedIn()) return;

        if (c==cmdMessage) { 
            contact.msgSuspended=null; 
            keyGreen(); 
        }
        if (c==cmdResume) { keyGreen(); }
        if (c==cmdQuote) {
            try {
                String msg=new StringBuffer()
                    .append((char)0xbb) // »
                    .append(getMessage(cursor).toString())
                    .append("\n")
                    .toString();
                new MessageEdit(display,contact,msg);
                msg=null;
            } catch (Exception e) {/*no messages*/}
        }/*
        if (c==cmdContact) {
            new RosterItemActions(display, contact, -1);
        }*/
	
	if (c==cmdActive) {
	    new ActiveContacts(display, contact);
	}
        
        if (c==cmdReply) {
            try {
		if (getMessage(cursor).messageType < Msg.MESSAGE_TYPE_HISTORY) return;
                if (getMessage(cursor).messageType == Msg.MESSAGE_TYPE_SUBJ) return;
				
                Msg msg=getMessage(cursor);
               
                new MessageEdit(display,contact,msg.from+": ");
            } catch (Exception e) {/*no messages*/}
        }
        
        if (c == cmdCopy)
        {
            try {
                StringBuffer clipstr=new StringBuffer();
                clipstr.append((getMessage(cursor).getSubject()==null)?"":getMessage(cursor).getSubject()+"\n");
                clipstr.append(getMessage(cursor).getBody());
                clipboard.setClipBoard(clipstr.toString());
                clipstr=null;
            } catch (Exception e) {/*no messages*/}
        }
        
        if (c==cmdCopyPlus) {
            try {
                StringBuffer clipstr=new StringBuffer();
                clipstr.append(clipboard.getClipBoard());
                clipstr.append("\n\n");
                clipstr.append((getMessage(cursor).getSubject()==null)?"":getMessage(cursor).getSubject()+"\n");
                clipstr.append(getMessage(cursor).getBody());
                
                clipboard.setClipBoard(clipstr.toString());
                clipstr=null;
            } catch (Exception e) {/*no messages*/}
        }
//#if TEMPLATES
//#         if (c==cmdTemplate) {
//#             try {
//#                 TemplateContainer.store(getMessage(cursor));
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#endif
        if (c==cmdSubscribe) {
            sd.roster.doSubscribe(contact);
        }
		
        if (c==cmdUnsubscribed) {
            sd.roster.sendPresence(contact.getBareJid(), "unsubscribed", null);
        }
        
        if (c==cmdSendBuffer) {
            String from=StaticData.getInstance().account.toString();
            String body=clipboard.getClipBoard();
            String subj=null;

            Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,body);

            try {
                if (body!=null)
                    sd.roster.sendMessage(contact, body, subj, 1);
                contact.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,"message sended from clipboard("+body.length()+"chars)"));
            } catch (Exception e) {
                contact.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,"message NOT sended"));
            }
            redraw();
        }
//#if LAST_MESSAGES
//#         if (c==cmdRecent) {
//#             //new HistoryList(contact.getBareJid(), display);
//#             loadRecentList();
//#         }
//#endif
    }

    private void clearReadedMessageList() {
//#if LAST_MESSAGES
//#         if (hisStorage) new HistoryStorage(contact.getBareJid(), "", true);
//#endif
        contact.smartPurge(cursor+1);
        messages=new Vector();
        
        moveCursorHome();  
        
        System.gc();
        redraw();
    }
    
    public void keyGreen(){
        if (!sd.roster.isLoggedIn()) return;
//#if ALT_INPUT   
//#         if (cf.altInput) {
//#             if (!startMessage) {
//#                     startMessage=true;
//#                     updateBottom(1);
//#             } else {
//#                 text=inputbox.getText();
//#                 //System.out.println(text);
//#                 sendMessage();
//#                 startMessage=false;
//#                 updateBottom(-10000);
//#                 redraw();
//#             }
//#         } else {
//#endif
            (new MessageEdit(display,contact,contact.msgSuspended)).setParentView(this);
            contact.msgSuspended=null;
//#if ALT_INPUT
//#         }
//#endif
    }
    
    public void keyRepeated(int keyCode) {
        if (keyCode==KEY_NUM0) clearReadedMessageList();
        else if (keyCode==KEY_NUM4) nextContact(-1);
        else if (keyCode==KEY_NUM6) nextContact(1);
	else super.keyRepeated(keyCode);
    }       

    public void userKeyPressed(int keyCode) {
        super.userKeyPressed(keyCode);
//#if ALT_INPUT  
//#         if (cf.altInput) {
//#             if (!startMessage) {
//#                 if (keyCode==KEY_NUM3) new ActiveContacts(display, contact);
//#                 if (keyCode==KEY_NUM4) nextContact(-1);
//#                 if (keyCode==KEY_NUM6) nextContact(1);
//#                 if (keyCode==keyClear) {
//#                     if (messages.isEmpty()) return;
//#                     clearReadedMessageList();
//#                 }
//#             } else {
//#                 if (keyCode==KEY_NUM1) updateBottom(1);
//#                 if (keyCode==KEY_NUM2) updateBottom(2);
//#                 if (keyCode==KEY_NUM3) updateBottom(3);
//#                 if (keyCode==KEY_NUM4) updateBottom(4);
//#                 if (keyCode==KEY_NUM5) updateBottom(5);
//#                 if (keyCode==KEY_NUM6) updateBottom(6);
//#                 if (keyCode==KEY_NUM7) updateBottom(7);
//#                 if (keyCode==KEY_NUM8) updateBottom(8);
//#                 if (keyCode==KEY_NUM9) updateBottom(9);
//#                 if (keyCode==KEY_NUM0) updateBottom(0);
//#                 if (keyCode==KEY_POUND) updateBottom(-1);
//#             }
//#         } else {
//#endif
            if (keyCode==KEY_NUM3) new ActiveContacts(display, contact);
            if (keyCode==KEY_POUND) {
                try {
                    if (getMessage(cursor).messageType < Msg.MESSAGE_TYPE_HISTORY) return;
                    if (getMessage(cursor).messageType == Msg.MESSAGE_TYPE_SUBJ) return;

                    Msg msg=getMessage(cursor);

                    new MessageEdit(display,contact,msg.from+": ");
                } catch (Exception e) {/*no messages*/}
            }
            if (keyCode==keyClear) {
                if (messages.isEmpty()) return;
                clearReadedMessageList();
            }
//#if ALT_INPUT  
//#         }
//#endif
    }
//#ifdef ALT_INPUT
//#     private void sendMessage(){
//#         try {
//#                 int comp=0; // composing event off
//#                 Roster r=StaticData.getInstance().roster;
//#                 if (text!=null) {
//#                     String from=StaticData.getInstance().account.toString();
//#                     
//#                     if (contact.origin!=Contact.ORIGIN_GROUPCHAT) {
//#                         contact.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,null,text));
//#                         comp=1; // composing event in message
//#                     }
//# 
//#                 } else if (contact.acceptComposing) comp=(composing)? 1:2;
//# 
//#                 if (!cf.eventComposing) comp=0;
//# 
//#                 try {
//#                     if (text!=null || comp>0)
//#                     r.sendMessage(contact, text, null, comp);
//#                 } catch (Exception e) { }
//#         } catch (Exception e) { }
//#     } 
//# 
//#     private void updateBottom(int key){
//#         if (startMessage) {
//#              if (inputbox!=null) {
//#                  inputbox.sendKey(key);
//#              } else {
//#                  Box inputbox=new Box();
//#                  setInputBoxItem(inputbox);
//#                  getInputBoxItem().sendKey(key);
//#              }
//#         } else {
//#             setInputBoxItem(null);
//#         }
//#     } 
//#endif

    private void nextContact(int direction){
	Vector activeContacts=new Vector();
        int nowContact = -1, contacts=-1;
	for (Enumeration r=StaticData.getInstance().roster.getHContacts().elements(); r.hasMoreElements(); ) 
	{
	    Contact c=(Contact)r.nextElement();
	    if (c.active()) {
                contacts=contacts+1;
                if (c==contact) nowContact=contacts;
                activeContacts.addElement(c);
            }
	}
        int size=activeContacts.size();
        
	if (size==0) return;

        try {
            nowContact+=direction;
            if (nowContact<0) nowContact=size-1;
            if (nowContact>=size) nowContact=0;
            
            Contact c=(Contact)activeContacts.elementAt(nowContact);
            new ContactMessageList((Contact)c,display).setParentView(StaticData.getInstance().roster);
        } catch (Exception e) { }
        
    }
//#if LAST_MESSAGES 
//#     private void loadRecentList() {
//#         try {
//#             DataInputStream is=NvStorage.ReadFileRecord(contact.bareJid.replace('@', '%'), 0);
//#             while (is.available()>0) {
//#                 contact.addMessage(new Msg(Msg.MESSAGE_TYPE_HISTORY, contact.bareJid.replace('@', '%'), null, is.readUTF()));
//#             }
//#             is.close();
//#         } catch (Exception e) {}
//#     }
//#endif
}
