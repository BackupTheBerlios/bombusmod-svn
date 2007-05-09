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
import ui.controls.TextFieldCombo;

/**
 *
 * @author Eugene Stahov
 */
public class StatusSelect extends VirtualList implements CommandListener, Runnable{
    
    private Command cmdOk=new Command("Select",Command.OK,1); //locale
    private Command cmdEdit=new Command(SR.MS_EDIT,Command.SCREEN,2); //locale
    private Command cmdDef=new Command(SR.MS_SETDEFAULT,Command.OK,3);
    private Command cmdCancel=new Command(SR.MS_CANCEL,Command.BACK,99); //locale
    /** Creates a new instance of SelectStatus */
    private Vector statusList;
    private Contact to;
    
    private Config cf;
    private int defp;
    
    public StatusSelect(Display d, Contact to) {
        super();
        statusList=StatusList.getInstance().statusList;
        
        cf=Config.getInstance();
        
        this.to=to;
        if (to==null) { setTitleItem(new Title(SR.MS_STATUS)); }
        else setTitleItem(new Title(to));
        
        addCommand(cmdOk);
        addCommand(cmdEdit);
        addCommand(cmdDef);
        //addCommand(cmdPriority);
        //addCommand(cmdAll);
        addCommand(cmdCancel);
        setCommandListener(this);
        
        /*
        int status=StaticData.getInstance().roster.myStatus;
        int i;
        for (i=0;i<statusList.size(); i++) {
            if (status==((ExtendedStatus)getItemRef(i)).getImageIndex()) break;
        }
        moveCursorTo(i);
        */
        defp=cf.loginstatus;
        moveCursorTo(defp, true);

        attachDisplay(d);
    }
    public VirtualElement getItemRef(int Index){
        return (VirtualElement)statusList.elementAt(Index);
    }
    
    private ExtendedStatus getSel(){ return (ExtendedStatus)getFocusedObject();}
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdDef) {
            cf.loginstatus=cursor;
	    cf.saveToStorage();
            redraw();
        }
        if (c==cmdOk) eventOk(); 
        if (c==cmdEdit) {
            new StatusForm( display, getSel() );
        };
        if (c==cmdCancel) destroyView();
    }
    
    public void eventOk(){
        destroyView();
        new Thread(this).start();
    }
    
    public void run(){
        int status=getSel().getImageIndex();
        try {
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
        
        private ChoiceGroup chPriorityAll;
        
        private ExtendedStatus status;
        
        private Command cmdOk=new Command("Ok",Command.OK,1); //locale
        private Command cmdCancel=new Command(SR.MS_CANCEL,Command.BACK,99); //locale
        
        public StatusForm(Display display, ExtendedStatus status){
            this.display=display;
            parentView=display.getCurrent();
            this.status=status;
            
            f=new Form(status.getName());
            
            tfPriority=new NumberField("Priority", status.getPriority(), -128, 128); //locale
            f.append(tfPriority);

            chPriorityAll=new ChoiceGroup(null, ChoiceGroup.MULTIPLE);
            chPriorityAll.append(SR.MS_ALL_STATUSES, null); //locale
            f.append(chPriorityAll);
            
            tfMessage=new TextFieldCombo(SR.MS_MESSAGE, status.getMessage(), 100, 0, "status", display);
            f.append(tfMessage);
            
            f.addCommand(cmdOk);
            f.addCommand(cmdCancel);
            
            f.setCommandListener(this);
            display.setCurrent(f);
        }
        
        public void commandAction(Command c, Displayable d){
            if (c==cmdOk) {
                status.setMessage(tfMessage.getString());
                
		int priority=tfPriority.getValue();
                status.setPriority(priority);
                
                boolean flags[]=new boolean[1];
                chPriorityAll.getSelectedFlags(flags);
                if (flags[0]) {
                    for (Enumeration e=StatusList.getInstance().statusList.elements(); e.hasMoreElements();) {
                        ((ExtendedStatus)e.nextElement()).setPriority(priority);
                    }
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
}
