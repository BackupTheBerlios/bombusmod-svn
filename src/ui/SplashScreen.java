/*
 * SplashScreen.java
 *
 * Created on 9 Февраль 2005 г., 21:31
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;

import Client.Config;
import Client.NvStorage;
import javax.microedition.lcdui.*;

/**
 *
 * @author Eugene Stahov
 */
public class SplashScreen extends Canvas implements CommandListener{
    
    private String capt;
    private int pos=-1;
    
    private int width;
    private int height;
    
    public Image img;
    
    public int keypressed=0;
    
    /*protected void sizeChanged(int w, int h){
        width=w;height=h;
    }
     */
    
    private static SplashScreen instance;
    public static SplashScreen getInstance(){
        if (instance==null) instance=new SplashScreen();
        return instance;
    }
    
    /** Creates a new instance of SplashScreen */
    private SplashScreen() {
        setFullScreenMode(Config.getInstance().fullscreen);
//#if HALLOWEEN
//#         String now=ui.Time.dispLocalTime();
//#         if (!now.startsWith("31.10")) ui.Colors.switchPalette();
//#endif
    }
    
    public void paint(Graphics g){
        if (pos==-1) return;
        width=getWidth();
        height=getHeight();
        Font f=FontCache.getSmallFont();
        
        int xp=pos*width/100;
        int xt=width/2;
        int h=f.getHeight()+1;

        int y=height-h-2;
        
        g.setColor(Colors.PGS_BGND);
        g.fillRect(0,0, width, height);
        
        g.setColor(Colors.PGS_BORDER);
        g.drawRect(0, y, width-1, h-1);
        
        g.setColor(Colors.PGS_REMAINED);
        g.fillRect(1, y+1, width-2,h-2);
        
        g.setFont(f);
        g.setColor(Colors.PGS_COMPLETE);
        g.drawString(capt,xt,y+1, Graphics.TOP|Graphics.HCENTER);
        
        g.setClip(1, y+1, xp, h-2);
        g.fillRect(1, y+1, width-2,h-2);
        
        g.setColor(Colors.PGS_REMAINED);
        g.drawString(capt,xt,y+1, Graphics.TOP|Graphics.HCENTER);
        
        g.setClip(0, 0, width, height);
        Font F=FontCache.getClockFont();
        g.setFont(F); 
        g.setColor(Colors.BLK_INK);
        g.drawString("Bombus", width/2, height/2, Graphics.TOP|Graphics.HCENTER);
         
    }
    
    public void setProgress(int progress) {
        pos=progress;
        repaint();
    }

    public void setFailed(){
        setProgress("Failed", 0);
    }
    public void setProgress(String caption, int progress){
        capt=caption;
        System.out.println(capt);
	setProgress(progress);
    }
    public int getProgress(){
        return pos;
    }

    protected void keyPressed(int keyCode) {
        keypressed=keyCode;
        //notifyAll();
    }
    
    // close splash
    private Display display;
    private Displayable parentView;
    private Command cmdExit=new Command("Hide Splash", Command.BACK, 99);
    
    public void setExit(Display display, Displayable nextDisplayable){
        this.display=display;
        parentView=nextDisplayable;
        setCommandListener(this);
        addCommand(cmdExit);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdExit) close();
    }
    
    public void close(){
        if (parentView!=null) display.setCurrent(parentView);
        parentView=null;
        repaint();
        img=null;
        instance=null; // освобождение памяти
        System.gc();
    }
}
