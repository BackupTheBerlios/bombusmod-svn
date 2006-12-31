/*
 * Config.java
 *
 * Created on 19 Март 2005 г., 18:37
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import Info.Version;
import images.RosterIcons;
import io.NvStorage;
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.Font;
import midlet.Damafon;
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
    
    public final int vibraLen=getIntProperty("vibra_len",500);
    
    public boolean ghostMotor=getBooleanProperty("moto_e398",false);
    public boolean win1251=false; //true;
    
    public char keyLock=getCharProperty("key_lock",'*');
    public char keyVibra=getCharProperty("key_vibra",'#');
    
    public char keyHide=getCharProperty("key_hide",'9');
    public char keyOfflines=getCharProperty("key_offlines",'0');


    // non-volatile values
    //public TimeZone tz=new RuGmt(0);
    public int accountIndex=-1;
    public boolean fullscreen=false;
    public int def_profile=0;
    public boolean showOfflineContacts=true;
    public boolean selfContact=false;
    public boolean notInList=true;
    public boolean ignore=false;
    public boolean eventComposing=false;
    
    public boolean autoLogin=true;
    
    public boolean autoFocus=true;
    
    public int gmtOffset;
    public int locOffset;
    
    public boolean popupFromMinimized=true;
    public boolean memMonitor;
    
    public int font1=0;
    public int font2=0;
    public int font3=0;
    
    public int textWrap=0;
    
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
            greenKeyCode=VirtualList.SE_GREEN;
	}
	if (platform.startsWith("Nokia")) {
	    greenKeyCode=VirtualList.NOKIA_GREEN;
	}
	if (platform.startsWith("Moto")) {
	    ghostMotor=true;
            istreamWaiting=true;
	    greenKeyCode=VirtualList.MOTOROLA_GREEN;
	    VirtualList.keyClear=0x1000;
	    VirtualList.keyVolDown=VirtualList.MOTOE680_VOL_DOWN;
	}
        
        if (platform.startsWith("SIE")) {
            keyLock='#';
            keyVibra='*';
        }
        
	VirtualList.greenKeyCode=greenKeyCode;
    }
    
    protected void loadFromStorage(){
	try {
	    DataInputStream inputStream=NvStorage.ReadFileRecord("config", 0);
	    accountIndex = inputStream.readInt();
	    showOfflineContacts=inputStream.readBoolean();
	    fullscreen=inputStream.readBoolean();
	    def_profile = inputStream.readInt();
	    selfContact=inputStream.readBoolean();
	    notInList=inputStream.readBoolean();
	    ignore=inputStream.readBoolean();
	    eventComposing=inputStream.readBoolean();
	    
	    gmtOffset=inputStream.readInt();
	    locOffset=inputStream.readInt();
	    
	    autoLogin=inputStream.readBoolean();
	    
	    popupFromMinimized=inputStream.readBoolean();
	    
	    win1251=inputStream.readBoolean();
	    memMonitor=inputStream.readBoolean();
            
            font1=inputStream.readInt();
            font2=inputStream.readInt();
            
            autoFocus=inputStream.readBoolean();
	    
	    textWrap=inputStream.readInt();
            
            loginstatus=inputStream.readInt();
	    
	    inputStream.close();
	} catch (Exception e) {
	    System.out.println("trouble in Config load :(");
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
	    outputStream.writeBoolean(selfContact);
	    outputStream.writeBoolean(notInList);
	    outputStream.writeBoolean(ignore);
	    outputStream.writeBoolean(eventComposing);
	    
	    outputStream.writeInt(gmtOffset);
	    outputStream.writeInt(locOffset);
	    
	    outputStream.writeBoolean(autoLogin);

	    outputStream.writeBoolean(popupFromMinimized);
	    
	    outputStream.writeBoolean(win1251);
	    outputStream.writeBoolean(memMonitor);
            
            outputStream.writeInt(font1);
            outputStream.writeInt(font2);
            
            outputStream.writeBoolean(autoFocus);

	    outputStream.writeInt(textWrap);
	    
            outputStream.writeInt(loginstatus);
	} catch (Exception e) { e.printStackTrace(); }
	
	NvStorage.writeFileRecord(outputStream, "config", 0, true);
    }
    
    
    public void updateTime(){
	Time.setOffset(gmtOffset, locOffset);
    }
    
    
    public final String getStringProperty(final String key, final String defvalue) {
	try {
	    String s=Damafon.getInstance().getAppProperty(key);
	    return (s==null)?defvalue:s;
	} catch (Exception e) {	}
        // возвращает defvalue, если атрибут не существует или имеет неправильный формат
        return defvalue;
    }
    
    public final int getIntProperty(final String key, final int defvalue) {
	try {
	    String s=Damafon.getInstance().getAppProperty(key);
	    return Integer.parseInt(s); //throws NullPointerException or NumberFormatException
	} catch (Exception e) { }
        // возвращает defvalue, если атрибут не существует или имеет неправильный формат
	return defvalue;
    }
    
    public final char getCharProperty(final String key, final char defvalue) {
	try {
	    String s=Damafon.getInstance().getAppProperty(key);
	    return s.charAt(0); //throws NullPointerException или IndexOutOfBoundsException
	} catch (Exception e) {	}
        // возвращает defvalue, если атрибут не существует или имеет неправильный формат
        return defvalue;
    }
    
    public final boolean getBooleanProperty(final String key, final boolean defvalue) {
	try {
	    String s=Damafon.getInstance().getAppProperty(key);
	    if (s.equals("true")) return true;
	    if (s.equals("yes")) return true;
	    if (s.equals("1")) return true;
            return false;
	} catch (Exception e) { }
        // возвращает defvalue, если атрибут не существует 
        return defvalue;
    }
    
}
