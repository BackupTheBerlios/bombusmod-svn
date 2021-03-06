/*
 * AccountPicker.java
 *
 * Created on 19.03.2005, 23:26
 *
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
import images.RosterIcons;
import io.NvStorage;
import locale.SR;
import midlet.BombusSmall;
import ui.*;
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import ui.controls.NumberField;


/**
 *
 * @author Eugene Stahov
 */
public class AccountSelect 
        extends VirtualList 
        implements CommandListener{

    Vector accountList;
    int activeAccount;
    
    Command cmdLogin=new Command(SR.MS_SELLOGIN, Command.OK,1); //locale
    Command cmdSelect=new Command("Select (no login)", Command.SCREEN,2); //locale
    Command cmdAdd=new Command(SR.MS_NEW_ACCOUNT, Command.SCREEN,3); //locale
    Command cmdEdit=new Command(SR.MS_EDIT,Command.ITEM,3); //locale
    Command cmdDel=new Command(SR.MS_DELETE,Command.ITEM,4); //locale
    Command cmdCancel=new Command(SR.MS_BACK,Command.BACK,99); //locale
    Command cmdQuit=new Command("Quit",Command.SCREEN,10); //locale
    
    /** Creates a new instance of AccountPicker */
    public AccountSelect(Display display, boolean enableQuit) {
        super();
        //this.display=display;

        setTitleItem(new Title("Accounts")); //locale
        
        accountList=new Vector();
        Account a;
        
        int index=0;
        activeAccount=Config.getInstance().accountIndex;
        do {
            a=Account.createFromStorage(index);
            if (a!=null) {
                accountList.addElement(a);
                a.active=(activeAccount==index);
                index++;
             }
       } while (a!=null);
        if (accountList.isEmpty()) {
            a=Account.createFromJad();
            if (a!=null) {
                //a.updateJidCache();
                accountList.addElement(a);
                rmsUpdate();
            }
        }
        moveCursorTo(activeAccount, true);
        attachDisplay(display);
        addCommand(cmdAdd);
        
        if (enableQuit) addCommand(cmdQuit);
        
        commandState();
        setCommandListener(this);
    }
    
    void commandState(){
        if (accountList.isEmpty()) {
            removeCommand(cmdEdit);
            removeCommand(cmdDel);
            removeCommand(cmdSelect);
            removeCommand(cmdLogin);
            removeCommand(cmdCancel);
        } else {
            addCommand(cmdEdit);
            addCommand(cmdDel);
            addCommand(cmdLogin);
            addCommand(cmdSelect);
            if (activeAccount>=0)
                addCommand(cmdCancel);  // нельзя выйти без активного аккаунта
        }
    }

    public VirtualElement getItemRef(int Index) { return (VirtualElement)accountList.elementAt(Index); }
    protected int getItemCount() { return accountList.size();  }

    public void commandAction(Command c, Displayable d){
        if (c==cmdQuit) {
            destroyView();
            BombusSmall.getInstance().notifyDestroyed();
        }
        if (c==cmdCancel) {
            destroyView();
            //Account.launchAccount();
            //StaticData.getInstance().account_index=0;
        }
        if (c==cmdLogin) switchAccount(true);
        if (c==cmdSelect) switchAccount(false);
        if (c==cmdEdit) new AccountForm(this, display,(Account)getFocusedObject());
        if (c==cmdAdd) {
            new AccountForm(this, display, null);
        }
        if (c==cmdDel) {
            {
                Config cf=Config.getInstance();
                if (cf.accountIndex>cursor) cf.accountIndex--;
                cf.saveToStorage();
            }
            accountList.removeElement(getFocusedObject());
            rmsUpdate();
            moveCursorHome();
            commandState();
            redraw();
        }
        
    }
    
    private void switchAccount(boolean login){
        if (!login) parentView=StaticData.getInstance().roster;
        destroyView();
	Config cf=Config.getInstance();
        cf.accountIndex=cursor;
        cf.saveToStorage();
        Account.loadAccount(login);
    }
    
    public void eventOk(){ switchAccount(true); }
    
    void rmsUpdate(){
        DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
        for (int i=0;i<accountList.size();i++) 
            ((Account)accountList.elementAt(i)).saveToDataOutputStream(outputStream);
        NvStorage.writeFileRecord(outputStream, Account.storage, 0, true);
    }

}
