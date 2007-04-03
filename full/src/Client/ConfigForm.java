/*
 * ConfigForm.java
 *
 * Created on 2.05.2005, 18:19
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
//#if FILE_IO
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import java.io.InputStream;
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
    
    //NumberField keepAlive;
    NumberField fieldLoc;
    NumberField fieldGmt;
    
    ChoiceGroup history;
    TextField historyFolder;
    
    ChoiceGroup awayStatus;
    
    NumberField fieldAwatDelay;
    ChoiceGroup autoAwayType;
    
    ChoiceGroup SkinFile;
    private Vector[] Skinfiles;

    Command cmdOk=new Command(SR.MS_OK,Command.OK,1);
//#if FILE_IO
    Command cmdSetHistFolder=new Command(SR.MS_SELECT_HISTORY_FOLDER, Command.ITEM,11);
//#endif
    Command cmdLoadSkin=new Command(SR.MS_LOAD_SKIN, Command.ITEM,15);
//#if FILE_IO
    Command cmdLoadSkinFS=new Command(SR.MS_LOAD_SKIN+"FS", Command.ITEM,16);
//#endif
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    
    Config cf;
    boolean ra[];
    boolean mv[];
    boolean ap[];
    boolean su[];
    boolean his[];
    boolean aa[];
    boolean lc[];
    Vector files[];
    
//#if FILE_IO
    private int HISTORY=0;
    private int COLORSHEME=1;
    
    private int returnVal=0;
//#endif
    private ColorScheme cs=ColorScheme.getInstance();
    
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
		roster.append(SR.MS_AUTH_NEW,null);
        
        boolean ra[]={
            cf.showOfflineContacts,
            cf.selfContact,
            cf.showTransports, 
            cf.ignore, 
            cf.notInList,
            cf.autoFocus,
            cf.autoSubscribe
        };
        this.ra=ra;
        //ra[5]=false;
        roster.setSelectedFlags(ra);

        message=new ChoiceGroup(SR.MS_MESSAGES, Choice.MULTIPLE);
        message.append(SR.MS_SMILES, null);
        message.append(SR.MS_STORE_PRESENCE,null);        
        message.append(SR.MS_COMPOSING_EVENTS, null);
        message.append(SR.MS_CAPS_STATE, null);
        message.append("AutoScroll", null);
        message.append("PopUps", null);
//#if LAST_MESSAGES
//#         message.append("Last messages", null);
//#endif
//#if ALT_INPUT
//#         message.append(SR.CLASSIC_CHAT, null);
//#endif        

        boolean mv[]={
            cf.smiles,
            cf.storeConfPresence,
            cf.eventComposing,
            cf.capsState,
            cf.autoScroll,
            cf.popUps
//#if LAST_MESSAGES
//#             ,cf.lastMessages
//#endif
//#if ALT_INPUT
//#             ,cf.altInput
//#endif
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
        
        ap=new boolean[6];
	int apctr=0;
        application=new ChoiceGroup(SR.MS_APPLICATION, Choice.MULTIPLE);
        ap[apctr++]=cf.fullscreen;
        application.append(SR.MS_FULLSCREEN,null);
        application.append(SR.MS_HEAP_MONITOR,null);
        application.append(SR.MS_NEW_MENU,null);
	if (!cf.ghostMotor) application.append(SR.MS_FLASHBACKLIGHT,null);
	if (cf.allowMinimize) application.append(SR.MS_ENABLE_POPUP,null);
        if (cf.allowLightControl) application.append("Turn on light",null);
        
	ap[apctr++]=cf.memMonitor;
        ap[apctr++]=cf.digitMemMonitor;
	ap[apctr++]=cf.blFlash;
	ap[apctr++]=cf.popupFromMinimized;
	ap[apctr++]=cf.lightState;
	
        application.setSelectedFlags(ap);
        
	//keepAlive=new NumberField(SR.MS_KEEPALIVE_PERIOD, cf.keepAlive, 10, 10000 );
	fieldGmt=new NumberField(SR.MS_GMT_OFFSET, cf.gmtOffset, -12, 12); 
        fieldLoc=new NumberField(SR.MS_CLOCK_OFFSET, cf.locOffset, -12, 12 );

        String fnts[]={"Normal", "Small", "Large"};
        font1=new ChoiceGroup(SR.MS_ROSTER_FONT, ChoiceGroup.POPUP, fnts, null);
        font2=new ChoiceGroup(SR.MS_MESSAGE_FONT, ChoiceGroup.POPUP, fnts, null);
        font1.setSelectedIndex(cf.font1/8, true);
        font2.setSelectedIndex(cf.font2/8, true);

        f.append(roster);
        f.append(font1);

        f.append(message);
        f.append(font2);
        
	String textWraps[]={SR.MS_TEXTWRAP_CHARACTER, SR.MS_TEXTWRAP_WORD};
	textWrap=new ChoiceGroup(SR.MS_TEXTWRAP, ChoiceGroup.POPUP, textWraps,null);
	textWrap.setSelectedIndex(cf.textWrap, true);
	f.append(textWrap);
        
        
        lang=new ChoiceGroup(SR.MS_LANGUAGE, ChoiceGroup.POPUP);
	Vector langs[]=new StringLoader().stringLoader("/lang/res.txt",2);
	
	for (Enumeration f=langs[1].elements(); f.hasMoreElements(); ) {
	    lang.append( (String)f.nextElement(), null );
	}
	
        try {
            lang.setSelectedIndex(cf.lang, true);
        } catch (Exception e) { cf.lang=0; }
        
        f.append(startup);

	f.append(application);

	//f.append(keepAlive);
	
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
        autoAwayType=new ChoiceGroup(SR.MS_AWAY_TYPE, Choice.POPUP);
        autoAwayType.append(SR.MS_AWAY_OFF, null);
        autoAwayType.append(SR.MS_AWAY_LOCK, null);
        autoAwayType.append(SR.MS_AWAY_IDLE, null);
        autoAwayType.setSelectedIndex(cf.autoAwayType, true);
        
        fieldAwatDelay=new NumberField(SR.MS_AWAY_PERIOD, cf.autoAwayDelay, 1, 30);

        awayStatus=new ChoiceGroup(SR.MS_SET, Choice.MULTIPLE);
        //awayStatus.append("KeyLock status", null);
        awayStatus.append("AutoStatus Message", null);
        
        boolean aa[]={
            cf.setAutoStatusMessage
        };
        this.aa=aa;
        awayStatus.setSelectedFlags(aa);
        f.append(awayStatus);
        
        f.append(autoAwayType);
        f.append(fieldAwatDelay);
        //autostatus
                
        SkinFile=new ChoiceGroup(SR.MS_LOAD_SKIN, ChoiceGroup.POPUP);
	Skinfiles=new StringLoader().stringLoader("/skins/res.txt",2);
	for (Enumeration f=Skinfiles[1].elements(); f.hasMoreElements(); ) {
	    SkinFile.append( (String)f.nextElement(), null );
	}
	SkinFile.setSelectedIndex(0, true);
	f.append(SkinFile);
	SkinFile.addCommand(cmdLoadSkin);
//#if FILE_IO
	SkinFile.addCommand(cmdLoadSkinFS);
//#endif
	SkinFile.setItemCommandListener(this);
        
        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        
        f.setCommandListener(this);
	//f.setItemStateListener(this);
        
        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            VirtualList.isbottom=cf.isbottom;
            
            roster.getSelectedFlags(ra);
            message.getSelectedFlags(mv);
            application.getSelectedFlags(ap);
	    startup.getSelectedFlags(su);
//#if FILE_IO
            history.getSelectedFlags(his);
//#endif
            awayStatus.getSelectedFlags(aa);
	    
            cf.showOfflineContacts=ra[0];
            cf.selfContact=ra[1];
            cf.showTransports=ra[2];
            cf.ignore=ra[3];
            cf.notInList=ra[4];
            cf.autoFocus=ra[5];
            cf.autoSubscribe=ra[6];

            
            int mvctr=0;
            cf.smiles=mv[mvctr++];
            cf.storeConfPresence=mv[mvctr++];
            cf.eventComposing=mv[mvctr++];
            cf.capsState=mv[mvctr++];
            cf.autoScroll=mv[mvctr++];
            cf.popUps=mv[mvctr++];
//#if LAST_MESSAGES
//#             cf.lastMessages=mv[mvctr++];
//#endif
//#if ALT_INPUT
//#         cf.altInput=mv[mvctr++];
//#endif
            
	    cf.autoLogin=su[0];
	    cf.autoJoinConferences=su[1];
            
	    int apctr=0;

            VirtualList.fullscreen=cf.fullscreen=ap[apctr++];
            StaticData.getInstance().roster.setFullScreenMode(cf.fullscreen);

	    VirtualList.memMonitor=cf.memMonitor=ap[apctr++];
            VirtualList.digitMemMonitor=cf.digitMemMonitor=ap[apctr++];   
            
	    cf.blFlash=ap[apctr++];
	    cf.popupFromMinimized=ap[apctr++];
            cf.lightState=ap[apctr++];
            
	    cf.gmtOffset=fieldGmt.getValue();
	    cf.locOffset=fieldLoc.getValue();
	    //cf.keepAlive=keepAlive.getValue();
            
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
            //cf.setKeyBlockStatus=aa[0];
            cf.setAutoStatusMessage=aa[0];

            
            cf.autoAwayDelay=fieldAwatDelay.getValue();
            cf.autoAwayType=autoAwayType.getSelectedIndex();
            
            cf.updateTime();
            
            cf.saveToStorage();
            
            StaticData.getInstance().roster.reEnumRoster();
            destroyView();
        }
        if (c==cmdCancel) destroyView();
    }

    public void commandAction(Command command, Item item) {
	if (command==cmdLoadSkin) {
            int skinfl=SkinFile.getSelectedIndex();
            String skinFile=(String)Skinfiles[0].elementAt(skinfl);

            cs.loadSkin(skinFile, 1);
	}
//#if FILE_IO
        if (command==cmdLoadSkinFS) {
            returnVal=COLORSHEME;
            new Browser(null, display, this, false);
        }
        
        if (command==cmdSetHistFolder) {
            returnVal=HISTORY;
            new Browser(null, display, this, true);
        }
//#endif
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
        ((Canvas)parentView).setFullScreenMode(cf.fullscreen);
        
        if (cf.allowLightControl) setLight(cf.lightState);
    }
    
    public static void setLight(boolean state) {
        if (state) {
            com.siemens.mp.game.Light.setLightOn();
        } else {
            com.siemens.mp.game.Light.setLightOff();    
        }
    }

//#if FILE_IO
    public void BrowserFilePathNotify(String pathSelected) {
        switch (returnVal) {
            case 0:
                historyFolder.setString(pathSelected);
                break;
            case 1:
                cs.loadSkin(pathSelected, 0);
                break;
        }
    }
//#endif
}
