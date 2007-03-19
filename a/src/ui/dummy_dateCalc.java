/*
 * dateCalc.java
 *
 * Created on 24.01.2007, 11:03
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
 *
 */

package ui;

/**
 *
 * @author User
 */
import javax.microedition.lcdui.DateField;
import java.util.Calendar;
import java.util.Date;

class dateCalc {
 private Calendar now;
 public final int[] monthDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}; 
 public int day,mon,year,hour,min,sec;
 private String s;
 private Date dt;

    public int kolDays (int hour1, int day1,int month1,int year1, int hour2, int day2,int month2,int year2) {
        int i, k=0, z=1;
        
        Calendar c=newCal(0,0, hour1, day1, month1, year1);

        Calendar n=newCal(0,0, hour2, day2, month2, year2);
    
        if (compareDates (c,n)>0) {
            i=hour1; hour1=hour2; hour2=i;
            i=day1; day1=day2; day2=i;
            i=month1; month1=month2; month2=i;
            i=year1; year1=year2; year2=i;
            z=-1;
        }
        
        if (year1==year2) {
            k=kolDays0 (hour1,day1,month1,hour2,day2,month2,year1);
        } else {
            k=kolDays0 (hour1,day1,month1,hour2,31,11,year1)+1;
            for (i=year1+1; i<year2; i++) {
                k+=365;
                if (leapYear (i)==true) k++;
            }
            leapYear (year2);
            k+=(kolDays0 (hour1,1,0,hour2,day2,month2,year2));
        }
        return z*k;
    }

public String kolTimes (Calendar c,  Calendar n) {
    String s="";
    int y=0,m=0,d0=0,w=0, h=0, mi=0, se=0, i;
    
    int sec1=c.get(Calendar.SECOND);
    int min1=c.get(Calendar.MINUTE);
    int hour1=c.get(Calendar.HOUR_OF_DAY);
    int day1=c.get(Calendar.DAY_OF_MONTH);
    int month1=c.get(Calendar.MONTH)+1;
    int year1=c.get(Calendar.YEAR);

    int sec2=n.get(Calendar.SECOND);
    int min2=n.get(Calendar.MINUTE);
    int hour2=n.get(Calendar.HOUR_OF_DAY);
    int day2=n.get(Calendar.DAY_OF_MONTH);
    int month2=n.get(Calendar.MONTH)+1;
    int year2=n.get(Calendar.YEAR);
        
    
    boolean out=false;
    if (compareDates(c,n)>0) {
        i=sec1; sec1=sec2; sec2=i;
        i=min1; min1=min2; min2=i;
        i=hour1; hour1=hour2; hour2=i;
        i=day1; day1=day2; day2=i;
        i=month1; month1=month2; month2=i;
        i=year1; year1=year2; year2=i;
    }
    
    if (hour1>hour2) {
        h=hour1-hour2;
    } else {
        h=hour2-hour1;
    }
    if (min1>min2) {
        mi=min1-min2;
    } else {
        mi=min2-min1;
    }
    if (sec1>sec2) {
        se=sec1-sec2;
    } else {
        se=sec2-sec1;
    }
    
    

    int d=kolDays (hour1, day1,month1,year1,hour2,day2,month2,year2);
    do {
        if (out==true) {
            if (y>0) {
                s+= y + " " + goodWordForm (y,6);  //год
            }
            if (m>0) {
                if (y>0) s+=", ";
                s+= m + " " + goodWordForm (m,5); //месяц
            }
            if (d0>0) {
                if ((y>0) || (m>0)) s+=", ";
                s+= d0 + " " + goodWordForm (d0,4); //день
            }
            if (h>0) {
                if ((y>0) || (m>0) || (d0>0)) s+=", ";
                s+= h + " " + goodWordForm (h, 2); //час
            }
            if (mi>0) {
                if ((y>0) || (m>0) || (d0>0) || (h>0)) s+=", ";
                s+= mi + " " + goodWordForm (mi, 1); //минута
            }
            if (se>0) {
                if ((y>0) || (m>0) || (d0>0) || (h>0) || (mi>0))  s+=", ";
                s+= se + " " + goodWordForm (se, 0); //секунд
            }
            if (s.length()<1) s=":-)";
                return s;
            }
            if (year1<year2-1) {
                year1++; y++; 
            } else if (year1==year2-1) {
                m = 12 - month1 + month2;
                if (day1>day2) { 
                    m--;
                    month1=month2-1;
                    if (month1<0) { month1=11; year1=year2-1; }
                    else year1=year2;
                    d0=kolDays (hour1,day1,month1,year1,hour2,day2,month2,year2);
                } else d0=day2-day1;
                if (m>11) {
                    y++;
                    m-=12;
                }
                out=true;
            }
            else if (year1==year2) {
                m=month2-month1;
                if (day1>day2) {
                    m--;
                    leapYear (year1);
                    d0=monthDays[month1]-day1+day2;
                }
                else d0=day2-day1;
                out=true;
            }
        }
        while (true);
    }
/* ------------------------------------------------------------ */
    public String goodWordForm (int d, int field) {
        String [][] suf =   {
                                {"секунда", "секунды", "секунд"},
                                {"минута", "минуты", "минут"},
                                {"час", "часа", "часов"},
                                {"неделя", "недели", "недель"},
                                {"день", "дня", "дней"},
                                {"месяц", "месяца", "месяцев"},
                                {"год", "года", "лет"}
                            };
        int index;
        if ((d%100>10) && (d%100<20) || (d%10==0) || (d%10>4)) {
            index=2;
        } else if ((d%10>1) && (d%10<5)) {
            index=1;
        } else
            index=0;
        return suf[field][index];
    }

    public boolean leapYear (int year) {
        if ( (year%4==0) && (year%100!=0) || (year%400==0) ) {
            monthDays[1]= 29;
            return true;
        } else {
            monthDays[1]=28;
            return false;
        }
    }

    public int kolDays0 (int h1,int d1,int m1, int h2,int d2,int m2, int y) {
        int i,s;
  
        leapYear (y);
  
        if (m1==m2) {
            s=d2-d1;
        } else {
            s=monthDays[m1]-d1+1;
            for (i=m1+1; i<m2; i++)
                s+=monthDays[i];
            s+=(d2-1);
        }
        return s;
    }

    public int compareDates (Calendar c, Calendar n) {
        if (c.getTime().getTime()>n.getTime().getTime()){
            System.out.println(">");
            return 1;
        } else {
            System.out.println("<");
            return -1;
        }
    }

    private Calendar newCal(int sec, int min, int hour, int day, int month, int year) {
        Calendar c=Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY,  hour);
        c.set(Calendar.MINUTE, min);
        c.set(Calendar.SECOND, sec);
        return c;
    }
}