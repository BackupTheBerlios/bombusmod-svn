/*
 * Roster.java
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

//TODO: упростить обработку исключений для theStream.send

package Client;

import Conference.BookmarkQuery;
import Conference.Bookmarks;
import Conference.ConferenceGroup;
import Conference.MucContact;
import Conference.QueryConfigForm;
import Conference.affiliation.Affiliations;
import Info.Phone;
import archive.ArchiveList;
import images.RosterIcons;
import locale.SR;
import login.LoginListener;
import login.NonSASLAuth;
import login.SASLAuth;
import midlet.Bombus;
import ui.AlertBox;
import ui.MainBar;
import vcard.VCard;
import vcard.vCardForm;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
//import javax.microedition.media.*;
//import Client.Contact.*;
import ui.*;
import ServiceDiscovery.ServiceDiscovery;
import Conference.ConferenceForm;
import PrivacyLists.PrivacySelect;
import Client.Config;

import Info.Version;
//#if FILE_TRANSFER
//# import io.file.transfer.TransferDispatcher;
//#endif
//import Client.msg.*;

/**
 *
 * @author Eugene Stahov
 */
//public class Roster implements JabberListener, VList.Callback{
public class Roster
        extends VirtualList
        implements
        JabberListener,
        CommandListener,
        Runnable,
        LoginListener,
        YesNoAlert.YesNoListener
        //ContactEdit.StoreContact
        //Thread
{
    
    
    private Jid myJid;
    
    /**
     * The stream representing the connection to ther server
     */
    public JabberStream theStream ;
        
    int messageCount;
    
    private Object messageIcon;
    public Object transferIcon;
   
    boolean reconnect=false;
    boolean querysign=false;
    
    boolean storepresence=true;
    
    public int myStatus=Presence.PRESENCE_OFFLINE;
    
    private Vector hContacts;
    private Vector vContacts;
    
    private Vector paintVContacts;  // для атомных операций.
    
    public Groups groups;
    
    public Vector bookmarks;
    
    public static boolean isAway=false;
    public static int oldStatus=0;
    
    public static boolean forme=false;
    public static boolean conference=false;


    private Command cmdActions=new Command(SR.MS_ITEM_ACTIONS, Command.SCREEN, 1);
    private Command cmdStatus=new Command(SR.MS_STATUS_MENU, Command.SCREEN, 2);
    private Command cmdActiveContacts;//=new Command(SR.MS_ACTIVE_CONTACTS, Command.SCREEN, 3);
    private Command cmdAlert=new Command(SR.MS_ALERT_PROFILE_CMD, Command.SCREEN, 8);
    private Command cmdConference=new Command(SR.MS_CONFERENCE, Command.SCREEN, 10);
    private Command cmdArchive=new Command(SR.MS_ARCHIVE, Command.SCREEN, 10);
    private Command cmdAdd=new Command(SR.MS_ADD_CONTACT, Command.SCREEN, 12);
    private Command cmdTools=new Command(SR.MS_TOOLS, Command.SCREEN, 14);    
    private Command cmdAccount=new Command(SR.MS_ACCOUNT_, Command.SCREEN, 15);
    private Command cmdInfo=new Command(SR.MS_ABOUT, Command.SCREEN, 80);
    private Command cmdTurnOnLight=new Command("TurnOn Light", Command.SCREEN, 16);
    private Command cmdTurnOffLight=new Command("TurnOff Light", Command.SCREEN, 16);
    private Command cmdPatchLight=new Command("Patch Control", Command.SCREEN, 16);
    private Command cmdMinimize=new Command(SR.MS_APP_MINIMIZE, Command.SCREEN, 90);
    private Command cmdQuit=new Command(SR.MS_APP_QUIT, Command.SCREEN, 99);
    
    private Config cf;
    private StaticData sd=StaticData.getInstance();

//#if (MOTOROLA_BACKLIGHT)
    private int blState=Integer.MAX_VALUE;

//#endif

//#if SASL
    private String token;

//#endif
    
    public long lastMessageTime=Time.localTime();
    public static int keyTimer=0;
    //public JabberBlockListener discoveryListener;
    
    public int autoAwayTime;

    private int pr;

    private String ms;

    public boolean setAutoStatus;

    private String myMessage;    

    //private TimerTaskAutoAway AutoAway;
    
    public static int lightType=0;
	
    private final static int maxReconnect=5;
    public int reconnectCount;
    
    public static String startTime=Time.dispLocalTime();

    public static boolean keyLockState=false;
	
    private static long notifyReadyTime=System.currentTimeMillis();
    private static int blockNotifyEvent=-111;
    
    public Contact lastAppearedContact=null;
    
    /**
     * Creates a new instance of Roster
     * Sets up the stream to the server and adds this class as a listener
     */
    public Roster(Display display /*, boolean selAccount*/) {
        super();

	setProgress(24);
                
        this.display=display;
        
        cf=Config.getInstance();
        
        autoAwayTime=cf.autoAwayTime*60;
        setAutoStatus=cf.setAutoStatus;
		
	//canback=false; // We can't go back using keyBack
        
        //msgNotify=new EventNotify(display, Profile.getProfile(0) );
        MainBar mainbar=new MainBar(4, null, null);
        setMainBarItem(mainbar);
        mainbar.addRAlign();
        mainbar.addElement(null);
        mainbar.addElement(null);
        mainbar.addElement(null); //ft
        //displayStatus();

        hContacts=new Vector();
        groups=new Groups();
        
        vContacts=new Vector(); // just for displaying
        
        if (ph.PhoneManufacturer()==ph.SIEMENS || ph.PhoneManufacturer()==ph.SIEMENS2) {
            switch (cf.lightType) {
                case 0: { //off
                    lightType=0;
                    setLight(false);
                    break;
                }
                case 1: { //on 
                    lightType=1;
                    sd.roster.setLight(true);
                    break;
                }
                case 2: { //auto
                    lightType=2;
                    break;
                }
            }
        }
        
        if (!VirtualList.digitMemMonitor) {
                int activeType=Command.SCREEN;
                if (ph.PhoneManufacturer()==ph.NOKIA) activeType=Command.BACK;
                if (ph.PhoneManufacturer()==ph.INTENT) activeType=Command.BACK;
                if (ph.PhoneManufacturer()==ph.J2ME) activeType=Command.BACK;

                cmdActiveContacts=new Command(SR.MS_ACTIVE_CONTACTS, activeType, 3);

                addCommand(cmdStatus);
                addCommand(cmdActions);
                addCommand(cmdActiveContacts);
                addCommand(cmdAlert);
                addCommand(cmdAdd);
                addCommand(cmdConference);


                if (ph.PhoneManufacturer()==ph.SIEMENS || ph.PhoneManufacturer()==ph.SIEMENS2) {
                    switch (cf.lightType) {
                        case 0: { //off
                            addCommand(cmdTurnOnLight); 
                            break;
                        }
                        case 1: { //on
                            addCommand(cmdTurnOffLight);  
                            break;
                        }
                        case 2: { //auto
                            addCommand(cmdPatchLight);
                            break;
                        }
                    }
                }

                addCommand(cmdTools);
                addCommand(cmdArchive);
                addCommand(cmdInfo);
                addCommand(cmdAccount);

                if (ph.PhoneManufacturer()==ph.NOKIA_9XXX) {
                } else {
                        addCommand(cmdQuit);
                }


                addOptionCommands();
                setCommandListener(this);
        }

	updateMainBar();

        SplashScreen.getInstance().setExit(display, this);
        
        //parentView=null; - already have
    }
    
    void addOptionCommands(){
        if (cf.allowMinimize) addCommand(cmdMinimize);
        //Config cf=StaticData.getInstance().config;
        //        if (cf.showOfflineContacts) {
        //            addCommand(cmdHideOfflines);
        //            removeCommand(cmdShowOfflines);
        //        } else {
        //            addCommand(cmdShowOfflines);
        //            removeCommand(cmdHideOfflines);
        //        }
    }
    public void setProgress(String pgs,int percent){
        SplashScreen.getInstance().setProgress(pgs, percent);
        setRosterMainBar(pgs);
        redraw();
    }
    public void setProgress(int percent){
        SplashScreen.getInstance().setProgress(percent);
        //redraw();
    }
    
    private void setRosterMainBar(String s){
        getMainBarItem().setElementAt(s, 3);
    }
    
    private int rscaler;
    private int rpercent;
    
    public void rosterItemNotify(){
        rscaler++;
        if (rscaler<4) return;
        rscaler=0;
        rpercent++;
        if (rpercent==100) rpercent=60;
        SplashScreen.getInstance().setProgress(rpercent);
    }
    
    // establishing connection process
    public void run(){
        //Iq.setXmlLang(SR.MS_XMLLANG);
        setQuerySign(true);
        setProgress(25);
	if (!reconnect) {
	    resetRoster();
	};
        setProgress(26);
        
        //logoff();
        try {
            Account a=sd.account;
//#if SASL_XGOOGLETOKEN
            if (a.useGoogleToken()) {
                setProgress(SR.MS_TOKEN, 30);
                token=new SASLAuth(a, null, this, null).responseXGoogleToken();
                if (token==null) throw new Exception("Can't get Google token");
            }
//#endif
            setProgress(SR.MS_CONNECT_TO+a.getServer(), 30);
            SR.loaded();
            theStream= a.openJabberStream();
            setProgress(SR.MS_OPENING_STREAM, 40);
            theStream.setJabberListener( this );
        } catch( Exception e ) {
            setProgress(SR.MS_FAILED, 0);
            reconnect=false;
            myStatus=Presence.PRESENCE_OFFLINE;
            //e.printStackTrace();
            setQuerySign(false);
            redraw();
            askReconnect(e);
        }
        //l.setCallback(this);
    }

    public void resetRoster() {
	synchronized (hContacts) {
	    hContacts=new Vector();
	    groups=new Groups();
	    vContacts=new Vector(); // just for displaying
	    bookmarks=null;
	}
	setMyJid(new Jid(sd.account.getJid()));
	updateContact(sd.account.getNick(), myJid.getBareJid(), Groups.SELF_GROUP, "self", false);
	
	System.gc();
    }
    
    public void errorLog(String s){
        if (s==null) return;
        if (s.length()==0) return;

        //new AlertBox(SR.MS_ERROR_, s, null, display, null);
        
        /*Alert error=new Alert(SR.MS_ERROR_, s, null, null);
        error.setTimeout(30000);
         error.addCommand(new Command(SR.MS_OK, Command.BACK, 1));
         display.setCurrent(error, display.getCurrent());
         */
        
        Msg m=new Msg(Msg.MESSAGE_TYPE_OUT, "local", "Error", s);
        messageStore(selfContact(), m);
    }
    
    public void beginPaint() {
        paintVContacts=vContacts;
    }
    
    public VirtualElement getItemRef(int Index){
        return (VirtualElement) paintVContacts.elementAt(Index);
    }
    
    public int getItemCount(){
        return paintVContacts.size();
    };
    
    public void setEventIcon(Object icon){
        transferIcon=icon;
        getMainBarItem().setElementAt(icon, 7);
        redraw();
    }
    
    public Object getEventIcon() {
        if (transferIcon!=null) return transferIcon;
        return messageIcon;
    }
  
    private void updateMainBar(){
        int s=querysign?RosterIcons.ICON_PROGRESS_INDEX:myStatus;
        int profile=cf.profile;//StaticData.getInstance().config.profile;
        Object en=(profile>1)? new Integer(profile+RosterIcons.ICON_PROFILE_INDEX):null;
        MainBar mainbar=(MainBar) getMainBarItem();
        mainbar.setElementAt(new Integer(s), 2);
        mainbar.setElementAt(en, 5);
        if (messageCount==0) {
            messageIcon=null;
            mainbar.setElementAt(null,1);
        } else {
            messageIcon=new Integer(RosterIcons.ICON_MESSAGE_INDEX);
            mainbar.setElementAt(" "+messageCount+" ",1);
        }
        mainbar.setElementAt(messageIcon, 0);
    }
    
    boolean countNewMsgs() {
        int m=0;
        synchronized (hContacts) {
            for (Enumeration e=hContacts.elements();e.hasMoreElements();){
                Contact c=(Contact)e.nextElement();
                m+=c.getNewMsgsCount();
            }
        }
        messageCount=m;
//#if USE_LED_PATTERN
//--                int pattern=cf.m55LedPattern;
//--                if (pattern>0) EventNotify.leds(pattern-1, m>0);
//#endif
        updateMainBar();
        return (m>0);
    }
    
    public void cleanupSearch(){
        int index=0;
        synchronized (hContacts) {
            while (index<hContacts.size()) {
                if ( ((Contact) hContacts.elementAt(index)).getGroupType()==Groups.TYPE_SEARCH_RESULT )
                    hContacts.removeElementAt(index);
                else index++;
            }
        }
        reEnumRoster();
    }
    
    public void cleanupGroup(){
        Group g=(Group)getFocusedObject();
        if (g==null) return;
        if (!g.collapsed) return;
        
        if (g instanceof ConferenceGroup) {
            ConferenceGroup cg= (ConferenceGroup) g;
            if (cg.getSelfContact().status>=Presence.PRESENCE_OFFLINE 
                && cg.getConference().getStatus()==Presence.PRESENCE_ONLINE)
                return;
        }
        //int gi=g.index;

        int index=0;

        int onlineContacts=0;
        
        synchronized (hContacts) {
            while (index<hContacts.size()) {
                Contact contact=(Contact)hContacts.elementAt(index);
                if (contact.inGroup(g)) {
                    if ( contact.origin>Contact.ORIGIN_ROSTERRES
                         && contact.status>=Presence.PRESENCE_OFFLINE
                         && contact.getNewMsgsCount()==0 )
                        hContacts.removeElementAt(index);
                    else { 
                        index++;
                        onlineContacts++;
                    } 
                }
                else index++; 
            }
            if (onlineContacts==0) {
                if (g.index>Groups.TYPE_COMMON) groups.removeGroup(g);
            }
        }
    }
    
    ReEnumerator reEnumerator=null;
    
    public void reEnumRoster(){
        if (reEnumerator==null) reEnumerator=new ReEnumerator();
        reEnumerator.queueEnum();
    }
    
    
    public Vector getHContacts() {return hContacts;}
    
    public void updateContact(String nick, String jid, String grpName, String subscr, boolean ask) {
        // called only on roster read
        int status=Presence.PRESENCE_OFFLINE;
        if (subscr.equals("none")) status=Presence.PRESENCE_UNKNOWN;
        if (ask) status=Presence.PRESENCE_ASK;
        //if (subscr.equals("remove")) status=Presence.PRESENCE_TRASH;
        if (subscr.equals("remove")) status=-1;
        
        Jid J=new Jid(jid);
        Contact c=findContact(J,false); // search by bare jid
        if (c==null) {
            c=new Contact(nick, jid, Presence.PRESENCE_OFFLINE, null);
            addContact(c);
        }
        for (Enumeration e=hContacts.elements();e.hasMoreElements();) {
            c=(Contact)e.nextElement();
            if (c.jid.equals(J,false)) {
                Group group= (c.jid.isTransport())? 
                    groups.getGroup(Groups.TYPE_TRANSP) :
                    groups.getGroup(grpName);
                if (group==null) {
                    group=groups.addGroup(grpName, true);
                }
                c.nick=nick;
                c.setGroup(group);
                c.subscr=subscr;
                c.offline_type=status;
                c.ask_subscribe=ask;
                
                Group g=c.getGroup();
                g.collapsed=true; 
                //if (status==Presence.PRESENCE_TRASH) c.status=status;
                //if (status!=Presence.PRESENCE_OFFLINE) c.status=status;
                c.setSortKey((nick==null)? jid:nick);
            }
        }
        if (status<0) removeTrash();
    }
    
    private final void removeTrash(){
        int index=0;
        synchronized (hContacts) {
            while (index<hContacts.size()) {
                Contact c=(Contact)hContacts.elementAt(index);
                if (c.offline_type<0) {
                    hContacts.removeElementAt(index);
                } else index++;
            }
            countNewMsgs();
        }
    }

    private MucContact findMucContact(Jid jid) {
        Contact contact=findContact(jid, true);
        try {
            return (MucContact) contact;
        } catch (Exception e) {
            // drop buggy bookmark in roster
            hContacts.removeElement(contact);
            return null;
        }
    }
    
    public final ConferenceGroup initMuc(String from, String joinPassword){
        setKeyTimer(0);
        // muc message
        int ri=from.indexOf('@');
        int rp=from.indexOf('/');
        String room=from.substring(0,ri);
        String roomJid=from.substring(0,rp).toLowerCase();
        
        
        ConferenceGroup grp=(ConferenceGroup)groups.getGroup(roomJid);
        
        
        // creating room
        
        if (grp==null) // we hasn't joined this room yet
            groups.addGroup(grp=new ConferenceGroup(roomJid, room) );
        grp.password=joinPassword;
        
        MucContact c=findMucContact( new Jid(roomJid) );
        
        if (c==null) {
            c=new MucContact(room, roomJid);
            addContact(c);
        }
		        
        // change nick if already in room
        if (c.getStatus()==Presence.PRESENCE_ONLINE) return grp;

        c.setStatus(Presence.PRESENCE_ONLINE);
        c.transport=RosterIcons.ICON_GROUPCHAT_INDEX; //FIXME: убрать хардкод
        c.bareJid=from;
        c.origin=Contact.ORIGIN_GROUPCHAT;
        c.commonPresence=true;
        //c.priority=99;
        //c.key1=0;
        grp.conferenceJoinTime=Time.localTime();
        grp.setConference(c);
        c.setGroup(grp);
        
        String nick=from.substring(rp+1);
		
        
        // old self-contact
        c=grp.getSelfContact();
        
        // check for existing entry - it may be our old self-contact
        // or another contact whose nick we pretend
        MucContact foundInRoom = findMucContact( new Jid(from) );
        if (foundInRoom!=null) {
            //choose found contact instead of old self-contact
            c=foundInRoom;
        }
 
        // if exists (and online - rudimentary check due to line 500)
        // rename contact
        if (c!=null) if (c.status>=Presence.PRESENCE_OFFLINE) {
            c.nick=nick;
            c.jid.setJid(from);
            c.bareJid=from;
        }
        
        // create self-contact if no any candidates found
        if (c==null) {
            c=new MucContact(nick, from);
            addContact(c);
        }
        
        
        grp.setSelfContact(c);
        c.setGroup(grp);
        c.origin=Contact.ORIGIN_GC_MYSELF;
        
        sort(hContacts);
        return grp;
    }
    
    public final MucContact mucContact(String from){
        // muc message
        int ri=from.indexOf('@');
        int rp=from.indexOf('/');
        String room=from.substring(0,ri);
        String roomJid=from.substring(0,rp).toLowerCase();
        

        ConferenceGroup grp=(ConferenceGroup)groups.getGroup(roomJid);
	

        
        if (grp==null) return null; // we are not joined this room
        
        MucContact c=findMucContact( new Jid(from) );
        
        if (c==null) {
            c=new MucContact(from.substring(rp+1), from);
            addContact(c);
            c.origin=Contact.ORIGIN_GC_MEMBER;
        }
        
        c.setGroup(grp);
        sort(hContacts);
        return c;
    }
    
    public final Contact getContact(final String jid, boolean createInNIL) {
        
        Jid J=new Jid(jid);

        // проверим наличие по полной строке
        Contact c=findContact(J, true); 
        if (c!=null) 
            return c;

        // проверим наличие без ресурсов
        c=findContact(J, false);
        if (c==null) {
            if (!createInNIL) return null;
            c=new Contact(null, jid, Presence.PRESENCE_OFFLINE, "not-in-list");
	    c.bareJid=J.getBareJid();
            c.origin=Contact.ORIGIN_PRESENCE;
            c.setGroup(groups.getGroup(Groups.TYPE_NOT_IN_LIST));
            addContact(c);
        } else {
            // здесь jid с новым ресурсом
            if (c.origin==Contact.ORIGIN_ROSTER) {
                c.origin=Contact.ORIGIN_ROSTERRES;
                c.setStatus(Presence.PRESENCE_OFFLINE);
                c.jid=J;
                //System.out.println("add resource");
            } else {
                c=c.clone(J, Presence.PRESENCE_OFFLINE);
                addContact(c);
                //System.out.println("cloned");
            }
        }
        sort(hContacts);
        return c;
    }
    
    public void addContact(Contact c) {
        synchronized (hContacts) { hContacts.addElement(c); }
    }
    

    public final Contact findContact(final Jid j, final boolean compareResources) {
        synchronized (hContacts) {
            for (Enumeration e=hContacts.elements();e.hasMoreElements();){
                Contact c=(Contact)e.nextElement();
                if (c.jid.equals(j,compareResources)) return c;
            }
        }
        return null;
    }
    
    /**
     * Method to inform the server we are now online
     */
    
    public void sendPresence(int status) {
        setKeyTimer(0);
        myStatus=status;
        setQuerySign(false);
        if (myStatus==Presence.PRESENCE_OFFLINE) {
            synchronized(hContacts) {
                for (Enumeration e=hContacts.elements(); e.hasMoreElements();){
                    Contact c=(Contact)e.nextElement();
                        c.setStatus(Presence.PRESENCE_OFFLINE); // keep error & unknown
                }
            }
        } else {
            lastOnlineStatus=myStatus;
        }
        
        // reconnect if disconnected        
        if (myStatus!=Presence.PRESENCE_OFFLINE && theStream==null ) {
            reconnect=(hContacts.size()>1);
            redraw();

            new Thread(this).start();
            return;
        }
        
        blockNotify(-111,13000);
        
        // send presence
        ExtendedStatus es= StatusList.getInstance().getStatus(myStatus);
        Presence presence = new Presence(myStatus, es.getPriority(), es.getMessage());
        if (isLoggedIn()) {
            if (!StaticData.getInstance().account.isMucOnly() )
				theStream.send( presence );
            
            sendConferencePresence();

            // disconnect
            if (status==Presence.PRESENCE_OFFLINE) {
                try {
                    theStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                theStream=null;
                System.gc();
            }
        }
        Contact c=selfContact();
        c.setStatus(myStatus);
        sort(hContacts);
        
        reEnumRoster();
    }

    public void sendPresence(int status, String message) {
        setKeyTimer(0);
        myStatus=status;
        myMessage=message;
        setQuerySign(false);
        if (myStatus==Presence.PRESENCE_OFFLINE) {
            synchronized(hContacts) {
                for (Enumeration e=hContacts.elements(); e.hasMoreElements();){
                    Contact c=(Contact)e.nextElement();
                        c.status=Presence.PRESENCE_OFFLINE; // keep error & unknown
                        isAway=false;
                }
            }
        }
        
        // reconnect if disconnected        
        if (myStatus!=Presence.PRESENCE_OFFLINE && theStream==null ) {
            reconnect=(hContacts.size()>1);
            redraw();
            
            new Thread(this).start();
            return;
        }

        blockNotify(-111,13000);
        
        ms=myMessage;

        ExtendedStatus es= StatusList.getInstance().getStatus(oldStatus);
        pr=es.getPriority();
        
        
        Presence presence = new Presence(myStatus, pr, ms);

        if (isLoggedIn()) {
            if (!StaticData.getInstance().account.isMucOnly() )
		theStream.send( presence );
            
            //sendConferencePresence();
            sendConferencePresence(myMessage);

            // disconnect
            if (status==Presence.PRESENCE_OFFLINE) {
                try {
                    theStream.close();
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                theStream=null;
                isAway=false;
                System.gc();
            }
        }
        Contact c=selfContact();
        c.status=myStatus;
        sort(hContacts);
        
        reEnumRoster();
    }

    public void sendDirectPresence(int status, Contact to) {
        if (to==null) { 
            sendPresence(status);
            return;
        }
        if (to.jid.isTransport()) blockNotify(-111,10000);
        ExtendedStatus es= StatusList.getInstance().getStatus(status);
        Presence presence = new Presence(status, es.getPriority(), es.getMessage());
        presence.setTo(to.getJid());
        if (theStream!=null) {
            theStream.send( presence );
        }
        if (to instanceof MucContact) ((MucContact)to).commonPresence=false;
    }
	
    public boolean isLoggedIn() {
        if (theStream==null) return false;
        return theStream.loggedIn;
    }

    public Contact selfContact() {
	return getContact(myJid.getJid(), true);
    }
    
    public void sendConferencePresence() {
        ExtendedStatus es= StatusList.getInstance().getStatus(myStatus);
        for (Enumeration e=hContacts.elements(); e.hasMoreElements();) {
            Contact c=(Contact) e.nextElement();
            if (c.origin!=Contact.ORIGIN_GROUPCHAT) continue;
            if (c.status==Presence.PRESENCE_OFFLINE) continue;
            if (!((MucContact)c).commonPresence) continue;
             Presence presence = new Presence(myStatus, es.getPriority(), es.getMessage());
            presence.setTo(c.getJid());
            theStream.send(presence);
        }
    }
    
    public void sendConferencePresence(String message) {
        myMessage=message;
        ExtendedStatus es= StatusList.getInstance().getStatus(myStatus);
        for (Enumeration e=hContacts.elements(); e.hasMoreElements();) {
            Contact c=(Contact) e.nextElement();
            if (c.origin!=Contact.ORIGIN_GROUPCHAT) continue;
            if (c.status==Presence.PRESENCE_OFFLINE) continue;
            Presence presence = new Presence(myStatus, es.getPriority(), myMessage);
            presence.setAttribute("to", c.getJid());
            theStream.send(presence);
        }
    }
    
    public void sendPresence(String to, String type, JabberDataBlock child) {
        JabberDataBlock presence=new Presence(to, type);
        if (child!=null) presence.addChild(child);
        theStream.send(presence);
    }
	
    
    public void doSubscribe(Contact c) {
        if (c.subscr==null) return;
        boolean subscribe = 
                c.subscr.startsWith("none") || 
                c.subscr.startsWith("from");
        if (c.ask_subscribe) subscribe=false;

        boolean subscribed = 
                c.subscr.startsWith("none") || 
                c.subscr.startsWith("to");
                //getMessage(cursor).messageType==Msg.MESSAGE_TYPE_AUTH;
        
        String to=c.getBareJid();
        
        if (subscribed) sendPresence(to,"subscribed", null);
        if (subscribe) sendPresence(to,"subscribe", null);
    }
    
  
  /**
     * Method to send a message to the specified recipient
     */
    
    public void sendMessage(Contact to, final String body, final String subject , int composingState) {
        boolean groupchat=to.origin==Contact.ORIGIN_GROUPCHAT;
        
        if (setAutoStatus) {
            if (isAway) {
                    ExtendedStatus es=StatusList.getInstance().getStatus(oldStatus);
                    String ms=es.getMessage();
                    sendPresence(oldStatus, ms);
                    isAway=false;
                    myStatus=oldStatus;
            }
        }

        Message message = new Message( 
                to.getJid(), 
                body, 
                subject, 
                groupchat 
        );
        if (groupchat && body==null && subject==null) return;
        if (composingState>0) {
            JabberDataBlock event=new JabberDataBlock("x", null,null);
            event.setNameSpace("jabber:x:event");
            if (body==null) event.addChild(new JabberDataBlock("id",null, null));
            if (composingState==1) {
                event.addChild("composing", null);
            }
            message.addChild(event);
        }
        //System.out.println(simpleMessage.toString());
        theStream.send( message );
        lastMessageTime=Time.localTime();
        setKeyTimer(0);
    }
    
    private Vector vCardQueue;
    public void resolveNicknames(int transportIndex){
	vCardQueue=new Vector();
	for (Enumeration e=hContacts.elements(); e.hasMoreElements();){
	    Contact k=(Contact) e.nextElement();
	    if (k.jid.isTransport()) continue;
	    if (k.transport==transportIndex && k.nick==null && k.getGroupType()>=Groups.TYPE_COMMON) {
		vCardQueue.addElement(VCard.getQueryVCard(k.getJid(), "nickvc"+k.bareJid));
	    }
	}
	setQuerySign(true);
	sendVCardReq();
	
    }
    private void sendVCardReq(){
        querysign=false; 
        if (vCardQueue!=null) if (!vCardQueue.isEmpty()) {
            JabberDataBlock req=(JabberDataBlock) vCardQueue.lastElement();
            vCardQueue.removeElement(req);
            //System.out.println(k.nick);
            theStream.send(req);
            querysign=true;
        }
        updateMainBar();
    }
    /**
     * Method to handle an incomming datablock.
     *
     * @param data The incomming data
     */

    public void loginFailed(String error){
        myStatus=Presence.PRESENCE_OFFLINE;
        setProgress(SR.MS_LOGIN_FAILED, 0);
        
        errorLog(error);
		
        try {
            theStream.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        theStream=null;
        System.gc();
		
        reconnect=false;
        setQuerySign(false);
        redraw();
    }
    
    public void loginSuccess() {
        // enable File transfers
//#if (FILE_IO && FILE_TRANSFER)
//#             theStream.addBlockListener(TransferDispatcher.getInstance());
//#endif
        //query bookmarks
        theStream.addBlockListener(new BookmarkQuery(BookmarkQuery.LOAD));
        
        //enable keep-alive packets
        theStream.startKeepAliveTask();
		
		theStream.loggedIn=true;
		
		reconnectCount=0;
        // залогинились. теперь, если был реконнект, то просто пошлём статус
        if (reconnect) {
            querysign=reconnect=false;
            sendPresence(myStatus);
            
            //тут будем реконнектить конференции 
            if (cf.autoJoinConferences)
                    mucReconnect();
            
            return;
        }
        
        // иначе будем читать ростер
        theStream.enableRosterNotify(true);
        rpercent=60;
        //AutoAway=new TimerTaskAutoAway();
        if (cf.setAutoStatus) TimerTaskAutoAway.startRotate(5,this);
        if (StaticData.getInstance().account.isMucOnly()) {
            setProgress(SR.MS_CONNECTED,100);
            try {
                reEnumRoster();
            } catch (Exception e) {
                //e.printStackTrace();
            }
			
            querysign=reconnect=false;
            SplashScreen.getInstance().close(); // display.setCurrent(this);
        } else {
            JabberDataBlock qr=new IqQueryRoster();
            setProgress(SR.MS_ROSTER_REQUEST, 60);
            theStream.send( qr );
        }
    }

    public void bindResource(String myJid) {
        Contact self=selfContact();
        self.jid=this.myJid=new Jid(myJid);
    }

    public void blockArrived( JabberDataBlock data ) {
        try {
            
            if( data instanceof Iq ) {
		String from=data.getAttribute("from");
                String type = (String) data.getTypeAttribute();
                String id=(String) data.getAttribute("id");
                
                if (id!=null) {
                    if (id.startsWith("ping")) theStream.pingSent=false;
				
                    if (id.startsWith("nickvc")) {
                        VCard vc=new VCard(data);//.getNickName();
                        //String from=vc.getJid();
                        String nick=vc.getNickName();
                        
                        Contact c=findContact(new Jid(from), false);
                        
                        String group=(c.getGroupType()==Groups.TYPE_COMMON)?
                            null: c.getGroup().name;
                        if (nick!=null)  storeContact(from,nick,group, false);
                        //updateContact( nick, c.rosterJid, group, c.subscr, c.ask_subscribe);
                        sendVCardReq();
                    }
                    
                    if (id.startsWith("getvc")) {
                        setQuerySign(false);
                        VCard vcard=new VCard(data);
                        String jid=id.substring(5);
                        Contact c=getContact(jid, true);
                        if (c!=null) {
                            c.vcard=vcard;
                            new vCardForm(display, vcard, c.getGroupType()==Groups.TYPE_SELF);
                        }
                    }
                    
                    if (id.equals("getver")) {
                        from=data.getAttribute("from");
                        String body=null;
                        if (type.equals("error")) {
                            body=SR.MS_NO_VERSION_AVAILABLE;
                            querysign=false;
                        } else if (type.equals("result")) {
                            JabberDataBlock vc=data.getChildBlock("query");
                            if (vc!=null) {
                                body=IqVersionReply.dispatchVersion(vc);
                                //гг
                            }
                            querysign=false;
                        }
                        
                        Msg m=new Msg(Msg.MESSAGE_TYPE_IN, "ver", SR.MS_CLIENT_INFO, body);
                        if (body!=null) { 
                            messageStore( getContact(from, false), m);
                            redraw();
                        }
                    }
                    
                } // id!=null
                if ( type.equals( "result" ) ) {
                    if (id.equals("getros")) {
                        // а вот и ростер подошёл :)
                        theStream.enableRosterNotify(false);

                        processRoster(data);
                        
                        setProgress(SR.MS_CONNECTED,100);
                        reEnumRoster();
                        // теперь пошлём присутствие
                        querysign=reconnect=false;
                        
                        if (cf.autoLogin) {
                            if (cf.loginstatus>4) {
                                sendPresence(Presence.PRESENCE_INVISIBLE);    
                            } else {
                                sendPresence(cf.loginstatus);
                            }
                        } else sendPresence(myStatus);
                        
                        SplashScreen.getInstance().close(); // display.setCurrent(this);                      
                    } 
                    if (id.equals("last")) {
                        JabberDataBlock tm=data.getChildBlock("query");
                        if (tm!=null) {
                            querysign=false;
                            from=data.getAttribute("from");
                            String body=IqLast.dispatchLast(tm);
                            
                            String lastType="seen/online";
                            
                            if (from.indexOf("/")>-1) lastType="idle";
                            
                            String status=tm.getText();
                            
                            Msg m=new Msg(Msg.MESSAGE_TYPE_IN, lastType, lastType, body+"\n"+status);
                            messageStore( getContact(from, false), m);
                            redraw();
                        }
                    }
                    if (id.equals("time")) {
                        JabberDataBlock tm=data.getChildBlock("query");
                        if (tm!=null) {
                            querysign=false;
                            from=data.getAttribute("from");
                            String body=IqTimeReply.dispatchTime(tm);
                            
                            String status=tm.getText();
                            
                            Msg m=new Msg(Msg.MESSAGE_TYPE_IN, SR.MS_TIME, SR.MS_TIME, body+"\n"+status);
                            messageStore( getContact(from, false), m);
                        }
                    }
                } else if (type.equals("get")){
                    JabberDataBlock query=data.getChildBlock("query");
                    if (query!=null){
                        Contact c=getContact(from, true);
                        if (query.isJabberNameSpace("jabber:iq:version")) {
                            c.setViewing(true);
                            theStream.send(new IqVersionReply(data));
                        }
                        // проверяем на запрос локального времени клиента
                        else if (query.isJabberNameSpace("jabber:iq:time")) {
                            theStream.send(new IqTimeReply(data));
                            c.setViewing(true);
                        }
                        // проверяем на запрос idle
                        else if (query.isJabberNameSpace("jabber:iq:last")) {
                            theStream.send(new IqLast(data, lastMessageTime));
                            c.setViewing(true);
                        }
                        else replyError(data);
                    }
                } else if (type.equals("set")) {
                    processRoster(data);
					theStream.send(new Iq(from, Iq.TYPE_RESULT, id));
                    reEnumRoster();
                }
            }
            
            // If we've received a message
            
            else if( data instanceof Message ) {
                querysign=false;
                boolean highlite=false;
                Message message = (Message) data;
                
                String from=message.getFrom();
                String body=message.getBody().trim();    
                String oob=message.getOOB();
                if (oob!=null) body+=oob;
                if (body.length()==0) body=null; 
                String subj=message.getSubject().trim(); if (subj.length()==0) subj=null;
                
                String tStamp=message.getTimeStamp();
		
                int start_me=-1;    //  не добавлять ник
                String name=null;
                boolean groupchat=false;
                conference=false;
				
				int mType=Msg.MESSAGE_TYPE_IN;
                
                try { // type=null
		    String type=message.getTypeAttribute();
                    if (type.equals("groupchat")) {
                        groupchat=true;
                        conference=true;
                        start_me=0; // добавить ник в начало
                        int rp=from.indexOf('/');
                        
                        name=from.substring(rp+1);
                        
                        if (rp>0) from=from.substring(0, rp);
                        
                        // subject
                        if (subj!=null) {
                            if (body==null) 
                                body=name+" "+SR.MS_HAS_SET_TOPIC_TO+" "+subj;
                            subj=null;
                            start_me=-1; // не добавлять /me к subj
                            highlite=true;
                            mType=Msg.MESSAGE_TYPE_SUBJ;
                            //sendConferencePresence();
                        }
                    }
                    if (type.equals("error")) {
                        
                        String errCode=message.getChildBlock("error").getAttribute("code");
                        
                        switch (Integer.parseInt(errCode)) {
                            case 302: body="Redirect"; break;
                            case 400: body="Bad Request"; break;
                            case 401: body="Not Authorized"; break;
                            case 402: body="Payment Required"; break;
                            case 403: body=SR.MS_VIZITORS_FORBIDDEN; break;
                            case 404: body="User has left"; break;
                            case 405: body="Not Allowed"; break;
                            case 406: body="Cannot send message because you are not in room"; break;
                            case 407: body="Registration Required"; break;
                            case 408: body="Request Timeout"; break;
                            case 409: body="Nickname is registered by another person"; break;
                            case 500: body="Internal Server Error"; break;
                            case 501: body="Not Implemented"; break;
                            case 502: body="Remote Server Error"; break;
                            case 503: break;
                            case 504: body="Remote Server Timeout"; break;
                            case 510: body="Disconnected"; break;
                            //default: body=SR.MS_ERROR_+message.getChildBlock("error")+"\n"+body;
                        }
                    }
					if (type.equals("headline")) mType=Msg.MESSAGE_TYPE_HEADLINE;
                } catch (Exception e) {}
                
                try {
                    //TODO: invitations
                    JabberDataBlock xmlns=message.findNamespace("http://jabber.org/protocol/muc#user");
                    String password=xmlns.getChildBlockText("password");
                    
                    JabberDataBlock invite=xmlns.getChildBlock("invite");
                    String inviteFrom=invite.getAttribute("from");
                    String inviteReason=invite.getChildBlockText("reason");
                            
                    String room=from+'/'+sd.account.getNickName();

                    ConferenceGroup invConf=initMuc(room, password);
                    if (invConf.getSelfContact().status==Presence.PRESENCE_OFFLINE)
                        invConf.getConference().status=Presence.PRESENCE_OFFLINE;
                    
                    body=inviteFrom+SR.MS_IS_INVITING_YOU+from+" ("+inviteReason+')';
                    
                } catch (Exception e) {}
                
                Contact c=getContact(from, true);

                if (name==null) name=c.getName();
                // /me

                if (body!=null) {
                    forme=false;
                    if (body.startsWith("/me ")) start_me=3;
                    if (start_me>=0) {
                        StringBuffer b=new StringBuffer(name);
                        if (start_me==0) b.append("> ");
                        b.append(body.substring(start_me));
                        body=b.toString();
                    }
                }
                
                boolean compose=false;
                JabberDataBlock x=message.getChildBlock("x");
                //if (body.length()==0) body=null; 
                
                if (x!=null) {
                    compose=(  x.getChildBlock("composing")!=null 
                            && c.status<Presence.PRESENCE_OFFLINE); // drop composing events from offlines
                    
                    if (compose) c.acceptComposing=true ; 
                    if (body!=null) compose=false;
                    c.setComposing(compose);
                if (compose) playNotify(888);
                }
                redraw();

                if (body==null) return;
                
                Msg m=new Msg(mType, from, subj, body);
                if (tStamp!=null) 
                    m.dateGmt=Time.dateIso8601(tStamp);
                if (groupchat) {
                    ConferenceGroup mucGrp=(ConferenceGroup)c.getGroup();
                    if (mucGrp.getSelfContact().getJid().equals(message.getFrom())) {
                        m.messageType=Msg.MESSAGE_TYPE_OUT;
                        m.unread=false;
                    } else {
                        if (m.dateGmt<= ((ConferenceGroup)c.getGroup()).conferenceJoinTime) m.messageType=Msg.MESSAGE_TYPE_HISTORY;
                        // highliting messages with myNick substring
	                String myNick=mucGrp.getSelfContact().getName();
			if (body.indexOf(myNick)>-1) {
                                //highlite |= body.indexOf(myNick)>-1;
	                        if (body.indexOf("> "+myNick+": ")>-1) {
	                            highlite=true;
	                        } else if (body.indexOf(" "+myNick+">")>-1) {
	                            highlite=true;
	                        } else if (body.indexOf(" "+myNick+",")>-1) {
	                            highlite=true;
	                        } else if (body.indexOf(": "+myNick+": ")>-1) {
	                            highlite=true;
	                        } else if (body.indexOf(" "+myNick+" ")>-1) {
	                            highlite=true;
	                        } else if (body.indexOf(", "+myNick)>-1) {
	                            highlite=true;
	                        } else if (body.endsWith(" "+myNick)) {
	                            highlite=true;
	                        } else if (body.indexOf(" "+myNick+"?")>-1) {
	                            highlite=true;
	                        } else if (body.indexOf(" "+myNick+"!")>-1) {
	                            highlite=true;
	                        } else if (body.indexOf(" "+myNick+".")>-1) highlite=true;
			}
                        //TODO: custom highliting dictionary
                    }
		m.from=name;

                }
                forme=highlite;
                m.setHighlite(highlite); 
 
                if (c.getGroupType()!=Groups.TYPE_NOT_IN_LIST || cf.notInList)
                    messageStore(c, m);
            }
            // присутствие

            else if( data instanceof Presence ) {
                if (myStatus==Presence.PRESENCE_OFFLINE) return;
                Presence pr= (Presence) data;
                
                String from=pr.getFrom();
                pr.dispathch();
                int ti=pr.getTypeIndex();
                //PresenceContact(from, ti);
                Msg m=new Msg(
                        (ti==Presence.PRESENCE_AUTH || ti==Presence.PRESENCE_AUTH_ASK)?
                            Msg.MESSAGE_TYPE_AUTH:Msg.MESSAGE_TYPE_PRESENCE,
                        from,
                        null,
                        pr.getPresenceTxt());
                
                JabberDataBlock xmuc=pr.findNamespace("http://jabber.org/protocol/muc");
                if (xmuc!=null) try {
                    MucContact c = mucContact(from);
                    
//toon
//                   String statusText=status.getChildBlockText("status"); 
//toon                    
                    
                    //System.out.println(b.toString());


                    //c.nick=nick;
                    
                    from=from.substring(0, from.indexOf('/'));
                    Msg chatPresence=new Msg(
                           Msg.MESSAGE_TYPE_PRESENCE,
			   "prs",
                           null,
                           c.processPresence(xmuc, pr) );
                    if (cf.storeConfPresence) {
                        messageStore(getContact(from, false), chatPresence);
                    }
                    c.addMessage(m);
                    c.priority=pr.getPriority();
                    //if (ti>=0) c.setStatus(ti);
                    
                } /* if (muc) */ catch (Exception e) { /*e.printStackTrace();*/ }
                else {
                    Contact c=getContact(from, false); 
                    //if (c==null) return; // drop presence
                    messageStore(c, m);
					
                    if (ti==Presence.PRESENCE_AUTH_ASK) {
                        if (cf.autoSubscribe) {
                            doSubscribe(c);
                            messageStore(c, new Msg(Msg.MESSAGE_TYPE_AUTH, from, null, SR.MS_AUTH_AUTO));
                        }
                    }
					
                    c.priority=pr.getPriority();
                    if (ti>=0) c.setStatus(ti);
                    
                    if (notifyReady(-111) &&
                        (ti==Presence.PRESENCE_ONLINE ||
                         ti==Presence.PRESENCE_CHAT)) {
                            if (lastAppearedContact!=null) lastAppearedContact.setAppearing(false);
                            c.setAppearing(true);
                            lastAppearedContact=c;
                          }
                    if (ti==Presence.PRESENCE_OFFLINE) c.setAppearing(false);

                    if (ti>=0) {
                        if (ti!=11 && (c.getGroupType()!=Groups.TYPE_TRANSP) && (c.getGroupType()!=Groups.TYPE_IGNORE)) playNotify(ti);
                    }
                }
		sort(hContacts);
                reEnumRoster();
            }
        } catch( Exception e ) {
            //e.printStackTrace();
        }
    }
    
    void replyError (JabberDataBlock stanza) {
        stanza.setAttribute("to", stanza.getAttribute("from"));
        stanza.setAttribute("from", null);
        stanza.setTypeAttribute("error");
        JabberDataBlock error=stanza.addChild("error", null);
        error.setTypeAttribute("cancel");
        error.addChild("feature-not-implemented",null);
        theStream.send(stanza);
    }
    
    void processRoster(JabberDataBlock data){
        JabberDataBlock q=data.getChildBlock("query");
        if (!q.isJabberNameSpace("jabber:iq:roster")) return;
        int type=0;
		
        //verifying from attribute as in RFC3921/7.2
        String from=data.getAttribute("from");
        if (from!=null) {
            String myJid=sd.account.getJid();
            if (! from.toLowerCase().equals(myJid.toLowerCase())) return;
        }
        
        Vector cont=(q!=null)?q.getChildBlocks():null;
        
        if (cont!=null)
            for (Enumeration e=cont.elements(); e.hasMoreElements();){
                JabberDataBlock i=(JabberDataBlock)e.nextElement();
                if (i.getTagName().equals("item")) {
                    String name=i.getAttribute("name");
                    String jid=i.getAttribute("jid");
                    String subscr=i.getAttribute("subscription");
                    boolean ask= (i.getAttribute("ask")!=null);

                    // найдём группу
                    String group=i.getChildBlockText("group");
                    if (group.length()==0) group=Groups.COMMON_GROUP;

                    // так можно проверить, когда пришёл jabber:iq:roster,
                    // на запрос ростера или при обновлении

                    //String iqType=data.getTypeAttribute();
                    //if (iqType.equals("set")) type=1;

                    updateContact(name,jid,group, subscr, ask);
                    sort(hContacts);
                }
            
            }
    }
    
    
    void messageStore(Contact c, Msg message) {
        if (c==null) return;  
        c.addMessage(message);
        
        if (cf.ghostMotor) System.gc(); 

        if (!message.unread) return;
        //TODO: clear unread flag if not-in-list IS HIDDEN
        
        if (countNewMsgs()) reEnumRoster();
        
        if (c.getGroupType()==Groups.TYPE_IGNORE) return;    // no signalling/focus on ignore
        
	if (cf.popupFromMinimized)
	    Bombus.getInstance().hideApp(false);
	
        if (cf.autoFocus) focusToContact(c, false);

        if (forme) {
            playNotify(500);
        } else if (conference) {
            if (message.messageType==message.MESSAGE_TYPE_IN) playNotify(800);
        } else {
            playNotify(1000);
        }
    }
    
    public void blockNotify(int event, long ms) {
        if (!notifyReady(-111)) return;
        blockNotifyEvent=event;
        notifyReadyTime=System.currentTimeMillis()+ms;
    }

    public boolean notifyReady(int event) {
        if ((blockNotifyEvent==event ||
            (blockNotifyEvent==-111 && event<=7)) &&
           System.currentTimeMillis()<notifyReadyTime) return false;
        else return true;
    }
   
    public void playNotify(int event) {
        if (!notifyReady(event)) return;
        
//        System.out.println("event: "+event);
	
        AlertCustomize ac=AlertCustomize.getInstance();
        
        int volume=ac.soundVol;
        int vibraLen=cf.vibraLen;
        String type, message;
        
        switch (event) {
            case 0: //online
                message=ac.soundOnline;
                type=ac.soundOnlineType;
                vibraLen=0;
                break;
            case 1: //chat
                message=ac.soundOnline;
                type=ac.soundOnlineType;
                vibraLen=0;
                break;
            case 5: //offline
                message=ac.soundOffline;
                type=ac.soundOfflineType;
                vibraLen=0;
                break;
            case 1000: //message
                message=ac.messagesnd;
                type=ac.messageSndType;
                break;
            case 800: //conference
                message=ac.soundConference;
                type=ac.soundConferenceType;
                break;
            case 500: //message for you
                message=ac.soundForYou;
                type=ac.soundForYouType;
                break;
            case 888: //composing
                message=ac.soundComposing;
                type=ac.soundComposingType;
                vibraLen=0;
                break;
            default :
                message="";
                type="none";
                vibraLen=0;
                break;
        }
            int profile=cf.profile;
            if (profile==AlertProfile.AUTO) profile=AlertProfile.ALL;
        
        EventNotify notify=null;
        
        boolean blFlashEn=cf.blFlash;   // motorola e398 backlight bug
        
        switch (profile) {
            case AlertProfile.ALL:   notify=new EventNotify(display, type, message,  volume, vibraLen, blFlashEn); break;
            case AlertProfile.NONE:  notify=new EventNotify(display, null, null,  volume,    0,           false    ); break;
            case AlertProfile.VIBRA: notify=new EventNotify(display, null, null,  volume,    vibraLen, blFlashEn); break;
            case AlertProfile.SOUND: notify=new EventNotify(display, type, message,  volume, 0,           blFlashEn); break;
        }
        if (notify!=null) notify.startNotify();
        blockNotify(event, 2000);
    }
    
/*    
    Contact messageStore(Msg message){
        Contact c=getContact(message.from, true);
        if (c.getGroupType()==Groups.TYPE_NOT_IN_LIST) 
            if (!cf.notInList) return c;

        messageStore(c, message);
        return c;
    }
*/
    private void focusToContact(final Contact c, boolean force) {
	
	Group g=c.getGroup();
	if (g.collapsed) {
	    g.collapsed=false;
	    reEnumerator.queueEnum(c, force);
	    //reEnumRoster();
	} else {
	    
	    int index=vContacts.indexOf(c);
	    if (index>=0) moveCursorTo(index, force);
	}
    }
    
    
    /**
     * Method to begin talking to the server (i.e. send a login message)
     */
    
    public void beginConversation(String SessionId) {
        //try {
        //setProgress(SR.MS_LOGINPGS, 42);
        
//#if SASL
        if (sd.account.isSASL()) {
            new SASLAuth(sd.account, SessionId, this, theStream)
  //#if SASL_XGOOGLETOKEN
            .setToken(token)
  //#endif
            ;
   
        } else {
            new NonSASLAuth(sd.account, SessionId, this, theStream);
        }
//#else
//#         new NonSASLAuth(sd.account, SessionId, this, theStream);
//#endif
    }
    
    /**
     * If the connection is terminated then print a message
     *
     * @e The exception that caused the connection to be terminated, Note that
     *  receiving a SocketException is normal when the client closes the stream.
     */
    public void connectionTerminated( Exception e ) {
        String error=null;
        setProgress(SR.MS_DISCONNECTED, 0);
         if( e != null ) {
            askReconnect(e);
            
        } else {
            try {
                sendPresence(Presence.PRESENCE_OFFLINE);
            } catch (Exception e2) {
                //e2.printStackTrace();
            }
         }
        redraw();
    }

    private void askReconnect(final Exception e) {
        String error;
        error=e.getClass().getName()+"\n"+e.getMessage();
        e.printStackTrace();

        lastOnlineStatus=myStatus;
         try {
             sendPresence(Presence.PRESENCE_OFFLINE);
        } catch (Exception e2) { }

        if (e instanceof SecurityException || reconnectCount>=maxReconnect) {
            errorLog(error);
        } else {
            reconnectCount++;
            String mainbar="("+reconnectCount+"/"+maxReconnect+") Reconnecting";
            Msg m=new Msg(Msg.MESSAGE_TYPE_OUT, "local", mainbar, error);
            messageStore(selfContact(), m);
            new Reconnect(mainbar, error, display);
         }
     }
    
     private int lastOnlineStatus;
     public void doReconnect() {
        sendPresence(lastOnlineStatus);
     }
    
    public void eventOk(){
        super.eventOk();
        if (createMsgList()==null) {
            cleanupGroup();
            reEnumRoster();
        }
    }
    
    
    private Displayable createMsgList(){
        Object e=getFocusedObject();
        if (e instanceof Contact) {
            return new ContactMessageList((Contact)e,display);
        }
        return null;
    }
    protected void keyGreen(){
        if (!isLoggedIn()) return;
        Displayable pview=createMsgList();
        if (pview!=null) {
            Contact c=(Contact)getFocusedObject();
            ( new MessageEdit(display, c, c.msgSuspended) ).setParentView(pview);
            c.msgSuspended=null;
        }
        //reEnumRoster();
    }

    protected void keyPressed(int keyCode) {
        super.keyPressed(keyCode);
        
        if (keyCode==keyClear) 
            try { 
                boolean isContact=( getFocusedObject() instanceof Contact );
                if (isContact)
                    new RosterItemActions(display, getFocusedObject(), RosterItemActions.DELETE_CONTACT); 
            } catch (Exception e) { /* NullPointerException */ }

//#if (MOTOROLA_BACKLIGHT)
        if (cf.ghostMotor) {
            // backlight management
            if (keyCode=='*') blState=(blState==1)? Integer.MAX_VALUE : 1;
            else blState=Integer.MAX_VALUE;
            
            display.flashBacklight(blState);
        }
        if (VirtualList.digitMemMonitor) {
            if (keyCode==-4 || keyCode==-1)  {
                if (ph.PhoneManufacturer()==ph.SIEMENS2 || ph.PhoneManufacturer()==ph.SIEMENS) {
                    new RosterMenu(display, getFocusedObject());
                    return;
                 }         
            }

            if (keyCode==-21 || keyCode==-22 || keyCode==21 || keyCode==22) {
                if (cf.ghostMotor) {
                    new RosterMenu(display, getFocusedObject());
		    return;
                }
            }

            if (keyCode==-7 || keyCode==-6)  {
                if (ph.PhoneManufacturer()==ph.NOKIA || ph.PhoneManufacturer()==ph.SONYE) {
                    new RosterMenu(display, getFocusedObject());
	            return;
                 }         
            }
            if (keyCode==40 || keyCode==41)  {
                if (ph.PhoneManufacturer()==ph.WINDOWS) {
                    new RosterMenu(display, getFocusedObject());
                    return;
                 }         
            }
        }
		
		
//#endif
    }

    public void userKeyPressed(int keyCode){
        //System.out.println(ph.PhoneManufacturer());
        if (keyCode==KEY_NUM0 || keyCode==keyBack) {
            cleanMarks();
            
            System.gc();
            
            if (messageCount==0) return;
            Object atcursor=getFocusedObject();
            Contact c=null;
            if (atcursor instanceof Contact) c=(Contact)atcursor;
            // а если курсор на группе, то искать с самого начала.
            else c=(Contact)hContacts.firstElement();
            
            Enumeration i=hContacts.elements();
            
            int pass=0; // 0=ищем курсор, 1=ищем
            while (pass<2) {
                if (!i.hasMoreElements()) i=hContacts.elements();
                Contact p=(Contact)i.nextElement();
                if (pass==1) if (p.getNewMsgsCount()>0) { 
		    focusToContact(p, true);
                    setRotator();
                    break; 
                }
                if (p==c) pass++; // полный круг пройден
            }
            System.gc();
            return;
        }
		
        if (VirtualList.digitMemMonitor) {
            if (keyCode==-4 || keyCode==-1)  {
                if (ph.PhoneManufacturer()==ph.SIEMENS || ph.PhoneManufacturer()==ph.SIEMENS2) {
                    new RosterMenu(display, getFocusedObject());
                    return;
                 }         
            }

            if (keyCode==-21 || keyCode==-22 || keyCode==21 || keyCode==22) {
                if (cf.ghostMotor) {
                    new RosterMenu(display, getFocusedObject());
	            return;
                }
            }

            if (keyCode==-7 || keyCode==-6)  {
                if (ph.PhoneManufacturer()==ph.NOKIA || ph.PhoneManufacturer()==ph.SONYE) {
                    new RosterMenu(display, getFocusedObject());
		    return;
                 }         
            }
            if (keyCode==40 || keyCode==41)  {
                if (ph.PhoneManufacturer()==ph.WINDOWS) {
                    new RosterMenu(display, getFocusedObject());
                    return;
                 }         
            }
        }
        if (keyCode=='3') {
            searchGroup(-1);
            setRotator();
            return;
        }
        if (keyCode=='9') {
            //searchGroup(1);
            //setRotator();
            new ColorForm(display);
            return;
        }
        
         if (keyCode==KEY_POUND) {
            if (ph.PhoneManufacturer()==ph.SIEMENS || ph.PhoneManufacturer()==ph.SIEMENS2)
            {
                System.gc();
                setWobbler();
            } else {
                fullMode=VirtualList.isbottom;
                VirtualList.isbottom=(fullMode+1)%7;
                System.gc();
            }
            return;
         }
         if (keyCode==KEY_STAR) {
             if (ph.PhoneManufacturer()==ph.SIEMENS || ph.PhoneManufacturer()==ph.SIEMENS2) {
                fullMode=VirtualList.isbottom;
                VirtualList.isbottom=(fullMode+1)%7;
                System.gc();
             } else {
                System.gc();
                setWobbler();
            }
             return;
         }       
    }
   
    protected void keyRepeated(int keyCode) {
        super.keyRepeated(keyCode);
        if (kHold==keyCode) return;
        //kHold=keyCode;
        kHold=keyCode;
        
        if (keyCode==cf.keyLock) {
            new SplashScreen(display, getMainBarItem(), cf.keyLock, cf.ghostMotor, false);
            return;
        } else if (keyCode==cf.keyVibra || keyCode==MOTOE680_FMRADIO /* TODO: redefine keyVibra*/) {
            // swap profiles
            int profile=cf.profile;
            cf.profile=(profile==AlertProfile.VIBRA)? 
                cf.lastProfile : AlertProfile.VIBRA;
            cf.lastProfile=profile;
            
            updateMainBar();
            redraw();
            return;
        } else if (keyCode==cf.keyOfflines || keyCode==keyBack) {
            cf.showOfflineContacts=!cf.showOfflineContacts;
            reEnumRoster();
            return;
        } else if (keyCode==KEY_NUM1) new Bookmarks(display, null);
       	else if (keyCode==KEY_NUM3) new ActiveContacts(display, null);
       	else if (keyCode==KEY_NUM4) new ConfigForm(display);
       	else if (keyCode==KEY_NUM7) new RosterToolsMenu(display);
        
        else if (keyCode==cf.keyHide) {
            if (cf.allowMinimize)
                Bombus.getInstance().hideApp(true);
            else if (ph.PhoneManufacturer()==ph.SIEMENS2)//SIEMENS: MYMENU call. Possible Main Menu for capable phones
             try {
                  Bombus.getInstance().platformRequest("native:ELSE_STR_MYMENU");
             } catch (Exception e) { }     
            else if (ph.PhoneManufacturer()==ph.SIEMENS)//SIEMENS-NSG: MYMENU call. Possible Native Menu for capable phones
             try {
                Bombus.getInstance().platformRequest("native:NAT_MAIN_MENU");
             } catch (Exception e) { }   
        }
    }

    public void setWobbler() {
        StringBuffer mess=new StringBuffer();
        Contact contact=(Contact)getFocusedObject();
        if (contact instanceof MucContact) {
            MucContact mucContact=(MucContact)contact;
            String jid=(mucContact.realJid==null)?"":"jid: "+mucContact.realJid;
            mess.append(jid+"\n"+mucContact.affiliation+"/"+mucContact.role);
        } else {
            mess.append("jid: "+contact.bareJid);
            mess.append("\nsubscription: "+contact.subscr);
        }
        mess.append((contact.presence!=null)?"\nstatus: "+contact.presence:"");

        VirtualList.setWobble(mess.toString());
    }
    
    public void logoff(){
        if (isLoggedIn())
        try {
             //sendPresence(Presence.PRESENCE_OFFLINE);
             
             ExtendedStatus es=StatusList.getInstance().getStatus(Presence.PRESENCE_OFFLINE);
             sendPresence(Presence.PRESENCE_OFFLINE, es.getMessage());
             
        } catch (Exception e) { 
            //e.printStackTrace(); 
        }
    };

   
    public void commandAction(Command c, Displayable d){
        if (c==cmdQuit) {
            fullMode=VirtualList.isbottom; //save panels state on exit
            cf.isbottom=(fullMode)%7;          
            cf.saveToStorage();
            
            destroyView();
            logoff();
            //StaticData sd=StaticData.getInstance();
            //cf.saveToStorage();
	    Bombus.getInstance().notifyDestroyed();
            return;
        }
        if (c==cmdMinimize) { Bombus.getInstance().hideApp(true);  }
        
        if (c==cmdActiveContacts) {
                new ActiveContacts(display, null);
        }
        
        if (c==cmdAccount){ new AccountSelect(display, false); }
        if (c==cmdStatus) { reconnectCount=0; new StatusSelect(display, null); }
        if (c==cmdAlert) { new AlertProfile(display); }
        
	if (c==cmdArchive) { new ArchiveList(display, null, -1); }
        
        if (c==cmdInfo) { new Info.InfoWindow(display); }
        
        if (c==cmdTurnOnLight) {
            lightType=1;
            setLight(true);
            cf.lightType=1;
            cf.saveToStorage();
            removeCommand(cmdTurnOffLight);
            removeCommand(cmdTurnOnLight);
            addCommand(cmdPatchLight);
        }
        if (c==cmdTurnOffLight) {
            lightType=0;
            setLight(false);
            cf.lightType=0;
            cf.saveToStorage();
            removeCommand(cmdPatchLight);
            removeCommand(cmdTurnOffLight);
            addCommand(cmdTurnOnLight);
        }
        if (c==cmdPatchLight) {
            lightType=2;
            cf.lightType=2;
            cf.saveToStorage();
            removeCommand(cmdPatchLight);
            removeCommand(cmdTurnOnLight);
            addCommand(cmdTurnOffLight);
        }
        
        if (c==cmdTools) { new RosterToolsMenu(display); }
        // stream-sensitive commands
        // check for closed socket
        if (!isLoggedIn()) return;
        
        if (c==cmdConference) { 
            //new ConferenceForm(display); 
            new Bookmarks(display, null);
        }
        if (c==cmdActions) try { 
            new RosterItemActions(display, getFocusedObject(), -1);
        } catch (Exception e) { /* NullPointerException */ }
        
        if (c==cmdAdd) {
            Object o=getFocusedObject();
            Contact cn=null;
            if (o instanceof Contact) {
                cn=(Contact)o;
                if (cn.getGroupType()!=Groups.TYPE_NOT_IN_LIST && cn.getGroupType()!=Groups.TYPE_SEARCH_RESULT) cn=null;
            }
            if (o instanceof MucContact) { cn=(Contact)o; }
            new ContactEdit(display, cn);
        }
    }
    

    public void reEnterRoom(Group group) {
	ConferenceGroup confGroup=(ConferenceGroup)group;
        String confJid=confGroup.getSelfContact().getJid();
	new ConferenceForm(display, confJid, confGroup.password, false);
        //sendPresence(confGroup.getSelfContact().getJid(), null, null);

	//confGroup.getConference().status=Presence.PRESENCE_ONLINE;
    }
    
    public void leaveRoom(int index, Group group){
	//Group group=groups.getGroup(index);
	ConferenceGroup confGroup=(ConferenceGroup)group;
	Contact myself=confGroup.getSelfContact();
        sendPresence(myself.getJid(), "unavailable", null);
		//roomOffline(group);
        for (Enumeration e=hContacts.elements(); e.hasMoreElements();) {
            Contact contact=(Contact)e.nextElement();
            if (contact.inGroup(group)) contact.setStatus(Presence.PRESENCE_OFFLINE);
        }
	}
    public void roomOffline(final Group group) {
         for (Enumeration e=hContacts.elements(); e.hasMoreElements();) {
             Contact contact=(Contact)e.nextElement();
            if (contact.inGroup(group)) contact.setStatus(Presence.PRESENCE_OFFLINE);
         }
    }
    
    protected void showNotify() { super.showNotify(); countNewMsgs(); }
    
    private void searchGroup(int direction){
	synchronized (vContacts) {
	    int size=vContacts.size();
	    int pos=cursor;
	    int count=size;
	    try {
		while (count>0) {
		    pos+=direction;
		    if (pos<0) pos=size-1;
		    if (pos>=size) pos=0;
		    if (vContacts.elementAt(pos) instanceof Group) break;
		}
		moveCursorTo(pos, true);
	    } catch (Exception e) { }
	}
    }
    
    public void ActionConfirmed() {
        deleteContact((Contact)getFocusedObject());
    }

    public void deleteContact(Contact c) {
	for (Enumeration e=hContacts.elements();e.hasMoreElements();) {
	    Contact c2=(Contact)e. nextElement();
	    if (c.jid.equals(c2. jid,false)) {
			c2.setStatus(Presence.PRESENCE_TRASH);
			c2.offline_type=Presence.PRESENCE_TRASH;
	    }
	}
	
	if (c.getGroupType()==Groups.TYPE_NOT_IN_LIST) {
	    hContacts.removeElement(c);
            countNewMsgs();
	    reEnumRoster();
	} else
	    theStream.send(new IqQueryRoster(c.getBareJid(),null,null,"remove"));
    }
   
    
    public void setQuerySign(boolean requestState) {
        querysign=requestState;
        updateMainBar();
    }
    
    /**
     * store cotnact on server
     */
    public void storeContact(String jid, String name, String group, boolean askSubscribe){
        
        theStream.send(new IqQueryRoster(jid, name, group, null));
        if (askSubscribe) theStream.send(new Presence(jid,"subscribe"));
    }

    public void loginMessage(String msg) {
        setProgress(msg, 42);
    }

    private class ReEnumerator implements Runnable{

        Thread thread;
        int pendingRepaints=0;
	boolean force;
	
	Object desiredFocus;
        
        public void queueEnum(Object focusTo, boolean force) {
	    desiredFocus=focusTo;
	    this.force=force;
	    queueEnum();
        }
	
        synchronized public void queueEnum() {
            pendingRepaints++;
            if (thread==null) (thread=new Thread(this)).start();
        }
        
        public void run(){
            try {
                while (pendingRepaints>0) {
                    //System.out.println(pendingRepaints);
                    pendingRepaints=0;
                    
                    int locCursor=cursor;
                    Object focused=(desiredFocus==null)?getFocusedObject():desiredFocus;
		    desiredFocus=null;
                    
                    Vector tContacts=new Vector(vContacts.size());
                    //boolean offlines=cf.showOfflineContacts;//StaticData.getInstance().config.showOfflineContacts;
                    
                    Enumeration e;
                    int i;
                    groups.resetCounters();
                    
                    synchronized (hContacts) {
                        for (e=hContacts.elements();e.hasMoreElements();){
                            Contact c=(Contact)e.nextElement();
                            boolean online=c.status<5;
                            // group counters
                            Group grp=c.getGroup();
			    grp.addContact(c);
                        }
                    }
                    
                    // self-contact group
                    Group selfContactGroup=groups.getGroup(Groups.TYPE_SELF);
                    if (cf.selfContact || selfContactGroup.tonlines>1 || selfContactGroup.unreadMessages>0 )
                        groups.addToVector(tContacts, Groups.TYPE_SELF);
                    // adding groups
                    for (i=Groups.TYPE_COMMON;i<groups.getCount();i++)
                        groups.addToVector(tContacts,i);
                    // hiddens
                    if (cf.ignore) groups.addToVector(tContacts,Groups.TYPE_IGNORE);
                    // not-in-list
                    if (cf.notInList) groups.addToVector(tContacts,Groups.TYPE_NOT_IN_LIST);

                    // transports
                    Group transpGroup=groups.getGroup(Groups.TYPE_TRANSP);
                    if (cf.showTransports || transpGroup.unreadMessages>0)
                        groups.addToVector(tContacts,Groups.TYPE_TRANSP);
                    
                    // always visible
                    Group visibleGroup=groups.getGroup(Groups.TYPE_VISIBLE);
                    //if (visibleGroup.unreadMessages>0)
                        groups.addToVector(tContacts,Groups.TYPE_VISIBLE);
                    
                    //if (groups.getGroup(Groups.SRC_RESULT_INDEX).tncontacts>0)
                    groups.addToVector(tContacts, Groups.TYPE_SEARCH_RESULT);
                    
                    vContacts=tContacts;
                     
                    setRosterMainBar("("+groups.getRosterOnline()+"/"+groups.getRosterContacts()+")");

                    
                    //resetStrCache();
                    if (cursor<0) cursor=0;
                    
                    // вернём курсор на прежний элемент
                    if ( locCursor==cursor && focused!=null ) {
                        int c=vContacts.indexOf(focused);
                        if (c>=0) moveCursorTo(c, force);
			force=false;
                    }
                    //if (cursor>=vContacts.size()) cursor=vContacts.size()-1; //moveCursorEnd(); // вернём курсор из нирваны
                    
                    focusedItem(cursor);
                    redraw();
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
            thread=null;
        }
    }
	
    public void setMyJid(Jid myJid) {
        this.myJid = myJid;
    }
    
    public static void setLight(boolean state) {
            if (state==true) {
                com.siemens.mp.game.Light.setLightOn();
            } else {
                com.siemens.mp.game.Light.setLightOff();    
            }
    }
    
    public void mucReconnect() {
        Enumeration e;
        
        synchronized (hContacts) {
            for (e=hContacts.elements();e.hasMoreElements();){
                Contact c=(Contact)e.nextElement();
                
                if (c.origin==Contact.ORIGIN_GROUPCHAT) {
                    if (c.getGroup() instanceof ConferenceGroup) {
                        ConferenceGroup mucGrp=(ConferenceGroup)c.getGroup();
                        MucContact self=mucGrp.getSelfContact();
                        if (self.status>=Presence.PRESENCE_OFFLINE) {
                            confJoin(mucGrp.getConference().bareJid);
                            //System.out.println("reconnect "+mucGrp.getConference().bareJid);
                        }
                    }
                }
            }
        }
    }
    
    public void confJoin(String conference){
        ConferenceGroup grp=initMuc(conference, "");
        
        JabberDataBlock x=new JabberDataBlock("x", null, null);
        x.setNameSpace("http://jabber.org/protocol/muc");
        
        if (grp.password.length()!=0) {
            // adding password to presence
            x.addChild("password", grp.password);
        }
        
        JabberDataBlock history=x.addChild("history", null);
        history.setAttribute("maxstanzas", String.valueOf(cf.confMessageCount));
        history.setAttribute("maxchars","32768");
        try {
            long last=grp.getConference().lastMessageTime;
            long delay= ( grp.conferenceJoinTime - last ) /1000 ;
            if (last!=0) history.setAttribute("seconds",String.valueOf(delay)); // todo: change to since
        } catch (Exception e) {};

        sendPresence(conference, null, x);
        reEnumRoster();
    }  

    public void setAutoAway() {
        if (!isAway) {
            oldStatus=myStatus;
            if (myStatus==0 || myStatus==1) {
                String away="Auto away since "+Time.timeString(Time.localTime())+"(GMT+"+Time.GMTOffset+")";
                isAway=true;
                sendPresence(2, away);
            }
        }
    }
    
    public void setKeyTimer(int value) {
        keyTimer=value;        
    }
    
    public void cleanMarks() {
      synchronized(hContacts) {
        for (Enumeration e=hContacts.elements(); e.hasMoreElements();){
          Contact c=(Contact)e.nextElement();
            c.setViewing(false);
            c.setAppearing(false);
        }
      }
    }
    
    public void siemensKeyLock() {
        if (Config.getInstance().setKeyBlockStatus)
            new SplashScreen(display, getMainBarItem(), Config.getInstance().keyLock, false, true);
    }
}

class TimerTaskAutoAway extends Thread{
   
    private boolean stop;
    private boolean exit;
     
    private static TimerTaskAutoAway instance;
     
    private Roster rRoster;

    private int keyTimer=0;

    private int autoAwayTime=Config.getInstance().autoAwayTime*60;
    
    private TimerTaskAutoAway() {
        exit=false;
        start();
     }
    
    public static void startRotate(int max, Roster roster){
        if (instance==null) instance=new TimerTaskAutoAway();
        synchronized (instance) {
            instance.rRoster=roster;
        }
    }
    
     public void run() {
        while (true) {
            try {
                sleep(5000);
            } catch (Exception e) {}
            
            synchronized (this) {
                if (stop) {
                    break;
                }
                if (!rRoster.keyLockState) {
                    keyTimer=rRoster.keyTimer;
                    //here
                    //System.out.println("test "+keyTimer+" sec");
                    try {
                        if (Phone.PhoneManufacturer()==Phone.SIEMENS || Phone.PhoneManufacturer()==Phone.SIEMENS2) {
                            if (rRoster.lightType==2) {
                                if (getKeyLockState()) {
                                        rRoster.setLight(false);
                                        rRoster.siemensKeyLock();
                                } else {
                                        rRoster.setLight(true);
                                }
                            }
                        }

                        rRoster.setKeyTimer(keyTimer+5);                        
                        if (keyTimer>=autoAwayTime) {
                            //System.out.println("test4");
                            try {
                                rRoster.setAutoAway();
                                //System.out.println("test6");
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean getKeyLockState() {
        boolean lightState=(System.getProperty("MPJCKEYL").startsWith("1"))?true:false;
        return lightState;
    }
 } 