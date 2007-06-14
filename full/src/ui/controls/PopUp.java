/*
 * PopUp.java
 *
 * Created on 2.02.2007, 0:19
 *
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ui.controls;

import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.ColorScheme;
import ui.FontCache;

public class PopUp {
    private int popUpHeight, popUpWidth, widthBorder, heightBorder;
    private int border=8;
    private int padding=4;
    
    private Font font;
    
    private String str;
    
    private static String wrapSeparators=" .,-=/\\;:+*()[]<>~!@#%^_&";
    private boolean wordsWrap;
    
    private boolean kikoban=false;
    
    private Vector strings;
    
    /** Creates a new instance of PopUp */
    public PopUp(Graphics g, String txt) {
        str=txt.trim();
        int height=g.getClipHeight();
        int width=g.getClipWidth();
        
        kikoban=(str.startsWith("!"))?true:false;
        font=(kikoban)?FontCache.getClockFont():FontCache.getBalloonFont();

        strings=parseMessage(width-border-padding);
        
        int length=getMaxWidth();
        
        int strWdth=(length!=0)?length:getStrWidth(str);
        
        popUpWidth=(strWdth>(width-border))?width-border:strWdth+padding;

        widthBorder=(strWdth>popUpWidth)?border/2:(width-popUpWidth)/2;


        int stringsHeight=getHeight();

        if (stringsHeight>height) {
            heightBorder=0;
            popUpHeight=height;
        } else {
            popUpHeight=stringsHeight+padding;
            heightBorder=(height-popUpHeight)/2;
        }
     
        g.translate(widthBorder-g.getTranslateX(), heightBorder-g.getTranslateY());

        g.setClip(0,0,popUpWidth+1,popUpHeight+1);

        draw(g);
    }
    
    public void draw(Graphics g) {
        g.setColor((kikoban)?0xff0000:ColorScheme.BALLOON_INK);
        
        g.fillRect(1,1,popUpWidth,popUpHeight);                 //shadow

        g.fillRect(0,0,popUpWidth,popUpHeight);                     //border
        
        g.setColor((kikoban)?0xff0000:ColorScheme.BALLOON_BGND);
        g.fillRect(1,1,popUpWidth-2,popUpHeight-2);                 //fill
        
        g.setColor((kikoban)?0xffff00:ColorScheme.BALLOON_INK);
        g.setFont(font);
        
        drawAllStrings(g, 2,3);
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
                        lines.addElement(s.toString()); //lastest string in l
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
            
            if (lines.isEmpty()) lines.removeElementAt(lines.size()-1);  //lastest string
            state++;
            
            s=null;
        }
        return lines;
    }
    
    private void drawAllStrings(Graphics g, int x, int y) {
        Vector lines=strings;
        if (lines.size()<1) return;
        
        int fh=getFontHeight();

	for (int line=0; line<lines.size(); ) 
	{
            g.drawString((String) lines.elementAt(line), x, y, Graphics.TOP|Graphics.LEFT);
            line=line+1;
            y += fh;
	}
    }
    
    private int getFontHeight() {
        return font.getHeight();
    }

    private int getHeight() {
        return getFontHeight()*strings.size();
    }
    
    private int getStrWidth(String string) {
        return font.stringWidth(string);
    }
    
    private int getMaxWidth() {
        Vector lines=strings;

        int length=0;
        
        if (lines.size()<1) return length;

	for (int line=0; line<lines.size(); ) 
	{
            String string=(String) lines.elementAt(line);
            length=(length>getStrWidth(string))?length:getStrWidth(string);
            line++;
	}
        return length;
    }
}
 
