/*
 * RosterToolsMenu.java
 *
 * Created on 11 Декабрь 2005 г., 20:43
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import PrivacyLists.PrivacySelect;
import ServiceDiscovery.ServiceDiscovery;
import javax.microedition.lcdui.Display;
import locale.SR;
import ui.ColorForm;
import ui.Menu;
import ui.MenuItem;
import vcard.VCard;
import vcard.vCardForm;

/**
 *
 * @author EvgS
 */
public class RosterToolsMenu
        extends Menu {
    
    /** Creates a new instance of RosterToolsMenu */
    public RosterToolsMenu(Display display) {
        super(SR.MS_JABBER_TOOLS);
        addItem(SR.MS_DISCO, 0, 0x13);
        addItem(SR.MS_PRIVACY_LISTS, 1, 0x46);
        addItem(SR.MS_MY_VCARD, 2, 0x0f16);
        addItem(SR.MS_OPTIONS, 3, 0x0f03);
        
//#if (FILE_IO && FILE_TRANSFER)
//#         addItem(SR.MS_ROOT,4, 0x0f10);
//#         addItem(SR.MS_FILE_TRANSFERS, 5, 0x0f34);
//#endif
        addItem(SR.MS_COLOR_TUNE, 6, 0x0f25);
        addItem(SR.MS_SOUNDS_OPTIONS, 7, 0x0f17);
/*		
        addItem("ArchiveDump", 10);
*/        
        attachDisplay(display);
    }
    public void eventOk(){
        destroyView();
        boolean connected= ( StaticData.getInstance().roster.isLoggedIn() );
        MenuItem me=(MenuItem) getFocusedObject();
        if (me==null)  return;
        int index=me.index;
        switch (index) {
            case 0: // Service Discovery
                if (connected) new ServiceDiscovery(display, null, null);
                break;
            case 1: // Privacy Lists
                if (connected) new PrivacySelect(display);
                break;
            case 2: {
                if (! connected) break;
                Contact c=StaticData.getInstance().roster.selfContact();
                if (c.vcard!=null) {
                    new vCardForm(display, c.vcard, true);
                    return;
                }
                VCard.request(c.getBareJid(), c.getJid());
                return;
            }
            case 3:
                new ConfigForm(display);
                return;
//#if (FILE_IO)
            case 4:
                new io.file.browse.Browser(null, display, null, false);
                return;
            case 5:
                new io.file.transfer.TransferManager(display);
                return;
//#endif
            case 6:
                new ColorForm(display);
                return;
            case 7:
                new AlertCustomizeForm(display);
                return;
/*
            case 10:
                new archive.DebugDumpArchive(display);
                return;
*/
        }
    }
}