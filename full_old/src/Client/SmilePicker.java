/*
 * SmilePicker.java
 *
 * Created on 6.03.2005, 11:50
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

package Client;
import Messages.MessageParser;
import images.SmilesIcons;
import locale.SR;
import ui.*;
import javax.microedition.lcdui.*;
import java.util.Vector;
import ui.controls.Balloon;

/**
 *
 * @author Eugene Stahov
 */
    
/**
 *
 * @author Eugene Stahov
 */
public class SmilePicker extends VirtualList implements CommandListener, VirtualElement{

    private final static int CURSOR_HOFFSET=1;
    private final static int CURSOR_VOFFSET=1;
   
    private int imgCnt;
    private int xCnt;
    private int xLastCnt;
    private int xCursor;
    private int lines;

    private int lineHeight;
    private int imgWidth;
    
    private ImageList il;
    
    private MessageEdit me;
    private int caretPos;
     
     Command cmdBack=new Command(SR.MS_CANCEL,Command.BACK,99);
     Command cmdOK=new Command(SR.MS_SELECT,Command.OK,1);
     
     private Vector smileTable;
 
     /** Creates a new instance of SmilePicker */
    public SmilePicker(Display display, MessageEdit me, int caretPos) {
         super(display);
         this.me=me;
         this.caretPos=caretPos;
         
         il = SmilesIcons.getInstance();
        
        smileTable=MessageParser.getInstance().getSmileTable();
        
        imgCnt=smileTable.size();
        //il.getCount();
        
        imgWidth=il.getWidth()+2*CURSOR_HOFFSET;
        lineHeight = il.getHeight()+2*CURSOR_VOFFSET;

        xCnt= getWidth() / imgWidth;
        
        lines=imgCnt/xCnt;
        xLastCnt=imgCnt-lines*xCnt;
        if (xLastCnt>0) lines++; else xLastCnt=xCnt;
        
        addCommand(cmdOK);
        addCommand(cmdBack);
        setCommandListener(this);
    }
    
    int lineIndex;
    //SmileItem si=new SmileItem();
    
    public int getItemCount(){ return lines; }
    public VirtualElement getItemRef(int index){ lineIndex=index; return this;}
    
    //private class SmileItem implements VirtualElement {
    public int getVWidth(){ return 0; }
    public int getVHeight() { return lineHeight; }
    public int getColor(){ return ColorScheme.LIST_INK; }
    public int getColorBGnd(){ return ColorScheme.LIST_BGND; }
    public void onSelect(){
        try {
            me.insertText( getTipString() , caretPos);
        } catch (Exception e) { /*e.printStackTrace();*/  }
        destroyView();
    };
    
        
    public void drawItem(Graphics g, int ofs, boolean selected){
        int max=(lineIndex==lines-1)? xLastCnt:xCnt;
        for (int i=0;i<max;i++) {
            il.drawImage(g, lineIndex*xCnt + i, i*imgWidth+CURSOR_HOFFSET, CURSOR_VOFFSET);
        }
    };
    
    //}
    public void drawCursor (Graphics g, int width, int height){
        int x=xCursor*imgWidth;
        g.setColor(ColorScheme.LIST_BGND);
        g.fillRect(0,0,width, height);
        g.translate(x,0);
        super.drawCursor(g, imgWidth, lineHeight);
        g.translate(-x,0);
    } 
    
    public void keyLeft(){ 
        if (xCursor>0) xCursor--; 
        else {
            //if (cursor==0) return;
            //
            if (cursor==0) {
                keyDwn();
                keyLeft();
                return;
            }
            //
            xCursor=xCnt-1;
            keyUp();
            setRotator();
        }
    }
    public void keyRight(){ 
        if ( xCursor < ( (cursor<lines-1)?(xCnt-1):(xLastCnt-1) ) ) {
            xCursor++;
            setRotator();
        }
        else {
            if (cursor==lines-1) return;
            xCursor=0;
            keyDwn();
        }
    }
    public void keyDwn(){
        super.keyDwn();
        if (cursor!=lines-1) return;
        if (xCursor >= xLastCnt) xCursor=xLastCnt-1;
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdBack) {
            destroyView();
            return;
        }
        if (c==cmdOK) { eventOk(); }
    }

    public void moveCursorEnd() {
        super.moveCursorEnd();
        xCursor=xLastCnt-1;
    }

    public void moveCursorHome() {
        super.moveCursorHome();
        xCursor=0;
    }

    public String getTipString() {
        return (String) smileTable.elementAt(cursor*xCnt+xCursor);
    }

    protected void drawBalloon(final Graphics g, int balloon, final String text) {
        if (cursor==0) balloon+=lineHeight+Balloon.getHeight();
        int x=xCursor*imgWidth;
        g.translate(x, balloon);
        Balloon.draw(g, text);
    }

    protected void pointerPressed(int x, int y) { 
        super.pointerPressed(x,y);
        if (x>=xCnt*imgWidth) return;
        xCursor=x/imgWidth;
        setRotator();
        if (cursor!=lines-1) return;
        if (xCursor >= xLastCnt) xCursor=xLastCnt-1;
    }
}
