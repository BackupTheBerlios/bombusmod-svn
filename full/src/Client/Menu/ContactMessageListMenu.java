/*
 * ContactMessageListMenu.java
 *
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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
 */

package Client.Menu;
import Client.ActiveContacts;
import Client.Config;
import Client.Contact;
import Client.MessageEdit;
import Client.Msg;
import Client.StaticData;
import Conference.MucContact;
//#ifdef HISTORY
//# import History.HistoryAppend;
//#endif
import Messages.MessageItem;
import Messages.MessageUrl;
//#ifdef ARCHIVE
//# import archive.MessageArchive;
//#endif
import io.NvStorage;
import java.io.DataInputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
//#if TEMPLATES
//# import templates.TemplateContainer;
//# import History.HistoryStorage;
//# import ui.ColorScheme;
//#endif
import ui.Menu;
import ui.MenuItem;
import ui.VirtualList;
import util.ClipBoard;

public class ContactMessageListMenu
        extends Menu {

    private boolean connected;
    
    private ClipBoard clipboard=ClipBoard.getInstance();
    private StaticData sd=StaticData.getInstance();
    private Config cf=Config.getInstance();
//#if LAST_MESSAGES
//#     private boolean hisStorage=(cf.lastMessages)?true:false;    
//#endif

    private Contact contact;

    private Msg msg=null;
    
    private Displayable parentView;
    
    public ContactMessageListMenu(Display display, boolean loggedIn, Contact who, Msg message, int cursor) {
        super(who.toString());
        this.msg=message;
        this.connected=loggedIn;
        this.contact=who;
        this.parentView=display.getCurrent();

//#ifndef WMUC     
//#ifdef ANTISPAM
//#         if (contact instanceof MucContact && contact.origin!=Contact.ORIGIN_GROUPCHAT) {
//#             MucContact mc=(MucContact) contact;
//#             if (mc.roleCode!=MucContact.GROUP_MODERATOR) {
//#                 switch (mc.getPrivateState()) {
//#                     case MucContact.PRIVATE_DECLINE:
//#                         addItem(SR.MS_UNLOCK_PRIVATE, 23);
//#                         break;
//#                     case MucContact.PRIVATE_NONE:
//#                     case MucContact.PRIVATE_REQUEST:
//#                         addItem(SR.MS_UNLOCK_PRIVATE, 23);
//#                         addItem(SR.MS_BLOCK_PRIVATE, 22);
//#                         break;
//#                     case MucContact.PRIVATE_ACCEPT:
//#                         addItem(SR.MS_BLOCK_PRIVATE, 22);
//#                         break;
//#                 }
//#                 
//#             }
//#         }
//#endif
//#endif
        
//#if LAST_MESSAGES      
//#         if (hisStorage 
//#ifndef WMUC
//#                 && contact instanceof MucContact==false
//#endif
//#                 ) addItem(SR.MS_LAST_MESSAGES, 17);
//#endif 

        if (contact.msgSuspended!=null) 
            addItem(SR.MS_RESUME, 0);
        
        addItem(SR.MS_NEW_MESSAGE, 3);
        
        if (msg!=null)
            if (msg.messageType==Msg.MESSAGE_TYPE_AUTH) {
                addItem(SR.MS_SUBSCRIBE, 1);
                addItem(SR.MS_DECLINE, 2);
        }

//#ifndef WMUC
        if (contact instanceof MucContact && contact.origin==Contact.ORIGIN_GROUPCHAT) {
            addItem(SR.MS_REPLY, 4);
        }
//#endif
        if (msg!=null) {
            addItem(SR.MS_CLEAR_LIST, 7);
        }
        
        if (contact.origin!=Contact.ORIGIN_GROUPCHAT)
            addItem(SR.MS_CONTACT, 8);

        if (msg!=null) {
            if (msg.isHasUrl())
                addItem(SR.MS_GOTO_URL,9);
            if (msg.getBody().startsWith("xmlSkin"))
            addItem(SR.MS_USE_COLOR_SCHEME,10);
        }
	addItem(SR.MS_ACTIVE_CONTACTS, 11);
        if (msg!=null) {
            addItem(SR.MS_QUOTE, 5);
//#ifdef ARCHIVE
//#             addItem(SR.MS_ADD_ARCHIVE, 6);
//#endif
//#if TEMPLATES
//#         addItem(SR.MS_SAVE_TEMPLATE, 14);
//#endif
            addItem(SR.MS_COPY, 12);
//#ifdef FILE_IO
        addItem(SR.MS_SAVE_CHAT, 16);
//#endif
        }
        if (!clipboard.isEmpty()) {
            if (msg!=null) {
                addItem("+ "+SR.MS_COPY, 13);
            }
            addItem(SR.MS_SEND_BUFFER, 15);
        }

        attachDisplay(display);
    }
    
    public void eventOk(){
        MenuItem me=(MenuItem) getFocusedObject();
        if (me==null)  return;
        int index=me.index;

//#ifndef WMUC
        MucContact mc=null;
        if (contact instanceof MucContact) {
            mc=(MucContact) contact;
        }
//#endif
        
        switch (index) {
            case 0:
                (new MessageEdit(display,contact,contact.msgSuspended)).setParentView(parentView);
                contact.msgSuspended=null;
                return;
            case 1:
                sd.roster.doSubscribe(contact);
                break;
            case 2:
                sd.roster.sendPresence(contact.getBareJid(), "unsubscribed", null, false);
                break;
            case 3:
                contact.msgSuspended=null;
               (new MessageEdit(display,contact,contact.msgSuspended)).setParentView(parentView);
               return;
            case 4:
                Reply();
                return;
            case 5:
                Quote();
                return;
            case 6:
                try {
                    MessageArchive.store(msg);
                } catch (Exception e) {/*no messages*/}
                break;
            case 7:
                clearReadedMessageList();
                destroyView();
                return;
            case 8:
//#ifndef WMUC
                if (contact instanceof MucContact) {
                    new RosterItemActions(display, mc, -1);
                } else {
//#endif
                    new RosterItemActions(display, contact, -1);
//#ifndef WMUC
                }
//#endif
                return;
            case 9:
                try {
                    Vector urls=msg.getUrlList();
                    new MessageUrl(display, urls);
                } catch (Exception e) {/*no urls*/}
                break;
//#ifdef COLORS 
//#             case 10:
//#                 ColorScheme.getInstance().loadSkin(msg.getBody(),2);
//#                 break;
//#endif
            case 11:
                new ActiveContacts(display, contact);
                return;
            case 12:
                try {
                    StringBuffer clipstr=new StringBuffer();
                    clipstr.append((msg.getSubject()==null)?"":msg.getSubject()+"\n");
                    clipstr.append(msg.quoteString());
                    clipboard.setClipBoard(clipstr.toString());
                    clipstr=null;
                } catch (Exception e) {/*no messages*/}
                break;
            case 13:
                try {
                    StringBuffer clipstr=new StringBuffer();
                    clipstr.append(clipboard.getClipBoard());
                    clipstr.append("\n\n");
                    clipstr.append((msg.getSubject()==null)?"":msg.getSubject()+"\n");
                    clipstr.append(msg.quoteString());

                    clipboard.setClipBoard(clipstr.toString());
                    clipstr=null;
                } catch (Exception e) {/*no messages*/}
                break;
//#if TEMPLATES
//#             case 14:
//#                 try {
//#                     TemplateContainer.store(msg);
//#                 } catch (Exception e) {/*no messages*/}
//#                 break;
//#endif
            case 15:
                String from=StaticData.getInstance().account.toString();
                String body=clipboard.getClipBoard();

                String id=String.valueOf((int) System.currentTimeMillis());

                try {
                    if (body!=null)
                        sd.roster.sendMessage(contact, id, body, null, null);
                    contact.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,null,"message sended from clipboard("+body.length()+"chars)"));
                } catch (Exception e) {
                    contact.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,null,"message NOT sended"));
                }
                break;
//#if LAST_MESSAGES
//#             case 17:
//#                 loadRecentList();
//#                 break;
//#endif
//#ifndef WMUC 
//#ifdef ANTISPAM
//#             case 22:
//#                 mc.setPrivateState(MucContact.PRIVATE_DECLINE);
//# 
//#                 if (!contact.tempMsgs.isEmpty())
//#                     contact.purgeTemps();
//#                 break;
//#             case 23:
//#                 mc.setPrivateState(MucContact.PRIVATE_ACCEPT);
//# 
//#                 if (!contact.tempMsgs.isEmpty()) {
//#                     for (Enumeration tempMsgs=contact.tempMsgs.elements(); tempMsgs.hasMoreElements(); ) 
//#                     {
//#                         Msg tmpmsg=(Msg) tempMsgs.nextElement();
//#                         contact.addMessage(tmpmsg);
//#                     }
//#                     contact.purgeTemps();
//#                 }
//#                 break;
//#endif
//#endif
        }
        destroyView();
    }
    
    
//#if (FILE_IO && HISTORY)
//#     private void saveMessages() {
//#         if (cf.msgPath==null) return;
//# 
//#          String fromName=StaticData.getInstance().account.getUserName();
//#          StringBuffer body=new StringBuffer();
//#          
//#          for (Enumeration messages=contact.msgs.elements(); messages.hasMoreElements(); ) 
//#          {
//#             Msg message=(Msg) messages.nextElement();
//#              
//#             if (message.messageType!=Msg.MESSAGE_TYPE_OUT) fromName=contact.toString();
//# 
//#             body.append(message.getDayTime());
//#             body.append(" <");
//#             body.append(fromName);
//#             body.append("> ");
//#             if (message.subject!=null) {
//#                 body.append(message.subject);
//#                 body.append("\r\n");
//#             }
//#             body.append(message.getBody());
//#             body.append("\r\n");
//#          }
//# 
//#          //save
//#          
//#            String histRecord="log_"+((contact.nick==null)?contact.getBareJid():contact.nick);
//#            new HistoryAppend(body, histRecord);
//#     }
//#endif
    
    
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
    
    private void clearReadedMessageList() {
//#if LAST_MESSAGES
//#         if (hisStorage) new HistoryStorage(contact.getBareJid(), "", true);
//#endif
        contact.smartPurge(cursor+1);
       
        contact.lastUnread=contact.msgs.size()-1;
    }
    
    private void Reply() {
        try {
            if (msg.messageType < Msg.MESSAGE_TYPE_PRESENCE/*.MESSAGE_TYPE_HISTORY*/) return;
            if (msg.messageType == Msg.MESSAGE_TYPE_SUBJ) return;

            (new MessageEdit(display,contact,msg.from+": ")).setParentView(parentView);
        } catch (Exception e) {/*no messages*/}
    }
    
    private void Quote() {
        try {
            String message=new StringBuffer()
                .append((char)0xbb) // �
                .append(" ")
                .append(msg.quoteString())
                .append("\n")
                .toString();
            (new MessageEdit(display,contact,message)).setParentView(parentView);
            message=null;
        } catch (Exception e) {/*no messages*/}
    }
}
