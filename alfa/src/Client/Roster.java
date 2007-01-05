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
import Network.Presence;
import Network.Stream;
import archive.ArchiveList;
import images.RosterIcons;
import locale.SR;
import login.LoginListener;
import midlet.Damafon;
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
    private Command cmdArchive=new Command(SR.MS_ARCHIVE, Command.SCREEN, 3); //locale
    private Command cmdOptions=new Command(SR.MS_OPTIONS, Command.SCREEN, 4);     //locale
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
        
        setLight(true);
        addCommand(cmdOptions);
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
        Msg m=new Msg(Msg.MESSAGE_TYPE_OUT, "local", "Debug", s);
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
        Title title=(Title) getTitleItem();
        title.setElementAt(new Integer(s), 2);
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
                index++;
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
    
    public final void updateContact(final String nick, final String jid, final String grpName, String subscr, boolean ask, final String newStatus, Integer client) {
        // called only on roster read
        int status;
        if (newStatus.equals("1")) {
            status=Presence.PRESENCE_ONLINE;    
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
                c.client=client;
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
    
    public Contact selfContact() {
	return getContact(myJid.getId(), true);
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
            return;
        }
        
        // иначе будем читать ростер
        theStream.enableRosterNotify(true);
        rpercent=60;

        setProgress("Roster request", 60); //locale
        //theStream.send( qr );
    }

    
    public void messageStore(Contact c, Msg message) {
        if (c==null) return;  
        c.addMessage(message);
        
        if (cf.ghostMotor) System.gc(); 

        if (!message.unread) return;
        //TODO: clear unread flag if not-in-list IS HIDDEN
        
        if (countNewMsgs()) reEnumRoster();
        
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
        
	if (cf.popupFromMinimized)
	    Damafon.getInstance().hideApp(false);
	
        if (cf.autoFocus) focusToContact(c, false);

        if (message.messageType!=Msg.MESSAGE_TYPE_HISTORY) 
            playNotify(0);
    }
    
    public void playNotify(int event) {
        Config cf=Config.getInstance();
        
        EventNotify notify=null;
        
        boolean blFlashEn=false;   // motorola e398 backlight bug
        
        notify=new EventNotify(display, true, cf.vibraLen, blFlashEn); 

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
            theStream.logOut();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    };

   
    public void commandAction(Command c, Displayable d){
        if (c==cmdUpdate) {
            try {
                theStream.getRoster();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
        if (c==cmdArchive) { new ArchiveList(display, null); }
        if (c==cmdInfo) { new Info.InfoWindow(display); }
        
        if (c==cmdLightOn) { setLight(true); }
        if (c==cmdLightOff) { setLight(false); }
        
        
        if (c==cmdOptions) { new ConfigForm(display); }
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
                    if (selfContactGroup.tonlines>1 || selfContactGroup.unreadMessages>0 )
                        groups.addToVector(tContacts, Groups.TYPE_SELF);
                    // adding groups
                    for (i=Groups.TYPE_COMMON;i<groups.getCount();i++)
                        groups.addToVector(tContacts,i);
                    // not-in-list
                    if (cf.notInList) groups.addToVector(tContacts,Groups.TYPE_NOT_IN_LIST);
                    
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
                            Integer client=(Integer)e.nextElement();
                            String name=(String)e.nextElement().toString().trim();
                            boolean ask= false;

                            String group="General";

                            updateContact(name,id,group, "both", ask, status, client);
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

