/*
 * Baloon.java
 *
 * Created on 6 Февраль 2006 г., 23:09
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui.controls;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.Colors;
import ui.FontCache;

/**
 *
 * @author Evg_S
 */
public class Balloon {
    
    public static int getHeight(){
        Font f=FontCache.getBalloonFont();
        return f.getHeight()+3;
    }
 /*   
  public static void draw(Graphics g, String str)
  {
    int x=0;
    int y=0;
    int w=g.getClipWidth();
             
    Font f=FontCache.getBalloonFont();
    g.setFont(f); 
    
    int ii, pointS = 0, len = str.length();
    for ( ii = 0; ii >= 0; )
    {
      ii = str.indexOf(" ", ii + 1);
      if ( ii < 0 )
      {//больше нет пробелов
        g.setColor(Colors.BALLOON_BGND);
        g.fillRect(0, 0, getLen(str), y+ f.getHeight());
        if ( getLen(str) > w )
        {
          g.setColor(Colors.BALLOON_INK);
          g.drawString(str.substring(0, pointS), x, y, Graphics.TOP|Graphics.LEFT);
          str = str.substring(pointS + 1);
          y += getHeight();
        }
        g.setColor(Colors.BALLOON_INK);
        g.drawString(str, x, y,  Graphics.TOP|Graphics.LEFT);
        //return y + f.getHeight();
      }
      else
      {
        if ( f.stringWidth(str.substring(0, ii)) < w )
        {
          pointS = ii;
        }
        else
        {
          g.drawString(str.substring(0, pointS), x, y, Graphics.TOP|Graphics.LEFT);
          str = str.substring(pointS + 1);
          pointS = ii = 0;
          y += f.getHeight();
        }
      }
    }
    //return y;
  }
  

    private static int getLen(String str) {
        Font f=FontCache.getBalloonFont();
        return f.stringWidth(str);
    }
  */

  public static void draw(Graphics g, String text) {
        Font f=FontCache.getBalloonFont();
        g.setFont(f);
        int height=getHeight();
        int width=f.stringWidth(text)+6;
        
        int y=height-g.getTranslateY();
        if (y<0) y=0;
        y-=height-1;
        g.translate(0, y);
        
        g.setColor(Colors.BALLOON_INK);
        g.fillRect(2, 0, width, height);

        g.setColor(Colors.BALLOON_BGND);
        g.fillRect(3, 1, width-2, height-2);
       
        g.setColor(Colors.BALLOON_INK);
        g.drawString(text, 5, 2, Graphics.TOP | Graphics.LEFT);
    }
}
