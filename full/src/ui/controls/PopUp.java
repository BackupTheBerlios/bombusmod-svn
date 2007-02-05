/*
 * PopUp.java
 *
 * Created on 2 Февраль 2007 г., 0:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.Colors;
import ui.FontCache;

/**
 *
 * @author adsky
 */
public class PopUp {
    private int x1,y1,x2;

    private int height;

    private int y2;
    
    protected Font font=FontCache.getMsgFont();
    
    //private static String wrapSeparators=" .,-=/\\;:+*()[]<>~!@#%^_&";
    
    /** Creates a new instance of PopUp */
    public PopUp(Graphics g, String txt, int x1, int y1, int x2, int y2) {
        String text=txt.trim();
        this.x1=x1;
        this.y1=y1;
        this.x2=x2;
        this.y2=y2;
        draw(g, txt);
    }
    
    public void draw(Graphics g, String txt) {
        g.setColor(Colors.BALLOON_INK);
        g.fillRoundRect(x1,y1,x2,y2,10,10);

        g.setColor(Colors.BALLOON_BGND);
        g.fillRoundRect(x1+1,y1+1,x2-2,y2-2,10,10);
        
        g.setColor(Colors.BALLOON_INK);
        drawStrings(g, x1+2,y1+2,x2-2, txt);
        
    }
    
    public void drawStrings(Graphics g, int x, int y, int w, String str)
    {
        int ii, pointS = 0, len = str.length();
        for ( ii = 0; ii >= 0; )
        {
            //wrapSeparators.indexOf(c)>=0
            ii = str.indexOf(" ", ii + 1);
            if ( ii < 0 )
            {//больше нет пробелов
                if ( getLen(str) > w )
                {
                    g.drawString(str.substring(0, pointS), x, y, Graphics.TOP|Graphics.LEFT);
                    str = str.substring(pointS + 1);
                    y += getHeight();
                }
                g.drawString(str, x, y,  Graphics.TOP|Graphics.LEFT);
                //return y + getHeight();
            }
            else
            {
                if ( getLen(str.substring(0, ii)) < w )
                {
                    pointS = ii;
                }
                else
                {
                    g.drawString(str.substring(0, pointS), x, y, Graphics.TOP|Graphics.LEFT);
                    str = str.substring(pointS + 1);
                    pointS = ii = 0;
                    y += getHeight();
                }
            }
    }
    //return y;
  }

    private int getLen(String str) {
        int result=font.stringWidth(str);
        return result;
    }

    private int getHeight() {
        int result=font.getHeight();
        return result;
    }
}
 