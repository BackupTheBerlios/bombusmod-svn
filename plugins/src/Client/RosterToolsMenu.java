/*
 * RosterToolsMenu.java
 *
 * Created on 11.12.2005, 20:43
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
//#ifdef PRIVACY
//# import PrivacyLists.PrivacySelect;
//#endif
//#ifdef SERVICE_DISCOVERY
//# import ServiceDiscovery.ServiceDiscovery;
//#endif
//#if (FILE_IO && HISTORY)
//# import History.HistoryConfig;
//#endif
import Stats.Stats;
import javax.microedition.lcdui.Display;
import locale.SR;
//#ifdef COLORS
//# import ui.ColorForm;
//#endif
import ui.Menu;
import ui.MenuItem;
import util.strconv;
import vcard.VCard;
import vcard.vCardForm;

public class RosterToolsMenu
        extends Menu {
    
    /** Creates a new instance of RosterToolsMenu */
    public RosterToolsMenu(Display display) {
        super(SR.MS_JABBER_TOOLS);
//#ifdef SERVICE_DISCOVERY
//#         addItem(SR.MS_DISCO, 0, 0x13);
//#endif
//#ifdef PRIVACY
//#         addItem(SR.MS_PRIVACY_LISTS, 1, 0x46);
//#endif
        addItem(SR.MS_MY_VCARD, 2, 0x0f16);
        addItem(SR.MS_OPTIONS, 3, 0x0f03);
//#if (FILE_IO && HISTORY)
//#         addItem(SR.MS_HISTORY_OPTIONS, 4, 0x0f01);
//#endif
        
//#if (FILE_IO)
        addItem(SR.MS_ROOT,5, 0x0f10);
//#endif
//#if (FILE_IO && FILE_TRANSFER)
//#         addItem(SR.MS_FILE_TRANSFERS, 6, 0x0f34);
//#endif
//#ifdef COLORS
//#         addItem(SR.MS_COLOR_TUNE, 7, 0x0f25);
//#endif
        addItem(SR.MS_SOUNDS_OPTIONS, 8, 0x0f17);
//#ifdef POPUPS
//#         addItem(SR.MS_STATS, 9, 0x0f30);
//#endif
        addItem(SR.MS_CHECK_UPDATE, 10, 0x46);
        
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
//#ifdef SERVICE_DISCOVERY
//#             case 0: // Service Discovery
//#                 if (connected) new ServiceDiscovery(display, null, null);
//#                 break;
//#endif
//#ifdef PRIVACY
//#             case 1: // Privacy Lists
//#                 if (connected) new PrivacySelect(display);
//#                 break;
//#endif
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
//#if (FILE_IO && HISTORY)
//#             case 4: //history
//#                 new HistoryConfig(display);
//#                 return;
//#endif 
//#if (FILE_IO)
            case 5:
                new io.file.browse.Browser(null, display, null, false);
                return;
//#endif
//#if (FILE_IO && FILE_TRANSFER)
//#             case 6:
//#                 new io.file.transfer.TransferManager(display);
//#                 return;
//#endif
//#ifdef COLORS
//#             case 7:
//#                 new ColorForm(display);
//#                 return;
//#endif
            case 8:
                new AlertCustomizeForm(display);
                return;
//#ifdef POPUPS
//#             case 9: //traffic stats
//#                 StringBuffer str= new StringBuffer();
//#                 Stats stats=Stats.getInstance();
//#                 str.append("Traffic stats:\nAll(");
//#                 str.append(stats.getSessionsCount());
//#                 str.append("): ");
//#                 
//#                 str.append(strconv.getSizeString(stats.getAllTraffic()));
//#                 
//#                 str.append("\nPrevious: ");
//#                 str.append(strconv.getSizeString(stats.getLatest()));
//#                 
//#                 str.append("\nCurrent: ");
//#                 str.append(strconv.getSizeString(stats.getGPRS()));
//# 
//#                 if (connected)
//#                     str.append(StaticData.getInstance().roster.theStream.getStreamStats());
//# 
//#                 StaticData.getInstance().roster.setWobbler(str.toString());
//#                 str=null;
//#                 return;
//#endif
            case 10:
                if (! connected) break;
                new util.LastVersion(display);
                return;
/*
            case 10:
                new archive.DebugDumpArchive(display);
                return;
*/
        }
    }
}