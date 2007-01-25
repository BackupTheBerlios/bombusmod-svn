/*
 * ShowFile.java
 *
 * Created on 9 ќкт€брь 2006 г., 14:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package io.file.browse;

import Client.Msg;
import io.file.FileIO;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.*;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import locale.SR;

/**
 *
 * @author User
 */
public class ShowFile implements CommandListener{
    
    private Display display;
    private Displayable parentView;
    
    private Command back = new Command(SR.MS_BACK, Command.BACK, 2);
    private Command stop = new Command(SR.MS_STOP, Command.BACK, 3);

    private int len;

    private byte[] b;

    private Player pl;
    
    public ShowFile(Display display, String fileName, int type) {
        this.display=display;
        parentView=display.getCurrent();
        
        if (type==1) play(fileName);
        if (type==2) view(fileName);
        if (type==3) read(fileName);
    }
	private void load(String file) {
            try {
                FileIO f=FileIO.createConnection(file);
                InputStream is=f.openInputStream();
                len=(int)f.fileSize();
                b=new byte[len];

                is.read(b);
                is.close();
                f.close();
            } catch (Exception e) {}
        }
        
	private void view(String file) {
            load(file);
            Image img = Image.createImage(b, 0, len);

            Form form = new Form(file);
            form.append(new Spacer(10, 10));
            form.append(new ImageItem(null, img, ImageItem.LAYOUT_CENTER | ImageItem.LAYOUT_NEWLINE_BEFORE, "[image]"));

            form.addCommand(back);
            form.setCommandListener(this);
            display.setCurrent(form);
	}    
    
	private void read(String file) {
            load(file);
            TextBox tb = new TextBox(file+"("+len+" bytes)", null, len, TextField.ANY | TextField.UNEDITABLE);

            tb.addCommand(back);
            tb.setCommandListener(this);

            if (len > 0) tb.setString(new String(b, 0, len));

            tb.setCommandListener(this);
            display.setCurrent(tb);
	}    
    
	private void play(String file) {
            try {

                pl = Manager.createPlayer("file://" + file);
                pl.realize();
                pl.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (MediaException ex) {
                ex.printStackTrace();
            }

		Alert a = new Alert("Play", "Playing" + " " + file, null, null);
		a.addCommand(stop);
                a.addCommand(back);
		a.setCommandListener(this);
		display.setCurrent(a);
	}
    
    public void commandAction(Command c, Displayable d) {
        if (c==back) display.setCurrent(parentView);
        if (c==stop) {
		try {
                    pl.stop();
                    pl.close();
		} catch (Exception e) { }
        }
    }
}
