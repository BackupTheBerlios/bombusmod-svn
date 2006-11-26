/*
 * vCardForm.java
 *
 * Created on 3 Jrnz,hm 2005 г., 0:37
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package vcard;
import Client.StaticData;
//#if (FILE_IO)
import com.siemens.mp.io.File;
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//#endif

//#if (!MIDP1)
import images.camera.*;
//#endif

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
    
    private String photoType;

    private int st=-1;
    
    private String phototype="jpg";
    
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
        photoType=vcard.getPhotoType();
        
        photo=vcard.getPhoto();
        setPhoto();
        
        for (int index=0; index<vcard.getCount(); index++) {
            String data=vcard.getVCardData(index);
            String name=(String)VCard.vCardLabels.elementAt(index);
            Item item=null;
            if (editable) {
                item=new TextField(name, data, 200, TextField.ANY);
                items.addElement(item);
            } else if (data!=null) {
                item=new StringItem (name, data);
                item.addCommand(cmdCopy);
                item.setItemCommandListener(this);
            }
            if (item!=null) {
                f.append(item);
//#if !(MIDP1)
                f.append(new Spacer(256, 3));
//#else
//--                f.append("\n");
//#endif
            }
        }
        
        f.append("\n\n[end of vCard]");
        
        f.addCommand(cmdCancel);
        f.addCommand(cmdRefresh);
        if (editable) {
            f.addCommand(cmdPublish);
//#if (FILE_IO)
            f.addCommand(cmdPhoto);
//#endif
//#if !(MIDP1)
            String cameraAvailable=System.getProperty("supports.video.capture");
            if (cameraAvailable!=null) if (cameraAvailable.startsWith("true"))
                f.addCommand(cmdCamera);
//#endif
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

    if (c==cmdDelPhoto) {photo=null; setPhoto();}
        
        if (c!=cmdPublish) return;
        
        vcard.setPhoto(photo);
        
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
        System.out.println("VCard sent");
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
                    setPhoto();
                } catch (Exception e) {e.printStackTrace();}
            }
            if (st==2 & photo!=null) {
                if (photoType!=null) {
                        int slashPos=photoType.indexOf('/');
                        if (slashPos>-1) {
                            phototype=photoType.substring(slashPos+1).toLowerCase();
                            if (phototype=="jpeg") phototype="jpg";
                        }
                }
                
                //try {
                //    FileIO f=FileIO.createConnection(pathSelected+"photo_"+vcard.getNickName()+"_"+getDate()+"."+phototype);
                //    f.Write(photo);
                //} catch (Exception e) {}
                file=FileIO.createConnection(pathSelected+"photo_"+vcard.getNickName()+"_"+getDate()+"."+phototype);
                try {
                    os=file.openOutputStream();
                    writeFile(photo);
                    os.close();
                    file.close();
                } catch (IOException ex) {
                    try {
                        file.close();
                    } catch (IOException ex2) {
                        ex2.printStackTrace();
                    }
                    ex.printStackTrace();
                }
            }
        }
    }
    
    void writeFile(byte b[]){
        try {
            os.write(b);
            filePos+=b.length;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
//#endif

//#if (!MIDP1)
    public void cameraImageNotify(byte[] capturedPhoto) {
        photo=capturedPhoto;
        setPhoto();
    }
//#endif

    private void setPhoto() {
        if (photo==null) return;
        String size=String.valueOf(photo.length)+" bytes";
        Item photoItem;
//#if !(MIDP1)
        try {
            Image photoImg=Image.createImage(photo, 0, photo.length);
            photoItem=new ImageItem(size, photoImg, 0, null);
        } catch (Exception e) { photoItem=new StringItem(size, "[Unsupported format]"); }
        f.set(photoIndex, photoItem);
//#endif
    }
    private String getDate() {
        long dateGmt=Time.localTime();
        return Time.dayString(dateGmt); 
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
        clipboard.s=string;
    }
}
