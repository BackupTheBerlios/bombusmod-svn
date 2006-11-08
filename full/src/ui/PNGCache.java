package ui;

import javax.microedition.lcdui.Image;

public class PNGCache {
    public static NetAccuFont netaccufont;

    public static void init() throws Exception {
        netaccufont = new NetAccuFont("/images/newfont.png");

    }
}

