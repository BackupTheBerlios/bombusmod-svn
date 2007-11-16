/*
 * Config.java
 *
 * Created on 19.03.2005, 18:37
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
import images.RosterIcons;
import images.SmilesIcons;
import java.io.*;
import java.util.*;
import midlet.BombusMod;
import ui.FontCache;
import util.StringLoader;
import ui.Time;
import ui.VirtualList;
import io.NvStorage;
//import javax.microedition.rms.*;

/**
 *
 * @author Eugene Stahov
 */
public class Config {
    
    public final int vibraLen=getIntProperty("vibra_len",500);

    public final static int AWAY_OFF=0;
    public final static int AWAY_LOCK=1;
    public final static int AWAY_MESSAGE=2;
    public final static int AWAY_IDLE=3;
    
    public static int KEY_BACK = -11;
    public static int SOFT_LEFT = -1000;
    public static int SOFT_RIGHT = -1000;
    
    public final static int SUBSCR_AUTO=0;
    public final static int SUBSCR_ASK=1;
    public final static int SUBSCR_DROP=2;
    public final static int SUBSCR_REJECT=3;
    
    public boolean ghostMotor=getBooleanProperty("moto_e398",false);
    public boolean blFlash=!ghostMotor; //true;
    
    
    public boolean muc119=getBooleanProperty("muc_119",true);	// before muc 1.19 use muc#owner instead of muc#admin
    
    public char keyLock=getCharProperty("key_lock",'*');
    public char keyVibra=getCharProperty("key_vibra",'#');
    

     public String msgPath="";
     public boolean msgLog=false;
     public boolean msgLogPresence=false;
     public boolean msgLogConf=false;
     public boolean msgLogConfPresence=false;
     public boolean cp1251=true;
    
    public String defGcRoom="bombusmod@conference.jabber.ru";
    
    Phone ph=Phone.getInstance();
    
    // non-volatile values
    public int accountIndex=-1;
    public boolean fullscreen=false;
    public int def_profile=0;
    public boolean smiles=true;
    public boolean showOfflineContacts=false;
    public boolean showTransports=true;
    public boolean selfContact=false;
    //public boolean notInList=true;
    public boolean ignore=false;
    public boolean eventComposing=true;
    
    public boolean storeConfPresence=true;      
    
    public boolean autoLogin=true;
    public boolean autoJoinConferences=true;
    
    public boolean autoFocus=false;
    
    public int loginstatus=0;//loginstatus
    
    public int gmtOffset;
    public int locOffset;
    
    public boolean popupFromMinimized=true;
    public boolean memMonitor=true;
    public boolean newMenu=false;
    
    public int font1=0;
    public int font2=0;
    public int font3=0;

    public String lang;  //not detected (en)
    public boolean capsState=false;
    public int textWrap=0;
    public int autoSubscribe=SUBSCR_ASK;
	
    // runtime values
    public boolean allowMinimize=false;
    public boolean allowLightControl=false;
    
    public int profile=0;
    public int lastProfile=0;
    
    public boolean istreamWaiting;

    // Singleton
    private static Config instance;

    public int autoAwayType=0;
    public int autoAwayDelay=5; //5 minutes
    public boolean setAutoStatusMessage=false;
    
    public int confMessageCount=20;

    public boolean altInput=false;

    public int isbottom=2; //default state both panels show, reverse disabled
   
    public boolean lightState=false;
    
    public boolean lastMessages=false;

    public boolean autoScroll=true;

    public boolean popUps=true;

    public boolean showResources=true;
    
    public boolean antispam=true;
    
    public boolean enableVersionOs=true;
    
    public boolean collapsedGroups=true;
    
    public int messageLimit=512;
    
    public boolean eventDelivery=false;
    
    public boolean transliterateFilenames=false;
    
    public boolean rosterStatus=true;
//#ifdef MOOD
//#     public boolean userMoods=true;
//#endif
    
    public boolean queryExit = false;
    
    public boolean showLastAppearedContact = false;
    
    public int notInListDropLevel=NotInListFilter.ALLOW_ALL; //enable all
    
    public boolean showBalloons = true;
    
    public boolean userKeys = false;

    public int msglistLimit=100;
    
    public boolean useTabs=true;
    
    public int phoneManufacturer;    
    
    public static Config getInstance(){
	if (instance==null) {
	    instance=new Config();
	    instance.loadFromStorage();

            FontCache.rosterFontSize=instance.font1;
            FontCache.msgFontSize=instance.font2;
            FontCache.resetCache();
	}
	return instance;
    }
    
    /** Creates a new instance of Config */
    private Config() {
     
        
	int gmtloc=TimeZone.getDefault().getRawOffset()/3600000;
	locOffset=getIntProperty( "time_loc_offset", 0);
	gmtOffset=getIntProperty("time_gmt_offset", gmtloc);
	
	short greenKeyCode=-1000;

                
	if (phoneManufacturer==Phone.SONYE) {
            //prefetch images
            RosterIcons.getInstance();
            SmilesIcons.getInstance();
            
			allowMinimize=true;
            greenKeyCode=VirtualList.SE_GREEN;
            if (phoneManufacturer==Phone.SONYE_M600) {
                KEY_BACK=-11;
            }
	} else if (phoneManufacturer==Phone.NOKIA) {
	    blFlash=false;
	    greenKeyCode=VirtualList.NOKIA_GREEN;
	} else if (phoneManufacturer==Phone.MOTOEZX) {
	    //VirtualList.keyClear=0x1000;
	    VirtualList.keyVolDown=VirtualList.MOTOE680_VOL_DOWN;
	    KEY_BACK=VirtualList.MOTOE680_REALPLAYER;
	} else if (phoneManufacturer==Phone.MOTO) {
	    ghostMotor=true;
	    blFlash=false;
            istreamWaiting=true;
	    greenKeyCode=VirtualList.MOTOROLA_GREEN;
	    //VirtualList.keyClear=0x1000;
	} else if (phoneManufacturer==Phone.SIEMENS || Phone.PhoneManufacturer()==Phone.SIEMENS2) {
            keyLock='#';
            keyVibra='*';
            allowLightControl=true;
            blFlash=true;
            KEY_BACK=-4; //keyCode==702
            greenKeyCode=VirtualList.SIEMENS_GREEN;
        } else if (phoneManufacturer==Phone.WTK) {
	    greenKeyCode=VirtualList.NOKIA_GREEN;
	}
        
	VirtualList.greenKeyCode=greenKeyCode;
    }
    
    protected void loadFromStorage(){
        DataInputStream inputStream=NvStorage.ReadFileRecord("config", 0);
	try {
	    accountIndex = inputStream.readInt();
	    showOfflineContacts=inputStream.readBoolean();
	    fullscreen=inputStream.readBoolean();
	    def_profile = inputStream.readInt();
	    smiles=inputStream.readBoolean();
	    showTransports=inputStream.readBoolean();
	    selfContact=inputStream.readBoolean();
	    collapsedGroups=inputStream.readBoolean();
	    ignore=inputStream.readBoolean();
	    eventComposing=inputStream.readBoolean();
	    
	    gmtOffset=inputStream.readInt();
	    locOffset=inputStream.readInt();
	    
	    autoLogin=inputStream.readBoolean();
	    autoJoinConferences=inputStream.readBoolean();
	    
	    popupFromMinimized=inputStream.readBoolean();
	    
	    blFlash=inputStream.readBoolean();
	    memMonitor=inputStream.readBoolean();
            
            font1=inputStream.readInt();
            font2=inputStream.readInt();
            
            autoFocus=inputStream.readBoolean();
            
            notInListDropLevel=inputStream.readInt();
            
            storeConfPresence=inputStream.readBoolean();
            
            capsState=inputStream.readBoolean();
	    
	    textWrap=inputStream.readInt();
            
            loginstatus=inputStream.readInt();

            msgPath=inputStream.readUTF();
            msgLog=inputStream.readBoolean();
            msgLogPresence=inputStream.readBoolean();
            msgLogConfPresence=inputStream.readBoolean();
            msgLogConf=inputStream.readBoolean();
            cp1251=inputStream.readBoolean();
            
            autoAwayDelay=inputStream.readInt();
        
            defGcRoom=inputStream.readUTF();
            
            altInput=inputStream.readBoolean();
            
            isbottom=inputStream.readInt();
            
            confMessageCount=inputStream.readInt();
            
            newMenu=inputStream.readBoolean();
            
            lightState=inputStream.readBoolean();
			
            /*autoSubscribe=*/inputStream.readBoolean();
            
            lastMessages=inputStream.readBoolean();

            setAutoStatusMessage=inputStream.readBoolean();
            
            autoAwayType=inputStream.readInt();
            
            autoScroll=inputStream.readBoolean();
            
            popUps=inputStream.readBoolean();
            
            showResources=inputStream.readBoolean();
            
            antispam=inputStream.readBoolean();
            
            enableVersionOs=inputStream.readBoolean();
            
            messageLimit=inputStream.readInt();
            
            lang=inputStream.readUTF();
            
            eventDelivery=inputStream.readBoolean();
            
            transliterateFilenames=inputStream.readBoolean();
            
            rosterStatus=inputStream.readBoolean();
            
            queryExit=inputStream.readBoolean();
            
            showLastAppearedContact=inputStream.readBoolean();
            
            showBalloons=inputStream.readBoolean();
            
            userKeys=inputStream.readBoolean();
            
            msglistLimit=inputStream.readInt();
            
            useTabs=inputStream.readBoolean();
            
            autoSubscribe=inputStream.readInt();
            
	    inputStream.close();
	} catch (Exception e) {
            try {
                if (inputStream!=null)
                    inputStream.close();
            } catch (IOException ex) { }
	}
	
	lastProfile=profile=def_profile;
        if (lastProfile==AlertProfile.VIBRA) lastProfile=0;
	updateTime();
	VirtualList.fullscreen=fullscreen;
	VirtualList.isbottom=isbottom;
	VirtualList.memMonitor=memMonitor;
        VirtualList.showBalloons=showBalloons;
        VirtualList.userKeys=userKeys;
    }
    
    public String langFileName(){
        if (lang==null) {
            //auto-detecting
            lang=System.getProperty("microedition.locale");
            System.out.println(lang);
            //We will use only language code from locale
            if (lang==null) lang="en"; else lang=lang.substring(0, 2).toLowerCase();
        }
        
        if (lang.equals("en")) return null;  //english
	Vector files[]=new StringLoader().stringLoader("/lang/res.txt", 3);
        for (int i=0; i<files[0].size(); i++) {
            String langCode=(String) files[0].elementAt(i);
            if (lang.equals(langCode))
        	return (String) files[1].elementAt(i);
        }
        return null; //unknown language ->en
    }
    
    public void saveToStorage(){
	
	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
	
	try {
	    outputStream.writeInt(accountIndex);
	    outputStream.writeBoolean(showOfflineContacts);
	    outputStream.writeBoolean(fullscreen);
	    outputStream.writeInt(def_profile);
	    outputStream.writeBoolean(smiles);
	    outputStream.writeBoolean(showTransports);
	    outputStream.writeBoolean(selfContact);
	    outputStream.writeBoolean(collapsedGroups);
	    outputStream.writeBoolean(ignore);
	    outputStream.writeBoolean(eventComposing);
	    
	    outputStream.writeInt(gmtOffset);
	    outputStream.writeInt(locOffset);
	    
	    outputStream.writeBoolean(autoLogin);
	    outputStream.writeBoolean(autoJoinConferences);

            outputStream.writeBoolean(popupFromMinimized);
	    
	    outputStream.writeBoolean(blFlash);
	    outputStream.writeBoolean(memMonitor);
            
            outputStream.writeInt(font1);
            outputStream.writeInt(font2);
            
            outputStream.writeBoolean(autoFocus);
            
            outputStream.writeInt(notInListDropLevel /*keepAlive*/);
            
            outputStream.writeBoolean(storeConfPresence); 

            outputStream.writeBoolean(capsState); 
	    
	    outputStream.writeInt(textWrap);
            
            outputStream.writeInt(loginstatus);

            outputStream.writeUTF(msgPath);
            outputStream.writeBoolean(msgLog);
            outputStream.writeBoolean(msgLogPresence);
            outputStream.writeBoolean(msgLogConfPresence);
            outputStream.writeBoolean(msgLogConf);
            outputStream.writeBoolean(cp1251);

            
            outputStream.writeInt(autoAwayDelay);
            
            outputStream.writeUTF(defGcRoom);
            
            outputStream.writeBoolean(altInput);
            
            outputStream.writeInt(isbottom);
            
            outputStream.writeInt(confMessageCount);
            
            outputStream.writeBoolean(newMenu);
            
            outputStream.writeBoolean(lightState);
			
            outputStream.writeBoolean(false /*autoSubscribe*/);
            
            outputStream.writeBoolean(lastMessages);
            
            outputStream.writeBoolean(setAutoStatusMessage);
            
            outputStream.writeInt(autoAwayType);
            
            outputStream.writeBoolean(autoScroll);
            
            outputStream.writeBoolean(popUps);
            
            outputStream.writeBoolean(showResources);
            
            outputStream.writeBoolean(antispam);
            
            outputStream.writeBoolean(enableVersionOs);
            
            outputStream.writeInt(messageLimit);
            
            outputStream.writeUTF(lang);      
            
            outputStream.writeBoolean(eventDelivery);
            
            outputStream.writeBoolean(transliterateFilenames);
            
            outputStream.writeBoolean(rosterStatus);
            
            outputStream.writeBoolean(queryExit);
           
            outputStream.writeBoolean(showLastAppearedContact);
            outputStream.writeBoolean(showBalloons);
            
            outputStream.writeBoolean(userKeys);
            
            outputStream.writeInt(msglistLimit);
            
            outputStream.writeBoolean(useTabs);
            
            outputStream.writeInt(autoSubscribe);
            
	} catch (Exception e) { }
	
	NvStorage.writeFileRecord(outputStream, "config", 0, true);
    }

    
    public void updateTime(){
	Time.setOffset(gmtOffset, locOffset);
    }
    
    
    public final String getStringProperty(final String key, final String defvalue) {
	try {
	    String s=BombusMod.getInstance().getAppProperty(key);
	    return (s==null)?defvalue:s;
	} catch (Exception e) {	}
        return defvalue;
    }
    
    public final int getIntProperty(final String key, final int defvalue) {
	try {
	    String s=BombusMod.getInstance().getAppProperty(key);
	    return Integer.parseInt(s);
	} catch (Exception e) { }
	return defvalue;
    }
    
    public final char getCharProperty(final String key, final char defvalue) {
	try {
	    String s=BombusMod.getInstance().getAppProperty(key);
	    return s.charAt(0);
	} catch (Exception e) {	}
        return defvalue;
    }
    
    public final boolean getBooleanProperty(final String key, final boolean defvalue) {
	try {
	    String s=BombusMod.getInstance().getAppProperty(key);
	    if (s.equals("true")) return true;
	    if (s.equals("yes")) return true;
	    if (s.equals("1")) return true;
            return false;
	} catch (Exception e) { }
        return defvalue;
    }
    
}
