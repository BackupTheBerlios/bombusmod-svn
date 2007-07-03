/*
 * EntityCaps.java
 *
 * Created on 17 �?юнь 2007 г., 2:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Client;

import Info.Version;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;

/**
 *
 * @author Evg_S
 */
public class EntityCaps implements JabberBlockListener{
    
    /** Creates a new instance of EntityCaps */
    public EntityCaps() {}

    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        if (!data.getTypeAttribute().equals("get")) return BLOCK_REJECTED;
        
        JabberDataBlock query=data.getChildBlock("query");
        if (query==null) return BLOCK_REJECTED;
        String node=query.getAttribute("node");
        
        
        if (query.isJabberNameSpace("http://jabber.org/protocol/caps")) {
            if (!node.startsWith(BOMBUS_NAMESPACE))
                return BLOCK_REJECTED;
        } else if (query.isJabberNameSpace("http://jabber.org/protocol/disco#info")) {
            if (node==null) return BLOCK_REJECTED;
        } else return BLOCK_REJECTED;
        
        JabberDataBlock result=new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
        result.addChild(query);
        
        JabberDataBlock identity=query.addChild("identity", null);
        identity.setAttribute("category","client");
        identity.setAttribute("type","mobile");
        identity.setAttribute("name", "BombusMod");

//#ifdef MOOD
//#         if (node.endsWith("#mood+notify")) {
//#             query.addChild("feature", null).setAttribute("var","http://jabber.org/protocol/mood+notify");
//#         } else {
//#endif
            for (int i=0; i<features.length; i++) {
                query.addChild("feature", null).setAttribute("var",features[i]);
            }
//#ifdef MOOD
//#         }
//#endif
        
        StaticData.getInstance().roster.theStream.send(result);
        
        return BLOCK_PROCESSED;
    }

    public static JabberDataBlock presenceEntityCaps() {
        JabberDataBlock c=new JabberDataBlock("c", null, null);
        c.setAttribute("xmlns", "http://jabber.org/protocol/caps");
        c.setAttribute("node", BOMBUS_NAMESPACE);
        c.setAttribute("ver", Version.getVersionNumber());
//#ifdef MOOD
//#         if (Config.getInstance().userMoods)
//#             c.setAttribute("ext", "mood+notify");
//#endif
        return c;
    }
    
    private final static String BOMBUS_NAMESPACE=Version.getUrl();
    
    private final static String features[]={
        "jabber:iq:version",
        "jabber:x:data",
        "jabber:iq:last",
        "jabber:iq:time",
        "jabber:x:event",
        "http://jabber.org/protocol/disco#info",
        "http://www.xmpp.org/extensions/xep-0199.html#ns",
        "http://jabber.org/protocol/muc",
        "http://jabber.org/protocol/si",
        "http://jabber.org/protocol/si/profile/file-transfer",
//#ifdef MOOD
//#         "http://jabber.org/protocol/mood+notify",
//#endif
        "http://jabber.org/protocol/ibb"
    };
}
