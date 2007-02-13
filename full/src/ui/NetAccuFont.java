package ui;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.io.IOException;

public class NetAccuFont {

    public NetAccuFont(String name) {
        loadFont(name);
    }
    
    public static int fontWidth = 0;
    public static int fontHeight = 0;

    static final byte[] emptyByteArray = new byte[0];
    final static int numberStart = 48;
    static int font_offset [] = null;
    static int font_offset_y [] = null;
    static byte font_bbx[] = null;
    static int font_top2bbx = 0;

    public static void drawString(Graphics g, String[] text, int x, int y) {
        for (byte i = 0; i < text.length; i++) {
            drawString(g, text[i], x, y);
            y += fontHeight;
        }
    }
    public static void drawString(Graphics g, String text, int x, int y) {
        starting_x = x;
        starting_y = y;
        byte[] textBytes;
            textBytes = text.getBytes();

            int charnumber = 0;
            for (int i = 0; i < textBytes.length; i++) {

                byte textByte = textBytes[i];

                if (textByte >= 48 && textByte < 58) {//numbers
                    int column = (textByte - numberStart) + 1;
                    paintLetter(g, 1, column, charnumber, textByte);
                }

               
                switch (textByte) {
                    case 38://& gprs
                        paintLetter(g, 1, 18, charnumber, textByte);
                        break;
                    case 40://(
                        paintLetter(g, 1, 16, charnumber, textByte);
                        break;
                    case 41://)
                        paintLetter(g, 1, 17, charnumber, textByte);
                        break;
                    case 43://+ bat
                        paintLetter(g, 1, 11, charnumber, textByte);
                        break;
                    case 45://- net
                        paintLetter(g, 1, 12, charnumber, textByte);
                        break;
                    case 58://:
                        paintLetter(g, 1, 13, charnumber, textByte);
                        break;
                    case 60://< k
                        paintLetter(g, 1, 14, charnumber, textByte);
                        break;
                    case 61://= b
                        paintLetter(g, 1, 15, charnumber, textByte);
                        break;
                    case 32://
                        paintLetter(g, 1, 20, charnumber, textByte);
                        break;
                    case -93://
                        paintLetter(g, 1, 20, charnumber, textByte);
                        break;
                }

                charnumber++;
            }
    }
    
    private static void paintLetter(Graphics g, int row, int column, int charNumber, byte textByte) {
        int y_src = (row - 1) * fontHeight;
        int x_src = (column * fontWidth) - fontWidth;
        int transform = javax.microedition.lcdui.game.Sprite.TRANS_NONE;
        int writeTo_x = starting_x + (charNumber * (fontWidth - FONT_SPACING));
        try {
            g.drawRegion(netaccufonts, x_src, y_src, fontWidth, fontHeight, transform, writeTo_x, starting_y, Graphics.LEFT | Graphics.TOP);
        } catch (Exception e) { }

    }
    
    private static void loadFont(String imageName) {
        try {
            netaccufonts = Image.createImage(imageName);
            fontWidth = netaccufonts.getWidth()/20;
            fontHeight = netaccufonts.getHeight();
        } catch (IOException e) {}
    }

    private static Image netaccufonts;

    public static final byte FONT_SPACING = 0;
    private static int starting_x;
    private static int starting_y;
}