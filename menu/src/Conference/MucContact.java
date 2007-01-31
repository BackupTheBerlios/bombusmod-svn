/*
 * MucContact.java
 *
 * Created on 2 Май 2006 г., 14:05
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Conference;

import Client.Contact;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Presence;
import images.RosterIcons;
import locale.SR;
import Client.Msg;

/**
 *
 * @author root
 */
public class MucContact extends Contact{
    
    public final static int AFFILIATION_OUTCAST=-1;
    public final static int AFFILIATION_NONE=0;
    public final static int AFFILIATION_MEMBER=1;
    public final static int AFFILIATION_ADMIN=2;
    public final static int AFFILIATION_OWNER=3;
    
    public final static int ROLE_VISITOR=-1;
    public final static int ROLE_PARTICIPANT=0;
    public final static int ROLE_MODERATOR=1;
    
    public String realJid;
    
    public String affiliation;
    public String role;
    
    public int roleCode;
    public int affiliationCode;
    
    public boolean commonPresence=true;
    
    public long lastMessageTime;

    /** Creates a new instance of MucContact */
    public MucContact(String nick, String jid) {
        super(nick, jid, Presence.PRESENCE_OFFLINE, "muc");
        offline_type=Presence.PRESENCE_OFFLINE;
    }
    
    public String processPresence(JabberDataBlock xmuc, Presence presence) {
        //System.out.println(presence);
        String from=jid.getJid();
        
        int presenceType=presence.getTypeIndex();
        
         if (presenceType==Presence.PRESENCE_ERROR) {
            JabberDataBlock error=presence.getChildBlock("error");
            String mucErrCode=error.getAttribute("code");
            int errCode=0;
            try {
                errCode=Integer.parseInt(mucErrCode);
            } catch (Exception e) { return "Unsupported MUC error"; }
            ConferenceGroup grp=(ConferenceGroup)getGroup();
            if (status>=Presence.PRESENCE_OFFLINE) testMeOffline();
			if (errCode!=409 || status>=Presence.PRESENCE_OFFLINE)
				setStatus(presenceType);
            switch (errCode) {
                case 401: return "Password required";
                case 403: return "You are banned in this room";
                case 404: return "Room does not exists";
                case 405: return "You can't create room on this server";
                case 406: return "Reserved roomnick must be used";
                case 407: return "This room is members-only";
                case 409: return "Nickname is already in use by another occupant";
                case 503: return "Maximum number of users has been reached in this room";
                default: return error.toString();
            }
         }
        
        JabberDataBlock item=xmuc.getChildBlock("item");   

        String role=item.getAttribute("role");
        if (role.equals("visitor")) roleCode=ROLE_VISITOR;
        if (role.equals("participant")) roleCode=ROLE_PARTICIPANT;
        if (role.equals("moderator")) roleCode=ROLE_MODERATOR;
        
        String affiliation=item.getAttribute("affiliation");
        if (affiliation.equals("owner")) affiliationCode=AFFILIATION_OWNER;
        if (affiliation.equals("admin")) affiliationCode=AFFILIATION_ADMIN;
        if (affiliation.equals("member")) affiliationCode=AFFILIATION_MEMBER;
        if (affiliation.equals("none")) affiliationCode=AFFILIATION_NONE;
        
        boolean roleChanged= !role.equals(this.role);
        boolean affiliationChanged= !affiliation.equals(this.affiliation);
        this.role=role;
        this.affiliation=affiliation;
        
        String chNick=item.getAttribute("nick");

        setSortKey(nick);
        
        if (role.equals("moderator")) {
            transport=RosterIcons.ICON_MODERATOR_INDEX;
            key0=1;
            //jidHash &= 0x3fffffff;
        } else {
            transport= (role.equals("visitor"))? RosterIcons.getInstance().getTransportIndex("conference_visitors") : 0;
            if (role.equals("participant")) {
                transport= (affiliation.equals("member"))? 0x00 : RosterIcons.getInstance().getTransportIndex("conference_visitors");
            }    
            key0=2;
            //jidHash |= 0x40000000;
        }


        int rp=from.indexOf('/');
        //String nick=from.substring(rp+1);
        
        JabberDataBlock statusBlock=xmuc.getChildBlock("status");
        int statusCode;
        try { 
            statusCode=Integer.parseInt( statusBlock.getAttribute("code") ); 
        } catch (Exception e) { statusCode=0; }
        

        StringBuffer b=new StringBuffer(nick);
        
        if (presence.getTypeIndex()==Presence.PRESENCE_OFFLINE) {
            String reason=item.getChildBlockText("reason");
            String realJid=item.getAttribute("jid");
            switch (statusCode) {
                
                case 303:
                    b.append(SR.MS_IS_NOW_KNOWN_AS);
                    b.append(chNick);
                    // исправим jid
                    String newJid=from.substring(0,rp+1)+chNick;
                    //System.out.println(newJid);
                    jid.setJid(newJid);
                    bareJid=newJid; // непонятно, зачем я так сделал...
                    from=newJid;
                    nick=chNick;
                    break;
                    
                case 307: //kick
                case 301: //ban
                    if (realJid!=null) {
                        b.append(" (");
                        b.append(realJid);
                        b.append(')');
                    }
                    b.append(
                            (statusCode==301)? SR.MS_WAS_BANNED : SR.MS_WAS_KICKED );
                    b.append("(");
                    b.append(reason);
                    b.append(")");
                    if (reason.indexOf("talks") > -1) toTalks();
                    testMeOffline();
                    break;
                case 321:
                    b.append(SR.MS_HAS_BEEN_UNAFFILIATED_AND_KICKED_FROM_MEMBERS_ONLY_ROOM);
                    testMeOffline();
                    break;
                case 322:
                    b.append(SR.MS_HAS_BEEN_KICKED_BECAUSE_ROOM_BECAME_MEMBERS_ONLY);
                    testMeOffline();
                    break;
                    
                default:
                    if (realJid!=null) {
                        b.append(" (");
                        b.append(realJid);
                        b.append(')');
                    }
                    b.append(SR.MS_HAS_LEFT_CHANNEL);
					testMeOffline();
            } 
                
        } else {
            if (this.status==Presence.PRESENCE_OFFLINE) {
                String realJid=item.getAttribute("jid");
                if (realJid!=null) {
                    b.append(" (");
                    b.append(realJid);
                    b.append(')');
                    this.realJid=realJid;  //for moderating purposes
                }
                b.append(SR.MS_HAS_JOINED_THE_CHANNEL_AS);
                b.append(role);
                if (!affiliation.equals("none")) {
                    b.append(SR.MS_AND);
                    b.append(affiliation);
//toon
                    //b.append(" with status ");
                    //b.append(pr.getPresenceTxt());
                    
                }
            } else {
                b.append(SR.MS_IS_NOW);
                if ( roleChanged ) b.append(role);
                if (affiliationChanged) {
                    if (roleChanged) b.append(" and ");
                    
                    b.append(affiliation.equals("none")? "unaffiliated" : affiliation);
                }
                if (!roleChanged && !affiliationChanged)
                    b.append(presence.getPresenceTxt());
            }
//toon
        }
        
        setStatus(presenceType);
        return b.toString();
    }
    
    public String getTipString() {
        StringBuffer tip=new StringBuffer();
        int nm=getNewMsgsCount();
        
        if (nm!=0) tip.append(nm);
        
        if (realJid!=null) {
            if (tip.length()!=0)  tip.append(' ');
            tip.append(realJid);
        }
        
        return (tip.length()==0)? null:tip.toString();
    }
    
    void toTalks(){
        ConferenceGroup group=(ConferenceGroup)getGroup();
        if ( group.getSelfContact() == this ) {
            StaticData sd=StaticData.getInstance();
            sd.roster.confJoin("bombusmod_talks@conference.jabber.ru/"+sd.account.getNickName());
        }
    }  
    
    void testMeOffline(){
         ConferenceGroup group=(ConferenceGroup)getGroup();
         if ( group.getSelfContact() == this ) 
            StaticData.getInstance().roster.roomOffline(group);
    }
    

    public void addMessage(Msg m) {
        super.addMessage(m);
        switch (m.messageType) {
            case Msg.MESSAGE_TYPE_IN: break;
            case Msg.MESSAGE_TYPE_OUT: break;
            case Msg.MESSAGE_TYPE_HISTORY: break;
            default: return;
        }
        lastMessageTime=m.dateGmt;
    }
}