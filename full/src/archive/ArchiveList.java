/*
 * ArchiveList.java
 *
 * Created on 11 Ltrf,hm 2005 пїЅ., 5:24
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package archive;

import Client.MessageEdit;

import Client.Config;
import Client.Msg;
import Client.Title;
import Messages.MessageList;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import locale.SR;
import ui.ComplexString;
//#if FILE_IO
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif
import ui.Time;
import util.strconv;
import ui.YesNoAlert;

/**
 *
 * @author EvgS
 */
public class ArchiveList 
    extends MessageList
	implements YesNoAlert.YesNoListener
//#if (FILE_IO)
    , BrowserListener
//#endif
{

    Command cmdDelete=new Command(SR.MS_DELETE /*"Delete"*/, Command.SCREEN, 9);
    Command cmdPaste=new Command(SR.MS_PASTE_BODY /*"Paste Body"*/, Command.SCREEN, 1);
    Command cmdSubj=new Command(SR.MS_PASTE_SUBJECT /*"Paste Subject"*/, Command.SCREEN, 3);
//#if (FILE_IO)
    Command cmdExport=new Command(SR.MS_EXPORT_TO_FILE /*"Paste Jid"*/, Command.SCREEN, 5);
//#endif
    Command cmdJid=new Command(SR.MS_PASTE_JID /*"Paste Jid"*/, Command.SCREEN, 2);
    //Command cmdNick=new Command("Paste Nickname", Command.SCREEN, 3);
    
    MessageArchive archive=new MessageArchive();
    MessageEdit target;
    
    private int caretPos;
    
//#if FILE_IO    
    int fileSize;
    private int filePos;
    String filePath;
    private FileIO file;
    private OutputStream os;
//#endif
    /** Creates a new instance of ArchiveList */
    public ArchiveList(Display display, MessageEdit target, int caretPos) {
 	super ();
 	this.target=target;
        this.caretPos=caretPos;
	setCommandListener(this);
	addCommand(cmdBack);
	addCommand(cmdDelete);
//#if (FILE_IO)	
        addCommand(cmdExport);
//#endif
	if (target!=null) {
	    addCommand(cmdPaste);
	    addCommand(cmdJid);
	}
        
        attachDisplay(display);
        
        
        try {
            focusedItem(0);
        } catch (Exception e) {}
	
	Title title=new Title(SR.MS_ARCHIVE /*"Archive"*/);
	title.addElement(null);
	title.addRAlign();
	title.addElement(null);
	title.addElement(SR.MS_FREE /*"free "*/);
        setTitleItem(title);
        
    }

    protected void beginPaint() {
        getTitleItem().setElementAt(" ("+String.valueOf(getItemCount())+")",1);
	getTitleItem().setElementAt(String.valueOf(archive.freeSpace()),3);
    }
    
    public int getItemCount() {
	return archive.size();
    }
    
    public Msg getMessage(int index) {
	return archive.msg(index);
    }

    public void commandAction(Command c, Displayable d) {
        super.commandAction(c,d);
	if (c==cmdDelete) { deleteMessage(); }
	if (c==cmdPaste) { pasteData(0); }
	if (c==cmdSubj) { pasteData(1); }
	if (c==cmdJid) { pasteData(2); }
//#if FILE_IO
        if (c==cmdExport) { new Browser(null, display, this, true); }
//#endif
    }
	

    private void deleteMessage() {
        archive.delete(cursor);
        messages=new Vector();
        redraw();
    }
    
    private void pasteData(int field) {
	if (target==null) return;
	Msg m=getMessage(cursor);
	if (m==null) return;
	String data;
	switch (field) {
	case 1: 
	    data=m.subject;
	    break;
	case 2: 
	    data=m.from;
	    break;
	default:
	    data=m.getBody();
	}
	target.insertText(data, caretPos);
	destroyView();
    }
    
    public void keyGreen() { pasteData(0); }
    
    public void focusedItem(int index) {
	if (target==null) return;
	try {
	    if (getMessage(index).subject!=null) {
		addCommand(cmdSubj);
		return;
	    }
	} catch (Exception e) { }
	removeCommand(cmdSubj);
    }
//#if FILE_IO    
    public void exportData(String arhPath) {
            Config cf=Config.getInstance();
            
            byte[] bodyMessage;
            int items=getItemCount();
            StringBuffer body=new StringBuffer();
            
            for(int i=0; i<items-1; i++){
                Msg m=getMessage(i);
                
                body.append(m.getDayTime());
                body.append(" <");
                body.append(m.from);
                body.append("> ");
                
                if (m.subject!=null) {
                    body.append(m.subject);
                    body.append("\r\n");
                }
                
                body.append(m.getBody());
                body.append("\r\n");
            }
                if (cf.cp1251) {
                    bodyMessage=strconv.convUnicodeToCp1251(body.toString()).getBytes();
                } else {
                    bodyMessage=body.toString().getBytes();
                }
                
                file=FileIO.createConnection(arhPath+"archive_"+getDate()+".txt");
                try {
                    os=file.openOutputStream();
                    writeFile(bodyMessage);
                    os.close();
                    file.close();
                } catch (IOException ex) {
                    try {
                        file.close();
                    } catch (IOException ex2) {
                        ex2.printStackTrace();
                    }
                    ex.printStackTrace();
                }
                
            arhPath=null;
	destroyView();
    }
    
    
    void writeFile(byte b[]){
        try {
            os.write(b);
            filePos+=b.length;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void BrowserFilePathNotify(String pathSelected) {
        exportData(pathSelected);
    }
//#endif
    private String getDate() {
        long dateGmt=Time.localTime();
        return Time.dayString(dateGmt); 
    }
  
    public void destroyView(){
	super.destroyView();
	archive.close();
    }
	
    public void userKeyPressed(int keyCode) {
       super.userKeyPressed(keyCode);
        if (keyCode==keyClear) {
            if (getItemCount()>0) new YesNoAlert(display, this, SR.MS_DELETE, SR.MS_SURE_DELETE);
        }
    }
	
    public void ActionConfirmed() {
        deleteMessage();
    }

}
