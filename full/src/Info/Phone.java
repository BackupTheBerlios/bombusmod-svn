/*
 * Phone.java
 *
 * Created on 22.02.2007, 13:18
 *
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

package Info;

public class Phone {
    public final static int NOT_DETECTED=0;
    public final static int NONE=-1;
    public final static int SONYE=1;
    public final static int NOKIA=2;
    public final static int SIEMENS=3;
    public final static int SIEMENS2=4;
    public final static int MOTO=5;
    public final static int MOTOEZX=6;
    public final static int WINDOWS=7;
    public final static int INTENT=8;
    public final static int J2ME=9;
    public final static int NOKIA_9XXX=10;
    public final static int SONYE_M600=11;
    public final static int WTK=50;
    
    protected final static int OTHER=99;
    
    protected static int phoneManufacturer;
    
    private static String platformName;

    private static Phone instance;
    
    public static Phone getInstance(){
	if (instance==null) {
	    instance=new Phone();
	}
	return instance;
    }
    
    /** Creates a new instance of Phone */
    private Phone() {
        if (phoneManufacturer==NOT_DETECTED) {
            String platform=getPlatformName();
            phoneManufacturer=NONE;

            if (platform.endsWith("(NSG)")) {
                phoneManufacturer=SIEMENS;
                return;
            } else if (platform.startsWith("SIE")) {
                phoneManufacturer=SIEMENS2;
                return;
            } else if (platform.startsWith("Motorola-EZX")) {
                phoneManufacturer=MOTOEZX;
                return;
            } else if (platform.startsWith("Moto")) {
                    phoneManufacturer=MOTO;
                return;
            } else if (platform.startsWith("SonyE")) {
                if (platform.startsWith("SonyEricssonM600")) {
                    phoneManufacturer=SONYE_M600;
                    return;
                }
                phoneManufacturer=SONYE;
                return;
            } else if (platform.startsWith("Windows")) {
                phoneManufacturer=WINDOWS;
                return;
            } else if (platform.startsWith("Nokia9500") || 
                    platform.startsWith("Nokia9300") || 
                    platform.startsWith("Nokia9300i")) {
                phoneManufacturer=NOKIA_9XXX;
                return;
            } else if (platform.startsWith("Nokia")) {
                phoneManufacturer=NOKIA;
                return;
            } else if (platform.startsWith("Intent")) {
                phoneManufacturer=INTENT;
                return;
            } else if (platform.startsWith("wtk-emulator")) {
                phoneManufacturer=WTK;
                return;
            } else if (platform.startsWith("j2me")) {
                phoneManufacturer=J2ME;
                return;
            } else {
                phoneManufacturer=OTHER;
            }
        }
    }
   
    public static String getPlatformName() {
        if (platformName==null) {
            platformName=System.getProperty("microedition.platform");
            
            String device=System.getProperty("device.model");
            String firmware=System.getProperty("device.software.version");
            
            if (platformName==null) platformName="Motorola";
            
            if (platformName.startsWith("j2me")) {
                if (device.startsWith("wtk-emulator")) {
                    platformName=device;
                    return platformName;
                }
                if (device!=null && firmware!=null)
                    platformName="Motorola"; // buggy v360

		else {
		    // Motorola EZX phones
		    String hostname=System.getProperty("microedition.hostname");
		    if (hostname!=null) {
		        platformName="Motorola-EZX";
		        if (device!=null) {
		    	    // Motorola EZX ROKR
			    hostname=device;
                        }
                     
                        if (hostname.indexOf("(none)")<0)
                         platformName+="/"+hostname;
						 return platformName;
                    }
		}
             }
 	    //else 
		if (platformName.startsWith("Moto")) {
                if (device==null) device=System.getProperty("funlights.product");
                if (device!=null) platformName="Motorola-"+device;
            }

            if (platformName.indexOf("SIE") > -1) {
                platformName=System.getProperty("microedition.platform")+" (NSG)";
            } else if (System.getProperty("com.siemens.OSVersion")!=null) {
                platformName="SIE-"+System.getProperty("microedition.platform")+"/"+System.getProperty("com.siemens.OSVersion");
            }
        }
        return platformName;
    }

    public static String getOs() {
        return "MIDP2 Platform=" +getPlatformName();
    }
    
    public static int PhoneManufacturer() {
        return phoneManufacturer;
    }
}
