/*
 * EventNotify.java
 *
 * Created on 3 Март 2005 г., 23:37
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;
import javax.microedition.lcdui.*;

import javax.microedition.media.*;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

/**
 *
 * @author Eugene Stahov
 */
public class EventNotify 
        implements Runnable
	,PlayerListener
{
    
    private int lenVibra;
    private boolean enableLights;
   
    private Display display;

    private static Player player;
    
    private final static String tone="A6E6J6";

    private boolean playSnd;
    
    /** Creates a new instance of EventNotify */
    public EventNotify(
	Display display,
        boolean playSnd,
	int vibraLength, 
	boolean enableLights
    ) {
        this.display=display;
	this.playSnd=playSnd;
	this.lenVibra=vibraLength;
	this.enableLights=enableLights;
    }
    
    public void startNotify (){
    release();
    if (enableLights) display.flashBacklight(1000);

    if (lenVibra>0)
         display.vibrate(lenVibra);
         new Thread(this).start();
    }
    
    public void run(){
        if (playSnd) {
            try {
		for (int i=0; i<tone.length(); ) {
		    int note=(tone.charAt(i++)-'A')+12*(tone.charAt(i++)-'0');
		    int duration=150;
		    Manager.playTone(note, duration, 100);
		    Thread.sleep(duration);
		}
            } catch (Exception e) { e.printStackTrace();}
        }
    }
    
    public synchronized void release(){
        if (player!=null) {
	    player.removePlayerListener(this);
	    player.close();
	}
        player=null;
    }
    
    public void playerUpdate(Player player, String string, Object object) {
	if (string.equals(PlayerListener.END_OF_MEDIA)) {    release(); }
    }
}
