/*
 * UrnXmppTimeReply.java
 */

package com.alsutton.jabber.datablocks;

import Client.Config;
import com.alsutton.jabber.JabberDataBlock;

public class UrnXmppTimeReply extends Iq{
    public UrnXmppTimeReply(JabberDataBlock request) {
        super(request.getAttribute("from"),
              Iq.TYPE_RESULT,
              request.getAttribute("id") );
        JabberDataBlock query=addChild("time",null);
        query.setNameSpace("urn:xmpp:time");
	query.addChild("tzo", "+0"+Integer.toString(Config.getInstance().gmtOffset)+":00");
        query.addChild("utc",ui.Time.utcLocalTime()+"Z");
    }
}
