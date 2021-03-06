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
//#     public final static String version="$BOMBUSVERSION$(small)";
//#     //public final static String version="0.4.2.774M(small)";
//#else
    public final static String version="$BOMBUSVERSION$-Zlib(small)";
     //public final static String version="0.4.2.777M-Zlib(small)";
//#endif
    // this string will be patched by build.xml/post-preprocess
    
    public final static String url="http://bombus.jrudevels.org";

    private static String platformName;
    
    public static String getPlatformName() {
        if (platformName==null) {
            platformName=System.getProperty("microedition.platform");
            
            String device=System.getProperty("device.model");
            String firmware=System.getProperty("device.software.version");
            
            if (platformName==null) platformName="Motorola";
            
            if (platformName.startsWith("j2me")) {
                
                if (device!=null && firmware!=null)
                    platformName="Motorola"; // buggy v360
            }
            
            if (platformName.startsWith("Moto")) {
                if (device==null) device=System.getProperty("funlights.product");
                if (device!=null) platformName="Motorola-"+device;
            }
            
//#if (!MIDP1)
            if (platformName.indexOf("SIE") > -1) {
                platformName=System.getProperty("microedition.platform");
            } else if (System.getProperty("com.siemens.OSVersion")!=null) {
                platformName="SIE-"+System.getProperty("microedition.platform")+"/"+System.getProperty("com.siemens.OSVersion");
            }
//#endif
        }
        return platformName;
    }

    public static String getOs() {
        return ConstMIDP.MIDP + " Platform=" +Version.getPlatformName();
    }
    
    public static String getVersionLang() { return version+" (en)"; }
    
    public static String midletName() {
//#if HALLOWEEN
//#          String now=ui.Time.dispLocalTime();
//#          if (!now.startsWith("31.10")) return "Bombus";
//#          return "Jack'O'Lantern";
//#else
        return "Bombus";
//#endif
    }
}