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

import Client.Msg;
import Client.StaticData;
import Info.Phone;
import javax.microedition.lcdui.*;
import locale.SR;

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

    private Msg msg;
    
    MessageArchive archive=new MessageArchive();
    
    public archiveEdit(Display display, Msg msg) {
        this.msg=msg;
        this.display=display;
        parentView=display.getCurrent();
        
        int maxSize=500;
	t=new TextBox(SR.MS_EDIT, null, maxSize, TextField.ANY);
		
        try {
            maxSize=t.setMaxSize(4096);
            
            body=msg.getBody();

            if (body!=null) {
                if (body.length()>maxSize)
                    body=body.substring(0, maxSize-1);
                t.setString(body);
            }
         } catch (Exception e) {}
        
        
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
}
