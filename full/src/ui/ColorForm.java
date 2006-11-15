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
    Command cmdCancel=new Command(SR.MS_DONE, Command.BACK,99);
    
	public ColorForm(Display display) {
		super();
                this.display=display;
                parentView=display.getCurrent();

                reloadSkin();
                selectionList = new List("Colors", List.IMPLICIT, NAMES, null);
                selectionList.addCommand(cmdOk);
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


    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }

}