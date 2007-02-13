/*
 * FontCache.java
 *
 * Created on 5 Февраль 2006 г., 3:15
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;

import javax.microedition.lcdui.Font;

/**
 *
 * @author Evg_S
 */
public class FontCache {

    private static Font normal;
    private static Font bold;
    private static Font msgFont;
    private static Font msgFontBold;
    private static Font balloonFont;
    private static Font clockFont;
    
    public static int rosterFontSize=Font.SIZE_MEDIUM;
    public static int msgFontSize=Font.SIZE_MEDIUM;
    public final static int balloonFontSize=Font.SIZE_SMALL;
    public final static int clockFontSize=Font.SIZE_LARGE;

    public final static Font getRosterNormalFont() {
        if (normal==null) {
            normal=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, rosterFontSize);
        }
        return normal;
    }
    
    public final static Font getRosterBoldFont() {
        if (bold==null) {
            bold=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, rosterFontSize);
        }
        return bold;
    }

    public final static Font getMsgFont() {
        if (msgFont==null) {
            msgFont=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, msgFontSize);
        }
        return msgFont;
    }

    public final static Font getMsgFontBold() {
        if (msgFontBold==null) {
            msgFontBold=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, msgFontSize);
        }
        return msgFontBold;
    }

    public final static Font getBalloonFont() {
        if (balloonFont==null) {
            balloonFont=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, balloonFontSize);
        }
        return balloonFont;
    }

    public final static Font getClockFont() {
        if (clockFont==null) {
            clockFont=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, clockFontSize);
        }
        return clockFont;
    }


    public final static void resetCache() {
        normal=bold=msgFont=msgFontBold=balloonFont=null;
    }
}
