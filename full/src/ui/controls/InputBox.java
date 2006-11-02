/*
 * InputBox.java
 *
 * Created on 2 РќРѕСЏР±СЂСЊ 2006 Рі., 0:49
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
    
    public InputBox() {
       
        this.bitMapFont = BitMapFont.getInstance("/fonts/B52.bmf");
        this.messageViewer = this.bitMapFont.getViewer("Hello World! привет мир!");
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
    }

    protected void paint(Graphics g) {
        int x = 0;
        int y = 0;
        this.messageViewer.paint( x, y, g );
    }
}