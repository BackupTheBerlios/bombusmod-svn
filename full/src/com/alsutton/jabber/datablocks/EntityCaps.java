/*
 * EntityCaps.java
 *
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
 *
 * Created on 5 ���� 2007 �., 10:06
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

package com.alsutton.jabber.datablocks;

import Client.Roster;
import Client.StaticData;
import Info.Version;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Enumeration;

public class EntityCaps extends Iq
{
    
    public static final String[] features = {
        "jabber:iq:version", 
        "jabber:iq:last",
        "jabber:iq:time",
        "jabber:x:data", 
        "http://jabber.org/protocol/disco#info",
        "http://jabber.org/protocol/muc",
        "http://www.xmpp.org/extensions/xep-0199.html#ns"
    };
    
    public EntityCaps(JabberDataBlock request) {
        super(request.getAttribute("from"),
            Iq.TYPE_RESULT,
            request.getAttribute("id") );

        JabberDataBlock query=addChild("query", null);
        query.setNameSpace("http://jabber.org/protocol/disco#info");
        query.setAttribute("node", Version.url);

        JabberDataBlock identity=query.addChild("identity", null);
        identity.setAttribute("category","client");
        identity.setAttribute("type","mobile");

        for (int i=0; i<features.length;i++) {
            JabberDataBlock feature=query.addChild("feature", null);
            feature.setAttribute("var",features[i]);
        }
    }
}
