/*
 * RosterMenu.java
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
 *
 */

package Client;
//#ifdef NEW_MENU
//# import Conference.Bookmarks;
//# import Conference.MucContact;
//#ifdef MOOD
//# import UserMood.MoodSelect;
//#endif
//#ifdef ARCHIVE
//# import archive.ArchiveList;
//#endif
//# import images.RosterIcons;
//# import javax.microedition.lcdui.Display;
//# import locale.SR;
//# import midlet.BombusMod;
//# import ui.Menu;
//# import ui.MenuItem;
//#endif

public class RosterMenu 
//#ifdef NEW_MENU
//#         extends Menu
//#endif
{
//#ifdef NEW_MENU
//#     private Object o;
//#     
//#     private Config cf=Config.getInstance();
//#     private StaticData sd=StaticData.getInstance();
//#     
//#     private Roster roster=StaticData.getInstance().roster;
//# 
//#     /** Creates a new instance of RosterToolsMenu */
//#     public RosterMenu(Display display, Object o) {
//#         super(SR.MS_MAIN_MENU);
//#         this.o=o;
//#         addItem(SR.MS_ITEM_ACTIONS, 0, 0x0f27);
//#         addItem(SR.MS_STATUS_MENU, 1, 0x0f16);
//#         addItem(SR.MS_ACTIVE_CONTACTS, 2, 0x0f21);
//#ifdef MOOD
//#         if (roster.useUserMood)
//#             addItem(SR.MS_USER_MOOD, 3, 0x0f16);
//#endif
//#         addItem(SR.MS_ALERT_PROFILE_CMD, 4, 0x0f17);
//#         addItem(SR.MS_CONFERENCE, 5, RosterIcons.ICON_GROUPCHAT_INDEX);
//#ifdef ARCHIVE
//#         addItem(SR.MS_ARCHIVE, 6,0x0f12);
//#endif
//#         addItem(SR.MS_ADD_CONTACT, 7, 0x0f02);
//#         addItem(SR.MS_TOOLS, 8,0x0f24);    
//#         addItem(SR.MS_ACCOUNT_, 9,0x0f01);
//#         addItem(SR.MS_ABOUT, 10,0x0f04);
//#         addItem(SR.MS_CLEAN_ALL_MESSAGES, 110, RosterIcons.ICON_TRASHCAN_INDEX);
//#         addItem(SR.MS_APP_QUIT, 12,0x0f22);
//#     
//# 	attachDisplay(display);
//#     }
//#     public void eventOk(){
//# 	destroyView();
//#         boolean connected= ( roster.theStream != null );
//# 	MenuItem me=(MenuItem) getFocusedObject();
//#         
//# 	if (me==null)  return;
//#         
//# 	int index=me.index;
//# 	switch (index) {
//# 	    case 0: //actions
//#                 if (connected) {
//#                     new RosterItemActions(display, o, -1).setParentView(roster);
//#                 } 
//#                 break;
//# 	    case 1: //status
//#                 StaticData.getInstance().roster.reconnectCount=0; 
//# 		new StatusSelect(display, null).setParentView(roster);
//# 		break;
//#             case 2: //active
//#                 new ActiveContacts(display, null).setParentView(roster);
//# 		break;
//#ifdef MOOD
//#             case 3: //user mood
//#                 if (connected) {
//#                     new MoodSelect(display, null).setParentView(roster);
//#                 }
//# 		break;
//#endif
//#             case 4: //alert
//#                 new AlertProfile(display).setParentView(roster);
//# 		break;
//#             case 5: //conference
//#                 if (connected) {
//#                    new Bookmarks(display, null).setParentView(roster);
//#                 }
//#                 break;
//#ifdef ARCHIVE
//#             case 6: //archive
//#                 new ArchiveList(display, null, -1).setParentView(roster);
//# 		break;
//#endif
//#             case 7: {//add contact
//#                 if (connected)  {
//#                     Contact cn=null;
//#                     if (o instanceof Contact) {
//#                         cn=(Contact)o;
//#                         if (cn.getGroupType()!=Groups.TYPE_NOT_IN_LIST && cn.getGroupType()!=Groups.TYPE_SEARCH_RESULT) cn=null;
//#                     }
//#                     if (o instanceof MucContact) { cn=(Contact)o; }
//#                     new ContactEdit(display, cn);
//#                     break;
//#                 }
//#             }
//#             case 8: //tools
//#                 new RosterToolsMenu(display).setParentView(roster);
//# 		break;
//#             case 9: //account
//#                 new AccountSelect(display, false).setParentView(roster);
//# 		break; 
//#             case 10: //about
//#                 new Info.InfoWindow(display);
//# 		break; 
//#             case 11: //cleanup All Histories
//#                 roster.cleanupAllHistories();
//# 		break; 
//# 	    case 12: {//quit
//#                 roster.quit();
//#                 return;
//# 	    }
//# 	}
//#     }
//#endif
}
