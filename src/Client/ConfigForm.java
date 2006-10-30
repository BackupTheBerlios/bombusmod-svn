/*
 * ConfigForm.java
 *
 * Created on 2 Май 2005 г., 18:19
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import java.util.Enumeration;
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
//#if !(MIDP1)
	,ItemCommandListener
//#endif
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
    
    ChoiceGroup font1;
    ChoiceGroup font2;
    
    ChoiceGroup textWrap;
    
    NumberField keepAlive;
    NumberField fieldLoc;
    NumberField fieldGmt;
    
    ChoiceGroup history;
    TextField historyFolder;
    
    Command cmdOk=new Command("Ok",Command.OK,1); //locale
    //Command cmdSign=new Command("- (Sign)",Command.ITEM,2);
    Command cmdSetHistFolder=new Command("Select History", Command.ITEM,12);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99); //locale
    
    Config cf;
    boolean ra[];
    boolean mv[];
    boolean ap[];
    boolean su[];
    boolean his[];
    
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
        roster.append("Ignore-List", null);
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

        message=new ChoiceGroup("Messages", Choice.MULTIPLE); //locale
        message.append(SR.MS_STORE_PRESENCE,null);    //locale     
        message.append(SR.MS_COMPOSING_EVENTS, null); //locale
//#if (!MIDP1)
        message.append(SR.MS_CAPS_STATE, null); //locale
//#endif
        
        boolean mv[]={
            cf.storeConfPresence,
            cf.eventComposing
//#if (!MIDP1)
            ,cf.capsState
//#endif
        };
        this.mv=mv;
        
        message.setSelectedFlags(mv);

	startup=new ChoiceGroup(SR.MS_STARTUP_ACTIONS, Choice.MULTIPLE); //locale
        startup.append(SR.MS_AUTOLOGIN, null); //locale
        startup.append(SR.MS_AUTO_CONFERENCES,null); //locale
        su=new boolean[2];
        su[0]=cf.autoLogin;
        su[1]=cf.autoJoinConferences;
        startup.setSelectedFlags(su);
        
        ap=new boolean[4];
	int apctr=0;
        application=new ChoiceGroup(SR.MS_APPLICATION, Choice.MULTIPLE); //locale
//#if !(MIDP1)
        ap[apctr++]=cf.fullscreen;
        application.append("fullscreen",null); //locale
//#endif
        application.append(SR.MS_HEAP_MONITOR,null); //locale
	if (!cf.ghostMotor)
            application.append(SR.MS_FLASHBACKLIGHT,null); //locale
	if (cf.allowMinimize)
	    application.append(SR.MS_ENABLE_POPUP,null); //locale
	ap[apctr++]=cf.memMonitor;
	ap[apctr++]=cf.blFlash;
	ap[apctr++]=cf.popupFromMinimized;
	
        application.setSelectedFlags(ap);
        
	keepAlive=new NumberField("Keep-Alive period", cf.keepAlive, 20, 600 ); //locale
	fieldGmt=new NumberField("GMT offset", cf.gmtOffset, -12, 12);  //locale
        fieldLoc=new NumberField(SR.MS_CLOCK_OFFSET, cf.locOffset, -12, 12 ); //locale

        String fnts[]={"Normal", "Small", "Large"};
        font1=new ChoiceGroup("Roster font", ConstMIDP.CHOICE_POPUP, fnts, null); //locale
        font2=new ChoiceGroup("Message font", ConstMIDP.CHOICE_POPUP, fnts, null); //locale
        font1.setSelectedIndex(cf.font1/8, true);
        font2.setSelectedIndex(cf.font2/8, true);

        f.append(roster);
        f.append(font1);

        f.append(message);
        f.append(font2);
	
	String textWraps[]={SR.MS_TEXTWRAP_CHARACTER, SR.MS_TEXTWRAP_WORD}; //locale
	textWrap=new ChoiceGroup(SR.MS_TEXTWRAP, ConstMIDP.CHOICE_POPUP, textWraps,null); //locale
	textWrap.setSelectedIndex(cf.textWrap, true);
	f.append(textWrap);
	
	f.append(startup);

	f.append(application);

	f.append(keepAlive);
	
        f.append("Time settings (hours)"); //locale
        f.append("\n");
        
        f.append(fieldGmt);
        f.append(fieldLoc);
        
//#if !SMALL
        history=new ChoiceGroup("History", Choice.MULTIPLE); //locale
        history.append("Save History", null); //locale
        history.append("Save Presences",null);    //locale     
        history.append("Save Conf History", null); //locale
        history.append("Save Conf Presences", null); //locale
        history.append("1251 correction", null); //locale
        
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
        
        
        historyFolder=new TextField("History Folder", null, 200, TextField.ANY);
        historyFolder.setString(cf.msgPath);
        historyFolder.addCommand(cmdSetHistFolder);
        f.append(historyFolder);
        historyFolder.setItemCommandListener(this);
//#endif

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
            history.getSelectedFlags(his);
	    
            cf.showOfflineContacts=ra[0];
            cf.selfContact=ra[1];
            cf.showTransports=ra[2];
            cf.ignore=ra[3];
            cf.notInList=ra[4];
            cf.autoFocus=ra[5];

            cf.storeConfPresence=mv[0];
            cf.eventComposing=mv[1];
//#if (!MIDP1)
            cf.capsState=mv[2];
//#endif

	    cf.autoLogin=su[0];
	    cf.autoJoinConferences=su[1];
            
	    int apctr=0;
//#if !(MIDP1)
            VirtualList.fullscreen=cf.fullscreen=ap[apctr++];
            StaticData.getInstance().roster.setFullScreenMode(cf.fullscreen);
//#endif
	    VirtualList.memMonitor=cf.memMonitor=ap[apctr++];
	    cf.blFlash=ap[apctr++];
	    cf.popupFromMinimized=ap[apctr++];
            
	    cf.gmtOffset=fieldGmt.getValue();
	    cf.locOffset=fieldLoc.getValue();
	    cf.keepAlive=keepAlive.getValue();
            
            FontCache.rosterFontSize=cf.font1=font1.getSelectedIndex()*8;
            FontCache.msgFontSize=cf.font2=font2.getSelectedIndex()*8;
            FontCache.resetCache();
	    
	    cf.textWrap=textWrap.getSelectedIndex();
            
            cf.msgLog=his[0];
            cf.msgLogPresence=his[1];
            cf.msgLogConf=his[2];
            cf.msgLogConfPresence=his[3];
            cf.cp1251=his[4];
            
            cf.msgPath=historyFolder.getString();
	                
            cf.updateTime();
            
            cf.saveToStorage();
            
            StaticData.getInstance().roster.reEnumRoster();
            destroyView();
        }
//#if MIDP1
//#         if (c==cmdPlaySound) testSound();
//#endif
        if (c==cmdCancel) destroyView();
    }

//#if !(MIDP1)
    public void commandAction(Command command, Item item) {
//#if (FILE_IO)
        if (command==cmdSetHistFolder) {
           new Browser(display, this, true);
        }
//#endif
    }
//#endif
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
//#if !(MIDP1)
        ((Canvas)parentView).setFullScreenMode(cf.fullscreen);
//#endif
    }
    public void BrowserFilePathNotify(String pathSelected) {
        historyFolder.setString(pathSelected);  
    }
}
