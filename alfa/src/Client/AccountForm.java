/*
 * AccountForm.java
 *
 * Created on 20 Март 2005 г., 21:20
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.ConstMIDP;
import ui.controls.NumberField;

class AccountForm implements CommandListener, ItemStateListener {
    
    private final AccountSelect accountSelect;
    
    private Display display;
    private Displayable parentView;
    
    private Form f;
    private TextField userbox;
    private TextField passbox;
    
    Command cmdOk = new Command("Ok", Command.OK, 1); //locale
    Command cmdPwd = new Command(SR.MS_SHOWPWD, Command.SCREEN, 2); //locale
    Command cmdCancel = new Command(SR.MS_BACK /*"Back"*/, Command.BACK, 99); //locale
    
    Account account;
    
    boolean newaccount;
    
    public AccountForm(AccountSelect accountSelect, Display display, Account account) {
	this.accountSelect = accountSelect;
	this.display=display;
	parentView=display.getCurrent();
	
	newaccount= account==null;
	if (newaccount) account=new Account();
	this.account=account;
	
	String title = (newaccount)?
	    SR.MS_NEW_ACCOUNT /*"New Account"*/:  //locale
	    (account.toString());
	f = new Form(title);
	userbox = new TextField("Username", account.getUserName(), 32, TextField.URL); f.append(userbox); //locale
	passbox = new TextField("Password", account.getPassword(), 32, TextField.PASSWORD);	f.append(passbox); //locale
        passStars(false);

        
	f.addCommand(cmdOk);
        f.addCommand(cmdPwd);
	f.addCommand(cmdCancel);
	
	f.setCommandListener(this);
	f.setItemStateListener(this);
	
	display.setCurrent(f);
    }
    
    private void passStars(boolean force) {
	if (passbox.size()==0 || force)
	    passbox.setConstraints(TextField.ANY | ConstMIDP.TEXTFIELD_SENSITIVE);
        fixPassBugWEME();
    }
    
    private String fixPassBugWEME(){
        String newPass=passbox.getString();
        String oldPass=account.getPassword();
        
        if (oldPass!=null)
            if (oldPass.length()==newPass.length() && newPass.startsWith("**") && newPass.endsWith("**")) {
                newPass=oldPass;
                passbox.setString(oldPass);
            }
        return newPass;
    }
    
    public void itemStateChanged(Item item) {
	if (item==passbox) passStars(false);
    }
    
    public void commandAction(Command c, Displayable d) {
	if (c==cmdCancel) {
	    destroyView();
	    return;
	}
	if (c==cmdOk) {
	    String user = userbox.getString();
	    account.setUserName(user.trim());
            
	    account.setPassword(fixPassBugWEME());
	    
	    if (newaccount) accountSelect.accountList.addElement(account);
	    accountSelect.rmsUpdate();
	    accountSelect.commandState();
	    
	    destroyView();
	}
        if (c==cmdPwd) passStars(true);
    }
    
    public void destroyView()	{
	if (display!=null)   display.setCurrent(parentView);
    }
}
