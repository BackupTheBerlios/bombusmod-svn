/*
 * DrawInputBox.java
 *
 * Created on 26.04.2007, 0:19
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

package ui.inputbox;

import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.ColorScheme;
import ui.FontCache;

/**
 *
 * @author adsky
 */
public class DrawInputBox {
    private int height, width;
    
    private Font font;
    
    private String str;
    
    private static String wrapSeparators=" .,-=/\\;:+*()[]<>~!@#%^_&";
    
    private boolean kikoban=false;
    
    private Vector strings;

    public DrawInputBox(Graphics g, String txt) {
        this.str=txt.trim();
        this.height=g.getClipHeight();
        this.width=g.getClipWidth();

        font=FontCache.getBalloonFont();

        strings=parseMessage(width-4);

        g.setClip(0, height-getHeight(), width, getHeight());
        g.translate(0, height-getHeight());
        draw(g);
    }
    
    public void draw(Graphics g) {
        g.setColor(ColorScheme.BALLOON_INK);
        g.fillRect(0,0,width,getHeight());
        
        g.setColor(ColorScheme.BALLOON_BGND);
        g.fillRect(1,1,width-2,getHeight()-2);
        
        g.setColor(ColorScheme.BALLOON_INK);
        g.setFont(font);
        drawStrings(g);
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
                        lines.addElement(s.toString()); //по�?ледн�?�? под�?трока в l
                        s.setLength(0); w=0;
                    }
                }
                if (c==0x09) c=0x20;

                if (c>0x1f) wordWidth+=cw;

                if (wrapSeparators.indexOf(c)>=0) {
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
            
            if (lines.isEmpty()) lines.removeElementAt(lines.size()-1);  //по�?ледн�?�? �?трока
            state++;
        }
        return lines;
    }
    
    private void drawStrings(Graphics g) {
        int y=1;
        if (strings.size()<1) return;

	for (int line=0; line<strings.size(); ) 
	{
            g.drawString((String) strings.elementAt(line), 2, y, Graphics.TOP|Graphics.LEFT);
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
 