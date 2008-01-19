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
//#             JabberDataBlock query=data.findNamespace("query", "jabber:iq:private");
//#             if (query==null) return BLOCK_REJECTED;
//#             
//#             cs=query.findNamespace("cs", CONFIG_NS);
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
//#         JabberDataBlock query=iq.addChildNs("query", "jabber:iq:private");
//#         JabberDataBlock cs=query.addChildNs("cs", CONFIG_NS);
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
//#             //cs.addChild("blFlash", (cf.blFlash)?"1":"0");
//#             cs.addChild("memMonitor", (cf.memMonitor)?"1":"0");
//#             
//#             cs.addChild("font1", Integer.toString(cf.font1));
//#             cs.addChild("font2", Integer.toString(cf.font2));
//#             
//#             cs.addChild("autoFocus", (cf.autoFocus)?"1":"0");
//#             
//#             cs.addChild("lang", cf.lang);
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
//#             
//#             cs.addChild("messageLimit", Integer.toString(cf.messageLimit));
//#             
//#             cs.addChild("eventDelivery", (cf.eventDelivery)?"1":"0");
//#             
//#             cs.addChild("transliterateFilenames", (cf.transliterateFilenames)?"1":"0");
//#             
//#             cs.addChild("rosterStatus", (cf.rosterStatus)?"1":"0");
//#             cs.addChild("queryExit", (cf.queryExit)?"1":"0");
//#             //cs.addChild("showLastAppearedContact", (cf.showLastAppearedContact)?"1":"0");
//#             cs.addChild("showBalloons", (cf.showBalloons)?"1":"0");
//#             
//#             cs.addChild("userKeys", (cf.userKeys)?"1":"0");
//#             cs.addChild("msglistLimit", Integer.toString(cf.msglistLimit));
//#             
//#             cs.addChild("useTabs", (cf.useTabs)?"1":"0");
//#             
//#             cs.addChild("autoSubscribe", Integer.toString(cf.autoSubscribe));
//#         }
//#         //System.out.println(iq.toString());
//#         roster.theStream.send(iq);
//#     }
//#     
//#     protected void loadFromPrivateStorage(){
//# 	try {
//# 	    cf.accountIndex=cf.getIntProperty("accountIndex",-1);
//# 	    cf.showOfflineContacts=cf.getBooleanProperty("showOfflineContacts",false);
//# 	    cf.fullscreen=cf.getBooleanProperty("fullscreen",true);
//# 	    cf.def_profile = cf.getIntProperty("def_profile",0);
//# 	    cf.smiles=cf.getBooleanProperty("smiles",true);
//# 	    cf.showTransports=cf.getBooleanProperty("showTransports",true);
//# 	    cf.selfContact=cf.getBooleanProperty("selfContact",false);
//# 
//# 	    cf.ignore=cf.getBooleanProperty("ignore",false);
//#             cf.collapsedGroups=cf.getBooleanProperty("collapsedGroups",false);
//# 	    cf.eventComposing=cf.getBooleanProperty("eventComposing",true);
//# 	    
//# 	    cf.gmtOffset=cf.getIntProperty("gmtOffset",0);
//# 	    cf.locOffset=cf.getIntProperty("locOffset",0);
//# 	    
//# 	    cf.autoLogin=cf.getBooleanProperty("autoLogin",true);
//# 	    cf.autoJoinConferences=cf.getBooleanProperty("autoJoinConferences",true);
//# 	    
//# 	    cf.popupFromMinimized=cf.getBooleanProperty("popupFromMinimized",true);
//# 	    
//# 	    //cf.blFlash=cf.getBooleanProperty("blFlash",true);
//# 	    cf.memMonitor=cf.getBooleanProperty("memMonitor",true);
//#             
//#             cf.font1=cf.getIntProperty("font1",0);
//#             cf.font2=cf.getIntProperty("font2",0);
//#             
//#             cf.autoFocus=cf.getBooleanProperty("autoFocus",false);
//#             
//#             cf.lang=cf.getStringProperty("lang","en");
//#             
//#             cf.storeConfPresence=cf.getBooleanProperty("storeConfPresence",true);
//#             
//#             cf.capsState=cf.getBooleanProperty("capsState",true);
//# 	    
//#             cf.textWrap=cf.getIntProperty("textWrap",0);
//#             
//#             cf.loginstatus=cf.getIntProperty("loginstatus",0);
//# 
//#             cf.msgPath=cf.getStringProperty("msgPath","");
//#             cf.msgLog=cf.getBooleanProperty("msgLog",false);
//#             cf.msgLogPresence=cf.getBooleanProperty("msgLogPresence",false);
//#             cf.msgLogConfPresence=cf.getBooleanProperty("msgLogConfPresence",false);
//#             cf.msgLogConf=cf.getBooleanProperty("msgLogConf",false);
//#             cf.cp1251=cf.getBooleanProperty("cp1251",true);
//#             
//#             cf.autoAwayDelay=cf.getIntProperty("autoAwayDelay",5);
//#         
//#             cf.defGcRoom=cf.getStringProperty("defGcRoom","bombusmod@conference.jabber.ru");
//#             
//#             cf.altInput=cf.getBooleanProperty("altInput",false);
//#             
//#             cf.isbottom=cf.getIntProperty("isbottom",2);
//#             
//#             cf.confMessageCount=cf.getIntProperty("confMessageCount",20);
//#             
//#             cf.newMenu=cf.getBooleanProperty("newMenu",false);
//#             
//#             cf.lightState=cf.getBooleanProperty("lightState",true);
//#             
//#             cf.lastMessages=cf.getBooleanProperty("lastMessages",false);
//# 
//#             cf.setAutoStatusMessage=cf.getBooleanProperty("setAutoStatusMessage",false);
//#             
//#             cf.autoAwayType=cf.getIntProperty("autoAwayType",0);
//#             
//#             cf.autoScroll=cf.getBooleanProperty("autoScroll",true);
//#             
//#             cf.popUps=cf.getBooleanProperty("popUps",true);
//#             
//#             cf.showResources=cf.getBooleanProperty("showResources",true);
//#             
//#             cf.antispam=cf.getBooleanProperty("antispam",true);
//#             
//#             cf.enableVersionOs=cf.getBooleanProperty("enableVersionOs",true);
//#             
//#             cf.messageLimit=cf.getIntProperty("messageLimit",300);
//#             
//#             cf.eventDelivery=cf.getBooleanProperty("eventDelivery",true);
//#             
//#             cf.transliterateFilenames=cf.getBooleanProperty("transliterateFilenames",false);
//#             
//#             cf.rosterStatus=cf.getBooleanProperty("rosterStatus",true);
//#             cf.queryExit=cf.getBooleanProperty("queryExit",false);
//#             //cf.showLastAppearedContact=cf.getBooleanProperty("showLastAppearedContact",false);
//#             
//#             VirtualList.showBalloons=cf.showBalloons=cf.getBooleanProperty("showBalloons",false);
//#             
//#             cf.userKeys=cf.getBooleanProperty("userKeys",false);
//#             cf.msglistLimit=cf.getIntProperty("msglistLimit",100);
//#             
//#             cf.useTabs=cf.getBooleanProperty("useTabs",true);
//#             
//#             cf.autoSubscribe=cf.getIntProperty("autoSubscribe", cf.SUBSCR_ASK);
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
//#             VirtualList.userKeys=cf.userKeys;
//# 	} catch (Exception e) {}
//#     }
//#endif
}
