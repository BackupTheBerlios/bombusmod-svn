/*
 * BookmarkItem.java
 *
 * Created on 17 Сентябрь 2005 г., 23:21
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Conference;
import Client.Config;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import images.RosterIcons;
import ui.*;

/**
 *
 * @author EvgS
 */
public class BookmarkItem extends IconTextElement{
    
    String name;
    String jid;
    String nick;
    String password;
    boolean autojoin=false;
    boolean isUrl;
    
    public int getImageIndex(){ return (isUrl)? RosterIcons.ICON_PRIVACY_ACTIVE: RosterIcons.ICON_GCJOIN_INDEX; }
    public String toString(){ return jid+'/'+nick; }
    public String getJid() { return jid; }

    public int getColor(){ return Colors.LIST_INK;}
    
    /** Creates a new instance of BookmarkItem */
    public BookmarkItem() {
        super(RosterIcons.getInstance());
    }
    
    public BookmarkItem(JabberDataBlock data) {
        this();
        isUrl=!data.getTagName().equals("conference");
        name=data.getAttribute("name");
        try {
            if (data.getAttribute("autojoin").equals("true")) {
                autojoin=true;
            }
        } catch (Exception e) {}
        jid=data.getAttribute((isUrl)?"url":"jid");
        nick=data.getChildBlockText("nick");
        password=data.getChildBlockText("password");
        
        if ((autojoin==true) && (Config.getInstance().autoJoinConferences==true)) {
            System.out.println(jid+" autojoin");
            StringBuffer gchat=new StringBuffer();
            gchat.append(jid);
            gchat.append('/');
            gchat.append(nick);
            join(gchat.toString(),password,Config.getInstance().confMessageCount);
        }
    }
    
    public BookmarkItem(String jid, String nick, String password, boolean autojoin){
        this();
        this.name=this.jid=jid;
        this.nick=nick;
        this.password=password;
        this.autojoin=autojoin;
    }
    
    public JabberDataBlock constructBlock() {
        JabberDataBlock data=new JabberDataBlock((isUrl)?"url":"conference", null, null);
        data.setAttribute("name", name);
        data.setAttribute((isUrl)?"url":"jid", jid);
        data.setAttribute("autojoin", (autojoin)?"true":"false");
        if (nick.length()>0) data.addChild("nick",nick);
        if (password.length()>0) data.addChild("password",password);
        
        //System.out.println(jid+" autojoin "+autojoin);        
        
        return data;
    }
    
    public static void join(String name, String pass, int maxStanzas) {
        StaticData sd=StaticData.getInstance();
        
        ConferenceGroup grp=sd.roster.initMuc(name, pass);

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
            long last=grp.getConference().lastMessageTime;
            long delay= ( grp.conferenceJoinTime - last ) /1000 ;
            if (last!=0) history.setAttribute("seconds",String.valueOf(delay)); // todo: change to since
        } catch (Exception e) {};

        sd.roster.sendPresence(name, null, x);
        sd.roster.reEnumRoster();
    }
}
