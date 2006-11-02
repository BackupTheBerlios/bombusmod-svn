/*
 * TransferTask.java
 *
 * Created on 28 Октябрь 2006 г., 17:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package io.file.transfer;

import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import images.RosterIcons;
import io.file.FileIO;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import ui.Colors;
import ui.IconTextElement;

/**
 *
 * @author Evg_S
 */
public class TransferTask 
        extends IconTextElement 
{
    
    public final static int COMPLETE=1;
    public final static int PROGRESS=3;
    public final static int ERROR=4;
    public final static int HANDSHAKE=6;
    public final static int IN_ASK=7;
    public final static int NONE=5;
    
    private int state=NONE;
    private boolean sending;
    boolean showEvent;
    String jid;
    String id;
    String sid;
    String fileName;
    String description;
    int fileSize;
    private int filePos;
    String filePath;
    private FileIO file;
    private OutputStream os;
    
    private Vector methods;
    
    /** Creates TransferTask for incoming file */
    public TransferTask(String jid, String id, String sid, String name, String description, int size, Vector methods) {
        super(RosterIcons.getInstance());
        state=IN_ASK;
        showEvent=true;
        this.jid=jid;
        this.id=id;
        this.sid=sid;
        this.fileName=name;
        this.description=description;
        this.fileSize=size;
        this.methods=methods;
    }

    protected int getImageIndex() { return state; }

    public int getColor() { return (sending)? Colors.MESSAGE_OUT : Colors.MESSAGE_IN; }

    public void drawItem(Graphics g, int ofs, boolean sel) {
        int xpgs=(g.getClipWidth()/3)*2;
        int pgsz=g.getClipWidth()-xpgs-4;
        int filled=(pgsz*filePos)/fileSize; 
        
        int oldColor=g.getColor();
        g.setColor(0xffffff);
        
        g.fillRect(xpgs, 3, pgsz, getVHeight()-6);
        g.setColor(0xaaaaaa);
        g.drawRect(xpgs, 3, pgsz, getVHeight()-6);
        g.fillRect(xpgs, 3, filled, getVHeight()-6);
        g.setColor(oldColor);
        
        super.drawItem(g, ofs, sel);
        showEvent=false;
    }
    
    public String toString() { return fileName; }

    public String getTipString() { return String.valueOf(fileSize); }

    void decline() {
        JabberDataBlock reject=new Iq(jid, Iq.TYPE_ERROR, id);
        JabberDataBlock error=reject.addChild("error",null);
        error.setTypeAttribute("cancel");
        error.setAttribute("code","405");
        error.addChild("not-allowed",null).setNameSpace("urn:ietf:params:xml:ns:xmpp-stanzas");
        TransferDispatcher.getInstance().send(reject);
        
        state=ERROR;
        showEvent=true;
    }

    void accept() {
        
        try {
            file=FileIO.createConnection(filePath+fileName);
            os=file.openOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
            decline();
            return;
        }
        JabberDataBlock accept=new Iq(jid, Iq.TYPE_RESULT, id);
        
        JabberDataBlock si=accept.addChild("si", null);
        si.setNameSpace("http://jabber.org/protocol/si");
        
        JabberDataBlock feature=si.addChild("feature", null);
        feature.setNameSpace("http://jabber.org/protocol/feature-neg");
        
        JabberDataBlock x=feature.addChild("x", null);
        x.setNameSpace("jabber:x:data");
        x.setTypeAttribute("submit");
        
        JabberDataBlock field=x.addChild("field", null);
        field.setAttribute("var","stream-method");
        field.addChild("value", "http://jabber.org/protocol/ibb");
        
        TransferDispatcher.getInstance().send(accept);
        state=HANDSHAKE;
    }
    
    void writeFile(byte b[]){
        try {
            os.write(b);
            filePos+=b.length;
            state=PROGRESS;
        } catch (IOException ex) {
            ex.printStackTrace();
            state=ERROR;
            showEvent=true;
            //todo: terminate transfer
        }
    }

    boolean isAcceptWaiting() { return state==IN_ASK; }

    void closeFile() {
        try {
            if (os!=null)
                os.close();
            file.close();
            state=COMPLETE;
        } catch (IOException ex) {
            ex.printStackTrace();
            state=ERROR;
        }
        showEvent=true;
    }

}
