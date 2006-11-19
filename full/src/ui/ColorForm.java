/*
 *ColorForm.java
 *
 * Created on 2 РњР°Р№ 2005 Рі., 18:19
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;

import Client.Config;
import Client.StaticData;
//#if (FILE_IO)
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import java.io.IOException;
import java.io.OutputStream;
//#endif
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.controls.NumberField;
import ui.ColorSelector;
import ui.*;

/**
 *
 * @author Evg_S
 */


public class ColorForm implements CommandListener
//#if (FILE_IO)
        , BrowserListener
//#endif
{
      private Displayable currentChoice = null;
      private Display display;
      private Displayable parentView;
      

      private static Colors cl=Colors.getInstance();

      public final static String[] NAMES = {"balloon border and font",
                                    "balloon back",
                                    "common back",
                                    "even back",
                                    "common font",
                                    "subject",
                                    "highlight",
                                    "disco",
                                    "bar",
                                    "bar font",
                                    "contact default",
                                    "contact chat",
                                    "contact away",
                                    "contact xa",
                                    "contact dnd",
                                    "group",
                                    "block font",
                                    "block back",
                                    "message in",
                                    "message out",
                                    "message presence",
                                    "message auth",
                                    "message history",
                                    "pages remained",
                                    "pages complete",
                                    "pages border",
                                    "pages back",
                                    "heap total",
                                    "heap free",
                                    "cursor back",
                                    "cursor border",
                                    "scroll",
                                    "scroll bar",
                                    "scroll back"};
      
      public final static int[] COLORS = {cl.BALLOON_INK,
                                    cl.BALLOON_BGND,
                                    cl.LIST_BGND,
                                    cl.LIST_BGND_EVEN,
                                    cl.LIST_INK,
                                    cl.MSG_SUBJ,
                                    cl.MSG_HIGHLIGHT,
                                    cl.DISCO_CMD,
                                    cl.BAR_BGND,
                                    cl.BAR_INK,
                                    cl.CONTACT_DEFAULT,
                                    cl.CONTACT_CHAT,
                                    cl.CONTACT_AWAY,
                                    cl.CONTACT_XA,
                                    cl.CONTACT_DND,
                                    cl.GROUP_INK,
                                    cl.BLK_INK,
                                    cl.BLK_BGND,
                                    cl.MESSAGE_IN,
                                    cl.MESSAGE_OUT,
                                    cl.MESSAGE_PRESENCE,
                                    cl.MESSAGE_AUTH,
                                    cl.MESSAGE_HISTORY,
                                    cl.PGS_REMAINED,
                                    cl.PGS_COMPLETE,
                                    cl.PGS_BORDER,
                                    cl.PGS_BGND,
                                    cl.HEAP_TOTAL,
                                    cl.HEAP_FREE,
                                    cl.CURSOR_BGND,
                                    cl.CURSOR_OUTLINE,
                                    cl.SCROLL_BRD,
                                    cl.SCROLL_BAR,
                                    cl.SCROLL_BGND};

    List selectionList;
      
    Command cmdOk=new Command(SR.MS_EDIT,Command.OK,1);
//#if (FILE_IO)
    Command cmdSaveSkin=new Command("Save Skin", Command.ITEM,2); 
    int fileSize;
    private int filePos;
    String filePath;
    private FileIO file;
    private OutputStream os;
//#endif
    Command cmdCancel=new Command(SR.MS_DONE, Command.BACK,99);
    
	public ColorForm(Display display) {
		super();
                this.display=display;
                parentView=display.getCurrent();

                reloadSkin();
                selectionList = new List("Colors", List.IMPLICIT, NAMES, null);
                selectionList.addCommand(cmdOk);
//#if (FILE_IO)
                selectionList.addCommand(cmdSaveSkin);
//#endif
                selectionList.addCommand(cmdCancel);
                display.setCurrent(selectionList);
                selectionList.setCommandListener(this);
	}
      
    public void commandAction(Command c, Displayable d) {
        int pos = selectionList.getSelectedIndex();
        
        if (c==cmdCancel) {
            destroyView();
            return;
        }
        
//#if (FILE_IO)
        if (c==cmdSaveSkin) new Browser(null,display, this, true);
//#endif
        
        if (c==cmdOk) {
            if (pos != NAMES.length - 1) {
              try {
                  if (!NAMES[pos].startsWith("(n/a)")) {
                    new ColorSelector(display, pos);
                  }
              } catch(Exception err) {}
           }
        }
    }

    public final static void reloadSkin() {
        cl.loadFromStorage();
        final int[] COLORS = {            cl.BALLOON_INK,
                                    cl.BALLOON_BGND,
                                    cl.LIST_BGND,
                                    cl.LIST_BGND_EVEN,
                                    cl.LIST_INK,
                                    cl.MSG_SUBJ,
                                    cl.MSG_HIGHLIGHT,
                                    cl.DISCO_CMD,
                                    cl.BAR_BGND,
                                    cl.BAR_INK,
                                    cl.CONTACT_DEFAULT,
                                    cl.CONTACT_CHAT,
                                    cl.CONTACT_AWAY,
                                    cl.CONTACT_XA,
                                    cl.CONTACT_DND,
                                    cl.GROUP_INK,
                                    cl.BLK_INK,
                                    cl.BLK_BGND,
                                    cl.MESSAGE_IN,
                                    cl.MESSAGE_OUT,
                                    cl.MESSAGE_PRESENCE,
                                    cl.MESSAGE_AUTH,
                                    cl.MESSAGE_HISTORY,
                                    cl.PGS_REMAINED,
                                    cl.PGS_COMPLETE,
                                    cl.PGS_BORDER,
                                    cl.PGS_BGND,
                                    cl.HEAP_TOTAL,
                                    cl.HEAP_FREE,
                                    cl.CURSOR_BGND,
                                    cl.SCROLL_BRD,
                                    cl.SCROLL_BAR,
                                    cl.SCROLL_BGND
      };
    }

//#if (FILE_IO)
    public void saveSkin(String path){
        
                byte[] bodyMessage;
                
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
                
                bodyMessage=body.toString().getBytes();
                
                file=FileIO.createConnection(path+"skin.txt");
                try {
                    os=file.openOutputStream();
                    writeFile(bodyMessage);
                    os.close();
                    file.close();
                } catch (IOException ex) {
                    try {
                        file.close();
                    } catch (IOException ex2) {
                        ex2.printStackTrace();
                    }
                    ex.printStackTrace();
                }
    }
    
    void writeFile(byte b[]){
        try {
            os.write(b);
            filePos+=b.length;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
//#endif 

    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }
    
//#if (FILE_IO)
    public void BrowserFilePathNotify(String pathSelected) {
        saveSkin(pathSelected);
    }
//#endif 
}