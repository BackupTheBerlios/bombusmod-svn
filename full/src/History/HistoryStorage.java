/*
 * HistoryStorage.java
 *
 * Created on 13 ������� 2006 �., 14:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package History;


import Client.Contact;
import Client.Msg;
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

    private String hisJid;

    private Contact c;
    
    /**
     * Creates a new instance of HistoryStorage
     */
    public HistoryStorage(Contact c, String message, boolean clear) {
        
        this.c=c;
        this.bareJid=c.getBareJid().replace('@', '%');
        
            recentList=new Vector(count);
            loadRecentList();
            
            if (clear==true) {
                recentList.removeAllElements();
                saveRecentList();
            } else {
                if (message==null) return;
                saveMessage(message);
            }
    }

    public void saveMessage(String message) {
        int i=0;
        int tempCount=count-1;
        while (i<recentList.size()) {
            if (i>tempCount) recentList.removeElementAt(i);
             else i++;
        }
        recentList.insertElementAt(message, 0);
        saveRecentList();
    }
    
    private void saveRecentList() {
        DataOutputStream os=NvStorage.CreateDataOutputStream();
        try {
            for (Enumeration e=recentList.elements(); e.hasMoreElements(); ) {
                String s=(String)e.nextElement();
                os.writeUTF(s);
            }
        } catch (Exception e) {}
        
        NvStorage.writeFileRecord(os, bareJid, 0, true);
    }
    
    private void loadRecentList() {
        try {
            DataInputStream is=NvStorage.ReadFileRecord(bareJid, 0);
            while (is.available()>0)
                    recentList.addElement(is.readUTF());
            is.close();
        } catch (Exception e) {}
    }
}
