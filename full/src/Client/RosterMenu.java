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

import Conference.Bookmarks;
import Conference.ConferenceForm;
import Conference.MucContact;
import Info.Phone;
import Info.Version;
import archive.ArchiveList;
import images.RosterIcons;
import javax.microedition.lcdui.Display;
import locale.SR;
import midlet.Bombus;
import ui.IconTextElement;
import ui.Menu;
import ui.MenuItem;

public class RosterMenu extends Menu
{

    private Object o;
    
    private Config cf=Config.getInstance();
    private StaticData sd=StaticData.getInstance();

    /** Creates a new instance of RosterToolsMenu */
    public RosterMenu(Display display, Object o) {
        super(SR.MS_MAIN_MENU);
        this.o=o;
        addItem(SR.MS_ITEM_ACTIONS, 0, 0x0f27);
        addItem(SR.MS_STATUS_MENU, 1, 0x0f16);
        addItem(SR.MS_ACTIVE_CONTACTS, 2, 0x0f21);
        addItem(SR.MS_ALERT_PROFILE_CMD, 3, 0x0f17);
        addItem(SR.MS_CONFERENCE, 4,0x40);
        addItem(SR.MS_ARCHIVE, 5,0x0f12);
        addItem(SR.MS_ADD_CONTACT, 6, 0x0f02);
        addItem(SR.MS_TOOLS, 7,0x0f24);    
        addItem(SR.MS_ACCOUNT_, 8,0x0f01);
        
        if (Phone.PhoneManufacturer()==Phone.SIEMENS || Phone.PhoneManufacturer()==Phone.SIEMENS2) {
            switch (cf.lightType%2) {
                case 0: { //off
                    addItem("Включить свет", 12,0x0f31);
                    break;
                }
                case 1: { //on
                    addItem("Выключить свет", 12,0x0f31);
                    break;
                }
            }
        }
        addItem(SR.MS_ABOUT, 10,0x0f04);
        addItem(SR.MS_APP_QUIT, 11,0x0f22);
    
	attachDisplay(display);
    }
    public void eventOk(){
	destroyView();
        boolean connected= ( StaticData.getInstance().roster.theStream != null );
	MenuItem me=(MenuItem) getFocusedObject();
        
	if (me==null)  return;
        
	int index=me.index;
	switch (index) {
	    case 0: //actions
                if (connected) new RosterItemActions(display, o, -1).setParentView(StaticData.getInstance().roster); 
                break;
	    case 1: //status
                StaticData.getInstance().roster.reconnectCount=0; 
				new StatusSelect(display, null).setParentView(StaticData.getInstance().roster);
		break;
            case 2: //active
                new ActiveContacts(display, null).setParentView(StaticData.getInstance().roster);
		break;
            case 3: //alert
                new AlertProfile(display).setParentView(StaticData.getInstance().roster);
		break;
            case 4: //conference
                if (connected) {
                   new Bookmarks(display, null).setParentView(StaticData.getInstance().roster);
                }
                break;
            case 5: //archive
                new ArchiveList(display, null, -1).setParentView(StaticData.getInstance().roster);
		break;
            case 6: {//add contact
                if (connected)  {
                    Contact cn=null;
                    if (o instanceof Contact) {
                        cn=(Contact)o;
                        if (cn.getGroupType()!=Groups.TYPE_NOT_IN_LIST && cn.getGroupType()!=Groups.TYPE_SEARCH_RESULT) cn=null;
                    }
                    if (o instanceof MucContact) { cn=(Contact)o; }
                    new ContactEdit(display, cn);
                    break;
                }
            }
            case 7: //tools
                new RosterToolsMenu(display).setParentView(StaticData.getInstance().roster);
		break;
            case 8: //account
                new AccountSelect(display, false).setParentView(StaticData.getInstance().roster);
		break; 
            case 10: //about
                new Info.InfoWindow(display);
		break; 
	    case 11: {//quit
                StaticData.getInstance().roster.destroyView();
                StaticData.getInstance().roster.logoff();
                Bombus.getInstance().notifyDestroyed();
                return;
	    }
	    case 12: {//light
                switch (cf.lightType%2) {
                    case 0: { //off
                        sd.roster.lightType=1;
                        sd.roster.setLight(false);
                        cf.lightType=1;
                        cf.saveToStorage();
                        break;
                    }
                    case 1: { //on
                        sd.roster.lightType=2;
                        sd.roster.setLight(true);
                        cf.lightType=2;
                        cf.saveToStorage();
                        break;
                    }
                }
                break;
            }
	}
    }
}