/*
 * Bookmarks.java
 *
 * Created on 18 Сентябрь 2005 пїЅ., 0:03
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Conference;
import Client.*;
import ServiceDiscovery.ServiceDiscovery;
import images.RosterIcons;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.*;
import java.util.*;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.Iq;

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
    
    private Command cmdCancel=new Command (SR.MS_CANCEL, Command.BACK, 99); //locale
    private Command cmdJoin=new Command (SR.MS_JOIN, Command.SCREEN, 10); //locale
    private Command cmdDisco=new Command (SR.MS_DISCO_ROOM, Command.SCREEN, 15); //locale
    //private Command cmdRfsh=new Command ("Refresh", Command.SCREEN, 20); //locale
    private Command cmdDel=new Command (SR.MS_DELETE, Command.SCREEN, 30); //locale
    
    Roster roster=StaticData.getInstance().roster;

    JabberStream stream=roster.theStream;
    /** Creates a new instance of Bookmarks */
    public Bookmarks(Display display, BookmarkItem toAdd) {
        super (display);
        setTitleItem(new Title(2, null, SR.MS_BOOKMARKS)); //locale
        
        this.toAdd=toAdd;
        
        //bookmarks=roster.bookmarks;
        
        if (toAdd!=null) addBookmark();
        
        addCommand(cmdCancel);
        addCommand(cmdJoin);
        //addCommand(cmdRfsh);
        addCommand(cmdDel);
        addCommand(cmdDisco);
        setCommandListener(this);
    }
    
    /*private void processIcon(boolean processing){
        getTitleItem().setElementAt((processing)?(Object)new Integer(RosterIcons.ICON_PROGRESS_INDEX):(Object)null, 0);
        redraw();
    }*/
    
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
            StaticData.getInstance().roster.bookmarks.addElement(toAdd);
            saveBookmarks();
        }
    }
    
    public void eventOk(){
        BookmarkItem join=(BookmarkItem)getFocusedObject();
        if (join==null) return;
        if (join.isUrl) return;
        ConferenceForm.join(join.toString(), join.password, 20);
        //stream.cancelBlockListener(this);
        display.setCurrent(StaticData.getInstance().roster);
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) exitBookmarks();
        if (c==cmdJoin) eventOk();
        //if (c==cmdRfsh) loadBookmarks();
        if (c==cmdDel) deleteBookmark();
        if (c==cmdDisco) new ServiceDiscovery(display, ((BookmarkItem)getFocusedObject()).getJid(), null);
    }
    
    private void deleteBookmark(){
        BookmarkItem del=(BookmarkItem)getFocusedObject();
        if (del==null) return;
        if (del.isUrl) return;
        StaticData.getInstance().roster.bookmarks.removeElement(del);
        saveBookmarks();
        redraw();
    }
    
    private void saveBookmarks() {
        new BookmarkQuery(BookmarkQuery.SAVE);
    }

    private void exitBookmarks(){
        //stream.cancelBlockListener(this);
        destroyView();
        //display.setCurrent(StaticData.getInstance().roster);
    }
}
