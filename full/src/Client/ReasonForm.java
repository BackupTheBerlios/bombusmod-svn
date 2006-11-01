package Client;

import java.util.Hashtable;
import javax.microedition.lcdui.*;
import locale.SR;

public class ReasonForm implements CommandListener
{
    
    private Display display;
    private Displayable parentView;
    Form form;
    
    private String nick;
    private String type;
    private String role;
    private String jid;
    
    private Command cmdOk=new Command("OK", Command.OK, 1);
    private Command cmdCancel = new Command(SR.MS_BACK, Command.BACK, 99);

    private TextBox t;
    
    private int charsCount=500;

    public ReasonForm(Display display, String nick, String type, String role, String jid) {        
        this.nick=nick;
        this.type=type;
        this.role=role;
        this.jid=jid;
        
        this.display=display;
        parentView=display.getCurrent();
    try {
        t=new TextBox("Reason",null,2048, TextField.ANY);
        charsCount=2048;
    } catch (Exception j) {        
        try {
            t=new TextBox("Reason",null,1024, TextField.ANY);
            charsCount=1024;
        } catch (Exception a) {
            try {
                t=new TextBox("Reason",null,960, TextField.ANY);
                charsCount=960;
            } catch (Exception b) {
                try {
                    t=new TextBox("Reason",null,896, TextField.ANY);
                    charsCount=896;
                } catch (Exception c) {
                    try {
                        t=new TextBox("Reason",null,832, TextField.ANY);
                        charsCount=832;
                    } catch (Exception d) {
                        try {
                            t=new TextBox("Reason",null,768, TextField.ANY);
                            charsCount=768;
                        } catch (Exception e) {
                            try {
                                t=new TextBox("Reason",null,704, TextField.ANY);
                                charsCount=704;
                            } catch (Exception f) {
                                try {
                                    t=new TextBox("Reason",null,640, TextField.ANY);
                                    charsCount=640;
                                } catch (Exception g) {
                                    try {
                                        t=new TextBox("Reason",null,576, TextField.ANY);
                                        charsCount=576;
                                    } catch (Exception h) {
                                        try {
                                            t=new TextBox("Reason",null,512, TextField.ANY);
                                            charsCount=512;
                                        } catch (Exception i) {
                                            t=new TextBox("Reason",null,500, TextField.ANY);
                                            charsCount=500;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
        
        t.addCommand(cmdOk);
        t.addCommand(cmdCancel);
        t.setCommandListener(this);
        
        display.setCurrent(t);
    }
   
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) display.setCurrent(StaticData.getInstance().roster);
	if (c==cmdOk) {
            final Roster roster=StaticData.getInstance().roster;
	    String reason = t.getString();
            Hashtable attrs=new Hashtable();
            attrs.put(type, role);
            attrs.put("nick", nick);
            roster.setMucMod(jid, attrs, reason);
            display.setCurrent(StaticData.getInstance().roster);
	}
    }
}