/*
 * MoodSelect.java
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

package UserMood;
import Client.Config;
import Client.StaticData;
import com.alsutton.jabber.datablocks.IqMood;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.pim.Contact;
import locale.SR;
import ui.*;
import ui.MainBar;
import ui.controls.TextFieldCombo;

public class MoodSelect extends VirtualList implements CommandListener, Runnable{
    
    private Command cmdOk=new Command(SR.MS_SELECT,Command.OK,1);
    private Command cmdEdit=new Command(SR.MS_EDIT,Command.SCREEN,2);
    private Command cmdCancel=new Command(SR.MS_CANCEL,Command.BACK,99);

    private Vector moodList;

    private Contact to;
    
    private Config cf=Config.getInstance();
     
    public MoodSelect(Display d, Contact to) {
        super();
        moodList=MoodList.getInstance().moodList;
        this.to=to;
        
        setMainBarItem(new MainBar(SR.MS_STATUS));

        addCommand(cmdOk);
        addCommand(cmdEdit);

        addCommand(cmdCancel);
        setCommandListener(this);

        attachDisplay(d);
    }
    public VirtualElement getItemRef(int Index){
        return (VirtualElement)moodList.elementAt(Index);
    }
    
    private Mood getSel(){ return (Mood)getFocusedObject();}
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdOk) eventOk(); 
        if (c==cmdEdit) {
            new MoodForm( display, getSel() );
        };
        if (c==cmdCancel) destroyView();
    }
    
    public void eventOk(){
        destroyView();
        new Thread(this).start();
    }
    
    public void run(){
        try {
            StaticData.getInstance().roster.theStream.send(new IqMood(null,"publish1",getSel().getName(),null));
        } catch (Exception e) { }
    }
    
    public int getItemCount(){
        return moodList.size();
    }


    class MoodForm implements CommandListener{
        private Display display;
        public Displayable parentView;
        
        private Form f;

        private TextField tfMessage;
        
        private Mood mood;
        
        private Command cmdOk=new Command(SR.MS_OK,Command.OK,1);
        private Command cmdCancel=new Command(SR.MS_CANCEL,Command.BACK,99);
        
        public MoodForm(Display display, Mood mood){
            this.display=display;
            parentView=display.getCurrent();
            this.mood=mood;
            
            f=new Form(mood.getMessage());
            
            tfMessage=new TextFieldCombo(SR.MS_MESSAGE, mood.getMessage(), 100, 0, "mood", display);
            f.append(tfMessage);
            
            f.addCommand(cmdOk);
            f.addCommand(cmdCancel);
            
            f.setCommandListener(this);
            display.setCurrent(f);
        }
        
        public void commandAction(Command c, Displayable d){
            if (c==cmdOk) {
                mood.setMessage(tfMessage.getString());
                //System.out.println(mood.getName()+" "+mood.getMessage());
                StaticData.getInstance().roster.theStream.send(new IqMood(null,"publish1",mood.getName(),mood.getMessage()));
                
                goToRoster();
            } else {
                destroyView();
            }
        }
        
        public void destroyView(){
            if (display!=null) display.setCurrent(parentView);
        }
        
        public void goToRoster(){
            if (display!=null) display.setCurrent(StaticData.getInstance().roster);
        }
    }
}
