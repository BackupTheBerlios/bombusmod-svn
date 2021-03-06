/*
 * Time.java
 *
 * Created on 20.02.2005, 13:03
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
import java.util.*;
import locale.SR;

/**
 *
 * @author Eugene Stahov
 */
public class Time {
    
    private static Calendar c=Calendar.getInstance( TimeZone.getTimeZone("GMT") );
    private static long offset=0; 
    private static long locToGmtoffset=0;
    
    public static int GMTOffset=0;
 
    /** Creates a new instance of Time */
    private Time() { }
    
    public static void setOffset(int gmtOffset, int locOffset){
        offset=60*60*1000*gmtOffset;
        locToGmtoffset=((long)locOffset)*60*60*1000;
        GMTOffset=gmtOffset+locOffset;
    }

    public static String lz2(int i){
        if (i<10) return "0"+i; else return String.valueOf(i);
    }
    public static String timeString(long date){
        Calendar c=calDate(date);
        return lz2(c.get(Calendar.HOUR_OF_DAY))+":"+lz2(c.get(Calendar.MINUTE));
    }
    
    private static Calendar calDate(long date){
        c.setTime(new Date(date+offset));
        return c;
    }
    
    public static String dayString(long date){
        Calendar c=calDate(date);
        return lz2(c.get(Calendar.DAY_OF_MONTH))+"."+
               lz2(c.get(Calendar.MONTH)+1)+"."+
               lz2(c.get(Calendar.YEAR) % 100)+" ";
    }

    public static long localTime(){
        return System.currentTimeMillis()+locToGmtoffset;
    }
    
    public static String utcLocalTime(){
        long date=localTime();
        c.setTime(new Date(date));
        return String.valueOf(c.get(Calendar.YEAR))+
                lz2(c.get(Calendar.MONTH))+
                lz2(c.get(Calendar.DAY_OF_MONTH)+1)+
                'T'+timeString(date)+':'+lz2(c.get(Calendar.SECOND));
    }
    
    public static String dispLocalTime(){
        long date=localTime();
        //Calendar c=calDate(date);
        return dayString(date)+timeString(date);
    }
    
    private final static int[] calFields=
    {Calendar.YEAR,         Calendar.MONTH,     Calendar.DATE, 
     Calendar.HOUR_OF_DAY,  Calendar.MINUTE,    Calendar.SECOND};
     
    private final static int[] ofsFieldsA=
    { 0, 4, 6, 9, 12, 15 } ;
    
    public static long dateIso8601(String sdate){
        try {
            int l=4;    // yearlen
            for (int i=0; i<calFields.length; i++){
                int begIndex=ofsFieldsA[i];
                int field=Integer.parseInt(sdate.substring(begIndex, begIndex+l));
                if (i==1) field--;
                l=2;
                c.set(calFields[i], field);
            }
        } catch (Exception e) {    }
        return c.getTime().getTime(); 
    }
    
    public static String secDiffToDate(int seconds){
        String result ="";
        int d = 0,h = 0,m = 0,s = 0;
        if (seconds>86400){
            d=(seconds/86400);
            seconds=seconds-(d*86400);
        }
        if (seconds>3600){
            h=(seconds/3600);
            seconds=seconds-(h*3600);
        }
        if (seconds>60){
            m=(seconds/60);
            seconds=seconds-(m*60);
        }
        s=seconds;
        
        if (d>0) {
            result+= d + " " + goodWordForm (d,3);
        }
        if (h>0) {
            if (d>0) result+=", ";
            result+= h + " " + goodWordForm (h, 2);
        }
        if (m>0) {
            if ((d>0) || (h>0)) result+=", ";
            result+= m + " " + goodWordForm (m, 1);
        }
        if (s>0) {
            if ((d>0) || (h>0) || (m>0))  result+=", ";
            result+= s + " " + goodWordForm (s, 0);
        }
        return result;
    }
    
    public static String goodWordForm (int d, int field) {
        String [][] suf =  {
            {SR.MS_SEC1, SR.MS_SEC2, SR.MS_SEC3},
            {SR.MS_MIN1, SR.MS_MIN2, SR.MS_MIN3},
            {SR.MS_HOUR1, SR.MS_HOUR2, SR.MS_HOUR3},
            {SR.MS_DAY1, SR.MS_DAY2, SR.MS_DAY3},
        };
        int index;
        if ((d%100>10) && (d%100<20) || (d%10==0) || (d%10>4))
            index=2;
        else if ((d%10>1) && (d%10<5)) 
            index=1;
        else 
            index=0;
        return suf[field][index];
    }
}

