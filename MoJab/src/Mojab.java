import java.lang.*;
import java.util.*;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// A first MIDlet with simple text and a few commands.
public class Mojab extends MIDlet implements CommandListener 
{
    //All the commands
    private Command exitCommand; 
    private Command accountCommand;
    //private Command optionsCommand;
    private Command connectCommand;

    private Command discCommand;
    private Command sessOkCommand;
    
    private Command sendCommand;
    private Command sortCommand;
    private Command refreshCommand;
    //private Command statusCommand;
    //private Command addCommand;
    //private Command editCommand;
    
    private Command msgOkCommand;
    private Command msgReplyCommand;
    private Command sendOkCommand;
    private Command sendCancelCommand;
    
    //private Command configOkCommand;
    //private Command configCancelCommand;
    private Command accOkCommand;
    private Command accCancelCommand;

    //The display for this MIDlet
    private Display display;

    //Screens
    private Displayable introScreen;
    
    //Private
    private Config config = null;
    private Session session = null;
    private Roster roster = null;
    private AccountForm account = null;
    private TextBox sender = null;

    private Vector incom;     //incomming messages
    private Message curmsg;   //message currently being composed
    
    public Mojab() 
    {
        display = Display.getDisplay(this);
        exitCommand = new Command("Exit", Command.EXIT, 1);
        connectCommand = new Command("Connect", Command.SCREEN, 2);
        //optionsCommand = new Command("Options", Command.SCREEN, 3);
        accountCommand = new Command("Account setup", Command.SCREEN, 3);

        discCommand = new Command("Disconnect",  Command.STOP, 3);
        sessOkCommand = new Command("OK", Command.OK, 2);
        
        sendCommand = new Command("Send message", Command.ITEM,  1);
        sortCommand = new Command("Sort roster", Command.SCREEN,  2);
        refreshCommand = new Command("Refresh roster", Command.SCREEN, 2);
        //statusCommand = new Command("Change status", Command.SCREEN, 2);        
        //addCommand = new Command("Add contact", Command.SCREEN, 2);        
        //editCommand = new Command("Edit contact", Command.ITEM, 2);
        
        msgOkCommand = new Command("OK", Command.OK, 1);
        msgReplyCommand = new Command("Reply", Command.SCREEN, 1);
        sendOkCommand = new Command("OK", Command.OK, 1);
        sendCancelCommand = new Command("Cancel", Command.CANCEL, 1);

        //configOkCommand = new Command("OK", Command.OK, 1);
        //configCancelCommand = new Command("Cancel", Command.CANCEL, 1);
        accOkCommand = new Command("OK", Command.OK, 1);
        accCancelCommand = new Command("Cancel", Command.CANCEL, 1);
        
        incom = new Vector();
    }

    public void startApp() 
    {
        introScreen = uvodni();
        display.setCurrent(introScreen);
        config = new Config();
    }

    public void pauseApp()
    {
    }

    public void destroyApp(boolean unconditional)
    {
        if (session != null)
            session.close();
        loggedin = false;
    }

    public void commandAction(Command c, Displayable s) 
    {
        //Main
        if (c == exitCommand) 
        {
           destroyApp(false);
           notifyDestroyed();
        }
        else if (c == connectCommand) 
        {
            session = new Session(this);
            session.addCommand(discCommand);
            //session.addCommand(optionsCommand);
            session.setCommandListener(this);
            display.setCurrent(session);
            //session.connect("jabber.brno.czf", "localhost", 5555);
            //session.connect("jabber.cz", "jabber.cz", 5222);
            session.connect(config.getHost(), config.getRealHost(), config.port);
        }
        /*else if (c == optionsCommand)
        {
        }*/
        //Session/Roster
        else if (c == discCommand)
        {
            if (roster != null)
            {
                if (config.caching)
                    roster.save();
                else
                    roster.deleteStore();
                roster = null;
            }
            if (session != null)
                session.close();
            display.setCurrent(introScreen);
            session = null;
            loggedin = false;
        }
        else if (c == sessOkCommand)
        {
            if (!loggedin || roster == null)
            {
                if (session != null)
                    session.close();
                display.setCurrent(introScreen);
                session = null;
            }
            else
            {
                display.setCurrent(roster);
            }
        }
        else if (c == sendCommand || c == roster.SELECT_COMMAND)
        {
            curmsg = new Message(roster.currentJID());
            sender = dispSend();
            display.setCurrent(sender);
        }
        else if (c == sortCommand)
        {
            session.pause();
            roster.sort();
            session.resume();
        }
        else if (c == refreshCommand)
        {
            session.pause();
            roster.clear();
            session.getBuddyList();
            session.presence("unavailable", "", "");
            session.presence("", "", "Online");
            session.resume();
        }
        //Message view
        else if (c == msgOkCommand)
        {
            incom.removeElementAt(0);
            displayNext();
        }
        else if (c == msgReplyCommand)
        {
            Message inmsg = (Message) incom.elementAt(0);
            curmsg = new Message(inmsg.getJID());
            sender = dispSend();
            display.setCurrent(sender);
        }
        //Message send
        else if (c == sendOkCommand)
        {
            curmsg.setText(sender.getString());
            session.sendMessage(curmsg);
            sender = null;
            curmsg = null;
            displayNext();
        }
        else if (c == sendCancelCommand)
        {
            sender = null;
            displayNext();
        }        
        //Account settings
        else if (c == accountCommand)
        {
            account = dispAccount();
            display.setCurrent(account);
        }
        else if (c == accOkCommand)
        {
            account.save();
            display.setCurrent(introScreen);
            account = null;
        }
        else if (c == accCancelCommand)
        {
            display.setCurrent(introScreen);
            account = null;
        }
    }

    //============================ Callbacks from Session ======================

    private boolean loggedin = false;
    
    public void logic(int status)
    {
        switch (status)
        {
            case Session.S_ERROR: //On error display the session info
                display.setCurrent(session);
                break;
            case Session.S_CONN: //If connection ok
                session.login(config.getUser(),  config.password, config.resource);
                break;
            case Session.S_NORM: //Switched to normal state
                if (!loggedin) //after successfull login
                {
                    loggedin = true;
                    //Create the roster
                    roster = new Roster();
                    roster.setCommandListener(this);
                    roster.addCommand(sendCommand);
                    roster.addCommand(sortCommand);
                    roster.addCommand(refreshCommand);
                    /*roster.addCommand(statusCommand);
                    roster.addCommand(addCommand);
                    roster.addCommand(editCommand);*/
                    roster.addCommand(discCommand);
                    //Add commands for error ack to session
                    session.addCommand(sessOkCommand);
                    //Display the list
                    if (config.caching)
                    {
                        if (!roster.load())
                            session.getBuddyList();
                    }
                    else
                        session.getBuddyList();
                    //Set our status
                    session.presence("", "", "Online");
                    display.setCurrent(roster);
                }
                break;
        }        
    }
    
    public void clearRoster()
    {
        if (roster != null)
            roster.clear();
    }
    
    public void addRosterItem(String name, String jid)
    {
        if (roster != null)
            roster.addContact(name, jid);
    }
    
    public void incommingMessage(Message msg)
    {
        incom.addElement(msg);
        Screen current = (Screen) display.getCurrent();
        if (current == roster)
            displayNext();
        else
        {
            String title = current.getTitle();
            if (title.charAt(0) == '(')
                title = title.substring(title.indexOf(' ') + 1);
            current.setTitle("(" + (incom.size() - 1) + ") " + title);
        }
    }
    
    public void statusChanged(String jid, int status)
    {
        roster.setStatus(jid, status);
    }
    
    //================================= Tools =================================

    private void displayNext()
    {
        if (incom.size() > 0)
        {
            Message msg = (Message) incom.firstElement();
            display.setCurrent(dispMessage(msg.getJID(), msg.getText()));
        }
        else
            display.setCurrent(roster);            
    }
    
    //=========================== Screen constructors =========================

    private Displayable uvodni()
    {
        Form t = new Form("moJab");
        t.append("Version 0.2 (tiny)");
        t.append("http://mojab.sf.net/");
        t.addCommand(exitCommand);
        t.addCommand(connectCommand);
        //t.addCommand(optionsCommand);
        t.addCommand(accountCommand);
        t.setCommandListener(this);
        return t;        
    }
    
    private Displayable dispMessage(String from, String text)
    {
        String title = roster.findNameOf(from);
        if (title == null) title = from;
        if (incom.size() > 1)
            title = "(" + (incom.size()-1) + ") " + from;
        Form t = new Form(title);
        t.append(text);
        t.addCommand(msgOkCommand);
        t.addCommand(msgReplyCommand);
        t.setCommandListener(this);
        return t;
    }
    
    /*private Displayable dispConfig()
    {
        ConfigForm t = new ConfigForm();
        t.addCommand(configOkCommand);
        t.addCommand(configCancelCommand);
        t.setCommandListener(this);
        return t;
    }*/

    private AccountForm dispAccount()
    {
        AccountForm t = new AccountForm(config);
        t.addCommand(accOkCommand);
        t.addCommand(accCancelCommand);
        t.setCommandListener(this);
        return t;
    }
    
    private TextBox dispSend()
    {
        String title = roster.findNameOf(curmsg.getJID());
        if (title == null) title = curmsg.getJID();
        TextBox t = new TextBox("To: "+title, "", 1024, 0);
        t.addCommand(sendOkCommand);
        t.addCommand(sendCancelCommand);
        t.setCommandListener(this);
        return t;
    }

}
