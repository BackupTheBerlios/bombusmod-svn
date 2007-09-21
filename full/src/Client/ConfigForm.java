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
import Info.Phone;
//#if FILE_IO
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.controls.NumberField;
import util.StringLoader;
import ui.*;

public class ConfigForm implements
	CommandListener 
//#if  FILE_IO && COLORS 
//# 	,ItemCommandListener
//#         , BrowserListener
//#endif
{
    private Display display;
    private Displayable parentView;

    Form f;
    ChoiceGroup roster;
    ChoiceGroup message;
    
    ChoiceGroup nil;
    
    NumberField MessageLimit;
    
    ChoiceGroup startup;
    ChoiceGroup application;

    ChoiceGroup lang;
    
    ChoiceGroup font1;
    ChoiceGroup font2;
    
    ChoiceGroup textWrap;
    
    //NumberField keepAlive;
    NumberField fieldLoc;
    NumberField fieldGmt;
//#ifdef AUTOSTATUS
//#     ChoiceGroup awayStatus;
//# 
//#     NumberField fieldAwatDelay;
//#     ChoiceGroup autoAwayType;
//#endif
//#if SERVER_SIDE_CONFIG  
//#     ChoiceGroup settings;
//#endif
    Command cmdOk=new Command(SR.MS_OK,Command.OK,1);
    
//#ifdef COLORS
//#     private ColorScheme cs=ColorScheme.getInstance();
//#     
//#     ChoiceGroup SkinFile;
//#     private Vector[] Skinfiles;
//#     Command cmdLoadSkin=new Command(SR.MS_LOAD_SKIN, Command.ITEM,15);
//#if FILE_IO
//#     Command cmdLoadSkinFS=new Command(SR.MS_LOAD_SKIN+"FS", Command.ITEM,16);
//#endif
//#endif
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    
    private Phone ph=Phone.getInstance();
    
    Config cf;
    boolean ra[];
    boolean mv[];
    boolean ap[];
    boolean su[];
//#ifdef AUTOSTATUS
//#     boolean aa[];
//#endif
    boolean lc[];
    boolean se[];
    Vector files[];
    Vector langs[];
    
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
        roster.append(SR.MS_COLLAPSED_GROUPS, null);
        roster.append(SR.MS_AUTOFOCUS,null);
	roster.append(SR.MS_AUTH_NEW,null);
        roster.append(SR.MS_SHOW_RESOURCES,null);
        roster.append(SR.MS_SHOW_STATUSES,null);
        roster.append(SR.MS_SHOW_LAST_APPEARED_CONTACTS,null);
        

        boolean ra[]={
            cf.showOfflineContacts,
            cf.selfContact,
            cf.showTransports, 
            cf.ignore, 
            cf.collapsedGroups,
            cf.autoFocus,
            cf.autoSubscribe,
            cf.showResources,
            cf.rosterStatus,
            cf.showLastAppearedContact
        };
        this.ra=ra;

        roster.setSelectedFlags(ra);

        nil=new ChoiceGroup(SR.MS_NOT_IN_LIST, ChoiceGroup.POPUP);
        nil.append(SR.MS_NIL_DROP_MP, null);
        nil.append(SR.MS_NIL_DROP_P, null);
        nil.append(SR.MS_NIL_ALLOW_ALL, null);
        nil.setSelectedIndex((cf.notInListDropLevel>NotInListFilter.ALLOW_ALL)? NotInListFilter.ALLOW_ALL: cf.notInListDropLevel, true);

        message=new ChoiceGroup(SR.MS_MESSAGES, Choice.MULTIPLE);
//#ifdef SMILES
//#         message.append(SR.MS_SMILES, null);
//#endif
        message.append(SR.MS_COMPOSING_EVENTS, null);
        message.append(SR.MS_CAPS_STATE, null);
        message.append("AutoScroll", null);
//#ifdef ANTISPAM
//#         message.append("Antispam Conference", null);
//#endif
//#ifdef POPUPS
//#         message.append("popUps", null);
//#endif
        
        message.append("show balloons", null);
        
        
//#if ALT_INPUT
//#         message.append(SR.CLASSIC_CHAT, null);
//#endif    
            message.append(SR.MS_DELIVERY, null);

        boolean mv[]={
//#ifdef SMILES
//#             cf.smiles,
//#endif
            cf.eventComposing,
            cf.capsState,
            cf.autoScroll
//#ifdef ANTISPAM
//#             ,cf.antispam
//#endif
//#ifdef POPUPS
//#             ,cf.popUps
//#endif
            ,cf.showBalloons
                    
//#if ALT_INPUT
//#             ,cf.altInput
//#endif
            ,cf.eventDelivery
        };
        this.mv=mv;
 
        message.setSelectedFlags(mv);
        
        MessageLimit=new NumberField(SR.MS_MESSAGE_COLLAPSE_LIMIT, cf.messageLimit, 200, 1000);

	startup=new ChoiceGroup(SR.MS_STARTUP_ACTIONS, Choice.MULTIPLE);
        startup.append(SR.MS_AUTOLOGIN, null);
        startup.append(SR.MS_AUTO_CONFERENCES,null);
        su=new boolean[2];
        su[0]=cf.autoLogin;
        su[1]=cf.autoJoinConferences;
        startup.setSelectedFlags(su);
        
        application=new ChoiceGroup(SR.MS_APPLICATION, Choice.MULTIPLE);
        application.append(SR.MS_FULLSCREEN,null);
        application.append(SR.MS_HEAP_MONITOR,null);
        application.append(SR.MS_SHOW_HARDWARE,null);
        application.append(SR.MS_CONFIRM_EXIT,null);
//#ifdef USER_KEYS
//#         application.append(SR.MS_CUSTOM_KEYS,null);
//#endif
//#ifdef NEW_MENU
//#         application.append(SR.MS_NEW_MENU,null);
//#endif
        application.append(SR.MS_FLASHLIGHT,null);
	application.append(SR.MS_FLASHBACKLIGHT,null);

        boolean ap[]={
            cf.fullscreen,
            cf.memMonitor,
            cf.enableVersionOs,
            cf.queryExit,
//#ifdef USER_KEYS
//#             cf.userKeys,
//#endif
//#ifdef NEW_MENU
//#             cf.newMenu,
//#endif
            cf.lightState,
            cf.blFlash,
            cf.popupFromMinimized
        };
        
	if (cf.allowMinimize) {
            application.append(SR.MS_ENABLE_POPUP,null);
        }

        this.ap=ap;
        
        application.setSelectedFlags(ap);
        
	//keepAlive=new NumberField(SR.MS_KEEPALIVE_PERIOD, cf.keepAlive, 10, 10000 );
	fieldGmt=new NumberField(SR.MS_GMT_OFFSET, cf.gmtOffset, -12, 12); 
        fieldLoc=new NumberField(SR.MS_CLOCK_OFFSET, cf.locOffset, -12, 12 );

        String fnts[]={SR.MS_FONTSIZE_NORMAL, SR.MS_FONTSIZE_SMALL, SR.MS_FONTSIZE_LARGE};
        font1=new ChoiceGroup(SR.MS_ROSTER_FONT, ChoiceGroup.POPUP, fnts, null);
        font2=new ChoiceGroup(SR.MS_MESSAGE_FONT, ChoiceGroup.POPUP, fnts, null);
        font1.setSelectedIndex(cf.font1/8, true);
        font2.setSelectedIndex(cf.font2/8, true);

        f.append(roster);
        
        f.append(nil);
        
        f.append(font1);

        f.append(message);
        f.append(MessageLimit);
        f.append(font2);
        
	String textWraps[]={SR.MS_TEXTWRAP_CHARACTER, SR.MS_TEXTWRAP_WORD};
	textWrap=new ChoiceGroup(SR.MS_TEXTWRAP, ChoiceGroup.POPUP, textWraps,null);
	textWrap.setSelectedIndex(cf.textWrap, true);
	f.append(textWrap);
        
//******************** lang
        lang=new ChoiceGroup(SR.MS_LANGUAGE, ChoiceGroup.POPUP);
	langs=new StringLoader().stringLoader("/lang/res.txt",3);
        
        String tempLang=cf.lang;

        if (tempLang==null) { //not detected
            String locale=System.getProperty("microedition.locale");  
            if (locale!=null) {
                tempLang=locale.substring(0, 2).toLowerCase();
            }
        }

	for (int i=0; i<langs[0].size(); i++) {
            String label=(String) langs[2].elementAt(i);
            String langCode=(String) langs[0].elementAt(i);
	    lang.append( label, null );
            if (tempLang.equals(langCode))
                lang.setSelectedIndex(i, true);
        }
//******************** lang
        
        
        f.append(startup);

	f.append(application);

	//f.append(keepAlive);
	
        f.append(SR.MS_TIME_SETTINGS);
        f.append("\n");
        
        f.append(fieldGmt);
        f.append(fieldLoc);
        
        f.append(lang);
//#ifdef AUTOSTATUS
//#         autoAwayType=new ChoiceGroup(SR.MS_AWAY_TYPE, Choice.POPUP);
//#         autoAwayType.append(SR.MS_AWAY_OFF, null);
//#         autoAwayType.append(SR.MS_AWAY_LOCK, null);
//#         autoAwayType.append(SR.MS_MESSAGE_LOCK, null);
//#         autoAwayType.append(SR.MS_IDLE, null);
//#         autoAwayType.setSelectedIndex(cf.autoAwayType, true);
//#         
//#         fieldAwatDelay=new NumberField(SR.MS_AWAY_PERIOD, cf.autoAwayDelay, 1, 60);
//# 
//#         awayStatus=new ChoiceGroup(SR.MS_SET, Choice.MULTIPLE);
//#         awayStatus.append("AutoStatus Message", null);
//#         
//#         boolean aa[]={
//#             cf.setAutoStatusMessage
//#         };
//#         this.aa=aa;
//#         awayStatus.setSelectedFlags(aa);
//#         f.append(awayStatus);
//#     
//#         f.append(autoAwayType);
//#         f.append(fieldAwatDelay);
//#endif

//#ifdef COLORS       
//#         SkinFile=new ChoiceGroup(SR.MS_LOAD_SKIN, ChoiceGroup.POPUP);
//#         Skinfiles=new StringLoader().stringLoader("/skins/res.txt",2);
//# 	for (Enumeration f=Skinfiles[1].elements(); f.hasMoreElements(); ) {
//# 	    SkinFile.append( (String)f.nextElement(), null );
//# 	}
//# 	SkinFile.setSelectedIndex(0, true);
//# 	f.append(SkinFile);
//# 	SkinFile.addCommand(cmdLoadSkin);
//#         
//#if FILE_IO
//# 	SkinFile.addCommand(cmdLoadSkinFS);
//#endif
//# 
//# 	SkinFile.setItemCommandListener(this);
//#endif
    
//#if SERVER_SIDE_CONFIG  
//#         settings=new ChoiceGroup(SR.MS_OPTIONS, Choice.MULTIPLE);
//#         settings.append(SR.MS_SAVE_OPTIONS_TO_SERVER, null);
//#         
//#         boolean se[]={
//#             false
//#         };
//#         this.se=se;
//#         settings.setSelectedFlags(se);
//#         f.append(settings);
//#endif
        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);       
        f.setCommandListener(this);
        
        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            VirtualList.isbottom=cf.isbottom;
            
            roster.getSelectedFlags(ra);
            message.getSelectedFlags(mv);
            application.getSelectedFlags(ap);
	    startup.getSelectedFlags(su);
//#ifdef AUTOSTATUS
//#             awayStatus.getSelectedFlags(aa);
//#endif
//#if SERVER_SIDE_CONFIG  
//#             settings.getSelectedFlags(se);
//#endif 
            
            cf.notInListDropLevel=nil.getSelectedIndex();
            
            cf.showOfflineContacts=ra[0];
            cf.selfContact=ra[1];
            cf.showTransports=ra[2];
            cf.ignore=ra[3];
            cf.collapsedGroups=ra[4];
            cf.autoFocus=ra[5];
            cf.autoSubscribe=ra[6];
            cf.showResources=ra[7];
            cf.rosterStatus=ra[8];
            cf.showLastAppearedContact=ra[9];
            
            int mvctr=0;
//#ifdef SMILES
//#             cf.smiles=mv[mvctr++];
//#endif
            cf.eventComposing=mv[mvctr++];
            cf.capsState=mv[mvctr++];
            cf.autoScroll=mv[mvctr++];
//#ifdef ANTISPAM
//#             cf.antispam=mv[mvctr++];
//#endif
//#ifdef POPUPS
//#             cf.popUps=mv[mvctr++];
//#endif
            VirtualList.showBalloons=cf.showBalloons=mv[mvctr++];
 
//#if ALT_INPUT
//#             cf.altInput=mv[mvctr++];
//#endif
            cf.eventDelivery=mv[mvctr++];
            
            
	    cf.autoLogin=su[0];
	    cf.autoJoinConferences=su[1];
            
	    int apctr=0;

            VirtualList.fullscreen=cf.fullscreen=ap[apctr++];
	    VirtualList.memMonitor=cf.memMonitor=ap[apctr++];
            cf.enableVersionOs=ap[apctr++];
            cf.queryExit=ap[apctr++];
//#ifdef USER_KEYS
//#             VirtualList.userKeys=cf.userKeys=ap[apctr++];
//#endif
//#ifdef NEW_MENU
//#             cf.newMenu=ap[apctr++];
//#endif
            cf.lightState=ap[apctr++];
            cf.blFlash=ap[apctr++];
            if (cf.allowMinimize) {
                cf.popupFromMinimized=ap[apctr++];
            }
            
	    cf.gmtOffset=fieldGmt.getValue();
	    cf.locOffset=fieldLoc.getValue();
            
            FontCache.rosterFontSize=cf.font1=font1.getSelectedIndex()*8;
            FontCache.msgFontSize=cf.font2=font2.getSelectedIndex()*8;
            FontCache.resetCache();
	    
	    cf.textWrap=textWrap.getSelectedIndex();

            cf.lang=(String) langs[0].elementAt( lang.getSelectedIndex() );
//#ifdef AUTOSTATUS
//#             cf.setAutoStatusMessage=aa[0];
//# 
//#             
//#             cf.autoAwayDelay=fieldAwatDelay.getValue();
//#             cf.autoAwayType=autoAwayType.getSelectedIndex();
//#endif
            cf.messageLimit=MessageLimit.getValue();

            
            if (cf.allowLightControl)
                StaticData.getInstance().roster.setLight(cf.lightState);   
            
            StaticData.getInstance().roster.setFullScreenMode(cf.fullscreen);
            
            cf.updateTime();
            
            cf.saveToStorage();
//#if SERVER_SIDE_CONFIG  
//#             boolean savesettings=se[0];
//#             if (savesettings)
//#                 new ConfigPrivateStorage(false);
//#endif
            StaticData.getInstance().roster.reEnumRoster();
            destroyView();
        }
        if (c==cmdCancel) destroyView();
    }

    public void commandAction(Command command, Item item) {
//#ifdef COLORS
//# 	if (command==cmdLoadSkin) {
//#             int skinfl=SkinFile.getSelectedIndex();
//#             String skinFile=(String)Skinfiles[0].elementAt(skinfl);
//# 
//#             cs.loadSkin(skinFile, 1);
//# 	}
//#        
//#if FILE_IO
//#         if (command==cmdLoadSkinFS) {
//#             new Browser(null, display, this, false);
//#         }
//#endif
//#endif
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
        ((Canvas)parentView).setFullScreenMode(cf.fullscreen);
    }

//#if FILE_IO && COLORS
//#     public void BrowserFilePathNotify(String pathSelected) {
//#         cs.loadSkin(pathSelected, 0);
//#     }
//#endif
}
