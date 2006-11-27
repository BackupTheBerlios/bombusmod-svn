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
import java.io.InputStream;

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
    private boolean toneSequence;
    private String soundName;
    private String soundType;
    
    private Display display;

    private static Player player;
    
    private final static String tone="A6E6J6";
    private int sndVolume;
    
    /** Creates a new instance of EventNotify */
    public EventNotify(
	Display display, 
	String soundMediaType, 
	String soundFileName, 
	int sndVolume,
	int vibraLength, 
	boolean enableLights
    ) {
        this.display=display;
	this.soundName=soundFileName;
	this.soundType=soundMediaType;
	this.lenVibra=vibraLength;
	this.enableLights=enableLights;
	if (soundType!=null) toneSequence= soundType.equals("tone");
	this.sndVolume=sndVolume;
    }
    
    public void startNotify (){
        release();
        if (soundName!=null)
        try {
    
            InputStream is = getClass().getResourceAsStream(soundName);
            player = Manager.createPlayer(is, soundType);

            player.addPlayerListener(this);
	    player.realize();
	    player.prefetch();
	    

	    try {
		VolumeControl vol=(VolumeControl) player.getControl("VolumeControl");
		vol.setLevel(sndVolume);
	    } catch (Exception e) { e.printStackTrace(); }

	    player.start();
        } catch (Exception e) { }

	if (enableLights) display.flashBacklight(1000);

    if (lenVibra>0)
         display.vibrate(lenVibra);
        
	if (toneSequence) new Thread(this).start();
    }
    
    public void run(){
        try {
	    if (toneSequence) {
		for (int i=0; i<tone.length(); ) {
		    int note=(tone.charAt(i++)-'A')+12*(tone.charAt(i++)-'0');
		    int duration=150;
		    Manager.playTone(note, duration, 100);
		    Thread.sleep(duration);
		}
	    }
        } catch (Exception e) { e.printStackTrace();}
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
