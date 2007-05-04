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
import locale.SR;
import ui.*;
import Client.*;
import java.util.*;
import javax.microedition.lcdui.*;
import ui.MainBar;

/**
 *
 * @author EvgS
 */
public class AppendTemplate         
        extends MessageList 
        implements CommandListener{

    
    Command cmdSelect=new Command(SR.MS_APPEND, Command.OK, 1);
    Command cmdNew=new Command(SR.MS_NEW, Command.SCREEN, 1);
    Command cmdDelete=new Command(SR.MS_DELETE , Command.SCREEN, 2);

    TemplateContainer template=new TemplateContainer();

    MessageEdit target;
    
    private int caretPos;
    
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
	if (c==cmdDelete) {
	    template.delete(cursor);
	    messages=new Vector();
	    redraw();
	}
	if (c==cmdSelect) { pasteData(); }
        if (c==cmdNew) {
            try {
                new NewTemplate(display);
                messages=new Vector();
                redraw();
            } catch (Exception e) {/*no messages*/}
        }
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
    
    public void focusedItem(int index) {
	if (target==null) return;
    }
    
    public void destroyView(){
	super.destroyView();
	template.close();
    }
}