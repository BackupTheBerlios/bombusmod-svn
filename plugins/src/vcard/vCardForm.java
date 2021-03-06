/*
 * vCardForm.java
 *
 * Created on 3.10.2005, 0:37
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
 */

package vcard;
import Client.StaticData;
//#if (FILE_IO)
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//#endif

import images.camera.*;

import java.util.*;
import javax.microedition.lcdui.*;
import locale.SR;

import ui.Time;
import util.ClipBoard;

/**
 *
 * @author EvgS
 */
public class vCardForm 
        implements CommandListener, Runnable, ItemCommandListener
//#if (FILE_IO)
        , BrowserListener
//#endif
        , CameraImageListener
{
    
    private Display display;
    private Displayable parentView;
    
    protected Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    protected Command cmdPublish=new Command(SR.MS_PUBLISH, Command.OK /*Command.SCREEN*/, 1);
    protected Command cmdRefresh=new Command(SR.MS_REFRESH, Command.SCREEN, 2);
//#if (FILE_IO)
    protected Command cmdPhoto=new Command(SR.MS_LOAD_PHOTO, Command.SCREEN,3);
    protected Command cmdSavePhoto=new Command(SR.MS_SAVE_PHOTO, Command.SCREEN,4); //locale
//#endif
    protected Command cmdDelPhoto=new Command(SR.MS_CLEAR_PHOTO, Command.SCREEN,4);
    protected Command cmdCamera=new Command(SR.MS_CAMERA, Command.SCREEN,5);
    protected Command cmdClear = new Command(SR.MS_CLEAR_PHOTO, Command.SCREEN, 6);
    protected Command cmdCopy = new Command(SR.MS_COPY, Command.SCREEN, 7);    
    
    private Form f;
    private Vector items=new Vector();
    private VCard vcard;
    
    private byte[] photo;
    private int photoIndex;
    private String photoType=null;
    private int st=-1;

    private ClipBoard clipboard;  // The clipboard class
//#if FILE_IO    
    int fileSize;
    private int filePos;
    String filePath;
    private FileIO file;
    private OutputStream os;
//#endif
    /** Creates a new instance of vCardForm */
    public vCardForm(Display display, VCard vcard, boolean editable) {
        this.display=display;
        parentView=display.getCurrent();
        
        this.vcard=vcard;
        
        f=new Form(SR.MS_VCARD);
        f.append(vcard.getJid());
        
        if (vcard.isEmpty() && !editable) 
            f.append("\n[no vCard available]"); 
        else { 
            photoIndex=f.append("[]");
             
            photo=vcard.getPhoto();
            setPhoto();
        }
        
        for (int index=0; index<vcard.getCount(); index++) {
            String data=vcard.getVCardData(index);
            String name=(String)VCard.vCardLabels.elementAt(index);
            Item item=null;
            if (editable) {
                //truncating large string
                if (data!=null) {
                    int len=data.length();
                    if (data.length()>500)
                        data=data.substring(0, 494)+"<...>";
                } 
                
                item=new TextField(name, data, 500, TextField.ANY);
                items.addElement(item);
            } else if (data!=null) {
                item=new StringItem (name, data);
                item.addCommand(cmdCopy);
                item.setItemCommandListener(this);
            }
            if (item!=null) {
                f.append(item);
                f.append(new Spacer(256, 3));
            }
        }
        
        f.addCommand(cmdCancel);
        f.addCommand(cmdRefresh);
        if (editable) {
            f.addCommand(cmdPublish);
//#if (FILE_IO)
            f.addCommand(cmdPhoto);
//#endif
            String cameraAvailable=System.getProperty("supports.video.capture");
            if (cameraAvailable!=null) if (cameraAvailable.startsWith("true"))
                f.addCommand(cmdCamera);
            f.addCommand(cmdDelPhoto);
        }
        if (!editable) {
            f.addCommand(cmdClear);
        }
//#if (FILE_IO)
        f.addCommand(cmdSavePhoto);
//#endif
        f.setCommandListener(this);
        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdCancel) destroyView();
        if (c==cmdRefresh) {
            VCard.request(vcard.getJid(), vcard.getId().substring(5));
            destroyView();
        }
        
        if (c==cmdClear) {
            vcard.photo=null;
            destroyView();
        }
        
//#if (FILE_IO)
        if (c==cmdPhoto) {
            st=1;
            new Browser(null, display, this, false);
        }
        if (c==cmdSavePhoto) {
            st=2;
            new Browser(null, display, this, true);
        }
//#endif

//#if (!MIDP1)
        if (c==cmdCamera)
            new CameraImage(display, this);
//#endif

        if (c==cmdDelPhoto) {
            photo=null; 
            setPhoto();
        }
        
        if (c!=cmdPublish) return;
        
        vcard.setPhoto(photo);
        vcard.setPhotoType(getPhotoMIMEType());
        for (int index=0; index<vcard.getCount(); index++) {
            String field=((TextField)items.elementAt(index)).getString();
            if (field.length()==0) field=null;
            vcard.setVCardData(index, field);
        }
        //System.out.println(vcard.constructVCard().toString());
        new Thread(this).start();
        destroyView();
    }
    
    private void destroyView() {
        display.setCurrent(parentView);
    }

    public void run() {
        StaticData.getInstance().roster.theStream.send(vcard.constructVCard());
        //System.out.println("VCard sent");
    }

//#if (FILE_IO)
    public void BrowserFilePathNotify(String pathSelected) {
        if (st>0) {
            if (st==1) {
                try {
                    FileIO f=FileIO.createConnection(pathSelected);
                    InputStream is=f.openInputStream();
                    byte[] b=new byte[(int)f.fileSize()];
                    //System.out.println(f.fileSize());
                    is.read(b);
                    is.close();
                    f.close();
                    photo=b;
                    vcard.setPhotoType(getPhotoMIMEType());
                    setPhoto();
                } catch (Exception e) {
                    System.out.println("error on load");
                }
            }
            if (st==2 & photo!=null) {
                photoType=getPhotoMIMEType();
                if (photoType!=null) {
                        int slashPos=photoType.indexOf('/');
                        if (slashPos>-1) {
                            photoType=photoType.substring(slashPos+1).toLowerCase();
                            if (photoType=="jpeg") photoType="jpg";
                        }
                }

                file=FileIO.createConnection(pathSelected+"photo_"+vcard.getNickName()+"_"+getDate()+"."+photoType);
                try {
                    os=file.openOutputStream();
                    writeFile(photo);
                    os.close();
                    file.close();
                } catch (IOException ex) {
                    try {
                        file.close();
                    } catch (IOException ex2) { }
                }
            }
        }
    }
    
    void writeFile(byte b[]){
        try {
            os.write(b);
            filePos+=b.length;
        } catch (IOException ex) { }
    }
//#endif

    public void cameraImageNotify(byte[] capturedPhoto) {
        photo=capturedPhoto;
        setPhoto();
    }

     private void setPhoto() {
        Item photoItem=new StringItem(null, "[no photo available]");
        if (photo!=null) {
            String size=String.valueOf(photo.length)+" bytes";
            try {
                Image photoImg=Image.createImage(photo, 0, photo.length);
                photoItem=new ImageItem(size, photoImg, 0, null);
            } catch (Exception e) { 
                photoItem=new StringItem(size, "[Unsupported format]");
            }
        }
         f.set(photoIndex, photoItem);
     }
	 
    private String getDate() {
        long dateGmt=Time.utcTimeMillis();
        return Time.dayLocalString(dateGmt).trim(); 
    }

    public void commandAction(Command command, Item item) {
        if (command == cmdCopy)
        {
          try {
            String text=((StringItem) item).getText();
            CopyText(text);
          } catch (Exception e) {/*no messages*/}
        }
    }

    private void CopyText(String string) {
        clipboard.setClipBoard(string);
    }
    
	
    public String getPhotoMIMEType() {
        try {
            if (photo[0]==0x89 &&
                photo[1]==0x50 &&
                photo[2]==0x4E &&
                photo[3]==0x47)
            {
                System.out.println("image/png");
                return "image/png";
            }
            
            if (photo[0]==(byte)0xff &&
                photo[1]==(byte)0xd8 &&
                photo[6]==(byte)'J' &&
                photo[7]==(byte)'F' &&
                photo[8]==(byte)'I' &&
                photo[9]==(byte)'F')
            {
                System.out.println("image/jpeg");
                return "image/jpeg";
            }
            
            if (photo[1]==(byte)'G' &&
                photo[2]==(byte)'I' &&
                photo[3]==(byte)'F')
            {
                System.out.println("image/gif");
                return "image/gif";
            }
            
            if (photo[1]==(byte)'B' &&
                photo[2]==(byte)'M')
            {
                System.out.println("image/x-ms-bmp");
                return "image/x-ms-bmp";
            }
        } catch (Exception e) {
            System.out.println("unknown mime");
        }
        return null;
    }
}
