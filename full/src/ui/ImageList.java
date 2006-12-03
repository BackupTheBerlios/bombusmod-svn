/*
 * ImageListC.java
 *
 * Created on 31 Январь 2005 г., 0:06
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

/**
 *
 * @author Eugene Stahov
 */

package ui;
/*
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
*/
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

public class ImageList {

    //public final static int ICON_ASK_INDEX=0x06;
    
    public Image skin_png;
    
    protected Image resImage;
    protected int height;

    protected int width;
    //int count,total;
    /** Creates a new instance of ImageListC */
    public ImageList(String resource, int rows, int columns) {
        try {
            //resImage = SkinFile("/images/skin"); //res.skin
            resImage = Image.createImage(resource);
            width = resImage.getWidth()/columns;
            height = (rows==0)? width : resImage.getHeight()/rows;
        } catch (Exception e) { 
            System.out.print("Can't load ");
            System.out.println(resource);
        }
    }
    
    public void drawImage(Graphics g, int index, int x, int y){
        int ho=g.getClipHeight();
        int wo=g.getClipWidth();
        int xo=g.getClipX();
        int yo=g.getClipY();
        
        int iy=y-height*(int)(index>>4);
        int ix=x-width*(index&0x0f);
        g.clipRect(x,y, width,height);
        try {
            g.drawImage(resImage,ix,iy,Graphics.TOP|Graphics.LEFT);
        } catch (Exception e) {}
        g.setClip(xo,yo, wo, ho);
    };
    
    public int getHeight() {return height;}
    public int getWidth() {return width;}
    //public int getCount() {return total;}
    
    /*  //res.skin
	public Image SkinFile( String dataUrl ) throws IOException
	{
		InputStream plainIn = getClass().getResourceAsStream( dataUrl );
                
		if (plainIn == null) {
			throw new IllegalArgumentException("Unable to open resource [" + dataUrl + "]: resource not found: does it start with \"/\"?");
		}
		DataInputStream in = new DataInputStream( plainIn );
		try {
                        skin_png = javax.microedition.lcdui.Image.createImage( in );
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException( e.toString() );
		} finally {
			try {
				in.close();
			} catch (Exception e) {}
		}
                return skin_png;
	} 
    */
}
