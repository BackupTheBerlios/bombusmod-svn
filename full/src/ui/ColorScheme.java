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
//#if FILE_IO
import io.file.FileIO;
//#endif
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import util.StringLoader;

public class ColorScheme {

    private static Hashtable skin;
    
    private static String skinFile;
    
    private static ColorScheme instance;

    private static int resourceType=1;

    public static int getColor;
    
    public static ColorScheme getInstance(){
	if (instance==null) {
	    instance=new ColorScheme();
	    instance.loadFromStorage();
	}
	return instance;
    }
/*    
    private static int BALLOON_INK          =0;
    private static int BALLOON_BGND         =1;
    
    private static int LIST_BGND            =2;
    private static int LIST_BGND_EVEN       =3;
    private static int LIST_INK             =4;
    
    private static int MSG_SUBJ             =5;
    private static int msg_highlight        =6;
    
    private static int disco_cmd            =7;
    
    private static int bar_bgnd             =8;
    private static int bar_ink              =9;
    
    private static int contact_default      =10;
    private static int contact_chat         =11;
    private static int contact_away         =12;
    private static int contact_xa           =13;
    private static int contact_dnd          =14;
    
    private static int group_ink            =15;
    
    private static int blk_ink              =16;
    private static int blk_bgnd             =17;

    private static int message_in           =18;
    private static int message_out          =19;
    private static int message_presence     =20;
    private static int message_auth         =21;
    private static int message_history      =22;

    private static int pgs_remained         =23;
    private static int pgs_complete         =24;
    private static int pgs_border           =25;
    private static int pgs_bgnd             =26;
    
    private static int heap_total           =27;
    private static int heap_free            =28;

    private static int cursor_bgnd          =29;
    private static int cursor_outline       =30;

    private static int scroll_brd           =31;
    private static int scroll_bar           =32;
    private static int scroll_bgnd          =33;
*/    

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
    
/*
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
*/    
    public static int[] COLORS = {            
        BALLOON_INK,
        BALLOON_BGND,
        
        LIST_BGND,
        LIST_BGND_EVEN,
        LIST_INK,

        MSG_SUBJ,
        MSG_HIGHLIGHT,
        
        DISCO_CMD,
        BAR_BGND,
        BAR_INK,

        CONTACT_DEFAULT,
        CONTACT_CHAT,
        CONTACT_AWAY,
        CONTACT_XA,
        CONTACT_DND,

        GROUP_INK,
        BLK_INK,
        BLK_BGND,
        MESSAGE_IN,
        MESSAGE_OUT,

        MESSAGE_PRESENCE,
        MESSAGE_AUTH,
        MESSAGE_HISTORY,
        PGS_REMAINED,
        PGS_COMPLETE,

        PGS_BORDER,
        PGS_BGND,
        HEAP_TOTAL,
        HEAP_FREE,
        CURSOR_BGND,

        CURSOR_OUTLINE,
        SCROLL_BRD,
        SCROLL_BAR,
        SCROLL_BGND
  };
    
  public static int getColor(int color) {
      return COLORS[color];
  }
    

    
    protected void loadFromStorage(){
	try {
	    DataInputStream inputStream=NvStorage.ReadFileRecord("ColorDB", 0);
            
            for (int i=0; i<COLORS.length; i++) {
                COLORS[i]=inputStream.readInt();
            }

	    inputStream.close();
	} catch (Exception e) { }
    }

    protected void saveToStorage(){
	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
	try {
            
            for (int i=0; i<COLORS.length; i++) {
                outputStream.writeInt(COLORS[i]);
            }
        } catch (IOException e) { }
	NvStorage.writeFileRecord(outputStream, "ColorDB", 0, true);
    }
    
    
    public void loadSkin(String skinFile, int resourceType){
        this.skinFile=skinFile;
        this.resourceType=resourceType;
        try {
            BALLOON_INK=loadInt("BALLOON_INK", BALLOON_INK);
            BALLOON_BGND=loadInt("BALLOON_BGND", BALLOON_BGND);
            LIST_BGND=loadInt("LIST_BGND", LIST_BGND);
            LIST_BGND_EVEN=loadInt("LIST_BGND_EVEN", LIST_BGND_EVEN);
            LIST_INK=loadInt("LIST_INK", LIST_INK);
            MSG_SUBJ=loadInt("MSG_SUBJ", MSG_SUBJ);
            MSG_HIGHLIGHT=loadInt("MSG_HIGHLIGHT", MSG_HIGHLIGHT);
            DISCO_CMD=loadInt("DISCO_CMD", DISCO_CMD);
            BAR_BGND=loadInt("BAR_BGND", BAR_BGND);
            BAR_INK=loadInt("BAR_INK", BAR_INK);
            CONTACT_DEFAULT=loadInt("CONTACT_DEFAULT", CONTACT_DEFAULT);
            CONTACT_CHAT=loadInt("CONTACT_CHAT", CONTACT_CHAT);
            CONTACT_AWAY=loadInt("CONTACT_AWAY", CONTACT_AWAY);
            CONTACT_XA=loadInt("CONTACT_XA", CONTACT_XA);
            CONTACT_DND=loadInt("CONTACT_DND", CONTACT_DND);
            GROUP_INK=loadInt("GROUP_INK", GROUP_INK);
            BLK_INK=loadInt("BLK_INK", BLK_INK);
            BLK_BGND=loadInt("BLK_BGND", BLK_BGND);
            MESSAGE_IN=loadInt("MESSAGE_IN", MESSAGE_IN);
            MESSAGE_OUT=loadInt("MESSAGE_OUT", MESSAGE_OUT);
            MESSAGE_PRESENCE=loadInt("MESSAGE_PRESENCE", MESSAGE_PRESENCE);
            MESSAGE_AUTH=loadInt("MESSAGE_AUTH", MESSAGE_AUTH);
            MESSAGE_HISTORY=loadInt("MESSAGE_HISTORY", MESSAGE_HISTORY);
            PGS_REMAINED=loadInt("PGS_REMAINED", PGS_REMAINED);
            PGS_COMPLETE=loadInt("PGS_COMPLETE", PGS_COMPLETE);
            PGS_BORDER=loadInt("PGS_BORDER", PGS_BORDER);
            PGS_BGND=loadInt("PGS_BGND", PGS_BGND);
            HEAP_TOTAL=loadInt("HEAP_TOTAL", HEAP_TOTAL);
            HEAP_FREE=loadInt("HEAP_FREE", HEAP_FREE);
            CURSOR_BGND=loadInt("CURSOR_BGND", CURSOR_BGND);
            CURSOR_OUTLINE=loadInt("CURSOR_OUTLINE", CURSOR_OUTLINE);
            SCROLL_BRD=loadInt("SCROLL_BRD", SCROLL_BRD);
            SCROLL_BAR=loadInt("SCROLL_BAR", SCROLL_BAR);
            SCROLL_BGND=loadInt("SCROLL_BGND", SCROLL_BGND);
            saveToStorage();
        } catch (Exception e) { }
        skin=null;
        skinFile=null;
    }
    
    private static int loadInt(String key, int defaultColor) {
        if (skin==null) {
            //System.out.println(skinFile);

            switch (resourceType) {
//#if FILE_IO
                case 0:
                    byte[] b = null;
                    int len=0;
                    try {
                        FileIO f=FileIO.createConnection(skinFile);
                        InputStream is=f.openInputStream();
                        len=(int)f.fileSize();
                        b=new byte[len];

                        is.read(b);
                        is.close();
                        f.close();
                    } catch (Exception e) {}
                        if (b!=null) {
                            String str=new String(b, 0, len).toString().trim();
                            skin=new StringLoader().hashtableLoaderFromString(str);
                        } else
                        return defaultColor;
                    break;
//#endif
                case 1:
                    skin=new StringLoader().hashtableLoader(skinFile);
                    break;
                    
                case 2:
                    skin=new StringLoader().hashtableLoaderFromString(skinFile);
            }
        }
        try {
            String value=(String)skin.get(key);
            return getColorInt(value);
        } catch (Exception e) {
            StaticData.getInstance().roster.errorLog(e.toString());
            return defaultColor;
        }
    }
    
    public static String getSkin(){
                StringBuffer body=new StringBuffer();
                body.append("xmlSkin\t"+StaticData.getInstance().account.getNickName());
                body.append("\r\n");
                body.append("BALLOON_INK\t"+getColorString(BALLOON_INK)+"\r\n");
                body.append("BALLOON_BGND\t"+getColorString(BALLOON_BGND)+"\r\n");
                body.append("LIST_BGND\t"+getColorString(LIST_BGND)+"\r\n");
                body.append("LIST_BGND_EVEN\t"+getColorString(LIST_BGND_EVEN)+"\r\n");
                body.append("LIST_INK\t"+getColorString(LIST_INK)+"\r\n");
                body.append("MSG_SUBJ\t"+getColorString(MSG_SUBJ)+"\r\n");
                body.append("MSG_HIGHLIGHT\t"+getColorString(MSG_HIGHLIGHT)+"\r\n");
                body.append("DISCO_CMD\t"+getColorString(DISCO_CMD)+"\r\n");
                body.append("BAR_BGND\t"+getColorString(BAR_BGND)+"\r\n");
                body.append("BAR_INK\t"+getColorString(BAR_INK)+"\r\n");
                body.append("CONTACT_DEFAULT\t"+getColorString(CONTACT_DEFAULT)+"\r\n");
                body.append("CONTACT_CHAT\t"+getColorString(CONTACT_CHAT)+"\r\n");
                body.append("CONTACT_AWAY\t"+getColorString(CONTACT_AWAY)+"\r\n");
                body.append("CONTACT_XA\t"+getColorString(CONTACT_XA)+"\r\n");
                body.append("CONTACT_DND\t"+getColorString(CONTACT_DND)+"\r\n");
                body.append("GROUP_INK\t"+getColorString(GROUP_INK)+"\r\n");
                body.append("BLK_INK\t"+getColorString(BLK_INK)+"\r\n");
                body.append("BLK_BGND\t"+getColorString(BLK_BGND)+"\r\n");
                body.append("MESSAGE_IN\t"+getColorString(MESSAGE_IN)+"\r\n");
                body.append("MESSAGE_OUT\t"+getColorString(MESSAGE_OUT)+"\r\n");
                body.append("MESSAGE_PRESENCE\t"+getColorString(MESSAGE_PRESENCE)+"\r\n");
                body.append("MESSAGE_AUTH\t"+getColorString(MESSAGE_AUTH)+"\r\n");
                body.append("MESSAGE_HISTORY\t"+getColorString(MESSAGE_HISTORY)+"\r\n");
                body.append("PGS_REMAINED\t"+getColorString(PGS_REMAINED)+"\r\n");
                body.append("PGS_COMPLETE\t"+getColorString(PGS_COMPLETE)+"\r\n");
                body.append("PGS_BORDER\t"+getColorString(PGS_BORDER)+"\r\n");
                body.append("PGS_BGND\t"+getColorString(PGS_BGND)+"\r\n");
                body.append("HEAP_TOTAL\t"+getColorString(HEAP_TOTAL)+"\r\n");
                body.append("HEAP_FREE\t"+getColorString(HEAP_FREE)+"\r\n");
                body.append("CURSOR_BGND\t"+getColorString(CURSOR_BGND)+"\r\n");
                body.append("CURSOR_OUTLINE\t"+getColorString(CURSOR_OUTLINE)+"\r\n");
                body.append("SCROLL_BRD\t"+getColorString(SCROLL_BRD)+"\r\n");
                body.append("SCROLL_BAR\t"+getColorString(SCROLL_BAR)+"\r\n");
                body.append("SCROLL_BGND\t"+getColorString(SCROLL_BGND)+"\r\n");

                return body.toString();
    }

    public static String ColorToString(int cRed, int cGreen, int cBlue) {
        StringBuffer color=new StringBuffer(8);
        
        color.append("0x");
        
        color.append(expandHex(cRed));

        color.append(expandHex(cGreen));
        
        color.append(expandHex(cBlue));
        
        return color.toString();
    }
    
    public static String expandHex(int eVal) {
        String rVal=Integer.toHexString(eVal);
        if (rVal.length()==1) rVal="0"+rVal;
      
        return rVal;
    }
    
    public static int getColorInt(int color, int pos) {
        String ncolor = getColorString(color);

        switch (pos) {
            case 0:
                return Integer.parseInt(ncolor.substring(2,4),16);
            case 1:
                return Integer.parseInt(ncolor.substring(4,6),16);
            case 2:
                return Integer.parseInt(ncolor.substring(6,8),16);
        }
        return -1;
    }
    
    public static String getColorString(int color) {
        StringBuffer ncolor=new StringBuffer();
        
        ncolor.append("0x");
        
        String col=Integer.toHexString(color);
        
        for (int i=0; i<6-col.length(); i++)
            ncolor.append("0");
        
        ncolor.append(col);

        return ncolor.toString();
    }
    
    public static int getColorInt(String color) { // 0x010000 -> 1
        return Integer.parseInt(color.substring(2),16);
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
