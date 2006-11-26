/*
 * KeyBlock.java
 *
 * Created on 15 Май 2005 пїЅ., 3:08
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;

import Client.Config;
import Client.ExtendedStatus;
import Client.Roster;
import Client.StaticData;
import Client.StatusList;
import images.RosterIcons;
import javax.microedition.lcdui.*;
import java.util.*;
import midlet.Bombus;

/**
 *
 * @author Eugene Stahov
 */
public class KeyBlock extends Canvas implements Runnable{
    
    private int width;
    private int height;
    
    private Display display;
    private Displayable parentView;
    
    private Image img;
    
    private ComplexString status;
    
    private char exitKey;
    private int kHold;
    
    private TimerTaskClock tc;
    
    boolean motorola_backlight;
    boolean singleflash;
    
    private StaticData sd=StaticData.getInstance();
    
    /** Creates a new instance */
    public KeyBlock(
            Display display, 
            ComplexString status, 
            char exitKey, 
            boolean motorola_backlight,
            boolean siemens_slider) 
    {
        this.status=status;
        this.display=display;
        kHold=this.exitKey=exitKey;
        this.motorola_backlight=motorola_backlight;
        
        parentView=display.getCurrent();
        
            if (!Roster.isAway) {
                String away=(siemens_slider)?"Slider closed ("+Time.timeString(Time.localTime())+")":"Auto Status on KeyLock since "+Time.timeString(Time.localTime());
                Roster.oldStatus=sd.roster.myStatus;
                    try {
                        if (Roster.oldStatus==0 || Roster.oldStatus==1) {
                            Roster.isAway=true;
                            sd.roster.sendPresence(3, away);
                        }
                    } catch (Exception e) { e.printStackTrace(); }
            }
        
        status.setElementAt(new Integer(RosterIcons.ICON_KEYBLOCK_INDEX),6);
        repaint();
        
        singleflash=true;

        new Thread(this).start();
        
        tc=new TimerTaskClock();
        
        
//#if !(MIDP1)
        setFullScreenMode(Config.getInstance().fullscreen);
//#elif USE_SIEMENS_API
//--	com.siemens.mp.game.Light.setLightOff();
//#endif
        //System.gc();   // heap cleanup
    }
    
    public void run(){
        try {
            img=Bombus.splash;
            if (img==null) img=Image.createImage("/images/splash.png");
        } catch (Exception e) {};
        
        display.setCurrent(this);
    }
    
    public void paint(Graphics g){
        width=getWidth();
        height=getHeight();
        Font f=FontCache.getClockFont();
        
        g.setColor(Colors.BLK_BGND);
        g.fillRect(0,0, width, height);
        
        if (img!=null) g.drawImage(img, width/2, height/2, Graphics.VCENTER|Graphics.HCENTER);
        
        int h=f.getHeight()+1;

        int y=0;

        g.setColor(Colors.BLK_INK);
        g.translate(0, y);
        status.drawItem(g, 0, false);
        
        String time=Time.timeString(Time.localTime());
        int tw=f.stringWidth(time);
        
        g.translate(width/2, height);
        //if (Colors.BLK_BGND!=0x010101) {
        //    g.setColor(Colors.BLK_BGND);
        //    g.fillRect(-tw/2-5, -h, tw+10, h);
        //}

        if (Colors.BLK_INK!=0x010101) {
            g.setColor(Colors.BLK_INK);
            g.setFont(f);
            g.drawString(time, 0, 0, Graphics.BOTTOM | Graphics.HCENTER);
        }
        
        if (motorola_backlight) 
            if (singleflash) display.flashBacklight(1);
        singleflash=false;
    }
    
    public void keyPressed(int keyCode) { 
        //System.out.println("blocked press"+(char) keyCode);
        if (keyCode==-24) {
            destroyView();
            sd.roster.setLight(true);
        } 
        kHold=0; 
    }
    public void keyReleased(int keyCode) { 
        //System.out.println("blocked released"+(char) keyCode); kHold=0; 
    }
    protected void keyRepeated(int keyCode) { 
        //System.out.println("blocked repeat"+(char) keyCode);
        if (kHold==0)
        if (keyCode==exitKey) destroyView(); 
    }

    private void destroyView(){
        status.setElementAt(null,6);
//#if !(MIDP1)
        if (motorola_backlight) display.flashBacklight(Integer.MAX_VALUE);
//#endif
        if (display!=null)   display.setCurrent(parentView);
        img=null;
        tc.stop();
            if (Roster.isAway) {
                int newStatus=sd.roster.oldStatus;
                ExtendedStatus es=StatusList.getInstance().getStatus(newStatus);
                String ms=es.getMessage();
                Roster.isAway=false;
                sd.roster.sendPresence(newStatus, ms);
            }
//#if USE_SIEMENS_API
//--	com.siemens.mp.game.Light.setLightOn();
//#endif
        System.gc();
    }
    
    private class TimerTaskClock extends TimerTask {
        private Timer t;
        public TimerTaskClock(){
            t=new Timer();
            t.schedule(this, 10, 20000);
        }
        public void run() {
            repaint();
//#if USE_SIEMENS_API
//--	com.siemens.mp.game.Light.setLightOff();
//#endif
        }
        public void stop(){
            cancel();
            t.cancel();
        }
    }
}
