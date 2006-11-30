/*
 * ConfigForm.java
 *
 * Created on 2 Май 2005 г., 18:19
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
//#if FILE_IO
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.controls.NumberField;
import util.StringLoader;
import ui.*;

/**
 *
 * @author Evg_S
 */

/*
 * roster elements:
 *  [] self-contact
 *  [] offline contacts
 *  [] transports
 *  [] hidden group
 *  [] not-in-list
 *  [] clock
 *
 * message
 *  [] show smiles
 *  [] history
 *  [] composing
 *
 * startup actions
 *  [] login
 *  [] Join conferences
 *
 * application
 *  [] fullscreen
 */

public class ConfigForm implements
	CommandListener 
	,ItemCommandListener
//#if (FILE_IO)
        , BrowserListener
//#endif
{
    private Display display;
    private Displayable parentView;

    Form f;
    ChoiceGroup roster;
    ChoiceGroup message;
    ChoiceGroup startup;
    ChoiceGroup application;

    ChoiceGroup lang;
    
    ChoiceGroup font1;
    ChoiceGroup font2;
    
    ChoiceGroup textWrap;
    
    NumberField keepAlive;
    NumberField fieldLoc;
    NumberField fieldGmt;
    
    ChoiceGroup history;
    TextField historyFolder;
    
    ChoiceGroup autoaway;
    NumberField autoAwayTime;
    
    ChoiceGroup SkinFile;
    private Vector[] Skinfiles;

    Command cmdOk=new Command(SR.MS_OK,Command.OK,1);
    Command cmdLoadSkin=new Command(SR.MS_LOAD_SKIN, Command.ITEM,11);
    Command cmdSetHistFolder=new Command(SR.MS_SELECT_HISTORY_FOLDER, Command.ITEM,12);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    
    Config cf;
    boolean ra[];
    boolean mv[];
    boolean ap[];
    boolean su[];
    boolean his[];
    boolean aa[];
    Vector files[];

    private Colors cl;
    
    /** Creates a new instance of ConfigForm */
    public ConfigForm(Display display) {
        this.display=display;
        parentView=display.getCurrent();
        
        cf=Config.getInstance();
        
        f=new Form(SR.MS_OPTIONS);
        roster=new ChoiceGroup(SR.MS_ROSTER_ELEMENTS, Choice.MULTIPLE);
        roster.append(SR.MS_OFFLINE_CONTACTS, null);
        roster.append(SR.MS_SELF_CONTACT, null);
        roster.append(SR.MS_TRANSPORTS, null);
        roster.append(SR.MS_IGNORE_LIST, null);
        roster.append(SR.MS_NOT_IN_LIST, null);
        roster.append(SR.MS_AUTOFOCUS,null);
        
        boolean ra[]={
            cf.showOfflineContacts,
            cf.selfContact,
            cf.showTransports, 
            cf.ignore, 
            cf.notInList,
            cf.autoFocus
        };
        this.ra=ra;
        //ra[5]=false;
        roster.setSelectedFlags(ra);

        message=new ChoiceGroup(SR.MS_MESSAGES, Choice.MULTIPLE);
        message.append(SR.MS_SMILES, null);
        message.append(SR.MS_STORE_PRESENCE,null);        
        message.append(SR.MS_COMPOSING_EVENTS, null);
        message.append(SR.MS_CAPS_STATE, null);
        message.append(SR.CLASSIC_CHAT, null);

        boolean mv[]={
            cf.smiles,
            cf.storeConfPresence,
            cf.eventComposing,
            cf.capsState,
            cf.altInput
        };
        this.mv=mv;
        
        message.setSelectedFlags(mv);

	startup=new ChoiceGroup(SR.MS_STARTUP_ACTIONS, Choice.MULTIPLE);
        startup.append(SR.MS_AUTOLOGIN, null);
        startup.append(SR.MS_AUTO_CONFERENCES,null);
        su=new boolean[2];
        su[0]=cf.autoLogin;
        su[1]=cf.autoJoinConferences;
        startup.setSelectedFlags(su);
        
        ap=new boolean[5];
	int apctr=0;
        application=new ChoiceGroup(SR.MS_APPLICATION, Choice.MULTIPLE);
        ap[apctr++]=cf.fullscreen;
        application.append(SR.MS_FULLSCREEN,null);
        application.append(SR.MS_HEAP_MONITOR,null);
        application.append(SR.MS_NEW_MENU,null);
	if (!cf.ghostMotor) application.append(SR.MS_FLASHBACKLIGHT,null);
	if (cf.allowMinimize) application.append(SR.MS_ENABLE_POPUP,null);
        
	ap[apctr++]=cf.memMonitor;
        ap[apctr++]=cf.digitMemMonitor;
	ap[apctr++]=cf.blFlash;
	ap[apctr++]=cf.popupFromMinimized;
	
        application.setSelectedFlags(ap);
        
	keepAlive=new NumberField(SR.MS_KEEPALIVE_PERIOD, cf.keepAlive, 10, 10000 );
	fieldGmt=new NumberField(SR.MS_GMT_OFFSET, cf.gmtOffset, -12, 12); 
        fieldLoc=new NumberField(SR.MS_CLOCK_OFFSET, cf.locOffset, -12, 12 );

        String fnts[]={"Normal", "Small", "Large"};
        font1=new ChoiceGroup(SR.MS_ROSTER_FONT, ConstMIDP.CHOICE_POPUP, fnts, null);
        font2=new ChoiceGroup(SR.MS_MESSAGE_FONT, ConstMIDP.CHOICE_POPUP, fnts, null);
        font1.setSelectedIndex(cf.font1/8, true);
        font2.setSelectedIndex(cf.font2/8, true);

        f.append(roster);
        f.append(font1);

        f.append(message);
        f.append(font2);
	
	String textWraps[]={SR.MS_TEXTWRAP_CHARACTER, SR.MS_TEXTWRAP_WORD};
	textWrap=new ChoiceGroup(SR.MS_TEXTWRAP, ConstMIDP.CHOICE_POPUP, textWraps,null);
	textWrap.setSelectedIndex(cf.textWrap, true);
	f.append(textWrap);
        
        
        lang=new ChoiceGroup(SR.MS_LANGUAGE, ConstMIDP.CHOICE_POPUP);
	Vector langs[]=new StringLoader().stringLoader("/lang/res.txt",2);
	
	for (Enumeration f=langs[1].elements(); f.hasMoreElements(); ) {
	    lang.append( (String)f.nextElement(), null );
	}
	
        try {
            lang.setSelectedIndex(cf.lang, true);
        } catch (Exception e) { cf.lang=0; }
        
        f.append(startup);

	f.append(application);

	f.append(keepAlive);
	
        f.append(SR.MS_TIME_SETTINGS);
        f.append("\n");
        
        f.append(fieldGmt);
        f.append(fieldLoc);
        
        f.append(lang);
        
//#if FILE_IO
        history=new ChoiceGroup(SR.MS_HISTORY, Choice.MULTIPLE); //locale
        history.append(SR.MS_SAVE_HISTORY, null); //locale
        history.append(SR.MS_SAVE_PRESENCES,null);    //locale     
        history.append(SR.MS_SAVE_HISTORY_CONF, null); //locale
        history.append(SR.MS_SAVE_PRESENCES_CONF, null); //locale
        history.append(SR.MS_1251_CORRECTION, null); //locale
        
        boolean his[]={
            cf.msgLog,
            cf.msgLogPresence,
            cf.msgLogConf,
            cf.msgLogConfPresence,
            cf.cp1251
        };
        this.his=his;
        
        history.setSelectedFlags(his);
        f.append(history);
        
        
        historyFolder=new TextField(SR.MS_HISTORY_FOLDER, null, 200, TextField.ANY);
        historyFolder.setString(cf.msgPath);
        historyFolder.addCommand(cmdSetHistFolder);
        f.append(historyFolder);
        historyFolder.setItemCommandListener(this);
//#endif
        //autostatus
        autoaway=new ChoiceGroup(SR.MS_SET, Choice.MULTIPLE);
        autoaway.append(SR.MS_AUTOSTATUS, null);
        
        boolean aa[]={
            cf.setAutoStatus,
        };
        this.aa=aa;
        autoaway.setSelectedFlags(aa);
        f.append(autoaway);
        
        autoAwayTime=new NumberField(SR.MS_AUTOSTATUS_TIME, cf.autoAwayTime, -12, 12);
        f.append(autoAwayTime);
        //autostatus
                
        SkinFile=new ChoiceGroup(SR.MS_LOAD_SKIN, ConstMIDP.CHOICE_POPUP);
	Skinfiles=new StringLoader().stringLoader("/skins/res.txt",2);
	for (Enumeration f=Skinfiles[1].elements(); f.hasMoreElements(); ) {
	    SkinFile.append( (String)f.nextElement(), null );
	}
	SkinFile.setSelectedIndex(0, true);
	f.append(SkinFile);
	SkinFile.addCommand(cmdLoadSkin);
	SkinFile.setItemCommandListener(this);
        
        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        
        f.setCommandListener(this);
	//f.setItemStateListener(this);
        
        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            roster.getSelectedFlags(ra);
            message.getSelectedFlags(mv);
            application.getSelectedFlags(ap);
	    startup.getSelectedFlags(su);
//#if FILE_IO
            history.getSelectedFlags(his);
//#endif
            autoaway.getSelectedFlags(aa);
	    
            cf.showOfflineContacts=ra[0];
            cf.selfContact=ra[1];
            cf.showTransports=ra[2];
            cf.ignore=ra[3];
            cf.notInList=ra[4];
            cf.autoFocus=ra[5];

            cf.smiles=mv[0];
            cf.storeConfPresence=mv[1];
            cf.eventComposing=mv[2];
            cf.capsState=mv[3];
            cf.altInput=mv[4];

	    cf.autoLogin=su[0];
	    cf.autoJoinConferences=su[1];
            
	    int apctr=0;

            VirtualList.fullscreen=cf.fullscreen=ap[apctr++];
            StaticData.getInstance().roster.setFullScreenMode(cf.fullscreen);

	    VirtualList.memMonitor=cf.memMonitor=ap[apctr++];
            VirtualList.digitMemMonitor=cf.digitMemMonitor=ap[apctr++];   
            
	    cf.blFlash=ap[apctr++];
	    cf.popupFromMinimized=ap[apctr++];
            
	    cf.gmtOffset=fieldGmt.getValue();
	    cf.locOffset=fieldLoc.getValue();
	    cf.keepAlive=keepAlive.getValue();
            
            FontCache.rosterFontSize=cf.font1=font1.getSelectedIndex()*8;
            FontCache.msgFontSize=cf.font2=font2.getSelectedIndex()*8;
            FontCache.resetCache();
	    
	    cf.textWrap=textWrap.getSelectedIndex();

            cf.lang=lang.getSelectedIndex();
	    
//#if FILE_IO
            cf.msgLog=his[0];
            cf.msgLogPresence=his[1];
            cf.msgLogConf=his[2];
            cf.msgLogConfPresence=his[3];
            cf.cp1251=his[4];
            
            cf.msgPath=historyFolder.getString();
//#endif             
            cf.setAutoStatus=aa[0];
            cf.autoAwayTime=autoAwayTime.getValue();

            
            cf.updateTime();
            
            cf.saveToStorage();
            
            StaticData.getInstance().roster.reEnumRoster();
            destroyView();
        }
        if (c==cmdCancel) destroyView();
    }

    public void commandAction(Command command, Item item) {
	if (command==cmdLoadSkin) {
            loadSkin();
	}
//#if (FILE_IO)
        if (command==cmdSetHistFolder) {
           new Browser(null, display, this, true);
        }
//#endif
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
        ((Canvas)parentView).setFullScreenMode(cf.fullscreen);
    }

    
    public void BrowserFilePathNotify(String pathSelected) {
        historyFolder.setString(pathSelected);  
    }
    
    private static Hashtable skin;

    public void loadSkin(){
            skin=null;
            int skinfl=SkinFile.getSelectedIndex();
            String skinFile=(String)Skinfiles[0].elementAt(skinfl);
            
            cl=Colors.getInstance();
            cl.BALLOON_INK=loadInt(skinFile, "BALLOON_INK");
            cl.BALLOON_BGND=loadInt(skinFile, "BALLOON_BGND");
            cl.LIST_BGND=loadInt(skinFile, "LIST_BGND");
            cl.LIST_BGND_EVEN=loadInt(skinFile, "LIST_BGND_EVEN");
            cl.LIST_INK=loadInt(skinFile, "LIST_INK");
            cl.MSG_SUBJ=loadInt(skinFile, "MSG_SUBJ");
            cl.MSG_HIGHLIGHT=loadInt(skinFile, "MSG_HIGHLIGHT");
            cl.DISCO_CMD=loadInt(skinFile, "DISCO_CMD");
            cl.BAR_BGND=loadInt(skinFile, "BAR_BGND");
            cl.BAR_INK=loadInt(skinFile, "BAR_INK");
            cl.CONTACT_DEFAULT=loadInt(skinFile, "CONTACT_DEFAULT");
            cl.CONTACT_CHAT=loadInt(skinFile, "CONTACT_CHAT");
            cl.CONTACT_AWAY=loadInt(skinFile, "CONTACT_AWAY");
            cl.CONTACT_XA=loadInt(skinFile, "CONTACT_XA");
            cl.CONTACT_DND=loadInt(skinFile, "CONTACT_DND");
            cl.GROUP_INK=loadInt(skinFile, "GROUP_INK");
            cl.BLK_INK=loadInt(skinFile, "BLK_INK");
            cl.BLK_BGND=loadInt(skinFile, "BLK_BGND");
            cl.MESSAGE_IN=loadInt(skinFile, "MESSAGE_IN");
            cl.MESSAGE_OUT=loadInt(skinFile, "MESSAGE_OUT");
            cl.MESSAGE_PRESENCE=loadInt(skinFile, "MESSAGE_PRESENCE");
            cl.MESSAGE_AUTH=loadInt(skinFile, "MESSAGE_AUTH");
            cl.MESSAGE_HISTORY=loadInt(skinFile, "MESSAGE_HISTORY");
            cl.PGS_REMAINED=loadInt(skinFile, "PGS_REMAINED");
            cl.PGS_COMPLETE=loadInt(skinFile, "PGS_COMPLETE");
            cl.PGS_BORDER=loadInt(skinFile, "PGS_BORDER");
            cl.PGS_BGND=loadInt(skinFile, "PGS_BGND");
            cl.HEAP_TOTAL=loadInt(skinFile, "HEAP_TOTAL");
            cl.HEAP_FREE=loadInt(skinFile, "HEAP_FREE");
            cl.CURSOR_BGND=loadInt(skinFile, "CURSOR_BGND");
            cl.CURSOR_OUTLINE=loadInt(skinFile, "CURSOR_OUTLINE");
            cl.SCROLL_BRD=loadInt(skinFile, "SCROLL_BRD");
            cl.SCROLL_BAR=loadInt(skinFile, "SCROLL_BAR");
            cl.SCROLL_BGND=loadInt(skinFile, "SCROLL_BGND");
            cl.saveToStorage();
            skin=null;
    }
    
    private static int loadInt(String skinFile,String key) {
        if (skin==null) {
            skin=new StringLoader().hashtableLoader(skinFile);
        }
        try {
            String value=(String)skin.get(key);
            return Integer.parseInt(value.substring(2),16);
        } catch (Exception e) {
            System.out.println(e);
            return 0xFF0000;
        }
    }
}
