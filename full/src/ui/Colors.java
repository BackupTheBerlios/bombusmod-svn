/*
 * Colors.java
 *
 * Created on 4 Февраль 2006 г., 22:26
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;

import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import midlet.Bombus;

/**
 *
 * @author Evg_S
 */
public class Colors {
    public static int BALLOON_INK          =0x000000;
    public static int BALLOON_BGND         =0xffffe0;
    
    public static int LIST_BGND           =0xFFFFFF;
    public static int LIST_BGND_EVEN      =0xffeeff;
    public static int LIST_INK            =0x000000;
    
    public static int MSG_SUBJ            =0xa00000;
    public static int MSG_HIGHLIGHT       =0x904090;
    
    public static int DISCO_CMD           =0x000080;
    
    public static int BAR_BGND         =0x0033ff;
    public static int BAR_INK          =0x33ffff;
    
    public static int CONTACT_DEFAULT     =0x000000;
    public static int CONTACT_CHAT        =0x39358b;
    public static int CONTACT_AWAY        =0x008080;
    public static int CONTACT_XA          =0x535353;
    public static int CONTACT_DND         =0x800000;
    
    public static int GROUP_INK           =0x000080;
    
    public static int BLK_INK             =0xffffff;
    public static int BLK_BGND            =0x000000;

    public static int MESSAGE_IN        =0x0000b0;
    public static int MESSAGE_OUT       =0xb00000;
    public static int MESSAGE_PRESENCE  =0x006000;
    public static int MESSAGE_AUTH      =0x400040;
    public static int MESSAGE_HISTORY   =0x535353;

    public static int PGS_REMAINED        =0xffffff;
    public static int PGS_COMPLETE        =0x0000ff;
    public static int PGS_BORDER          =0x808080;
    public static int PGS_BGND            =0x000000;
    
    public static int HEAP_TOTAL          =0xffffff;
    public static int HEAP_FREE           =0x00007f;

    public static int CURSOR_BGND    =0xC8D7E6;
    public static int CURSOR_OUTLINE =0x000066;

    public static int SCROLL_BRD     =0x000000;
    public static int SCROLL_BAR     =0xBBBBBB;
    public static int SCROLL_BGND    =0xDDDDDD;

    private static Colors instance;
    
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
	} catch (Exception e) {
	    //e.printStackTrace();
	}
    }

    public void saveToStorage(){
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

        } catch (IOException e) { 
            //e.printStackTrace();
        }
	NvStorage.writeFileRecord(outputStream, "ColorDB", 0, true);
    }
    
    public static Colors getInstance(){
	if (instance==null) {
	    instance=new Colors();
	    instance.loadFromStorage();
	}
	return instance;
    }
}
