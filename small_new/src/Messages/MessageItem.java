/*
 * MessageItem.java
 *
 * Created on 21.01.2006, 23:17
 *
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
 */

package Messages;

import Client.Msg;
import images.RosterIcons;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import ui.Colors;
import ui.ComplexString;
import ui.FontCache;
import ui.Time;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author Evg_S
 */
public class MessageItem implements 
	VirtualElement,
	MessageParser.MessageParserNotify
{
    
    Msg msg;
    Vector msgLines;
    private VirtualList view;
    private boolean even;
    private boolean smiles;
    private boolean partialParse=false;
    
    /** Creates a new instance of MessageItem */
    public MessageItem(Msg msg, VirtualList view, boolean showSmiles) {
	this.msg=msg;
	this.view=view;
        this.smiles=showSmiles;
    }

    public int getVHeight() { 
        if (msg.itemHeight<0) msg.itemHeight=FontCache.getMsgFont().getHeight();
        return msg.itemHeight; 
    }
    
    public int getVWidth() { return 0; }
    
    public int getColorBGnd() {
        return (even)?
            Colors.LIST_BGND_EVEN:
            Colors.LIST_BGND;
    }
    
    public int getColor() { return msg.getColor(); }
    
    public void drawItem(Graphics g, int ofs, boolean selected) {
        /*if (selected)*/
        g.translate(1,0);
        if (msgLines==null) {
            MessageParser.getInstance().parseMsg(this, view.getListWidth());
            return;
        }
        int y=0;
        for (Enumeration e=msgLines.elements(); e.hasMoreElements(); ) {
            ComplexString line=(ComplexString) e.nextElement();
            if (line.isEmpty()) break;
            int h=line.getVHeight();
            if (y>=0 && y<g.getClipHeight()) {
                if (msg.itemCollapsed) if (msgLines.size()>1) {
                    RosterIcons.getInstance().drawImage(g, RosterIcons.ICON_MSGCOLLAPSED_INDEX, 0,0);
                    g.translate(8,0); //FIXME: хардкод
                }
                line.drawItem(g, 0, selected);
            }
            g.translate(0, h);
            if (msg.itemCollapsed) break;
        }
    }
    
    public void onSelect() {
        msg.itemCollapsed=!msg.itemCollapsed;
        updateHeight();
        if (partialParse) {
            partialParse=false;
            MessageParser.getInstance().parseMsg(this, view.getListWidth());
        }
    }
    
    byte repaintCounter;
    public void notifyRepaint(Vector v, Msg parsedMsg, boolean finalized) {
        msgLines=v;
        updateHeight();
        partialParse=!finalized;
        if (!finalized && !msg.itemCollapsed) if ((--repaintCounter)>=0) return;
        repaintCounter=5;
        view.redraw();
    }
    
    private void updateHeight() {
        int height=0;
        for (Enumeration e=msgLines.elements(); e.hasMoreElements(); ) {
            ComplexString line=(ComplexString) e.nextElement();
            height+=line.getVHeight();
            if (msg.itemCollapsed) break;
        }
        msg.itemHeight=height;
    }
    
    /*public void notifyUrl(String url) { 
        //if (urlList==null) urlList=new Vector();
        //urlList.addElement(url);
    }*/
    
    public Vector getUrlList() { 
        Vector urlList=new Vector();
        addUrls(msg.getBody(), urlList);
        return (urlList.size()==0)? null: urlList;
    }

    private void addUrls(String text, Vector urlList) {
        int pos=0;
        int len=text.length();
        while (pos<len) {
            int head=text.indexOf("http://", pos);
            if (head>=0) {
                pos=head;
                
                while (pos<len) {
                    char c=text.charAt(pos);
                    if (c==' ' || c==0x09 || c==0x0d || c==0x0a || c==0xa0 || c==')' )  
                        break;
                    pos++;
                }
                urlList.addElement(text.substring(head, pos));
                
            } else break;
        }
    }
    
    public void setEven(boolean even) {
        this.even = even;
    }

    public String getTipString() {
        if (Time.localTime() - msg.dateGmt> (24*60*60*1000)) return msg.getDayTime();
        return msg.getTime();
    }

    void toggleSmiles() {
        smiles=!smiles;
        MessageParser.getInstance().parseMsg(this, view.getListWidth());    
    }
    boolean smilesEnabled() {return smiles; }
}
