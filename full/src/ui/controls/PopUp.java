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
    private int height, mHeight, width, mWidth, wBorder, hBorder;
    
    private Font font;;
    
    private String str;
    
    /** Creates a new instance of PopUp */
    public PopUp(Graphics g, String txt, int width, int height) {
        this.str=txt.trim();
        this.mHeight=g.getClipHeight();
        this.mWidth=g.getClipWidth();
        this.height=height;
        this.width=width;
        this.wBorder=(mWidth-width)/2;
        this.hBorder=(mHeight-height)/2;

        g.translate(wBorder-g.getTranslateX(), hBorder-g.getTranslateY());
        draw(g);
    }
    
    public void draw(Graphics g) {
        g.setColor(Colors.BALLOON_INK);
        g.fillRoundRect(0,0,width,height,10,10);

        g.setColor(Colors.BALLOON_BGND);
        g.fillRoundRect(1,1,width-2,height-2,10,10);
        
        g.setColor(Colors.BALLOON_INK);
        drawStrings(g, 2,2,width-4);
    }
    
    public void drawStrings(Graphics g, int x, int y, int w)
    {
        font=FontCache.getMsgFont();
        g.setFont(font);
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
                return;// y + getHeight();
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
    return;// y;
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
 