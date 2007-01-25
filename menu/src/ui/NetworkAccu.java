package ui;

import javax.microedition.lcdui.Graphics;
import ui.NetAccuFont;

public class NetworkAccu {

    private static int accu;

    private static int net;
    
    public static int gprscount;
    
    public static int startGPRS=-1;
    
    public static int offGPRS=0;

    public NetworkAccu() {
        super();
    }
 
    public static void draw(Graphics g, int width) {
        try {
            accu=getAccuLevel();
            net=getNetworkLevel();
        } catch (Exception e) {}

        int y=1;
                
        int accuLevel=(accu>-1)?accu:-1;
        int netLevel=(net>-1)?net:-1;
        
        if (accuLevel>-1 && netLevel>-1) {
            String wstring=accuLevel+"+-"+netLevel;
            int x_a=width-(NetAccuFont.fontWidth*wstring.length());
            NetAccuFont.drawString(g, wstring, x_a, y);
        }
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
    
    public static int getGPRS() {
        String gprs=System.getProperty("MPJCGPRS");
        int gprscnt=Integer.parseInt(gprs);
        
        int gprscount=0;
        
        if (gprscnt>-1) {
            if (startGPRS==-1) {
                startGPRS=gprscnt;
                gprscount=0;
            } else {
                gprscount=gprscnt-startGPRS;
            }
        } 
        return gprscount;
    }    
    
}
