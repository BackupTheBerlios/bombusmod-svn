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
import javax.microedition.lcdui.Graphics;
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

    private String text;
    
    public InputBox(String text) {
        this.text=text;
       
        this.bitMapFont = BitMapFont.getInstance("/fonts/sirclive.bmf");
        this.messageViewer = this.bitMapFont.getViewer(text);
        int availableWidth = getWidth() - 2;
        int padding = 2;
        int textOrientation = Graphics.LEFT;
        this.messageViewer.layout( availableWidth, availableWidth, padding, textOrientation );
    }
    public int getVHeight() {
        return this.messageViewer.getHeight();
    }

    public void drawItem(Graphics g, int i, boolean b) {

        g.setColor(Colors.BAR_BGND);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(0xa0a0a0);
        g.fillRect(1, 1, getWidth()-1, getHeight()-1);
        g.setColor(0xffffff);
        g.fillRect(2, 2, getWidth()-2, getHeight()-2);
        paint(g);
    }

    protected void paint(Graphics g) {
        int x = 0;
        int y = 0;
        this.messageViewer.paint( x, y, g );
    }
}