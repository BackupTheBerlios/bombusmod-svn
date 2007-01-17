/*
 * IqTimeReply.java
 *
 * Created on 10 Сентябрь 2005 г., 23:15
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.JabberDataBlock;
import ui.Time;

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
    
    public IqTimeReply(String to) {
        super(to, Iq.TYPE_GET, "time");
        addChild("query",null).setNameSpace("jabber:iq:time");
    }

    public static String dispatchTime(JabberDataBlock data) {
        if (!data.isJabberNameSpace("jabber:iq:time")) return "unknown time namespace";
        StringBuffer tm=new StringBuffer();
        String field=data.getChildBlockText("display");
        System.out.println(field);
        if (field.length()>0) {
                tm.append(field);
        }
        return tm.toString();
    }
}
