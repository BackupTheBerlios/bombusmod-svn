/*
 * RosterItemActions.java
 *
 * Created on 11.12.2005, 19:05
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

import Client.RenameGroup;
import Conference.ConferenceForm;
import Conference.ConferenceGroup;
import Conference.InviteForm;
import Conference.MucContact;
import Conference.QueryConfigForm;
import Conference.affiliation.Affiliations;
import ServiceDiscovery.ServiceDiscovery;
import com.alsutton.jabber.datablocks.IqLast;
import com.alsutton.jabber.datablocks.IqTimeReply;
import com.alsutton.jabber.datablocks.IqVersionReply;
import com.alsutton.jabber.datablocks.Presence;
//#if FILE_TRANSFER
//# import io.file.transfer.TransferSendFile;
//#endif
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.lcdui.Display;
import locale.SR;
import ui.ColorScheme;
import ui.IconTextElement;
import ui.Menu;
import ui.MenuItem;
import ui.YesNoAlert;
import util.ClipBoard;
import vcard.VCard;
import vcard.vCardForm;
import Conference.affiliation.ConferenceQuickPrivelegeModify;

/**
 *
 * @author EvgS
 */
public class RosterItemActions extends Menu implements YesNoAlert.YesNoListener{
    
    public final static int DELETE_CONTACT=4;
    public final static int DELETE_GROUP=1004;
    
    Object item;
	
    Roster roster;
    
    private ClipBoard clipboard;
    private int action;
    
    /** Creates a new instance of RosterItemActions */
    public RosterItemActions(Display display, Object item, int action) {
	super(item.toString());
	this.item=item;
        this.action=action;
	
        roster=StaticData.getInstance().roster;
        
        if (!roster.isLoggedIn()) return;
	
        if (item==null) return;
        boolean isContact=( item instanceof Contact );

	if (isContact) {
	    Contact contact=(Contact)item;
	    if (contact.getGroupType()==Groups.TYPE_TRANSP) {
		addItem(SR.MS_LOGON,5, 0x36);
		addItem(SR.MS_LOGOFF,6, 0x37);
		addItem(SR.MS_RESOLVE_NICKNAMES, 7);
	    }
	    //if (contact.group==Groups.SELF_INDEX) addItem("Commands",30);
	    
	    addItem(SR.MS_VCARD,1, 0x0f16);
            addItem(SR.MS_CLIENT_INFO,0, 0x0f04);
	    addItem(SR.MS_COMMANDS,30, 0x0f24);
	    addItem("Copy JID",892, 0x0f22);
	    addItem("Send current color scheme",912, 0x0f22);
	    addItem("Send buffer",914, 0x0f22);
            
            if (contact.getJid()==contact.getBareJid()) {
                addItem(SR.MS_SEEN,890);    
            } else {
                if (contact.getStatus()<Presence.PRESENCE_OFFLINE)
                    addItem(SR.MS_IDLE,889);
                if (contact.status>4)
                    addItem(SR.MS_ONLINE,890); 
            }
            
                            
            //if (from.indexOf("/")>-1) lastType="idle";
	    
	    if (contact.getGroupType()!=Groups.TYPE_SELF && contact.getGroupType()!=Groups.TYPE_SEARCH_RESULT && contact.origin<Contact.ORIGIN_GROUPCHAT) {
		if (contact.getGroupType()!=Groups.TYPE_TRANSP)
		addItem(SR.MS_EDIT,2, 0x0f13);
		addItem(SR.MS_SUBSCRIPTION,3, 0x47);
		addItem(SR.MS_DELETE, DELETE_CONTACT, 0x12);
                addItem(SR.MS_DIRECT_PRESENCE,45, 0x01);
	    }
            
	    if (contact.origin==Contact.ORIGIN_GROUPCHAT) return; //TODO: п©п╬п╢п╨п╩�?▌�?┤п╦�?┌�?▄ �?┌п╬�?┌ п╤п╣ �?│п©п╦�?│п╬п╨, �?┤�?┌п╬ п╦ п╢п╩�?�? ConferenceGroup
            
            boolean onlineConferences=false;
            for (Enumeration cI=StaticData.getInstance().roster.getHContacts().elements(); cI.hasMoreElements(); ) {
                try {
                    MucContact mcI=(MucContact)cI.nextElement();
                    if (mcI.origin==Contact.ORIGIN_GROUPCHAT && mcI.status==Presence.PRESENCE_ONLINE)
                        onlineConferences=true;
                } catch (Exception e) {}
            }
            
            if (contact instanceof MucContact) {
                MucContact selfContact= ((ConferenceGroup) contact.getGroup()).getSelfContact();
                MucContact mc=(MucContact) contact;
                
                //invite
                if (mc.realJid!=null) {
                    if (onlineConferences) addItem(SR.MS_INVITE,40, 0x0f20);
                }
                //invite
                
                int myAffiliation=selfContact.affiliationCode;
                if (myAffiliation==MucContact.AFFILIATION_OWNER) myAffiliation++; // allow owner to change owner's affiliation

            
                addItem(SR.MS_TIME,891); 
                
                if (selfContact.roleCode==MucContact.ROLE_MODERATOR) {
                    addItem(SR.MS_KICK,8, 0x0f06);
                    
                    if (myAffiliation>=MucContact.AFFILIATION_ADMIN && mc.affiliationCode<myAffiliation)
                        addItem(SR.MS_BAN,9, 0x0f06);
                    
                    if (mc.affiliationCode<MucContact.AFFILIATION_ADMIN) 
                        /* 5.1.1 *** A moderator MUST NOT be able to revoke voice privileges from an admin or owner. */ 
                    if (mc.roleCode==MucContact.ROLE_VISITOR) addItem(SR.MS_GRANT_VOICE,31);
                    else addItem(SR.MS_REVOKE_VOICE, 32);
                }
                
                if (myAffiliation>=MucContact.AFFILIATION_ADMIN) {
                    // admin use cases
                    
                    //roles
                    if (mc.affiliationCode<MucContact.AFFILIATION_ADMIN) 
                        /* 5.2.1 ** An admin or owner MUST NOT be able to revoke moderation privileges from another admin or owner. */ 
                    if (mc.roleCode==MucContact.ROLE_MODERATOR) addItem(SR.MS_REVOKE_MODERATOR,31);
                    else addItem(SR.MS_GRANT_MODERATOR,33);
                    
                    //affiliations
                    if (mc.affiliationCode<myAffiliation) {
                        if (mc.affiliationCode!=MucContact.AFFILIATION_NONE) addItem(SR.MS_UNAFFILIATE,36);
                        /* 5.2.2 */
                        if (mc.affiliationCode!=MucContact.AFFILIATION_MEMBER) addItem(SR.MS_GRANT_MEMBERSHIP,35);
                    }
                    
                    
//--toon               //m.addItem(new MenuItem("Set Affiliation",15));
                }
                if (myAffiliation>=MucContact.AFFILIATION_OWNER) {
                    // owner use cases
                    //if (mc.affiliationCode<=selfContact.affiliationCode) /* 5.2.2 */
                    if (mc.affiliationCode!=MucContact.AFFILIATION_ADMIN) addItem(SR.MS_GRANT_ADMIN,37);
                    //else addItem(SR.MS_REVOKE_ADMIN,35);
                    
                    if (mc.affiliationCode!=MucContact.AFFILIATION_OWNER) addItem(SR.MS_GRANT_OWNERSHIP,38);
                    //else addItem(SR.MS_REVOKE_OWNERSHIP,37);
                }
                if (mc.realJid!=null && mc.getStatus()<Presence.PRESENCE_OFFLINE) {
                    
                }
            } else if (contact.getGroupType()!=Groups.TYPE_TRANSP) {
                // usual contact - invite item check
                 if (onlineConferences) addItem(SR.MS_INVITE,40);
            }
//#if (FILE_IO && FILE_TRANSFER)
//#             if (contact.getGroupType()!=Groups.TYPE_TRANSP) 
//#                 if (contact!=StaticData.getInstance().roster.selfContact())
//#                     addItem(SR.MS_SEND_FILE, 50, 0x0f34);
//#             
//#endif
        } else {
	    Group group=(Group)item;
	    if (group.index==Groups.TYPE_SEARCH_RESULT)
		addItem(SR.MS_DISCARD,21);
	    if (group instanceof ConferenceGroup) {
		MucContact self=((ConferenceGroup)group).getSelfContact();
		if (self.status>=Presence.PRESENCE_OFFLINE) // offline or error
		    addItem(SR.MS_REENTER,23, 0x0f21);
		else {
		    addItem(SR.MS_LEAVE_ROOM,22, 0x0f22);
                    addItem(SR.MS_DIRECT_PRESENCE,46);
		    if (self.affiliationCode>=MucContact.AFFILIATION_OWNER) {
			addItem(SR.MS_CONFIG_ROOM,10, 0x0f03);
                    }
		    if (self.affiliationCode>=MucContact.AFFILIATION_ADMIN) {
			addItem(SR.MS_OWNERS,11);
			addItem(SR.MS_ADMINS,12);
			addItem(SR.MS_MEMBERS,13);
			addItem(SR.MS_BANNED,14);
 		    }
 		}
	    } else {
                if (    group.index!=Groups.TYPE_IGNORE
                        && group.index!=Groups.TYPE_NOT_IN_LIST
                        && group.index!=Groups.TYPE_SEARCH_RESULT
                        && group.index!=Groups.TYPE_SELF
                        && group.index!=Groups.TYPE_TRANSP)
                {
                    addItem(SR.MS_RENAME,1001);
                    addItem(SR.MS_DELETE, DELETE_GROUP);
                }
            }
 	}
	if (getItemCount()>0) {
            if (action<0) 
                attachDisplay(display);
            else try {
                this.display=display; // to invoke dialog Y/N
                doAction(action);
            } catch (Exception e) { e.printStackTrace(); }
        }
     }
     
     public void eventOk(){
         try {
             //final Roster roster=StaticData.getInstance().roster;
             MenuItem me=(MenuItem) getFocusedObject();
            destroyView();
            if (me==null) return;
            int index=action=me.index;
            doAction(index);
            //destroyView();
        } catch (Exception e) { e.printStackTrace();  }
    }

    private void doAction(final int index) {
        boolean isContact=( item instanceof Contact );
        Contact c = null;
        Group g = null;
        if (isContact) c=(Contact)item; else g=(Group) item;
        
        String to=null;
        if (isContact) to=(index<3)? c.getJid() : c.getBareJid();

            switch (index) {
                case 0: // info
                    roster.setQuerySign(true);
                    roster.theStream.send(new IqVersionReply(to));
                    break;
                case 1: // vCard
                    if (c.vcard!=null) {
                        new vCardForm(display, c.vcard, c.getGroupType()==Groups.TYPE_SELF);
                        return;
                    }
                    VCard.request(c.getBareJid(), c.getJid());
                    break;
                    
                case 2:
                    (new ContactEdit(display, c )).parentView=roster;
                    return; //break;
                    
                case 3: //subscription
                    new SubscriptionEdit(display, c);
                    return; //break;
                case DELETE_CONTACT:
                    new YesNoAlert(display, SR.MS_DELETE_ASK, c.getNickJid(), this);
                    return;
                case 6: // logoff
                {
                    roster.blockNotify(-111,10000); //block sounds to 10 sec
                    //querysign=true; displayStatus();
                    Presence presence = new Presence(
                            Presence.PRESENCE_OFFLINE, -1, "");
                    presence.setTo(c.getJid());
                    roster.theStream.send( presence );
                    break;
                }
                case 5: // logon
                {
                    roster.blockNotify(-111,10000); //block sounds to 10 sec
                    //querysign=true; displayStatus();
                    Presence presence = new Presence(roster.myStatus, 0, "");
                    presence.setTo(c.getJid());
                    roster.theStream.send( presence );
                    break;
                }
                case 7: // Nick resolver
                {
                    roster.resolveNicknames(c.transport);
                    break;
                }
                
                case 21:
                {
                    roster.cleanupSearch();
                    break;
                }
                case 30:
                {
                    new ServiceDiscovery(display, c.getJid(), "http://jabber.org/protocol/commands");
                    return;
                }
                case 889: //idle
                {
                    roster.setQuerySign(true);
                    roster.theStream.send(new IqLast(c.getJid()));
                    break;
                }
                case 890: //seen & online
                {
                    roster.setQuerySign(true);
                    roster.theStream.send(new IqLast(c.getBareJid()));
                    break;
                }
                
                case 891: //time
                {
                    roster.setQuerySign(true);
                    roster.theStream.send(new IqTimeReply(c.getBareJid()));
                    break;
                }
                
                case 892: //Copy JID
                {
                    if (!(c instanceof MucContact)) {
                        //System.out.println("1 c "+c.bareJid);
                        try {
                            if (c.bareJid!=null)
                                clipboard.setClipBoard(c.bareJid);
                        } catch (Exception e) {/*no messages*/}
                    }
                    break;
                }
                
                case 912: //send color scheme
                {
                    String from=StaticData.getInstance().account.toString();
                    String body=ColorScheme.getSkin();
                    String subj="";

                    Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,body);
            
                    try {
                        roster.sendMessage(c, body, subj, 0);
                        c.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,"scheme sended"));
                    } catch (Exception e) {
                        c.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,"scheme NOT sended"));
                        e.printStackTrace();
                    }
                    break;
                }
                
                case 914: //send message from buffer
                {
                    String from=StaticData.getInstance().account.toString();
                    String body=clipboard.getClipBoard();
                    String subj="";

                    Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,body);
            
                    try {
                        roster.sendMessage(c, body, subj, 0);
                        c.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,"message sended from clipboard("+body.length()+"chars)"));
                    } catch (Exception e) {
                        c.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,"message NOT sended"));
                        e.printStackTrace();
                    }
                    break;
                }
                
                case 40: //invite
                {
                    //new InviteForm(c, display);
                    if (c.jid!=null) {
                        new InviteForm(c, display);                        
                    } else {
                        MucContact mcJ=(MucContact) c;

                        if (mcJ.realJid!=null) {
                            boolean onlineConferences=false;
                            for (Enumeration cJ=StaticData.getInstance().roster.getHContacts().elements(); cJ.hasMoreElements(); ) {
                                try {
                                    MucContact mcN=(MucContact)cJ.nextElement();
                                    if (mcN.origin==Contact.ORIGIN_GROUPCHAT && mcN.status==Presence.PRESENCE_ONLINE)
                                        onlineConferences=true;
                                } catch (Exception e) {}
                            }
                            if (onlineConferences) new InviteForm(mcJ, display);
                        }
                    }
                    return;
                }

                case 45: //direct presence
                {
                    new StatusSelect(display, c);
                    return;
                }
                
//#if (FILE_IO && FILE_TRANSFER)
//#                 case 50: //send file
//#                 {
//#                     new TransferSendFile(display, c.getJid());
//#                     return;
//#                 }   
//#endif
            }
            
            if (c instanceof MucContact || g instanceof ConferenceGroup) {
                MucContact mc=(MucContact) c;
                
                switch (index) { // muc contact actions
                    case 10: // room config
                    {
                        String roomJid=((ConferenceGroup)g).getConference().getJid();
                        new QueryConfigForm(display, roomJid);
                        break;
                    }
                    case 11: // owners
                    case 12: // admins
                    case 13: // members
                        
                    case 14: // outcasts
                    {
                        String roomJid=((ConferenceGroup)g).getConference().getJid();
                        new Affiliations(display, roomJid, index-10);
                        return;
                    }
                    /*case 15: // affiliation
                    {
                        String roomJid=conferenceRoomContact(g.index).getJid();
                        new AffiliationModify(display, roomJid, c.realJid, affiliation)(display, roomJid, index-10);
                    }
                     */
                    case 22:
                    {
                        roster.leaveRoom( g );
                        break;
                    }
                    case 23:
                    {
                        roster.reEnterRoom( g );
                        return; //break;
                    }
                    case 46: //conference presence
                    {
                        new StatusSelect(display, ((ConferenceGroup)g).getConference());
                        return;
                    }
                    
                     case 8: // kick
                     {
                        ConferenceGroup mucGrp=(ConferenceGroup)c.getGroup();
                        String myNick=mucGrp.getSelfContact().getName();
                        new ConferenceQuickPrivelegeModify(display, mc, ConferenceQuickPrivelegeModify.KICK,myNick);
                        return;
                     }
                     case 9: // ban
                     {
                        ConferenceGroup mucGrp=(ConferenceGroup)c.getGroup();
                        String myNick=mucGrp.getSelfContact().getName();
                        new ConferenceQuickPrivelegeModify(display, mc, ConferenceQuickPrivelegeModify.OUTCAST,myNick);
                        return;
                     }
                     case 31: //grant voice and revoke moderator
                     {
                        new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.PARTICIPANT,null);
                        return;
                     }
                     case 32: //revoke voice
                     {
                        new ConferenceQuickPrivelegeModify(display, mc, ConferenceQuickPrivelegeModify.VISITOR,null);
                        return;
                     }
                     
                     case 33: //grant moderator
                     {
                        new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.MODERATOR,null);
                        return;
                     }
                     
             /*case 34: //reserved
            {
             
            }*/
                    
                case 35: //grant membership and revoke admin
                 {
                    new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.MEMBER,null);
                     return;
                 }
                 
                case 36: //revoke membership
                 {
                    new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.NONE,null);
                     return;
                 }
                 
                case 37: //grant admin and revoke owner
                 {
                    new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.ADMIN,null);
                     return;
                 }
                 
                case 38: //grant owner
                 {
                    new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.OWNER,null);
                     return;
                 }
                
                case 892: //Copy JID
                {
                    MucContact mcJ=(MucContact) c;
                    //System.out.println("2 muc "+mcJ.realJid);
                    try {
                        if (mcJ.realJid!=null)
                            clipboard.setClipBoard(mcJ.realJid);
                    } catch (Exception e) {}
                    break;
                 }
             }
        } else {
            Group sg=(Group)item;

            if (       sg.index!=Groups.TYPE_IGNORE 
                    && sg.index!=Groups.TYPE_NOT_IN_LIST
                    && sg.index!=Groups.TYPE_SEARCH_RESULT
                    && sg.index!=Groups.TYPE_SELF
                    && sg.index!=Groups.TYPE_TRANSP)
            {
                switch (index) {
                    case 1001: //rename
                    {
                        new RenameGroup(display, sg);
                        return;
                    }
                    case DELETE_GROUP: //delete
                    {
                        new YesNoAlert(display, SR.MS_DELETE_ASK, sg.getName(), this);
                        return;
                    }    
                }
            }
         }
     }
    
    public void ActionConfirmed() {
        switch (action) {
            case DELETE_CONTACT:
            {
                roster.deleteContact((Contact)item);
                break;
            }
            case DELETE_GROUP:
            {
                roster.deleteGroup((Group)item);
                break;
            }
        }
        display.setCurrent(roster);
    }
}
