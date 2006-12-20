/*
 * XMLParser.java
 *
 * Created on 30. listopad 2003, 12:56
 */

package mojab.xml;

import java.io.*;
import java.lang.*;
import java.util.Vector;

/**
 * This class implements a minimal XML parser based on the finite-state
 * automaton. No xml checking is done, more or less valid XML is expected.
 *
 * @author  radek
 */
public class XMLParser 
{
    private static final int S_OUT=0;          //out
    private static final int S_OPEN=1;         //after <
    private static final int S_TAGNAME=2;      //tag name
    private static final int S_WAIT_ATTR=3;    //waiting for attr name
    private static final int S_ATTR_NAME=4;    //processing attr name
    private static final int S_WAIT_EQ=5;      //processing =
    private static final int S_WAIT_VALUE=6;   //waiting for " or '
    private static final int S_ATTR_VALUE1=7;  //processing value in ""
    private static final int S_ATTR_VALUE2=8;  //processing value in ''
    private static final int S_BACKSLASH=9;    //after \\
    private static final int S_WAIT_CLOSE=10;  //waiting >
    
    private DataInputStream input;             //the stream being processed
    private XMLEventListener listener;         //external event processor
    
    private int state = S_OUT;
    private String text = "";
    private String tagname = "";
    private boolean closetag = false;
    private boolean emptytag = false;
    private String attrname = "";
    private String attrval = "";
    private Vector attributes = new Vector();
    
    /** Creates a new instance of XMLParser */
    public XMLParser(InputStream stream, XMLEventListener list)
    {
        input = new DataInputStream(stream);
        listener = list;
    }

    /**
     * Parse the input until no more data is available
     */
    public int parseNext()
    {
        int r = 0;
        try {
            while (input.available() > 0)
            {
                int i = input.read();
                if (i > 0)
                    automaton((char) i);
                else
                    break;
                r++;
            }
        } catch (IOException e) {
            listener.XMLError(e.getMessage());
            r = -1;
        }
        return r;
    }
    
    //======================================================================

    private void error(char ch)
    {
        //System.err.println("Error: char '"+ch+"', state "+state);
        listener.XMLError("char '"+ch+"', state "+state);
    }
    
    private void tagStarted()
    {
        if (text.length() > 0)
            listener.Text(decodeXML(text));
        text = "";
    }
    
    private void tagFinished()
    {
        tagname = tagname.toLowerCase();
        if (emptytag)
        {
            listener.TagStart(tagname, attributes);
            listener.TagEnd(tagname);
        }
        else if (closetag)
            listener.TagEnd(tagname);
        else
            listener.TagStart(tagname, attributes);
        
        state = S_OUT;
    }
    
    private void attrFinished()
    {
        attributes.addElement(new Attribute(attrname, decodeXML(attrval)));
    }
    
    private void automaton(char ch)
    {
        switch (state)
        {
            case S_OUT:
                if (ch == '<')
                {
                    state = S_OPEN;
                    closetag = false;
                    emptytag = false;
                    tagname = "";
                    attributes.removeAllElements();
                    tagStarted();
                }
                else 
                    text = text + ch;
                break;

            case S_OPEN:
                if (ch == '/' && !closetag)
                    closetag = true;
                else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <='Z'))
                {
                    tagname = String.valueOf(ch);
                    state = S_TAGNAME;
                }
                else if (ch == '?')
                {
                }
                else
                    error(ch);
                break;

            case S_TAGNAME:
                if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <='Z') ||
                    (ch >= '0' && ch <= '9') || ch == ':' || ch == '_')
                {
                    tagname = tagname + ch;
                }
                else if (ch == '/')
                {
                    emptytag = true;
                    state = S_WAIT_CLOSE;
                }
                else if (ch == '>')
                {
                    tagFinished();
                    state = S_OUT;
                }
                else
                    state = S_WAIT_ATTR;
                break;

            case S_WAIT_ATTR:
                if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <='Z') ||
                    (ch >= '0' && ch <= '9') || ch == ':' || ch == '_')
                {
                    attrname = String.valueOf(ch);
                    state = S_ATTR_NAME;
                }
                else if (ch == '/')
                {
                    emptytag = true;
                    state = S_WAIT_CLOSE;
                }
                else if (ch == '>')
                {
                    tagFinished();
                    state = S_OUT;
                }
                break;
                
            case S_ATTR_NAME:
                if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <='Z') ||
                    (ch >= '0' && ch <= '9') || ch == ':' || ch == '_')
                {
                    attrname = attrname + ch;
                }
                else if (ch == '=')
                    state = S_WAIT_VALUE;
                else
                    state = S_WAIT_EQ;
                break;
                
            case S_WAIT_EQ:
                if (ch == '=')
                    state = S_WAIT_VALUE;
                break;
                
            case S_WAIT_VALUE:
                if (ch == '"')
                {
                    attrval = "";
                    state = S_ATTR_VALUE1;
                }
                else if (ch == '\'')
                {
                    attrval = "";
                    state = S_ATTR_VALUE2;
                }
                else if (ch == '/')
                {
                    emptytag = false;
                    state = S_WAIT_CLOSE;
                }
                else if (ch == '?')
                {
                    state = S_WAIT_CLOSE;
                }
                else if (ch == '>')
                {
                    tagFinished();
                    state = S_OUT;
                }
                else
                    error(ch);
                break;
                
            case S_ATTR_VALUE1:
                if (ch == '"')
                {
                    attrFinished();
                    state = S_WAIT_ATTR;
                }
                else
                    attrval = attrval + ch;
                break;
                
            case S_ATTR_VALUE2:
                if (ch == '\'')
                {
                    attrFinished();
                    state = S_WAIT_ATTR;
                }
                else
                    attrval = attrval + ch;
                break;

            case S_WAIT_CLOSE:
                if (ch == '>')
                {
                    tagFinished();
                    state = S_OUT;
                }
                else
                    error(ch);
                
        }
    }
    
    //========================== String encoding =============================
    
    /**
     * Decodes XML UTF-8 string
     */
    private String decodeXML(String s)
    {
        StringBuffer result = new StringBuffer();
        StringBuffer sequence = new StringBuffer();
        boolean inseq = false;
        int base = 0;
        int remain = 0;
        for (int i = 0; i < s.length(); i++)
        {
            int ch = s.charAt(i);
            char c = '\0';
            if ((ch & 0xc0) == 128)
            {
                base = base << 6 | ch & 0x3f;
                if(--remain == 0)
                    c = (char) base;
            } 
            else if ((ch & 0x80) == 0)
            {
                c = (char) ch;
            }
            else if ((ch & 0xe0) == 192)
            {
                base = ch & 0x1f;
                remain = 1;
            } 
            else if ((ch & 0xf0) == 224)
            {
                base = ch & 0xf;
                remain = 2;
            } 
            else if ((ch & 0xf8) == 240)
            {
                base = ch & 7;
                remain = 3;
            } 
            else if ((ch & 0xfc) == 248)
            {
                base = ch & 3;
                remain = 4;
            } 
            else
            {
                base = ch & 1;
                remain = 5;
            }

            if (c != 0)
                if (c == '&')
                    inseq = true;
                else if (c == ';')
                {
                    inseq = false;
                    if(sequence.toString().equals("lt"))
                        result.append('<');
                    else if(sequence.toString().equals("gt"))
                        result.append('>');
                    else if(sequence.toString().equals("quot"))
                        result.append('"');
                    else if(sequence.toString().equals("amp"))
                        result.append('&');
                    else if(sequence.toString().equals("apos"))
                        result.append('\'');
                    else
                        result.append('?');
                    sequence.delete(0, sequence.length());
                } 
                else
                {
                    if (inseq)
                        sequence.append(c);
                    else
                        result.append(c);
                }
        }

        return result.toString();
    }
    
}
