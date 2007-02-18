/*
 * Version.java
 *
 * Created on 23 Апрель 2005 г., 22:44
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Info;

import locale.SR;
import ui.ConstMIDP;

/**
 *
 * @author Evg_S
 */
public class Version {
//#if (!ZLIB)
//#     public final static String version="$BOMBUSVERSION$";
//#else
    public final static String version="$BOMBUSVERSION$-Z";
//#endif
    // this string will be patched by build.xml/post-preprocess
    
    public final static String url="http://www.apatin.net.ru";
	
    private static String platformName;
    
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
		}
            }
	    else if (platformName.startsWith("Moto")) {
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
        return ConstMIDP.MIDP + " Platform=" +Version.getPlatformName();
    }
    
    public static String getVersionLang() { return version+" ("+SR.MS_IFACELANG+")"; }

}
