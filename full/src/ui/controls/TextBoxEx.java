/*
 * TextBoxEx.java
 *
 * Created on 12 Сентябрь 2007 г., 21:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls;

import Client.Config;
import Info.Phone;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import locale.SR;
import util.ClipBoard;

/**
 *
 * @author ad
 */
public class TextBoxEx
    extends TextBox
    implements CommandListener
    {
    
    private Command cmdABC=new Command("Abc", Command.SCREEN, 15);
    private Command cmdAbc=new Command("abc", Command.SCREEN, 15);
    private Command cmdClearTitle=new Command("clear title", Command.SCREEN, 16);
    private Command cmdPasteText=new Command(SR.MS_PASTE, Command.SCREEN, 98);  

    private ClipBoard clipboard;  // The clipboard class
    
    private Display display;
    private Displayable parentView;
    
    private Config cf=Config.getInstance();

    private String subject;
    
    /** Creates a new instance of TextBoxEx */
    public TextBoxEx(String subject, String body, int constraints, Display display) {
        super(subject, null, 500, constraints);
        
        this.display=display;
        this.subject=subject;
		
        try {
            //expanding buffer as much as possible
            int maxSize=setMaxSize(4096); //must not trow

            if (body!=null) {
                if (body.length()>maxSize)
                    body=body.substring(0, maxSize-1);
                setString(body);
            }
         } catch (Exception e) {}

        
        if (!clipboard.isEmpty())
            addCommand(cmdPasteText);
        
        addCommand(cmdClearTitle);
        
        setInitialCaps(cf.capsState);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdAbc) {setInitialCaps(false); return; }
        if (c==cmdABC) {setInitialCaps(true); return; }
        
        if (c==cmdClearTitle) {
            setTitle(getTitle()==null?subject:null); 
            return; 
        }
        if (c==cmdPasteText) { insertText(clipboard.getClipBoard(), getCaretPos()); return; }
    }

    public int getCaretPos() {
        String body=getString();
        
        int caretPos=getCaretPosition();
        // +MOTOROLA STUB
        if (Phone.PhoneManufacturer()==Phone.MOTO)
            caretPos=-1;
        
        if (caretPos<0) caretPos=body.length();
        
        return caretPos;
    }
    
    
    private void setInitialCaps(boolean state) {
        setConstraints(state? TextField.INITIAL_CAPS_SENTENCE: TextField.ANY);
        removeCommand(state? cmdABC: cmdAbc);
        addCommand(state? cmdAbc: cmdABC);
        cf.capsState=state;
    }
    
    
    public void insertText(String s, int caretPos) {
        String src=getString();

        StringBuffer sb=new StringBuffer(s);
        
        if (caretPos>0) 
            if (src.charAt(caretPos-1)!=' ')   
                sb.insert(0, ' ');
        
        if (caretPos<src.length())
            if (src.charAt(caretPos)!=' ')
                sb.append(' ');
        
        if (caretPos==src.length()) sb.append(' ');
        
        try {
            int freeSz=getMaxSize()-size();
            if (freeSz<sb.length()) sb.delete(freeSz, sb.length());
        } catch (Exception e) {}
       
        insert(sb.toString(), caretPos);
        sb=null;
    }
    

/*
 *  //memory leak :(
 *
    private class textSizeNotify extends Thread{   
        private boolean stop;
        private boolean exit;
        private textSizeNotify instance;
    
        public textSizeNotify() {
            exit=false;
            stop=true;
            start();
        }

        public void destroyTask(){
            stop=false;
        }
        
        public void startNotify(){
            if (instance==null) instance=new textSizeNotify();
            if (t==null) {
                instance.destroyTask(); return;
            }

        }

        public void run() {
            while (true) {
                if (exit) return;
                try {  sleep(300);  } catch (Exception e) {}
                
                if (stop) continue;
                
                try {
                    int freeSz=t.getMaxSize()-t.size();

                    t.setTitle("("+freeSz+") "+to.toString());
                    freeSz=0;
                } catch (Exception e) { 
                    t.setTitle(to.toString());
                }
            }
        }
    }
 */
}
