/*
 * ColorScheme.java
 *
 * Created on 28 Февраль 2007 г., 13:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui;

import Client.StaticData;
import java.util.Hashtable;
import util.StringLoader;

/**
 *
 * @author User
 */
public class ColorScheme {

    private static Colors cl;
    private static Hashtable skin;
    
    private static String skinFile;
    
    /** Creates a new instance of ColorScheme */
    public ColorScheme() {
    }
    
    
    public static void loadSkin(String skinFile){
        skinFile=skinFile;
        
            cl=Colors.getInstance();
            cl.BALLOON_INK=loadInt("BALLOON_INK");
            cl.BALLOON_BGND=loadInt("BALLOON_BGND");
            cl.LIST_BGND=loadInt("LIST_BGND");
            cl.LIST_BGND_EVEN=loadInt("LIST_BGND_EVEN");
            cl.LIST_INK=loadInt("LIST_INK");
            cl.MSG_SUBJ=loadInt("MSG_SUBJ");
            cl.MSG_HIGHLIGHT=loadInt("MSG_HIGHLIGHT");
            cl.DISCO_CMD=loadInt("DISCO_CMD");
            cl.BAR_BGND=loadInt("BAR_BGND");
            cl.BAR_INK=loadInt("BAR_INK");
            cl.CONTACT_DEFAULT=loadInt("CONTACT_DEFAULT");
            cl.CONTACT_CHAT=loadInt("CONTACT_CHAT");
            cl.CONTACT_AWAY=loadInt("CONTACT_AWAY");
            cl.CONTACT_XA=loadInt("CONTACT_XA");
            cl.CONTACT_DND=loadInt("CONTACT_DND");
            cl.GROUP_INK=loadInt("GROUP_INK");
            cl.BLK_INK=loadInt("BLK_INK");
            cl.BLK_BGND=loadInt("BLK_BGND");
            cl.MESSAGE_IN=loadInt("MESSAGE_IN");
            cl.MESSAGE_OUT=loadInt("MESSAGE_OUT");
            cl.MESSAGE_PRESENCE=loadInt("MESSAGE_PRESENCE");
            cl.MESSAGE_AUTH=loadInt("MESSAGE_AUTH");
            cl.MESSAGE_HISTORY=loadInt("MESSAGE_HISTORY");
            cl.PGS_REMAINED=loadInt("PGS_REMAINED");
            cl.PGS_COMPLETE=loadInt("PGS_COMPLETE");
            cl.PGS_BORDER=loadInt("PGS_BORDER");
            cl.PGS_BGND=loadInt("PGS_BGND");
            cl.HEAP_TOTAL=loadInt("HEAP_TOTAL");
            cl.HEAP_FREE=loadInt("HEAP_FREE");
            cl.CURSOR_BGND=loadInt("CURSOR_BGND");
            cl.CURSOR_OUTLINE=loadInt("CURSOR_OUTLINE");
            cl.SCROLL_BRD=loadInt("SCROLL_BRD");
            cl.SCROLL_BAR=loadInt("SCROLL_BAR");
            cl.SCROLL_BGND=loadInt("SCROLL_BGND");
            cl.saveToStorage();
            skin=null;
    }
    
    private static int loadInt(String key) {
        if (skin==null) {
            skin=new StringLoader().hashtableLoader(skinFile);
        }
        try {
            String value=(String)skin.get(key);
            return Integer.parseInt(value.substring(2),16);
        } catch (Exception e) {
            System.out.println(e);
            return 0xFF0000;
        }
    }

    
//#if (FILE_IO)
    public static String getSkin(){
                
                cl=Colors.getInstance();
                StringBuffer body=new StringBuffer();
                body.append("xmlSkin\t"+StaticData.getInstance().account.getNickName());
                body.append("\r\n");
                body.append("BALLOON_INK\t0x"+Integer.toHexString(cl.BALLOON_INK)+"\r\n");
                body.append("BALLOON_BGND\t0x"+Integer.toHexString(cl.BALLOON_BGND)+"\r\n");
                body.append("LIST_BGND\t0x"+Integer.toHexString(cl.LIST_BGND)+"\r\n");
                body.append("LIST_BGND_EVEN\t0x"+Integer.toHexString(cl.LIST_BGND_EVEN)+"\r\n");
                body.append("LIST_INK\t0x"+Integer.toHexString(cl.LIST_INK)+"\r\n");
                body.append("MSG_SUBJ\t0x"+Integer.toHexString(cl.MSG_SUBJ)+"\r\n");
                body.append("MSG_HIGHLIGHT\t0x"+Integer.toHexString(cl.MSG_HIGHLIGHT)+"\r\n");
                body.append("DISCO_CMD\t0x"+Integer.toHexString(cl.DISCO_CMD)+"\r\n");
                body.append("BAR_BGND\t0x"+Integer.toHexString(cl.BAR_BGND)+"\r\n");
                body.append("BAR_INK\t0x"+Integer.toHexString(cl.BAR_INK)+"\r\n");
                body.append("CONTACT_DEFAULT\t0x"+Integer.toHexString(cl.CONTACT_DEFAULT)+"\r\n");
                body.append("CONTACT_CHAT\t0x"+Integer.toHexString(cl.CONTACT_CHAT)+"\r\n");
                body.append("CONTACT_AWAY\t0x"+Integer.toHexString(cl.CONTACT_AWAY)+"\r\n");
                body.append("CONTACT_XA\t0x"+Integer.toHexString(cl.CONTACT_XA)+"\r\n");
                body.append("CONTACT_DND\t0x"+Integer.toHexString(cl.CONTACT_DND)+"\r\n");
                body.append("GROUP_INK\t0x"+Integer.toHexString(cl.GROUP_INK)+"\r\n");
                body.append("BLK_INK\t0x"+Integer.toHexString(cl.BLK_INK)+"\r\n");
                body.append("BLK_BGND\t0x"+Integer.toHexString(cl.BLK_BGND)+"\r\n");
                body.append("MESSAGE_IN\t0x"+Integer.toHexString(cl.MESSAGE_IN)+"\r\n");
                body.append("MESSAGE_OUT\t0x"+Integer.toHexString(cl.MESSAGE_OUT)+"\r\n");
                body.append("MESSAGE_PRESENCE\t0x"+Integer.toHexString(cl.MESSAGE_PRESENCE)+"\r\n");
                body.append("MESSAGE_AUTH\t0x"+Integer.toHexString(cl.MESSAGE_AUTH)+"\r\n");
                body.append("MESSAGE_HISTORY\t0x"+Integer.toHexString(cl.MESSAGE_HISTORY)+"\r\n");
                body.append("PGS_REMAINED\t0x"+Integer.toHexString(cl.PGS_REMAINED)+"\r\n");
                body.append("PGS_COMPLETE\t0x"+Integer.toHexString(cl.PGS_COMPLETE)+"\r\n");
                body.append("PGS_BORDER\t0x"+Integer.toHexString(cl.PGS_BORDER)+"\r\n");
                body.append("PGS_BGND\t0x"+Integer.toHexString(cl.PGS_BGND)+"\r\n");
                body.append("HEAP_TOTAL\t0x"+Integer.toHexString(cl.HEAP_TOTAL)+"\r\n");
                body.append("HEAP_FREE\t0x"+Integer.toHexString(cl.HEAP_FREE)+"\r\n");
                body.append("CURSOR_BGND\t0x"+Integer.toHexString(cl.CURSOR_BGND)+"\r\n");
                body.append("CURSOR_OUTLINE\t0x"+Integer.toHexString(cl.CURSOR_OUTLINE)+"\r\n");
                body.append("SCROLL_BRD\t0x"+Integer.toHexString(cl.SCROLL_BRD)+"\r\n");
                body.append("SCROLL_BAR\t0x"+Integer.toHexString(cl.SCROLL_BAR)+"\r\n");
                body.append("SCROLL_BGND\t0x"+Integer.toHexString(cl.SCROLL_BGND)+"\r\n");

                return body.toString();
    }
//#endif 
}
