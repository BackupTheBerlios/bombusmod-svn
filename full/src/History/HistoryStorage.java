/*
 * HistoryStorage.java
 *
 * Created on 13.11.2006, 14:49
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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
