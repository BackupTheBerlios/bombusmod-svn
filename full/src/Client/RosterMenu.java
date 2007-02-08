/*
 * RosterMenu.java
 *
 * Created on 24 �������� 2006 �., 17:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Client;

import Conference.Bookmarks;
import Conference.ConferenceForm;
import Conference.MucContact;
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
        
        if (Version.getPlatformName().startsWith("SIE-")) {
            if (cf.lightState) {
                addItem("TurnOff Light", 12,0x0f31);
                sd.roster.lightState=1;
                sd.roster.setLight(true);
            } else {
                addItem("TurnOn Light", 12,0x0f31);
                sd.roster.lightState=0;
                sd.roster.setLight(false);
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
                if (StaticData.getInstance().roster.lightState==1) {
                    StaticData.getInstance().roster.setLight(false);
                    StaticData.getInstance().roster.lightState=0;
                    cf.lightState=false;
                    cf.saveToStorage();
                } else {
                    StaticData.getInstance().roster.setLight(true);
                    StaticData.getInstance().roster.lightState=1;
                    cf.lightState=true;
                    cf.saveToStorage();
                }
                break;
            }
	}
    }
}