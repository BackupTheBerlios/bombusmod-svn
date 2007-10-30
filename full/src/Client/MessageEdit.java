/*
 * MessageEdit.java
 *
 * Created on 20.02.2005, 21:20
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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
 *
 */

package Client;
//#ifndef WMUC
import Conference.AppendNick;
//#endif
import Info.Phone;
//#ifdef ARCHIVE
//# import archive.ArchiveList;
//#endif
import javax.microedition.lcdui.*;
import locale.SR;
//#if TEMPLATES
//# import templates.AppendTemplate;
//#endif
import ui.VirtualList;
import util.ClipBoard;
//#ifdef TRANSLIT
//# import util.Translit;
//#endif
import ui.Time;

/**
 *
 * @author Eugene Stahov
 */
public class MessageEdit 
        implements CommandListener, Runnable
{
    
    private Display display;
    private Displayable parentView;

    private String body;
    private String subj;
    
    private Contact to;
    private Command cmdSuspend=new Command(SR.MS_SUSPEND, Command.BACK,90);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.SCREEN,99);
    private Command cmdSend=new Command(SR.MS_SEND, Command.OK,1);
//#ifdef SMILES
//#     private Command cmdSmile=new Command(SR.MS_ADD_SMILE, Command.SCREEN,2);
//#endif
    private Command cmdInsNick=new Command(SR.MS_NICKNAMES,Command.SCREEN,3);
    private Command cmdInsMe=new Command(SR.MS_SLASHME, Command.SCREEN, 4); ; // /me
//#ifdef TRANSLIT
//#     private Command cmdSendInTranslit=new Command(SR.MS_SEND_IN_TRANSLIT, Command.SCREEN, 5);
//#endif
    private Command cmdPaste=new Command(SR.MS_ARCHIVE, Command.SCREEN, 6);    
    private Command cmdSubj=new Command(SR.MS_SET_SUBJECT, Command.SCREEN, 7);
//#if TEMPLATES
//#     private Command cmdTemplate=new Command(SR.MS_TEMPLATE, Command.SCREEN, 97); 
//#endif  
    
    private Command cmdABC=new Command("Abc", Command.SCREEN, 15);
    private Command cmdAbc=new Command("abc", Command.SCREEN, 15);
    private Command cmdClearTitle=new Command("clear title", Command.SCREEN, 16);
    private Command cmdPasteText=new Command(SR.MS_PASTE, Command.SCREEN, 98);  

    private boolean composing=true;
    
    private Config cf=Config.getInstance();
//#ifdef TRANSLIT
//#     private boolean sendInTranslit=false;
//#endif
    private TextBox t;

    private String subject;
    
    private ClipBoard clipboard=ClipBoard.getInstance();
    
    /** Creates a new instance of MessageEdit */
    public MessageEdit(Display display, Contact to, String body) {
        this.to=to;
        this.display=display;
        parentView=display.getCurrent();

        t=new TextBox(to.toString(), "", 500, TextField.ANY);

        this.subject=to.toString();
		
        try {
            //expanding buffer as much as possible
            int maxSize=t.setMaxSize(4096); //must not trow

            if (body!=null) {
                if (body.length()>maxSize)
                    body=body.substring(0, maxSize-1);
                t.setString(body);
            }
         } catch (Exception e) {}

        
        t.addCommand(cmdSend);
        t.addCommand(cmdInsMe);
//#ifdef SMILES
//#         t.addCommand(cmdSmile);
//#endif
        if (to.origin>=Contact.ORIGIN_GROUPCHAT)
            t.addCommand(cmdInsNick);
//#ifdef TRANSLIT
//#         t.addCommand(cmdSendInTranslit);
//#endif
//#ifdef ARCHIVE
//#         t.addCommand(cmdPaste);
//#endif
        if (!clipboard.isEmpty())
            t.addCommand(cmdPasteText);
        
        t.addCommand(cmdClearTitle);
        
        setInitialCaps(cf.capsState);
        
        t.addCommand(cmdSuspend);
//#if TEMPLATES
//#         t.addCommand(cmdTemplate);
//#endif
        t.addCommand(cmdCancel);
        t.setCommandListener(this);
        
        if (to.origin==Contact.ORIGIN_GROUPCHAT)
            t.addCommand(cmdSubj);

        new Thread(this).start() ; // composing

        display.setCurrent(t);
    }
    
    public void setParentView(Displayable parentView){
        this.parentView=parentView;
    }
    
    public void commandAction(Command c, Displayable d){
        body=t.getString();
        
        int caretPos=getCaretPos();
		
        if (body.length()==0) body=null;
        
        if (c==cmdInsMe) { t.insert("/me ", 0); return; }
//#ifdef SMILES
//#         if (c==cmdSmile) { new SmilePicker(display, this, caretPos); return; }
//#endif
//#ifndef WMUC
        if (c==cmdInsNick) { new AppendNick(display, to, this, caretPos); return; }
//#endif
//#ifdef ARCHIVE
//# 	if (c==cmdPaste) { new ArchiveList(display, this, caretPos); return; }
//#endif
        
        if (c==cmdAbc) {setInitialCaps(false); return; }
        if (c==cmdABC) {setInitialCaps(true); return; }
        
        if (c==cmdClearTitle) { t.setTitle(t.getTitle()==null?subject:null); return; }
        if (c==cmdPasteText) { insertText(clipboard.getClipBoard(), getCaretPos()); return; }
                
//#if TEMPLATES
//#         if (c==cmdTemplate) { new AppendTemplate(display,  this, caretPos); return; }
//#endif
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
//#ifdef TRANSLIT
//#         if (c==cmdSendInTranslit) {
//#             sendInTranslit=true;
//#         }
//#endif
        if (c==cmdSubj) {
            if (body==null) return;
            subj=body;
            body="/me "+SR.MS_HAS_SET_TOPIC_TO+": "+subj;
            body=null; //"/me has set the topic to: "+subj;
        }
        
        // message/composing sending
        destroyView();
        new Thread(this).start();
        return; 
    }
    
    
    public void run(){
        Roster r=StaticData.getInstance().roster;
        String comp=null; // composing event off

        String id=String.valueOf((int) System.currentTimeMillis());
        
        if (body!=null)
            body=body.trim();
//#ifdef TRANSLIT
//#         if (sendInTranslit==true) {
//#             if (body!=null)
//#                body=Translit.translit(body);
//#             if (subj!=null )
//#                subj=Translit.translit(subj);
//#         }
//#endif
        if (body!=null || subj!=null ) {
            String from=StaticData.getInstance().account.toString();
            Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,body);
            msg.id=id;
            // не добавляем в групчат свои сообщения
            // не шлём composing
            if (to.origin!=Contact.ORIGIN_GROUPCHAT) {
                to.addMessage(msg);
                comp="active"; // composing event in message
            }
        } else if (to.acceptComposing) comp=(composing)? "composing":"paused";
        
        if (!Config.getInstance().eventComposing) comp=null;
        
        try {
            if (body!=null || subj!=null || comp!=null) {
                r.sendMessage(to, id, body, subj, comp);
            }
        } catch (Exception e) { }

        ((VirtualList)parentView).redraw();
        ((VirtualList)parentView).repaint();
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }


    public int getCaretPos() {
        String body=t.getString();
        
        int caretPos=t.getCaretPosition();
        // +MOTOROLA STUB
        if (Phone.PhoneManufacturer()==Phone.MOTO)
            caretPos=-1;
        
        if (caretPos<0) caretPos=body.length();
        
        return caretPos;
    }
    
    
    private void setInitialCaps(boolean state) {
        t.setConstraints(state? TextField.INITIAL_CAPS_SENTENCE: TextField.ANY);
        t.removeCommand(state? cmdABC: cmdAbc);
        t.addCommand(state? cmdAbc: cmdABC);
        Config.getInstance().capsState=state;
    }
    
    
    public void insertText(String s, int caretPos) {
        String src=t.getString();

        StringBuffer sb=new StringBuffer(s);
        
        if (caretPos>0) 
            if (src.charAt(caretPos-1)!=' ')   
                sb.insert(0, ' ');
        
        if (caretPos<src.length())
            if (src.charAt(caretPos)!=' ')
                sb.append(' ');
        
        if (caretPos==src.length()) sb.append(' ');
        
        try {
            int freeSz=t.getMaxSize()-t.size();
            if (freeSz<sb.length()) sb.delete(freeSz, sb.length());
        } catch (Exception e) {}
       
        t.insert(sb.toString(), caretPos);
        sb=null;
    }
}

