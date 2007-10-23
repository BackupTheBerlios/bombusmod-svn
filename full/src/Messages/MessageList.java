/*
 * MessageList.java
 *
 * Created on 11.12.2005, 3:02
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

package Messages;

import Client.Config;
import Client.Msg;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.ColorScheme;
import ui.VirtualElement;
import ui.VirtualList;
import ui.FontCache;

public abstract class MessageList 
    extends VirtualList
{
    
    protected Vector messages;
    
    /** Creates a new instance of MessageList */
  
    public MessageList() {
        super();
	messages=new Vector();
        smiles=Config.getInstance().smiles;
    
        enableListWrapping(false);
	
        cursor=0;//activate
        stringHeight=FontCache.getMsgFont().getHeight();
    }

    public MessageList(Display display) {
        this();
        attachDisplay(display);
    }
    
    
    public abstract int getItemCount(); // из protected �?делали public

    protected VirtualElement getItemRef(int index) {
	if (messages.size()<getItemCount()) messages.setSize(getItemCount());
	MessageItem mi=(MessageItem) messages.elementAt(index);
	if (mi==null) {
	    mi=new MessageItem(getMessage(index), this, smiles);
            mi.setEven( (index & 1) == 0);
            mi.getColor();
	    messages.setElementAt(mi, index);
	}
        return mi;
    }
    
    public abstract Msg getMessage(int index);
    
    public void markRead(int msgIndex) {}
    
    protected boolean smiles;

//#ifdef SMILES
//#     protected void keyPressed(int keyCode) { // overriding this method to avoid autorepeat
//#         super.keyPressed(keyCode);
//#         if (keyCode=='*') 
//#             try {
//#                 ((MessageItem)getFocusedObject()).toggleSmiles();
//#             } catch (Exception e){}
//#     }
//#endif
    public void keyGreen() { eventOk(); }
   
}
