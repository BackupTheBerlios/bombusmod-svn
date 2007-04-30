/*
 * ArchiveList.java
 *
 * Created on 11.12.2005, 5:24
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

import Client.MessageEdit;

import Client.Config;
import Client.Msg;
import Client.StaticData;
import ui.MainBar;
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
import util.ClipBoard;
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

    Command cmdDelete=new Command(SR.MS_DELETE, Command.SCREEN, 9);
    Command cmdPaste=new Command(SR.MS_PASTE_BODY, Command.SCREEN, 1);
    Command cmdSubj=new Command(SR.MS_PASTE_SUBJECT, Command.SCREEN, 3);
    Command cmdEdit=new Command(SR.MS_EDIT, Command.SCREEN, 4);
    Command cmdCopy = new Command(SR.MS_COPY, Command.SCREEN, 5);
    Command cmdCopyPlus = new Command("+ "+SR.MS_COPY, Command.SCREEN, 5);
//#if (FILE_IO)
    Command cmdExport=new Command(SR.MS_EXPORT_TO_FILE, Command.SCREEN, 6);
//#endif
    Command cmdJid=new Command(SR.MS_PASTE_JID /*"Paste Jid"*/, Command.SCREEN, 2);
    
    MessageArchive archive=new MessageArchive();
    MessageEdit target;
    
    private int caretPos;
    
    private ClipBoard clipboard;
    
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
        addCommand(cmdCopy);
        if (!clipboard.isEmpty()) {
            addCommand(cmdCopyPlus);
        }
//#if (FILE_IO)	
        addCommand(cmdExport);
//#endif
	addCommand(cmdEdit);
	if (target!=null) {
	    addCommand(cmdPaste);
	    addCommand(cmdJid);
	}
        
        attachDisplay(display);
        
        
        try {
            focusedItem(0);
        } catch (Exception e) {}
	
	MainBar mainbar=new MainBar(SR.MS_ARCHIVE /*"Archive"*/);
	mainbar.addElement(null);
	mainbar.addRAlign();
	mainbar.addElement(null);
	mainbar.addElement(SR.MS_FREE /*"free "*/);
        setMainBarItem(mainbar);
    }

    protected void beginPaint() {
        getMainBarItem().setElementAt(" ("+String.valueOf(getItemCount())+")",1);
	getMainBarItem().setElementAt(String.valueOf(archive.freeSpace()),3);
    }
    
    public int getItemCount() {
	return archive.size();
    }
    
    public Msg getMessage(int index) {
	return archive.msg(index);
    }

    public void commandAction(Command c, Displayable d) {
        super.commandAction(c,d);
        
	Msg m=getMessage(cursor);
	if (m==null) return;
        
	if (c==cmdDelete) { deleteMessage(); redraw(); }
	if (c==cmdPaste) { pasteData(0); }
	if (c==cmdSubj) { pasteData(1); }
	if (c==cmdJid) { pasteData(2); }
//#if FILE_IO
        if (c==cmdExport) { new Browser(null, display, this, true); }
//#endif
        if (c==cmdEdit) {
            try {
                new archiveEdit(display,getMessage(cursor)).setParentView(StaticData.getInstance().roster);
                deleteMessage();
            } catch (Exception e) {/*no messages*/}
        }
        
        if (c == cmdCopy)
        {
            try {
                clipboard.setClipBoard(getMessage(cursor).getBody());
            } catch (Exception e) {/*no messages*/}
        }
        
        if (c==cmdCopyPlus) {
            try {
                StringBuffer clipstr=new StringBuffer();
                clipstr.append(clipboard.getClipBoard());
                clipstr.append("\n\n");
                clipstr.append(getMessage(cursor).getBody());
                
                clipboard.setClipBoard(clipstr.toString());
                clipstr=null;
            } catch (Exception e) {/*no messages*/}
        }
    }
	

    private void deleteMessage() {
        archive.delete(cursor);
        messages=new Vector();
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
            
            for(int i=0; i<items; i++){
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
                        //ex2.printStackTrace();
                    }
                    //ex.printStackTrace();
                }
            body=null;
            arhPath=null;
	destroyView();
    }
    
    
    void writeFile(byte b[]){
        try {
            os.write(b);
            filePos+=b.length;
        } catch (IOException ex) {
            //ex.printStackTrace();
        }
    }
    
    public void BrowserFilePathNotify(String pathSelected) {
        exportData(pathSelected);
    }
//#endif
    private String getDate() {
        long dateGmt=Time.localTime();
        return Time.dayString(dateGmt).trim(); 
    }
  
    public void destroyView(){
	super.destroyView();
	archive.close();
    }
	
    public void userKeyPressed(int keyCode) {
       super.userKeyPressed(keyCode);
        if (keyCode==keyClear) {
            if (getItemCount()>0) new YesNoAlert(display, SR.MS_DELETE, SR.MS_SURE_DELETE, this);
        }
    }
	
    public void ActionConfirmed() {
        deleteMessage();
    }

}
