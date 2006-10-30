/*
 * ConferenceForm.java
 *
 * Created on 24 Июль 2005 г., 18:32
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Conference;
import Client.*;
import com.alsutton.jabber.JabberDataBlock;
import locale.SR;
import ui.*;
import javax.microedition.lcdui.*;
import ui.controls.NumberField;

/**
 *
 * @author EvgS
 */
public class ConferenceForm implements CommandListener{
    
    private Display display;
    private Displayable parentView;
    
    Command cmdJoin=new Command(SR.MS_JOIN, Command.SCREEN, 1); //locale
//#if (!SMALL)
    Command cmdBookmarks=new Command(SR.MS_BOOKMARKS, Command.SCREEN, 2); //locale
    Command cmdAdd=new Command("Add bookmark", Command.SCREEN, 3); //locale
//#endif
    Command cmdCancel=new Command (SR.MS_CANCEL, Command.BACK, 99); //locale
    
    TextField roomField;
    TextField hostField;
    TextField nickField;
    TextField passField;
    NumberField msgLimitField;
    
    StaticData sd=StaticData.getInstance();
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display) { this(display, null, null, null, null); }
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display, String room, String server, String nick, String password) {
        this.display=display;
        parentView=display.getCurrent();
        
        Form formJoin=new Form(SR.MS_JOIN_CONFERENCE); //locale

        if (room==null && server==null) {
            room=Config.getInstance().defGcRoom;
            // trying to split string like room@server
            int roomE=room.indexOf('@');
            if (roomE>0) {
                server=room.substring(roomE+1);
                room=room.substring(0, roomE);
            }
        }
        // default server
        if (server==null) server="conference."+sd.account.getServer();
        
        roomField=new TextField("Room", room, 64, TextField.URL); //locale
        formJoin.append(roomField);
        
        hostField=new TextField(SR.MS_AT_HOST, server, 64, TextField.URL); //locale
        formJoin.append(hostField);
        
        if (nick==null) nick=sd.account.getNickName();
        nickField=new TextField(SR.MS_NICKNAME, nick, 32, TextField.ANY); //locale
        formJoin.append(nickField);
        
        passField=new TextField("Password", password, 32, TextField.ANY | ConstMIDP.TEXTFIELD_SENSITIVE ); //locale
        formJoin.append(passField);
        
        msgLimitField=new NumberField(SR.MS_MSG_LIMIT, 20, 0, 20); //locale
        formJoin.append(msgLimitField);
        
        formJoin.addCommand(cmdJoin);
//#if (!SMALL)
        formJoin.addCommand(cmdBookmarks);
        formJoin.addCommand(cmdAdd);
//#endif
        
        formJoin.addCommand(cmdCancel);
        formJoin.setCommandListener(this);
        display.setCurrent(formJoin);
    }
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) { destroyView(); }
//#if (!SMALL)
        if (c==cmdBookmarks) { new Bookmarks(display, null); }
//#endif        
        if (c==cmdJoin
//#if (!SMALL)
                || c==cmdAdd
//#endif   
            ) {
            String nick=nickField.getString().trim();
            String host=hostField.getString().trim();
            String room=roomField.getString().trim();
            String pass=passField.getString();
            int msgLimit=msgLimitField.getValue();
            
            if (nick.length()==0) return;
            if (room.length()==0) return;
            if (host.length()==0) return;
            StringBuffer gchat=new StringBuffer(room.trim());
            gchat.append('@');
            gchat.append(host.trim());
            //sd.roster.mucContact(gchat.toString(), Contact.ORIGIN_GROUPCHAT);
//#if (!SMALL)
            if (c==cmdAdd) new Bookmarks(display, new BookmarkItem(gchat.toString(), nick, pass));
            else {
//#endif
                try {
                    gchat.append('/');
                    gchat.append(nick.trim());
                    join(gchat.toString(),pass, msgLimit);
                    
                    display.setCurrent(sd.roster);
                } catch (Exception e) {
                    e.printStackTrace();
                    display.setCurrent(new Alert("Exception", e.toString(), null, AlertType.ERROR), sd.roster);
                }
//#if (!SMALL)
            }
//#endif
        }
    }
    public static void join(String name, String pass, int maxStanzas) {
        StaticData sd=StaticData.getInstance();
        
        
        
        ConferenceGroup grp=sd.roster.initMuc(name, pass);
        // требуется для возможности нормального выхода
        //sd.roster.mucContact(name, Contact.ORIGIN_GC_MYSELF); 
        //sd.roster.activeRooms.addElement(jid);
 
        JabberDataBlock x=new JabberDataBlock("x", null, null);
        x.setNameSpace("http://jabber.org/protocol/muc");
        if (pass.length()!=0) {
            // adding password to presence
            x.addChild("password", pass);
        }
        
        JabberDataBlock history=x.addChild("history", null);
        history.setAttribute("maxstanzas", String.valueOf(maxStanzas));
        history.setAttribute("maxchars","32768");
        try {
            long delay= ( grp.conferenceJoinTime
                              - ( (Msg)(grp.getConference().msgs.lastElement()) ).dateGmt
                        ) /1000 ;
            history.setAttribute("seconds",String.valueOf(delay)); // todo: change to since
        } catch (Exception e) {};

        //sd.roster.groups.getGroup(name.substring(0, name.indexOf('@'))).imageExpandedIndex=ImageList.ICON_GCJOIN_INDEX;
        sd.roster.sendPresence(name, null, x);
        sd.roster.reEnumRoster();
    }
    public void destroyView(){
        if (parentView!=null) display.setCurrent(parentView);
    }
}
