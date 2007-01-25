/*
 * TransferAcceptFile.java
 *
 * Created on 29 Октябрь 2006 г., 1:20
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package io.file.transfer;

import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.TextFieldCombo;

/**
 *
 * @author Evg_S
 */
public class TransferAcceptFile 
        implements CommandListener, BrowserListener
{
    private Display display;
    private Displayable parentView;
    
    Form f;
    TransferTask t;
    TextField fileName;
    TextField path;
    
    Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    Command cmdDecline=new Command(SR.MS_CANCEL, Command.CANCEL, 90);
    Command cmdBack=new Command(SR.MS_BACK, Command.BACK, 99);
    Command cmdPath=new Command(SR.MS_PATH, Command.SCREEN, 2);
    /** Creates a new instance of TransferAcceptFile */
    public TransferAcceptFile(Display display, TransferTask transferTask) {
        this.display=display;
        parentView=display.getCurrent();
        t=transferTask;
        
        f=new Form(SR.MS_ACCEPT_FILE);
        fileName=new TextField(SR.MS_FILE, t.fileName, 32, TextField.ANY);
        path=new TextFieldCombo(SR.MS_SAVE_TO, t.filePath, 200, TextField.ANY, "recvPath", display);
        
        f.append(new StringItem(SR.MS_SENDER, t.jid));
        f.append(fileName);
        f.append(new StringItem(SR.MS_SIZE, String.valueOf(t.fileSize)));
        f.append(path);
        f.append(new StringItem(SR.MS_DESCRIPTION, t.description));
        
        f.addCommand(cmdOk);
        f.addCommand(cmdPath);
        f.addCommand(cmdDecline);
        f.addCommand(cmdBack);
        
        f.setCommandListener(this);
        display.setCurrent(f);
    }

    public void BrowserFilePathNotify(String pathSelected) { path.setString(pathSelected); }

    public void commandAction(Command c, Displayable d) {
        if (c==cmdDecline) { t.decline(); }
        if (c==cmdPath) { new Browser(path.getString(), display, this, true); return; }
        if (c==cmdOk) {
            t.fileName=fileName.getString();
            t.filePath=path.getString();
            t.accept();
        }
        
        display.setCurrent(parentView);
    }
}
