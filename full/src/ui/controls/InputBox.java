/*
 * InputBox.java
 *
 * Created on 2 Ноябрь 2006 г., 0:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import ui.Colors;
import ui.FontCache;
import ui.VirtualList;

import ui.polish.*;

/**
 *
 * @author [AD]
 */
public class InputBox extends Canvas {

    private BitMapFont bitMapFont;
    private BitMapFontViewer messageViewer;
    
    public String text;
    public int key;
    
    private Graphics g;

    public int width;

    public int height;

    private Timer timer;

    private boolean openedChar=false;

    private int lastkey;

    private int keycount=0;

    private int charsnum=0;

    public InputBox(String text, int key) {
        this.text=text;
        this.key=key;
        sendKey(key);
    }

    public void drawItem(Graphics g) {
        paint(g);
    }

    protected void paint(Graphics g) {
            g.setColor(0xa0a0a0); g.fillRect(1, 1,width-1, height-1);
            g.setColor(0xffffff); g.fillRect(2, 2, width-2, height-2);
            this.messageViewer.paint( 2, 2, g );
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
                String chars[]={" ","1","!","@","?"};
                if (keycount>chars.length) {
                    charsnum=0;
                } else {
                    charsnum=keycount;
                }
                if (openedChar) {
                    text=text.substring(0,text.length()-1)+chars[charsnum];
                } else {
                    text=text+chars[charsnum];                    
                }
                //text=text+"\u00A0";
                break;
            }
            case 2: {
                String chars[]={"\u0430","\u0431","\u0432","\u0433","2","a","b","c"};
                if (keycount>chars.length) {
                    charsnum=0;
                } else {
                    charsnum=keycount;
                }
                if (openedChar) {
                    text=text.substring(0,text.length()-1)+chars[charsnum];
                } else {
                    text=text+chars[charsnum];                    
                } 
                //text=text+"\u0430";
                break;
            }
            case 3: {
                String chars[]={"\u0434","\u0435","\u0436","\u0437","3","d","e","f"};
                if (keycount>chars.length) {
                    charsnum=0;
                } else {
                    charsnum=keycount;
                }
                if (openedChar) {
                    text=text.substring(0,text.length()-1)+chars[charsnum];
                } else {
                    text=text+chars[charsnum];                    
                }   
                //text=text+"\u0434";
                break;
            }
            case 4: {
                String chars[]={"\u0438","\u0439","\u043A","\u043B","4","g","h","i"};
                if (keycount>chars.length) {
                    charsnum=0;
                } else {
                    charsnum=keycount;
                }
                if (openedChar) {
                    text=text.substring(0,text.length()-1)+chars[charsnum];
                } else {
                    text=text+chars[charsnum];                    
                }
                //text=text+"\u0438";
                break;
            }
            case 5: {
                String chars[]={"\u043C","\u043D","\u043E","5","j","k","l"};
                if (keycount>chars.length) {
                    charsnum=0;
                } else {
                    charsnum=keycount;
                }
                if (openedChar) {
                    text=text.substring(0,text.length()-1)+chars[charsnum];
                } else {
                    text=text+chars[charsnum];                    
                }
                //text=text+"\u043C";
                break;
            }
            case 6: {
                String chars[]={"\u043F","\u0440","\u0441","6","m","n","o"};
                if (keycount>chars.length) {
                    charsnum=0;
                } else {
                    charsnum=keycount;
                }
                if (openedChar) {
                    text=text.substring(0,text.length()-1)+chars[charsnum];
                } else {
                    text=text+chars[charsnum];                    
                }
                //text=text+"\u043F";
                break;
            }
            case 7: {
                String chars[]={"\u0442","\u0443","\u0444","\u0445","7","p","q","r","s"};
                if (keycount>chars.length) {
                    charsnum=0;
                } else {
                    charsnum=keycount;
                }
                if (openedChar) {
                    text=text.substring(0,text.length()-1)+chars[charsnum];
                } else {
                    text=text+chars[charsnum];                    
                }
                //text=text+"\u0442";
                break;
            }
           case 8: {
                String chars[]={"\u0446","\u0447","\u0448","\u0449","\u044A","8","t","u","v"};
                if (keycount>chars.length) {
                    charsnum=0;
                } else {
                    charsnum=keycount;
                }
                if (openedChar) {
                    text=text.substring(0,text.length()-1)+chars[charsnum];
                } else {
                    text=text+chars[charsnum];                    
                }
                //text=text+"\u0446";
                break;
            }
            case 9: {
                String chars[]={"\u044B","\u044C","\u044D","\u044E","\u044F","9","w","x","y","z"};
                if (keycount>chars.length) {
                    charsnum=0;
                } else {
                    charsnum=keycount;
                }
                if (openedChar) {
                    text=text.substring(0,text.length()-1)+chars[charsnum];
                } else {
                    text=text+chars[charsnum];                    
                }
                //text=text+"\u044B";
                break;
            }
            case 0: {
                String chars[]={"\n",".",",","-","+",":"};
                if (keycount>chars.length) {
                    charsnum=0;
                } else {
                    charsnum=keycount;
                }
                if (openedChar) {
                    text=text.substring(0,text.length()-1)+chars[charsnum];
                } else {
                    text=text+chars[charsnum];                    
                }
                //text=text+"\n";
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
		this.bitMapFont = BitMapFont.getInstance("/fonts/font.bmf");
		this.messageViewer = this.bitMapFont.getViewer(text);
                this.width=getWidth();
                this.height=this.messageViewer.getHeight()+4;
		int availableWidth = width - 6;
		int padding = 2;
		int orientation = Graphics.LEFT;
		this.messageViewer.layout( availableWidth, availableWidth, padding, orientation );
                
                startTimer();
    }
    
    public void startTimer() {
        stopTimer();
        timer = new Timer();
        timer.schedule(new RemindTask(), 1500);
        openedChar=true;
    }
    
    public void stopTimer() {
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
        openedChar=false;
    }


    class RemindTask extends TimerTask {
        public void run() {
            stopTimer();
        }
   }
}