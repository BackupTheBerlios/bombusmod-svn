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

    /*public int socketLINGER=getIntProperty("LINGER",-1);
    public int socketRCVBUF=getIntProperty("RCVBUF",-1);
    public int socketSNDBUF=getIntProperty("SNDBUF",-1);*/
    
    public final static int AWAY_OFF=0;
    public final static int AWAY_LOCK=1;
    public final static int AWAY_IDLE=2;
    
    public static int KEY_BACK = -11;
    public static int SOFT_LEFT = -1000;
    public static int SOFT_RIGHT = -1000;

    public boolean ghostMotor=getBooleanProperty("moto_e398",false);
    public boolean blFlash=!ghostMotor; //true;
    
    
    public boolean muc119=getBooleanProperty("muc_119",true);	// before muc 1.19 use muc#owner instead of muc#admin
    
    public char keyLock=getCharProperty("key_lock",'*');
    public char keyVibra=getCharProperty("key_vibra",'#');
    
//#if FILE_IO
     public String msgPath="";
     public boolean msgLog=false;
     public boolean msgLogPresence=false;
     public boolean msgLogConf=false;
     public boolean msgLogConfPresence=false;
     public boolean cp1251=true;
//#endif
     
    public char keyHide=getCharProperty("key_hide",'9');
    public char keyOfflines=getCharProperty("key_offlines",'0');
    
    public String defGcRoom="bombusmod@conference.jabber.ru";
    
    Phone ph=Phone.getInstance();
    
    public String m_client=getStringProperty("m_client","BombusMod");    
    public String m_ver=getStringProperty("m_ver",Version.getVersionLang());
    public String m_os=getStringProperty("m_os",ph.getOs()); 
    
    // non-volatile values
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
    public boolean newMenu=false;
    
    public int font1=0;
    public int font2=0;
    public int font3=0;

    public int lang=0;  //en
    public boolean capsState=true;
    public int textWrap=0;
    public boolean autoSubscribe=false;
	
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
   
    public boolean lightState=true;
    
    public boolean lastMessages=false;

    public boolean autoScroll=false;

    public boolean popUps=true;

    public boolean showResources=true;
    
    public boolean antispam=false;
    
    public boolean enableVersionOs=true;

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
	
	short greenKeyCode=-1000;
	
	if (ph.PhoneManufacturer()==ph.SONYE) {
            //prefetch images
            RosterIcons.getInstance();
            SmilesIcons.getInstance();
            
			allowMinimize=true;
            greenKeyCode=VirtualList.SE_GREEN;
            if (ph.PhoneManufacturer()==ph.SONYE_M600) {
                System.out.println("bl");
                KEY_BACK=-11;
            }
	} else if (ph.PhoneManufacturer()==ph.NOKIA) {
	    blFlash=false;
	    greenKeyCode=VirtualList.NOKIA_GREEN;
	} else if (ph.PhoneManufacturer()==ph.MOTOEZX) {
	    VirtualList.keyClear=0x1000;
	    VirtualList.keyVolDown=VirtualList.MOTOE680_VOL_DOWN;
	    KEY_BACK=VirtualList.MOTOE680_REALPLAYER;
	} else if (ph.PhoneManufacturer()==ph.MOTO) {
	    ghostMotor=true;
	    blFlash=false;
            istreamWaiting=true;
	    greenKeyCode=VirtualList.MOTOROLA_GREEN;
	    VirtualList.keyClear=0x1000;
	} else if (ph.PhoneManufacturer()==ph.SIEMENS || ph.PhoneManufacturer()==ph.SIEMENS2) {
            keyLock='#';
            keyVibra='*';
            allowLightControl=true;
            blFlash=true;
            KEY_BACK=-4; //keyCode==702
            greenKeyCode=VirtualList.SIEMENS_GREEN;
        } else if (ph.PhoneManufacturer()==ph.WTK) {
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
	    notInList=true;
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
//#else
//#             inputStream.readUTF();
//#             inputStream.readBoolean();
//#             inputStream.readBoolean();
//#             inputStream.readBoolean();
//#             inputStream.readBoolean();
//#             inputStream.readBoolean();   
//#endif
            
            autoAwayDelay=inputStream.readInt();
        
            defGcRoom=inputStream.readUTF();
            
            altInput=inputStream.readBoolean();
            
            isbottom=inputStream.readInt();
            
            confMessageCount=inputStream.readInt();
            
            newMenu=inputStream.readBoolean();
            
            lightState=inputStream.readBoolean();
			
            autoSubscribe=inputStream.readBoolean();
            
            lastMessages=inputStream.readBoolean();

            setAutoStatusMessage=inputStream.readBoolean(); //setKeyBlockStatus=inputStream.readBoolean();
            
            autoAwayType=inputStream.readInt();
            
            autoScroll=inputStream.readBoolean();
            
            popUps=inputStream.readBoolean();
            
            showResources=inputStream.readBoolean();
            
            antispam=inputStream.readBoolean();
            
            enableVersionOs=inputStream.readBoolean();
            
	    inputStream.close();
	} catch (Exception e) {
	    accountIndex = -1;
	    showOfflineContacts=true;
	    fullscreen=false;
	    def_profile = 0;
	    smiles=true;
	    showTransports=true;
	    selfContact=true;
	    notInList=false;
	    ignore=false;
	    eventComposing=true;
	    
	    gmtOffset=3;
	    locOffset=0;
	    
	    autoLogin=true;
	    autoJoinConferences=true;
	    
	    popupFromMinimized=true;
	    
	    blFlash=!ghostMotor;
	    memMonitor=true;
            
            font1=0;
            font2=0;
            
            autoFocus=false;
            
            lang=0;
            
            storeConfPresence=true;
            
            capsState=false;
	    
	    textWrap=0;
            
            loginstatus=0;
//#if FILE_IO
             msgPath="";
             msgLog=false;
             msgLogPresence=false;
             msgLogConf=false;
             msgLogConfPresence=false;
             cp1251=true;
//#endif
            
            autoAwayDelay=5;
        
            defGcRoom="bombusmod@conference.jabber.ru";
            
            altInput=false;
            
            isbottom=2;
            
            confMessageCount=20;
            
            newMenu=false;
            
            lightState=false;
			
            autoSubscribe=true;
            
            lastMessages=false;

            setAutoStatusMessage=true; //setKeyBlockStatus=inputStream.readBoolean();
            
            autoAwayType=0;
            
            autoScroll=true;
            
            popUps=true;
            
            showResources=true;
            
            antispam=false;
            
            enableVersionOs=true;
            try {
                if (inputStream!=null)
                    inputStream.close();
            } catch (IOException ex) {
                //ex.printStackTrace();
            }
	}
	
	lastProfile=profile=def_profile;
        if (lastProfile==AlertProfile.VIBRA) lastProfile=0;
	updateTime();
	VirtualList.fullscreen=fullscreen;
	VirtualList.isbottom=isbottom;
	VirtualList.memMonitor=memMonitor;
//#ifdef NEW_MENU
//#         VirtualList.newMenu=newMenu;
//#endif
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
//#else
//#             outputStream.writeUTF("");
//#             outputStream.writeBoolean(false);
//#             outputStream.writeBoolean(false);
//#             outputStream.writeBoolean(false);
//#             outputStream.writeBoolean(false);
//#             outputStream.writeBoolean(false); 
//#endif
            
            outputStream.writeInt(autoAwayDelay);
            
            outputStream.writeUTF(defGcRoom);
            
            outputStream.writeBoolean(altInput);
            
            outputStream.writeInt(isbottom);
            
            outputStream.writeInt(confMessageCount);
            
            outputStream.writeBoolean(newMenu);
            
            outputStream.writeBoolean(lightState);
			
            outputStream.writeBoolean(autoSubscribe);
            
            outputStream.writeBoolean(lastMessages);
            
            outputStream.writeBoolean(setAutoStatusMessage); //outputStream.writeBoolean(setKeyBlockStatus);
            
            outputStream.writeInt(autoAwayType);
            
            outputStream.writeBoolean(autoScroll);
            
            outputStream.writeBoolean(popUps);
            
            outputStream.writeBoolean(showResources);
            
            outputStream.writeBoolean(antispam);
            
            outputStream.writeBoolean(enableVersionOs);
            
	} catch (Exception e) {
            //e.printStackTrace();
    }
	
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
        // возвращает defvalue, е�?ли атрибут не �?уще�?твует или имеет неправильный формат
        return defvalue;
    }
    
    public final int getIntProperty(final String key, final int defvalue) {
	try {
	    String s=Bombus.getInstance().getAppProperty(key);
	    return Integer.parseInt(s); //throws NullPointerException or NumberFormatException
	} catch (Exception e) { }
        // возвращает defvalue, е�?ли атрибут не �?уще�?твует или имеет неправильный формат
	return defvalue;
    }
    
    public final char getCharProperty(final String key, final char defvalue) {
	try {
	    String s=Bombus.getInstance().getAppProperty(key);
	    return s.charAt(0); //throws NullPointerException или IndexOutOfBoundsException
	} catch (Exception e) {	}
        // возвращает defvalue, е�?ли атрибут не �?уще�?твует или имеет неправильный формат
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
        // возвращает defvalue, е�?ли атрибут не �?уще�?твует 
        return defvalue;
    }
    
}
