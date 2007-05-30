/*
 * ConfigPrivateStorage.java
 *
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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
//#if SERVER_SIDE_CONFIG
//# import com.alsutton.jabber.JabberBlockListener;
//# import com.alsutton.jabber.JabberDataBlock;
//# import com.alsutton.jabber.datablocks.Iq;
//# import java.util.Enumeration;
//# import ui.VirtualList;
//#endif
public class ConfigPrivateStorage 
//#if SERVER_SIDE_CONFIG
//#     implements JabberBlockListener
//#endif    
{
//#if SERVER_SIDE_CONFIG    
//#     private final static String CONFIG_NS="Config";
//#     
//#     private Config cf=Config.getInstance();
//#     
//#     private JabberDataBlock cs;
//#     
//#     public ConfigPrivateStorage (boolean get) {
//#         requestGroupState(get);
//#     }
//#     
//#     public int blockArrived(JabberDataBlock data) {
//#         if (data instanceof Iq) 
//#             if (data.getTypeAttribute().equals("result")) {
//#             JabberDataBlock query=data.findNamespace("jabber:iq:private");
//#             if (query==null) return BLOCK_REJECTED;
//#             
//#             cs=query.findNamespace(CONFIG_NS);
//#             if (cs==null) return BLOCK_REJECTED;
//#             
//#             //System.out.println(cs.toString());
//#             
//#             loadFromPrivateStorage();
//# 
//#             return NO_MORE_BLOCKS;
//#         }
//#         return BLOCK_REJECTED;
//#     }
//# 
//#     public void requestGroupState(boolean get) {
//#         Roster roster=StaticData.getInstance().roster;
//#         if (!roster.isLoggedIn()) return;
//#         
//#         JabberDataBlock iq=new Iq(null, (get)? Iq.TYPE_GET : Iq.TYPE_SET, (get)? "queryCS" : "setCS");
//#         JabberDataBlock query=iq.addChild("query", null);
//#         query.setNameSpace("jabber:iq:private");
//#         JabberDataBlock cs=query.addChild("cs", null);
//#         cs.setNameSpace(CONFIG_NS);
//#         
//#         if (get) {
//#             roster.theStream.addBlockListener(this);
//#         } else {
//#             cs.addChild("accountIndex", Integer.toString(cf.accountIndex));
//#             cs.addChild("showOfflineContacts", (cf.showOfflineContacts)?"1":"0");
//#             
//#             cs.addChild("fullscreen", (cf.fullscreen)?"1":"0");
//#             cs.addChild("def_profile", Integer.toString(cf.def_profile));
//#             cs.addChild("smiles", (cf.smiles)?"1":"0");
//#             cs.addChild("showTransports", (cf.showTransports)?"1":"0");
//#             cs.addChild("selfContact", (cf.selfContact)?"1":"0");
//#             cs.addChild("ignore", (cf.ignore)?"1":"0");
//#             cs.addChild("collapsedGroups", (cf.collapsedGroups)?"1":"0");
//#             cs.addChild("eventComposing", (cf.eventComposing)?"1":"0");
//#             
//#             cs.addChild("gmtOffset", Integer.toString(cf.gmtOffset));
//#             cs.addChild("locOffset", Integer.toString(cf.locOffset));
//#             
//#             cs.addChild("autoLogin", (cf.autoLogin)?"1":"0");
//#             cs.addChild("autoJoinConferences", (cf.autoJoinConferences)?"1":"0");
//#             cs.addChild("popupFromMinimized", (cf.popupFromMinimized)?"1":"0");
//#             
//#             cs.addChild("blFlash", (cf.blFlash)?"1":"0");
//#             cs.addChild("memMonitor", (cf.memMonitor)?"1":"0");
//#             
//#             cs.addChild("font1", Integer.toString(cf.font1));
//#             cs.addChild("font2", Integer.toString(cf.font2));
//#             
//#             cs.addChild("autoFocus", (cf.autoFocus)?"1":"0");
//#             
//#             cs.addChild("lang", Integer.toString(cf.lang));
//#             
//#             cs.addChild("storeConfPresence", (cf.storeConfPresence)?"1":"0");
//#             cs.addChild("capsState", (cf.capsState)?"1":"0");
//#             
//#             cs.addChild("textWrap", Integer.toString(cf.textWrap));
//#             
//#             cs.addChild("loginstatus", Integer.toString(cf.loginstatus));
//#             
//#             cs.addChild("msgPath", cf.msgPath);
//#             cs.addChild("msgLog", (cf.msgLog)?"1":"0");
//#             cs.addChild("msgLogPresence", (cf.msgLogPresence)?"1":"0");
//#             cs.addChild("msgLogConfPresence", (cf.msgLogConfPresence)?"1":"0");
//#             cs.addChild("msgLogConf", (cf.msgLogConf)?"1":"0");
//#             cs.addChild("cp1251", (cf.cp1251)?"1":"0");
//#             
//#             cs.addChild("autoAwayDelay", Integer.toString(cf.autoAwayDelay));
//#             
//#             cs.addChild("defGcRoom", cf.defGcRoom);
//#             
//#             cs.addChild("altInput", (cf.altInput)?"1":"0");
//#             cs.addChild("isbottom", Integer.toString(cf.isbottom));
//#             cs.addChild("confMessageCount", Integer.toString(cf.confMessageCount));
//#             cs.addChild("newMenu", (cf.newMenu)?"1":"0");
//#             cs.addChild("lightState", (cf.lightState)?"1":"0");
//#             cs.addChild("autoSubscribe", (cf.autoSubscribe)?"1":"0");
//#             cs.addChild("lastMessages", (cf.lastMessages)?"1":"0");
//#             
//#             cs.addChild("setAutoStatusMessage", (cf.setAutoStatusMessage)?"1":"0");
//#             cs.addChild("autoAwayType", Integer.toString(cf.autoAwayType));
//#             
//#             cs.addChild("autoScroll", (cf.autoScroll)?"1":"0");
//#             cs.addChild("popUps", (cf.popUps)?"1":"0");
//#             cs.addChild("showResources", (cf.showResources)?"1":"0");
//#             cs.addChild("antispam", (cf.antispam)?"1":"0");
//#             cs.addChild("enableVersionOs", (cf.enableVersionOs)?"1":"0");
//#         }
//#         //System.out.println(iq.toString());
//#         roster.theStream.send(iq);
//#     }
//#     
//#     protected void loadFromPrivateStorage(){
//# 	try {
//# 	    cf.accountIndex=getIntProperty("accountIndex",-1);
//# 	    cf.showOfflineContacts=getBooleanProperty("showOfflineContacts",false);
//# 	    cf.fullscreen=getBooleanProperty("fullscreen",true);
//# 	    cf.def_profile = getIntProperty("def_profile",0);
//# 	    cf.smiles=getBooleanProperty("smiles",true);
//# 	    cf.showTransports=getBooleanProperty("showTransports",true);
//# 	    cf.selfContact=getBooleanProperty("selfContact",false);
//# 
//# 	    cf.ignore=getBooleanProperty("ignore",false);
//#             cf.collapsedGroups=getBooleanProperty("collapsedGroups",false);
//# 	    cf.eventComposing=getBooleanProperty("eventComposing",true);
//# 	    
//# 	    cf.gmtOffset=getIntProperty("gmtOffset",0);
//# 	    cf.locOffset=getIntProperty("locOffset",0);
//# 	    
//# 	    cf.autoLogin=getBooleanProperty("autoLogin",true);
//# 	    cf.autoJoinConferences=getBooleanProperty("autoJoinConferences",true);
//# 	    
//# 	    cf.popupFromMinimized=getBooleanProperty("popupFromMinimized",true);
//# 	    
//# 	    cf.blFlash=getBooleanProperty("blFlash",true);
//# 	    cf.memMonitor=getBooleanProperty("memMonitor",true);
//#             
//#             cf.font1=getIntProperty("font1",0);
//#             cf.font2=getIntProperty("font2",0);
//#             
//#             cf.autoFocus=getBooleanProperty("autoFocus",false);
//#             
//#             cf.lang=getIntProperty("lang",0);
//#             
//#             cf.storeConfPresence=getBooleanProperty("storeConfPresence",true);
//#             
//#             cf.capsState=getBooleanProperty("capsState",true);
//# 	    
//# 	    cf.textWrap=getIntProperty("textWrap",0);
//#             
//#             cf.loginstatus=getIntProperty("loginstatus",0);
//# 
//#             cf.msgPath=getStringProperty("msgPath","");
//#             cf.msgLog=getBooleanProperty("msgLog",false);
//#             cf.msgLogPresence=getBooleanProperty("msgLogPresence",false);
//#             cf.msgLogConfPresence=getBooleanProperty("msgLogConfPresence",false);
//#             cf.msgLogConf=getBooleanProperty("msgLogConf",false);
//#             cf.cp1251=getBooleanProperty("cp1251",true);
//#             
//#             cf.autoAwayDelay=getIntProperty("autoAwayDelay",5);
//#         
//#             cf.defGcRoom=getStringProperty("defGcRoom","bombusmod@conference.jabber.ru");
//#             
//#             cf.altInput=getBooleanProperty("altInput",false);
//#             
//#             cf.isbottom=getIntProperty("isbottom",2);
//#             
//#             cf.confMessageCount=getIntProperty("confMessageCount",20);
//#             
//#             cf.newMenu=getBooleanProperty("newMenu",false);
//#             
//#             cf.lightState=getBooleanProperty("lightState",true);
//# 			
//#             cf.autoSubscribe=getBooleanProperty("autoSubscribe",true);
//#             
//#             cf.lastMessages=getBooleanProperty("lastMessages",false);
//# 
//#             cf.setAutoStatusMessage=getBooleanProperty("setAutoStatusMessage",false);
//#             
//#             cf.autoAwayType=getIntProperty("autoAwayType",0);
//#             
//#             cf.autoScroll=getBooleanProperty("autoScroll",true);
//#             
//#             cf.popUps=getBooleanProperty("popUps",true);
//#             
//#             cf.showResources=getBooleanProperty("showResources",true);
//#             
//#             cf.antispam=getBooleanProperty("antispam",true);
//#             
//#             cf.enableVersionOs=getBooleanProperty("enableVersionOs",true);
//#             
//#             cf.lastProfile=cf.profile=cf.def_profile;
//#             if (cf.lastProfile==AlertProfile.VIBRA) cf.lastProfile=0;
//#             cf.updateTime();
//# 
//#             cf.saveToStorage();
//# 
//#             VirtualList.fullscreen=cf.fullscreen;
//#             VirtualList.isbottom=cf.isbottom;
//#             VirtualList.memMonitor=cf.memMonitor;
//#ifdef NEW_MENU
//#             VirtualList.newMenu=cf.newMenu;
//#endif
//# 	} catch (Exception e) {}
//#     }
//#     
//#     public final String getStringProperty(final String key, final String defvalue) {
//# 	try {
//# 	    String s=cs.getChildBlock(key).getText();
//#             //System.out.println(key+": "+s);
//# 	    return (s==null)?defvalue:s;
//# 	} catch (Exception e) {	}
//#         return defvalue;
//#     }
//#     
//#     public final int getIntProperty(final String key, final int defvalue) {
//# 	try {
//# 	    String s=cs.getChildBlock(key).getText();
//#             //System.out.println(key+": "+s);
//# 	    return Integer.parseInt(s);
//# 	} catch (Exception e) { }
//# 	return defvalue;
//#     }
//# 
//#     public final boolean getBooleanProperty(final String key, final boolean defvalue) {
//# 	try {
//# 	    String s=cs.getChildBlock(key).getText();
//#             //System.out.println(key+": "+s);
//# 	    if (s.equals("true")) return true;
//# 	    if (s.equals("yes")) return true;
//# 	    if (s.equals("1")) return true;
//#             return false;
//# 	} catch (Exception e) { }
//#         return defvalue;
//#     }
//#endif
}
