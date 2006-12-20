/*
 * Config.java
 *
 * Created on 21. prosinec 2003, 16:14
 */

import java.lang.*;
import java.io.*;
import javax.microedition.rms.*;

/**
 *
 * @author  radek
 */
public class Config 
{
    private static final String ID = "mojab";
    
    public String JID;
    public boolean savePassword;
    public String password;
    public boolean manualHost; 
    public String host;
    public int port;
    public String resource;
    public boolean caching;
    
    public Config() 
    {
        JID = "";
        savePassword = true;
        password = "";
        manualHost = false;
        host = "";
        port = 5222;
        resource = "mobile";
        caching = false;
        load();
    }
    
    public String getUser()
    {
        int r = JID.indexOf('@');
        if (r == -1)
            return JID;
        else
            return JID.substring(0, r);
    }
    
    public String getHost()
    {
        int r = JID.indexOf('@');
        if (r == -1)
            return "";
        else
            return JID.substring(r+1);
    }
    
    public String getRealHost()
    {
        if (manualHost && host.length() > 0)
            return host;
        else
            return getHost();
    }
    
    //=====================================================================
    
    public void save()
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buffer);
        RecordStore store = null;
        try {
            store = RecordStore.openRecordStore(ID, true);
            dos.writeUTF(JID);
            dos.writeBoolean(savePassword);
            dos.writeUTF(password);
            dos.writeBoolean(manualHost);
            dos.writeUTF(host);
            dos.writeInt(port);
            dos.writeUTF(resource);
            dos.writeBoolean(caching);
            byte[] bytes = buffer.toByteArray();
            if (store.getNumRecords() > 0)
                store.setRecord(1, bytes, 0, bytes.length);
            else
                store.addRecord(bytes, 0, bytes.length);
        } catch (Exception e) {
        } finally {
            try {
                store.closeRecordStore();
                dos.close();
            } catch (Exception e2) {
            }
        }
    }
    
    public void load()
    {
        RecordStore store = null;
        try {
            store = RecordStore.openRecordStore(ID, true);
            byte[] bytes = store.getRecord(1);
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
            JID = dis.readUTF();
            savePassword = dis.readBoolean();
            password = dis.readUTF();
            manualHost = dis.readBoolean();
            host = dis.readUTF();
            port = dis.readInt();
            resource = dis.readUTF();
            caching = dis.readBoolean();
            dis.close();
        } catch (Exception e) {
        } finally {
            try {
                store.closeRecordStore();
            } catch (Exception e2) {
            }
        }
    }

}
