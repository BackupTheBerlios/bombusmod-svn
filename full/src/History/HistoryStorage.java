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
    
    private int count=4;
    
    /**
     * Creates a new instance of HistoryStorage
     */
    public HistoryStorage(String bareJid, String message, boolean clear) {
        
        if (bareJid==null) return;
        
        this.bareJid=bareJid.replace('@', '%');
        recentList=new Vector(count);
        if (clear==false) {
            loadRecentList();
        } else {
            recentList.removeAllElements();
            saveRecentList();
            return;
        }
        
        if (message==null) return;
        saveMessage(message);
        //if (recentList.isEmpty()) return;
    }

    public boolean saveMessage(String message) {
        int i=0;
        int tempCount=count-1;
        //if (message.length()==0) return false;
        while (i<recentList.size()) {
            if (i>tempCount) recentList.removeElementAt(i);
             else i++;
        }
        recentList.insertElementAt(message, 0);
        saveRecentList();
        return true;
    }
    
    private boolean saveRecentList() {
        boolean result = false;
        DataOutputStream os=NvStorage.CreateDataOutputStream();
        try {
            for (Enumeration e=recentList.elements(); e.hasMoreElements(); ) {
                String s=(String)e.nextElement();
                os.writeUTF(s);
            }
            result = true;
        } catch (Exception e) { result = false; }
        
        NvStorage.writeFileRecord(os, bareJid, 0, true);
        
        return result;
    }
    
    private boolean loadRecentList() {
        boolean result = false;
        try {
            DataInputStream is=NvStorage.ReadFileRecord(bareJid, 0);
            while (is.available()>0)
                recentList.addElement(is.readUTF());
            is.close();
            result = true;
        } catch (Exception e) { result = false; }
        
        return result;
    }
}
