/*
 * Contact.java
 *
 * Created on 6.01.2005, 19:16
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
//#if LAST_MESSAGES
//# import History.HistoryStorage;
//#endif
import com.alsutton.jabber.JabberDataBlock;
import images.RosterIcons;
import io.NvStorage;
//#if FILE_IO
import io.file.FileIO;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
//#endif
import ui.ColorScheme;
import util.strconv;
import vcard.VCard;
import java.util.*;
import ui.IconTextElement;
import ui.ImageList;
import com.alsutton.jabber.datablocks.Presence;

/**
 * Contact
 * @author Eugene Stahov
 */
public class Contact extends IconTextElement{
    
    private int COLORS[]={
        ColorScheme.CONTACT_DEFAULT,
        ColorScheme.CONTACT_CHAT,
        ColorScheme.CONTACT_AWAY,
        ColorScheme.CONTACT_XA,
        ColorScheme.CONTACT_DND,
        ColorScheme.CONTACT_DEFAULT,
        ColorScheme.CONTACT_DEFAULT,
        ColorScheme.CONTACT_DEFAULT
    };
    
    public final static byte ORIGIN_ROSTER=0;
    public final static byte ORIGIN_ROSTERRES=1;
    public final static byte ORIGIN_CLONE=2;
    public final static byte ORIGIN_PRESENCE=3;
    public final static byte ORIGIN_GROUPCHAT=4;
    public final static byte ORIGIN_GC_MEMBER=5;
    public final static byte ORIGIN_GC_MYSELF=6;

    private Integer incomingViewing;

    /** Creates a new instance of Contact */
    protected Contact (){
        //lastReaded=0;
        super(RosterIcons.getInstance());
        msgs=new Vector();
        key1="";
    }

    public String nick;
    public Jid jid;
    public String bareJid;    // for roster/subscription manipulating
    protected int status;
    public int priority;
    private Group group;
    public int transport;
    
    public boolean moveToLatest=false;

    public String presence;
    
    public boolean acceptComposing;
    public Integer incomingComposing;
    private Integer incomingAppearing;
    
    public boolean isSelected;
    
    public String msgSuspended;
    
    //public int key1;
    protected int key0;
    protected String key1;

    public byte origin;
    //public boolean gcMyself;
    
    public String subscr;
    public int offline_type=Presence.PRESENCE_UNKNOWN;
    public boolean ask_subscribe;
    
    public Vector msgs;
    
//#ifdef ANTISPAM
//#     public Vector tempMsgs=new Vector();
//#endif
    
    private int newMsgCnt=-1;
    public int unreadType;
    public int lastUnread;
    
    public VCard vcard;
    
    private Config cf=Config.getInstance();
    
//#if FILE_IO    
    int fileSize;
    private int filePos;
    String filePath;
    private FileIO file;
    private OutputStream os;
//#endif
    
    //public long conferenceJoinTime;
    
    public int firstUnread(){
        int unreadIndex=0;
        for (Enumeration e=msgs.elements(); e.hasMoreElements();) {
            if (((Msg)e.nextElement()).unread) break;
            unreadIndex++;
        }
        return unreadIndex;
    }

    public Contact(final String Nick, final String sJid, final int Status, String subscr) {
        this();
        nick=Nick; jid= new Jid(sJid); status=Status;
        bareJid=sJid;
        this.subscr=subscr;
    
        setSortKey((Nick==null)?sJid:Nick);
        
        //calculating transport
        transport=RosterIcons.getInstance().getTransportIndex(jid.getTransport());
    }
    
    public Contact clone(Jid newjid, final int status) {
        Contact clone=new Contact();
        clone.group=group; 
        clone.jid=newjid; 
        clone.nick=nick;
        clone.key1=key1;
        clone.subscr=subscr;
        clone.offline_type=offline_type;
        clone.origin=ORIGIN_CLONE; 
        clone.status=status; 
        clone.transport=RosterIcons.getInstance().getTransportIndex(newjid.getTransport()); //<<<<

        clone.bareJid=bareJid;
        return clone;
    }
    
    public int getImageIndex() {
        if (getNewMsgsCount()>0) 
            switch (unreadType) {
                case Msg.MESSAGE_TYPE_AUTH: return RosterIcons.ICON_AUTHRQ_INDEX;
//#ifdef ANTISPAM
//#                 case Msg.MESSAGE_TYPE_REQUEST_PRIVATE: return RosterIcons.ICON_AUTHRQ_INDEX;
//#endif
                default: return RosterIcons.ICON_MESSAGE_INDEX;
            }
        if (incomingComposing!=null) return RosterIcons.ICON_COMPOSING_INDEX;
        if (incomingViewing!=null) return RosterIcons.ICON_VIEWING_INDEX;
        if (incomingAppearing!=null) return RosterIcons.ICON_APPEARING_INDEX;
        int st=(status==Presence.PRESENCE_OFFLINE)?offline_type:status;
        if (st<8) st+=transport; 
        return st;
    }
    
    public int getNewMsgsCount() {
        if (getGroupType()==Groups.TYPE_IGNORE) return 0;
        if (newMsgCnt>-1) return newMsgCnt;
        int nm=0;
        unreadType=Msg.MESSAGE_TYPE_IN;
        for (Enumeration e=msgs.elements(); e.hasMoreElements(); ) {
            Msg m=(Msg)e.nextElement();
            if (m.unread) { 
                nm++;
                if (m.messageType==Msg.MESSAGE_TYPE_AUTH
//#ifdef ANTISPAM
//#                         || m.messageType==Msg.MESSAGE_TYPE_REQUEST_PRIVATE
//#endif
                ) 
                    unreadType=m.messageType;
            }
        }
        return newMsgCnt=nm;
    }
    
    public boolean needsCount(){ 
        return (newMsgCnt<0);  
    }
    
    public boolean active(){
	if (msgs.size()>1) return true;
	if (msgs.size()==0) return false;
	return (((Msg)msgs.elementAt(0)).messageType!=Msg.MESSAGE_TYPE_PRESENCE);
    }
    
    public void resetNewMsgCnt() { newMsgCnt=-1;}
    
    public void setComposing (boolean state) {
        incomingComposing=(state)? new Integer(RosterIcons.ICON_COMPOSING_INDEX):null;
    }
    
    public void setViewing (boolean state) {
        incomingViewing=(state)? new Integer(RosterIcons.ICON_VIEWING_INDEX):null;
    }
    
    public void setAppearing (boolean state) {
        incomingAppearing=(state)? new Integer(RosterIcons.ICON_APPEARING_INDEX):null;
    }
    
    public int compare(IconTextElement right){
        Contact c=(Contact) right;
        int cmp;
        if ((cmp=key0-c.key0) !=0) return cmp;
        if ((cmp=status-c.status) !=0) return cmp;
        if ((cmp=key1.compareTo(c.key1)) !=0) return cmp;
        if ((cmp=c.priority-priority) !=0) return cmp;
        return c.transport-transport;
    };
    
//#ifdef ANTISPAM
//#     public void addTempMessage(Msg m) {
//#         tempMsgs.addElement(m);
//#     }
//#endif
    
    public void addMessage(Msg m) {
        boolean first_replace=false;
        if (m.isPresence()) { 
            presence=m.getBody();
            if (msgs.size()==1) 
                if ( ((Msg)msgs.firstElement()).isPresence())
                   if (origin!=ORIGIN_GROUPCHAT) first_replace=true;
        }
        
//#if LAST_MESSAGES
//#         if (cf.lastMessages
//#             && (origin!=ORIGIN_GROUPCHAT)
//#             && (getGroupType()!=Groups.TYPE_TRANSP)
//#             && (getGroupType()!=Groups.TYPE_IGNORE)
//#             && ((this instanceof MucContact)==false)
//#             && (m.messageType==Msg.MESSAGE_TYPE_IN))
//#         {
//#                 new HistoryStorage(getBareJid(), m.getBody(), false);
//#         }
//#endif        
//#if FILE_IO
        if (cf.msgLog && group.index!=Groups.TYPE_TRANSP && group.index!=Groups.TYPE_SEARCH_RESULT)
        {
            //String histRecord=(nick==null)?getBareJid():nick;
            String fromName=StaticData.getInstance().account.getUserName();
            if (m.messageType!=Msg.MESSAGE_TYPE_OUT) fromName=toString();
            boolean allowLog=false;
            switch (m.messageType) {
                case Msg.MESSAGE_TYPE_PRESENCE:
                    if (origin>=ORIGIN_GROUPCHAT && cf.msgLogConfPresence) allowLog=true;
                    if (origin<ORIGIN_GROUPCHAT && cf.msgLogPresence) allowLog=true;
                    break;
                default:
                    if (origin>=ORIGIN_GROUPCHAT && cf.msgLogConf) allowLog=true;
                    if (origin<ORIGIN_GROUPCHAT) allowLog=true;
            }
            if (origin!=ORIGIN_GROUPCHAT && this instanceof MucContact)
                 allowLog=false;
            if (allowLog)
            {
                StringBuffer body=new StringBuffer(m.getDayTime());
                body.append(" <");
                body.append(fromName);
                body.append("> ");
                if (m.subject!=null) {
                    body.append(m.subject);
                    body.append("\r\n");
                }
                body.append(m.getBody());
                body.append("\r\n");
                
               byte[] bodyMessage;
               String histRecord=(nick==null)?getBareJid():nick;
               if (cf.cp1251) {
                    bodyMessage=strconv.convUnicodeToCp1251(body.toString()).getBytes();
               } else {
                    bodyMessage=body.toString().getBytes();
               }
                file=FileIO.createConnection(cf.msgPath+histRecord+".txt");
                try {
                    os = file.openOutputStream(0);
                    writeFile(bodyMessage);
                    os.close();
                    os.flush();
                    file.close();
                } catch (IOException ex) {
                    try {
                        file.close();
                    } catch (IOException ex2) {
                        //ex2.printStackTrace();
                    }
                    //ex.printStackTrace();
                }
                body=null;
                bodyMessage=null;
            }
       }
//#endif

        if (first_replace) {
            msgs.setElementAt(m,0);
            
            if (cf.autoScroll)
                moveToLatest=true;
            
            return;
        } 
        if (cf.autoScroll)
            moveToLatest=true;
        
        msgs.addElement(m);
        
        if (cf.autoScroll)
            moveToLatest=true;
        
        if (m.unread) {
            lastUnread=msgs.size()-1;
            if (m.messageType>unreadType) unreadType=m.messageType;
            if (newMsgCnt>=0) newMsgCnt++;
        }
    }
//#if (FILE_IO)    
    private void writeFile(byte b[]){
        try {
            os.write(b);
            filePos+=b.length;
        } catch (IOException ex) { }
    }
//#endif
  
    public int getColor() { return (status>7)?0:COLORS[status]; }

    public int getFontIndex(){
        if (!cf.showResources)
            return active()?1:0;
        return (status<5)?1:0;
    }
    
    public String toString() {
        if (!cf.showResources)
            return (nick==null)?getJid():nick;
        if (origin>ORIGIN_GROUPCHAT) return nick;
        if (origin==ORIGIN_GROUPCHAT) return getJid();
        return (nick==null)?getJid():nick+jid.getResource(); 
    }
   
    public final String getName(){ return (nick==null)?getBareJid():nick; }

    public final String getJid() {
        return jid.getJid();
    }

    public final String getBareJid() {
        return bareJid;
    }

    public String getNickJid() {
        if (nick==null) return bareJid;
        return nick+" <"+bareJid+">";
    }
    
    public final void purge() {
//#ifdef ANTISPAM
//#         purgeTemps();
//#endif
        msgs=new Vector();
        vcard=null;
        resetNewMsgCnt();
    }
    
//#ifdef ANTISPAM
//#     public final void purgeTemps() {
//#         tempMsgs=new Vector();
//#     }
//#endif
    
    public final void smartPurge(int cursor) {
        try {
            if (cursor==msgs.size() && msgs.size()>0)
                msgs=new Vector();
            else
                for (int i=0; i<cursor; i++)
                    msgs.removeElementAt(0);
        } catch (Exception e) { }
        
        vcard=null;
        resetNewMsgCnt();
    }
    
    public final void setSortKey(String sortKey){
        key1=(sortKey==null)? "": sortKey.toLowerCase();
    }

    public String getTipString() {
        int nm=getNewMsgsCount();
        if (nm!=0) return String.valueOf(nm);
        if (nick!=null) return bareJid;
        return null;
    }

    public Group getGroup() { return group; }
    
    public int getGroupType() {  
        if (group==null) return 0; 
        return group.index;  
    }
    
    public boolean inGroup(Group ingroup) {  
        return group==ingroup;  
    }

    public void setGroup(Group group) { 
        this.group = group; 
    }

    public void setStatus(int status) {
        setComposing(false);
        this.status = status;
        if (status>=Presence.PRESENCE_OFFLINE) acceptComposing=false;
    }

    public int getStatus() {
        return status;
    }
}
