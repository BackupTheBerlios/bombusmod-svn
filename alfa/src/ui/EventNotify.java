/*
 * EventNotify.java
 *
 * Created on 3 РњР°СЂС‚ 2005 Рі., 23:37
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;
import javax.microedition.lcdui.*;
import java.io.InputStream;

//#if !(MIDP1)
import javax.microedition.media.*;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;
//#endif

//#if USE_SIEMENS_API
//--import com.siemens.mp.game.*;
//--import com.siemens.mp.media.*;
//--import com.siemens.mp.media.control.VolumeControl;
//--import com.siemens.mp.m55.*;
//#endif

/**
 *
 * @author Eugene Stahov
 */
public class EventNotify 
        implements Runnable
//#if USE_SIEMENS_API || !(MIDP1)
	,PlayerListener
//#endif
{
    
    private int lenVibra;
    private boolean enableLights;
   
    private Display display;
    
    private final static String tone="A6E6J6";

    private boolean playSnd;

//#if USE_SIEMENS_API || !(MIDP1)
    private static Player player;
//#endif
    
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
//#if USE_SIEMENS_API || !(MIDP1)
    if (enableLights) display.flashBacklight(1000);

    if (lenVibra>0)
         display.vibrate(lenVibra);
//#endif
         new Thread(this).start();
    }
    
    public void run(){
//#if USE_SIEMENS_API || !(MIDP1)
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
//#endif
    }

    public synchronized void release(){
//#if USE_SIEMENS_API || !(MIDP1)
        if (player!=null) {
	    player.removePlayerListener(this);
	    player.close();
	}
        player=null;
//#endif
    }

    
//#if USE_SIEMENS_API || !(MIDP1)
    public void playerUpdate(Player player, String string, Object object) {
	if (string.equals(PlayerListener.END_OF_MEDIA)) {    release(); }
    }
//#endif

//#if USE_LED_PATTERN
//--    public static void leds(int pattern, boolean state){
//--        if (state) Ledcontrol.playPattern(pattern);
//--        else       Ledcontrol.stopPattern();
//--    }
//#endif

}
