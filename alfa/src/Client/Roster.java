/*
 * Roster.java
 *
 * Created on 6 Январь 2005 г., 19:16
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */


package Client;

import Info.Version;
import archive.ArchiveList;
//import com.sun.pisces.LineSink;
import images.RosterIcons;
import locale.SR;
import login.LoginListener;
import login.NonSASLAuth;
import midlet.Damafon;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import ui.*;
import Client.Config;

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
        LoginListener
        //ContactEdit.StoreContact
        //Thread
{
    
    
    private Jid myJid;
    
    /**
     * The stream representing the connection to ther server
     */
    public Stream theStream ;
        
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

    private Command cmdUpdate=new Command("Update Roster", Command.SCREEN, 1); //locale
//    private Command cmdActions=new Command("Actions >", Command.SCREEN, 2); //locale
//    private Command cmdStatus=new Command(SR.MS_STATUS_MENU, Command.SCREEN, 3); //locale
//    private Command cmdActiveContact;//=new Command(SR.MS_ACTIVE_CONTACTS, Command.SCREEN, 3);
    private Command cmdAlert=new Command("Alert Profile", Command.SCREEN, 2); //locale
//    private Command cmdConference=new Command(SR.MS_CONFERENCE, Command.SCREEN, 10); //locale
    private Command cmdArchive=new Command(SR.MS_ARCHIVE, Command.SCREEN, 3); //locale
//    private Command cmdAdd=new Command(SR.MS_ADD_CONTACT, Command.SCREEN, 12); //locale
    private Command cmdTools=new Command(SR.MS_TOOLS, Command.SCREEN, 4);     //locale
    private Command cmdAccount=new Command("Account >", Command.SCREEN, 5); //locale
    private Command cmdLightOn=new Command("LightOn", Command.SCREEN, 6);
    private Command cmdLightOff=new Command("LightOFF", Command.SCREEN, 6);
    private Command cmdInfo=new Command(SR.MS_ABOUT, Command.SCREEN, 7); //locale
    private Command cmdMinimize=new Command("Minimize", Command.SCREEN, 8); //locale
    private Command cmdQuit=new Command("Quit", Command.SCREEN, 9); //locale
    
    private Config cf;
    private StaticData sd=StaticData.getInstance();

//#if (MOTOROLA_BACKLIGHT)
    private int blState=Integer.MAX_VALUE;

//#endif

//#if SASL
    private String token;

//#endif
    
    private long lastMessageTime=Time.localTime();

    private String myMessage;
    //public JabberBlockListener discoveryListener;
    
    /**
     * Creates a new instance of Roster
     * Sets up the stream to the server and adds this class as a listener
     */
    public Roster(Display display /*, boolean selAccount*/) {
        super();

	setProgress(24);
        //setTitleImages(StaticData.getInstance().rosterIcons);
                
        this.display=display;
        cf=Config.getInstance();

        Title title=new Title(4, null, null);
        setTitleItem(title);
        title.addRAlign();
        title.addElement(null);
        title.addElement(null);
        title.addElement(null);


        hContacts=new Vector();
        groups=new Groups();
        
        vContacts=new Vector(); // just for displaying
        
        addCommand(cmdUpdate);
        
        //int activeType=Command.SCREEN;
        //String platform=Version.getPlatformName();
        //if (platform.startsWith("Nokia")) activeType=Command.BACK;
        //if (platform.startsWith("Intent")) activeType=Command.BACK;
        
        //cmdActiveContact=new Command(SR.MS_ACTIVE_CONTACTS, activeType, 3);
        
        //addCommand(cmdStatus);
        //addCommand(cmdActions);
        //addCommand(cmdActiveContact);
        addCommand(cmdAlert);
        //addCommand(cmdAdd);
        //addCommand(cmdServiceDiscovery);
        //addCommand(cmdConference);
        setLight(true);
        //addCommand(cmdPrivacy);
        addCommand(cmdTools);
        addCommand(cmdArchive);
        addCommand(cmdInfo);
        addCommand(cmdAccount);

        addCommand(cmdQuit);
        
        addOptionCommands();
        setCommandListener(this);

	updateTitle();
        SplashScreen.getInstance().setExit(display, this);
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
        setRosterTitle(pgs);
        redraw();
    }
    public void setProgress(int percent){
        SplashScreen.getInstance().setProgress(percent);
        //redraw();
    }
    
    private void setRosterTitle(String s){
        getTitleItem().setElementAt(s, 3);
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
        Iq.setXmlLang("en");
        setQuerySign(true);
        setProgress(25);
	if (!reconnect) {
	    resetRoster();
	};
        setProgress(26);
        
        //logoff();
        try {
            Account a=sd.account;
            setProgress("Connect", 30); //locale
            //SR.loaded();
            theStream= a.openJabberStream();
            setProgress(SR.MS_OPENING_STREAM, 40); //locale
            //theStream.setJabberListener( this );
        } catch( Exception e ) {
            setProgress("Failed", 0); //locale
            reconnect=false;
            myStatus=Presence.PRESENCE_OFFLINE;
            e.printStackTrace();
            String error=e.getClass().getName()+"\n"+e.getMessage();
            errorLog( error );
            setQuerySign(false);
            redraw();
            //l.setTitleImgL(0);//offline
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
	//myJid=new Jid(sd.account.getJid());
	//updateContact(sd.account.getNickName(), myJid.getBareJid(), Groups.SELF_GROUP, "self", false); //???
	
	System.gc();
    }
    
    
    public void errorLog(String s){
        if (s==null) return;
        if (s.length()==0) return;
        /*
        Alert error=new Alert("Error", s, null, null);
        error.setTimeout(30000);
        error.addCommand(new Command("Ok", Command.BACK, 1));
        display.setCurrent(error, display.getCurrent());
         */
        Msg m=new Msg(Msg.MESSAGE_TYPE_OUT, "local", "Error", s);
        messageStore("0", m);
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
        getTitleItem().setElementAt(icon, 7);
        redraw();
    }
    
    public Object getEventIcon() {
        if (transferIcon!=null) return transferIcon;
        return messageIcon;
    }
    
    private void updateTitle(){
        int s=querysign?RosterIcons.ICON_PROGRESS_INDEX:myStatus;
        int profile=cf.profile;//StaticData.getInstance().config.profile;
        Object en=(profile>1)? new Integer(profile+RosterIcons.ICON_PROFILE_INDEX):null;
        Title title=(Title) getTitleItem();
        title.setElementAt(new Integer(s), 2);
        title.setElementAt(en, 5);
        if (messageCount==0) {
            messageIcon=null;
            title.setElementAt(null,1);
        } else {
            messageIcon=new Integer(RosterIcons.ICON_MESSAGE_INDEX);
            title.setElementAt(" "+messageCount+" ",1);
        }
        title.setElementAt(messageIcon, 0);
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

        updateTitle();
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

        //int gi=g.index;

        int index=0;

        int onlineContacts=0;
        
        synchronized (hContacts) {
            while (index<hContacts.size()) {
                Contact contact=(Contact)hContacts.elementAt(index);
                if (contact.inGroup(g)) {
                    if ( contact.origin>Contact.ORIGIN_ROSTERRES
                         && contact.status==Presence.PRESENCE_OFFLINE
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
    
    public final void updateContact(final String nick, final String jid, final String grpName, String subscr, boolean ask, final String newStatus) {
        // called only on roster read
        int status;
        if (newStatus.equals("1")) {
            status=Presence.PRESENCE_ONLINE;    
            System.out.println(status);
        } else {
            status=Presence.PRESENCE_OFFLINE;
        }
            
        Jid J=new Jid(jid);
        Contact c=findContact(J,false); // пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ bare jid
        if (c==null) {
            c=new Contact(nick, jid, status, null);
            addContact(c);
        }
        for (Enumeration e=hContacts.elements();e.hasMoreElements();) {
            c=(Contact)e.nextElement();
            if (c.jid.equals(J)) {
                Group group= groups.getGroup(grpName);
                if (group==null) {
                    group=groups.addGroup(grpName, true);
                }
                c.nick=nick;
                c.setGroup(group);
                c.subscr=subscr;
                c.offline_type=status;
                c.ask_subscribe=ask;
                c.status=status;
                
                //Group g=c.getGroup();
                //g.collapsed=true;     

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
	    c.bareJid=J.getId();
            c.origin=Contact.ORIGIN_PRESENCE;
            c.setGroup(groups.getGroup(Groups.TYPE_NOT_IN_LIST));
            addContact(c);
        } else {
            // здесь jid с новым ресурсом
            if (c.origin==Contact.ORIGIN_ROSTER) {
                c.origin=Contact.ORIGIN_ROSTERRES;
                c.status=Presence.PRESENCE_OFFLINE;
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
                if (c.jid.equals(j)) return c;
            }
        }
        return null;
    }
    
    /**
     * Method to inform the server we are now online
     */
    
    public void sendPresence(int status) {
        myStatus=status;
        setQuerySign(false);
        if (myStatus==Presence.PRESENCE_OFFLINE) {
            synchronized(hContacts) {
                for (Enumeration e=hContacts.elements(); e.hasMoreElements();){
                    Contact c=(Contact)e.nextElement();
                    c.status=Presence.PRESENCE_OFFLINE; // keep error & unknown
                }
            }
        }
        //Vector v=sd.statusList;//StaticData.getInstance().statusList;
        //ExtendedStatus es=null;
        
        // reconnect if disconnected        
        if (myStatus!=Presence.PRESENCE_OFFLINE && theStream==null ) {
            reconnect=(hContacts.size()>1);
            redraw();
            
            new Thread(this).start();
            return;
        }
        
        // send presence
        ExtendedStatus es= StatusList.getInstance().getStatus(myStatus);
        Presence presence = new Presence(myStatus, es.getPriority(), es.getMessage());
        if (theStream!=null) {
            // disconnect
            if (status==Presence.PRESENCE_OFFLINE) {
                try {
                    theStream.close();
                } catch (Exception e) { e.printStackTrace(); }
                theStream=null;
                System.gc();
            }
        }
        Contact c=selfContact();
        c.status=myStatus;
        sort(hContacts);
        
        reEnumRoster();
    }
    
    public Contact selfContact() {
	return getContact(myJid.getId(), true);
    }

    /**
     * Method to send a message to the specified recipient
     */
    
    public void sendMessage(Contact to, final String body, final String subject , int composingState) {
        boolean groupchat=to.origin==Contact.ORIGIN_GROUPCHAT;
        Message message = new Message( 
                to.getJid(), 
                body, 
                subject, 
                groupchat 
        );
        if (groupchat && body==null /*&& subject==null*/) return;
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
        //theStream.send( message );
        lastMessageTime=Time.localTime();
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
        
        reconnect=false;
        setQuerySign(false);
        redraw();
    }
    
    public void loginSuccess() {
        // залогинились. теперь, если был реконнект, то просто пошлём статус
        if (reconnect) {
            querysign=reconnect=false;
            //sendPresence(myStatus);
            sendPresence(cf.loginstatus);
            return;
        }
        
        // иначе будем читать ростер
        theStream.enableRosterNotify(true);
        rpercent=60;
        
        JabberDataBlock qr=new IqQueryRoster();
        setProgress("Roster request", 60); //locale
        //theStream.send( qr );
    }
    
    public void blockArrived( JabberDataBlock data ) { // тут листенер потока!!!
        try {

        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
/*    
    void processRoster(String data){
        //updateContact(name,jid,group, subscr, ask);
        sort(hContacts);
    }
*/    
    
    public void messageStore(Contact c, Msg message) {
        if (c==null) return;  
        c.addMessage(message);
        
        if (cf.ghostMotor) System.gc(); 

        if (!message.unread) return;
        //TODO: clear unread flag if not-in-list IS HIDDEN
        
        if (countNewMsgs()) reEnumRoster();
        
        if (c.getGroupType()==Groups.TYPE_IGNORE) return;    // no signalling/focus on ignore
        
	if (cf.popupFromMinimized)
	    Damafon.getInstance().hideApp(false);
	
        if (cf.autoFocus) focusToContact(c, false);

        if (message.messageType!=Msg.MESSAGE_TYPE_HISTORY) 
            playNotify(0);
    }
    
    public void messageStore(String id, Msg message) {
        if (id==null) return;
        //Jid J=new Jid(id);
        Contact c=getContact(id,true);
        
        if (c==null) return;  
        c.addMessage(message);
        
        if (cf.ghostMotor) System.gc(); 

        if (!message.unread) return;
        //TODO: clear unread flag if not-in-list IS HIDDEN
        
        if (countNewMsgs()) reEnumRoster();
        
        if (c.getGroupType()==Groups.TYPE_IGNORE) return;    // no signalling/focus on ignore
        
	if (cf.popupFromMinimized)
	    Damafon.getInstance().hideApp(false);
	
        if (cf.autoFocus) focusToContact(c, false);

        if (message.messageType!=Msg.MESSAGE_TYPE_HISTORY) 
            playNotify(0);
    }
    
    public void playNotify(int event) {
        Config cf=Config.getInstance();
        int profile=cf.profile;
        if (profile==AlertProfile.AUTO) profile=AlertProfile.ALL;
        
        EventNotify notify=null;
        
        boolean blFlashEn=cf.blFlash;   // motorola e398 backlight bug
        
        switch (profile) {
            case AlertProfile.ALL:   notify=new EventNotify(display, true, cf.vibraLen, blFlashEn); break;
            case AlertProfile.NONE:  notify=new EventNotify(display, false,    0,           false    ); break;
            case AlertProfile.VIBRA: notify=new EventNotify(display, false,    cf.vibraLen, blFlashEn); break;
            case AlertProfile.SOUND: notify=new EventNotify(display, true, 0,           blFlashEn); break;
        }
        if (notify!=null) notify.startNotify();
    }
    
    
    Contact messageStore(Msg message){
        Contact c=getContact(message.from, true);
        if (c.getGroupType()==Groups.TYPE_NOT_IN_LIST) 
            if (!cf.notInList) return c;

        messageStore(c, message);
        return c;
    }

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
        setProgress(SR.MS_LOGINPGS, 42);
        
        new NonSASLAuth(sd.account, SessionId, this, theStream);
    }
    
    /**
     * If the connection is terminated then print a message
     *
     * @e The exception that caused the connection to be terminated, Note that
     *  receiving a SocketException is normal when the client closes the stream.
     */
    public void connectionTerminated( Exception e ) {
        //l.setTitleImgL(0);
        //System.out.println( "Connection terminated" );
        if( e != null ) {
            String error=e.getClass().getName()+"\n"+e.getMessage();
            errorLog(error);
            e.printStackTrace();
        }
        setProgress(SR.MS_DISCONNECTED, 0);
        try {
            sendPresence(Presence.PRESENCE_OFFLINE);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        redraw();
    }
    
    //private VList l;
    //private IconTextList l;
    
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
//#if (MOTOROLA_BACKLIGHT) && !(MIDP1)
        if (cf.ghostMotor) {
            // backlight management
            if (keyCode=='*') blState=(blState==1)? Integer.MAX_VALUE : 1;
            else blState=Integer.MAX_VALUE;
            
            display.flashBacklight(blState);
        }
//#endif
    }
    

    public void userKeyPressed(int keyCode){
        if (keyCode==KEY_NUM0 /* || keyCode==MOTOE680_REALPLAYER  CONFLICT WITH ALCATEL. (platform=J2ME)*/) {
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
        }

        if (keyCode=='3') searchGroup(-1);
	if (keyCode=='9') searchGroup(1);
    }
    
    public void logoff(){
        if (theStream!=null)
        try {
             //sendPresence(Presence.PRESENCE_OFFLINE);
            /*
81.176.79.141:80  
POST /auth.phtml?act=logout HTTP/1.0
Content-Type: application/x-www-form-urlencoded
User-Agent: Damafon 2.1.12.4000
Pragma: no-cache
Host: damochka.ru:80
Content-Length: 12
Cookie: SITEID=24825b1ae9ad72a2f5314d70448a0d08; auth2_login=adeen; lastUpdate=1166961844; auth2_clean: ok-1166961769; auth2_pwd: 0%3A2c3bae7a8869af158d85bc766d1e323635b7; VIPID: ; news_last=2; auth2_logged=1; auth2_save=1; meet_nr=1; hotlog=1

redirect=%2F
            */
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    };

   
    public void commandAction(Command c, Displayable d){
        if (c==cmdUpdate) {
            updateRoster(null);
            return;
        }
        
        if (c==cmdQuit) {
            destroyView();
            logoff();
            //StaticData sd=StaticData.getInstance();
            //cf.saveToStorage();
	    Damafon.getInstance().notifyDestroyed();
            return;
        }
        if (c==cmdMinimize) { Damafon.getInstance().hideApp(true);  }
        
        //if (c==cmdActiveContact) { new ActiveContacts(display, null); }
        
        if (c==cmdAccount){ new AccountSelect(display, false); }
        //if (c==cmdStatus) { new StatusSelect(display, null); }
        if (c==cmdAlert) { new AlertProfile(display); }
        if (c==cmdArchive) { new ArchiveList(display, null); }
        if (c==cmdInfo) { new Info.InfoWindow(display); }
        
        if (c==cmdLightOn) { setLight(true); }
        if (c==cmdLightOff) { setLight(false); }
        
        
        if (c==cmdTools) { new RosterToolsMenu(display); }
        // stream-sensitive commands
        // check for closed socket
        if (StaticData.getInstance().roster.theStream==null) return;
        /*
        if (c==cmdActions) try { 
            new RosterItemActions(display, getFocusedObject()); 
        } catch (Exception e) { }
        
            
        if (c==cmdAdd) {
            Object o=getFocusedObject();
            Contact cn=null;
            if (o instanceof Contact) {
                cn=(Contact)o;
                if (cn.getGroupType()!=Groups.TYPE_NOT_IN_LIST && cn.getGroupType()!=Groups.TYPE_SEARCH_RESULT) cn=null;
            }
            new ContactEdit(display, cn);
        }
        */
    }
    

    protected void showNotify() { super.showNotify(); countNewMsgs(); }
    
    
    protected void keyRepeated(int keyCode) {
        super.keyRepeated(keyCode);
        if (kHold==keyCode) return;
        //kHold=keyCode;
        kHold=keyCode;
//#if (!SMALL)
        if (keyCode==cf.keyLock) 
            new KeyBlock(display, getTitleItem(), cf.keyLock, cf.ghostMotor); 
//#endif
        if (keyCode==cf.keyVibra || keyCode==MOTOE680_FMRADIO /* TODO: redefine keyVibra*/) {
            // swap profiles
            int profile=cf.profile;
            cf.profile=(profile==AlertProfile.VIBRA)? 
                cf.lastProfile : AlertProfile.VIBRA;
            cf.lastProfile=profile;
            
            updateTitle();
            redraw();
        }
        
        if (keyCode==cf.keyOfflines /* || keyCode==MOTOE680_REALPLAYER CONFLICT WITH ALCATEL. (platform=J2ME) 
         TODO: redifine keyOfflines*/) {
            cf.showOfflineContacts=!cf.showOfflineContacts;
            reEnumRoster();
        }

       	if (keyCode==KEY_NUM3) new ActiveContacts(display, null);

        if (keyCode==cf.keyHide && cf.allowMinimize) {
            Damafon.getInstance().hideApp(true);
        }
    }

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

    public void deleteContact(Contact c) {
	for (Enumeration e=hContacts.elements();e.hasMoreElements();) {
	    Contact c2=(Contact)e. nextElement();
	    if (c.jid.equals(c2.jid)) {
		c2.status=c2.offline_type=Presence.PRESENCE_TRASH;
	    }
	}
	
	if (c.getGroupType()==Groups.TYPE_NOT_IN_LIST) {
	    hContacts.removeElement(c);
            countNewMsgs();
	    reEnumRoster();
	}// else
	    //theStream.send(new IqQueryRoster(c.getBareJid(),null,null,"remove")); // тут обновляем ростер!
    }
   
    
    public void setQuerySign(boolean requestState) {
        querysign=requestState;
        updateTitle();
    }
    /**
     * store cotnact on server
     */
    public void storeContact(String jid, String name, String group, boolean askSubscribe){
        
       // theStream.send(new IqQueryRoster(jid, name, group, null)); добавляем контакт
        //if (askSubscribe) theStream.send(new Presence(jid,"subscribe"));
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

                    // search result
                    //if (groups.getGroup(Groups.SRC_RESULT_INDEX).tncontacts>0)
                    groups.addToVector(tContacts, Groups.TYPE_SEARCH_RESULT);
                    
                    vContacts=tContacts;
                    
                    String time=Time.timeString(Time.localTime());
                    
                    setRosterTitle("("+groups.getRosterOnline()+"/"+groups.getRosterContacts()+") "+time);
                    
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
            } catch (Exception e) {e.printStackTrace();}
            thread=null;
        }
    }
    private void setLight(boolean state) {
        if (Version.getPlatformName().indexOf("SIE-S75") > -1) {
            if (state){
                com.siemens.mp.game.Light.setLightOn();
            } else { 
                com.siemens.mp.game.Light.setLightOff();
            }
           removeCommand(state? cmdLightOn: cmdLightOff);
            addCommand(state? cmdLightOff: cmdLightOn);
        }
    }

    public void updateRoster(String RosterContacts) {
        theStream.enableRosterNotify(false);
        
        processRoster(RosterContacts);

        setProgress("Connected",100);
        reEnumRoster();
        querysign=reconnect=false;

        SplashScreen.getInstance().close(); // display.setCurrent(this);
    }
    
    void processRoster(String RosterContacts){
        int type=0;
        
        if (RosterContacts!=null) {
            
            try {
                while (RosterContacts.indexOf("\r\n")>-1) {
                    String line=RosterContacts.substring(0,RosterContacts.indexOf("\r\n"));
                    RosterContacts=RosterContacts.substring(RosterContacts.indexOf("\r\n")+2,RosterContacts.length());
                    
                    Vector ContactItem=new Vector();
                    ContactItem=RosterParser(line);
                    
                    for (Enumeration e=ContactItem.elements(); e.hasMoreElements();){

                            String id=(String)e.nextElement().toString().trim();
                            String sex=(String)e.nextElement().toString().trim();
                            String status=(String)e.nextElement().toString().trim();
                            String client=(String)e.nextElement().toString().trim();
                            String name=(String)e.nextElement().toString().trim();
                            boolean ask= false;

                            String group="General";

                            updateContact(name,id,group, "both", ask, status);
                            sort(hContacts);            
                    }
                }
            } catch (Exception e) {}
        }
    }
    
    public Vector RosterParser(String data) {
	Vector v = new Vector();
        int cnt=0;
        int pos=0;
        int pos2=0;
        
	try {
            while (cnt<5) {
                if (cnt<4) {
                    pos2=data.indexOf(" ",pos)+1;
                    String line=data.substring(pos,pos2);
                    pos=pos2;
                    v.addElement(line);
                } else {
                    String line=data.substring(pos,data.length());
                    v.addElement(line);
                }
                cnt++;
            }
        } catch (Exception e)	{ }
	return v;
    }
    
}

