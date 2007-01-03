/*
 * RosterItemActions.java
 *
 * Created on 11 Декабрь 2005 г., 19:05
 *
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import Network.Presence;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.lcdui.Display;
import locale.SR;
import ui.IconTextElement;
import ui.Menu;
import ui.MenuItem;
/**
 *
 * @author EvgS
 */
public class RosterItemActions extends Menu{
    
    Object item;
    
    /** Creates a new instance of RosterItemActions */
    public RosterItemActions(Display display, Object item) {
	super(item.toString());
	this.item=item;
	
        if (item==null) return;
        boolean isContact=( item instanceof Contact );

	if (isContact) {
	    Contact contact=(Contact)item;
	    if (contact.getGroupType()!=Groups.TYPE_SELF) {
		addItem(SR.MS_EDIT,2); //locale
		addItem(SR.MS_SUBSCRIPTION,3); //locale
		addItem(SR.MS_DELETE,4); //locale
	    }
        }
	if (getItemCount()>0) attachDisplay(display);
	
    }
    
    public void eventOk(){
        try {
            final Roster roster=StaticData.getInstance().roster;
            boolean isContact=( item instanceof Contact );
            Contact c = null;
            Group g = null;
            if (isContact) c=(Contact)item; else g=(Group) item;
            
            MenuItem me=(MenuItem) getFocusedObject();
            if (me==null) {
                destroyView(); return;
            }
            int index=me.index;
            String to=null;
            if (isContact) to=(index<3)? c.getJid() : c.getBareJid();
            destroyView();
            switch (index) {
                case 2:
                    (new ContactEdit(display, c )).parentView=roster;
                    return; //break;
                    
                case 3: //subscription
                    new SubscriptionEdit(display, c);
                    return; //break;
                case 4:
                    roster.deleteContact((Contact)item);
                    return;
            }
            
            destroyView();
        } catch (Exception e) { e.printStackTrace();  }
    }
        
}
