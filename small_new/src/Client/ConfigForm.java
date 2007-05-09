/*
 * ConfigForm.java
 *
 * Created on 2.05.2005, 18:19
 *
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
 */

package Client;
import java.util.Enumeration;
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
    
    ChoiceGroup autoAwayType;
    
    //NumberField keepAlive;
    NumberField fieldLoc;
    NumberField fieldGmt;
    
    NumberField fieldAwatDelay;
    
    Command cmdOk=new Command(SR.MS_OK,Command.OK,1);
    //Command cmdSign=new Command("- (Sign)",Command.ITEM,2);
    Command cmdPlaySound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    
    Config cf;
    boolean ra[];
    boolean mv[];
    boolean ap[];
    boolean su[];
    Vector files[];
    
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
//#if (HISTORY)        
//#         message.append(SR.MS_HISTORY, null);
//#endif
        message.append(SR.MS_STORE_PRESENCE,null);        
        message.append(SR.MS_COMPOSING_EVENTS, null);
//#if (!MIDP1)
        message.append(SR.MS_CAPS_STATE, null);
//#endif
        
        boolean mv[]={
//#if (HISTORY)        
//#             cf.msgLog,
//#endif
            cf.storeConfPresence,
            cf.eventComposing
//#if (!MIDP1)
            ,cf.capsState
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
        
        ap=new boolean[4];
	int apctr=0;
        application=new ChoiceGroup(SR.MS_APPLICATION, Choice.MULTIPLE);
//#if !(MIDP1)
        ap[apctr++]=cf.fullscreen;
        application.append(SR.MS_FULLSCREEN,null);
//#endif
        application.append(SR.MS_HEAP_MONITOR,null);
	if (!cf.ghostMotor)
            application.append(SR.MS_FLASHBACKLIGHT,null);
	if (cf.allowMinimize)
	    application.append(SR.MS_ENABLE_POPUP,null);
	ap[apctr++]=cf.memMonitor;
	ap[apctr++]=cf.blFlash;
	ap[apctr++]=cf.popupFromMinimized;
	
        application.setSelectedFlags(ap);
        
        autoAwayType=new ChoiceGroup(SR.MS_AWAY_TYPE, ConstMIDP.CHOICE_POPUP);
        autoAwayType.append(SR.MS_AWAY_OFF, null);
        autoAwayType.append(SR.MS_AWAY_LOCK, null);
        autoAwayType.append(SR.MS_AWAY_IDLE, null);
        autoAwayType.setSelectedIndex(cf.autoAwayType, true);
        fieldAwatDelay=new NumberField(SR.MS_AWAY_PERIOD, cf.autoAwayDelay, 1, 30);
        
        
	// keepAlive=new NumberField(SR.MS_KEEPALIVE_PERIOD, cf.keepAlive, 20, 600 );
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
	
	f.append(startup);

	f.append(application);

        f.append(autoAwayType);
        f.append(fieldAwatDelay);
	//f.append(keepAlive);
	
        f.append(SR.MS_TIME_SETTINGS);
        f.append("\n");
        
        f.append(fieldGmt);
        f.append(fieldLoc);
        
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
	    
            cf.showOfflineContacts=ra[0];
            cf.selfContact=ra[1];
            cf.showTransports=ra[2];
            cf.ignore=ra[3];
            cf.notInList=ra[4];
            cf.autoFocus=ra[5];
            cf.autoSubscribe=ra[6];

            int haIdx=0;            
//#if (HISTORY)
//#             cf.msgLog=mv[0];
//#             haIdx=1;
//#endif

            cf.storeConfPresence=mv[haIdx++];
            cf.eventComposing=mv[haIdx++];
//#if (!MIDP1)
            cf.capsState=mv[haIdx++];
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
	    //cf.keepAlive=keepAlive.getValue();
            
            FontCache.rosterFontSize=cf.font1=font1.getSelectedIndex()*8;
            FontCache.msgFontSize=cf.font2=font2.getSelectedIndex()*8;
            FontCache.resetCache();
	    
	    cf.textWrap=textWrap.getSelectedIndex();
	    

            cf.updateTime();
            
            cf.saveToStorage();
            
            StaticData.getInstance().roster.reEnumRoster();
            destroyView();
        }
        if (c==cmdCancel) destroyView();
    }
   
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
//#if !(MIDP1)
        ((Canvas)parentView).setFullScreenMode(cf.fullscreen);
//#endif
    }
}
