/*
 * HistoryStorageSave.java
 *
 * Created on 13 Декабрь 2006 г., 14:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package History;

import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import locale.SR;


public class HistoryList
        implements CommandListener
{
    
    private Command cmdBack;
    private Command cmdSelect;
    private Command cmdClear;
    
    private List list;
    private String bareJid;
    
    private Vector recentList;
    
    private Display display;
    private Displayable parentView;

    private int count=3;
    
    
    
    public HistoryList(String bareJid, Display display) {
        
        this.display=display;
        this.bareJid=bareJid;
        
        System.out.println("list");
        
        loadRecentList();
        
        if (recentList.isEmpty()) return;
        
        System.out.println("list!=null");
        
        parentView=display.getCurrent();
        
        cmdBack=new Command(SR.MS_BACK, Command.BACK, 99);
        cmdSelect=new Command(SR.MS_SELECT, Command.OK, 1);
	cmdClear=new Command(SR.MS_CLEAR, Command.SCREEN,2);
        
        list=new List(bareJid, List.IMPLICIT);
        list.addCommand(cmdBack);
	list.addCommand(cmdClear);
        list.setSelectCommand(cmdSelect);
        
        for (Enumeration e=recentList.elements(); e.hasMoreElements();)
            list.append((String)e.nextElement(), null);
        
        list.setCommandListener(this);
        display.setCurrent(list);

    }

    public void commandAction(Command command, Displayable displayable) {
        display.setCurrent(parentView);
        if (command==cmdClear) recentList.removeAllElements();
    }

    private void loadRecentList() {
        recentList=new Vector(count);
        System.out.println(bareJid);
        try {
            DataInputStream is=NvStorage.ReadFileRecord(bareJid, 0);
            
            while (is.available()>0)
                recentList.addElement(is.readUTF());
            is.close();
        } catch (Exception e) { }
        System.out.println(recentList.size());
    }
}