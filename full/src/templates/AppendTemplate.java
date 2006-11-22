/*
 * AppendTemplate.java
 *
 */

package templates;

import Messages.MessageList;
import locale.SR;
import ui.*;
import Client.*;
import java.util.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author EvgS
 */
public class AppendTemplate         
        extends MessageList 
        implements CommandListener{

    
    Command cmdSelect=new Command(SR.MS_APPEND, Command.OK, 1);
    Command cmdNew=new Command("New", Command.SCREEN, 1);
    Command cmdDelete=new Command(SR.MS_DELETE , Command.SCREEN, 2);

    TemplateContainer template=new TemplateContainer();
    TextBox target;
    
    /** Creates a new instance of AccountPicker */
    public AppendTemplate(Display display, TextBox target) {
	super (display);
	this.target=target;
	if (target!=null) {
	    addCommand(cmdSelect);
	}
	addCommand(cmdNew);
	addCommand(cmdDelete);
	addCommand(cmdBack);

        focusedItem(0);
	
	setCommandListener(this);
	
	Title title=new Title(SR.MS_SELECT);
	title.addRAlign();
	title.addElement(null);
	title.addElement(SR.MS_FREE);
        setTitleItem(title);
    }
     protected void beginPaint() {
	getTitleItem().setElementAt(String.valueOf(template.freeSpace()),2);
    }
    
    public int getItemCount() {
	return template.size();
    }
    
    public Msg getMessage(int index) {
	return template.msg(index);
    }

    public void commandAction(Command c, Displayable d) {
	if (c==cmdBack) {
	    destroyView();
	    //return;
	}
	if (c==cmdDelete) {
	    template.delete(cursor);
	    messages=new Vector();
	    redraw();
	}
	if (c==cmdSelect) { pasteData(); }
        if (c==cmdNew) {
            try {
                new NewTemplate(display);
                messages=new Vector();
                redraw();
            } catch (Exception e) {/*no messages*/}
        }
    }
    
    private void pasteData() {
	if (target==null) return;
	Msg m=getMessage(cursor);
	if (m==null) return;
	String data;
	data=m.getBody();
	try {
	    int paste=target.getMaxSize()-target.size();
	    if (paste>data.length()) paste=data.length();
	    target.insert(data.substring(0,paste), target.size());
	} catch (Exception e) {
	    e.printStackTrace();
	}
	destroyView();
    }
    
    public void keyGreen() { pasteData(); }
    
    public void keyPressed(int keyCode) {
        if (keyCode==FIRE) {
            pasteData();
        } else super.keyPressed(keyCode);
    }
    
    public void focusedItem(int index) {
	if (target==null) return;
    }
    
    public void destroyView(){
	super.destroyView();
	template.close();
    }
}