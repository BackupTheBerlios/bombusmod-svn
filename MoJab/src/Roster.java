/*
 * Roster.java
 *
 * Created on 14. prosinec 2003, 15:05
 */

import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

/**
 *
 * @author  radek
 */
public class Roster extends List
{
    private java.util.Vector roster;
    
    private Image[] images;
        
    public Roster() 
    {
        super("moJab online", List.IMPLICIT);
        roster = new java.util.Vector();
        images = new Image[5];
        for (int i = 0; i < 4; i++) images[i] = null;
        try {
            images[Contact.ONLINE] = Image.createImage("/images/online.png");
            images[Contact.AWAY] = Image.createImage("/images/away.png");
            images[Contact.NOTAVAIL] = Image.createImage("/images/xa.png");
            images[Contact.DND] = Image.createImage("/images/dnd.png");
            images[Contact.OFFLINE] = Image.createImage("/images/offline.png");
        } catch (IOException e) {
            //System.out.println("Img: "+e);
        }
    }
    
    public void clear()
    {
        while (size() > 0)
            delete(0);
        roster.removeAllElements();
    }
    
    public void sort()
    {
        boolean change = true;
        
        while (change)
        {
            change = false;
            for (int i = 0; i < roster.size()-1; i++)
            {
                Contact con1 = (Contact) roster.elementAt(i);
                Contact con2 = (Contact) roster.elementAt(i+1);
                if (con1.greaterThan(con2))
                {
                    roster.setElementAt(con2, i);
                    roster.setElementAt(con1, i+1);
                    change = true;
                }
            }
        }

        setSelectedIndex(0, true);
        
        for (int i = 0; i < roster.size(); i++)
        {
            Contact con = (Contact) roster.elementAt(i);
            set(i, con.name, images[con.status]); 
        }
        
    }
    
    public void addContact(String name, String jid)
    {
        Contact con = new Contact(name, Contact.stripResource(jid));
        append(con.name, images[Contact.OFFLINE]);
        roster.addElement(con);
    }
    
    public String findNameOf(String jid)
    {
        int index = findIndex(jid);
        if (index != -1)
            return ((Contact) roster.elementAt(index)).name;
        else
            return null;
    }

    public int findIndex(String jid)
    {
        String find = Contact.stripResource(jid).toLowerCase();
        for (int i = 0; i < roster.size(); i++)
        {
            Contact con = (Contact) roster.elementAt(i);
            if (con.jid.toLowerCase().equals(find))
                return i;
        }
        return -1;
    }
    
    public String currentJID()
    {
        Contact con = (Contact) roster.elementAt(getSelectedIndex());
        return con.jid;
    }
    
    public void setStatus(String jid, int status)
    {
        int index = findIndex(jid);
        if (index != -1)
        {
            Contact con = (Contact) roster.elementAt(index);
            con.status = status;            
            set(index, getString(index), images[status]);
        }
    }
    
    public void save()
    {
        RecordStore store = null;
        try {
            store = RecordStore.openRecordStore("roster", true);
            for (int i = 0; i < roster.size(); i++)
            {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(buffer);
                Contact con = (Contact) roster.elementAt(i);
                dos.writeUTF(con.jid);
                if (con.name != null)
                    dos.writeUTF(con.name);
                else
                    dos.writeUTF("@");
                dos.close();
                byte[] bytes = buffer.toByteArray();
                if (store.getNumRecords() >= i+1)
                    store.setRecord(i+1, bytes, 0, bytes.length);
                else
                    store.addRecord(bytes, 0, bytes.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                store.closeRecordStore();
            } catch (Exception e2) {
            }
        }
    }
    
    public boolean load()
    {
        boolean ret = false;
        RecordStore store = null;
        try {
            store = RecordStore.openRecordStore("roster", true);
            for (int i = 0; i < store.getNumRecords(); i++)
            {
                byte[] bytes = store.getRecord(i+1);
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
                String jid = dis.readUTF();
                String name = dis.readUTF();
                if (name.equals("@")) name = null;
                addContact(name, jid);
                dis.close();
            }
            ret = true;
        } catch (Exception e) {
        } finally {
            try {
                store.closeRecordStore();
            } catch (Exception e2) {
            }
        }
        return ret;
    }
    
    public void deleteStore()
    {
        try {
            RecordStore.deleteRecordStore("roster");
        } catch (Exception e) {
        }
    }
    
}
