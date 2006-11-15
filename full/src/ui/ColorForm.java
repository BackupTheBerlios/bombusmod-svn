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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.controls.NumberField;
import ui.ColorSelector;
import util.StringLoader;
import ui.*;

/**
 *
 * @author Evg_S
 */


public class ColorForm implements CommandListener
{
      private Displayable currentChoice = null;
      private Display display;
      private Displayable parentView;
      

      private static Colors cl=Colors.getInstance();

      public static String[] NAMES = {"balloon border and font",
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
                                    "cursor bgnd",
                                    "scroll",
                                    "scroll bar",
                                    "scroll back"};
      public static int[] COLORS = {cl.BALLOON_INK,
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
                                    cl.SCROLL_BGND};

    List selectionList;
      
    Command cmdOk=new Command(SR.MS_EDIT,Command.OK,1);
    Command cmdLoad=new Command("Load From skin.txt",Command.SCREEN,2);    
    Command cmdCancel=new Command(SR.MS_DONE, Command.BACK,99);

    
	public ColorForm(Display display) {
		super();
                this.display=display;
                parentView=display.getCurrent();
                
                cl.loadFromStorage();
                reloadSkin();
                selectionList = new List("Colors", List.IMPLICIT, NAMES, null);
                selectionList.addCommand(cmdOk);
                selectionList.addCommand(cmdCancel);
                selectionList.addCommand(cmdLoad);
                display.setCurrent(selectionList);
                selectionList.setCommandListener(this);
	}
        
    private static Hashtable skin;
      
      public void commandAction(Command c, Displayable d) {
        int pos = selectionList.getSelectedIndex();
        if (c==cmdLoad) {
            loadSkin("/skins/skin.txt");
            destroyView();
        }
        if (c==cmdCancel) {
            destroyView();
        } else {
            if (pos == NAMES.length - 1) {

            } else {
              try {
                  if (!NAMES[pos].startsWith("(n/a)")) {
                    new ColorSelector(display, pos);
                  }
              }
              catch(Exception err) {
              }
            }
        }
      }
        
    public void loadSkin(String skinFile){
            if (skin==null) {
                skin=new StringLoader().hashtableLoader(skinFile);
            }
            
            cl.BALLOON_INK=loadInt(skinFile, "BALLOON_INK");
            cl.BALLOON_BGND=loadInt(skinFile, "BALLOON_BGND");
            cl.LIST_BGND=loadInt(skinFile, "LIST_BGND");
            cl.LIST_BGND_EVEN=loadInt(skinFile, "LIST_BGND_EVEN");
            cl.LIST_INK=loadInt(skinFile, "LIST_INK");
            cl.MSG_SUBJ=loadInt(skinFile, "MSG_SUBJ");
            cl.MSG_HIGHLIGHT=loadInt(skinFile, "MSG_HIGHLIGHT");
            cl.DISCO_CMD=loadInt(skinFile, "DISCO_CMD");
            cl.BAR_BGND=loadInt(skinFile, "BAR_BGND");
            cl.BAR_INK=loadInt(skinFile, "BAR_INK");
            cl.CONTACT_DEFAULT=loadInt(skinFile, "CONTACT_DEFAULT");
            cl.CONTACT_CHAT=loadInt(skinFile, "CONTACT_CHAT");
            cl.CONTACT_AWAY=loadInt(skinFile, "CONTACT_AWAY");
            cl.CONTACT_XA=loadInt(skinFile, "CONTACT_XA");
            cl.CONTACT_DND=loadInt(skinFile, "CONTACT_DND");
            cl.GROUP_INK=loadInt(skinFile, "GROUP_INK");
            cl.BLK_INK=loadInt(skinFile, "BLK_INK");
            cl.BLK_BGND=loadInt(skinFile, "BLK_BGND");
            cl.MESSAGE_IN=loadInt(skinFile, "MESSAGE_IN");
            cl.MESSAGE_OUT=loadInt(skinFile, "MESSAGE_OUT");
            cl.MESSAGE_PRESENCE=loadInt(skinFile, "MESSAGE_PRESENCE");
            cl.MESSAGE_AUTH=loadInt(skinFile, "MESSAGE_AUTH");
            cl.MESSAGE_HISTORY=loadInt(skinFile, "MESSAGE_HISTORY");
            cl.PGS_REMAINED=loadInt(skinFile, "PGS_REMAINED");
            cl.PGS_COMPLETE=loadInt(skinFile, "PGS_COMPLETE");
            cl.PGS_BORDER=loadInt(skinFile, "PGS_BORDER");
            cl.PGS_BGND=loadInt(skinFile, "PGS_BGND");
            cl.HEAP_TOTAL=loadInt(skinFile, "HEAP_TOTAL");
            cl.HEAP_FREE=loadInt(skinFile, "HEAP_FREE");
            cl.CURSOR_BGND=loadInt(skinFile, "CURSOR_BGND");
            cl.SCROLL_BRD=loadInt(skinFile, "SCROLL_BRD");
            cl.SCROLL_BAR=loadInt(skinFile, "SCROLL_BAR");
            cl.SCROLL_BGND=loadInt(skinFile, "SCROLL_BGND");
            cl.saveToStorage();
    }    
    
    public static void reloadSkin() {
      int[] COLORS = {cl.BALLOON_INK,
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
                                    cl.SCROLL_BGND};
    }
   
    private static int loadInt(String skinFile,String key) {
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

    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
        ((Canvas)parentView).setFullScreenMode(Config.getInstance().fullscreen);
    }
}