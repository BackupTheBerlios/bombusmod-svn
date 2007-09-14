/*
 * VirtualList.java
 *
 * Created on 30.01.2005, 14:46
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

package ui;
import Info.Phone;
import Info.Version;
import Stats.Stats;
import javax.microedition.lcdui.*;
import java.util.*;
import Client.*;
//#ifdef POPUPS
//# import ui.controls.PopUp;
//#endif
import ui.controls.Balloon;
import ui.controls.ScrollBar;
import util.strconv;

//#if ALT_INPUT
//# import ui.inputbox.Box;
//#endif

public abstract class VirtualList         
        extends Canvas 
{
    public void focusedItem(int index) {}

    abstract protected int getItemCount();

    abstract protected VirtualElement getItemRef(int index);

    protected int getMainBarBGndRGB() {return ColorScheme.BAR_BGND;} 
    
    private StaticData sd=StaticData.getInstance();

    private boolean reverse=false;

    public static int isbottom=2; //default state both panels show, reverse disabled

    private static final int USER_OTHER_KEY_PRESSED = 0;
    private static final int USER_STAR_KEY_PRESSED = 1;
    private static final int USER_KEY_EXECUTED = 2;
    
    private int lastKeyPressed = 0;
    private int additionKeyState = USER_OTHER_KEY_PRESSED;
    
    
    public Phone ph=Phone.getInstance();
    
//#ifdef POPUPS
//#     private static PopUp popup = new PopUp();
//# 
//#     public static void setWobble(String txt){
//#         popup.setMessage(txt);
//#     }
//#endif
    protected int getMainBarRGB() {return ColorScheme.BAR_INK;} 
    
    private Config cf=Config.getInstance();
    private Stats stats=Stats.getInstance();

    public void eventOk(){
        try {
            ((VirtualElement)getFocusedObject()).onSelect();
            updateLayout();
            fitCursorByTop();
        } catch (Exception e) {} 
    }

    public void userKeyPressed(int keyCode){}
    
    public void userAdditionKeyPressed(int keyCode){}

    public static final short SIEMENS_GREEN=-11;
    public static final short NOKIA_GREEN=-10;
    public static final short NOKIA_PEN=-50;
    public static final short MOTOROLA_GREEN=-10;

    public static final short MOTOE680_VOL_UP=-9;
    public static final short MOTOE680_VOL_DOWN=-8;
    public static final short MOTOE680_REALPLAYER=-6;
    public static final short MOTOE680_FMRADIO=-7;

    public static final short SE_CLEAR=-8;
    
    public static final short MOTOROLA_FLIP=-200;
    
    public static final short SE_FLIPOPEN_JP6=-30;
    public static final short SE_FLIPCLOSE_JP6=-31;
    public static final short SE_GREEN=-5;
    
    public static final short SIEMENS_FLIPOPEN=-24;
    public static final short SIEMENS_FLIPCLOSE=-22; //�������� ������� ������ ����������� � �� ���� ��� ���������� :(
    
    public static final short SIEMENS_VOLUP=-13;
    public static final short SIEMENS_VOLDOWN=-14;
    
    public static final short SIEMENS_CAMERA=-20;
    public static final short SIEMENS_MPLAYER=-21;
    
    public int stringHeight=15;

    public static short keyClear=-8;
    public static short keyVolDown=0x1000;
    public static short keyBack=-11;
    public static short greenKeyCode=SIEMENS_GREEN;
    public static boolean fullscreen=false;
    public static boolean memMonitor;
    public static boolean showBalloons;

    public static boolean canBack=true;

    int width;
    int height;

    private Image offscreen;
    
    protected int cursor;

    protected boolean stickyWindow=true;
    
    private int itemLayoutY[]=new int[1];
    private int listHeight;
    
    protected synchronized void updateLayout(){
        int size=getItemCount();
        if (size==0) {
            listHeight=0;
            return;
        }
        int layout[]=new int[size+1];
        int y=0;
        for (int index=0; index<size; index++){
            y+=getItemRef(index).getVHeight();
            layout[index+1]=y;
        };
        listHeight=y;
        itemLayoutY=layout;
    }
    
    protected int getElementIndexAt(int yPos){
        int end=getItemCount()-1;
        if (end<0) return -1;
        int begin=0;
        while (end-begin>1) {
            int index=(end+begin)/2;
            if (yPos<itemLayoutY[index]) end=index; else begin=index;
        }
        return (yPos<itemLayoutY[end])? begin:end;
    }
    
    public int win_top;
    private int winHeight;
    
    protected int offset;
    
    protected boolean showBalloon;
    
    protected VirtualElement mainbar;
 //#if ALT_INPUT   
//#     protected Box inputbox; //alt
//#     public Box getInputBoxItem() { return (Box)inputbox; } //alt
//#     public void setInputBoxItem(Box ib) { this.inputbox=ib; } //alt
//#     //public static String inputBoxText=null;
 //#endif
    private boolean wrapping = true;
    
    public static int fullMode; 

    public static int startGPRS=-1;
    public static int offGPRS=0;

    private int itemBorder[];

    private int lastClickY;
    private int lastClickItem;
    private long lastClickTime;
    
//#ifdef ELF    
//#     private static boolean sie_accu=true;
//#     private static boolean sie_net=true;
//#endif
    
    public void enableListWrapping(boolean wrap) { this.wrapping=wrap; }
    
    public ComplexString getMainBarItem() {return (ComplexString)mainbar;}
    public void setMainBarItem(ComplexString mainbar) { this.mainbar=mainbar; }

    public Object getFocusedObject() { 
        try {
            return getItemRef(cursor);
        } catch (Exception e) { }
        return null;
    }    

    protected Display display;
    protected Displayable parentView;

    ScrollBar scrollbar;
    /** Creates a new instance of VirtualList */
    public VirtualList() {
        width=getWidth();
        height=getHeight();
        
        if (ph.PhoneManufacturer()==ph.WINDOWS) {
            setTitle("Bombus CE");
        }

        setFullScreenMode(fullscreen);

	itemBorder=new int[32];
	
	scrollbar=new ScrollBar();
	scrollbar.setHasPointerEvents(hasPointerEvents());
    }

    /** Creates a new instance of VirtualList */
    public VirtualList(Display display) {
        this();

        attachDisplay(display);
    }

    public void attachDisplay (Display display) {
        if (this.display!=null) return;
        this.display=display;
        parentView=display.getCurrent();
        display.setCurrent(this);
        redraw();
    }

    public void redraw(){
        Displayable d=display.getCurrent();
        //System.out.println(d.toString());
        if (d instanceof Canvas) {
            //((Canvas)d).serviceRepaints();
            ((Canvas)d).repaint();
        }
    }

    protected void hideNotify() {
	offscreen=null;
    }

    protected void showNotify() {
	if (!isDoubleBuffered()) 
	    offscreen=Image.createImage(width, height);
//#if (USE_ROTATOR)
//#         TimerTaskRotate.startRotate(-1, this);
//#endif
    }

    protected void sizeChanged(int w, int h) {
        width=w;
        height=h;
	if (!isDoubleBuffered()) 
	    offscreen=Image.createImage(width, height);
    }

    protected void beginPaint(){};

    public void paint(Graphics graphics) {
        width=getWidth();	// patch for SE
        height=getHeight();
        
        boolean paintTop=true;
        boolean paintBottom=true;
        
        int mHeight=0, iHeight=0; // nokia fix
        
	Graphics g=(offscreen==null)? graphics: offscreen.getGraphics();

        switch (isbottom) {
            case 0: paintTop=false; paintBottom=false; reverse=false; break;
            case 1: paintTop=true;  paintBottom=false; reverse=false; break;
            case 2: paintTop=true;  paintBottom=true;  reverse=false; break;
            case 3: paintTop=false; paintBottom=true;  reverse=false; break;
            case 4: paintTop=true;  paintBottom=false; reverse=true;  break;
            case 5: paintTop=true;  paintBottom=true;  reverse=true;  break;
            case 6: paintTop=false; paintBottom=true;  reverse=true;  break;
        }
        //paintTop=true;  paintBottom=true;  reverse=true;

        beginPaint();
        
        int list_bottom=0;        
        itemBorder[0]=0;
        updateLayout();
        
        setAbsOrg(g, 0,0);
       
        if (mainbar!=null)
            mHeight=mainbar.getVHeight(); // nokia fix

        iHeight=FontCache.getBalloonFont().getHeight(); // nokia fix
        
        if (paintTop) {
            if (reverse) {
                itemBorder[0]=iHeight;
                drawInfoPanel(g);
            } else {
                if (mainbar!=null)
                    itemBorder[0]=mHeight; 
                drawMainPanel(g);
            }
        }

//#if ALT_INPUT 
//#         if (inputbox!=null) {
//#                 list_bottom=inputbox.getHeight();
//#         } else {
//#endif
                if (paintBottom) {
                    if (reverse) {
                        if (mainbar!=null) list_bottom=mHeight;
                    } else {
                        list_bottom=iHeight; 
                    }
                }
//#if ALT_INPUT
//#         }
//#endif
       
        winHeight=height-itemBorder[0]-list_bottom;

        int count=getItemCount(); // ������ ??��??��
        
        boolean scroll=(listHeight>winHeight);

        if (count==0) {
            cursor=(cursor==-1)?-1:0; 
            win_top=0;
        } else if (cursor>=count) {
            cursor=count-1;
            stickyWindow=true;
        }
        if (count>0 && stickyWindow) fitCursorByTop();
        
        int itemMaxWidth=(scroll) ?(width-scrollbar.getScrollWidth()) : (width);

        int itemIndex=getElementIndexAt(win_top);
        int displayedIndex=0;
        int displayedBottom=itemBorder[0];
   
        int baloon=-1;
        int itemYpos;
        try {
            while ((itemYpos=itemLayoutY[itemIndex]-win_top)<winHeight) {
                
                VirtualElement el=getItemRef(itemIndex);
                
                boolean sel=(itemIndex==cursor);
                
                int lh=el.getVHeight();

                setAbsOrg(g, 0, itemBorder[0]);
                g.setClip(0,0, itemMaxWidth, winHeight);    
                
                g.translate(0,itemYpos);
                
                g.setColor(el.getColorBGnd());

                if (sel) {
                    drawCursor(g, itemMaxWidth, lh); 
                    baloon=g.getTranslateY();
                } else
                    g.fillRect(0,0, itemMaxWidth, lh);

                g.setColor(el.getColor());
                
                g.clipRect(0, 0, itemMaxWidth, lh);
                el.drawItem(g, (sel)?offset:0, sel, (showBalloon)?true:false);
                
                itemIndex++;
		displayedBottom=itemBorder[++displayedIndex]=itemBorder[0]+itemYpos+lh;
            }
        } catch (Exception e) { }

        int clrH=height-displayedBottom;
        if (clrH>0) {
            setAbsOrg(g, 0,displayedBottom);
            g.setClip(0, 0, itemMaxWidth, clrH);
            g.setColor(ColorScheme.LIST_BGND);
            g.fillRect(0, 0, itemMaxWidth, clrH);
        }

        if (scroll) {
	    
            setAbsOrg(g, 0, itemBorder[0]);
            g.setClip(0, 0, width, winHeight);

	    scrollbar.setPostion(win_top);
	    scrollbar.setSize(listHeight);
	    scrollbar.setWindowSize(winHeight);
	    
	    scrollbar.draw(g);
        } else scrollbar.setSize(0);

        setAbsOrg(g, 0, 0);
        g.setClip(0,0, width, height);
        
        drawHeapMonitor(g, itemBorder[0]); //heap monitor
        
        if (showBalloon) {
            if (showBalloons) {
                String text=null;
                try {
                    text=((VirtualElement)getFocusedObject()).getTipString();
                } catch (Exception e) { }
                if (text!=null)
                    drawBalloon(g, baloon, text);
            }
        }
//#if ALT_INPUT
//#         if (inputbox!=null) {
//#             if (list_bottom>0)
//#                 setAbsOrg(g, 0, 0);
//#                 inputbox.draw(g, width, height);
//#         } else {
//#endif
                if (paintBottom) {
                    if (reverse) {
                        setAbsOrg(g, 0, height-mHeight);
                        drawMainPanel(g);
                    } else {
                        setAbsOrg(g, 0, height-iHeight);
                        drawInfoPanel(g);
                    }
                }
//#if ALT_INPUT
//#         }
//#endif
        
        setAbsOrg(g, 0, 0);
        g.setClip(0,0, width, height);

//#ifdef POPUPS
//#         drawPopUp(g);
//#endif
	if (offscreen!=null) graphics.drawImage(offscreen, 0,0, Graphics.TOP | Graphics.LEFT );
    }
    
//#ifdef POPUPS
//#     protected void drawPopUp(final Graphics g) {
//#         popup.paintCustom(g);
//#     }
//#endif
    
    protected void drawBalloon(final Graphics g, int balloon, final String text) {
        setAbsOrg(g,0,balloon);
        Balloon.draw(g, text);
    }

    private void drawHeapMonitor(final Graphics g, int y) {
        if (memMonitor) {
            int ram=(int)(((int)Runtime.getRuntime().freeMemory()*width)/(int)Runtime.getRuntime().totalMemory());
            g.setColor(ColorScheme.HEAP_TOTAL);  g.fillRect(0,y,width,1);
            g.setColor(ColorScheme.HEAP_FREE);  g.fillRect(0,y,ram,1);
        }
    }
    
    private void drawInfoPanel (final Graphics g) {
        Font bottomFont=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);
        int h=bottomFont.getHeight();
        
        g.setClip(0,0, width, h);

        g.setColor(getMainBarBGndRGB());
        g.fillRect(0, 0, width, h);
   
        g.setColor(getMainBarRGB());
        g.setFont(bottomFont);
        
        StringBuffer s=new StringBuffer();    
        s.append(Time.localTime());
        s.append(" ");
        s.append(strconv.getSizeString(stats.getGPRS()));
//#ifdef ELF
//#         s.append(getNetworkLevel());
//#         s.append(getAccuLevel());
//#endif
        if (s!=null) {
            g.drawString(s.toString(), width/2, 1, Graphics.TOP|Graphics.HCENTER);
            s=null;
        }
    }

    private void drawMainPanel (final Graphics g) {    
        if (mainbar!=null) {
            int h=mainbar.getVHeight();
            g.setClip(0,0, width, h);
            
            g.setColor(getMainBarBGndRGB());
            g.fillRect(0, 0, width, h);
            
            g.setColor(getMainBarRGB());
            mainbar.drawItem(g,0,false, false);
        }
    }

    private void setAbsOrg(Graphics g, int x, int y){
        g.translate(x-g.getTranslateX(), y-g.getTranslateY());
    }

    public void moveCursorHome(){
        stickyWindow=true;
        if (cursor>0) {
            cursor=0;
        }
        setRotator();
    }

    public void moveCursorEnd(){
        stickyWindow=true;
        int count=getItemCount();
        if (cursor>=0) {
            cursor=(count==0)?0:count-1;
        }
        setRotator();
    }

    public void moveCursorTo(int index, boolean force){
        int count=getItemCount();
        if (index<0) index=0;
        if (index>=count) index=count-1; 
        
        cursor=index;
        stickyWindow=true;
        
        repaint();
    }
    
    protected void fitCursorByTop(){
        try {
            int top=itemLayoutY[cursor];
            if (top<win_top) win_top=top;   
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                int bottom=itemLayoutY[cursor+1]-winHeight;
                if (bottom>win_top) win_top=bottom;  
            }
            if (top>=win_top+winHeight) win_top=top; 
        } catch (Exception e) { }
    }
    
    protected void fitCursorByBottom(){
        try {
            int bottom=itemLayoutY[cursor+1]-winHeight;
            if (bottom>win_top) win_top=bottom;
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                int top=itemLayoutY[cursor];
                if (top<win_top) win_top=top;
            }
            if (itemLayoutY[cursor+1]<=win_top) win_top=bottom;
        } catch (Exception e) {}
    }

    protected int kHold;
    
    protected void keyRepeated(int keyCode){ key(keyCode); }
    protected void keyReleased(int keyCode) { kHold=0; }
    protected void keyPressed(int keyCode) { kHold=0; key(keyCode);  lastKeyPressed=keyCode; }
    
    protected void pointerPressed(int x, int y) {
	if (scrollbar.pointerPressed(x, y, this)) {
            stickyWindow=false;
            return;
        } 
	int i=0;
	while (i<32) {
	    if (y<itemBorder[i]) break;
	    i++;
	}
	if (i==0 || i==32) return;
	//System.out.println(i);
	if (cursor>=0) {
            moveCursorTo(getElementIndexAt(win_top)+i-1, true);
            setRotator();
        }
	
	long clickTime=System.currentTimeMillis();
	if (cursor==lastClickItem)
	    if (lastClickY-y<5 && y-lastClickY<5) 
		if (clickTime-lastClickTime<500){
		    y=0;    // ������ "�������� �����"
		    eventOk();
		}
	lastClickTime=clickTime;
	lastClickY=y;
	lastClickItem=cursor;
        
        // ??������ ??������ ���??������� �������
        int il=itemLayoutY[cursor+1]-winHeight;
        if (il>win_top) win_top=il;
        il=itemLayoutY[cursor];
        if (il<win_top) win_top=il;
        
        repaint();
    }
    protected void pointerDragged(int x, int y) { 
        if (scrollbar.pointerDragged(x, y, this)) stickyWindow=false; 
    }
    protected void pointerReleased(int x, int y) { scrollbar.pointerReleased(x, y, this); }

    private boolean additionKeyPressed(int keyCode) {
        switch (keyCode) {
            case KEY_NUM0: 
                //setWobble("bl!");
                return true;
            case KEY_NUM1: 
                //setWobble("bl!");
                return true;
            case KEY_NUM2: 
                //setWobble("bl!");
                return true;
            case KEY_NUM3: 
                //setWobble("bl!");
                return true;
            case KEY_NUM4: 
                new ConfigForm(display);
                return false;
            case KEY_NUM5: 
                //setWobble("bl!");
                return true;
            case KEY_NUM6: 
                //setWobble("bl!");
                return true;
            case KEY_NUM7: 
                //setWobble("bl!");
                return true;
            case KEY_NUM8: 
                //setWobble("bl!");
                return true;
            case KEY_NUM9: 
                //setWobble("bl!");
                return true;
            default:
                return true;
        }
    }
    
    private void key(int keyCode) {
        //System.out.println(keyCode);

        switch (additionKeyState) {
            case USER_OTHER_KEY_PRESSED:
                additionKeyState=(keyCode==KEY_STAR)?USER_STAR_KEY_PRESSED:USER_OTHER_KEY_PRESSED;
                break;
            case USER_STAR_KEY_PRESSED:
                additionKeyState=(keyCode!=KEY_STAR)?USER_KEY_EXECUTED:USER_STAR_KEY_PRESSED;
                if (!additionKeyPressed(keyCode))
                    return;
                break;
            case USER_KEY_EXECUTED:
                additionKeyState=(keyCode==KEY_STAR)?USER_STAR_KEY_PRESSED:USER_OTHER_KEY_PRESSED;
                break;            
        }
//#ifdef POPUPS
//#         popup.next();
//#endif
        if ((keyCode==cf.SOFT_RIGHT || keyCode==cf.KEY_BACK) && ph.PhoneManufacturer()!=ph.SONYE) {
            if (canBack==true)
                destroyView();
            return;
        }
        
//#if ALT_INPUT
//#         if (inputbox==null) {
//#endif
            switch (keyCode) {
                case 0: break;
                case NOKIA_PEN: { 
                    if (canBack==true)
                        destroyView(); 
                    break; 
                }
                case MOTOE680_VOL_UP:
                case MOTOROLA_FLIP: { 
                    userKeyPressed(keyCode); 
                    break; 
                }
                case KEY_NUM1:  { 
                    moveCursorHome();    
                    break; 
                }
                case KEY_NUM7:  { 
                    moveCursorEnd();     
                    break; 
                }
                
                case KEY_NUM4:  { 
                    userKeyPressed(keyCode); 
                    break; 
                }
                case KEY_NUM6:  { 
                    userKeyPressed(keyCode); 
                    break; 
                }
                
                case NOKIA_GREEN: {
                    if (ph.PhoneManufacturer()==ph.NOKIA) {
                        keyGreen();
                        break; 
                    }
                }

                default:
                    try {
                        switch (getGameAction(keyCode)){
                            case UP:    { keyUp(); break; }
                            case DOWN:  { keyDwn(); break; }
                            case LEFT:  { keyLeft(); break; }
                            case RIGHT: { keyRight(); break; }
                            case FIRE:  { eventOk(); break; }
                        default:
                            if (keyCode==greenKeyCode) { keyGreen(); break; }
                            if (keyCode==keyVolDown) { moveCursorEnd(); break; }
                            if (keyCode=='5') {  eventOk(); break; }

                            userKeyPressed(keyCode);
                        }
                    } catch (Exception e) {/* IllegalArgumentException @ getGameAction */}

                    if (keyCode==KEY_STAR) {
                            System.gc();
//#ifdef POPUPS
//#                             if (cf.popUps) {
//#                                 int freemem=(int)Runtime.getRuntime().freeMemory();
//#                                 setWobble("Free "+(freemem/1000)+" kb");
//#                             }
//#endif
                    }
            }
//#if ALT_INPUT
//#         } else {
//#             if (keyCode==greenKeyCode)
//#                 keyGreen();
//#             
//#             userKeyPressed(keyCode);
//#         }
//#endif
        repaint();
    }

    public void keyUp() {
	 
        if (cursor==0) {
            if (wrapping)  moveCursorEnd(); else itemPageUp();
            setRotator();
            return;
        }

        if (itemPageUp()) return;
        //stickyWindow=true;
        cursor--;
        fitCursorByBottom();
        setRotator();
    }

    public void keyDwn() { 
	if (cursor==getItemCount()-1) 
        { 
            if (wrapping) moveCursorHome(); else itemPageDown();
            setRotator();
            return; 
        }
        
        if (itemPageDown()) return;
        stickyWindow=true; 
        cursor++;
        setRotator();
    }
    
    private boolean itemPageDown() {
        try {
            stickyWindow=false;

            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                stickyWindow=true;
                return false;
            }

            if (!cursorInWindow()) return false;
            
            int remainder=itemLayoutY[cursor+1]-win_top;
            if (remainder<=winHeight) return false;
            if (remainder<=2*winHeight) {
                win_top=remainder-winHeight+win_top+8;
                return true;
            }
            win_top+=winHeight-stringHeight;
            return true;
        } catch (Exception e) {}
        return false;
    }
    
    private boolean itemPageUp() {
        try {
            stickyWindow=false;
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                //stickyWindow=true;
                return false;
            }

            if (!cursorInWindow()) { return false; }
            
            int remainder=win_top-itemLayoutY[cursor];
            if (remainder<=0) return false;
            if (remainder<=winHeight) {
                win_top=itemLayoutY[cursor];
                return true;
            }
            win_top-=winHeight-stringHeight;
            return true;
        } catch (Exception e) {}
        return false;
    }

    public void keyLeft() {
        try {
            stickyWindow=false;
            win_top-=winHeight;
            if (win_top<0) {
                win_top=0;
                cursor=0;
            }
            if (!cursorInWindow()) {
                cursor=getElementIndexAt(itemLayoutY[cursor]-winHeight);
                if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) fitCursorByTop();
            }
            setRotator();
        } catch (Exception e) {};
    }

    public void keyRight() { 
        try {
            stickyWindow=false;
            win_top+=winHeight;
            int endTop=listHeight-winHeight;
            if (endTop<win_top) {
                win_top= (listHeight<winHeight)? 0 : endTop;
                cursor=getItemCount()-1;
            } else
                if (!cursorInWindow()) {
                    cursor=getElementIndexAt(itemLayoutY[cursor]+winHeight);
                   
                    if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) fitCursorByTop();
                }
            setRotator();
        } catch (Exception e) {};
    }
    
    public boolean cursorInWindow(){
        try {
            int y1=itemLayoutY[cursor]-win_top;
            int y2=itemLayoutY[cursor+1]-win_top;
            if (y1>=winHeight) return false;
            if (y2>=0) return true;
        } catch (Exception e) { }
        return false;
    }

    protected void keyGreen() { eventOk(); }
    
    protected  void setRotator(){
//#if (USE_ROTATOR)
//#         try {
//#             if (getItemCount()<1) return;
//#             focusedItem(cursor);
//#         } catch (Exception e) { return; }
//#         
//#         if (cursor>=0) {
//#             int itemWidth=getItemRef(cursor).getVWidth();
//#             if (itemWidth>=width-scrollbar.getScrollWidth() ) itemWidth-=width/2; else itemWidth=0;
//#             TimerTaskRotate.startRotate(itemWidth, this);
//#         }
 //#endif
    }
    
    protected void drawCursor (Graphics g, int width, int height){
            g.setColor(ColorScheme.CURSOR_BGND);    g.fillRect(1, 1, width-1, height-1);
            g.setColor(ColorScheme.CURSOR_OUTLINE); g.drawRect(0, 0, width-1, height-1);
    }

    public void setParentView(Displayable parentView){
        this.parentView=parentView;
    }

    public void destroyView(){
        if (display!=null && parentView!=null /*prevents potential app hiding*/ )   
            display.setCurrent(parentView);
    }

    public int getListWidth() {
        return width-scrollbar.getScrollWidth()-2;
    }

    public final static void sort(Vector sortVector){
        try {
            synchronized (sortVector) {
                int f, i;
                IconTextElement left, right;
                
                for (f = 1; f < sortVector.size(); f++) {
                    left=(IconTextElement)sortVector.elementAt(f);
                    right=(IconTextElement)sortVector.elementAt(f-1);
                    if ( left.compare(right) >=0 ) continue;
                    i = f-1;
                    while (i>=0){
                        right=(IconTextElement)sortVector.elementAt(i);
                        if (right.compare(left) <0) break;
                        sortVector.setElementAt(right,i+1);
                        i--;
                    }
                    sortVector.setElementAt(left,i+1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); /* ClassCastException */
        }
    }
    
    public int getCursor() {
        return cursor;
    }
    
    
//#ifdef ELF    
//#     public static String getAccuLevel() {
//# 
//#         if (sie_accu==false) return "";
//#         try {
//#             String cap=System.getProperty("MPJC_CAP");
//#             return (cap==null)? "": " "+cap+"%";
//#         } catch (Exception e) { sie_accu=false; }
//# 
//#         return "";
//#     }
//#     
//#     public static String getNetworkLevel() {
//# 
//#         if (sie_net==false) return "";
//#         try {
//#             String rx=System.getProperty("MPJCRXLS");
//#             int rp=rx.indexOf(',');
//#             return (rp<0)? "": " "+rx.substring(0,rp)+"db";
//#         } catch (Exception e) { sie_net=false; }
//# 
//#         return "";
//#     }
//#endif    
}

//#if (USE_ROTATOR)    
//# class TimerTaskRotate extends Thread{
//#     //private Timer t;
//#     private int scrollLen;
//#     private int balloon;
//#     private int scroll;
//#     
//#     private boolean stop;
//#     private boolean exit;
//#     
//#     private VirtualList attachedList;
//#     
//#     private static TimerTaskRotate instance;
//#     
//#     private TimerTaskRotate() {
//#         exit=false;
//#         stop=true;
//#         start();
//#     }
//#     
//#     public static void startRotate(int max, VirtualList list){
//#         //Windows mobile J9 hanging test
//#         if (Phone.PhoneManufacturer()==Phone.WINDOWS) {
//#             list.showBalloon=true;
//#             list.offset=0;
//#             return;
//#         }
//#         if (instance==null) 
//#             instance=new TimerTaskRotate();
//#         if (max<0) {
//#             instance.destroyTask(); return;
//#         }
//#         
//#         synchronized (instance) {
//#             list.offset=0;
//#             instance.scrollLen=max;
//#             //list.showBalloon=false; //<< uncomment this to disable keep balloon floating when traversing
//#             instance.balloon=(list.showBalloon)? 7 : 15; //time to show
//#             instance.scroll=8;
//#             instance.attachedList=list;
//#             instance.stop=false;
//#         }
//#     }
//#     
//#     public void run() {
//#         // ��������� ������ ���
//#         //stickyWindow=false;
//#     
//#         while (true) {
//#             if (exit) return;
//#             try {  sleep(300);  } catch (Exception e) {}
//#             if (stop) continue;
//#             
//#             boolean redraw = false;
//#             synchronized (this) {
//#                 if (balloon<0) {
//#                     stop=true;
//#                     continue;
//#                 }
//#                 //System.out.println("b:"+scrollLen+" scroll="+scroll+" balloon="+balloon + " stop=" + stop);
//#                 
//#                 if (attachedList==null) 
//#                     stop=true;
//#                 
//#                 if (scrollLen>=0 || balloon>=0) { 
//#                     stop=false;
//#                     redraw=true;
//#                 }
//#                 
//#                 if (stop) {
//#                     if (attachedList!=null) 
//#                         attachedList.offset=0;
//#                     attachedList.showBalloon=false;
//#                     attachedList=null;
//#                     continue;
//#                 }
//#                 
//#                 //scroll state machine
//#                 if (scroll>0) scroll--;
//#                 if (scroll==0) {
//#                     if (attachedList.offset>=scrollLen) {
//#                         scrollLen=-1;
//#                         attachedList.offset=0;
//#                     } else 
//#                         attachedList.offset+=30;
//#                 }
//#                 
//#                 //balloon state machine
//#                 if (balloon>=0) 
//#                     balloon--;
//#                 attachedList.showBalloon=(balloon<8 && balloon>0);
//#                 
//#             }
//#             if (redraw) 
//#                 attachedList.redraw();
//#             redraw=false;
//#             
//#         }
//#     }
//#     public void destroyTask(){
//#         synchronized (this) { 
//#             if (attachedList!=null) attachedList.offset=0;
//#             stop=true; 
//#             //attachedList=null;
//#         }
//#     }
//# }
//#endif
