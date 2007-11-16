/*
 * archiveEdit.java
 *
 * Created on 20.02.2005, 21:20
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
 *
 */

package archive;

import Client.Config;
import Client.Msg;
import Client.StaticData;
import Info.Phone;
import javax.microedition.lcdui.*;
import locale.SR;
import util.ClipBoard;

/**
 *
 * @author Eugene Stahov
 */
public class archiveEdit implements CommandListener
{
    
    private Display display;
    private Displayable parentView;
    private TextBox t;
    private String body;

    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.SCREEN,99);
    private Command cmdOk=new Command(SR.MS_OK, Command.OK /*Command.SCREEN*/,1);
    
    private Command cmdABC=new Command("Abc", Command.SCREEN, 15);
    private Command cmdAbc=new Command("abc", Command.SCREEN, 15);
    private Command cmdClearTitle=new Command("clear title", Command.SCREEN, 16);
    private Command cmdPasteText=new Command(SR.MS_PASTE, Command.SCREEN, 98);  

    private Msg msg;
    
    MessageArchive archive=new MessageArchive();
    
    private ClipBoard clipboard=ClipBoard.getInstance();
    
    public archiveEdit(Display display, Msg msg) {
        this.msg=msg;
        this.display=display;
        parentView=display.getCurrent();
        this.body=msg.getBody();
        
	t=new TextBox(SR.MS_EDIT, "", 500, TextField.ANY);
		
        try {
            //expanding buffer as much as possible
            int maxSize=t.setMaxSize(4096); //must not trow

            if (body!=null) {
                if (body.length()>maxSize)
                    body=body.substring(0, maxSize-1);
                t.setString(body);
            }
         } catch (Exception e) {}

        
        if (!clipboard.isEmpty())
            t.addCommand(cmdPasteText);
        
        t.addCommand(cmdClearTitle);
        
        setInitialCaps(Config.getInstance().capsState);
        
        
        t.addCommand(cmdOk);

        t.addCommand(cmdCancel);
        t.setCommandListener(this);

        display.setCurrent(t);
    }
    
    public void commandAction(Command c, Displayable d){
        body=t.getString();
		
        int caretPos=t.getCaretPosition();
        if (Phone.PhoneManufacturer()==Phone.MOTO)
            caretPos=-1;
        if (caretPos<0) caretPos=body.length();
		
        if (body.length()==0) body=null;
        
        if (c==cmdAbc) {setInitialCaps(false); return; }
        if (c==cmdABC) {setInitialCaps(true); return; }
        
        if (c==cmdClearTitle) { t.setTitle(t.getTitle()==null?SR.MS_EDIT:null); return; }
        if (c==cmdPasteText) { insertText(clipboard.getClipBoard(), getCaretPos()); return; }

        Msg newmsg=null;
        if (c==cmdCancel) { 
            newmsg=new Msg(msg.messageType, msg.from, msg.subject, msg.getBody());
        } else if (c==cmdOk) {
            newmsg=new Msg(msg.messageType, msg.from, msg.subject, body);
        }
        archive.add(newmsg);
        
        body=null;
        archive.close();
        
        new ArchiveList(display, null, -1);

        destroyView();
        return; 
    }
    
    public void destroyView(){
        display.setCurrent(StaticData.getInstance().roster);
    }

    public void setParentView(Displayable parentView){
        this.parentView=parentView;
    }
    

    public int getCaretPos() {     
        int caretPos=t.getCaretPosition();
        // +MOTOROLA STUB
        if (Phone.PhoneManufacturer()==Phone.MOTO)
            caretPos=-1;
        
        if (caretPos<0) caretPos=t.getString().length();
        
        return caretPos;
    }
    
    
    private void setInitialCaps(boolean state) {
        t.setConstraints(state? TextField.INITIAL_CAPS_SENTENCE: TextField.ANY);
        t.removeCommand(state? cmdABC: cmdAbc);
        t.addCommand(state? cmdAbc: cmdABC);
        Config.getInstance().capsState=state;
    }
    
    
    public void insertText(String s, int caretPos) {
        String src=t.getString();

        StringBuffer sb=new StringBuffer(s);
        
        if (caretPos>0) 
            if (src.charAt(caretPos-1)!=' ')   
                sb.insert(0, ' ');
        
        if (caretPos<src.length())
            if (src.charAt(caretPos)!=' ')
                sb.append(' ');
        
        if (caretPos==src.length()) sb.append(' ');
        
        try {
            int freeSz=t.getMaxSize()-t.size();
            if (freeSz<sb.length()) sb.delete(freeSz, sb.length());
        } catch (Exception e) {}
       
        t.insert(sb.toString(), caretPos);
        sb=null;
    }
}
