/*
 * HistoryConfig.java
 *
 * Created on 18 ���� 2007 �., 15:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package History;

import Client.Config;
import Client.StaticData;
//#if FILE_IO
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.TextField;
import locale.SR;

public class HistoryConfig implements
	CommandListener 
//#if FILE_IO
	,ItemCommandListener
        , BrowserListener
//#endif
{
    private Display display;
    private Displayable parentView;

    Form f;
    ChoiceGroup message;
//#if FILE_IO
    ChoiceGroup history;
    TextField historyFolder;

    Command cmdSetHistFolder=new Command(SR.MS_SELECT_HISTORY_FOLDER, Command.ITEM,11);
//#endif
    
    Command cmdOk=new Command(SR.MS_OK,Command.OK,1);    
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    
    Config cf;
    boolean mv[];
    boolean his[];
    
    /** Creates a new instance of ConfigForm */
    public HistoryConfig(Display display) {
        this.display=display;
        parentView=display.getCurrent();
        
        cf=Config.getInstance();
        
        f=new Form(SR.MS_HISTORY_OPTIONS);
        
        message=new ChoiceGroup(SR.MS_MESSAGES, Choice.MULTIPLE);
        message.append(SR.MS_STORE_PRESENCE,null);        
//#if LAST_MESSAGES
//#         message.append("Last messages", null);
//#endif

        boolean mv[]={
            cf.storeConfPresence
//#if LAST_MESSAGES
//#             ,cf.lastMessages
//#endif
        };
        this.mv=mv;
        message.setSelectedFlags(mv);
        f.append(message);
        
//#if FILE_IO
        history=new ChoiceGroup(SR.MS_HISTORY, Choice.MULTIPLE); //locale
        history.append(SR.MS_SAVE_HISTORY, null); //locale
        history.append(SR.MS_SAVE_PRESENCES,null);    //locale     
        history.append(SR.MS_SAVE_HISTORY_CONF, null); //locale
        history.append(SR.MS_SAVE_PRESENCES_CONF, null); //locale
        history.append(SR.MS_1251_CORRECTION, null); //locale
        history.append(SR.MS_1251_TRANSLITERATE_FILENAMES, null); //locale
        
        boolean his[]={
            cf.msgLog,
            cf.msgLogPresence,
            cf.msgLogConf,
            cf.msgLogConfPresence,
            cf.cp1251,
            cf.transliterateFilenames
        };
        this.his=his;
        
        history.setSelectedFlags(his);
        f.append(history);
        
        historyFolder=new TextField(SR.MS_HISTORY_FOLDER, null, 200, TextField.ANY);
        historyFolder.setString(cf.msgPath);
        historyFolder.addCommand(cmdSetHistFolder);
        f.append(historyFolder);
        historyFolder.setItemCommandListener(this);
//#endif

        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        
        f.setCommandListener(this);
       
        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            message.getSelectedFlags(mv);
//#if FILE_IO
            history.getSelectedFlags(his);
//#endif

            int mvctr=0;
            cf.storeConfPresence=mv[mvctr++];
//#if LAST_MESSAGES
//#             cf.lastMessages=mv[mvctr++];
//#endif
	    
//#if FILE_IO
            cf.msgLog=his[0];
            cf.msgLogPresence=his[1];
            cf.msgLogConf=his[2];
            cf.msgLogConfPresence=his[3];
            cf.cp1251=his[4];
            cf.transliterateFilenames=his[5];
            
            cf.msgPath=historyFolder.getString();
//#endif             

            StaticData.getInstance().roster.reEnumRoster();
            destroyView();
        }
        if (c==cmdCancel) destroyView();
    }
    
//#if FILE_IO
    public void commandAction(Command command, Item item) {

        if (command==cmdSetHistFolder) {
            new Browser(null, display, this, true);
        }
    }
//#endif
    
    public void destroyView(){
        if (display!=null)
            display.setCurrent(parentView);
    }

//#if FILE_IO
    public void BrowserFilePathNotify(String pathSelected) {
        historyFolder.setString(pathSelected);
    }
//#endif
}
