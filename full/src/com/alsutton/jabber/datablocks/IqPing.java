/*
 * IqPing.java
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

package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.JabberDataBlock;
import ui.Time;

public class IqPing extends Iq{

    public IqPing(JabberDataBlock request) {
        super(request.getAttribute("from"),
              Iq.TYPE_RESULT,
              request.getAttribute("id") );
/*
 <iq from='juliet@capulet.lit/chamber'
    to='romeo@montague.lit/home' 
    id='e2e1'
    type='result'/>
 */
    }
    
    public IqPing(String to) {
/*
<iq from='romeo@montague.lit/home' 
    to='juliet@capulet.lit/chamber'
    type='get' 
    id='e2e1'>
  <ping xmlns='http://www.xmpp.org/extensions/xep-0199.html#ns'/>
</iq>
 */
        super(to, Iq.TYPE_GET, "_ping");
        addChild("ping",null).setNameSpace("http://www.xmpp.org/extensions/xep-0199.html#ns");
    }
}
