/*
 * InputBox.java
 *
 * Created on 2 Ноябрь 2006 г., 0:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextField;
import ui.Colors;
import ui.FontCache;

/**
 *
 * @author [AD]
 */
public class InputBox {
    
    protected Font font=FontCache.getMsgFont();
    private int height;
    private int width;

    private TextField nickField;
    
    public InputBox() {
    }

    public int getVHeight() {
        return font.getHeight();
    }

    public void drawItem(Graphics g, int i, boolean b) {
        g.setColor(Colors.BALLOON_BGND);
        g.fillRect(0, 0, width, height);
       
        //TextField nickField=null;
        //nickField=new TextField("text", "text", 32, TextField.ANY);
        g.setColor(Colors.BALLOON_INK);
        g.drawString("test", (width/2)+10, height/2, Graphics.TOP | Graphics.LEFT);
    }
}
/*


package ui;
import java.util.*;
import javax.microedition.lcdui.*;
import ui.VirtualElement;

public class ComplexString extends Vector implements VirtualElement{

    //private Vector v;
    public final static int IMAGE=0x00000000;
    public final static int COLOR=0x01000000;
    public final static int RALIGN=0x02000000;
    public final static int UNDERLINE=0x03000000;

    protected Font font=FontCache.getMsgFont();
    private int height;
    private int width;
    private ImageList imageList;
    private int colorBGnd=Colors.LIST_BGND;
    private int color=Colors.LIST_INK;
    

    public ComplexString() {
        super();
    }

    public ComplexString(ImageList imageList) {
        this();
        this.imageList=imageList;
    }

    private int imgHeight(){
        return (imageList==null)?0:imageList.getHeight();
    }
    private int imgWidth(){
        return (imageList==null)?0:imageList.getWidth();
    }
    
    public int getColor() {return color;}
    public int getColorBGnd() {return colorBGnd;}
    
    public void setColorBGnd(int color){ colorBGnd=color;}
    public void setColor(int color){ this.color=color;}
    
    public void onSelect(){};
    
    public void drawItem(Graphics g, int offset, boolean selected){
        //g.setColor(0);
        boolean ralign=false;
	boolean underline=false;
        
        int w=0;
        int dw;
        int imageYOfs=( getVHeight()-imgHeight() )>>1;
//#if ALCATEL_FONT
//#         int fontYOfs=(( getVHeight()-font.getHeight() )>>1) +1;
//#else
        int fontYOfs=(( getVHeight()-font.getHeight() )>>1);
//#endif
        int imgWidth=imgWidth();
        
        g.setFont(font);
        for (int index=0; index<elementCount;index++) {
            Object ob=elementData[index];
            if (ob!=null) {
                
                if (ob instanceof String ){
                    // string element
                    dw=font.stringWidth((String)ob);
                    if (ralign) w-=dw; 
                    g.drawString((String)ob,w,fontYOfs,Graphics.LEFT|Graphics.TOP);
		    if (underline) {
			int y=getVHeight()-1;
			g.drawLine(w, y, w+dw, y);
			underline=false;
		    }
                    if (!ralign) w+=dw;

                } else if ((ob instanceof Integer)) {
                    // image element or color
                    int i=((Integer)ob).intValue();
                    switch (i&0xff000000) {
                        case IMAGE:
                            if (imageList==null) break;
                            if (ralign) w-=imgWidth;
                            imageList.drawImage(g, ((Integer)ob).intValue(), w, imageYOfs);
                            if (!ralign) w+=imgWidth;
                            break;
                        case COLOR:
                            g.setColor(0xFFFFFF&i);
                            break;
                        case RALIGN:
                            ralign=true;
                            w=g.getClipWidth()-1;
			    break;
			case UNDERLINE:
			    underline=true;
			    break;
                    }
                }  else if (ob instanceof VirtualElement) { 
                    int clipw=g.getClipWidth(); 
                    int cliph=g.getClipHeight();
                    ((VirtualElement)ob).drawItem(g,0,false);
                    g.setClip(g.getTranslateX(), g.getTranslateY(), clipw, cliph);
                    //TODO: рисование не с нулевой позиции и вычисление ширины
                }

            } // if ob!=null
        } // for
        
    }

    public int getVWidth() {
        //g.setColor(0);
        if (width>0) return width;  // cached
        
        int w=0;
        int imgWidth=imgWidth();
        
        for (int index=0; index<elementCount;index++) {
            Object ob=elementData[index];
            if (ob!=null) {
                
                if (ob instanceof String ){
                    // string element
                    w+=font.stringWidth((String)ob);
                } else if ((ob instanceof Integer)&& imageList!=null) {
                    // image element or color
                    int i=(((Integer)ob).intValue());
                    switch (i&0xff000000) {
                        case IMAGE:
                            w+=imgWidth;
                            break;
                    }
                } // Integer
            } // if ob!=null
        } // for
        return width=w;
    }

    public void setElementAt(Object obj, int index) {
        height=width=0; // discarding cached values
        if (index>=elementCount) this.setSize(index+1);
        super.setElementAt(obj, index);
    }
    
    public int getVHeight(){
        if (height!=0) return height;
        for (int i=0;i<elementCount;i++){
            int h=0;
            Object o=elementData[i];
            if (o==null) continue;
            if (o instanceof String) { h=font.getHeight(); } else
            if (o instanceof Integer) {
                int a=((Integer)o).intValue();
                if ((a&0xff000000) == 0) { h=imageList.getWidth(); }
            } else
            if (o instanceof VirtualElement) { h=((VirtualElement)o).getVHeight(); }
            if (h>height) height=h;
        }
        return height;
    }

    public void addElement(Object obj) {
        height=width=0; // discarding cached values
        super.addElement(obj);
    }

    public void addImage(int imageIndex){ addElement(new Integer(imageIndex)); }
    public void addColor(int colorRGB){ addElement(new Integer(COLOR | colorRGB)); }
    public void addRAlign(){ addElement(new Integer(RALIGN)); }
    public void addUnderline(){ addElement(new Integer(UNDERLINE)); }
    
    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public String getTipString() { return null; }

}
*/