/*
 * NewInputBox.java
 *
 * Created on 23.03.2006, 14:19
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
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import ui.ColorScheme;
import ui.FontCache;
import ui.VirtualList;

//import ui.polish.*;

public class NewInputBox extends Canvas {

    //private BitMapFont bitMapFont;
    //private BitMapFontViewer messageViewer;
    
    public String text="1";
    public int key;
    
    private Graphics g;

    public int width;

    public int height=10;

    private Timer timer;

    private boolean openedChar=false;

    private int lastkey;

    private int keycount=0;

    private int charsnum=0;
    
    
    private Font font=FontCache.getBalloonFont();

    private static String wrapSeparators=" .,-=/\\;:+*()[]<>~!@#%^_&";
    private Vector strings;

    public NewInputBox(String text, int key) {
        System.out.println("NewInputBox");
        this.text=text;
        this.key=key;
        g.setFont(font);
        this.height=font.getHeight();
        
        strings=parseMessage(100);
        System.out.println(height);
        sendKey(key);
        paint(g);
    }
    
    public void paint(Graphics g) {
        g.setColor(0xa0a0a0); g.fillRect(1, 1,width-1, height-1);
        g.setColor(0xffffff); g.fillRect(2, 2, width-2, height-2);
        
        g.setColor(ColorScheme.BALLOON_INK);
        drawAllStrings(g, 3,3);
    }

    public String getText() {
        return text;
    }

    public void sendKey(int key) {
        if (lastkey==key) {
            if (openedChar) {
                keycount++;                
            } else {
                keycount=0;    
            }
        } else {
            openedChar=false;;
            this.lastkey=key;
            keycount=0;
        }
        switch (key) {
            case 1: {
                String chars[]={" ","\n","1"};
                charsnum=(keycount>chars.length)? 0 : keycount;
                text=(openedChar)? text.substring(0,text.length()-1)+chars[charsnum] : text+chars[charsnum];
                break;
            }
            case 2: {
                String chars[]={"\u0430","\u0431","\u0432","\u0433","2","a","b","c","\u0410","\u0411","\u0412","\u0413","A","B","C"};
                charsnum=(keycount>chars.length)? 0 : keycount;
                text=(openedChar)? text.substring(0,text.length()-1)+chars[charsnum] : text+chars[charsnum];
                break;
            }
            case 3: {
                String chars[]={"\u0434","\u0435","\u0451","\u0436","\u0437","3","d","e","f","\u0414","\u0415","\u0401","\u0416","\u0417","D","E","F"};
                charsnum=(keycount>chars.length)? 0 : keycount;
                text=(openedChar)? text.substring(0,text.length()-1)+chars[charsnum] : text+chars[charsnum];
                break;
            }
            case 4: {
                String chars[]={"\u0438","\u0439","\u043A","\u043B","4","g","h","i","\u0418","\u0419","\u041A","\u041B","G","H","I"};
                charsnum=(keycount>chars.length)? 0 : keycount;
                text=(openedChar)? text.substring(0,text.length()-1)+chars[charsnum] : text+chars[charsnum];
                break;
            }
            case 5: {
                String chars[]={"\u043C","\u043D","\u043E","5","j","k","l","\u041C","\u041D","\u041E","J","K","L"};
                charsnum=(keycount>chars.length)? 0 : keycount;
                text=(openedChar)? text.substring(0,text.length()-1)+chars[charsnum] : text+chars[charsnum];
                break;
            }
            case 6: {
                String chars[]={"\u043F","\u0440","\u0441","6","m","n","o","\u041F","\u0420","\u0421","M","N","O"};
                charsnum=(keycount>chars.length)? 0 : keycount;
                text=(openedChar)? text.substring(0,text.length()-1)+chars[charsnum] : text+chars[charsnum];
                break;
            }
            case 7: {
                String chars[]={"\u0442","\u0443","\u0444","\u0445","7","p","q","r","s","\u0422","\u0423","\u0424","\u0425","P","Q","R","S"};
                charsnum=(keycount>chars.length)? 0 : keycount;
                text=(openedChar)? text.substring(0,text.length()-1)+chars[charsnum] : text+chars[charsnum];
                break;
            }
           case 8: {
                String chars[]={"\u0446","\u0447","\u0448","\u0449","\u044A","8","t","u","v","\u0426","\u0427","\u0428","\u0429","\u042A","T","U","V"};
                charsnum=(keycount>chars.length)? 0 : keycount;
                text=(openedChar)? text.substring(0,text.length()-1)+chars[charsnum] : text+chars[charsnum];
                break;
            }
            case 9: {
                String chars[]={"\u044B","\u044C","\u044D","\u044E","\u044F","9","w","x","y","z","\u042B","\u042C","\u042D","\u042E","\u042F","W","X","Y","Z"};
                charsnum=(keycount>chars.length)? 0 : keycount;
                text=(openedChar)? text.substring(0,text.length()-1)+chars[charsnum] : text+chars[charsnum];
                break;
            }
            case 0: {
                String chars[]={".",",","?","!","'","\"","0","+","-","(",")","@","/",":","_","~","#","$","%","^","&","*","№"};
                charsnum=(keycount>chars.length)? 0 : keycount;
                text=(openedChar)? text.substring(0,text.length()-1)+chars[charsnum] : text+chars[charsnum];
                break;
            }
            case -1: {
                text=text.substring(0,text.length()-1);
                break;
            }
            default : {
                text="";
                String chars[]={};
            }
        }

        this.text=text;
        this.width=100;
        this.height=20;
        startTimer();
    }
    
    public void startTimer() {
        stopTimer();
        timer = new Timer();
        timer.schedule(new RemindTask(), 1000);
        openedChar=true;
    }
    
    public void stopTimer() {
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
        openedChar=false;
    }


    private Vector parseMessage(int stringWidth) {
        Vector lines=new Vector();
        int state=0;
        String txt=text;
        
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
            
            if (lines.isEmpty()) lines.removeElementAt(lines.size()-1);  //последняя строка
            state++;
        }
        return lines;
    }
    
    private void drawAllStrings(Graphics g, int x, int y) {
        if (strings.size()<1) return;

	for (int line=0; line<strings.size(); ) 
	{
            g.drawString((String) strings.elementAt(line), x, y, Graphics.TOP|Graphics.LEFT);
            line=line+1;
            y += getFontHeight();
	}
    }
    
    private int getFontHeight() {
        int result=font.getHeight();
        return result;
    }
    
    public int getStringsHeight() {
        int result=getFontHeight()*strings.size();
        return result;
    }

    class RemindTask extends TimerTask {
        public void run() {
            stopTimer();
        }
   }
}