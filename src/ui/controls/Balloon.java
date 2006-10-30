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
    
    public static void draw(Graphics g, String text) {
        String txt=text.trim();
        Font f=FontCache.getBalloonFont();
        g.setFont(f);
        int height=getHeight();
        int width=f.stringWidth(txt)+6;
        
        int y=height-g.getTranslateY();
        if (y<0) y=0;
        y-=height-1;
        g.translate(0, y);
        
        g.setColor(Colors.BALLOON_INK);
        g.fillRect(2, 0, width, height);

        g.setColor(Colors.BALLOON_BGND);
        g.fillRect(3, 1, width-2, height-2);
       
        g.setColor(Colors.BALLOON_INK);
        g.drawString(txt, 5, 2, Graphics.TOP | Graphics.LEFT);
    }
/*    
    public static int draw2(Graphics g, String str) {
        int x=0;
        int y=0;
        int w= g.getClipWidth();
        
        int ii, pointS = 0, len = str.length();
        
        Font f=FontCache.getBalloonFont();
        g.setFont(f);
        g.setColor(Colors.BALLOON_INK);
        
        for ( ii = 0; ii >= 0; ){
        if ( getLen(str) > w ) {
      
          g.drawString(str.substring(0, pointS), x, y, Graphics.TOP|Graphics.LEFT);
          str = str.substring(pointS + 1);
          y += f.getHeight();
        }
        g.drawString(str, x, y,  Graphics.TOP|Graphics.LEFT);
        return y + f.getHeight();
        }
        return y;
    }

    public static void draw3(Graphics g, String s) {
 
        int width=g.getClipWidth()-2;
        
        Font f=FontCache.getBalloonFont();
        g.setFont(f);
        int last = 0;
        int line=  0;
        int lines= 0;
        
        if((f.stringWidth(s.substring(last,s.length())) < width)){
            g.setColor(Colors.BALLOON_BGND);
            g.fillRect(0, 0, f.stringWidth(s.substring(last,s.length())), line + getHeight());
            
            g.setColor(Colors.BALLOON_INK);
            g.drawRect(0, 0, f.stringWidth(s.substring(last,s.length())), line + getHeight());
            g.drawString(s.substring(last,s.length()).trim(),1,line,Graphics.TOP|Graphics.LEFT);  
            return;
        }

        for(int i=0;i<s.length();i++){
                if((f.stringWidth(s.substring(last,i)) >= width)){
                    g.drawString(s.substring(last,i).trim(),1,line,Graphics.TOP|Graphics.LEFT);
                    last = i; line = line + getHeight();
                    lines++;
                } else {
                        g.setColor(Colors.BALLOON_INK);
                        g.drawString(s.substring(last,s.length()).trim(),1,line,Graphics.TOP|Graphics.LEFT);
                        break;
                 }
        }
    }

    private static int getLen(String str) {
        Font f=FontCache.getBalloonFont();
        return f.stringWidth(str);
    }
 */
}
