/*
 * MessageEdit.java
 *
 * Created on 20 Февраль 2005 пїЅ., 21:20
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import Conference.AppendNick;
import archive.ArchiveList;
import javax.microedition.lcdui.*;
import locale.SR;
import templates.AppendTemplate;
import ui.VirtualList;
import util.ClipBoard;

/**
 *
 * @author Eugene Stahov
 */
public class MessageEdit 
        implements CommandListener, Runnable
{
    
    private Display display;
    private Displayable parentView;
    private TextBox t;
    private String body;
    private String subj;
    
    private ClipBoard clipboard;  // The clipboard class
    
    private Contact to;
    private Command cmdSuspend=new Command(SR.MS_SUSPEND, Command.BACK,90);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.SCREEN,99);
    private Command cmdSend=new Command(SR.MS_SEND, Command.OK /*Command.SCREEN*/,1);
    private Command cmdSmile=new Command(SR.MS_ADD_SMILE, Command.SCREEN,2);
    private Command cmdInsNick=new Command(SR.MS_NICKNAMES,Command.SCREEN,3);
    private Command cmdInsMe=new Command(SR.MS_SLASHME, Command.SCREEN, 4); ; // /me
    private Command cmdSubj=new Command(SR.MS_SET_SUBJECT, Command.SCREEN, 10);
    private Command cmdPaste=new Command(SR.MS_ARCHIVE, Command.SCREEN, 5);
    private Command cmdABC=new Command("Abc", Command.SCREEN, 15);
    private Command cmdAbc=new Command("abc", Command.SCREEN, 15);
    private Command cmdTemplate=new Command(SR.MS_TEMPLATE, Command.SCREEN, 97); 
    private Command cmdPasteText=new Command(SR.MS_PASTE, Command.SCREEN, 98);    

    private boolean composing=true;
    
    private Config cf=Config.getInstance();
 
    private int charsCount=-1;
    //private Command cmdSubject=new Command("Subject",Command.SCREEN,10);
    
    /** Creates a new instance of MessageEdit */
    public MessageEdit(Display display, Contact to, String body) {
        this.to=to;
        this.display=display;
        parentView=display.getCurrent();
        
        if (charsCount>0) {
            try {
                t=new TextBox(to.toString(),null,charsCount, TextField.ANY);
            } catch (Exception a) { 
                charsCount=500;
                t=new TextBox(to.toString(),null,charsCount, TextField.ANY);
            }
         } else {
            try {
                t=new TextBox(to.toString(),null,4096, TextField.ANY);
                charsCount=4096;
            } catch (Exception a) { 
                try {
                    t=new TextBox(to.toString(),null,2048, TextField.ANY);
                    charsCount=2048;
                } catch (Exception b) {  
                    try {
                        t=new TextBox(to.toString(),null,1024, TextField.ANY);
                    } catch (Exception c) {
                        try {
                            t=new TextBox(to.toString(),null,500, TextField.ANY);
                            charsCount=1024;
                        } catch (Exception d) {
                            try {
                                t=new TextBox(to.toString(),null,960, TextField.ANY);
                                charsCount=960;
                            } catch (Exception e) {
                                try {
                                    t=new TextBox(to.toString(),null,896, TextField.ANY);
                                    charsCount=896;
                                } catch (Exception f) {
                                    try {
                                        t=new TextBox(to.toString(),null,832, TextField.ANY);
                                        charsCount=832;
                                    } catch (Exception g) {
                                        try {
                                            t=new TextBox(to.toString(),null,768, TextField.ANY);
                                            charsCount=768;
                                        } catch (Exception h) {
                                            try {
                                                t=new TextBox(to.toString(),null,704, TextField.ANY);
                                                charsCount=704;
                                            } catch (Exception i) {
                                                try {
                                                    t=new TextBox(to.toString(),null,640, TextField.ANY);
                                                    charsCount=640;
                                                } catch (Exception j) {
                                                    try {
                                                        t=new TextBox(to.toString(),null,576, TextField.ANY);
                                                        charsCount=576;
                                                    } catch (Exception k) {
                                                        try {
                                                            t=new TextBox(to.toString(),null,512, TextField.ANY);
                                                            charsCount=512;
                                                        } catch (Exception l) {
                                                            t=new TextBox(to.toString(),null,500, TextField.ANY);
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
                 }
             }
         }
        
        try {
            if (body!=null) t.setString(body);
        } catch (Exception e) {
            t.setString("<large text>");
        }
        t.addCommand(cmdSend);
        t.addCommand(cmdInsMe);
        t.addCommand(cmdSmile);
        if (to.origin>=Contact.ORIGIN_GROUPCHAT)
            t.addCommand(cmdInsNick);
        t.addCommand(cmdPaste);
       
        t.addCommand(cmdSuspend);
        if (clipboard.s!=null)
            t.addCommand(cmdPasteText);
        
        t.addCommand(cmdTemplate);
        t.addCommand(cmdCancel);
        t.setCommandListener(this);
        
        if (to.origin==Contact.ORIGIN_GROUPCHAT)
            t.addCommand(cmdSubj);
        
        //t.setInitialInputMode("MIDP_LOWERCASE_LATIN");
        new Thread(this).start() ; // composing
        
        setInitialCaps(cf.capsState);
        
        display.setCurrent(t);
    }
    
    public void addText(String s) {
        //t.insert(s, t.getCaretPosition());
        if ( t.size()>0 )
        if ( !t.getString().endsWith(" ") ) append(" ");
        append(s);  // теперь вставка происходит всегда в конец строки
        append(" "); // хвостовой пробел    
    }
    
    private void append(String s) { t.insert(s, t.size()); }
    
    public void setParentView(Displayable parentView){
        this.parentView=parentView;
    }
    
    public void commandAction(Command c, Displayable d){
        body=t.getString();
        if (body.length()==0) body=null;
        
        if (c==cmdInsMe) { t.insert("/me ", 0); return; }
        if (c==cmdSmile) { new SmilePicker(display, this); return; }
        if (c==cmdInsNick) { new AppendNick(display, to); return; }
        if (c==cmdAbc) {setInitialCaps(false); return; }
        if (c==cmdABC) {setInitialCaps(true); return; }
	if (c==cmdPaste) { new ArchiveList(display, t); return; }
        if (c==cmdPasteText) { t.insert(clipboard.s, charsCount); return; } 
        if (c==cmdTemplate) { new AppendTemplate(display, t); return; }
        if (c==cmdCancel) { 
            composing=false; 
            body=null; 
        }
        if (c==cmdSuspend) { 
            composing=false; 
            to.msgSuspended=body; 
            body=null;
        }
        if (c==cmdSend && body==null) return;
        if (c==cmdSubj) {
            if (body==null) return;
            subj=body;
            body="/me "+SR.MS_HAS_SET_TOPIC_TO+": "+subj;
        }
        // message/composing sending
        destroyView();
        new Thread(this).start();
        return; 
    }
    
    
    public void run(){
        Roster r=StaticData.getInstance().roster;
        int comp=0; // composing event off
        
        if (body!=null /*|| subj!=null*/ ) {
            String from=StaticData.getInstance().account.toString();
            Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,body);
            // не добавляем в групчат свои сообщения
            // не шлём composing
            if (to.origin!=Contact.ORIGIN_GROUPCHAT) {
                to.addMessage(msg);
                comp=1; // composing event in message
            }
            
        } else if (to.acceptComposing) comp=(composing)? 1:2;
        
        if (!cf.eventComposing) comp=0;
        
        try {
            if (body!=null /*|| subj!=null*/ || comp>0)
            r.sendMessage(to, body, subj, comp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((VirtualList)parentView).redraw();
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }

    private void setInitialCaps(boolean state) {
//#if (!MIDP1)
        t.setConstraints(state? TextField.INITIAL_CAPS_SENTENCE: TextField.ANY);
        t.removeCommand(state? cmdABC: cmdAbc);
        t.addCommand(state? cmdAbc: cmdABC);
        cf.capsState=state;
//#endif
    }

}
