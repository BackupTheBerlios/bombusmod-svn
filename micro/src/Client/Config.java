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
import midlet.BombusSmall;
import ui.FontCache;
import util.StringLoader;
import ui.Time;
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

    public boolean ghostMotor=getBooleanProperty("moto_e398",false);
    public boolean blFlash=!ghostMotor; //true;
    
    public boolean muc119=getBooleanProperty("muc_119",true);	// before muc 1.19 use muc#owner instead of muc#admin

    public char keyLock=getCharProperty("key_lock",'*');
    public char keyVibra=getCharProperty("key_vibra",'#');
    
    public char keyHide=getCharProperty("key_hide",'9');
    public char keyOfflines=getCharProperty("key_offlines",'0');
    
    public String defGcRoom=getStringProperty("gc_room","bombus@conference.jabber.ru");

    public int accountIndex=-1;
    public boolean fullscreen=false;
    public int def_profile=0;
    public boolean showOfflineContacts=true;
    public boolean showTransports=true;
    public boolean selfContact=false;

    public int notInListDropLevel=NotInListFilter.ALLOW_ALL; //enable all
    
    public boolean ignore=false;
    public boolean eventComposing=false;
    
    public boolean eventDelivery=false;
    
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

    public String lang;  //en

    public boolean capsState=true;
    public int textWrap=0;
    
    public boolean autoSubscribe=false;
    
    public int autoAwayType=0;
    public int autoAwayDelay=5; //5 minutes
    
    public boolean enableVersionOs=true;

    // runtime values
    public boolean allowMinimize=false;
    public int profile=0;
    public int lastProfile=0;
    
    public int loginstatus=0;//loginstatus
    
    public boolean istreamWaiting;
    

    // Singleton
    private static Config instance;

    public static Config getInstance(){
	if (instance==null) {
	    instance=new Config();
	    // this method called outside class constructor to avoid recursion
            // because using of NvStorage methods depending on Config.getInstance()
	    instance.loadFromStorage();

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
            if (platform.startsWith("SonyEricssonM600")) VirtualList.keyBack=-11;
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
    }
    
    protected void loadFromStorage(){
	try {
	    DataInputStream inputStream=NvStorage.ReadFileRecord("small_config", 0);
	    accountIndex = inputStream.readInt();
	    showOfflineContacts=inputStream.readBoolean();
	    fullscreen=inputStream.readBoolean();
	    def_profile = inputStream.readInt();
	    showTransports=inputStream.readBoolean();
	    selfContact=inputStream.readBoolean();
	    ignore=inputStream.readBoolean();
	    eventComposing=inputStream.readBoolean();
	    
	    gmtOffset=inputStream.readInt();
	    locOffset=inputStream.readInt();

	    autoLogin=inputStream.readBoolean();
	    autoJoinConferences=inputStream.readBoolean();
	    
	    notInListDropLevel=inputStream.readInt();
	    
	    popupFromMinimized=inputStream.readBoolean();
	    
	    blFlash=inputStream.readBoolean();
	    memMonitor=inputStream.readBoolean();
            
            font1=inputStream.readInt();
            font2=inputStream.readInt();
            
            autoFocus=inputStream.readBoolean();

            /*lang=*/inputStream.readInt();

            storeConfPresence=inputStream.readBoolean();
            
            capsState=inputStream.readBoolean();
	    
	    textWrap=inputStream.readInt();
            
            loginstatus=inputStream.readInt();
            
            autoSubscribe=inputStream.readBoolean();
            
            autoAwayType=inputStream.readInt();
            autoAwayDelay=inputStream.readInt();
            
            enableVersionOs=inputStream.readBoolean();
            
            lang=inputStream.readUTF();
            
            eventDelivery=inputStream.readBoolean();

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
	    outputStream.writeBoolean(showTransports);
	    outputStream.writeBoolean(selfContact);
	    outputStream.writeBoolean(true /*notInList*/);
	    outputStream.writeBoolean(ignore);
	    outputStream.writeBoolean(eventComposing);
	    
	    outputStream.writeInt(gmtOffset);
	    outputStream.writeInt(locOffset);

            outputStream.writeBoolean(autoLogin);
	    outputStream.writeBoolean(autoJoinConferences);
	    
	    outputStream.writeInt(notInListDropLevel /*keepAlive*/);

	    outputStream.writeBoolean(popupFromMinimized);
	    
	    outputStream.writeBoolean(blFlash);
	    outputStream.writeBoolean(memMonitor);
            
            outputStream.writeInt(font1);
            outputStream.writeInt(font2);
            
            outputStream.writeBoolean(autoFocus);

            outputStream.writeInt(0 /*lang*/);

            outputStream.writeBoolean(storeConfPresence); 

            outputStream.writeBoolean(capsState); 
	    
	    outputStream.writeInt(textWrap);
            
            outputStream.writeBoolean(autoSubscribe);

            outputStream.writeInt(autoAwayType);
            outputStream.writeInt(autoAwayDelay);
            
            outputStream.writeBoolean(enableVersionOs);
            
            outputStream.writeUTF(lang);
            
            outputStream.writeBoolean(eventDelivery);

            outputStream.writeInt(loginstatus);

	} catch (Exception e) { e.printStackTrace(); }
	
	NvStorage.writeFileRecord(outputStream, "small_config", 0, true);
    }
    
    
    public void updateTime(){
	Time.setOffset(gmtOffset, locOffset);
    }
    
    
    public final String getStringProperty(final String key, final String defvalue) {
	try {
	    String s=BombusSmall.getInstance().getAppProperty(key);
	    return (s==null)?defvalue:s;
	} catch (Exception e) {	}
        // возвращает defvalue, если атрибут не существует или имеет неправильный формат
        return defvalue;
    }
    
    public final int getIntProperty(final String key, final int defvalue) {
	try {
	    String s=BombusSmall.getInstance().getAppProperty(key);
	    return Integer.parseInt(s); //throws NullPointerException or NumberFormatException
	} catch (Exception e) { }
        // возвращает defvalue, если атрибут не существует или имеет неправильный формат
	return defvalue;
    }
    
    public final char getCharProperty(final String key, final char defvalue) {
	try {
	    String s=BombusSmall.getInstance().getAppProperty(key);
	    return s.charAt(0); //throws NullPointerException или IndexOutOfBoundsException
	} catch (Exception e) {	}
        // возвращает defvalue, если атрибут не существует или имеет неправильный формат
        return defvalue;
    }
    
    public final boolean getBooleanProperty(final String key, final boolean defvalue) {
	try {
	    String s=BombusSmall.getInstance().getAppProperty(key);
	    if (s.equals("true")) return true;
	    if (s.equals("yes")) return true;
	    if (s.equals("1")) return true;
            return false;
	} catch (Exception e) { }
        // возвращает defvalue, если атрибут не существует 
        return defvalue;
    }
    
}
