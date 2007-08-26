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
import com.ssttr.crypto.SHA1;

/**
 *
 * @author Evg_S
 */
public class EntityCaps implements JabberBlockListener{
    
    /** Creates a new instance of EntityCaps */
    public EntityCaps() {}
    
    private static String mood="ep-notify";

    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        if (!data.getTypeAttribute().equals("get")) return BLOCK_REJECTED;
        
        JabberDataBlock query=data.getChildBlock("query");
        if (query==null) return BLOCK_REJECTED;
        String node=query.getAttribute("node");
        
        if (!query.isJabberNameSpace("http://jabber.org/protocol/disco#info")) 
            return BLOCK_REJECTED;
        if (node!=null) 
            if (!node.startsWith(BOMBUS_NAMESPACE))
                return BLOCK_REJECTED;
        
        JabberDataBlock result=new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
        result.addChild(query);
        
        JabberDataBlock identity=query.addChild("identity", null);
        identity.setAttribute("category","client");
        identity.setAttribute("type","mobile");
        identity.setAttribute("name", "BombusMod");

//#ifdef MOOD
//#         if (node.endsWith("#"+mood)) {
//#             query.addChild("feature", null).setAttribute("var","http://jabber.org/protocol/mood");
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
	
    public static String ver=null;
    
    public static String calcVerHash() {
        if (ver!=null) return ver;
        
        SHA1 sha1=new SHA1();
        sha1.init();
        
        sha1.update("client/mobile");
        sha1.update("<");
        
        for (int i=0; i<features.length; i++) {
            sha1.update(features[i]);
            sha1.update("<");
        }
        
        sha1.finish();
        ver=sha1.getDigestBase64();
        
        return ver;
    }

    public static JabberDataBlock presenceEntityCaps() {
        JabberDataBlock c=new JabberDataBlock("c", null, null);
        c.setAttribute("xmlns", "http://jabber.org/protocol/caps");
        c.setAttribute("node", BOMBUS_NAMESPACE+'#'+Version.getVersionNumber());
        c.setAttribute("ver", calcVerHash());
        c.setAttribute("hash", "sha-1"); //todo: change to algo when caps 1.5 will be released
//#ifdef MOOD
//#         if (Config.getInstance().userMoods)
//#             c.setAttribute("ext", mood);
//#endif
        return c;
    }
    
    private final static String BOMBUS_NAMESPACE=Version.getUrl();
//features MUST be sorted
    private final static String features[]={
        "http://jabber.org/protocol/disco#info",
		"http://jabber.org/protocol/ibb",
        "http://www.xmpp.org/extensions/xep-0199.html#ns",
        "http://jabber.org/protocol/muc",
        "http://jabber.org/protocol/si",
        "http://jabber.org/protocol/si/profile/file-transfer",
        "jabber:iq:time", //DEPRECATED
        "jabber:iq:version",
        "jabber:x:data",
        "jabber:x:event",
        "urn:xmpp:time",
//#ifdef MOOD
//#         "http://jabber.org/protocol/mood"
//#endif
    };
}
