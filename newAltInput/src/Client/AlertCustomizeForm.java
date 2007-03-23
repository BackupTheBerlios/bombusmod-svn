/*
 * AlertCustomizeForm.java
 *
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

import locale.SR;
import ui.EventNotify;
import java.util.Vector;
import util.StringLoader;
import java.util.Enumeration;
import javax.microedition.lcdui.*;


public class AlertCustomizeForm implements
	CommandListener ,ItemCommandListener
{
    private Display display;
    private Displayable parentView;

    Form f;
    ChoiceGroup MessageFile;
    ChoiceGroup OnlineFile;
    ChoiceGroup OfflineFile;
    ChoiceGroup ForYouFile;
    ChoiceGroup ComposingFile;
    ChoiceGroup ConferenceFile;
    
    Gauge sndVol;
    
    Command cmdOk=new Command(SR.MS_OK,Command.OK,1);
    Command cmdMessageSound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdOnlineSound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdOfflineSound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdForYouSound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdComposingSound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdConferenceSound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    
    AlertCustomize ac;
    
    Vector files[]=new StringLoader().stringLoader("/sounds/res.txt",3);


    /** Creates a new instance of ConfigForm */
    public AlertCustomizeForm(Display display) {
        this.display=display;
        parentView=display.getCurrent();
        
        ac=AlertCustomize.getInstance();
       
        f=new Form(SR.MS_OPTIONS);
        
        MessageFile=new ChoiceGroup("Message sound", ChoiceGroup.POPUP);
	for (Enumeration f=files[2].elements(); f.hasMoreElements(); ) {
	    MessageFile.append( (String)f.nextElement(), null );
	}
        try {
            MessageFile.setSelectedIndex(ac.soundsMsgIndex, true);
        } catch (Exception e) {ac.soundsMsgIndex=0;}
        
	f.append(MessageFile);
	MessageFile.addCommand(cmdMessageSound);
	MessageFile.setItemCommandListener(this);
        
        
        OnlineFile=new ChoiceGroup(SR.MS_ONLINE+" "+SR.MS_SOUND, ChoiceGroup.POPUP);
	for (Enumeration f=files[2].elements(); f.hasMoreElements(); ) {
	    OnlineFile.append( (String)f.nextElement(), null );
	}
        try {
            OnlineFile.setSelectedIndex(ac.soundOnlineIndex, true);
        } catch (Exception e) {ac.soundOnlineIndex=0;}
	f.append(OnlineFile);
        OnlineFile.addCommand(cmdOnlineSound);
	OnlineFile.setItemCommandListener(this);
        
                 
        OfflineFile=new ChoiceGroup(SR.MS_OFFLINE+" "+SR.MS_SOUND, ChoiceGroup.POPUP);
	for (Enumeration f=files[2].elements(); f.hasMoreElements(); ) {
	    OfflineFile.append( (String)f.nextElement(), null );
	}
        try {
            OfflineFile.setSelectedIndex(ac.soundOfflineIndex, true);
        } catch (Exception e) {ac.soundOfflineIndex=0;}
	f.append(OfflineFile);
        OfflineFile.addCommand(cmdOfflineSound);
	OfflineFile.setItemCommandListener(this);
        
    
        ForYouFile=new ChoiceGroup(SR.MS_MESSAGE_FOR_ME+" "+SR.MS_SOUND, ChoiceGroup.POPUP);
	for (Enumeration f=files[2].elements(); f.hasMoreElements(); ) {
	    ForYouFile.append( (String)f.nextElement(), null );
	}
        try {
            ForYouFile.setSelectedIndex(ac.soundForYouIndex, true);
        } catch (Exception e) {ac.soundForYouIndex=0;}
	f.append(ForYouFile);
        ForYouFile.addCommand(cmdForYouSound);
	ForYouFile.setItemCommandListener(this);
        
            
        ComposingFile=new ChoiceGroup(SR.MS_COMPOSING_EVENTS+" "+SR.MS_SOUND, ChoiceGroup.POPUP);
	for (Enumeration f=files[2].elements(); f.hasMoreElements(); ) {
	    ComposingFile.append( (String)f.nextElement(), null );
	}
        try {
            ComposingFile.setSelectedIndex(ac.soundComposingIndex, true);
        } catch (Exception e) {ac.soundComposingIndex=0;}
	f.append(ComposingFile);
        ComposingFile.addCommand(cmdComposingSound);
	ComposingFile.setItemCommandListener(this);   
        
        ConferenceFile=new ChoiceGroup(SR.MS_SOUND+" for conference", ChoiceGroup.POPUP);
	for (Enumeration f=files[2].elements(); f.hasMoreElements(); ) {
	    ConferenceFile.append( (String)f.nextElement(), null );
	}
        try {
            ConferenceFile.setSelectedIndex(ac.soundConferenceIndex, true);
        } catch (Exception e) {ac.soundConferenceIndex=0;}
	f.append(ConferenceFile);
        ConferenceFile.addCommand(cmdConferenceSound);
	ConferenceFile.setItemCommandListener(this);

        sndVol=new Gauge("Sound volume", true, 10,  ac.soundVol/10);
	sndVol.addCommand(cmdMessageSound);
	sndVol.setItemCommandListener(this);
	f.append(sndVol);

        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        
        f.setCommandListener(this);
        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
	    ac.soundsMsgIndex=MessageFile.getSelectedIndex();
	    ac.soundVol=sndVol.getValue()*10;
            ac.soundOnlineIndex=OnlineFile.getSelectedIndex();
            ac.soundOfflineIndex=OfflineFile.getSelectedIndex();
            ac.soundForYouIndex=ForYouFile.getSelectedIndex();
            ac.soundComposingIndex=ComposingFile.getSelectedIndex();
            ac.soundConferenceIndex=ConferenceFile.getSelectedIndex(); 

 	    ac.loadSoundName();
 	    ac.loadOnlineSoundName();
 	    ac.loadOfflineSoundName();
 	    ac.loadForYouSoundName();
            ac.loadComposingSoundName();
            ac.loadConferenceSoundName();
            ac.saveToStorage();
            StaticData.getInstance().roster.reEnumRoster();
            destroyView();
        }
            
        if (c==cmdCancel) destroyView();
    }

    public void commandAction(Command command, Item item) {
 	if (command==cmdMessageSound) {
 	    MessageSound();
 	}
 	if (command==cmdOnlineSound) {
 	    OnlineSound();
 	}
 	if (command==cmdOfflineSound) {
 	    OfflineSound();
 	}
 	if (command==cmdForYouSound) {
 	    ForYouSound();
 	}
 	if (command==cmdComposingSound) {
 	    ComposingSound();
 	}
 	if (command==cmdConferenceSound) {
 	    ConferenceSound();
 	}
    }

    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
        ((Canvas)parentView).setFullScreenMode(Config.getInstance().fullscreen);
    }

    private void MessageSound(){
	int sound=MessageFile.getSelectedIndex();
	String soundFile=(String)files[1].elementAt(sound);
	String soundType=(String)files[0].elementAt(sound);
        int soundVol=sndVol.getValue()*10;
	new EventNotify(display, soundType, soundFile,soundVol, 0, false).startNotify();
    }

    private void OnlineSound(){
	int sound=OnlineFile.getSelectedIndex();
	String soundFile=(String)files[1].elementAt(sound);
	String soundType=(String)files[0].elementAt(sound);
        int soundVol=sndVol.getValue()*10;
	new EventNotify(display, soundType, soundFile,soundVol, 0, false).startNotify();
    }
         
    private void OfflineSound(){
	int sound=OfflineFile.getSelectedIndex();
	String soundFile=(String)files[1].elementAt(sound);
	String soundType=(String)files[0].elementAt(sound);
        int soundVol=sndVol.getValue()*10;
	new EventNotify(display, soundType, soundFile,soundVol, 0, false).startNotify();
    }
    
    private void ForYouSound(){
	int sound=ForYouFile.getSelectedIndex();
	String soundFile=(String)files[1].elementAt(sound);
	String soundType=(String)files[0].elementAt(sound);
        int soundVol=sndVol.getValue()*10;
	new EventNotify(display, soundType, soundFile,soundVol, 0, false).startNotify();
    }
    
    private void ComposingSound(){
	int sound=ComposingFile.getSelectedIndex();
	String soundFile=(String)files[1].elementAt(sound);
	String soundType=(String)files[0].elementAt(sound);
        int soundVol=sndVol.getValue()*10;
	new EventNotify(display, soundType, soundFile,soundVol, 0, false).startNotify();
    }
    
    private void ConferenceSound(){
	int sound=ConferenceFile.getSelectedIndex();
	String soundFile=(String)files[1].elementAt(sound);
	String soundType=(String)files[0].elementAt(sound);
        int soundVol=sndVol.getValue()*10;
	new EventNotify(display, soundType, soundFile,soundVol, 0, false).startNotify();
    }
}
