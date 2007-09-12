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
import Conference.AppendNick;
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
import ui.controls.TextBoxEx;
import util.ClipBoard;
import util.Translit;
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
    private Command cmdSend=new Command(SR.MS_SEND, Command.OK /*Command.SCREEN*/,1);
//#ifdef SMILES
//#     private Command cmdSmile=new Command(SR.MS_ADD_SMILE, Command.SCREEN,2);
//#endif
    private Command cmdInsNick=new Command(SR.MS_NICKNAMES,Command.SCREEN,3);
    private Command cmdInsMe=new Command(SR.MS_SLASHME, Command.SCREEN, 4); ; // /me
    private Command cmdSendInTranslit=new Command(SR.MS_SEND_IN_TRANSLIT, Command.SCREEN, 5);
    private Command cmdPaste=new Command(SR.MS_ARCHIVE, Command.SCREEN, 6);    
    private Command cmdSubj=new Command(SR.MS_SET_SUBJECT, Command.SCREEN, 7);
//#if TEMPLATES
//#     private Command cmdTemplate=new Command(SR.MS_TEMPLATE, Command.SCREEN, 97); 
//#endif  

    private boolean composing=true;
    
    private Config cf=Config.getInstance();

    private boolean sendInTranslit=false;

    private TextBoxEx t;
    
    /** Creates a new instance of MessageEdit */
    public MessageEdit(Display display, Contact to, String body) {
        this.to=to;
        this.display=display;
        parentView=display.getCurrent();

        t=new TextBoxEx(to.toString(), "", TextField.ANY, display);

        t.addCommand(cmdSend);
        t.addCommand(cmdInsMe);
//#ifdef SMILES
//#         t.addCommand(cmdSmile);
//#endif
        if (to.origin>=Contact.ORIGIN_GROUPCHAT)
            t.addCommand(cmdInsNick);
        
        t.addCommand(cmdSendInTranslit);
//#ifdef ARCHIVE
//#         t.addCommand(cmdPaste);
//#endif
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
		
        
        int caretPos=t.getCaretPos();
		
        if (body.length()==0) body=null;
        
        if (c==cmdInsMe) { t.insert("/me ", 0); return; }
//#ifdef SMILES
//#         if (c==cmdSmile) { new SmilePicker(display, this, caretPos); return; }
//#endif
        if (c==cmdInsNick) { new AppendNick(display, to, this, caretPos); return; }
//#ifdef ARCHIVE
//# 	if (c==cmdPaste) { new ArchiveList(display, this, caretPos); return; }
//#endif

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
        
        if (c==cmdSendInTranslit) {
            sendInTranslit=true;
        }
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
        int comp=0; // composing event off

        String id=String.valueOf((int) System.currentTimeMillis());
        
        if (sendInTranslit==true) {
            if (body!=null)
               body=Translit.translit(body).trim();
            if (subj!=null )
               subj=Translit.translit(subj).trim();
        }

        if (body!=null || subj!=null ) {
            String from=StaticData.getInstance().account.toString();
            Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,body);
            msg.id=id;
            // не добавляем в групчат свои сообщения
            // не шлём composing
            if (to.origin!=Contact.ORIGIN_GROUPCHAT) {
                to.addMessage(msg);
                comp=1; // composing event in message
            }
        } else if (to.acceptComposing) {
            comp=(composing)? 1:2;
        }
        if (!cf.eventComposing) comp=0;
        
        try {
            if (body!=null || subj!=null || comp>0) {
                r.sendMessage(to, id, body, subj, comp);
            }
        } catch (Exception e) { }

        ((VirtualList)parentView).redraw();
        ((VirtualList)parentView).repaint();
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }

    public void insertText(String string, int caretPos) {
        t.insertText(string, caretPos);
    }
}

