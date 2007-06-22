/*
 * AutoStatusTask.java
 *
 * Created on 12 Èþíü 2007 ã., 3:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Client;

public class AutoStatusTask implements Runnable {    
    private boolean stop;
    private long timeAwayEvent;
    private long timeXaEvent;

    private Config cf=Config.getInstance();
    
    public AutoStatusTask() {
        new Thread(this).start();
    }
    
    public void setTimeEvent(long delay){
        timeAwayEvent=(delay==0)? 0:delay+System.currentTimeMillis();
        timeXaEvent=(delay==0)? 0:(delay*3)+System.currentTimeMillis();
    }

    boolean isAwayTimerSet() { return (timeAwayEvent!=0); }
/*
    boolean isXaTimerSet() { return (timeXaEvent!=0); }
*/
    public void destroyTask(){
        stop=false;
    }

    public void run() {
        while (!stop) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                stop=true;
            }
       
            if (timeAwayEvent==0 && timeXaEvent==0) continue;
            //if (timeXaEvent==0) continue;
            
            long timeAwayRemained=System.currentTimeMillis()-timeAwayEvent;
            long timeXaRemained=System.currentTimeMillis()-timeXaEvent;

            //if (timeAwayRemained<0) System.out.println("AWAY: "+timeAwayRemained);
            
            if ((timeAwayRemained>0 && timeAwayEvent!=0) && timeXaRemained<0) {
                timeAwayEvent=0;
                //System.out.println("away"); //away
                StaticData.getInstance().roster.setAutoAway(); //away
            }
            
            //if (timeAwayRemained>0 && timeXaRemained<0) System.out.println("XA: "+timeXaRemained);
            
            if (timeAwayRemained>0 && timeXaRemained>0) {
                timeXaEvent=0;
                //System.out.println("xa"); //xa
                StaticData.getInstance().roster.setAutoXa(); //xa
            }

            if (timeAwayRemained<0) continue;
            if (timeXaRemained<0) continue;

            
        }
    }

}
