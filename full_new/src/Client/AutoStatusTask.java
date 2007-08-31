/*
 * AutoStatusTask.java
 *
 * Created on 12.06.2007, 3:12
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

package Client;

public class AutoStatusTask implements Runnable {    
    private boolean stop;
    private long timeAwayEvent=0;
    private long timeXaEvent=0;
    
    public AutoStatusTask() {
        new Thread(this).start();
    }
    
    public void setTimeEvent(long delay){
        timeAwayEvent=(delay==0)? 0:delay+System.currentTimeMillis();
        //    System.out.println("away set to: "+timeAwayEvent);  //пишем в вывод сколько поставили
        timeXaEvent=(delay==0)? 0:(delay*2)+timeAwayEvent;
        //    System.out.println("xa set to: "+timeXaEvent);  //пишем в вывод сколько поставили
    }

    boolean isAwayTimerSet() { 
        return (timeAwayEvent!=0);
    }

    public void destroyTask(){
        stop=false;
    }

    public void run() {
        while (!stop) {
            try {
                Thread.sleep(5000); //спим 5 секунд
            } catch (InterruptedException ex) {
                stop=true; //при ошибке завершаем таймер
            }
       
            if (timeAwayEvent==0 && timeXaEvent==0) //если оба события 0
                continue; //пропускаем
            
            long timeAwayRemained=(timeAwayEvent!=0)?System.currentTimeMillis()-timeAwayEvent:0; //если событие не 0 - вычитаем из текущего времени, время собития или ставим 0
            long timeXaRemained=(timeXaEvent!=0)?System.currentTimeMillis()-timeXaEvent:0; //если событие не 0 - вычитаем из текущего времени, время собития или ставим 0

            //if (timeAwayEvent!=0) //если событие не 0
            //    System.out.println("AWAY: "+timeAwayRemained);  //пишем в вывод сколько осталось
            
            //if (timeAwayEvent==0 && timeXaEvent!=0) //если событие не 0
            //    System.out.println("XA: "+timeXaRemained);  //пишем в вывод сколько осталось
            
            if (timeAwayRemained>0 && timeAwayEvent!=0) { //если перешли через границу и собыитие не 0
                timeAwayEvent=0; //ставим событие 0
                //System.out.println("away"); //пишем в вывод away
                StaticData.getInstance().roster.setAutoAway(); //ставим статус away
            }

            if (timeAwayEvent==0 && timeXaRemained>0) { //если перешли через границу и собыитие не 0
                timeXaEvent=0; //ставим событие 0
                //System.out.println("xa");  //пишем в вывод xa
                StaticData.getInstance().roster.setAutoXa(); //ставим статус xa
            }

            //if (timeAwayRemained<0) continue;
            //if (timeXaRemained<0) continue;
        }
    }

}
