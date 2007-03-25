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
import javax.microedition.lcdui.*;
import java.util.*;
import Client.*;
import ui.controls.PopUp;
import ui.controls.Balloon;
//#if ALT_INPUT
//# import ui.controls.InputBox;
//#endif
import ui.controls.ScrollBar;

/**
 * Вертикальный список виртуальных элементов.
 * класс реализует управление списком, скроллбар,
 * вызов отрисовки отображаемых на экране элементов.
 * @author Eugene Stahov
 */
public abstract class VirtualList         
        extends Canvas 
{
    
    /**
     * событие "Курсор выделил элемент"
     * в классе VirtualList вызываемая функция не выполняет действий, необходимо
     * переопределить (override) функцию для реализации необходимых действий
     * @param index индекс выделенного элемента
     */
    public void focusedItem(int index) {}


    /**
     * число элементов виртуального списка
     * эта функция абстрактная, должна быть переопределена при наследовании
     * @return число элементов списка, исключая заголовок
     */
    abstract protected int getItemCount();

    /**
     * элемент виртуального списка
     * эта функция абстрактная, должна быть переопределена при наследовании
     * @param index номер элемента списка. не превосходит значение, возвращённое getItemCount()
     * @return ссылка на элемент с номером index.
     */
    abstract protected VirtualElement getItemRef(int index);
    
    /**
     * цвет фона заголовка
     * @return RGB-цвет фона заголовка
     */
    protected int getMainBarBGndRGB() {return ColorScheme.BAR_BGND;} 
    
    private StaticData sd=StaticData.getInstance();

    private boolean reverse=false;

    public static int isbottom=2; //default state both panels show, reverse disabled

    public static String wobble="";
    
    public Phone ph=Phone.getInstance();
    
    public static void setWobble(String txt){
        wobble=txt.trim();
    }
    
    /**
     * цвет текста заголовка
     * @return RGB-цвет текста заголовка
     */
    protected int getMainBarRGB() {return ColorScheme.BAR_INK;} 
    
    private Config cf=Config.getInstance();
    
    /**
     * событие "Нажатие кнопки ОК"
     * базовая реализация VirtualList вызывает функцию onSelect для выбранного элемента; 
     * необходимо переопределить (override) функцию для реализации желаемых действий
     */
    public void eventOk(){
        try {
            ((VirtualElement)getFocusedObject()).onSelect();
            updateLayout();
            fitCursorByTop();
        } catch (Exception e) { e.printStackTrace();} 
    }
    
    /**
     * Обработчик дополнительных кнопок. Вызывается в случае, если код кнопки 
     * не был обработан функцией key(keyCode)
     * необходимо переопределить (override) функцию для реализации необходимых действий     
     * @param keyCode код клавиши
     */
    public void userKeyPressed(int keyCode){}
    
    //////////////////////////////////

    public static final int SIEMENS_GREEN=-11;
    public static final int NOKIA_GREEN=-10;
    public final static int NOKIA_PEN=-50;
    public static final int MOTOROLA_GREEN=-10;

    public static final int MOTOE680_VOL_UP=-9;
    public static final int MOTOE680_VOL_DOWN=-8;
    public static final int MOTOE680_REALPLAYER=-6;
    public static final int MOTOE680_FMRADIO=-7;

    public static final int SE_CLEAR=-8;
    
    public final static int MOTOROLA_FLIP=-200;
    
    public static final int SE_FLIPOPEN_JP6=-30;
    public static final int SE_FLIPCLOSE_JP6=-31;
    
    public static final int SIEMENS_FLIPOPEN=-24;
    public static final int SIEMENS_FLIPCLOSE=-22;
    
    public int stringHeight=15;

    public static int keyClear=-8;
    public static int keyVolDown=0x1000;
    public static int keyBack=0x1000;
    public static int greenKeyCode=SIEMENS_GREEN;
    public static boolean fullscreen=false;
    public static boolean memMonitor;
    public static boolean digitMemMonitor;
    
    /** метрика экрана */
    int width;
    int height;
    
    /** экранный буфер для скрытой отрисовки. используется, если платформа 
     * не поддерживает двойную буферизацию экрана
     */
    private Image offscreen;
	
    protected boolean canback=true; // Enable destroyView() on keyBack by default
    
    /** признак положения курсора в конце списка */
    protected boolean atEnd; //FIXME: перенести поведение в функции keyRight();
    
    protected int cursor;
    
    /** 
     * окно приклеено к позиции курсора 
     * ПР�?КЛЕ�?ВАЕТСЯ:
     *   при нажатии кнопок перемещения курсора
     *   при выборе стилусом элемента списка
     * ОТКЛЕ�?ВАЕТСЯ:
     *   при использовании скролбара
     */
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
        // деление пополам
        int end=getItemCount()-1;
        if (end<0) return -1;
        int begin=0;
        while (end-begin>1) {
            int index=(end+begin)/2;
            if (yPos<itemLayoutY[index]) end=index; else begin=index;
        }
        return (yPos<itemLayoutY[end])? begin:end;
    }
    
    public int win_top;    // верхняя граница окна относительно списка
    private int winHeight;  // отображаемый размер списка
    //int full_items; // полностью изображено в окне
    protected int offset;     // счётчик автоскроллинга
    
    protected boolean showBalloon;
    
    protected VirtualElement mainbar;
 //#if ALT_INPUT   
//#     protected InputBox inputbox; //alt
//#     public InputBox getBottomItem() { return (InputBox)inputbox; } //alt
//#     public void setInputBoxItem(InputBox inputbox) { this.inputbox=inputbox; } //alt
 //#endif   
    private boolean wrapping = true;
    
    public static int fullMode; 
    public static boolean paintTop=true;
    public static boolean paintBottom=true;

    public static int startGPRS=-1;
    public static int offGPRS=0;
    
    /** видимые границы элементов списка - зоны срабатывания touchscreen */
    private int itemBorder[];
    /** обработка doubleclick */
    private int lastClickY;
    private int lastClickItem;
    private long lastClickTime;
    
    /**
     * Разрешает заворачивание списка в кольцо (перенос курсора через конец списка)
     * по умолчанию установлен true
     * @param wrap будучи переданным true, разрешает перенос курсора через конец списка
     */
    public void enableListWrapping(boolean wrap) { this.wrapping=wrap; }
    
    /**
     * ссылка на заголовок списка
     * @return объект типа ComplexString
     */
    
    /**
     * СЃСЃС‹Р»РєР° РЅР° Р·Р°РіРѕР»РѕРІРѕРє СЃРїРёСЃРєР°
     * @return РѕР±СЉРµРєС‚ С‚РёРїР° ComplexString
     */
    
    public ComplexString getMainBarItem() {return (ComplexString)mainbar;}
    public void setMainBarItem(ComplexString mainbar) { this.mainbar=mainbar; }

    /**
     * возвращает ссылку на объект в фокусе. 
     * в классе VirtualList возвращает VirtualElement, на который указывает курсор,
     * однако, возможно переопределить функцию при наследовании
     * @return ссылка на объект в фокусе.
     */
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

        // rotator
//#if (USE_ROTATOR)        
//#        //rotator=new TimerTaskRotate(0, this);
//#endif

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
    
    /**
     * Запоминание предыдущего отображаемого объекта, подключенного к менеджеру
     * дисплея и подключение к дисплею виртуального списка (this) 
     * @param display менеджер дисплея мобильного устройства {@link }
     */
    public void attachDisplay (Display display) {
        if (this.display!=null) return;
        this.display=display;
        parentView=display.getCurrent();
        display.setCurrent(this);
        redraw();
    }


    /** запуск отложенной отрисовки активного Canvas */
    public void redraw(){
        //repaint(0,0,width,height);
        Displayable d=display.getCurrent();
        //System.out.println(d.toString());
        if (d instanceof Canvas) {
            ((Canvas)d).repaint();
        }
    }

    /** Вызывается после скрытия VirtualList. переопределяет наследуемый метод 
     * Canvas.hideNotify(). действие по умолчанию - освобождение экранного 
     * буфера offscreen, используемого при работе без автоматической двойной буферизации
     */
    protected void hideNotify() {
	offscreen=null;
    }
    
    /** Вызывается перед вызовом отрисовки VirtualList. переопределяет наследуемый метод 
     * Canvas.showNotify(). действие по умолчанию - создание экранного 
     * буфера offscreen, используемого при работе без автоматической двойной буферизации
     */
    protected void showNotify() {
	if (!isDoubleBuffered()) 
	    offscreen=Image.createImage(width, height);
        TimerTaskRotate.startRotate(-1, this);
    }
    
    /** Вызывается при изменении размера отображаемой области. переопределяет наследуемый метод 
     * Canvas.sizeChanged(int width, int heigth). сохраняет новые размеры области рисования.
     * также создаёт новый экранный буфер offscreen, используемый при работе без автоматической 
     * двойной буферизации
     */
//#if !(MIDP1)
    protected void sizeChanged(int w, int h) {
        width=w;
        height=h;
	if (!isDoubleBuffered()) 
	    offscreen=Image.createImage(width, height);
    }
//#endif
    
    /**
     * начало отрисовки списка.
     * функция вызывается перед отрисовкой списка, 
     * перед любыми обращениями к элементам списка.
     *
     * в классе VirtualList функция не выполняет никаких действий, необходимо
     * переопределить (override) функцию для реализации необходимых действий
     */
    protected void beginPaint(){};
    
    /**
     * отрисовка
     */
    public void paint(Graphics graphics) {
        width=getWidth();	// patch for SE
        height=getHeight();
        
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
        
        // заголовок окна
        beginPaint();
        
        int list_bottom=0;        
        itemBorder[0]=0;
        updateLayout(); //fixme: только при изменении списка
        
        setAbsOrg(g, 0,0);
        
        if (mainbar!=null)
            mHeight=mainbar.getVHeight(); // nokia fix

            iHeight=NetAccuFont.fontHeight+2; // nokia fix
        
        if (paintTop) {
            if (reverse) {
                itemBorder[0]=iHeight;
                drawMainPanel(g);
            } else {
                if (mainbar!=null)
                    itemBorder[0]=mHeight; 
                drawInfoPanel(g);
            }
        }
        
        if (paintBottom) {
            if (reverse) {
                if (mainbar!=null)
                    list_bottom=mHeight;
            } else {
                list_bottom=iHeight; 
            }
        }
       
        winHeight=height-itemBorder[0]-list_bottom;

        int count=getItemCount(); // размер списка
        
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
        // элементы окна
        // отрисовка
        int itemIndex=getElementIndexAt(win_top);
        int displayedIndex=0;
        int displayedBottom=itemBorder[0];
   
        int baloon=-1;
        atEnd=false;
        int itemYpos;
        try {
            // try вместо проверки на конец списка
            while ((itemYpos=itemLayoutY[itemIndex]-win_top)<winHeight) {
                
                VirtualElement el=getItemRef(itemIndex);
                
                boolean sel=(itemIndex==cursor);
                
                int lh=el.getVHeight();
                
                // окно списка
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
                el.drawItem(g, (sel)?offset:0, sel);
                
                itemIndex++;
		displayedBottom=itemBorder[++displayedIndex]=itemBorder[0]+itemYpos+lh;
            }
        } catch (Exception e) { atEnd=true; }

        // очистка остатка окна
        int clrH=height-displayedBottom;
        if (clrH>0) {
            setAbsOrg(g, 0,displayedBottom);
            g.setClip(0, 0, itemMaxWidth, clrH);
            g.setColor(ColorScheme.LIST_BGND);
            //g.setColor(VL_CURSOR_OUTLINE);
            g.fillRect(0, 0, itemMaxWidth, clrH);
        }

        // рисование скроллбара
        //g.setColor(VL_BGND);
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
        if (showBalloon) {
            String text=null;
            try {
                text=((VirtualElement)getFocusedObject()).getTipString();
            } catch (Exception e) { }
            if (text!=null)
                drawBalloon(g, baloon, text);
        }
//#if ALT_INPUT
//#         if (inputbox!=null) {
//#             setAbsOrg(g, 0, height-inputbox.height);  
//#             g.setClip(0,0, width, height);
//#             g.setColor(getMainBarBGndRGB());
//#             g.fillRect(0,0, width, height);
//#             g.setColor(getMainBarRGB());
//#             inputbox.drawItem(g);
//#         } else {
//#endif
                if (paintBottom) {
                    if (reverse) {
                        setAbsOrg(g, 0, height-mHeight);
                        drawInfoPanel(g);
                    } else {
                        setAbsOrg(g, 0, height-iHeight);
                        drawMainPanel(g);
                    }
                }
//#if ALT_INPUT
//#         }
//#endif
        
        setAbsOrg(g, 0, 0);
        g.setClip(0,0, width, height);
        drawHeapMonitor(g); //heap monitor
        
        if (wobble.length()>0) new PopUp(g,wobble, width-20, height-20);
        
	if (offscreen!=null) graphics.drawImage(offscreen, 0,0, Graphics.TOP | Graphics.LEFT );
	//full_items=fe;
    }

    protected void drawBalloon(final Graphics g, int balloon, final String text) {
        setAbsOrg(g,0,balloon);
        Balloon.draw(g, text);
        
    }

    private void drawHeapMonitor(final Graphics g) {
        if (memMonitor) {
            //g.setClip(0,0, width, 1);
            int freemem=(int)Runtime.getRuntime().freeMemory();
            int totalmem=(int)Runtime.getRuntime().totalMemory();
            int ram=(int)((freemem*width)/totalmem);
            g.setColor(ColorScheme.HEAP_TOTAL);  g.fillRect(0,0,width,1);
            g.setColor(ColorScheme.HEAP_FREE);  g.fillRect(0,0,ram,1);
        }
    }
    
    private void drawMainPanel (final Graphics g) {
        int h=NetAccuFont.fontHeight+2;
        g.setClip(0,0, width, h);

        g.setColor(getMainBarBGndRGB());
        g.fillRect(0, 0, width, h);

        String time=Time.timeString(Time.localTime());

        NetAccuFont.drawString(g, time, 1,  1);
        int w=(time.length()+1)*NetAccuFont.fontWidth;

        String traff = null;
        int ngprs=-1;
        int gprscount=0;
        try {
            try {
                ngprs=NetworkAccu.getGPRS();
            } catch (Exception e) { }
            if (ngprs>-1) {
                gprscount=ngprs;
            } else {
                int in=sd.roster.theStream.getBytesIn();
                int out=sd.roster.theStream.getBytesOut();
                gprscount=in+out;
            }
            traff=gprscount/1000+"<=";
        } catch (Exception e) {
            traff="0<=";
        }
        NetworkAccu.draw(g, width);
        NetAccuFont.drawString(g, traff, w, 1);
    }
    
    private void drawInfoPanel (final Graphics g) {    
        if (mainbar!=null) {
            int h=mainbar.getVHeight();
            g.setClip(0,0, width, h);
            
            g.setColor(getMainBarBGndRGB());
            g.fillRect(0, 0, width, h);
            
            g.setColor(getMainBarRGB());
            mainbar.drawItem(g,0,false);
        }
    }
    
    /**
     * перенос координат (0.0) в абсолютные координаты (x,y)
     * @param g графический контекст отрисовки
     * @param x абсолютная x-координата нового начала координат 
     * @param y абсолютная y-координата нового начала координат
     */
    private void setAbsOrg(Graphics g, int x, int y){
        g.translate(x-g.getTranslateX(), y-g.getTranslateY());
    }
    
   
    /**
     * перемещение курсора в начало списка
     */
    public void moveCursorHome(){
        stickyWindow=true;
        if (cursor>0) {
            cursor=0;
        }
        setRotator();
    }

    /**
     * перемещение курсора в конец списка
     */
    public void moveCursorEnd(){
        stickyWindow=true;
        int count=getItemCount();
        if (cursor>=0) {
            cursor=(count==0)?0:count-1;
        }
        setRotator();
    }

    /**
     * перемещение курсора в индексированную позицию
     * @param index позиция курсора в списке
     */
    public void moveCursorTo(int index, boolean force){
        int count=getItemCount();
        if (index<0) index=0;
        if (index>=count) index=count-1;    // если за последним элементом, то переместить на него
        
        cursor=index;
        stickyWindow=true;
        
        repaint();
    }
    
    protected void fitCursorByTop(){
        try {
            //проверка по верхней границе
            int top=itemLayoutY[cursor];
            // если верхний край выше окна, выровнять по верху
            if (top<win_top) win_top=top;   
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                // объект помещается на экране - проверим и нижнюю границу
                int bottom=itemLayoutY[cursor+1]-winHeight;
                // если нижний край ниже окна, выровнять по низу
                if (bottom>win_top) win_top=bottom;  
            }
            // случай, когда курсор больше окна, и он Н�?ЖЕ окна
            if (top>=win_top+winHeight) win_top=top; 
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    protected void fitCursorByBottom(){
        //проверка по верхней границе
        try {
            int bottom=itemLayoutY[cursor+1]-winHeight;
            // если нижний край ниже окна, выровнять по низу
            if (bottom>win_top) win_top=bottom;
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                // объект помещается на экране - проверим и нижнюю границу
                int top=itemLayoutY[cursor];
                // если верхний край выше окна, выровнять по верху
                if (top<win_top) win_top=top;
            }
            // случай, когда курсор больше окна, и он ВЫШЕ окна
            if (itemLayoutY[cursor+1]<=win_top) win_top=bottom;
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** код удерживаемой кнопки */
    protected int kHold;
    
    protected void keyRepeated(int keyCode){ key(keyCode); }
    protected void keyReleased(int keyCode) { kHold=0; }
    protected void keyPressed(int keyCode) { kHold=0; key(keyCode);  }
    
    protected void pointerPressed(int x, int y) {
        wobble=""; // hide woobbler
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
		    y=0;    // запрет "тройного клика"
		    eventOk();
		}
	lastClickTime=clickTime;
	lastClickY=y;
	lastClickItem=cursor;
        
        // сделаем элемент максимально видимым
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
    
    /**
     * обработка кодов кнопок
     * @param keyCode код нажатой кнопки
     */
    private void key(int keyCode) {
        wobble="";
       
        if (keyCode==-4 || keyCode==702) {
            if (ph.PhoneManufacturer()==ph.SIEMENS || ph.PhoneManufacturer()==ph.SIEMENS2) {
                destroyView();
                return;  
            }
        }
//#if ALT_INPUT
//#         if (inputbox==null) {
//#endif
            switch (keyCode) {
                case 0: break;
                case NOKIA_PEN: { destroyView(); break; }
                case MOTOE680_VOL_UP:
                case MOTOROLA_FLIP: { userKeyPressed(keyCode); break; }
                case KEY_NUM1:  { moveCursorHome();    break; }
                case KEY_NUM7:  { moveCursorEnd();     break; }

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
                                    if (keyCode==keyBack) {
                                        //TODO: Check, is destroyView() allowed
                                        destroyView();
                                        return;
                                    }
                                    userKeyPressed(keyCode);
                                }
                    } catch (Exception e) {/* IllegalArgumentException @ getGameAction */}
            }
//#if ALT_INPUT
//#         } else {
//#             userKeyPressed(keyCode);
//#         }
//#endif
        repaint();
    }
    
    /**
     * событие "Нажатие кнопки UP"
     * в классе VirtualList функция перемещает курсор на одну позицию вверх.
     * возможно переопределить (override) функцию для реализации необходимых действий
     */
    public void keyUp() {
	 
        if (cursor==0) {
            if (wrapping)  moveCursorEnd(); else itemPageUp();
            setRotator();
            return;
        }
        /*
        if (itemLayoutY[cursor]<win_top) {
            //верхняя граница элемента ещё не на экране
            win_top-=winHeight;
            if (win_top<0) win_top=0;
        } else {
            cursor--;
            if (getItemRef(cursor).getVHeight()>winHeight) {
                // если элемент не может поместиться на экране, вырвниваем по bottom
                win_top=itemLayoutY[cursor+1]-winHeight;
            } else if (win_top>itemLayoutY[cursor]) {
                win_top=itemLayoutY[cursor];
            }
        }
         */
        if (itemPageUp()) return;
        //stickyWindow=true;
        cursor--;
        fitCursorByBottom();
        setRotator();
    }
    
    /**
     * событие "Нажатие кнопки DOWN"
     * в классе VirtualList функция перемещает курсор на одну позицию вверх.
     * возможно переопределить (override) функцию для реализации необходимых действий
     */
    
    public void keyDwn() { 
	if (cursor==getItemCount()-1) 
        { 
            if (wrapping) moveCursorHome(); else itemPageDown();
            setRotator();
            return; 
        }
        /*if (itemLayoutY[cursor+1]>win_top+winHeight) {
            // нижняя граница текущего элемента ещё не на экране
            win_top+=winHeight; // пока - pagedown
        } else {
            cursor++;
            if (getItemRef(cursor).getVHeight()>winHeight) {
                // если элемент не может поместиться на экране, вырвниваем по top
                win_top=itemLayoutY[cursor];
            } else if (win_top+winHeight<itemLayoutY[cursor+1]) {
                win_top=itemLayoutY[cursor+1]-winHeight;
            }
        }*/
        if (itemPageDown()) return;
        stickyWindow=true; 
        cursor++;
        setRotator();
    }
    
    private boolean itemPageDown() {
        try {
            stickyWindow=false;
            // объект помещается полностью на экране?
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                stickyWindow=true;
                return false;
            }
            
            // объект на экране есть? (не смещён ли экран стилусом)
            if (!cursorInWindow()) return false;
            
            int remainder=itemLayoutY[cursor+1]-win_top;
            // хвост сообщения уже на экране?
            if (remainder<=winHeight) return false;
            // хвост сообщения на следующем экране?
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
            // объект помещается полностью на экране?
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                //stickyWindow=true;
                return false;
            }
            
            // объект на экране есть? (не смещён ли экран стилусом)
            
            if (!cursorInWindow()) { return false; }
            
            int remainder=win_top-itemLayoutY[cursor];
            // голова сообщения уже на экране?
            if (remainder<=0) return false;
            // хвост сообщения на следующем экране?
            if (remainder<=winHeight) {
                win_top=itemLayoutY[cursor];
                return true;
            }
            win_top-=winHeight-stringHeight;
            return true;
        } catch (Exception e) {}
        return false;
    }
    /**
     * событие "Нажатие кнопки LEFT"
     * в классе VirtualList функция перемещает курсор на одну страницу вверх.
     * возможно переопределить (override) функцию для реализации необходимых действий
     */
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

    /**
     * событие "Нажатие кнопки RIGHT"
     * в классе VirtualList функция перемещает курсор на одну страницу вниз.
     * возможно переопределить (override) функцию для реализации необходимых действий
     */
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
                    // [cursor+1] - workaround for sticking cursor if message is larger than screen
                    //cursor=getElementIndexAt(itemLayoutY[cursor+1]+winHeight);
                    
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
    
    /**
     * событие "Нажатие ЗЕЛЁНОЙ КНОПК�?"
     * в классе VirtualList функция выполняет вызов eventOk().
     * возможно переопределить (override) функцию для реализации необходимых действий
     */
    protected void keyGreen() { eventOk(); }
    
    /** перезапуск ротации скроллера длинных строк */
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
    // cursor rotator
//#if (USE_ROTATOR)    
//#     //private TimerTaskRotate rotator;
//#endif
    
    /**
     * рисование прямоугольного курсора
     * @param g графический контекст рисования
     * @param width ширина курсора
     * @param height высота курсора
     */
    protected void drawCursor (Graphics g, int width, int height){
            g.setColor(ColorScheme.CURSOR_BGND);    g.fillRect(1, 1, width-1, height-1);
            g.setColor(ColorScheme.CURSOR_OUTLINE); g.drawRect(0, 0, width-1, height-1);
    }

    public void setParentView(Displayable parentView){
        this.parentView=parentView;
    }
    
    /**
     * отсоединение от менеджера дисплея текущего виртуального списка, 
     * присоединение к менеджеру предыдущего Displayable
     */
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
    
    public void setTimeEvent(long time){
        synchronized (this) {
            timeEvent=(time==0)? 0:time+System.currentTimeMillis();
            if (time!=0) setRotator();
        }
    };
    protected long timeEvent;

    public int getCursor() {
        return cursor;
    }

    boolean probeTime(){
        synchronized (this) {
            if (timeEvent==0) return true;
            long timeRemained=System.currentTimeMillis()-timeEvent;
            //System.out.println(timeRemained);
            if (timeRemained>=0) {
                timeEvent=0;
                onTime();
            }
        }
        return false;
    }
    public void onTime() {};
 }

//#if (USE_ROTATOR)    
//# class TimerTaskRotate extends Thread{
//#      //private Timer t;
//#      private int scrollLen;
//#      private int balloon;
//#      private int scroll;
//# 
//#     
//#      private boolean stop;
//#      private boolean exit;
//#      
//#      private VirtualList attachedList;
//#  
//#      private static TimerTaskRotate instance;
//#     
//#     private TimerTaskRotate() {
//#         stop=true;
//#         exit=false;
//#          start();
//#      }
//#     
//#     public static void startRotate(int max, VirtualList list){
//#         //Windows mobile J9 hanging test
//#         if (Phone.PhoneManufacturer()==Phone.WINDOWS) {
//#             list.showBalloon=true;
//#             list.offset=0;
//#             return;
//#         }
//#         if (instance==null) instance=new TimerTaskRotate();
//#         list.offset=0;
//#         if (max<0) {
//#             instance.destroyTask(); return;
//#         }
//#         
//#          synchronized (instance) {
//#              list.offset=0;
//#              instance.scrollLen=max;
//#             //list.showBalloon=false; //<< uncomment this to disable keep balloon floating when traversing
//#              instance.balloon=(list.showBalloon)? 6 : 13;
//#              instance.scroll=7;
//#              instance.attachedList=list;
//#              instance.stop=false;
//#          }
//#      }
//#     
//#      public void run() {
//#          // РїСЂРѕРєСЂСѓС‚РєР° С‚РѕР»СЊРєРѕ СЂР°Р·
//#          //stickyWindow=false;
//#          
//#          while (true) {
//#              if (exit) return;
//#              try {  sleep(300);  } catch (Exception e) {}
//#              if (stop) continue;
//#              
//#             boolean redraw = false; 
//#             synchronized (this) {
//#                 //System.out.println("b:"+scrollLen+" scroll="+scroll+" balloon="+balloon + " stop=" + stop);
//# 
//#                 if (attachedList!=null) stop=attachedList.probeTime(); else stop=true;
//# 
//#                 if (scrollLen>=0 || balloon>=0) { 
//#                     stop=false;
//#                     redraw=true;
//#                 }
//#                 if (stop) {
//#                     if (attachedList!=null) attachedList.offset=0;
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
//#                          attachedList.offset=0;
//#                      } else attachedList.offset+=20;
//#                  }
//# 
//#                 //balloon state machine
//#                 if (balloon>=0) balloon--;
//#                 attachedList.showBalloon=(balloon<7 && balloon>0);
//# 
//#                 //if (redraw) attachedList.redraw();
//#              }
//#              if (redraw) attachedList.redraw();
//#          }
//#      }
//#      public void destroyTask(){
//#          synchronized (this) { 
//#              if (attachedList!=null) attachedList.offset=0;
//#              stop=true; 
//#             //attachedList=null;
//#          }
//#      }
//#  }
//#endif