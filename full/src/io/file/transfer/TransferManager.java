/*
 * TransferManager.java
 *
 * Created on 28 Октябрь 2006 г., 17:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package io.file.transfer;

import Client.Title;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author Evg_S
 */
public class TransferManager extends VirtualList implements CommandListener{
    
    private Vector transfers;
    
    Command cmdBack=new Command(SR.MS_BACK, Command.BACK, 99);
    /** Creates a new instance of TransferManager */
    public TransferManager(Display display) {
        super(display);
        
        addCommand(cmdBack);
        setCommandListener(this);
        setTitleItem(new Title(2, null, "Transfer tasks"));
        
        transfers=TransferDispatcher.getInstance().getTaskList();
    }

    protected int getItemCount() { return transfers.size(); }

    protected VirtualElement getItemRef(int index) { return (VirtualElement) transfers.elementAt(index); }

    public void eventOk() {
        TransferTask t=(TransferTask) getFocusedObject();
        if (t.isAcceptWaiting()) new TransferAcceptFile(display, t);
    }

    public void commandAction(Command command, Displayable displayable) {
        TransferDispatcher.getInstance().eventNotify();
        destroyView();
    }
}
