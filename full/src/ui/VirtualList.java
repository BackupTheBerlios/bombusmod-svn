/*
 * VirtualList.java
 *
 * Created on 30 Январь 2005 г., 14:46
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;
import Info.Version;
import javax.microedition.lcdui.*;
import java.util.*;
import Client.*;
import ui.controls.Balloon;
import ui.controls.InputBox;
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
    protected int getTitleBGndRGB() {return Colors.BAR_BGND;} 

    /**
     * цвет текста заголовка
     * @return RGB-цвет текста заголовка
     */
    protected int getTitleRGB() {return Colors.BAR_INK;} 
    
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
    public final static int MOTOROLA_FLIP=-200;
    public static final int MOTOE680_VOL_UP=-9;
    public static final int MOTOE680_VOL_DOWN=-8;
    public static final int MOTOE680_REALPLAYER=-6;
    public static final int MOTOE680_FMRADIO=-7;
    public static final int SE_GREEN=0;
    
    private final static int STRING_SZ=15;

    public static int keyClear=-8;
    public static int keyVolDown=0x1000;
    public static int greenKeyCode=SIEMENS_GREEN;
    public static boolean fullscreen=false;
    public static boolean memMonitor;
    
    /** метрика экрана */
    int width;
    int height;
    
    /** экранный буфер для скрытой отрисовки. используется, если платформа 
     * не поддерживает двойную буферизацию экрана
     */
    private Image offscreen;
    
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
    
    protected VirtualElement title;
    
    protected InputBox bottom;
    
    private boolean wrapping = true;
    
    private GradientItem gradientItem;

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
    public ComplexString getTitleItem() {return (ComplexString)title;}
    public void setTitleItem(ComplexString title) { this.title=title; }
    
    public InputBox getBottomItem() {return (InputBox)bottom;}
    public void setInputBoxItem(InputBox bottom) { this.bottom=bottom; }
    
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
        // rotator
        rotator=new TimerTaskRotate(0);
//#if !(MIDP1)
        //addCommand(cmdSetFullScreen);
        setFullScreenMode(fullscreen);
//#endif
	
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
	Graphics g=(offscreen==null)? graphics: offscreen.getGraphics();
        // заголовок окна
        
        beginPaint();
        
        int list_top=0; // верхняя граница списка
        updateLayout(); //fixme: только при изменении списка
        
        if (title!=null) {
            list_top=title.getVHeight();
            g.setClip(0,0, width, list_top);
            //g.setColor(getTitleBGndRGB());
            //g.fillRect(0,0, width, list_top);
            this.gradientItem = new GradientItem(width, list_top);
            this.gradientItem.setTopColor( 0xE0B000 );
            this.gradientItem.setMidColor( 0xFCCA0F );
            this.gradientItem.setBottomColor( 0xCD9030 );
            this.gradientItem.paint(g,width, list_top);

            g.setColor(getTitleRGB());
            title.drawItem(g,0,false);
        }

        drawHeapMonitor(g);
        if (bottom!=null) {
            winHeight=height-list_top-bottom.getVHeight();
        } else {
            winHeight=height-list_top;
        }


        itemBorder[0]=list_top;
        
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
        int displayedBottom=list_top;
   
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
                setAbsOrg(g, 0, list_top);
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
		displayedBottom=itemBorder[++displayedIndex]=list_top+itemYpos+lh;
            }
        } catch (Exception e) { atEnd=true; }

        // очистка остатка окна
        int clrH=height-displayedBottom;
        if (clrH>0) {
            setAbsOrg(g, 0,displayedBottom);
            g.setClip(0, 0, itemMaxWidth, clrH);
            g.setColor(Colors.LIST_BGND);
            //g.setColor(VL_CURSOR_OUTLINE);
            g.fillRect(0, 0, itemMaxWidth, clrH);
        }

        // рисование скроллбара
        //g.setColor(VL_BGND);
        if (scroll) {
	    
            setAbsOrg(g, 0, list_top);
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
        if (bottom!=null) {
            setAbsOrg(g, 0, height-bottom.getVHeight());  
            g.setClip(0,0, width, height);
            g.setColor(getTitleBGndRGB());
            g.fillRect(0,0, width, height);
            g.setColor(getTitleRGB());
            bottom.drawItem(g,0,false);
        }
	if (offscreen!=null) graphics.drawImage(offscreen, 0,0, Graphics.TOP | Graphics.LEFT );
	//full_items=fe;
    }

    protected void drawBalloon(final Graphics g, int balloon, final String text) {
        setAbsOrg(g,0,balloon);
        Balloon.draw(g, text);
    }

    private void drawHeapMonitor(final Graphics g) {
        if (memMonitor) {
            //int ram=(int)((Runtime.getRuntime().freeMemory()*32)/Runtime.getRuntime().totalMemory());
            //g.setColor(0xffffff);  g.fillRect(width-34,0,34,3);
            //g.setColor(0x00007f);  g.fillRect(width-33,1,ram,2);
            int freemem=(int)Runtime.getRuntime().freeMemory();
            int totalmem=(int)Runtime.getRuntime().totalMemory();
            int ram=(int)((freemem*width)/totalmem);
            g.setColor(Colors.HEAP_TOTAL);  g.fillRect(0,0,width,1);
            g.setColor(Colors.HEAP_FREE);  g.fillRect(0,0,ram,1);
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
        //win_top=0;
        if (cursor>0) {
            cursor=0;
            //focusedItem(0);
        }
        setRotator();
    }

    /**
     * перемещение курсора в конец списка
     */
    public void moveCursorEnd(){
        stickyWindow=true;
        int count=getItemCount();
        //win_top=count-visibleItemsCnt(count-1, -1);
        if (cursor>=0) {
            cursor=(count==0)?0:count-1;
            //focusedItem(cursor);
        }
        //win_top=(listHeight>winHeight)? listHeight-winHeight:0;
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
        //else if ((!force) && stickyWindow) return;
        
        cursor=index;
        stickyWindow=true;
        
        repaint();
        //moveCursor(index-cursor, force); 
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

    /*public void moveCursorTo(Object focused){
        int count=getItemCount();
        for (int index=0;index<count;index++){
            if (focused==getItemRef(index)) {
                moveCursorTo(index);
                break;
            }
        }
    }
     */
    /** код удерживаемой кнопки */
    protected int kHold;
    
    protected void keyRepeated(int keyCode){ key(keyCode); }
    protected void keyReleased(int keyCode) { kHold=0; }
    protected void keyPressed(int keyCode) { kHold=0; key(keyCode);  }
    
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
       
                if (keyCode==-4)  {
                    if (Version.getPlatformName().indexOf("SIE") > -1) {
                        destroyView();
			return;
                    }         
                }
                if (keyCode==702)  {
                    if (Version.getPlatformName().indexOf("SIE") > -1) {
                        destroyView();
			return;
                    }         
                }

        switch (keyCode) {
            case 0: break;
            case NOKIA_PEN: { destroyView(); break; }
            case MOTOE680_VOL_UP:
            case KEY_NUM1:  { moveCursorHome();    break; }
            case KEY_NUM7:  { moveCursorEnd();     break; }
            case '5':{ eventOk(); break; }
            case MOTOROLA_FLIP: break;
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
                            if (keyCode=='#') { System.gc(); break; }
                            userKeyPressed(keyCode);
                    }
                } catch (Exception e) {/* IllegalArgumentException @ getGameAction */}
        }

        
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
            win_top+=winHeight-STRING_SZ;
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
            win_top-=winHeight-STRING_SZ;
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
        focusedItem(cursor);
        rotator.destroyTask();
        if (getItemCount()<1) return;
        if (cursor>=0) {
            int itemWidth=getItemRef(cursor).getVWidth();
            if (itemWidth>=width-scrollbar.getScrollWidth() ) itemWidth-=width/2; else itemWidth=0;
            rotator=new TimerTaskRotate( itemWidth );
        }
    }
    // cursor rotator
    
    private class TimerTaskRotate extends TimerTask{
        private Timer t;
        private int Max;
        private int balloon;
        
        public TimerTaskRotate(int max){
            offset=0;
            balloon=6;
            //if (max<1) return;
            Max=max;
            t=new Timer();
            t.schedule(this, 2000, 300);
        }
        public void run() {
            // прокрутка только раз
            //stickyWindow=false;
            
            if (Max==-1 && balloon==-1) cancel();
            if (offset>=Max) {
                Max=-1;
                offset=0;
            } else offset+=20;
            
            if (showBalloon=balloon>=0) balloon--;
            redraw();
            //System.out.println("Offset "+offset);
        }
        public void destroyTask(){
            offset=0;
            if (t!=null){
                this.cancel();
                t.cancel();
                t=null;
            }
        }
    }
    private TimerTaskRotate rotator;

    
    /**
     * рисование прямоугольного курсора
     * @param g графический контекст рисования
     * @param width ширина курсора
     * @param height высота курсора
     */
    protected void drawCursor (Graphics g, int width, int height){
        //g.setColor(VL_CURSOR_SHADE);   g.drawRoundRect(x+2, y+2, width-1, height-1, 3,3);
        //g.setColor(Colors.CURSOR_BGND);    g.fillRect(1, 1, width-1, height-1);
        
            this.gradientItem = new GradientItem(width, height);
            this.gradientItem.setTopColor( 0xE0B000 );
            this.gradientItem.setMidColor( 0xFCCA0F );
            this.gradientItem.setBottomColor( 0xCD9030 );
            this.gradientItem.paint(g,width, height);
            
            g.setColor(0xCD9030); g.drawRect(0, 0, width-1, height-1);
        
        //g.setColor(Colors.CURSOR_OUTLINE); g.drawRect(0, 0, width-1, height-1);
        /*
        g.drawLine(1,0,width-2,0);
        g.drawLine(0,1,0,height-2);
        g.drawLine(0,width-1,0,height-2);
        g.drawLine(1,height-1,width-2,height-1);
         */
    }

    public void setParentView(Displayable parentView){
        this.parentView=parentView;
    }
    
    /**
     * отсоединение от менеджера дисплея текущего виртуального списка, 
     * присоединение к менеджеру предыдущего Displayable
     */
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }

    public int getListWidth() {
        return width-scrollbar.getScrollWidth();
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
}
