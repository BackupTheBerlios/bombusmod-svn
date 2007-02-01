/*
 * PopUp.java
 *
 * Created on 2 Февраль 2007 г., 0:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls;

import javax.microedition.lcdui.Graphics;
import ui.Colors;
import ui.polish.BitMapFont;
import ui.polish.BitMapFontViewer;

/**
 *
 * @author adsky
 */
public class PopUp {
    
    private BitMapFont bitMapFont;
    private BitMapFontViewer messageViewer;

    private int x1,y1,x2;

    private int height;

    private int y2;
    
    /** Creates a new instance of PopUp */
    public PopUp(Graphics g, String txt, int x1, int y1, int x2, int y2) {
        String text=txt.trim();
        this.x1=x1;
        this.y1=y1;
        this.x2=x2;
        this.y2=y2;
        
        this.bitMapFont = BitMapFont.getInstance("/fonts/font.bmf");
        this.messageViewer = this.bitMapFont.getViewer(text);
        int availableWidth = x2 - 6;
        int padding = 2;
        this.messageViewer.layout( availableWidth, availableWidth, padding, Graphics.LEFT);
        this.height=this.messageViewer.getHeight()+4;
        
        draw(g);
        //g.setClip(x1+6,y1+6,x2-7,y2-7);
        drawText(g);
    }
    
    public void draw(Graphics g) {
        g.setColor(Colors.BALLOON_INK);
        g.fillRoundRect(x1,y1,x2,y2,10,10);
        //.fillRect(2, 0, width, height);

        g.setColor(Colors.BALLOON_BGND);
        g.fillRoundRect(x1+1,y1+1,x2-2,y2-2,10,10);
        //.fillRect(3, 1, width-2, height-2);
    }

    public void drawText(Graphics g) {
        this.messageViewer.paint( x1+6, y1+6, g );
    }
    
/*
public class InputBox extends Canvas {


    

    protected void paint(Graphics g) {
            g.setColor(0xa0a0a0); g.fillRect(1, 1,width-1, height-1);
            g.setColor(0xffffff); g.fillRect(2, 2, width-2, height-2);
            this.messageViewer.paint( 2, 2, g );
    }

                this.text=text;
		this.bitMapFont = BitMapFont.getInstance("/fonts/font.bmf");
		this.messageViewer = this.bitMapFont.getViewer(text);
                this.width=getWidth();
                int availableWidth = width - 6;
		int padding = 2;
		int orientation = Graphics.LEFT;
		this.messageViewer.layout( availableWidth, availableWidth, padding, orientation );
                this.height=this.messageViewer.getHeight()+4;
                startTimer();
    }
}
 */
}
