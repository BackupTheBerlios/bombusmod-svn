/*
 * AppendTemplate.java
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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
 */

package templates;

import Client.MessageEdit;

import Messages.MessageList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import locale.SR;
import ui.*;
import Client.*;
import java.util.*;
import javax.microedition.lcdui.*;
import ui.MainBar;
//#if FILE_IO
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import util.strconv;
//#endif

public class AppendTemplate         
        extends MessageList 
        implements CommandListener
//#if (FILE_IO)
        , BrowserListener
//#endif
{

    
    Command cmdSelect=new Command(SR.MS_APPEND, Command.OK, 1);
    Command cmdNew=new Command(SR.MS_NEW, Command.SCREEN, 1);
    Command cmdDelete=new Command(SR.MS_DELETE , Command.SCREEN, 2);
    Command cmdDeleteAll=new Command(SR.MS_DELETE_ALL, Command.SCREEN, 3);
//#if (FILE_IO)
    Command cmdExport=new Command(SR.MS_EXPORT_TO_FILE, Command.SCREEN, 4);
    Command cmdImport=new Command(SR.MS_IMPORT_TO_FILE, Command.SCREEN, 5);
//#endif

    TemplateContainer template=new TemplateContainer();

    MessageEdit target;
    
    private int caretPos;
    
    
//#if FILE_IO    
    int fileSize;
    private int filePos;
    String filePath;
    private FileIO file;
    private OutputStream os;
    
    private int EXPORT=0;
    private int IMPORT=1;

    private int returnVal=0;
    
            
    private String start_item="<START_ITEM>";
    private String end_item="<END_ITEM>";

    private String start_date="<START_DATE>";
    private String end_date="<END_DATE>";

    private String start_from="<START_FROM>";
    private String end_from="<END_FROM>";

    private String start_subj="<START_SUBJ>";
    private String end_subj="<END_SUBJ>";

    private String start_body="<START_BODY>";
    private String end_body="<END_BODY>";
    
//#endif
    
    /** Creates a new instance of AccountPicker */
    public AppendTemplate(Display display, MessageEdit target, int caretPos) {
	super (display);
	this.target=target;
        this.caretPos=caretPos;
	if (target!=null) {
	    addCommand(cmdSelect);
	}
	addCommand(cmdNew);
	addCommand(cmdDelete);
        addCommand(cmdDeleteAll);
//#if (FILE_IO)	
        addCommand(cmdExport);
        addCommand(cmdImport);
//#endif
	addCommand(cmdBack);

        try {
            focusedItem(0);
        } catch (Exception e) {}
	
	setCommandListener(this);
	
	MainBar mainbar=new MainBar(SR.MS_SELECT);
	mainbar.addRAlign();
	mainbar.addElement(null);
	mainbar.addElement(SR.MS_FREE);
        setMainBarItem(mainbar);
    }
     protected void beginPaint() {
	getMainBarItem().setElementAt(String.valueOf(template.freeSpace()),2);
    }
    
    public int getItemCount() {
	return template.size();
    }
    
    public Msg getMessage(int index) {
	return template.msg(index);
    }

    public void commandAction(Command c, Displayable d) {
	if (c==cmdBack) {
	    destroyView();
	    //return;
	}
	Msg m=getMessage(cursor);
//#if FILE_IO
        if (c==cmdImport) { 
            returnVal=IMPORT;
            new Browser(null, display, this, false);
        }
//#endif
        if (c==cmdNew) {
            try {
                new NewTemplate(display);
                messages=new Vector();
                redraw();
            } catch (Exception e) {/*no messages*/}
        }
	if (m==null) return;
        
	if (c==cmdDelete) {
	    template.delete(cursor);
	    messages=new Vector();
	    redraw();
	}
        if (c==cmdDeleteAll) { deleteAllMessages(); redraw(); }
//#if FILE_IO
        if (c==cmdExport) { 
            returnVal=EXPORT;
            new Browser(null, display, this, true);
        }
//#endif
	if (c==cmdSelect) { pasteData(); }
    }
    
    private void pasteData() {
	if (target==null) return;
	Msg m=getMessage(cursor);
	if (m==null) return;
	String data;
	data=m.getBody();
	target.insertText(data, caretPos);
	destroyView();
    }
    
    public void keyGreen() { pasteData(); }
    
    public void keyPressed(int keyCode) {
        if (keyCode==FIRE) {
            pasteData();
        } else super.keyPressed(keyCode);
    }
    
    private void deleteAllMessages() {
        template.deleteAll();
        messages=new Vector();
    }
    
    public void focusedItem(int index) {
	if (target==null) return;
    }
    
    public void destroyView(){
	super.destroyView();
	template.close();
    }
    
    private String getDate() {
        long dateGmt=Time.localTime();
        return Time.dayString(dateGmt).trim(); 
    }
    
//#if FILE_IO 
    public void importData(String arhPath) {
            Config cf=Config.getInstance();
            
            byte[] bodyMessage;
            String archive="";
            bodyMessage=readFile(arhPath);
            
            
            if (bodyMessage!=null) {
                if (cf.cp1251) {
                    archive=strconv.convCp1251ToUnicode(new String(bodyMessage, 0, bodyMessage.length));
                } else {
                    archive=new String(bodyMessage, 0, bodyMessage.length);
                }
            }
            if (archive!=null) {
                try {
                    int pos=0;
                    int start_pos=0;
                    int end_pos=0;
                    
                    String date="";
                    String from="";
                    String subj="";
                    String body="";
                    
                    while (true) {
                        start_pos=archive.indexOf(start_item,pos);
                        end_pos=archive.indexOf(end_item,pos)+end_item.length();
                        pos=start_pos;
                        
                        if (start_pos>-1) {
                            date=archive.substring(archive.indexOf(start_date,pos)+start_date.length(), archive.indexOf(end_date,pos));
                            from=archive.substring(archive.indexOf(start_from,pos)+start_from.length(), archive.indexOf(end_from,pos));
                            subj=archive.substring(archive.indexOf(start_subj,pos)+start_subj.length(), archive.indexOf(end_subj,pos));
                            body=archive.substring(archive.indexOf(start_body,pos)+start_body.length(), archive.indexOf(end_body,pos));
                            
                            //System.out.println("["+date+"]"+from+":\r\n"+subj+" "+body);
                            
                            TemplateContainer.store(new Msg(Msg.MESSAGE_TYPE_IN,from,subj,body));
                        } else {
                            date=null;
                            from=null;
                            subj=null;
                            body=null;
                            break;
                        }

                        pos=end_pos;
                    }
                } catch (Exception e)	{  }
            }

            bodyMessage=null;
            arhPath=null;
	destroyView();
    }
    
    
    
    public void exportData(String arhPath) {
            Config cf=Config.getInstance();
            
            byte[] bodyMessage;
            int items=getItemCount();
            StringBuffer body=new StringBuffer();

            for(int i=0; i<items; i++){
                Msg m=getMessage(i);
                body.append(start_item+"\r\n");
                body.append(start_date);
                body.append(m.getDayTime());
                body.append(end_date+"\r\n");
                body.append(start_from);
                body.append(m.from);
                body.append(end_from+"\r\n");
                body.append(start_subj);
                if (m.subject!=null) {
                    body.append(m.subject);
                }
                body.append(end_subj+"\r\n");
                body.append(start_body);
                body.append(m.getBody());
                body.append(end_body+"\r\n");
                body.append(end_item+"\r\n\r\n");
            }
            
            if (cf.cp1251) {
                bodyMessage=strconv.convUnicodeToCp1251(body.toString()).getBytes();
            } else {
                bodyMessage=body.toString().getBytes();
            }

            file=FileIO.createConnection(arhPath+"template_"+getDate()+".txt");
            try {
                os=file.openOutputStream();
                writeFile(bodyMessage);
                os.close();
                file.close();
            } catch (IOException ex) {
                try {
                    file.close();
                } catch (IOException ex2) { }
            }
            body=null;
            arhPath=null;
	destroyView();
    }
    
    public byte[] readFile(String arhPath){
        byte[] b = null;
        int len=0;
        FileIO f=FileIO.createConnection(arhPath);
        try {
            InputStream is=f.openInputStream();
            len=(int)f.fileSize();
            b=new byte[len];

            is.read(b);
            is.close();
            f.close();
        } catch (Exception e) {
            try {
                f.close();
            } catch (IOException ex2) { }
        }
        
        if (b!=null) {
            return b;
        }
        return null;
    }
    

    
    void writeFile(byte b[]){
        try {
            os.write(b);
            filePos+=b.length;
        } catch (IOException ex) { }
    }
    
    public void BrowserFilePathNotify(String pathSelected) {
        switch (returnVal) {
            case 0:
                exportData(pathSelected);
                break;
            case 1:
                importData(pathSelected);
                break;
        }
        
    }
//#endif
}
