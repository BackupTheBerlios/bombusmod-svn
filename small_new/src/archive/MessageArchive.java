/*
 * MessageArchive.java
 *
 * Created on 11.12.2005, 2:33
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
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

package archive;

import Client.Msg;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 *
 * @author EvgS
 */
public class MessageArchive {
    
    RecordStore rs;
    Vector indexes;
    /** Creates a new instance of MessageArchive */
    public MessageArchive() {
	try {
	    rs=RecordStore.openRecordStore("archive", true);
	    int size=rs.getNumRecords();
	    indexes=new Vector(size);
	    RecordEnumeration re=rs.enumerateRecords(null, null, false);
	    
	    while (re.hasNextElement() ){
		indexes.addElement(new Integer(re.nextRecordId() ));
	    }
	    
	} catch (Exception e) { e.printStackTrace();}
    }

    /*public void importMessageArchive() {
	try {
	    rs=RecordStore.openRecordStore("archive", true);
	    int size=rs.getNumRecords();
	    indexes=new Vector(size);
	    RecordEnumeration re=rs.enumerateRecords(null, null, false);
	    
	    while (re.hasNextElement() ){
		indexes.addElement(new Integer(re.nextRecordId() ));
	    }
	    
	} catch (Exception e) { e.printStackTrace();}
    }*/

    public int size(){
	return indexes.size();
    }
    
    private int getRecordId(int index) {
	return ((Integer)indexes.elementAt(index)).intValue();
    }
    public Msg msg(int index){
	try {
	    ByteArrayInputStream bais=new ByteArrayInputStream(
		rs.getRecord(getRecordId(index))
	    );
	    DataInputStream dis=new DataInputStream(bais);
	    Msg msg=new Msg(dis);
            msg.itemCollapsed=true; 
	    dis.close();
	    return msg;
	} catch (Exception e) {}
	return null;
    }
    
    public void delete(int index) {
	try {
	    rs.deleteRecord(getRecordId(index));
	    indexes.removeElementAt(index);
	} catch (Exception e) {}
    }

    public int freeSpace(){
	try {
	    return rs.getSizeAvailable()/1024;
	} catch (Exception e) { }
	return 0;
    }
    
    public void close(){
	try {
	    rs.closeRecordStore();
	} catch (Exception e) { e.printStackTrace(); }
	rs=null;
    }
    public static void store(Msg msg) {
	try {
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    DataOutputStream dout = new DataOutputStream( bout );
	    msg.serialize( dout );
	    dout.close();
	    byte b[]=bout.toByteArray();
	    
	    RecordStore rs=RecordStore.openRecordStore("archive", true);
	    rs.addRecord(b, 0, b.length);
	    rs.closeRecordStore();
	    //new MessageArchive()
	} catch (Exception e) { e.printStackTrace(); }
    }
}
