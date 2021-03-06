/*
 * BookmarkItem.java
 *
 * Created on 17 Сентябрь 2005 г., 23:21
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Conference;
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
    boolean autojoin;
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
            autojoin=data.getAttribute("autojoin").equals("true");
        } catch (Exception e) {}
        jid=data.getAttribute((isUrl)?"url":"jid");
        nick=data.getChildBlockText("nick");
        password=data.getChildBlockText("password");
    }
    
    public BookmarkItem(String jid, String nick, String password){
        this();
        this.name=this.jid=jid;
        this.nick=nick;
        this.password=password;
    }
    
    public JabberDataBlock constructBlock() {
        JabberDataBlock data=new JabberDataBlock((isUrl)?"url":"conference", null, null);
        data.setAttribute("name", name);
        data.setAttribute((isUrl)?"url":"jid", jid);
        if (autojoin) data.setAttribute("autojoin", "true");
        if (nick.length()>0) data.addChild("nick",nick);
        if (password.length()>0) data.addChild("password",password);
        
        return data;
    }
}
