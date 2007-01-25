/*
 * ExtendedStatusList.java
 *
 * Created on 9 Ноябрь 2006 г., 12:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

/**
 *
 * @author User
 */
public class ExtendedStatusList {
    
    RecordStore rs;
    Vector indexes;

    public ExtendedStatusList() {
	try {
	    rs=RecordStore.openRecordStore("ex_status_list", true);
	    int size=rs.getNumRecords();
	    indexes=new Vector(size);
	    RecordEnumeration re=rs.enumerateRecords(null, null, false);
	    
	    while (re.hasNextElement() ){
		indexes.addElement(new Integer(re.nextRecordId() ));
	    }
	} catch (Exception e) { e.printStackTrace();}
    }
    
    public String msg(int index){
	try {
	    ByteArrayInputStream bais=new ByteArrayInputStream(rs.getRecord(getRecordId(index)));
	    DataInputStream dis=new DataInputStream(bais);
	    String msg=dis.readUTF();
	    dis.close();
	    return msg;
	} catch (Exception e) {}
	return null;
    }
    
    public int size(){
	return indexes.size();
    }
    
    private int getRecordId(int index) {
	return ((Integer)indexes.elementAt(index)).intValue();
    }
    
    public void delete(int index) {
	try {
	    rs.deleteRecord(getRecordId(index));
	    indexes.removeElementAt(index);
	} catch (Exception e) {}
    }
    
    public void close(){
	try {
	    rs.closeRecordStore();
	} catch (Exception e) { e.printStackTrace(); }
	rs=null;
    }
    
    public static void store(String status) {
	try {
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    DataOutputStream dout = new DataOutputStream( bout );
            dout.writeUTF(status);
	    dout.close();
	    byte b[]=bout.toByteArray();
	    
	    RecordStore rs=RecordStore.openRecordStore("ex_status_list", true);
	    rs.addRecord(b, 0, b.length);
	    rs.closeRecordStore();
	} catch (Exception e) { e.printStackTrace(); }
    }
}
