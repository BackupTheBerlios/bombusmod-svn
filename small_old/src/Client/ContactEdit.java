/*
 * ContactEdit.java
 *
 * Created on 7 Май 2005 г., 2:15
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import Conference.MucContact;
import javax.microedition.lcdui.*;
import java.util.*;
import locale.SR;
import ui.ConstMIDP;

/**
 *
 * @author Evg_S
 */
public final class ContactEdit
        implements CommandListener, ItemStateListener 
//#if (!MIDP1)
        , ItemCommandListener
//#endif
{
    private Display display;
    public Displayable parentView;
    
    Form f;
    TextField tJid;
    TextField tNick;
    TextField tGroup;
    ChoiceGroup tGrpList;
    ChoiceGroup tTranspList;
    ChoiceGroup tAskSubscrCheckBox;
    
    int ngroups;
    
    Command cmdOk=new Command("Add", Command.OK, 1); //locale
    Command cmdSet=new Command(SR.MS_SET, Command.ITEM, 2);
    Command cmdCancel=new Command(SR.MS_CANCEL,Command.BACK,99); //locale
    
    boolean newContact=true;
    Config cf;
    Roster roster;

    
    //StoreContact sC;
    
    public ContactEdit(Display display, Contact c) {
        this.display=display;
        parentView=display.getCurrent();
        
        StaticData sd=StaticData.getInstance();
        roster=sd.roster;
        
        Vector groups=sd.roster.groups.getRosterGroupNames();
        cf=Config.getInstance();
        
        f=new Form(SR.MS_ADD_CONTACT); //locale
        
        tJid=new TextField("User JID", null, 150, TextField.EMAILADDR);  //locale
        
        tNick=new TextField("Name", null, 32, TextField.ANY);  //locale
        tGroup=new TextField(SR.MS_GROUP ,null, 32, TextField.ANY); //locale
        
        
        tGrpList=new ChoiceGroup("Existing groups" , ConstMIDP.CHOICE_POPUP); //locale
        tTranspList=new ChoiceGroup(SR.MS_TRANSPORT, ConstMIDP.CHOICE_POPUP); //locale
        
        tAskSubscrCheckBox=new ChoiceGroup(SR.MS_SUBSCRIPTION, ChoiceGroup.MULTIPLE); //locale
        tAskSubscrCheckBox.append(SR.MS_ASK_SUBSCRIPTION, null); //locale
        
        
//#if (!MIDP1)
        //NOKIA FIX
        tGrpList.addCommand(cmdSet);
        tGrpList.setItemCommandListener(this);
        
        tTranspList.addCommand(cmdSet);
        tTranspList.setItemCommandListener(this);
//#endif
        
        // Transport droplist
        tTranspList.append(sd.account.getServer(), null);
        for (Enumeration e=sd.roster.getHContacts().elements(); e.hasMoreElements(); ){
            Contact ct=(Contact)e.nextElement();
            Jid transpJid=ct.jid;
            if (transpJid.isTransport()) 
                tTranspList.append(transpJid.getBareJid(),null);
        }
        tTranspList.append(SR.MS_OTHER,null); //locale
        
        try {
            String jid;
            if (c instanceof MucContact) {
                jid=Jid.toBareJid( ((MucContact)c).realJid );
            } else {
                jid=c.getBareJid();
            }
            // edit contact
            tJid.setString(jid);
            tNick.setString(c.nick);
            
            if (c instanceof MucContact) {
                c=null;
                throw new Exception();
            } 
            
            if (c.getGroupType()!=Groups.TYPE_NOT_IN_LIST  && c.getGroupType()!=Groups.TYPE_SEARCH_RESULT) {
                // edit contact
                f.setTitle(jid);
                cmdOk=new Command("Update", Command.OK, 1); //locale
                newContact=false;
            } else c=null; // adding not-in-list
        } catch (Exception e) {c=null;} // if MucContact does not contains realJid
        
        
        int sel=-1;
        ngroups=0;
        String grpName="";
        if (c!=null) grpName=c.getGroup().name;
        
        if (groups!=null) {
            ngroups=groups.size();
            for (int i=0;i<ngroups; i++) {
                String gn=(String)groups.elementAt(i);
                tGrpList.append(gn, null);
                
                if (gn.equals(grpName)) sel=i;
            }
        }
            
        //if (sel==-1) sel=groups.size()-1;
        if (sel<0) sel=0;
        tGroup.setString(group(sel));
        
        
        if (c==null){
            f.append(tJid);
            f.append(tTranspList);
        }
        updateChoise(tJid.getString(),tTranspList);
        f.append(tNick);
        f.append(tGroup);
        
        tGrpList.append("<New Group>",null); //locale
        tGrpList.setSelectedIndex(sel, true);
        
        f.append(tGrpList);
        
        if (newContact) {
            f.append(tAskSubscrCheckBox);
            tAskSubscrCheckBox.setSelectedIndex(0, true);
        }
        
        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        
        f.setCommandListener(this);
        f.setItemStateListener(this);
        
        display.setCurrent(f);
    }
    
    //public interface StoreContact {
    //    public void storeContact(String jid, String name, String group, boolean newContact);
    //}

    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            String jid=getString(tJid);
            if (jid!=null) {
                String name=getString(tNick);
                String group=getString(tGroup);
                
                try {
                    int gSel=tGrpList.getSelectedIndex();
                    if (gSel!=tGrpList.size()-1)  {
                        group=(gSel>0)? tGrpList.getString(gSel) : null; // nokia fix
                    }
                } catch (Exception e) {} // nokia fix
                
                // сохранение контакта
                boolean ask[]=new boolean[1];
                tAskSubscrCheckBox.getSelectedFlags(ask);
                roster.storeContact(jid,name,group, ask[0]);
                destroyView();
                return;
            }
        }
        
        if (c==cmdCancel) destroyView();
    }
    
//#if (!MIDP1)
    public void commandAction(Command command, Item item) {
        itemStateChanged(item);
    }
//#endif

    private String getString(TextField t){
        if (t.size()==0) return null;
        String s=t.getString().trim();
        if (s.length()==0) return null;
        return s;
    }
    
    private String group(int index) {
        if (index==0) return null;
        if (index==tGrpList.size()-1) return null;
        return tGrpList.getString(index);
    }
    
    private void updateChoise(String str, ChoiceGroup grp) {
        int sz=grp.size();
        int set=sz-1;
        for (int i=0; i<sz; i++) {
            if (str.equals(grp.getString(i))) {
                set=i;
                break;
            }
        }
        if (grp.getSelectedIndex()!=set) 
            grp.setSelectedIndex(set, true);
    }
    
    public void itemStateChanged(Item item){
        if (item==tGrpList) {
            int index=tGrpList.getSelectedIndex();
            tGroup.setString(group(index));
        }
        if (item==tGroup) {
            updateChoise(tGroup.getString(), tGrpList);
        }
        if (item==tTranspList) {
            int index=tTranspList.getSelectedIndex();
            if (index==tTranspList.size()-1) return;
            
            String transport=tTranspList.getString(index);
            
            String jid=tJid.getString();
            StringBuffer jidBuf=new StringBuffer(jid);
            
            int at=jid.indexOf('@');
            if (at<0) at=tJid.size();
            
            jidBuf.setLength(at);
            jidBuf.append('@');
            jidBuf.append(transport);
            tJid.setString(jidBuf.toString());
        }
        if (item==tJid) {
            String s1=tJid.getString();
            int at=tJid.getString().indexOf('@');
            try {
                updateChoise(s1.substring(at+1), tTranspList);
            } catch (Exception e) {}
        }
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView/*roster*/);
    }

}
