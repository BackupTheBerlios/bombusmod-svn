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
        return f.getHeight();
    }
    
    public static int draw(Graphics g, String str) {
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
/*
   public int drawStrings(Graphics g, String str) {
    int x=0;
    int y=0;
    int w= g.getClipWidth();
 
    int ii, pointS = 0, len = str.length();
	
    for ( ii = 0; ii >= 0; )
	
    {
	
      ii = str.indexOf(" ", ii + 1);
	
      if ( ii < 0 )
	
      {//больше нет пробелов
	
        if ( getLen(str) > w )
	
        {
	
          g.drawString(str.substring(0, pointS), x, y, Graphics.TOP|Graphics.LEFT);
	
          str = str.substring(pointS + 1);
	
          y += getHight();
	
        }
	
        g.drawString(str, x, y,  Graphics.TOP|Graphics.LEFT);
	
        return y + fontB.getHeight();
	
      }
	
      else
	
      {
	
        if ( fontB.stringWidth(str.substring(0, ii)) < w )
	
        {
	
          pointS = ii;
	
        }
	
        else
	
        {
	
          g.drawString(str.substring(0, pointS), x, y, Graphics.TOP|Graphics.LEFT);
	
          str = str.substring(pointS + 1);
	
          pointS = ii = 0;
	
          y += fontB.getHeight();
	
        }
	
      }
	
    }
	
    return y;
	
  }
 */
  public static void draw2(Graphics g, String s) {
 
        int width=g.getClipWidth()-2;
        
        Font f=FontCache.getBalloonFont();
        g.setFont(f);
        int last = 0;
        int line=  0;
        int lines= 0;
        
        if((f.stringWidth(s.substring(last,s.length())) < width)){
            System.out.println("dlina menshe shiriny, stroka №"+lines);

            g.setColor(Colors.BALLOON_BGND);
            g.fillRect(0, 0, f.stringWidth(s.substring(last,s.length())), line + getHeight());
            
            g.setColor(Colors.BALLOON_INK);
            g.drawRect(0, 0, f.stringWidth(s.substring(last,s.length())), line + getHeight());
            g.drawString(s.substring(last,s.length()).trim(),1,line,Graphics.TOP|Graphics.LEFT);  
            return;
        }

        for(int i=0;i<s.length();i++){
                if((f.stringWidth(s.substring(last,i)) >= width)){
                    System.out.println("dlina bolshe shiriny, stroka №"+lines);
                    /*g.setColor(Colors.BALLOON_BGND);
                    g.fillRect(0, 0, f.stringWidth(s.substring(last,i))+1, line + getHeight());

                        g.setColor(Colors.BALLOON_INK);
                        g.drawLine(0,0,f.stringWidth(s.substring(last,i))+1,0); //горизонтальная верх
                        g.drawLine(0,line + getHeight(),f.stringWidth(s.substring(last,i))+1,line + getHeight()); //горизонтальная низ

                        g.drawLine(0,0,0,line + getHeight()); //вертикальная верх
                        g.drawLine(f.stringWidth(s.substring(last,i))+1,0,f.stringWidth(s.substring(last,i))+1,line + getHeight()); //вертикальная низ
                    */
                    g.drawString(s.substring(last,i).trim(),1,line,Graphics.TOP|Graphics.LEFT);
                    last = i; line = line + getHeight();
                    lines++;
                } else {
                        System.out.println("dlina menshe shiriny, stroka №"+lines);
                        /*g.setColor(Colors.BALLOON_BGND);
                        if (lines<1){
                            g.setColor(Colors.BALLOON_BGND);
                            g.fillRect(0, line, f.stringWidth(s.substring(last,s.length()))+1, line+ getHeight());

                            g.setColor(Colors.BALLOON_INK);
                            g.drawLine(0,line + getHeight(),f.stringWidth(s.substring(last,s.length()))+1,line + getHeight()); //горизонтальная низ
                            g.drawLine(0,0,0,line + getHeight()); //вертикальная верх
                            g.drawLine(f.stringWidth(s.substring(last,s.length()))+1,0,f.stringWidth(s.substring(last,s.length()))+1,line + getHeight()); //вертикальная низ
                        } else {
                            g.setColor(Colors.BALLOON_BGND);
                            g.fillRect(0, line, f.stringWidth(s.substring(last,s.length()))+1, line);

                            g.setColor(Colors.BALLOON_INK);
                            g.drawLine(0,0,f.stringWidth(s.substring(last,s.length()))+1,0); //горизонтальная верх
                            g.drawLine(0,line + getHeight(),f.stringWidth(s.substring(last,s.length()))+1,line + getHeight()); //горизонтальная низ
                            g.drawLine(0,0,0,line + getHeight()); //вертикальная верх
                            g.drawLine(f.stringWidth(s.substring(last,s.length()))+1,0,f.stringWidth(s.substring(last,s.length()))+1,line + getHeight()); //вертикальная низ
                        }
                        */
                        g.setColor(Colors.BALLOON_INK);
                        g.drawString(s.substring(last,s.length()).trim(),1,line,Graphics.TOP|Graphics.LEFT);
                        break;
                 }
        }
        /*
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
        */
    }

    private static int getLen(String str) {
        Font f=FontCache.getBalloonFont();
        return f.stringWidth(str);
    }
}
