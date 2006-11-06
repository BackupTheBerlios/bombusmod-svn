/*
 * InputBox.java
 *
 * Created on 2 Ноябрь 2006 г., 0:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls;

import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
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

    int baloon=-1;
    
    public String text;
    public int key;
        
    private TimerTaskRotate typer;

    private String abc;

    private int abcWidth;
    
    private Graphics g;

    public int width;

    public int height;

    private boolean clrFlag = true;
    private boolean showBalloon=false;

    public InputBox(String text, int key) {
        this.text=text;
        this.key=key;
        
        switch (key) {
            case 1: {
                text=text+"\u00A0";
                break;
            }
            case 2: {
                text=text+"\u0430";
                abc="\u0430"+"\u0431"+"\u0432"+"\u0433"+"2abc";
                break;
            }
            case 3: {
                text=text+"\u0434";
                abc="\u0434"+"\u0435"+"\u0436"+"\u0437"+"3def";
                break;
            }
            case 4: {
                text=text+"\u0438";
                abc="\u0438"+"\u0439"+"\u043A"+"\u043B"+"4ghi";
                break;
            }
            case 5: {
                text=text+"\u043C";
                abc="\u043C"+"\u043D"+"\u043E"+"5jkl";
                break;
            }
            case 6: {
                text=text+"\u043F";
                abc="\u043F"+"\u0440"+"\u0441"+"6mno";
                break;
            }
            case 7: {
                text=text+"\u0442";
                abc="\u0442"+"\u0443"+"\u0444"+"\u0445"+"7pqrs";
                break;
            }
           case 8: {
                text=text+"\u0446";
                abc="\u0446"+"\u0447"+"\u0448"+"\u0449"+"\u044A"+"8tuv";
                break;
            }
            case 9: {
                text=text+"\u044B";
                abc="\u044B"+"\u044C"+"\u044D"+"\u044E"+"\u044F"+"9wxyz";
                break;
            }
            case 0: {
                text=text+"\n";
                break;
            }
            default : {
                text="";
                abc="";
            }
        }

		this.bitMapFont = BitMapFont.getInstance("/fonts/sirclive.bmf");
		this.messageViewer = this.bitMapFont.getViewer(text);
                this.width=getWidth();
                this.height=this.messageViewer.getHeight();
		int availableWidth = width - 5;
		int padding = 2;
		int orientation = Graphics.LEFT;
		this.messageViewer.layout( availableWidth-abcWidth, availableWidth, padding, orientation );
               
                if (abc.length()>0) showBalloon=true;
                
                typer=new TimerTaskRotate(0);
    }

    public void drawItem(Graphics g) {
        paint(g);
    }

    protected void paint(Graphics g) {
        if (clrFlag) {
            System.out.println("clear");
            g.setColor(0xff0000);
            g.fillRect(0, 0,width, height);
            clrFlag=false;
        }
        if (showBalloon) {
            System.out.println("paint showBalloon");
            g.setColor(0xa0a0a0); g.fillRect(1, 1, width-1, height-1);
            g.setColor(0xffffff); g.fillRect(2, 2, width-2, height-2);
            this.messageViewer.paint( 0, 0, g );

            Font f=FontCache.getBalloonFont();
            g.setFont(f);
            
            abcWidth=f.stringWidth(abc)+2;
            
            g.setColor(0x000000);
            g.drawString(abc,width-abcWidth,0,Graphics.TOP | Graphics.LEFT);
        } else {
            System.out.println("paint !showBalloon");
            g.setColor(0xa0a0a0); g.fillRect(1, 1,width-1, height-1);
            g.setColor(0xffffff); g.fillRect(2, 2, width-2, height-2);
            this.messageViewer.paint( 0, 0, g );
        }
    }

    private class TimerTaskRotate extends TimerTask{
        private Timer t;
        private int balloon;
        
        public TimerTaskRotate(int time){
            balloon=2;
            t=new Timer();
            t.schedule(this, 2000, 1000);
        }
        public void run() {
            if (balloon==-1) {
                System.out.println("run cancel");
                redraw(false);
                cancel();
            }
            if (showBalloon=balloon>=0) balloon--;
        }
        public void destroyTask(){
            if (t!=null){
                System.out.println("destroy t!=null");
                this.cancel();
                t.cancel();
                t=null;
            }
        }
    }
    
    private void redraw(boolean sb) {
        showBalloon=sb;
        clrFlag=true;
        System.out.println("redraw !showBalloon");
        repaint();
    }
    
    public void setTyper() {
        typer.destroyTask();
        typer=new TimerTaskRotate(5);
        showBalloon=true;
    }
}