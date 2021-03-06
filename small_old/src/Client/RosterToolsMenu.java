/*
 * RosterToolsMenu.java
 *
 * Created on 11 Декабрь 2005 г., 20:43
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import ServiceDiscovery.ServiceDiscovery;
import javax.microedition.lcdui.Display;
import locale.SR;
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
        addItem("Service Discovery", 0); //locale
        addItem(SR.MS_MY_VCARD, 2);
        addItem(SR.MS_OPTIONS, 3);
        /*if (m.getItemCount()>0)*/
        
//#if (FILE_IO && FILE_TRANSFER)
        addItem("root",4);
        addItem("File Transfers", 5);
//#endif
        
        attachDisplay(display);
    }
    public void eventOk(){
        destroyView();
        boolean connected= ( StaticData.getInstance().roster.theStream != null );
        MenuItem me=(MenuItem) getFocusedObject();
        if (me==null)  return;
        int index=me.index;
        switch (index) {
            case 0: // Service Discovery
                if (connected) new ServiceDiscovery(display, null, null);
                break;
            case 2: {
                if (! connected) break;
                Contact c=StaticData.getInstance().roster.selfContact();
                if (c.vcard!=null) {
                    new vCardForm(display, c.vcard, true);
                    return;
                }
                VCard.request(c.getJid());
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
        }
    }
}