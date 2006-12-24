/*
 * InfoWindow.java
 *
 * Created on 6 Сентябрь 2005 г., 22:21
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Info;
import Client.StaticData;
import javax.microedition.lcdui.*;
import locale.SR;
import midlet.Damafon;

/**
 *
 * @author EvgS
 */
public class InfoWindow implements CommandListener{

    private Display display;
    private Displayable parentView;
    
    private Form form;

    /** Creates a new instance of InfoWindow */
    public InfoWindow(Display display) {
        this.display=display;
        parentView=display.getCurrent();
        
        form=new Form(SR.MS_ABOUT);
        form.addCommand(new Command(SR.MS_CLOSE, Command.BACK, 99));
        form.append("Damafon v"+Version.version+"\nMobile client\n");
        form.append(Version.getOs());
        form.append("\nCopyright (c) 2006, Daniel Apatin (ad),\n");
        
        StringBuffer memInfo=new StringBuffer("\n\nMemory:\n");
        memInfo.append("Free=");
        //mem.append(Runtime.getRuntime().freeMemory()>>10);
        //mem.append("\nFree=");
        System.gc();
        memInfo.append(Runtime.getRuntime().freeMemory()>>10);
        memInfo.append("\nTotal=");
        memInfo.append(Runtime.getRuntime().totalMemory()>>10);
        form.append(memInfo.toString());

        try {
           int accu=getAccuLevel();
           int net=getNetworkLevel();

           if (accu>=0) {
               form.append("\nAccum level: "+accu+"%");
           }
           if (net>=0) {
               form.append("\nNetwork level: "+net+"db");
           }
        } catch (Exception e) {}
      
        form.setCommandListener(this);
        display.setCurrent(form);
    }
    
    public void commandAction(Command c, Displayable d) {
        display.setCurrent(parentView);
    }
    
    public static int getAccuLevel() {
        String cap=System.getProperty("MPJC_CAP");
        return (cap==null)? -1: Integer.parseInt(cap);
    }
    
    public static int getNetworkLevel() {
        String rx=System.getProperty("MPJCRXLS");
        int rp=rx.indexOf(',');
        return (rp<0)? -1: Integer.parseInt(rx.substring(0,rp));
    }
}
