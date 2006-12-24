/*
 * RosterToolsMenu.java
 *
 * Created on 11 Декабрь 2005 г., 20:43
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import javax.microedition.lcdui.Display;
import locale.SR;
import ui.Menu;
import ui.MenuItem;
/**
 *
 * @author EvgS
 */
public class RosterToolsMenu
        extends Menu {
    
    /** Creates a new instance of RosterToolsMenu */
    public RosterToolsMenu(Display display) {
        super(SR.MS_JABBER_TOOLS);
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
            case 3:
                new ConfigForm(display);
                return;
        }
    }
}