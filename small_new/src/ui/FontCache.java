/*
 * FontCache.java
 *
 * Created on 5.02.2006, 3:15
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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
    public static int balloonFontSize=Font.SIZE_SMALL;
    public static int clockFontSize=Font.SIZE_LARGE;

    public static Font getRosterNormalFont() {
        if (normal==null) {
            normal=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, rosterFontSize);
        }
        return normal;
    }
    
    public static Font getRosterBoldFont() {
        if (bold==null) {
            bold=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, rosterFontSize);
        }
        return bold;
    }

    public static Font getMsgFont() {
        if (msgFont==null) {
            msgFont=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, msgFontSize);
        }
        return msgFont;
    }

    public static Font getMsgFontBold() {
        if (msgFontBold==null) {
            msgFontBold=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, msgFontSize);
        }
        return msgFontBold;
    }

    public static Font getBalloonFont() {
        if (balloonFont==null) {
            balloonFont=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, balloonFontSize);
        }
        return balloonFont;
    }

    public static void resetCache() {
        normal=bold=msgFont=msgFontBold=balloonFont=null;
    }
    

    public static Font getClockFont() {
        if (clockFont==null) {
            clockFont=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, clockFontSize);
        }
        return clockFont;
    }
}
