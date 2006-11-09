/*
 * StatusList.java
 *
 * Created on 3 Декабрь 2005 г., 17:33
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import com.alsutton.jabber.datablocks.Presence;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import locale.SR;

/**
 *
 * @author EvgS
 */
public class StatusList {
    
    // Singleton implementation
    private static StatusList instance;
    public static StatusList getInstance() {
	if (instance==null) instance=new StatusList();
	return instance;
    }

    public Vector statusList;

    /** Creates a new instance of StatusList */
    private StatusList() {
        statusList=new Vector(7);
	try {
	    DataInputStream inputStream=NvStorage.ReadFileRecord("status", 0);
	    
	    createFromStream(Presence.PRESENCE_ONLINE, SR.MS_ONLINE, inputStream);
	    createFromStream(Presence.PRESENCE_CHAT, SR.MS_CHAT, inputStream);
	    createFromStream(Presence.PRESENCE_AWAY, SR.MS_AWAY, inputStream);
	    createFromStream(Presence.PRESENCE_XA, SR.MS_XA, inputStream);
	    createFromStream(Presence.PRESENCE_DND, SR.MS_DND, inputStream);
	    createFromStream(Presence.PRESENCE_INVISIBLE, SR.MS_INVISIBLE, inputStream);
	    createFromStream(Presence.PRESENCE_OFFLINE, SR.MS_OFFLINE, inputStream);
	    
	    inputStream.close();
        } catch (Exception e) { e.printStackTrace(); }

    }
    
    private void createFromStream(int presenceIndex, String presenceName, DataInputStream dataInputStream) {
	ExtendedStatus status=new ExtendedStatus(presenceIndex, presenceName);
        try {
	    status.setPriority(dataInputStream.readInt());
            status.setMessage(dataInputStream.readUTF());
        } catch (Exception e) { /*on stream errors*/ }
	statusList.addElement(status);
    }
    
    public void saveStatusToStorage(){
        DataOutputStream outputStream=NvStorage.CreateDataOutputStream();

        try {
            for (int i=0;i<statusList.size();i++) {
                ExtendedStatus e=(ExtendedStatus)statusList.elementAt(i);
                outputStream.writeInt(e.getPriority());
                outputStream.writeUTF(e.getMessage());
            }
        } catch (Exception e) { e.printStackTrace(); }

        NvStorage.writeFileRecord(outputStream, "status", 0, true);
    }
    
    public ExtendedStatus getStatus(final int status) {
	ExtendedStatus es=null;
	for (Enumeration e=statusList.elements(); e.hasMoreElements(); ){
	    es=(ExtendedStatus)e.nextElement();
	    if (status==es.getImageIndex()) break;
	}
	
	return es;
    }

}
