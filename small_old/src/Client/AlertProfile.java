/*
 * AlertProfile.java
 *
 * Created on 28 Март 2005 г., 0:05
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import images.RosterIcons;
import locale.SR;
import ui.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author Eugene Stahov
 */
public class AlertProfile extends VirtualList implements CommandListener {
    public final static int AUTO=0;
    public final static int ALL=1;
    public final static int VIBRA=2;
    public final static int SOUND=3;
    public final static int NONE=4;
    
    private final static String[] alertNames=
    { "Auto", "All signals", "Vibra", "Sound", "No signals"};
    
    private Profile profile=new Profile();
    int defp;
    Config cf;
    
    /** Creates a new instance of Profile */
    
    private Command cmdOk=new Command("Select",Command.OK,1); //locale
    private Command cmdDef=new Command(SR.MS_SETDEFAULT,Command.OK,2); //locale
    private Command cmdCancel=new Command(SR.MS_BACK,Command.BACK,99); //locale
    /** Creates a new instance of SelectStatus */
    public AlertProfile(Display d) {
        super();
        
        cf=Config.getInstance();
        
        setTitleItem(new Title("Alert Profile")); //locale
        
        addCommand(cmdOk);
        addCommand(cmdDef);
        addCommand(cmdCancel);
        setCommandListener(this);
        
        int p=cf.profile;
        defp=cf.def_profile;
        
        moveCursorTo(p, true);
        attachDisplay(d);
    }
    
    int index;
    public VirtualElement getItemRef(int Index){ index=Index; return profile;}
    private class Profile extends IconTextElement {
        public Profile(){
            super(RosterIcons.getInstance());
        }
        //public void onSelect(){}
        public int getColor(){ return Colors.LIST_INK; }
        public int getImageIndex(){return index+RosterIcons.ICON_PROFILE_INDEX;}
        public String toString(){ 
            StringBuffer s=new StringBuffer(alertNames[index]);
            if (index==defp) s.append(" (default)");
            return s.toString();
        }
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdOk) eventOk(); 
        if (c==cmdDef) { 
            cf.def_profile=defp=cursor;
	    cf.saveToStorage();
            redraw();
        }
        if (c==cmdCancel) destroyView();
    }
    
    public void eventOk(){
        cf.profile=cursor;
        destroyView();
    }
    
    public int getItemCount(){   return alertNames.length; }
    
}
