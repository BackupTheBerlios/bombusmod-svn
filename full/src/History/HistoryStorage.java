/*
 * HistoryStorage.java
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

/**
 *
 * @author User
 */
public class HistoryStorage {
    
    private String bareJid;
    
    public Vector recentList;
    
    private int count=3;
    
    /**
     * Creates a new instance of HistoryStorage
     */
    public HistoryStorage(String bareJid, String Message) {
        loadRecentList();
        System.out.println("init");        
        if (recentList.isEmpty()) return;
    }

    public void saveMessage(String message) {
        System.out.println("save");
        int i=0;
        if (message.length()==0) return;
        while (i<recentList.size()) {
            if ( message.equals((String)recentList.elementAt(i)) || i>(count-1) ) recentList.removeElementAt(i);
            else i++;
        }
        recentList.insertElementAt(message, 0);
        System.out.println("saveMessage "+recentList.size());
        saveRecentList();
        return;
    }
    
    

    private void saveRecentList() {
        DataOutputStream os=NvStorage.CreateDataOutputStream();
        try {
            for (Enumeration e=recentList.elements(); e.hasMoreElements(); ) {
                String s=(String)e.nextElement();
                os.writeUTF(s);
            }
        } catch (Exception e) { e.printStackTrace(); }
        
        NvStorage.writeFileRecord(os, bareJid, 0, true);
        
        System.out.println("saveRecentList() "+recentList.size());
    }
    
    private void loadRecentList() {
        recentList=new Vector(count);
        try {

            DataInputStream is=NvStorage.ReadFileRecord(bareJid, 0);
            
            while (is.available()>0)
                System.out.println(is.readUTF());
                recentList.addElement(is.readUTF());
            is.close();
        } catch (Exception e) { System.out.println(e); }
        
        System.out.println("loadRecentList() "+recentList.size());
    }
}
