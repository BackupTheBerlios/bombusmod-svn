

/*
 * SelectStatus.java
 *
 * Created on 27 Февраль 2005 г., 16:43
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import images.RosterIcons;
import java.util.*;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.*;
import ui.controls.NumberField;
import util.strconv;
//import ui.controls.TextFieldCombo;

/**
 *
 * @author Eugene Stahov
 */
public class StatusSelect extends VirtualList implements CommandListener, Runnable{
    
    private Command cmdOk=new Command(SR.MS_SELECT,Command.OK,1);
    private Command cmdEdit=new Command(SR.MS_EDIT,Command.SCREEN,2);
    private Command cmdDef=new Command(SR.MS_SETDEFAULT,Command.OK,3);
    private Command cmdCancel=new Command(SR.MS_CANCEL,Command.BACK,99);
    /** Creates a new instance of SelectStatus */
    private Vector statusList;

    private int defp;

    private Contact to;
    
    private Config cf=Config.getInstance();
     
    public StatusSelect(Display d, Contact to) {
        super();
        statusList=StatusList.getInstance().statusList;
        this.to=to;
        if (to==null) { 
            setTitleItem(new Title(SR.MS_STATUS));
        } else {
            setTitleItem(new Title(to));
        }
        addCommand(cmdOk);
        addCommand(cmdEdit);
        addCommand(cmdDef);
        //addCommand(cmdPriority);
        //addCommand(cmdAll);
        addCommand(cmdCancel);
        setCommandListener(this);
        
        defp=cf.loginstatus;
        moveCursorTo(defp, true);
        attachDisplay(d);
    }
    public VirtualElement getItemRef(int Index){
        return (VirtualElement)statusList.elementAt(Index);
    }
    
    private ExtendedStatus getSel(){ return (ExtendedStatus)getFocusedObject();}
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdOk) eventOk(); 
        if (c==cmdEdit) {
            new StatusForm( display, getSel() );
        };
        
        if (c==cmdDef) {
            cf.loginstatus=cursor;
	    cf.saveToStorage();
            redraw();
        }

        if (c==cmdCancel) destroyView();
    }
    
    public void eventOk(){
        destroyView();
        new Thread(this).start();
    }
    
    public void run(){
        int status=getSel().getImageIndex();
        try {
            Roster.isAway=false;
            StaticData.getInstance().roster.keyTimer=0;
            StaticData.getInstance().roster.sendDirectPresence(status, to);
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public int getItemCount(){   return statusList.size(); }
    
    private void save(){
        StatusList.getInstance().saveStatusToStorage();
    }

    class StatusForm implements CommandListener{
        private Display display;
        public Displayable parentView;
        
        private Form f;
        private NumberField tfPriority;
        private TextField tfMessage;
        
        private ExtendedStatus status;
        
        private Command cmdOk=new Command(SR.MS_OK,Command.OK,1);
        private Command cmdSetMusic=new Command("1251",Command.SCREEN,2); //locale
        private Command cmdSetMusicUnicode=new Command("Unicode",Command.SCREEN,3); //locale
        private Command cmdCancel=new Command(SR.MS_CANCEL,Command.BACK,99);

        private ChoiceGroup selStatus;
        
        ExtendedStatusList statuslist=new ExtendedStatusList();

        private ChoiceGroup addNewStatusText;
        
        public StatusForm(Display display, ExtendedStatus status){
            this.display=display;
            parentView=display.getCurrent();
            this.status=status;
            
            f=new Form(status.getName());
            
            tfPriority=new NumberField(SR.MS_PRIORITY, status.getPriority(), -128, 128);
            f.append(tfPriority);
            
            //tfMessage=new TextFieldCombo(SR.MS_MESSAGE, status.getMessage(), 100, 0, "status", display);
            tfMessage=new TextField(SR.MS_MESSAGE, status.getMessage(), 100, 0);
            f.append(tfMessage);
            
            
            selStatus=new ChoiceGroup(SR.MS_CHOOSE_STATUS, ConstMIDP.CHOICE_POPUP);
            selStatus.append(status.getMessage(), null );
            for (int i=0;i<statuslist.size();i++) {
                selStatus.append((String)statuslist.msg(i), null );
            }
            selStatus.setSelectedIndex(0, true);
            f.append(selStatus);
            
            addNewStatusText=new ChoiceGroup(null, ChoiceGroup.MULTIPLE);
            addNewStatusText.append(SR.MS_ALL_STATUSES, null);
            addNewStatusText.append(SR.MS_ADD_STATUS, null);
            addNewStatusText.append(SR.MS_REMOVE_STATUS, null);
            f.append(addNewStatusText);
            
            f.addCommand(cmdOk);
            f.addCommand(cmdCancel);
            
            if (getMusic()!=null) {
                f.addCommand(cmdSetMusic);
                f.addCommand(cmdSetMusicUnicode);
            }
            
            f.setCommandListener(this);
            display.setCurrent(f);
        }
        
        public void commandAction(Command c, Displayable d){
            if (c==cmdSetMusic) {
                tfMessage.setString(strconv.convCp1251ToUnicode(getMusic()));
            }
            if (c==cmdSetMusicUnicode) {
                tfMessage.setString(getMusic());
            }
            if (c==cmdOk) {
                boolean flags[]=new boolean[3];
                
                if (selStatus.getSelectedIndex()>0) {
                    if (flags[2]) {
                        status.setMessage(tfMessage.getString());
                    } else {
                        status.setMessage(selStatus.getString(selStatus.getSelectedIndex()));   
                    }
                } else {
                    status.setMessage(tfMessage.getString());                    
                }
               
		int priority=tfPriority.getValue();
                status.setPriority(priority);
                
                addNewStatusText.getSelectedFlags(flags);
                if (flags[0]) {
                    for (Enumeration e=StatusList.getInstance().statusList.elements(); e.hasMoreElements();) {
                        ((ExtendedStatus)e.nextElement()).setPriority(priority);
                    }
                }
                if (flags[1]) {
                    statuslist.store(tfMessage.getString());
                }
                if (flags[2]) {
                    statuslist.delete(selStatus.getSelectedIndex()-1);
                }
                save();
                destroyView();
            }
            if (c==cmdCancel) {  destroyView();  }
        }
        
        public void destroyView(){
            if (display!=null)   display.setCurrent(parentView);
        }
    }
    public static String getMusic() {
        return System.getProperty("MPJCPLYR");
    }
}
