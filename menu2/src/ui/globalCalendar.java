/*
 * globalCalendar.java
 *
 * Created on 24 январь 2007 г., 11:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui;

/**
 *
 * @author User
 */
import javax.microedition.lcdui.DateField;
import java.util.Calendar;
import java.util.Date;

class globalCalendar {
 private Calendar now;
 public final int[] monthDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}; 
 public int day,mon,year,hour,min,sec;
 private String s;
 private Date dt;

 public Date initDateField (int s, int mi, int h, int d, int m, int y) {
  now=Calendar.getInstance();
  now.set (Calendar.SECOND,s);
  now.set (Calendar.MINUTE,mi);
  now.set (Calendar.HOUR,h);
  now.set (Calendar.DAY_OF_MONTH,d);
  now.set (Calendar.MONTH,m);
  now.set (Calendar.YEAR,y);
  return now.getTime();
 }    
 
 public String oNum(int value) {
  if (value<10) return new String("0"+value); 
  else return new String(""+value);
 }

 public String oNum4 (int v) {
  String s="";
  if (v<-99) s=""+v;
  else if (v<-9) s="-0"+(-v);
  else if (v<0) s="-00"+(-v);
  else if (v<10) s="000"+v;
  else if (v<100) s="00"+v;
  else if (v<1000) s="0"+v;
  else s=""+v;
  return s;
 }

 public boolean leapYear (int year) {
  if ( (year%4==0) && (year%100!=0) || (year%400==0) ) {
   monthDays[1]= 29; return true;
  } 
  else { monthDays[1]=28; return false; }
 }

    public int compareDates (int sec1, int min1, int hour1, int day1,int month1,int year1, int sec2, int min2, int hour2, int day2,int month2,int year2) {
        if (year1<year2) {
            return -1;
        } else if (year1>year2) {
            return 1;
        } else {
            if (month1<month2) {
                return -1;
            } else if (month1>month2) {
                return 1;
            } else {
                if (day1<day2) {
                    return -1;
                } else if (day1>day2) {
                    return 1;
                } else {
                    if (hour1<hour2) {
                        return -1;
                    } else if (hour1>hour2) {
                        return 1;
                    } else {
                        if (min1<min2) {
                            return -1;
                        } else if (min1>min2) {
                            return 1;
                        } else {
                            if (sec1<sec2) {
                                return -1;
                            } else if (sec1>sec2) {
                                return 1;
                            }
                            return 0;
                        }
                    }
                }
            }
        }
    }

 public int kolDays0 (int h1,int d1,int m1,int h2,int d2,int m2,int y) {
  int i,s;
  leapYear (y);
  if (m1==m2) {
      s=d2-d1;
  } else {
   s=monthDays[m1]-d1+1;
   for (i=m1+1; i<m2; i++) s+=monthDays[i];
   s+=(d2-1);
  }
  return s;
 }
 
 public int kolDays (int hour1, int day1,int month1,int year1, int hour2, int day2,int month2,int year2) {
  int i,k=0,z=1;
  if (compareDates (10,11,hour1,day1,month1,year1,10,11,hour2,day2,month2,year2)>0) {
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

 public int abs (int a) {
  if (a<0) return -a;
  else return a;
 }

 public String goodWordForm (int d, int field) {
  String [][] suf =  {
   {"секунда", "секунды", "секунд"},
   {"минута", "минуты", "минут"},
   {"час", "часа", "часов"},
   {"недел€", "недели", "недель"},
   {"день", "дн€", "дней"},
   {"мес€ц", "мес€ца", "мес€цев"},
   {"год", "года", "лет"}
  };
  int index;
  if ((d%100>10) && (d%100<20) || (d%10==0) || (d%10>4)) index=2;
  else if ((d%10>1) && (d%10<5)) index=1;
  else index=0;
  return suf[field][index];
 }

    public String kolTimes (int sec1, int min1, int hour1, int day1, int month1, int year1,  int sec2, int min2, int hour2, int day2, int month2, int year2) {
        String s="";
        int y=0,m=0,d0=0,w=0, h=0, mi=0, se=0, i;
        boolean out=false;
        if (compareDates (sec1,min1,hour1,day1,month1,year1,sec2,min2,hour2,day2,month2,year2)>0) {
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
                    s+= m + " " + goodWordForm (m,5); //мес€ц
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
                    }
                    else d0=day2-day1;
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

 public boolean correctDate (int day, int month, int year) {
  leapYear (year);
  if ((month<0) || (month>11)) return false;
  if ((day<1) || (day>monthDays[month]) || (year<0)) return false;
  //≈сли разрешать годы<0 то исправить oNum4 ()
  return true;
 }
}