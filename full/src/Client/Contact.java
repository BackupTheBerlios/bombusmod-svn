/*
 * Contact.java
 *
 * Created on 6 января 2005 г., 19:16
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import History.HistoryStorage;
import images.RosterIcons;
import io.NvStorage;
//#if FILE_IO
import io.file.FileIO;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
//#endif
import ui.Colors;
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
    
    private static int COLORS[]={
        Colors.CONTACT_DEFAULT,
        Colors.CONTACT_CHAT,
        Colors.CONTACT_AWAY,
        Colors.CONTACT_XA,
        Colors.CONTACT_DND,
        Colors.CONTACT_DEFAULT,
        Colors.CONTACT_DEFAULT,
        Colors.CONTACT_DEFAULT
    };
    
    public final static byte ORIGIN_ROSTER=0;
    public final static byte ORIGIN_ROSTERRES=1;
    public final static byte ORIGIN_CLONE=2;
    public final static byte ORIGIN_PRESENCE=3;
    public final static byte ORIGIN_GROUPCHAT=4;
    public final static byte ORIGIN_GC_MEMBER=5;
    public final static byte ORIGIN_GC_MYSELF=6;

    private Integer incomingViewing;

    private String hisJid;
   
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
    
    public String presence;
    
    public boolean acceptComposing;
    public Integer incomingComposing;
    
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
        
        if (cf.lastMessages
            && sJid!=null
            /*&& (getGroupType()!=Groups.TYPE_SELF)
            && (getGroupType()!=Groups.TYPE_TRANSP)
            && (getGroupType()!=Groups.TYPE_SEARCH_RESULT)
            && (getGroupType()!=Groups.TYPE_NOT_IN_LIST)
            && (getGroupType()!=Groups.TYPE_IGNORE)
            && (origin!=ORIGIN_GROUPCHAT)*/) {
                loadRecentList(sJid);
        } else {
            System.out.println(Nick);
        }
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
                default: return RosterIcons.ICON_MESSAGE_INDEX;
            }
        if (incomingComposing!=null) return RosterIcons.ICON_COMPOSING_INDEX;
        if (incomingViewing!=null) return RosterIcons.ICON_VIEWING_INDEX;
        int st=(status==Presence.PRESENCE_OFFLINE)?offline_type:status;
        if (st<8) st+=transport; 
        return st;
    }
    public int getNewMsgsCount() {
        if (getGroupType()==Groups.TYPE_IGNORE) return 0;
        //return msgs.size()-lastReaded;
        if (newMsgCnt>-1) return newMsgCnt;
        int nm=0;
        unreadType=Msg.MESSAGE_TYPE_IN;
        for (Enumeration e=msgs.elements(); e.hasMoreElements(); ) {
            Msg m=(Msg)e.nextElement();
            if (m.unread) { 
                nm++;
                if (m.messageType==Msg.MESSAGE_TYPE_AUTH) unreadType=m.messageType;
            }
        }
        return newMsgCnt=nm;
    }
    
    public boolean needsCount(){ return (newMsgCnt<0);  }
    
    public boolean active(){
	if (msgs.size()>1) return true;
	if (msgs.size()==0) return false;
	return (((Msg)msgs.elementAt(0)).messageType!=Msg.MESSAGE_TYPE_PRESENCE);
    }
    
    public void resetNewMsgCnt() { newMsgCnt=-1;}
    
    public void setComposing (boolean state) {
        incomingComposing=(state)? new Integer(RosterIcons.ICON_COMPOSING_INDEX):null;
        //System.out.println("Composing:"+state);
    }
    
    public int compare(IconTextElement right){
        Contact c=(Contact) right;
        //1. status
        int cmp;
        //if (origin>=ORIGIN_GROUPCHAT && c.origin>=ORIGIN_GROUPCHAT) {
        //    if ((cmp=origin-c.origin) !=0) return cmp;
        //} else {
        //    if ((cmp=status-c.status) !=0) return cmp;
        //}
        if ((cmp=key0-c.key0) !=0) return cmp;
        if ((cmp=status-c.status) !=0) return cmp;
        if ((cmp=key1.compareTo(c.key1)) !=0) return cmp;
        if ((cmp=c.priority-priority) !=0) return cmp;
        return c.transport-transport;
        //return 0;
    };
    
    public void addMessage(Msg m) {
        boolean first_replace=false;
        if (m.isPresence()) { 
            presence=m.getBody();
            if (msgs.size()==1) 
                if ( ((Msg)msgs.firstElement()).isPresence())
                   if (origin!=ORIGIN_GROUPCHAT) first_replace=true;
        }

        if (cf.lastMessages
            && (m.messageType==Msg.MESSAGE_TYPE_IN)
            && (group.index!=Groups.TYPE_TRANSP)
            && (group.index!=Groups.TYPE_SEARCH_RESULT)
            && (group.index!=Groups.TYPE_NOT_IN_LIST)
            && (group.index!=Groups.TYPE_IGNORE)
            && (origin!=ORIGIN_GROUPCHAT)) {
                new HistoryStorage(bareJid, m.getBody(), false);
        }
        
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
            if (allowLog)
                //if (!first_replace || !m.)
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
               //FileIO f=FileIO.createConnection(cf.msgPath+histRecord+".txt");
               //f.Write(bodyMessage);
               file=FileIO.createConnection(cf.msgPath+histRecord+".txt");
                try {
                    //os=file.openOutputStream();
                    os = file.openOutputStream(0);
                    writeFile(bodyMessage);
                    os.close();
                    os.flush();
                    file.close();
                } catch (IOException ex) {
                    try {
                        file.close();
                    } catch (IOException ex2) {
                        ex2.printStackTrace();
                    }
                    ex.printStackTrace();
                }
            }
       }
//#endif
        // пїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ - presence, пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ
        if (first_replace) {
            msgs.setElementAt(m,0);
            return;
        } 
        msgs.addElement(m);
        if (m.unread) {
            lastUnread=msgs.size()-1;
            if (m.messageType>unreadType) unreadType=m.messageType;
            if (newMsgCnt>=0) newMsgCnt++;
        }
    }
//#if (FILE_IO)    
    void writeFile(byte b[]){
        try {
            os.write(b);
            filePos+=b.length;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
//#endif
  
    public int getColor() { return (status>7)?0:COLORS[status]; }

    public int getFontIndex(){
        return (status<5)?1:0;
    }
    
    public String toString() { 
        if (origin>ORIGIN_GROUPCHAT) return nick;
        if (origin==ORIGIN_GROUPCHAT) return getJid();
        return (nick==null)?getJid():nick+jid.getResource(); 
    }
    
    public final String getName(){ return (nick==null)?getBareJid():nick; }
    //public void onSelect(){}

    public final String getJid() {
        return jid.getJid();
    }

    public final String getBareJid() {
        return bareJid;
    }

    public final String getNickJid() {
        if (nick==null) return bareJid;
        return nick+" <"+bareJid+">";
    }
    
    public final void purge() {
        new HistoryStorage(getBareJid(), "", true);
        msgs=new Vector();
        vcard=null;
        resetNewMsgCnt();
    }
    
    public final void setSortKey(String sortKey){
        key1=(sortKey==null)? "": sortKey.toLowerCase();
    }
    
    public void setViewing (boolean state) {
        incomingViewing=(state)? new Integer(RosterIcons.ICON_COMPOSING_INDEX):null;
    }

    public String getTipString() {
        int nm=getNewMsgsCount();
        return (nm==0)? null:String.valueOf(nm);
    }

    public Group getGroup() { return group; }
    public int getGroupType() {  
        if (group==null) return 0; 
        return group.index;  
    }
    public boolean inGroup(Group ingroup) {  return group==ingroup;  }

    /*public void setGroupIndex(int groupIndex) {
        this.group = groupIndex;
    }*/
    public void setGroup(Group group) { this.group = group; }

    
    
//last messages loading   
    public void loadRecentList(String bareJid) {
        try {
            if (bareJid.indexOf("@")>-1) {
                hisJid=bareJid.replace('@', '%');
                DataInputStream is=NvStorage.ReadFileRecord(hisJid, 0);

                while (is.available()>0) {
                    System.out.println(bareJid+" "+is.readUTF());
                    Msg m=new Msg(Msg.MESSAGE_TYPE_IN, getJid(), null, is.readUTF());
                    msgs.addElement(m);
                }
                is.close();
            }
        } catch (Exception e) { e.printStackTrace(); }
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
