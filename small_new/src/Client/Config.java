/*
 * Config.java
 *
 * Created on 19.03.2005, 18:37
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
 *
 */

package Client;

import Info.Version;
import images.RosterIcons;
import io.NvStorage;
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.Font;
import midlet.Bombus;
import ui.FontCache;
import util.StringLoader;
import ui.Time;
import ui.VirtualElement;
import ui.VirtualList;
//import javax.microedition.rms.*;

/**
 *
 * @author Eugene Stahov
 */
public class Config {
    
    public final static int AWAY_OFF=0;
    public final static int AWAY_LOCK=1;
    public final static int AWAY_IDLE=2;

    
    public final int vibraLen=getIntProperty("vibra_len",500);
    
    //public int keepAlive=200;//getIntProperty("keep_alive",200);
    //public int keepAliveType=getIntProperty("keep_alive_type",0);

    /*public int socketLINGER=getIntProperty("LINGER",-1);
    public int socketRCVBUF=getIntProperty("RCVBUF",-1);
    public int socketSNDBUF=getIntProperty("SNDBUF",-1);*/

    public boolean ghostMotor=getBooleanProperty("moto_e398",false);
    public boolean blFlash=!ghostMotor; //true;
    
    public boolean msgLog=getBooleanProperty("msg_log",false);
    
    public boolean muc119=getBooleanProperty("muc_119",true);	// before muc 1.19 use muc#owner instead of muc#admin
    
    //public int soundsMsgIndex=0;

    //public String messagesnd;
    //public String messageSndType;
    
    public int soundVol=100;
    
//#if !(MIDP1)
    public char keyLock=getCharProperty("key_lock",'*');
    public char keyVibra=getCharProperty("key_vibra",'#');
//#else
//#     public boolean msgLogPresence=getBooleanProperty("msg_log_presence",false);
//#     public boolean msgLogConfPresence=getBooleanProperty("msg_log_conf_presence",false);
//#     public boolean msgLogConf=getBooleanProperty("msg_log_conf",false);
//#     public final String msgPath=getStringProperty("msg_log_path","");
//#     public final String siemensCfgPath=getStringProperty("cfg_path","");
//#     public char keyLock=getCharProperty("key_lock",'#');
//#     public char keyVibra=getCharProperty("key_vibra",'*');
//#endif
    
    public char keyHide=getCharProperty("key_hide",'9');
    public char keyOfflines=getCharProperty("key_offlines",'0');
    
//#if USE_LED_PATTERN
//#     public int m55LedPattern=0;
//#endif
    
    public String defGcRoom=getStringProperty("gc_room","bombus@conference.jabber.ru");
    
    //public String xmlLang=getStringProperty("xml_lang",null);
    
    // non-volatile values
    //public TimeZone tz=new RuGmt(0);
    public int accountIndex=-1;
    public boolean fullscreen=false;
    public int def_profile=0;
    //public boolean smiles=true;
    public boolean showOfflineContacts=true;
    public boolean showTransports=true;
    public boolean selfContact=false;
    public boolean notInList=true;
    public boolean ignore=false;
    public boolean eventComposing=false;
    
    public boolean storeConfPresence=true;      
    
    public boolean autoLogin=true;
    public boolean autoJoinConferences=false;
    
    public boolean autoFocus=true;
    
    public int gmtOffset;
    public int locOffset;
    
    public boolean popupFromMinimized=true;
    public boolean memMonitor;
    
    public int font1=0;
    public int font2=0;
    public int font3=0;

    //public int lang=0;  //en
    public boolean capsState=true;
    public int textWrap=0;
    
    public boolean autoSubscribe=false;
    
    public int autoAwayType=0;
    public int autoAwayDelay=5; //5 minutes
    
    // runtime values
    public boolean allowMinimize=false;
    public int profile=0;
    public int lastProfile=0;
    
    public boolean istreamWaiting;
    

    // Singleton
    private static Config instance;

    public static Config getInstance(){
	if (instance==null) {
	    instance=new Config();
	    // this method called outside class constructor to avoid recursion
            // because using of NvStorage methods depending on Config.getInstance()
	    instance.loadFromStorage();
	    //instance.loadSoundName();

            //FontCache.balloonFontSize=Font.SIZE_SMALL;
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
	
	int greenKeyCode=VirtualList.SIEMENS_GREEN;
	
	String platform=Version.getPlatformName();
	
	if (platform.startsWith("SonyE")) {
            //prefetch images
            RosterIcons.getInstance();
            
	    allowMinimize=true;
            greenKeyCode=0;
	}
	if (platform.startsWith("Nokia")) {
	    blFlash=false;
	    greenKeyCode=VirtualList.NOKIA_GREEN;
	}

	if (platform.startsWith("Motorola-EZX")) {
	    VirtualList.keyClear=0x1000;
	    VirtualList.keyVolDown=VirtualList.MOTOE680_VOL_DOWN;
	    VirtualList.keyBack=VirtualList.MOTOE680_REALPLAYER;
	} else
	if (platform.startsWith("Moto")) {
	    ghostMotor=true;
	    blFlash=false;
            istreamWaiting=true;
	    greenKeyCode=VirtualList.MOTOROLA_GREEN;
	    VirtualList.keyClear=0x1000;
	}
       
        if (platform.startsWith("SIE")) {
            keyLock='#';
            keyVibra='*';
        }
        
	VirtualList.greenKeyCode=greenKeyCode;
//#if USE_LED_PATTERN
//#         if (platform.startsWith("M55"))
//#         m55LedPattern=getIntProperty("led_pattern",5);
//#endif
    }
    
    protected void loadFromStorage(){
	try {
	    DataInputStream inputStream=NvStorage.ReadFileRecord("config", 0);
	    accountIndex = inputStream.readInt();
	    showOfflineContacts=inputStream.readBoolean();
	    fullscreen=inputStream.readBoolean();
	    def_profile = inputStream.readInt();
	    //smiles=inputStream.readBoolean();
	    showTransports=inputStream.readBoolean();
	    selfContact=inputStream.readBoolean();
	    notInList=inputStream.readBoolean();
	    ignore=inputStream.readBoolean();
	    eventComposing=inputStream.readBoolean();
	    
	    gmtOffset=inputStream.readInt();
	    locOffset=inputStream.readInt();
	    
	    //soundsMsgIndex=inputStream.readInt();
	    //soundVol=inputStream.readInt();
	    
	    autoLogin=inputStream.readBoolean();
	    autoJoinConferences=inputStream.readBoolean();
	    
	    /*keepAlive=*/ inputStream.readInt();
	    
	    popupFromMinimized=inputStream.readBoolean();
	    
	    blFlash=inputStream.readBoolean();
	    memMonitor=inputStream.readBoolean();
            
            font1=inputStream.readInt();
            font2=inputStream.readInt();
            
            autoFocus=inputStream.readBoolean();
            
            //lang=inputStream.readInt();
            
            storeConfPresence=inputStream.readBoolean();
            
            capsState=inputStream.readBoolean();
	    
	    textWrap=inputStream.readInt();
            
            autoSubscribe=inputStream.readBoolean();
            
            autoAwayType=inputStream.readInt();
            autoAwayDelay=inputStream.readInt();
	    
	    inputStream.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	lastProfile=profile=def_profile;
        if (lastProfile==AlertProfile.VIBRA) lastProfile=0;
	updateTime();
	VirtualList.fullscreen=fullscreen;
	VirtualList.memMonitor=memMonitor;
    }
   
    public void saveToStorage(){
	
	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
	
	try {
	    outputStream.writeInt(accountIndex);
	    outputStream.writeBoolean(showOfflineContacts);
	    outputStream.writeBoolean(fullscreen);
	    outputStream.writeInt(def_profile);
	    //outputStream.writeBoolean(smiles);
	    outputStream.writeBoolean(showTransports);
	    outputStream.writeBoolean(selfContact);
	    outputStream.writeBoolean(notInList);
	    outputStream.writeBoolean(ignore);
	    outputStream.writeBoolean(eventComposing);
	    
	    outputStream.writeInt(gmtOffset);
	    outputStream.writeInt(locOffset);
	    
	    //outputStream.writeInt(soundsMsgIndex);
	    //outputStream.writeInt(soundVol);
	    
	    outputStream.writeBoolean(autoLogin);
	    outputStream.writeBoolean(autoJoinConferences);
	    
	    outputStream.writeInt(0 /*keepAlive*/);

	    outputStream.writeBoolean(popupFromMinimized);
	    
	    outputStream.writeBoolean(blFlash);
	    outputStream.writeBoolean(memMonitor);
            
            outputStream.writeInt(font1);
            outputStream.writeInt(font2);
            
            outputStream.writeBoolean(autoFocus);
            
            //outputStream.writeInt(lang);
            
            outputStream.writeBoolean(storeConfPresence); 

            outputStream.writeBoolean(capsState); 
	    
	    outputStream.writeInt(textWrap);
            
            outputStream.writeBoolean(autoSubscribe);

            outputStream.writeInt(autoAwayType);
            outputStream.writeInt(autoAwayDelay);
	    
	} catch (Exception e) { e.printStackTrace(); }
	
	NvStorage.writeFileRecord(outputStream, "config", 0, true);
    }
    
    
    public void updateTime(){
	Time.setOffset(gmtOffset, locOffset);
    }
    
    
    public final String getStringProperty(final String key, final String defvalue) {
	try {
	    String s=Bombus.getInstance().getAppProperty(key);
	    return (s==null)?defvalue:s;
	} catch (Exception e) {	}
        // возвращает defvalue, если атрибут не существует или имеет неправильный формат
        return defvalue;
    }
    
    public final int getIntProperty(final String key, final int defvalue) {
	try {
	    String s=Bombus.getInstance().getAppProperty(key);
	    return Integer.parseInt(s); //throws NullPointerException or NumberFormatException
	} catch (Exception e) { }
        // возвращает defvalue, если атрибут не существует или имеет неправильный формат
	return defvalue;
    }
    
    public final char getCharProperty(final String key, final char defvalue) {
	try {
	    String s=Bombus.getInstance().getAppProperty(key);
	    return s.charAt(0); //throws NullPointerException или IndexOutOfBoundsException
	} catch (Exception e) {	}
        // возвращает defvalue, если атрибут не существует или имеет неправильный формат
        return defvalue;
    }
    
    public final boolean getBooleanProperty(final String key, final boolean defvalue) {
	try {
	    String s=Bombus.getInstance().getAppProperty(key);
	    if (s.equals("true")) return true;
	    if (s.equals("yes")) return true;
	    if (s.equals("1")) return true;
            return false;
	} catch (Exception e) { }
        // возвращает defvalue, если атрибут не существует 
        return defvalue;
    }
    
}
