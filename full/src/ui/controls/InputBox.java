/*
 * InputBox.java
 *
 * Created on 2 Ноябрь 2006 г., 0:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextField;
import ui.Colors;
import ui.FontCache;

import ui.polish.*;

/**
 *
 * @author [AD]
 */
public class InputBox extends Canvas {
    
    private BitMapFont bitMapFont;
    private BitMapFontViewer messageViewer;    
    
    public InputBox() {
        
        this.bitMapFont = BitMapFont.getInstance("/fonts/aston.bmf");
        this.messageViewer = this.bitMapFont.getViewer("Hello World!");
        int availableWidth = getWidth() - 20;
        int padding = 2;
        int textOrientation = Graphics.LEFT;
        this.messageViewer.layout( availableWidth, availableWidth, padding, textOrientation );
        
    }

    public int getVHeight() {
        return this.messageViewer.getHeight();
    }

    public void drawItem(Graphics g, int i, boolean b) {

        g.setColor(Colors.BAR_BGND);
        g.fillRect(0, 0, g.getClipWidth(), g.getClipHeight());
        paint(g);
        /*
         g.setColor(Colors.BALLOON_BGND);
        g.fillRect(0, 0, width, height);
        g.setColor(Colors.BALLOON_INK);
        g.drawString("test", (width/2)+10, height/2, Graphics.TOP | Graphics.LEFT);
         */
    }

    protected void paint(Graphics g) {
        int x = 0;
        int y = 0;
        this.messageViewer.paint( x, y, g );
    }
}