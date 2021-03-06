/*
 * AccountForm.java
 *
 * Created on 20.03.2005, 21:20
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
import ui.controls.NumberField;
import ui.controls.TextFieldCombo;

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
	
    private NumberField keepAlive;
    private ChoiceGroup keepAliveType;
/*
    private NumberField compressionbox;
*/  
    Command cmdOk = new Command(SR.MS_OK /*"OK"*/, Command.OK, 1);
    Command cmdPwd = new Command(SR.MS_SHOWPWD, Command.SCREEN, 2);
//#if SERVER_SIDE_CONFIG  
//#     Command cmdRequestOptions = new Command(SR.MS_RESTORE_OPTIONS_FROM_SERVER, Command.SCREEN, 3);
//#endif
    Command cmdCancel = new Command(SR.MS_BACK /*"Back"*/, Command.BACK, 99);
    
    Account account;
    
    boolean newaccount;
    
    public AccountForm(AccountSelect accountSelect, Display display, Account account) {
	this.accountSelect = accountSelect;
	this.display=display;
	parentView=display.getCurrent();
	
	newaccount= account==null;
	if (newaccount) account=new Account();
	this.account=account;
	
	String mainbar = (newaccount)?SR.MS_NEW_ACCOUNT:(account.toString());
	f = new Form(mainbar);

        userbox = new TextField(SR.MS_USERNAME, account.getUserName(), 64, TextField.ANY); 
        TextFieldCombo.setLowerCaseLatin(userbox); 
        f.append(userbox);
    
	passbox = new TextField(SR.MS_PASSWORD, account.getPassword(), 64, TextField.PASSWORD);	f.append(passbox);
        passStars(false);

        servbox = new TextField(SR.MS_SERVER,   account.getServer(),   64, TextField.ANY); f.append(servbox);
	ipbox = new TextField(SR.MS_HOST_IP, account.getHostAddr(), 64, TextField.ANY);	f.append(ipbox);

        portbox = new NumberField(SR.MS_PORT, account.getPort(), 0, 65535); f.append(portbox);
	register = new ChoiceGroup(null, Choice.MULTIPLE);
	register.append(SR.MS_SSL,null);
	register.append(SR.MS_PLAIN_PWD,null);
	register.append(SR.MS_SASL,null);
	register.append(SR.MS_CONFERENCES_ONLY,null);
	register.append(SR.MS_PROXY_ENABLE,null);
	register.append(SR.MS_REGISTER_ACCOUNT,null);
        
	boolean b[] = {account.getUseSSL(), account.getPlainAuth(), account.isSASL(), account.isMucOnly(), account.isEnableProxy(), false};
	
	register.setSelectedFlags(b);
	f.append(register);
     
        keepAliveType=new ChoiceGroup(SR.MS_KEEPALIVE, ChoiceGroup.POPUP);
        keepAliveType.append("by socket", null);
        keepAliveType.append("1 byte", null);
        keepAliveType.append("<iq/>", null);
        keepAliveType.append("ping", null);
        keepAliveType.append("version-ping", null);
        keepAliveType.setSelectedIndex(account.keepAliveType, true);
        f.append(keepAliveType);
		
	keepAlive=new NumberField(SR.MS_KEEPALIVE_PERIOD, account.keepAlivePeriod, 10, 2096 ); f.append(keepAlive);
	 
	resourcebox = new TextField(SR.MS_RESOURCE, account.getResource(), 64, TextField.ANY); f.append(resourcebox);
	nickbox = new TextField(SR.MS_NICKNAME, account.getNick(), 64, TextField.ANY); f.append(nickbox);

        proxyHost = new TextField(SR.MS_PROXY_HOST,   account.getProxyHostAddr(),   64, TextField.ANY); f.append(proxyHost);

	proxyPort = new NumberField(SR.PROXY_PORT, account.getProxyPort(), 0, 65535);	f.append(proxyPort);
/*
        compressionbox = new NumberField(SR.MS_COMPRESSION_LEVEL, account.getCompressionLevel(), 1, 8);	f.append(compressionbox);
*/
	f.addCommand(cmdOk);
        f.addCommand(cmdPwd);
//#if SERVER_SIDE_CONFIG        
//#         f.addCommand(cmdRequestOptions);
//#endif
	f.addCommand(cmdCancel);
	
	f.setCommandListener(this);
	f.setItemStateListener(this);
	
	display.setCurrent(f);
    }
    
    private void passStars(boolean force) {
	if (passbox.size()==0 || force)
	    passbox.setConstraints(TextField.ANY | TextField.SENSITIVE);
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
	    servbox.setString(user.substring(at+1));
	} else if (item==passbox) 
            passStars(false);
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
	    account.setUserName(user.trim().toLowerCase());
            
	    account.setPassword(fixPassBugWEME());
            
	    account.setServer(servbox.getString().trim().toLowerCase());
	    account.setHostAddr(ipbox.getString());
	    account.setResource(resourcebox.getString());
	    account.setNick(nickbox.getString());
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
			
            account.keepAlivePeriod=keepAlive.getValue();
            account.keepAliveType=keepAliveType.getSelectedIndex();
/*
            account.setCompressionLevel(compressionbox.getValue());
*/	    
	    if (newaccount) accountSelect.accountList.addElement(account);
	    accountSelect.rmsUpdate();
	    accountSelect.commandState();
	    
	    if (b[5])
		new AccountRegister(account, display, parentView); 
	    else destroyView();
	}
        if (c==cmdPwd) passStars(true);
//#if SERVER_SIDE_CONFIG  
//#         if (c==cmdRequestOptions) new ConfigPrivateStorage(true);
//#endif
    }
    
    public void destroyView()	{
	if (display!=null)   display.setCurrent(parentView);
    }
}
