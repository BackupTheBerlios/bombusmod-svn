/*
 * NetworkAccu.java
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

package ui;

import Client.StaticData;
import javax.microedition.lcdui.Graphics;

public class BottomInfo {
    public static int startGPRS=-1;
    private static boolean patched=true;
 
    public static String get() {
        StringBuffer s=new StringBuffer();

        String traff = null;
        int ngprs=-1;
        int gprscount=0;
        try {
            ngprs=getGPRS();
            if (ngprs>-1) {
                gprscount=ngprs;
            } else {
                StaticData sd=StaticData.getInstance();
                int in=sd.roster.theStream.getBytesIn();
                int out=sd.roster.theStream.getBytesOut();
                gprscount=in+out;
            }
            traff=(gprscount/1000)+"kB";
        } catch (Exception e) {
            traff="0kB";
        }
        
        s.append(Time.timeString(Time.localTime())+" "+traff);
        
        s.append(getAccuLevel()+getNetworkLevel());

        return s.toString();
     }
    
    public static String getAccuLevel() {
        if (patched==false) return "";
        try {
            String cap=System.getProperty("MPJC_CAP");
            return (cap==null)? "": " "+cap+"%";
        } catch (Exception e) { patched=false; }
        return "";
    }
    
    public static String getNetworkLevel() {
        if (patched==false) return "";
        try {
            String rx=System.getProperty("MPJCRXLS");
            int rp=rx.indexOf(',');
            return (rp<0)? "": " "+rx.substring(0,rp)+"dB";
        } catch (Exception e) { patched=false; }
        return "";
    }
    
    public static int getGPRS() {
        if (patched==false) return -1;
        try {
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
                return gprscount;
            }
        } catch (Exception e) { patched=false; }
        
        return -1;
    }    
    
}
