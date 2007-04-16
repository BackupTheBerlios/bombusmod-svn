import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class GetKeysCanvas extends Canvas
    implements Runnable
{

    public GetKeysCanvas()
    {
        work = true;
        setFullScreenMode(true);
        Thread thread = new Thread(this);
        thread.start();
    }

    protected void paint(Graphics graphics)
    {
        graphics.setColor(0xffffff);
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.setColor(0);
        int strwidth=str.length();
        
        Font bottomFont=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);
        int h=bottomFont.getHeight();
        
        graphics.drawString(str, getWidth() / 2, getHeight() / 2 - h, Graphics.TOP|Graphics.HCENTER);
    }

    public void run()
    {
        getKeys();
        while(work) 
        {
            repaint();
            try
            {
                Thread.sleep(30L);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void getKeys()
    {
        SOFT_LEFT = -1000;
        SOFT_RIGHT = -1000;

        try {
             Class.forName("com.siemens.mp.lcdui.Image");                     // Set Siemens specific keycodes
             SOFT_LEFT=-1;
             SOFT_RIGHT=-4;
        } catch (ClassNotFoundException ignore) {      
            boolean found;
             for (int i=-127;i<127;i++) {                                 // run thru all the keys
                try {
                   if (getKeyName(i).toUpperCase().indexOf("SOFT")>=0) {         // Check for "SOFT" in name description
                      if (getKeyName(i).indexOf("1")>=0) SOFT_LEFT=i;         // check for the 1st softkey
                      if (getKeyName(i).indexOf("2")>=0) SOFT_RIGHT=i;         // check for 2nd softkey
                   }
                }catch(Exception e){                                     
                   if (System.getProperty("microedition.platform").indexOf("Sony")>=0) { // Sony calls exception on some keys
                      SOFT_LEFT=-6;                                    // including softkeys
                      SOFT_RIGHT=-7;                                 // bugfix is to set the values ourself
                   }
                }
             }
        }
    }

    public void keyPressed(int Key)
    {
        if(Key == SOFT_LEFT)
            str = Key +" \u041B\u0435\u0432\u0430\u044F";
        else if(Key == SOFT_RIGHT)
            str = Key + " \u041F\u0440\u0430\u0432\u0430\u044F";
    }

    private static boolean work;

    private String str="";
    private static int SOFT_LEFT = 0;
    private static int SOFT_RIGHT = 0;
}
