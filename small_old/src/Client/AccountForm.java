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
    private TextField servbox;
    private TextField ipbox;
    private NumberField portbox;
    private TextField resourcebox;
    private TextField nickbox;
    private TextField proxyHost;
    private NumberField proxyPort;
    private ChoiceGroup register;
    
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
	servbox = new TextField(SR.MS_SERVER,   account.getServer(),   32, TextField.URL); f.append(servbox); //locale
	ipbox = new TextField(SR.MS_HOST_IP, account.getHostAddr(), 32, TextField.URL);	f.append(ipbox); //locale
	portbox = new NumberField(SR.MS_PORT, account.getPort(), 0, 65535); f.append(portbox); //locale
	register = new ChoiceGroup(null, Choice.MULTIPLE);
	register.append("use SSL",null); //locale
	register.append(SR.MS_PLAIN_PWD,null); //locale
	register.append(SR.MS_SASL,null); //locale
	register.append("conferences only",null); //locale
	register.append(SR.MS_PROXY_ENABLE,null); //locale
	register.append(SR.MS_REGISTER_ACCOUNT,null); //locale
	boolean b[] = {account.getUseSSL(), account.getPlainAuth(), account.isSASL(), account.isMucOnly(), account.isEnableProxy(), false};
	
	register.setSelectedFlags(b);
	f.append(register);
        
	proxyHost = new TextField(SR.MS_PROXY_HOST,   account.getProxyHostAddr(),   32, TextField.URL); f.append(proxyHost); //locale
	proxyPort = new NumberField(SR.PROXY_PORT, account.getProxyPort(), 0, 65535);	f.append(proxyPort); //locale
        
	resourcebox = new TextField(SR.MS_RESOURCE, account.getResource(), 32, TextField.ANY); f.append(resourcebox); //locale
	nickbox = new TextField(SR.MS_NICKNAME, account.getNickName(), 32, TextField.ANY); f.append(nickbox);
	
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
	if (item==userbox) {
	    String user = userbox.getString();
	    int at = user.indexOf('@');
	    if (at==-1) return;
	    //userbox.setString(user.substring(0,at));
	    servbox.setString(user.substring(at+1));
	}
	if (item==passbox) passStars(false);
    }
    
    public void commandAction(Command c, Displayable d) {
	if (c==cmdCancel) {
	    destroyView();
	    return;
	}
	if (c==cmdOk) {
	    boolean b[] = new boolean[6];
	    register.getSelectedFlags(b);
	    String user = userbox.getString();
	    int at = user.indexOf('@');
	    if (at!=-1) user=user.substring(0, at);
	    account.setUserName(user.trim());
            
	    account.setPassword(fixPassBugWEME());
            
	    account.setServer(servbox.getString().trim());
	    account.setHostAddr(ipbox.getString());
	    account.setResource(resourcebox.getString());
	    account.setNickName(nickbox.getString());
	    account.setUseSSL(b[0]);
	    account.setPlainAuth(b[1]);
//#if SASL
            account.setSasl(b[2]);
//#endif
	    account.setMucOnly(b[3]);
	    account.setEnableProxy(b[4]);
	    //account.updateJidCache();
	    
	    account.setPort(portbox.getValue());

	    account.setProxyHostAddr(proxyHost.getString());
            account.setProxyPort(proxyPort.getValue());
	    
	    if (newaccount) accountSelect.accountList.addElement(account);
	    accountSelect.rmsUpdate();
	    accountSelect.commandState();
	    
	    if (b[5])
		new AccountRegister(account, display, parentView); 
	    else destroyView();
	}
        if (c==cmdPwd) passStars(true);
    }
    
    public void destroyView()	{
	if (display!=null)   display.setCurrent(parentView);
    }
}
