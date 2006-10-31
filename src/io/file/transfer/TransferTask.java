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
    String jid;
    String id;
    String fileName;
    String description;
    int fileSize;
    private int filePos;
    String filePath;
    private FileIO file;
    
    private Vector methods;
    
    /** Creates TransferTask for incoming file */
    public TransferTask(String jid, String id, String name, String description, int size, Vector methods) {
        super(RosterIcons.getInstance());
        state=IN_ASK;
        this.jid=jid;
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
    }
    
    public String toString() { return fileName; }

    public String getTipString() { return String.valueOf(fileSize); }

    void decline() {
        JabberDataBlock reject=new Iq(jid, Iq.TYPE_ERROR, id);
        JabberDataBlock error=reject.addChild("error",null);
        error.setTypeAttribute("cancel");
        error.setAttribute("code","405");
        error.addChild("not-allowed",null).setNameSpace("urn:ietf:params:xml:ns:xmpp-stanzas");
    }

    void accept() {
        int state=HANDSHAKE;
    }

    boolean isAcceptWaiting() { return state==IN_ASK; }

}
