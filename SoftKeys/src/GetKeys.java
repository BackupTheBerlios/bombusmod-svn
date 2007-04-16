import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class GetKeys extends MIDlet {
    
    public GetKeys() { }

    protected void startApp() throws MIDletStateChangeException
    {
        display = Display.getDisplay(this);
        GetKeysCanvas gkc = new GetKeysCanvas();
        display.setCurrent(gkc);
    }

    protected void pauseApp() { }

    protected void destroyApp(boolean flag) throws MIDletStateChangeException { }

    public static Display display;
    
}
