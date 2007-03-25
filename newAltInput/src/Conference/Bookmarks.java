/*
 * Bookmarks.java
 *
 * Created on 18.09.2005, 0:03
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

package Conference;
import Client.*;
import Conference.affiliation.Affiliations;
import ServiceDiscovery.ServiceDiscovery;
import images.RosterIcons;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.*;
import java.util.*;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.Iq;
import ui.MainBar;

/**
 *
 * @author EvgS
 */
public class Bookmarks 
        extends VirtualList 
        implements CommandListener
{
    
    //private Vector bookmarks;
    
    private BookmarkItem toAdd;
    
    private Command cmdCancel=new Command (SR.MS_CANCEL, Command.BACK, 99);
    private Command cmdJoin=new Command (SR.MS_SELECT, Command.OK, 1);
    private Command cmdAdvJoin=new Command ("Edit/join", Command.SCREEN, 10);
    private Command cmdNew=new Command (SR.MS_NEW_BOOKMARK, Command.SCREEN, 15);
    private Command cmdConfigure=new Command (SR.MS_CONFIG_ROOM, Command.SCREEN, 16);
    private Command cmdDisco=new Command (SR.MS_DISCO_ROOM, Command.SCREEN, 17);
    
    private Command cmdUp=new Command (SR.MS_MOVE_UP, Command.SCREEN, 18);
    private Command cmdDwn=new Command (SR.MS_MOVE_DOWN, Command.SCREEN, 19);
    private Command cmdSave=new Command (SR.MS_SAVE_LIST, Command.SCREEN, 20);

    private Command cmdRoomOwners=new Command (SR.MS_OWNERS, Command.SCREEN, 21);
    private Command cmdRoomAdmins=new Command (SR.MS_ADMINS, Command.SCREEN, 22);
    private Command cmdRoomMembers=new Command (SR.MS_MEMBERS, Command.SCREEN, 23);
    private Command cmdRoomBanned=new Command (SR.MS_BANNED, Command.SCREEN, 24);
    
    private Command cmdDel=new Command (SR.MS_DELETE, Command.SCREEN, 30);
    
    Roster roster=StaticData.getInstance().roster;

    JabberStream stream=roster.theStream;
    /** Creates a new instance of Bookmarks */
    public Bookmarks(Display display, BookmarkItem toAdd) {
        super ();
        if (getItemCount()==0 && toAdd==null) {
            new ConferenceForm(display);
            return;
        }
        setMainBarItem(new MainBar(2, null, SR.MS_BOOKMARKS));
        
        this.toAdd=toAdd;
        
        //bookmarks=roster.bookmarks;
        //if ( bookmarks==null ) loadBookmarks(); 
        //else if (toAdd!=null) addBookmark();
        if (toAdd!=null) addBookmark();
        
        addCommand(cmdCancel);
        addCommand(cmdJoin);
        addCommand(cmdAdvJoin);
        //addCommand(cmdRfsh);
	addCommand(cmdNew);
        
        addCommand(cmdUp);
        addCommand(cmdDwn);
        addCommand(cmdSave);
        
        addCommand(cmdDisco);
        addCommand(cmdConfigure);
        addCommand(cmdRoomOwners);
        addCommand(cmdRoomAdmins);
        addCommand(cmdRoomMembers);
        addCommand(cmdRoomBanned);
        addCommand(cmdDel);
        setCommandListener(this);
	attachDisplay(display);
    }
    /*
    private void processIcon(boolean processing){
        getMainBarItem().setElementAt((processing)?(Object)new Integer(RosterIcons.ICON_PROGRESS_INDEX):(Object)null, 0);
        redraw();
    }
    */ 
    protected int getItemCount() { 
        Vector bookmarks=StaticData.getInstance().roster.bookmarks;
        return (bookmarks==null)?0: bookmarks.size(); 
    }
    
    protected VirtualElement getItemRef(int index) { 
        return (VirtualElement) StaticData.getInstance().roster.bookmarks.elementAt(index); 
    }
    
    public void loadBookmarks() {
    }

    private void addBookmark() {
        if (toAdd!=null) {
            //this.bookmarks.addElement(toAdd);
            StaticData.getInstance().roster.bookmarks.addElement(toAdd);
            saveBookmarks();
        }
    }
    
    public void eventOk(){
        BookmarkItem join=(BookmarkItem)getFocusedObject();
        if (join==null) return;
        if (join.isUrl) return;
        
        StaticData sd=StaticData.getInstance();
        ConferenceGroup grp=sd.roster.initMuc(join.toString(), join.password);
        JabberDataBlock x=new JabberDataBlock("x", null, null);
        x.setNameSpace("http://jabber.org/protocol/muc");
        sd.roster.sendPresence(join.toString(), null, x);
        sd.roster.reEnumRoster();
        display.setCurrent(roster);
    }
    
    public void commandAction(Command c, Displayable d){
	if (getItemCount()==0) return;
        
        if (c==cmdCancel) exitBookmarks();
        if (c==cmdJoin) eventOk();
        if (c==cmdAdvJoin) {
            BookmarkItem join=(BookmarkItem)getFocusedObject();
            new ConferenceForm(display, join, cursor);
        }
	if (c==cmdNew) new ConferenceForm(display);
        //if (c==cmdRfsh) loadBookmarks();
        if (c==cmdDel) deleteBookmark();

        String roomJid=((BookmarkItem)getFocusedObject()).getJid();
 
        if (c==cmdDisco) new ServiceDiscovery(display, roomJid, null);
        
        if (c==cmdConfigure) new QueryConfigForm(display, roomJid);

        if (c==cmdRoomOwners) {
            new Affiliations(display, roomJid, 1);  
        }
        if (c==cmdRoomAdmins) {
            new Affiliations(display, roomJid, 2);  
        }
        if (c==cmdRoomMembers) {
            new Affiliations(display, roomJid, 3);  
        }
        if (c==cmdRoomBanned) {
            new Affiliations(display, roomJid, 4);  
        }
        
        if (c==cmdSave) {
            saveBookmarks();
            exitBookmarks();
        }
        
        if (c==cmdUp) { move(-1); keyUp(); }
        if (c==cmdDwn) { move(+1); keyDwn(); }
        redraw();
    }
    
    private void deleteBookmark(){
        BookmarkItem del=(BookmarkItem)getFocusedObject();
        if (del==null) return;
        if (del.isUrl) return;
        //bookmarks.removeElement(del);
        StaticData.getInstance().roster.bookmarks.removeElement(del);
        if (getItemCount()>=cursor) moveCursorEnd();
        saveBookmarks();
        redraw();
    }
    
    private void saveBookmarks() {
        new BookmarkQuery(BookmarkQuery.SAVE);
    }

    private void exitBookmarks(){
        display.setCurrent(roster);
    }
    
    public void move(int offset){
        try {
            int index=cursor;
            BookmarkItem p1=(BookmarkItem)getItemRef(index);
            BookmarkItem p2=(BookmarkItem)getItemRef(index+offset);
            
            StaticData.getInstance().roster.bookmarks.setElementAt(p1, index+offset);
            StaticData.getInstance().roster.bookmarks.setElementAt(p2, index);
        } catch (Exception e) {/* IndexOutOfBounds */}
    }
}