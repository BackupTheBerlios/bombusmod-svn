/*
 * IqTimeReply.java
 *
 * Created on 10.09.2005, 23:15
 *
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
 */

package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.JabberDataBlock;

/**
 *
 * @author EvgS
 */
public class IqTimeReply extends Iq{
    
    /** Creates a new instance of IqTimeReply */
    public IqTimeReply(JabberDataBlock request) {
        super(request.getAttribute("from"),
              Iq.TYPE_RESULT,
              request.getAttribute("id") );
        JabberDataBlock query=addChild("query",null);
        query.setNameSpace("jabber:iq:time");
        query.addChild("utc",ui.Time.utcLocalTime());
        query.addChild("display", ui.Time.dispLocalTime());
    }
}
