/*
 * RosterMenu.java
 *
 * Created on 24 �������� 2006 �., 17:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Client;

import Conference.ConferenceForm;
import Conference.MucContact;
import archive.ArchiveList;
import javax.microedition.lcdui.Display;
import locale.SR;
import midlet.Bombus;
import ui.Menu;
import ui.MenuItem;

public class RosterMenu extends Menu
{

    private Object o;

    /** Creates a new instance of RosterToolsMenu */
    public RosterMenu(Display display, Object o) {
        super("Main menu");
        this.o=o;
        addItem(SR.MS_ITEM_ACTIONS, 0);
        addItem(SR.MS_STATUS_MENU, 1);
        addItem(SR.MS_ACTIVE_CONTACTS, 2);
        addItem(SR.MS_ALERT_PROFILE_CMD, 3);
        addItem(SR.MS_CONFERENCE, 4);
        addItem(SR.MS_ARCHIVE, 5);
        addItem(SR.MS_ADD_CONTACT, 6);
        addItem(SR.MS_TOOLS, 7);    
        addItem(SR.MS_ACCOUNT_, 8);
        addItem(SR.MS_ABOUT, 10);
        addItem(SR.MS_APP_QUIT, 11);
    
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
                if (connected) new RosterItemActions(display, o); 
                break;
	    case 1: //status
                new StatusSelect(display);
		return;
            case 2: //active
                new ActiveContacts(display, null);
		break;
            case 3: //alert
                new AlertProfile(display);
		break;
            case 4: //conference
                if (connected) new ConferenceForm(display);
                break;
            case 5: //archive
                new ArchiveList(display, null);
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
                new RosterToolsMenu(display);
		break;
            case 8: //account
                new AccountSelect(display, false);
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
	}
    }
}