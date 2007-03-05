/*
 * strconv.java
 *
 * Created on 12.01.2005, 1:25
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

/**
 *
 * @author Eugene Stahov
 */
package util;
import java.io.ByteArrayOutputStream;
import java.lang.*;

public class strconv {
    
    /** Creates a new instance of strconv */
    private strconv() {
    }
    
    public static final String convCp1251ToUnicode(final String s){
        if (s==null) return null;
        StringBuffer b=new StringBuffer(s.length());
        for (int i=0;i<s.length();i++){
            char ch=s.charAt(i);
            if (ch>0xbf) ch+=0x410-0xc0;
            if (ch==0xa8) ch=0x401;
            if (ch==0xb8) ch=0x451;
            b.append(ch);
            //setCharAt(i, ch);
        }
        return b.toString();
    }
    
    public static final String convUnicodeToCp1251(final String s){
        if (s==null) return null;
        StringBuffer b=new StringBuffer(s.length());
        for (int i=0;i<s.length();i++){
            char ch=s.charAt(i);
            if (ch==0x401) ch=0xa8; //Ё
            if (ch==0x451) ch=0xb8; //ё
            if (ch>0x409) ch+=0xc0-0x410;
            b.append(ch);
            //setCharAt(i, ch);
        }
        return b.toString();
    }
    
    public final static String toBase64( String source) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        
        int len=source.length();
        char[] out = new char[((len+2)/3)*4];
        for (int i=0, index=0; i<len; i+=3, index +=4) {
            boolean trip=false;
            boolean quad=false;
            
            int val = (0xFF & source.charAt(i))<<8;
            if ((i+1) < len) {
                val |= (0xFF & source.charAt(i+1));
                trip = true;
            }
            val <<= 8;
            if ((i+2) < len) {
                val |= (0xFF & source.charAt(i+2));
                quad = true;
            }
            out[index+3] = alphabet.charAt((quad? (val & 0x3F): 64));
            val >>= 6;
            out[index+2] = alphabet.charAt((trip? (val & 0x3F): 64));
            val >>= 6;
            out[index+1] = alphabet.charAt(val & 0x3F);
            val >>= 6;
            out[index+0] = alphabet.charAt(val & 0x3F);
        }
        return new String(out);
    }
    
    public final static String toBase64( byte source[], int len) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        
        if (len<0) len=source.length;
        char[] out = new char[((len+2)/3)*4];
        for (int i=0, index=0; i<len; i+=3, index +=4) {
            boolean trip=false;
            boolean quad=false;
            
            int val = (0xFF & source[i])<<8;
            if ((i+1) < len) {
                val |= (0xFF & source[i+1]);
                trip = true;
            }
            val <<= 8;
            if ((i+2) < len) {
                val |= (0xFF & source[i+2]);
                quad = true;
            }
            out[index+3] = alphabet.charAt((quad? (val & 0x3F): 64));
            val >>= 6;
            out[index+2] = alphabet.charAt((trip? (val & 0x3F): 64));
            val >>= 6;
            out[index+1] = alphabet.charAt(val & 0x3F);
            val >>= 6;
            out[index+0] = alphabet.charAt(val & 0x3F);
        }
        return new String(out);
    }
    
    public static StringBuffer toUTFSb(StringBuffer str) {
        int srcLen = str.length();
        StringBuffer outbuf=new StringBuffer( srcLen );
        for(int i=0; i < srcLen; i++) {
            int c = (int)str.charAt(i);
            //TODO: ескэйпить коды <0x20
            if ((c >= 1) && (c <= 0x7f)) {
                outbuf.append( (char) c);
                
            }
            if (((c >= 0x80) && (c <= 0x7ff)) || (c==0)) {
                outbuf.append((char)(0xc0 | (0x1f & (c >> 6))));
                outbuf.append((char)(0x80 | (0x3f & c)));
            }
            if ((c >= 0x800) && (c <= 0xffff)) {
                outbuf.append(((char)(0xe0 | (0x0f & (c >> 12)))));
                outbuf.append((char)(0x80 | (0x3f & (c >>  6))));
                outbuf.append(((char)(0x80 | (0x3f & c))));
            }
        }
        return outbuf;
    }
    
    public static byte[] fromBase64(String s) {
        int padding=0;
        int ibuf=1;
        ByteArrayOutputStream baos=new ByteArrayOutputStream(2048);
        for (int i=0; i<s.length(); i++) {
            int nextChar = s.charAt(i);
            //if( nextChar == -1 )
            //    throw new EndOfXMLException();
            int base64=-1;
            if (nextChar>'A'-1 && nextChar<'Z'+1) base64=nextChar-'A';
            else if (nextChar>'a'-1 && nextChar<'z'+1) base64=nextChar+26-'a';
            else if (nextChar>'0'-1 && nextChar<'9'+1) base64=nextChar+52-'0';
            else if (nextChar=='+') base64=62;
            else if (nextChar=='/') base64=63;
            else if (nextChar=='=') {base64=0; padding++;} else if (nextChar=='<') break;
            if (base64>=0) ibuf=(ibuf<<6)+base64;
            if (ibuf>=0x01000000){
                baos.write((ibuf>>16) &0xff);                   //00xx0000 0,1,2 =
                if (padding<2) baos.write((ibuf>>8) &0xff);     //0000xx00 0,1 =
                if (padding==0) baos.write(ibuf &0xff);         //000000xx 0 =
                //len+=3;
                ibuf=1;
            }
        }
        try { baos.close(); } catch (Exception e) {};
        //System.out.println(ibuf);
        //System.out.println(baos.size());
        return baos.toByteArray();
    }
    
    public static String unicodeToUTF(String src) {
        return toUTFSb(new StringBuffer(src)).toString();
    }
    
    public static String toLowerCase(String src){
        StringBuffer dst=new StringBuffer(src);
        int len=dst.length();
        for (int i=0; i<len; i++) {
            char c=dst.charAt(i);
            if (c>'A'-1 && c<'Z'+1) c+='a'-'A';         // default latin chars
            if (c>0x40f && c<0x430) c+=0x430-0x410;     // cyrillic chars
            // TODO: other schemes by request
            dst.setCharAt(i, c);
        }
        return dst.toString();
    }
    
    
    public static String ColorToString(int cRed, int cGreen, int cBlue) {
        StringBuffer color=new StringBuffer(8);
        
        color.append("0x");
        
        color.append(expandHex(cRed));

        color.append(expandHex(cGreen));
        
        color.append(expandHex(cBlue));
        
        return color.toString();
    }
    
    public static String expandHex(int eVal) {
        String rVal=Integer.toHexString(eVal);
        if (rVal.length()==1) rVal="0"+rVal;
      
        return rVal;
    }
    
    public static int getColorInt(int color, int pos) {
        StringBuffer ncolor=new StringBuffer(6);
        
        ncolor.append(Integer.toHexString(color));
        
        if (ncolor.length()==1) {
            ncolor.append("00000");
        } else if (ncolor.length()==2) {
            ncolor.append("0000");
        } else if (ncolor.length()==3) {
            ncolor.append("000");
        } else if (ncolor.length()==4) {
            ncolor.append("00");
        } else if (ncolor.length()==5) {
            ncolor.append("0");
        }
        switch (pos) {
            case 0:
                return Integer.parseInt(ncolor.toString().substring(0,2),16);
            case 1:
                return Integer.parseInt(ncolor.toString().substring(2,4),16);
            case 2:
                return Integer.parseInt(ncolor.toString().substring(4,6),16);
        }
        return -1;
    }
    
    public static String getColorString(int color) {
        StringBuffer ncolor=new StringBuffer();
        
        ncolor.append("0x");
        
        String col=Integer.toHexString(color);
        if (col.length()>1) {
            ncolor.append(col);
        } else {
            ncolor.append("0"+col);
        }
        
        switch (ncolor.length()) {
            //case 2:
            //    ncolor.append("000000");
            //    break;
            //case 3:
            //    ncolor.append("00000");
            //    break;
            case 4:
                ncolor.append("0000");
                break;
            case 5:
                ncolor.append("000");
                break;
            case 6:
                ncolor.append("00");
                break;
            case 7:
                ncolor.append("0");
                break;
        }
        return ncolor.toString();
    }
    
    public static int getColorInt(String color) { // 0x010000 -> 1
        return Integer.parseInt(color.substring(2),16);
    }
}
