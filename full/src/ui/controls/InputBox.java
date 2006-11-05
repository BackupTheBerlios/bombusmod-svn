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

    public InputBox(String text) {
		this.bitMapFont = BitMapFont.getInstance("/fonts/sirclive.bmf");
		this.messageViewer = this.bitMapFont.getViewer(text);
		int availableWidth = getWidth() - 5;
		int padding = 2;
		int orientation = Graphics.LEFT;
		this.messageViewer.layout( availableWidth, availableWidth, padding, orientation );
    }
    public int getVHeight() {
        return this.messageViewer.getHeight();
    }
    
    public int getFHeight() {
        return this.messageViewer.getFontHeight();
    }

    public void drawItem(Graphics g) {
        paint(g);
    }

    protected void paint(Graphics g) {
        g.setColor(0xa0a0a0); g.fillRect(1, 1, getWidth()-1, getVHeight()-1);
        g.setColor(0xffffff); g.fillRect(2, 2, getWidth()-2, getVHeight()-2);
        
        int x = 0;
        int y = 0;
        this.messageViewer.paint( x, y, g );
    }
}

/*
 import de.enough.polish.util.*;

public class MyCanvas extends Canvas {

	private BitMapFont bitMapFont;
	privte BitMapFontViewer messageViewer;

	public MyCanvas() {
		this.bitMapFont = BitMapFont.getInstance("/coolfont.bmf");
		this.messageViewer = this.bitMapFont.getViewer("Hello World!");
		int availableWidth = getWidth() - 20;
		int padding = 2;
		int textOrientation = Graphics.LEFT;
		this.messageViewer.layout( availableWidth, availableWidth, padding, orientation );
	}

	public void paint( Graphics g ) {
		int x = 10;
		int y = 20;
		this.messageViewer.paint( x, y, g );
	}
}
 */