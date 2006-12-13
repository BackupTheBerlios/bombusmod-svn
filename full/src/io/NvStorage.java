/*
 * NvStorage.java
 *
 * Created on 22 Март 2005 г., 22:56
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package io;
import java.io.*;
import util.strconv;

import javax.microedition.rms.*;

/**
 *
 * @author Eugene Stahov
 */
public class NvStorage {
    
    /**
     * Opens RMS record from named store
     * and returns it as DataInputStream
     */
    static public DataInputStream ReadFileRecord(String name, int index){
        DataInputStream istream=null;
        
        RecordStore recordStore=null;
        try {
            
            recordStore = RecordStore.openRecordStore(name, false);
            byte[] b=recordStore.getRecord(index+1);
            
            if (b.length!=0)         
            istream=new DataInputStream( new ByteArrayInputStream(b) );
            
        } catch (Exception e) { }
        finally { 
            try { recordStore.closeRecordStore(); } catch (Exception e) {} }
        
        return istream;
    }


    private static ByteArrayOutputStream baos;
    /** Creates DataOutputStream based on ByteOutputStream  */
    static public DataOutputStream CreateDataOutputStream(){
        if (baos!=null) return null;
        DataOutputStream ostream=new DataOutputStream( baos=new ByteArrayOutputStream());
        return ostream;
    }
    
    static public boolean writeFileRecord (
            DataOutputStream ostream, 
            String name, int index, 
            boolean rewrite)
    {
        ByteArrayOutputStream lbaos=baos;
        baos=null; // освободим для следующего
        byte[] b=lbaos.toByteArray();
        

        try {
            if (rewrite) RecordStore.deleteRecordStore(name);
        } catch (Exception e) {}

        RecordStore recordStore;
        try {
            recordStore = RecordStore.openRecordStore(name, true);
        } catch (Exception e) { return false;}
        
        try {
            try {
                recordStore.setRecord(index+1, b, 0, b.length);
            } catch (InvalidRecordIDException e) { recordStore.addRecord(b, 0, b.length); }
            recordStore.closeRecordStore();
            ostream.close();
        } catch (Exception e) { e.printStackTrace(); return false; }

        return true;
    }
}
