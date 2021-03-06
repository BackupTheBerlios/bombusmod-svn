/*
 * PrivacyItem.java
 *
 * Created on 10.09.2005, 21:30
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

package PrivacyLists;
import images.RosterIcons;
import ui.*;
import com.alsutton.jabber.*;
import Client.*;

/**
 *
 * @author EvgS
 */
public class PrivacyItem extends IconTextElement{
    
    public final static String types[]={"jid", "group", "subscription", "ANY"};
    public final static int ITEM_JID=0;
    public final static int ITEM_GROUP=1;
    public final static int ITEM_SUBSCR=2;
    public final static int ITEM_ANY=3;

    public final static String actions[]={"allow", "deny"};
    public final static int ITEM_ALLOW=0;
    public final static int ITEM_BLOCK=1;

    public final static String stanzas[]={"message", "presence-in", "presence-out", "iq"};
    public final static int STANZA_MSG=0;
    public final static int STANZA_PRESENCE_IN=1;
    public final static int STANZA_PRESENCE_OUT=2;
    public final static int STANZA_IQ=3;
    
    public final static String subscrs[]={"none", "from", "to", "both"};
    
    int type;    //jid|group|subscription|ANY
    String value=new String();
    int action=1;
    int order;
    boolean stanzasSet[]=new boolean[4];
    
    public int getImageIndex(){
        return action+ RosterIcons.ICON_PRIVACY_ALLOW;
    }
    
    public int getColor() { return ColorScheme.LIST_INK; }
    
    public String toString() { return (type==ITEM_ANY)?"ANY":value; }
    
    /** Creates a new instance of PrivacyItem */
    public PrivacyItem() {
        super(RosterIcons.getInstance());
    }
    
    public PrivacyItem(JabberDataBlock item) {
        this();
        String t=item.getTypeAttribute();
        if (t==null) type=ITEM_ANY;
        else 
            for (type=0; type<2; type++) 
                if (t.equals(types[type])) break;
        value=item.getAttribute("value");
        action=item.getAttribute("action").equals("allow")?0:1;
        order=Integer.parseInt(item.getAttribute("order"));
        int index;
        for (index=0; index<4; index++) {
            if (item.getChildBlock(stanzas[index])!=null) stanzasSet[index]=true;
        }
    }
    
    public static PrivacyItem itemIgnoreList(){
        PrivacyItem item=new PrivacyItem();
        item.type=ITEM_GROUP;
        item.value=Groups.IGNORE_GROUP;
        item.stanzasSet[STANZA_IQ]=true;
        item.stanzasSet[STANZA_PRESENCE_OUT]=true;
        return item;
    }
    
    public JabberDataBlock constructBlock() {
        JabberDataBlock item=new JabberDataBlock("item", null, null);
        if (type!=ITEM_ANY) {
            item.setTypeAttribute(types[type]);
            item.setAttribute("value", value);
        }
        item.setAttribute("action", actions[action] );
        item.setAttribute("order", String.valueOf(order));
        int index;
        for (index=0; index<4; index++) {
            if (stanzasSet[index]) item.addChild(stanzas[index], null);
        }
        return item;
    }
	
    public String getTipString() {
        StringBuffer tip=new StringBuffer(actions[action]);
        tip.append(" if ");
        tip.append(types[type]);
        if (type!=ITEM_ANY) {
            tip.append('=');
            tip.append(value);
        }
        tip.append(' ');
        
        if ((stanzasSet[0] && stanzasSet[1] && stanzasSet[2] && stanzasSet[3]) 
        || !(stanzasSet[0] || stanzasSet[1] || stanzasSet[2] || stanzasSet[3])) { 
            tip.append("all stanzas"); 
        } else {
            for (int i=0; i<4; i++) {
                if (stanzasSet[i]) {
                    tip.append(stanzas[i]);
                    tip.append(' ');
                }
            }
        }
        return tip.toString();
    }

    public String getSecondString() {
        return null;
    }
}
