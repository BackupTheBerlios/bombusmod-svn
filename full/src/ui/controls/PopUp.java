/*
 * PopUp.java
 *
 * Created on 2 Февраль 2007 г., 0:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls;

import java.util.Vector;
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
    
    private Font font;
    
    private String str;
    
    private static String wrapSeparators=" .,-=/\\;:+*()[]<>~!@#%^_&";
    private boolean wordsWrap;
    
    private boolean kikoban=false;
    
    private Vector strings;
    
    /** Creates a new instance of PopUp */
    public PopUp(Graphics g, String txt, int width, int height) {
        this.str=txt.trim();
        this.mHeight=g.getClipHeight();
        this.mWidth=g.getClipWidth();
        this.width=width;
        this.wBorder=(mWidth-width)/2;

        if (str.startsWith("!")==true) {
            kikoban=true;
        }
        font=(kikoban)?FontCache.getClockFont():FontCache.getBalloonFont();

        strings=parseMessage(width-4);
        
        int stringsHeight=getHeight();

        if (stringsHeight>mHeight) {
            this.hBorder=(mHeight-height)/2;
            this.height=height;
        } else {
            this.hBorder=(height-stringsHeight)/2;
            this.height=stringsHeight+2;
        }
        
        g.translate(wBorder-g.getTranslateX(), hBorder-g.getTranslateY());
        g.setClip(0,0,width,height);
        draw(g);
    }
    
    public void draw(Graphics g) {
        g.setColor((kikoban)?0xff0000:Colors.BALLOON_INK);
        g.fillRoundRect(0,0,width,height,10,10);
        
        g.setColor((kikoban)?0xff0000:Colors.BALLOON_BGND);
        g.fillRoundRect(1,1,width-2,height-2,10,10);
        
        g.setColor((kikoban)?0xffff00:Colors.BALLOON_INK);
        g.setFont(font);
        drawAllStrings(g, 4,2);
    }

    private Vector parseMessage(int stringWidth) {
        Vector lines=new Vector();
        int state=0;
        String txt=str;
        
        while (state<1) {
            int w=0;
            StringBuffer s=new StringBuffer();
	    int wordWidth=0;
	    int wordStartPos=0;

            if (txt==null) {
                state++;
                continue;
            }
            
            int pos=0;
            while (pos<txt.length()) {
                char c=txt.charAt(pos);

                int cw=font.charWidth(c);
                if (c!=0x20) {
                    boolean newline= ( c==0x0d || c==0x0a /*|| c==0xa0*/ );
                    if (wordWidth+cw>stringWidth || newline) {
                        s.append(txt.substring(wordStartPos,pos));
                        w+=wordWidth;
                        wordWidth=0;
                        wordStartPos=pos;
                        if (newline) wordStartPos++;
                    }
                    if (w+wordWidth+cw>stringWidth || newline) {
                        lines.addElement(s.toString()); //последняя подстрока в l
                        s.setLength(0); w=0;
                    }
                }
                if (c==0x09) c=0x20;

                if (c>0x1f) wordWidth+=cw;

                if (wrapSeparators.indexOf(c)>=0 || !wordsWrap) {
                    if (pos>wordStartPos) 
                        s.append(txt.substring(wordStartPos,pos));
                    if (c>0x1f) s.append(c);
                    w+=wordWidth;
                    wordStartPos=pos+1;
                    wordWidth=0;
                }
                
                pos++;
            }
	    if (wordStartPos!=pos)
		s.append(txt.substring(wordStartPos,pos));
            if (s.length()>0) {
                lines.addElement(s.toString());
            }
            
            if (lines.isEmpty()) lines.removeElementAt(lines.size()-1);  //последняя строка
            state++;
        }
        return lines;
    }
    
    private void drawAllStrings(Graphics g, int x, int y) {
        Vector lines=strings;
        if (lines.size()<1) return;

	for (int line=0; line<lines.size(); ) 
	{
            g.drawString((String) lines.elementAt(line), x, y, Graphics.TOP|Graphics.LEFT);
            line=line+1;
            y += getFontHeight();
	}
    }
    
    private int getFontHeight() {
        int result=font.getHeight();
        return result;
    }
    
    private int getHeight() {
        int result=getFontHeight()*strings.size();
        return result;
    }
}
 