/*
 * ColorScheme.java
 *
 * Created on 20.02.2005, 21:20
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

package ui;

import Client.StaticData;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import util.StringLoader;

public class ColorScheme {

    private static Hashtable skin;
    
    private static String skinFile;
    
    private static ColorScheme instance;

    private static boolean fs=false;
    
    public static ColorScheme getInstance(){
	if (instance==null) {
	    instance=new ColorScheme();
	    instance.loadFromStorage();
	}
	return instance;
    }

    public static int BALLOON_INK          =0x000000;
    public static int BALLOON_BGND         =0xffffe0;
    
    public static int LIST_BGND            =0xFFFFFF;
    public static int LIST_BGND_EVEN       =0xffeeff;
    public static int LIST_INK             =0x000000;
    
    public static int MSG_SUBJ             =0xa00000;
    public static int MSG_HIGHLIGHT        =0x904090;
    
    public static int DISCO_CMD            =0x000080;
    
    public static int BAR_BGND             =0x0033ff;
    public static int BAR_INK              =0x33ffff;
    
    public static int CONTACT_DEFAULT      =0x000000;
    public static int CONTACT_CHAT         =0x39358b;
    public static int CONTACT_AWAY         =0x008080;
    public static int CONTACT_XA           =0x535353;
    public static int CONTACT_DND          =0x800000;
    
    public static int GROUP_INK            =0x000080;
    
    public static int BLK_INK              =0xffffff;
    public static int BLK_BGND             =0x000000;

    public static int MESSAGE_IN           =0x0000b0;
    public static int MESSAGE_OUT          =0xb00000;
    public static int MESSAGE_PRESENCE     =0x006000;
    public static int MESSAGE_AUTH         =0x400040;
    public static int MESSAGE_HISTORY      =0x535353;

    public static int PGS_REMAINED         =0xffffff;
    public static int PGS_COMPLETE         =0x0000ff;
    public static int PGS_BORDER           =0x808080;
    public static int PGS_BGND             =0x000000;
    
    public static int HEAP_TOTAL           =0xffffff;
    public static int HEAP_FREE            =0x00007f;

    public static int CURSOR_BGND          =0xC8D7E6;
    public static int CURSOR_OUTLINE       =0x000066;

    public static int SCROLL_BRD           =0x000000;
    public static int SCROLL_BAR           =0xBBBBBB;
    public static int SCROLL_BGND          =0xDDDDDD;
    

    
    protected void loadFromStorage(){
	try {
	    DataInputStream inputStream=NvStorage.ReadFileRecord("ColorDB", 0);
            
            BALLOON_INK=inputStream.readInt();
            BALLOON_BGND=inputStream.readInt();

            LIST_BGND=inputStream.readInt();
            LIST_BGND_EVEN=inputStream.readInt();
            LIST_INK=inputStream.readInt();
            MSG_SUBJ=inputStream.readInt();
            MSG_HIGHLIGHT=inputStream.readInt();

            DISCO_CMD=inputStream.readInt();
            
            BAR_BGND=inputStream.readInt();
            BAR_INK=inputStream.readInt();

            CONTACT_DEFAULT=inputStream.readInt();
            CONTACT_CHAT=inputStream.readInt();
            CONTACT_AWAY=inputStream.readInt();
            CONTACT_XA=inputStream.readInt();
            CONTACT_DND=inputStream.readInt();

            GROUP_INK=inputStream.readInt();

            BLK_INK=inputStream.readInt();
            BLK_BGND=inputStream.readInt();

            MESSAGE_IN=inputStream.readInt();
            MESSAGE_OUT=inputStream.readInt();
            MESSAGE_PRESENCE=inputStream.readInt();
            MESSAGE_AUTH=inputStream.readInt();
            MESSAGE_HISTORY=inputStream.readInt();

            PGS_REMAINED=inputStream.readInt();
            PGS_COMPLETE=inputStream.readInt();
            PGS_BORDER=inputStream.readInt();
            PGS_BGND=inputStream.readInt();

            HEAP_TOTAL=inputStream.readInt();
            HEAP_FREE=inputStream.readInt();

            CURSOR_BGND=inputStream.readInt();

            SCROLL_BRD=inputStream.readInt();
            SCROLL_BAR=inputStream.readInt();
            SCROLL_BGND=inputStream.readInt();
            
            CURSOR_OUTLINE=inputStream.readInt();

	    inputStream.close();
	} catch (Exception e) { }
    }

    protected void saveToStorage(){
	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
	try {
	    outputStream.writeInt(BALLOON_INK);
	    outputStream.writeInt(BALLOON_BGND);
            
	    outputStream.writeInt(LIST_BGND);
	    outputStream.writeInt(LIST_BGND_EVEN);
	    outputStream.writeInt(LIST_INK);
	    outputStream.writeInt(MSG_SUBJ);
	    outputStream.writeInt(MSG_HIGHLIGHT);
            
	    outputStream.writeInt(DISCO_CMD);
            
            outputStream.writeInt(BAR_BGND);
            outputStream.writeInt(BAR_INK);
            
	    outputStream.writeInt(CONTACT_DEFAULT);
	    outputStream.writeInt(CONTACT_CHAT);
	    outputStream.writeInt(CONTACT_AWAY);
	    outputStream.writeInt(CONTACT_XA);
	    outputStream.writeInt(CONTACT_DND);
            
	    outputStream.writeInt(GROUP_INK);
            
	    outputStream.writeInt(BLK_INK);
	    outputStream.writeInt(BLK_BGND);
            
	    outputStream.writeInt(MESSAGE_IN);
	    outputStream.writeInt(MESSAGE_OUT);
	    outputStream.writeInt(MESSAGE_PRESENCE);
	    outputStream.writeInt(MESSAGE_AUTH);
	    outputStream.writeInt(MESSAGE_HISTORY);
            
	    outputStream.writeInt(PGS_REMAINED);
	    outputStream.writeInt(PGS_COMPLETE);
	    outputStream.writeInt(PGS_BORDER);
	    outputStream.writeInt(PGS_BGND);
            
	    outputStream.writeInt(HEAP_TOTAL);
	    outputStream.writeInt(HEAP_FREE);
            
	    outputStream.writeInt(CURSOR_BGND);
            
	    outputStream.writeInt(SCROLL_BRD);
	    outputStream.writeInt(SCROLL_BAR);
	    outputStream.writeInt(SCROLL_BGND);
            
            outputStream.writeInt(CURSOR_OUTLINE);

        } catch (IOException e) { }
	NvStorage.writeFileRecord(outputStream, "ColorDB", 0, true);
    }
    
    
    public void loadSkin(String skinFile, boolean fs){
        this.skinFile=skinFile;
        this.fs=fs;

        BALLOON_INK=loadInt("BALLOON_INK");
        BALLOON_BGND=loadInt("BALLOON_BGND");
        LIST_BGND=loadInt("LIST_BGND");
        LIST_BGND_EVEN=loadInt("LIST_BGND_EVEN");
        LIST_INK=loadInt("LIST_INK");
        MSG_SUBJ=loadInt("MSG_SUBJ");
        MSG_HIGHLIGHT=loadInt("MSG_HIGHLIGHT");
        DISCO_CMD=loadInt("DISCO_CMD");
        BAR_BGND=loadInt("BAR_BGND");
        BAR_INK=loadInt("BAR_INK");
        CONTACT_DEFAULT=loadInt("CONTACT_DEFAULT");
        CONTACT_CHAT=loadInt("CONTACT_CHAT");
        CONTACT_AWAY=loadInt("CONTACT_AWAY");
        CONTACT_XA=loadInt("CONTACT_XA");
        CONTACT_DND=loadInt("CONTACT_DND");
        GROUP_INK=loadInt("GROUP_INK");
        BLK_INK=loadInt("BLK_INK");
        BLK_BGND=loadInt("BLK_BGND");
        MESSAGE_IN=loadInt("MESSAGE_IN");
        MESSAGE_OUT=loadInt("MESSAGE_OUT");
        MESSAGE_PRESENCE=loadInt("MESSAGE_PRESENCE");
        MESSAGE_AUTH=loadInt("MESSAGE_AUTH");
        MESSAGE_HISTORY=loadInt("MESSAGE_HISTORY");
        PGS_REMAINED=loadInt("PGS_REMAINED");
        PGS_COMPLETE=loadInt("PGS_COMPLETE");
        PGS_BORDER=loadInt("PGS_BORDER");
        PGS_BGND=loadInt("PGS_BGND");
        HEAP_TOTAL=loadInt("HEAP_TOTAL");
        HEAP_FREE=loadInt("HEAP_FREE");
        CURSOR_BGND=loadInt("CURSOR_BGND");
        CURSOR_OUTLINE=loadInt("CURSOR_OUTLINE");
        SCROLL_BRD=loadInt("SCROLL_BRD");
        SCROLL_BAR=loadInt("SCROLL_BAR");
        SCROLL_BGND=loadInt("SCROLL_BGND");
        saveToStorage();
        this.skin=null;
    }
    
    private static int loadInt(String key) {
        if (skin==null) {
            System.out.println(skinFile);
            if (fs) {
                skin=new StringLoader().hashtableLoaderFS(skinFile);
            } else {
                skin=new StringLoader().hashtableLoader(skinFile);    
            }
        }
        try {
            String value=(String)skin.get(key);
            return Integer.parseInt(value.substring(2),16);
        } catch (Exception e) {
            System.out.println(e);
            return 0xFF0000;
        }
    }
    
    public static String getSkin(){
                StringBuffer body=new StringBuffer();
                body.append("xmlSkin\t"+StaticData.getInstance().account.getNickName());
                body.append("\r\n");
                body.append("BALLOON_INK\t0x"+Integer.toHexString(BALLOON_INK)+"\r\n");
                body.append("BALLOON_BGND\t0x"+Integer.toHexString(BALLOON_BGND)+"\r\n");
                body.append("LIST_BGND\t0x"+Integer.toHexString(LIST_BGND)+"\r\n");
                body.append("LIST_BGND_EVEN\t0x"+Integer.toHexString(LIST_BGND_EVEN)+"\r\n");
                body.append("LIST_INK\t0x"+Integer.toHexString(LIST_INK)+"\r\n");
                body.append("MSG_SUBJ\t0x"+Integer.toHexString(MSG_SUBJ)+"\r\n");
                body.append("MSG_HIGHLIGHT\t0x"+Integer.toHexString(MSG_HIGHLIGHT)+"\r\n");
                body.append("DISCO_CMD\t0x"+Integer.toHexString(DISCO_CMD)+"\r\n");
                body.append("BAR_BGND\t0x"+Integer.toHexString(BAR_BGND)+"\r\n");
                body.append("BAR_INK\t0x"+Integer.toHexString(BAR_INK)+"\r\n");
                body.append("CONTACT_DEFAULT\t0x"+Integer.toHexString(CONTACT_DEFAULT)+"\r\n");
                body.append("CONTACT_CHAT\t0x"+Integer.toHexString(CONTACT_CHAT)+"\r\n");
                body.append("CONTACT_AWAY\t0x"+Integer.toHexString(CONTACT_AWAY)+"\r\n");
                body.append("CONTACT_XA\t0x"+Integer.toHexString(CONTACT_XA)+"\r\n");
                body.append("CONTACT_DND\t0x"+Integer.toHexString(CONTACT_DND)+"\r\n");
                body.append("GROUP_INK\t0x"+Integer.toHexString(GROUP_INK)+"\r\n");
                body.append("BLK_INK\t0x"+Integer.toHexString(BLK_INK)+"\r\n");
                body.append("BLK_BGND\t0x"+Integer.toHexString(BLK_BGND)+"\r\n");
                body.append("MESSAGE_IN\t0x"+Integer.toHexString(MESSAGE_IN)+"\r\n");
                body.append("MESSAGE_OUT\t0x"+Integer.toHexString(MESSAGE_OUT)+"\r\n");
                body.append("MESSAGE_PRESENCE\t0x"+Integer.toHexString(MESSAGE_PRESENCE)+"\r\n");
                body.append("MESSAGE_AUTH\t0x"+Integer.toHexString(MESSAGE_AUTH)+"\r\n");
                body.append("MESSAGE_HISTORY\t0x"+Integer.toHexString(MESSAGE_HISTORY)+"\r\n");
                body.append("PGS_REMAINED\t0x"+Integer.toHexString(PGS_REMAINED)+"\r\n");
                body.append("PGS_COMPLETE\t0x"+Integer.toHexString(PGS_COMPLETE)+"\r\n");
                body.append("PGS_BORDER\t0x"+Integer.toHexString(PGS_BORDER)+"\r\n");
                body.append("PGS_BGND\t0x"+Integer.toHexString(PGS_BGND)+"\r\n");
                body.append("HEAP_TOTAL\t0x"+Integer.toHexString(HEAP_TOTAL)+"\r\n");
                body.append("HEAP_FREE\t0x"+Integer.toHexString(HEAP_FREE)+"\r\n");
                body.append("CURSOR_BGND\t0x"+Integer.toHexString(CURSOR_BGND)+"\r\n");
                body.append("CURSOR_OUTLINE\t0x"+Integer.toHexString(CURSOR_OUTLINE)+"\r\n");
                body.append("SCROLL_BRD\t0x"+Integer.toHexString(SCROLL_BRD)+"\r\n");
                body.append("SCROLL_BAR\t0x"+Integer.toHexString(SCROLL_BAR)+"\r\n");
                body.append("SCROLL_BGND\t0x"+Integer.toHexString(SCROLL_BGND)+"\r\n");

                return body.toString();
    }
/*    
    public void serialize(DataOutputStream os) throws IOException {
        os.writeInt(BALLOON_INK);
        os.writeInt(BALLOON_BGND);
        os.writeInt(LIST_BGND);
        os.writeInt(LIST_BGND_EVEN);
        os.writeInt(LIST_INK);
        os.writeInt(MSG_SUBJ);
        os.writeInt(MSG_HIGHLIGHT);
        os.writeInt(DISCO_CMD);
        os.writeInt(BAR_BGND);
        os.writeInt(BAR_INK);
        os.writeInt(CONTACT_DEFAULT);
        os.writeInt(CONTACT_CHAT);
        os.writeInt(CONTACT_AWAY);
        os.writeInt(CONTACT_XA);
        os.writeInt(CONTACT_DND);
        os.writeInt(GROUP_INK);
        os.writeInt(BLK_INK);
        os.writeInt(BLK_BGND);
        os.writeInt(MESSAGE_IN);
        os.writeInt(MESSAGE_OUT);
        os.writeInt(MESSAGE_PRESENCE);
        os.writeInt(MESSAGE_AUTH);
        os.writeInt(MESSAGE_HISTORY);
        os.writeInt(PGS_REMAINED);
        os.writeInt(PGS_COMPLETE);
        os.writeInt(PGS_BORDER);
        os.writeInt(PGS_BGND);
        os.writeInt(HEAP_TOTAL);
        os.writeInt(HEAP_FREE);
        os.writeInt(CURSOR_BGND);
        os.writeInt(CURSOR_OUTLINE);
        os.writeInt(SCROLL_BRD);
        os.writeInt(SCROLL_BAR);
        os.writeInt(SCROLL_BGND);
    }
    
    public ColorScheme (DataInputStream is) throws IOException {
        BALLOON_INK=is.readInt();
        BALLOON_BGND=is.readInt();
        LIST_BGND=is.readInt();
        LIST_BGND_EVEN=is.readInt();
        LIST_INK=is.readInt();
        MSG_SUBJ=is.readInt();
        MSG_HIGHLIGHT=is.readInt();
        DISCO_CMD=is.readInt();
        BAR_BGND=is.readInt();
        BAR_INK=is.readInt();
        CONTACT_DEFAULT=is.readInt();
        CONTACT_CHAT=is.readInt();
        CONTACT_AWAY=is.readInt();
        CONTACT_XA=is.readInt();
        CONTACT_DND=is.readInt();
        GROUP_INK=is.readInt();
        BLK_INK=is.readInt();
        BLK_BGND=is.readInt();
        MESSAGE_IN=is.readInt();
        MESSAGE_OUT=is.readInt();
        MESSAGE_PRESENCE=is.readInt();
        MESSAGE_AUTH=is.readInt();
        MESSAGE_HISTORY=is.readInt();
        PGS_REMAINED=is.readInt();
        PGS_COMPLETE=is.readInt();
        PGS_BORDER=is.readInt();
        PGS_BGND=is.readInt();
        HEAP_TOTAL=is.readInt();
        HEAP_FREE=is.readInt();
        CURSOR_BGND=is.readInt();
        CURSOR_OUTLINE=is.readInt();
        SCROLL_BRD=is.readInt();
        SCROLL_BAR=is.readInt();
        SCROLL_BGND=is.readInt();
    }
 */
}
