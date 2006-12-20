/*
 * AccountForm.java
 *
 * Created on 17. prosinec 2003, 17:51
 */

import javax.microedition.lcdui.*;

/**
 *
 * @author  radek
 */
public class AccountForm extends javax.microedition.lcdui.Form
        implements ItemStateListener
{    
    private TextField jid;
    private ChoiceGroup savePassword;
    private TextField password;
    private ChoiceGroup hostChoice;
    private TextField host;
    private TextField port;
    private TextField resource;
    private ChoiceGroup caching;
    
    private boolean hostDisp, pwdDisp;
    
    private int pwdPos = 2;
    private int hostPos = 4;
    
    private Config config;
    
    public AccountForm(Config conf) 
    {
        super("Account");
        this.config = conf;

        //Create the form
        jid = new TextField("JID", config.JID, 64, TextField.EMAILADDR);
        append(jid);
        savePassword = new ChoiceGroup("Password", ChoiceGroup.EXCLUSIVE);
        savePassword.append("Don't save", null);
        savePassword.append("Save", null);
        savePassword.setSelectedIndex(config.savePassword ? 1 : 0, true);
        append(savePassword);
        password = new TextField("Password", config.password, 32,  TextField.PASSWORD);
        pwdDisp = config.savePassword;
        if (pwdDisp)
            append(password);
        hostChoice = new ChoiceGroup("Host", ChoiceGroup.EXCLUSIVE);
        hostChoice.append("From JID", null);
        hostChoice.append("Manual", null);
        hostChoice.setSelectedIndex(config.manualHost ? 1 : 0, true);
        append(hostChoice);
        host = new TextField("Host", config.host, 64, TextField.ANY);
        hostDisp = config.manualHost;
        if (hostDisp)
            append(host);
        port = new TextField("Port", String.valueOf(config.port), 8, TextField.NUMERIC);
        append(port);
        resource = new TextField("Resource", config.resource, 16, TextField.ANY);
        append(resource);
        caching = new ChoiceGroup("Roster Cache", ChoiceGroup.EXCLUSIVE);
        caching.append("On", null);
        caching.append("Off", null);
        caching.setSelectedIndex(config.caching ? 0 : 1, true);
        append(caching);
        
        setItemStateListener(this);
    }
    
    public void save()
    {
        config.JID = jid.getString();
        config.savePassword = (savePassword.getSelectedIndex() == 1);
        config.password = password.getString();
        config.manualHost = (hostChoice.getSelectedIndex() == 1);
        config.host = host.getString();
        try {
            config.port = Integer.parseInt(port.getString());
        } catch (NumberFormatException e) {
            config.port = 5222;
        }
        config.resource = resource.getString();
        config.caching = (caching.getSelectedIndex() == 0);
        config.save();
    }
    
    //======================================================================
    
    public void itemStateChanged(javax.microedition.lcdui.Item item) 
    {
        if (item == hostChoice)
        {
            switch (hostChoice.getSelectedIndex())
            {
                case 0:
                    if (hostDisp)
                    {
                        delete(hostPos);
                        String text = jid.getString();
                        int n = text.indexOf('@');
                        if (n >= 0)
                            host.setString(text.substring(n+1));
                        hostDisp = false;
                    }
                    break;
                case 1:
                    if (!hostDisp)
                    {
                        insert(hostPos, host);
                        String text = jid.getString();
                        int n = text.indexOf('@');
                        if (n >= 0)
                            host.setString(text.substring(n+1));
                        hostDisp = true;
                    }
                    break;
            }
        }
        else if (item == savePassword)
        {
            switch (savePassword.getSelectedIndex())
            {
                case 0:
                    if (pwdDisp)
                    {
                        delete(pwdPos);
                        pwdDisp = false;
                        hostPos--;
                    }
                    break;
                case 1:
                    if (!pwdDisp)
                    {
                        insert(pwdPos, password);
                        pwdDisp = true;
                        hostPos++;
                    }
                    break;
            }
        }
    }
    
}
