/*
 * TransferDispatcher.java
 *
 * Created on 28 Октябрь 2006 г., 19:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package io.file.transfer;

import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Vector;

/**
 *
 * @author Evg_S
 */
public class TransferDispatcher implements JabberBlockListener{

    /** Singleton */
    private static TransferDispatcher instance;
    
    public static TransferDispatcher getInstance() {
        if (instance==null) instance=new TransferDispatcher();
        return instance;
    }
   
    
    private Vector taskList;
    public Vector getTaskList() { return taskList;  }
    
    /** Creates a new instance of TransferDispatcher */
    private TransferDispatcher() {
        taskList=new Vector();
    }

    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Iq) {
            String id=data.getAttribute("id");
            
            JabberDataBlock si=data.getChildBlock("si");
            if (si==null) return BLOCK_REJECTED;
            
            JabberDataBlock file=si.getChildBlock("file");
            JabberDataBlock feature=si.getChildBlock("feature");
            
            String type=data.getTypeAttribute();
            if (type.equals("set")) {
                // sender initiates file sending process
                TransferTask task=new TransferTask(
                        data.getAttribute("from"),
                        id,
                        file.getAttribute("name"),
                        file.getChildBlockText("desc"),
                        Integer.parseInt(file.getAttribute("size")),
                        null);
                taskList.addElement(task);
                
                return BLOCK_PROCESSED;
            }
            if (type.equals("result")) {
                // our file were accepted
            }
        }
        return BLOCK_REJECTED;
    }

}
