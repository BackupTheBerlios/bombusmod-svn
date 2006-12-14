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
import images.SmilesIcons;
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.Font;
import midlet.Bombus;
import ui.FontCache;
import util.StringLoader;
import ui.Time;
import ui.VirtualElement;
import ui.VirtualList;
import io.NvStorage;
//import javax.microedition.rms.*;

/**
 *
 * @author Eugene Stahov
 */
public class Config {
    
    public final int vibraLen=getIntProperty("vibra_len",500);
    
    public int keepAlive=200;//getIntProperty("keep_alive",200);
    public int keepAliveType=getIntProperty("keep_alive_type",0);

    /*public int socketLINGER=getIntProperty("LINGER",-1);
    public int socketRCVBUF=getIntProperty("RCVBUF",-1);
    public int socketSNDBUF=getIntProperty("SNDBUF",-1);*/

    public boolean ghostMotor=getBooleanProperty("moto_e398",false);
    public boolean blFlash=!ghostMotor; //true;
    
    
    public boolean muc119=getBooleanProperty("muc_119",true);	// before muc 1.19 use muc#owner instead of muc#admin
    
//#if !(MIDP1)
    public char keyLock=getCharProperty("key_lock",'*');
    public char keyVibra=getCharProperty("key_vibra",'#');
    
     public String msgPath="";
     public boolean msgLog=false;
     public boolean msgLogPresence=false;
     public boolean msgLogConf=false;
     public boolean msgLogConfPresence=false;
     public boolean cp1251=true;
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
    
    public String defGcRoom="bombusmod@conference.jabber.ru";
    
    public String m_client=getStringProperty("m_client","Bombus");    
    public String m_ver=getStringProperty("m_ver",Version.getVersionLang());
    public String m_os=getStringProperty("m_os",Version.getOs()); 
    
    //public String xmlLang=getStringProperty("xml_lang",null);
    
    // non-volatile values
    //public TimeZone tz=new RuGmt(0);
    public int accountIndex=-1;
    public boolean fullscreen=false;
    public int def_profile=0;
    public boolean smiles=true;
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
    
    public int loginstatus=0;//loginstatus
    
    public int gmtOffset;
    public int locOffset;
    
    public boolean popupFromMinimized=true;
    public boolean memMonitor;
    public boolean digitMemMonitor=false;
    
    public int font1=0;
    public int font2=0;
    public int font3=0;

    public int lang=0;  //en
    public boolean capsState=true;
    public int textWrap=0;
    public boolean autoSubscribe=false;
	
    // runtime values
    public boolean allowMinimize=false;
    public int profile=0;
    public int lastProfile=0;
    
    public boolean istreamWaiting;

    // Singleton
    private static Config instance;

    int autoAwayTime=5;
    boolean setAutoStatus=false;

    public int confMessageCount=20;

    public boolean altInput;

    public int isbottom=3;

    public boolean poundKey=getBooleanProperty("poundKey",true);
    public boolean starKey=getBooleanProperty("starKey",true);
    
    public boolean lightState=true;
    
    public boolean lastMessages=false;

    
    public static Config getInstance(){
	if (instance==null) {
	    instance=new Config();
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
            SmilesIcons.getInstance();
            
	    allowMinimize=true;
            greenKeyCode=VirtualList.SE_GREEN;
	}
	if (platform.startsWith("Nokia")) {
	    blFlash=false;
	    greenKeyCode=VirtualList.NOKIA_GREEN;
	}
	if (platform.startsWith("Moto")) {
	    ghostMotor=true;
	    blFlash=false;
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
	    smiles=inputStream.readBoolean();
	    showTransports=inputStream.readBoolean();
	    selfContact=inputStream.readBoolean();
	    notInList=inputStream.readBoolean();
	    ignore=inputStream.readBoolean();
	    eventComposing=inputStream.readBoolean();
	    
	    gmtOffset=inputStream.readInt();
	    locOffset=inputStream.readInt();
	    
	    autoLogin=inputStream.readBoolean();
	    autoJoinConferences=inputStream.readBoolean();
	    
	    keepAlive=inputStream.readInt();
	    
	    popupFromMinimized=inputStream.readBoolean();
	    
	    blFlash=inputStream.readBoolean();
	    memMonitor=inputStream.readBoolean();
            
            font1=inputStream.readInt();
            font2=inputStream.readInt();
            
            autoFocus=inputStream.readBoolean();
            
            lang=inputStream.readInt();
            
            storeConfPresence=inputStream.readBoolean();
            
            capsState=inputStream.readBoolean();
	    
	    textWrap=inputStream.readInt();
            
            
            loginstatus=inputStream.readInt();
//#if FILE_IO
            msgPath=inputStream.readUTF();
            msgLog=inputStream.readBoolean();
            msgLogPresence=inputStream.readBoolean();
            msgLogConfPresence=inputStream.readBoolean();
            msgLogConf=inputStream.readBoolean();
            cp1251=inputStream.readBoolean();
//#endif
            autoAwayTime=inputStream.readInt();
            setAutoStatus=inputStream.readBoolean();
            
            defGcRoom=inputStream.readUTF();
            
            altInput=inputStream.readBoolean();
            
            isbottom=inputStream.readInt();
            
            confMessageCount=inputStream.readInt();
            
            digitMemMonitor=inputStream.readBoolean();
            
            lightState=inputStream.readBoolean();
			
            autoSubscribe=inputStream.readBoolean();
            
            lastMessages=inputStream.readBoolean();
            
	    inputStream.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	lastProfile=profile=def_profile;
        if (lastProfile==AlertProfile.VIBRA) lastProfile=0;
	updateTime();
	VirtualList.fullscreen=fullscreen;
	VirtualList.memMonitor=memMonitor;
        VirtualList.digitMemMonitor=digitMemMonitor;
    }
    
    public String langFileName(){
        if (lang==0) return null;   //english
	Vector files[]=new StringLoader().stringLoader("/lang/res.txt", 2);
	if (lang>=files[0].size()) return null;
	return (String) files[0].elementAt(lang);
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
	    outputStream.writeBoolean(notInList);
	    outputStream.writeBoolean(ignore);
	    outputStream.writeBoolean(eventComposing);
	    
	    outputStream.writeInt(gmtOffset);
	    outputStream.writeInt(locOffset);
	    
	    outputStream.writeBoolean(autoLogin);
	    outputStream.writeBoolean(autoJoinConferences);
	    
	    outputStream.writeInt(keepAlive);

	    outputStream.writeBoolean(popupFromMinimized);
	    
	    outputStream.writeBoolean(blFlash);
	    outputStream.writeBoolean(memMonitor);
            
            outputStream.writeInt(font1);
            outputStream.writeInt(font2);
            
            outputStream.writeBoolean(autoFocus);
            
            outputStream.writeInt(lang);
            
            outputStream.writeBoolean(storeConfPresence); 

            outputStream.writeBoolean(capsState); 
	    
	    outputStream.writeInt(textWrap);
            
            outputStream.writeInt(loginstatus);
//#if FILE_IO
            outputStream.writeUTF(msgPath);
            outputStream.writeBoolean(msgLog);
            outputStream.writeBoolean(msgLogPresence);
            outputStream.writeBoolean(msgLogConfPresence);
            outputStream.writeBoolean(msgLogConf);
            outputStream.writeBoolean(cp1251);
//#endif
            
            outputStream.writeInt(autoAwayTime);
            outputStream.writeBoolean(setAutoStatus);
            
            outputStream.writeUTF(defGcRoom);
            
            outputStream.writeBoolean(altInput);
            
            outputStream.writeInt(isbottom);
            
            outputStream.writeInt(confMessageCount);
            
            outputStream.writeBoolean(digitMemMonitor);
            
            outputStream.writeBoolean(lightState);
			
            outputStream.writeBoolean(autoSubscribe);
            
            outputStream.writeBoolean(lastMessages);
            
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
