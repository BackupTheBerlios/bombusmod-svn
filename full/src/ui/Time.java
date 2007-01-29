/*
 * Time.java
 *
 * Created on 20 Февраль 2005 г., 13:03
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;
import java.util.*;

/**
 *
 * @author Eugene Stahov
 */
public class Time {
    
    private static Calendar c=Calendar.getInstance( TimeZone.getTimeZone("GMT") );
    private static long offset=0; 
    private static long locToGmtoffset=0;
    
    public static int GMTOffset=0;
    
    private static globalCalendar now;
 
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

    //private final static int[] ofsFieldsB=
    //{ 0, 4, 6, 9, 12, 15 } ;
    
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
    
    //now.kolTimes (10,11,12,now.getDay(),now.getMonth(),now.getYear(),10,17,13,eventDay,eventMonth,eventYear)
    
    public static String secDiffToDate(long seconds){
        Calendar c=calDate(localTime()-(seconds*1000));
        int s1=c.get(Calendar.SECOND);
        int mi1=c.get(Calendar.MINUTE);
        int h1=c.get(Calendar.HOUR_OF_DAY);
        int d1=c.get(Calendar.DAY_OF_MONTH);
        int m1=c.get(Calendar.MONTH)+1;
        int y1=c.get(Calendar.YEAR);
        
        Calendar n=calDate(localTime());
        int s2=n.get(Calendar.SECOND);
        int mi2=n.get(Calendar.MINUTE);
        int h2=n.get(Calendar.HOUR_OF_DAY);
        int d2=n.get(Calendar.DAY_OF_MONTH);
        int m2=n.get(Calendar.MONTH)+1;
        int y2=n.get(Calendar.YEAR);
        
        now=new globalCalendar ();
        
       
        String diff=now.kolTimes (s1,mi1,h1,d1,m1,y1, s2,mi2,h2,d2,m2,y2);
        
        //System.out.println(diff);
        return diff;
    }
    
    public static String secToDate(long seconds){
        Calendar c=calDate(System.currentTimeMillis()+locToGmtoffset-(seconds*1000));
        return lz2(c.get(Calendar.DAY_OF_MONTH))+"."+
               lz2(c.get(Calendar.MONTH)+1)+"."+
               lz2(c.get(Calendar.YEAR) % 100)+" "+lz2(c.get(Calendar.HOUR_OF_DAY))+":"+lz2(c.get(Calendar.MINUTE))+":"+lz2(c.get(Calendar.SECOND));
    }
    
    public static String secToTime(long seconds){
        Calendar c=calDate(System.currentTimeMillis()+locToGmtoffset-(seconds*1000));
        return lz2(c.get(Calendar.DAY_OF_MONTH))+"."+
               lz2(c.get(Calendar.MONTH)+1)+"."+
               lz2(c.get(Calendar.YEAR) % 100)+" "+lz2(c.get(Calendar.HOUR_OF_DAY))+":"+lz2(c.get(Calendar.MINUTE))+":"+lz2(c.get(Calendar.SECOND));
    }  
}

