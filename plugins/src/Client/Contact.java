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
//#if (FILE_IO && HISTORY)
//# import History.HistoryAppend;
//#endif
//#if MOOD
//# import UserMood.MoodLocale;
//#endif
import images.RosterIcons;
import ui.ColorScheme;
import ui.Time;
import vcard.VCard;
import java.util.*;
import ui.IconTextElement;
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
    
    public final static short ORIGIN_ROSTER=0;
    public final static short ORIGIN_ROSTERRES=1;
    public final static short ORIGIN_CLONE=2;
    public final static short ORIGIN_PRESENCE=3;
    public final static short ORIGIN_GROUPCHAT=4;
    public final static short ORIGIN_GC_MEMBER=5;
    public final static short ORIGIN_GC_MYSELF=6;

    public String nick;
    public Jid jid;
    public String bareJid;    // for roster/subscription manipulating
    protected int status;
    public int priority;
    private Group group;
    public int transport;
    
    public long ping=-1;
    
    public boolean moveToLatest=false;

    public String presence;
    public String statusString=null;
    
    public boolean acceptComposing;
    public boolean showComposing=false;
    
    public short deliveryType;
    
    public short incomingState=0;
    
    public final static short INC_NONE=0;
    public final static short INC_APPEARING=1;
    public final static short INC_VIEWING=2;
  
    //public boolean isSelected;
    
    public String msgSuspended;
    
    protected short key0;
    protected String key1;

    public byte origin;
    
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
    
    public boolean hasEntity;
    public String entityNode;
    public String entityVer;

//#ifdef MOOD
//#     public String mood=null;
//#     public String moodText=null;
//#endif
    
    private Config cf=Config.getInstance();

    protected Contact (){
        super(RosterIcons.getInstance());
        msgs=new Vector();
        key1="";
    }
    
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
//#ifdef ANTISPAM
//#         if (!tempMsgs.isEmpty())
//#             return RosterIcons.ICON_AUTHRQ_INDEX;
//#endif
       
        if (getNewMsgsCount()>0)  {
            switch (unreadType) {
                case Msg.MESSAGE_TYPE_AUTH: return RosterIcons.ICON_AUTHRQ_INDEX;
                default: return RosterIcons.ICON_MESSAGE_INDEX;
            }
        }
        
        if (showComposing==true) return RosterIcons.ICON_COMPOSING_INDEX;
        
        if (incomingState>0) return incomingState;
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
                if (m.messageType==Msg.MESSAGE_TYPE_AUTH) 
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
  
    public void setIncoming (int state) {
        short i=0;
        switch (state){
            case INC_APPEARING:
                i=RosterIcons.ICON_APPEARING_INDEX;
                break;
            case INC_VIEWING:
                i=RosterIcons.ICON_VIEWING_INDEX;
                break;
        }
        incomingState=i;
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
//#if FILE_IO && HISTORY
//# 
//#         if (cf.msgLog && cf.msgPath==null) {
//#ifdef POPUPS
//#             StaticData.getInstance().roster.setWobbler("Please enter valid path to store log");
//#endif
//#         } else 
//#             if (cf.msgLog && group.index!=Groups.TYPE_TRANSP && group.index!=Groups.TYPE_SEARCH_RESULT)
//#         {
//#             //String histRecord=(nick==null)?getBareJid():nick;
//#             String fromName=StaticData.getInstance().account.getUserName();
//#             if (m.messageType!=Msg.MESSAGE_TYPE_OUT) fromName=toString();
//#             boolean allowLog=false;
//#             switch (m.messageType) {
//#                 case Msg.MESSAGE_TYPE_PRESENCE:
//#                     if (origin>=ORIGIN_GROUPCHAT && cf.msgLogConfPresence) allowLog=true;
//#                     if (origin<ORIGIN_GROUPCHAT && cf.msgLogPresence) allowLog=true;
//#                     break;
//#                 default:
//#                     if (origin>=ORIGIN_GROUPCHAT && cf.msgLogConf) allowLog=true;
//#                     if (origin<ORIGIN_GROUPCHAT) allowLog=true;
//#             }
//#             if (origin!=ORIGIN_GROUPCHAT && this instanceof MucContact)
//#                  allowLog=false;
//#             if (allowLog)
//#             {
//#                 StringBuffer body=new StringBuffer(m.getDayTime());
//#                 body.append(" <");
//#                 body.append(fromName);
//#                 body.append("> ");
//#                 if (m.subject!=null) {
//#                     body.append(m.subject);
//#                     body.append("\r\n");
//#                 }
//#                 body.append(m.getBody());
//#                 body.append("\r\n");
//#                 
//#                 String histRecord=(nick==null)?getBareJid():nick;
//#                 
//#                 new HistoryAppend(body, histRecord);
//#             }
//#        }
//#endif

        if (first_replace) {
            msgs.setElementAt(m,0);
            
            if (cf.autoScroll)
                moveToLatest=true;
            
            return;
        } 
        
        msgs.addElement(m);
        
        if (cf.autoScroll)
            moveToLatest=true;
        
        if (m.unread) {
            lastUnread=msgs.size()-1;
            if (m.messageType>unreadType) unreadType=m.messageType;
            if (newMsgCnt>=0) newMsgCnt++;
        }
    }
  
    public int getColor() { 
        return (status>7)?0:COLORS[status];
    }

    public int getFontIndex(){
        if (!cf.showResources)
            return active()?1:0;
        return (status<5)?1:0;
    }
    
    public String toString() {
        if (!cf.showResources)
            return (nick==null)?getJid():nick;
        if (origin>ORIGIN_GROUPCHAT) 
            return nick;
        if (origin==ORIGIN_GROUPCHAT) 
            return getJid();
        return (nick==null)?getJid():nick+jid.getResource(); 
    }
   
    public final String getName(){ 
        return (nick==null)?getBareJid():nick; 
    }

    public final String getJid() {
        return jid.getJid();
    }

    public final String getBareJid() {
        return bareJid;
    }

    public String getNickJid() {
        if (nick==null) 
            return bareJid;
        return nick+" <"+bareJid+">";
    }
    
    public final void purge() {
//#ifdef ANTISPAM
//#        try {
//#            purgeTemps();
//#        } catch (Exception e) { }
//#endif
        msgs=new Vector();
        
        resetNewMsgCnt();
        
        try {
            if (vcard!=null) {
                vcard.clearVCard();
                vcard=null;
            }
        } catch (Exception e) { }
    }

    public final void smartPurge(int cursor) {
        try {
            if (cursor==msgs.size() && msgs.size()>0)
                msgs=new Vector();
            else
                for (int i=0; i<cursor; i++)
                    msgs.removeElementAt(0);
        } catch (Exception e) { }
        try {
            if (vcard!=null) {
                vcard.clearVCard();
                vcard=null;
            }
        } catch (Exception e) { }
        resetNewMsgCnt();
    }
    
    public final void setSortKey(String sortKey){
        key1=(sortKey==null)? "": sortKey.toLowerCase();
    }

    public String getTipString() {
        int nm=getNewMsgsCount();
        if (nm!=0) 
            return String.valueOf(nm);
        if (nick!=null) 
            return bareJid;
        return null;
    }

    public Group getGroup() { 
        return group; 
    }
    
    public int getGroupType() {  
        if (group==null) 
            return 0; 
        return group.index;  
    }
    
    public boolean inGroup(Group ingroup) {  
        return group==ingroup;  
    }

    public void setGroup(Group group) { 
        this.group = group; 
    }

    public void setStatus(int status) {
        setIncoming(0);
        this.status = status;
        if (status>=Presence.PRESENCE_OFFLINE) 
            acceptComposing=false;
    }

    public int getStatus() {
        return status;
    }
    
    public void setPing() {
        this.ping = Time.utcTimeMillis();
    }
    
    public void setComposing (boolean state) {
        showComposing=state;
    }

    public String getPing() {
        if (ping==-1) 
            return "";
        
        String timePing=Long.toString((Time.utcTimeMillis()-ping)/10);
        int dotpos=timePing.length()-2;
        
        StringBuffer s=new StringBuffer();
        
        s.append( (dotpos==0)? "0":timePing.substring(0, dotpos));
        s.append('.');
        s.append(timePing.substring(dotpos));
        s.append(" seconds");

        this.ping=-1;
        return String.valueOf(s);
    }
    
    void markDelivered(String id) {
        if (id==null) return;
        for (Enumeration e=msgs.elements(); e.hasMoreElements();) {
            Msg m=(Msg)e.nextElement();
            if (m.id!=null)
                if (m.id.equals(id)) 
                    m.delivered=true;
        }
    }
    
//#ifdef ANTISPAM
//#     public void addTempMessage(Msg m) {
//#         tempMsgs.addElement(m);
//#     }
//# 
//#     public final void purgeTemps() {
//#         tempMsgs=new Vector();
//#     }
//#endif

    public String getSecondString() {
        StringBuffer s=new StringBuffer();
        if (cf.rosterStatus) {
//#ifdef MOOD
//#             if (getUserMood()!=null) {
//#                 s.append(MoodLocale.loadString(getUserMood()));
//#                 if (getUserMoodText()!=null) {
//#                     s.append(" (");
//#                     s.append(getUserMoodText());
//#                     s.append(")");
//#                 }
//#             } else
//#endif
            if (statusString!=null)
                s.append(statusString);
            
            return (s.toString().length()<1)?null:s.toString();
        }
        s=null;
        return null;
    }
    
    
//#ifdef MOOD
//#     public String getUserMood() {
//#         return mood;
//#     }
//#     public void setUserMood (String mood) {
//#         this.mood=mood;
//#     }
//#     
//#     public String getUserMoodText() {
//#         return moodText;
//#     }
//#     public void setUserMoodText(String moodText) {
//#         this.moodText=moodText;
//#     }
//#endif
}
