/*
 * DiscoInfo.java
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

import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Enumeration;
import java.util.Vector;

public class DiscoInfo implements JabberBlockListener{
    public int blockArrived(JabberDataBlock data) {
        try {
            if (!(data instanceof Iq)) return JabberBlockListener.BLOCK_REJECTED;
            if (data.getAttribute("id").equals("getServerFeatures")) {
               
                Vector serverFeatures=new Vector();
                //System.out.println(data.toString());
                for (Enumeration e=data.findNamespace("http://jabber.org/protocol/disco#info").getChildBlocks().elements(); e.hasMoreElements(); ){
                    JabberDataBlock feature=(JabberDataBlock) e.nextElement();
                    
                    if (feature.getTagName().equals("feature")) {
                        String feat=feature.getAttribute("var");
                        if (feat!=null && feat.length()>0)
                            serverFeatures.addElement(feature.getAttribute("var"));
                    }
//#ifdef MOOD
//#                     else if (feature.getTagName().equals("identity")) {
//#                         if (feature.getAttribute("category").equals("pubsub"))
//#                             if (feature.getAttribute("type").equals("pep"))
//#                                 StaticData.getInstance().roster.useUserMood=true;                                
//#                     }
//#endif
                }
                
                StaticData.getInstance().roster.serverFeatures=serverFeatures;
                StaticData.getInstance().roster.redraw();
                return JabberBlockListener.NO_MORE_BLOCKS;
            }
        } catch (Exception e) { }
        return JabberBlockListener.BLOCK_REJECTED;
    }
    
    public DiscoInfo() {
        JabberDataBlock request=new Iq(StaticData.getInstance().account.getServer(), Iq.TYPE_GET, "getServerFeatures");
        JabberDataBlock query=request.addChildNs("query", "http://jabber.org/protocol/disco#info");
        StaticData.getInstance().roster.theStream.send(request);
    }
}
