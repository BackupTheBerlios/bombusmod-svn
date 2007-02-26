/*
 * RosterItemActions.java
 *
 * Created on 11 Декабрь 2005 г., 19:05
 *
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import Conference.ConferenceForm;
import Conference.ConferenceGroup;
import Conference.affiliation.ConferenceQuickPrivelegeModify;
import Conference.InviteForm;
import Conference.MucContact;
import Conference.QueryConfigForm;
import Conference.affiliation.Affiliations;
import ServiceDiscovery.ServiceDiscovery;
import com.alsutton.jabber.datablocks.IqVersionReply;
import com.alsutton.jabber.datablocks.Presence;
//#if FILE_TRANSFER
import io.file.transfer.TransferSendFile;
//#endif
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.lcdui.Display;
import locale.SR;
import ui.IconTextElement;
import ui.Menu;
import ui.MenuItem;
import ui.YesNoAlert;
import vcard.VCard;
import vcard.vCardForm;

/**
 *
 * @author EvgS
 */
public class RosterItemActions extends Menu implements YesNoAlert.YesNoListener{
    
    public final static int DELETE_CONTACT=4;
    
    Object item;
    
    Roster roster;
    /** Creates a new instance of RosterItemActions */
    public RosterItemActions(Display display, Object item, int action) {
	super(item.toString());
        
        roster=StaticData.getInstance().roster;
        
        if (!roster.isLoggedIn()) return;
        
	this.item=item;
	
        if (item==null) return;
        boolean isContact=( item instanceof Contact );

	if (isContact) {
	    Contact contact=(Contact)item;
	    if (contact.getGroupType()==Groups.TYPE_TRANSP) {
		addItem(SR.MS_LOGON,5);
		addItem(SR.MS_LOGOFF,6);
		addItem(SR.MS_RESOLVE_NICKNAMES, 7);
	    }
	    //if (contact.group==Groups.SELF_INDEX) addItem("Commands",30);
	    
	    addItem(SR.MS_VCARD,1);
	    addItem(SR.MS_CLIENT_INFO,0);
	    addItem(SR.MS_COMMANDS,30);
	    
	    if (contact.getGroupType()!=Groups.TYPE_SELF && contact.getGroupType()!=Groups.TYPE_SEARCH_RESULT && contact.origin<Contact.ORIGIN_GROUPCHAT) {
		if (contact.getGroupType()!=Groups.TYPE_TRANSP)
		    addItem(SR.MS_EDIT,2);
		addItem(SR.MS_SUBSCRIPTION,3);
		addItem(SR.MS_DELETE, DELETE_CONTACT);
                addItem(SR.MS_DIRECT_PRESENCE,45);
	    }
            
	    if (contact.origin==Contact.ORIGIN_GROUPCHAT) return; //TODO: подключить тот же список, что и для ConferenceGroup
            
            if (contact instanceof MucContact) {
                MucContact selfContact= ((ConferenceGroup) contact.getGroup()).getSelfContact();
                MucContact mc=(MucContact) contact;
                
                int myAffiliation=selfContact.affiliationCode;
                if (myAffiliation==MucContact.AFFILIATION_OWNER) myAffiliation++; // allow owner to change owner's affiliation

                
                if (selfContact.roleCode==MucContact.ROLE_MODERATOR) {
                    addItem(SR.MS_KICK,8);
                    
                    if (myAffiliation>=MucContact.AFFILIATION_ADMIN && mc.affiliationCode<myAffiliation)
                        addItem(SR.MS_BAN,9);
                    
                    if (mc.affiliationCode<MucContact.AFFILIATION_ADMIN) 
                        /* 5.1.1 *** A moderator MUST NOT be able to revoke voice privileges from an admin or owner. */ 
                    if (mc.roleCode==MucContact.ROLE_VISITOR) addItem(SR.MS_GRANT_VOICE,31);
                    else addItem(SR.MS_REVOKE_VOICE,32);
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
            } else if (contact.getGroupType()!=Groups.TYPE_TRANSP) {
                // usual contact - invite item check
                boolean onlineConferences=false;
                for (Enumeration c=StaticData.getInstance().roster.getHContacts().elements(); c.hasMoreElements(); ) {
                    try {
                        MucContact mc=(MucContact)c.nextElement();
                        if (mc.origin==Contact.ORIGIN_GROUPCHAT && mc.status==Presence.PRESENCE_ONLINE)
                            onlineConferences=true;
                    } catch (Exception e) {}
                }
                if (onlineConferences) addItem(SR.MS_INVITE,40);
                
            }
            
//#if (FILE_IO && FILE_TRANSFER)
            if (contact.getGroupType()!=Groups.TYPE_TRANSP) 
                if (contact!=StaticData.getInstance().roster.selfContact())
                    addItem("Send file", 50);
//#endif
        } else {
	    Group group=(Group)item;
	    if (group.index==Groups.TYPE_SEARCH_RESULT)
		addItem(SR.MS_DISCARD,21);
	    if (group instanceof ConferenceGroup) {
		MucContact self=((ConferenceGroup)group).getSelfContact();
		if (self.status>=Presence.PRESENCE_OFFLINE) // offline or error
		    addItem(SR.MS_REENTER,23);
		else {
		    addItem(SR.MS_LEAVE_ROOM,22);
                    addItem(SR.MS_DIRECT_PRESENCE,46);
		    if (self.affiliationCode>=MucContact.AFFILIATION_OWNER) {
			addItem(SR.MS_CONFIG_ROOM,10);
                    }
		    if (self.affiliationCode>=MucContact.AFFILIATION_ADMIN) {
			addItem(SR.MS_OWNERS,11);
			addItem(SR.MS_ADMINS,12);
			addItem(SR.MS_MEMBERS,13);
			addItem(SR.MS_BANNED,14);
		    }
		}
	    }
	    //m.addItem(new MenuItem("Cleanup offlines"))
	}
	if (getItemCount()>0) {
            if (action<0) attachDisplay(display);
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
            int index=me.index;
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
                //new DeleteContact(display,c);
                //break;
            case 6: // logoff
            {
                //querysign=true; displayStatus();
                Presence presence = new Presence(
                        Presence.PRESENCE_OFFLINE, -1, "");
                presence.setTo(c.getJid());
                roster.theStream.send( presence );
                break;
            }
            case 5: // logon
            {
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
            
            case 40: //invite
            {
                new InviteForm(c, display);
                return;
            }
            
            case 45: //direct presence
            {
                new StatusSelect(display, c);
                return;
            }
            
//#if (FILE_IO && FILE_TRANSFER)
            case 50: //send file
            {
                new TransferSendFile(display, c.getJid());
                return;
            }
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
                    roster.leaveRoom( 0, g);
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
                    new ConferenceQuickPrivelegeModify(display, mc, ConferenceQuickPrivelegeModify.KICK);
                    return;
                }
                case 9: // ban
                {
                    new ConferenceQuickPrivelegeModify(display, mc, ConferenceQuickPrivelegeModify.OUTCAST);
                    return;
                }
                case 31: //grant voice and revoke moderator
                {
                    new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.PARTICIPANT);
                    return;
                }
                case 32: //revoke voice
                {
                    new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.VISITOR);
                    return;
                }
                
                case 33: //grant moderator
                {
                    new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.MODERATOR);
                    return;
                }
                
        /*case 34: //reserved
        {
         
        }*/
                
                case 35: //grant membership and revoke admin
                {
                    new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.MEMBER);
                    return;
                }
                
                case 36: //revoke membership
                {
                    new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.NONE);
                    return;
                }
                
                case 37: //grant admin and revoke owner
                {
                    new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.ADMIN);
                    return;
                }
                
                case 38: //grant owner
                {
                    new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.OWNER);
                    return;
                }
            }
        }
    }

    public void ActionConfirmed() {
        roster.deleteContact((Contact)item);
        display.setCurrent(roster);
    }
}