/*
 * Session.java
 *
 * Created on 7. prosinec 2003, 20:57
 */

import java.lang.*;
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;

import mojab.xml.*;

/**
 *
 * @author  radek
 */
public class Session extends Form 
    implements Runnable, XMLEventListener
{
    public static final int S_ERROR = -1;
    public static final int S_DISC = 0;
    public static final int S_CONN = 1;
    public static final int S_LOGON = 2;
    public static final int S_NORM = 3;

    public static final int S_WAIT_ROSTER = 4;  //roster listing
    public static final int S_WAIT_BODY = 5;    //message body
    public static final int S_IN_BODY = 6;      //wait body end
    
    public static final int S_WAIT_SHOW = 10;  //wait presence <show> tag
    public static final int S_IN_SHOW = 11;
    
    private Mojab mojab;
    private StreamConnection scon;
    private InputStream inp;
    private OutputStream out = null;
    private XMLParser parser;
    private boolean parsingPaused = false;

    private int status = S_DISC;
    
    private String msgfrom = null;
    private String msgbody = null;
    private String presfrom = null;
    private int presstat = 0;
        
    public Session(Mojab mojab)
    {
        super("moJab");
        this.mojab = mojab;
    }
    
    public void connect(String addr, String ipaddr, int port)
    {
        try {
            message("Connecting...");
            System.out.println("add="+addr+" ipaddr="+ipaddr+" port="+port);
            StringBuffer surl = new StringBuffer("socket://");
            surl.append(ipaddr);
            surl.append(":");
            surl.append(port);
            
            scon =
                (StreamConnection) Connector.open(surl.toString());
            inp = scon.openInputStream();
            out = scon.openDataOutputStream();

            StringBuffer s = new StringBuffer("<stream:stream to=\"");
            s.append(addr);
            s.append("\" xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\">");
            out.write(s.toString().getBytes());
            setStatus(S_CONN);
            
            parser = new XMLParser(inp, this);
            Thread parsing = new Thread(this);
            parsing.setPriority(Thread.MIN_PRIORITY);
            parsing.start();
            
        } catch (IOException e) {
            error(e.getMessage());
            e.printStackTrace();
            setStatus(S_ERROR);
        }
        
    }
    
    public void close()
    {
        try {
            if (out != null)
            {
                String s = "</stream:stream>";
                out.write(s.getBytes());
            }
            if (scon != null)
                scon.close();
        } catch (IOException e) {
            error(e.getMessage());
        }
        setStatus(S_DISC);
    }
    
    public void login(String user, String pass, String resource)
    {
        message("Logging in...");
        try {
            StringBuffer s = new StringBuffer("<iq id=\"logon\" type=\"set\"><query xmlns=\"jabber:iq:auth\"><username>");
            s.append(encodeXML(user));
            s.append("</username><password>");
            s.append(encodeXML(pass));
            s.append("</password><resource>");
            s.append(encodeXML(resource));
            s.append("</resource></query></iq>");
            out.write(s.toString().getBytes());
            setStatus(S_LOGON);
        } catch (IOException e) {
            error(e.getMessage());
            setStatus(S_ERROR);
        }
    }
    
    public void presence(String type, String show, String status)
    {
        try {
            StringBuffer s;
            if (type.length() > 0)                
            {
                s = new StringBuffer("<presence type=\"");
                s.append(type);
                s.append("\">");
            }
            else
                s = new StringBuffer("<presence>");
            if (show.length() > 0)
            {
                s.append("<show>");
                s.append(show);
                s.append("</show>");
            }
            if (status.length() > 0)
            {
                s.append("<status>");
                s.append(status);
                s.append("</status>");
            }
            s.append("<priority>-1</priority></presence>");
            out.write(s.toString().getBytes());
        } catch (IOException e) {
            error(e.getMessage());
            setStatus(S_ERROR);
        }
    }
    
    public void getBuddyList()
    {
        try {
            String s = "<iq type=\"get\" id=\"roster\"><query xmlns=\"jabber:iq:roster\" /></iq>";
            out.write(s.getBytes());
        } catch (IOException e) {
            error(e.getMessage());
            setStatus(S_ERROR);
        }
        
    }
    
    public void sendMessage(Message msg)
    {
        try {
            StringBuffer s = new StringBuffer("<message type=\"chat\" to=\"");
            s.append(msg.getJID());
            s.append("\"><body>");
            s.append(encodeXML(msg.getText()));
            s.append("</body></message>");
            out.write(s.toString().getBytes());
        } catch (IOException e) {
            error(e.getMessage());
            setStatus(S_ERROR);
        }        
    }
    
    public int getStatus()
    {
        return status;
    }
    
    public void pause()
    {
        parsingPaused = true;
    }
    
    public void resume()
    {
        parsingPaused = false;
    }

    //=======================================================================
        
    private void message(String s)
    {
        append(s);
    }
    
    private void error(String s)
    {
        for (int i = 0; i < size(); i++)
            delete(i);
        append("Error: "+s);
    }
    
    private void setStatus(int stat)
    {
        status = stat;
        mojab.logic(status);
        //System.out.println("Status: "+stat);
    }
    
    //============================= Runnable ================================

    public void run() 
    {
        do {
            if (!parsingPaused)
                parser.parseNext();
            Thread.yield();
        } while (status != S_ERROR && status != S_DISC);
    }
    
    //========================== XMLEventListener ===========================

    private String attrValue(java.util.Vector attrs, String name)
    {
        String r = null;
        for (int i = 0; i < attrs.size(); i++)
        {
            Attribute attr = (Attribute) attrs.elementAt(i);
            if (attr.getName().equals(name))
            {
                r = attr.getValue();
                break;
            }
        }
        return r;
    }
    
    public void TagStart(java.lang.String name, java.util.Vector attrs) 
    {
        switch (status)
        {
            case S_LOGON:
                if (name.equals("iq") && attrValue(attrs, "id").equals("logon"))
                {
                    if (attrValue(attrs, "type").equals("result"))
                    {
                        message("OK");
                        setStatus(S_NORM);
                    }
                    else if (attrValue(attrs, "type").equals("error"))
                    {
                        error("Unable to log in");
                        setStatus(S_ERROR);
                    }
                }
                break;
            
            case S_NORM:
                if (name.equals("iq") && attrValue(attrs, "id").equals("roster"))
                {
                    mojab.clearRoster();
                    setStatus(S_WAIT_ROSTER);
                }
                else if (name.equals("message"))
                {
                    msgfrom = attrValue(attrs, "from");
                    //System.out.println("message from: "+msgfrom);
                    setStatus(S_WAIT_BODY);
                }
                else if (name.equals("presence"))
                {
                    presfrom = attrValue(attrs, "from");
                    String type = attrValue(attrs, "type");
                    if (type != null && type.equals("unavailable"))
                        presstat = Contact.OFFLINE;
                    else
                        presstat = Contact.ONLINE;
                    setStatus(S_WAIT_SHOW);
                }
                break;
                
            case S_WAIT_ROSTER:
                if (name.equals("item"))
                {
                    String jid = attrValue(attrs, "jid");
                    if (jid != null)
                    {
                        String cname = attrValue(attrs, "name");
                        if (cname == null)
                            cname = jid;
                        mojab.addRosterItem(cname, jid);
                    }
                }
                break;
                
            case S_WAIT_BODY:
                if (name.equals("body"))
                    setStatus(S_IN_BODY);
                break;
                                
            case S_WAIT_SHOW:
                if (name.equals("show"))
                    setStatus(S_IN_SHOW);
                break;
                                
        }
    }
    
    public void TagEnd(java.lang.String name) 
    {
        switch (status)
        {
            case S_WAIT_ROSTER:
                if (name.equals("iq"))
                    setStatus(S_NORM);
                break;

            case S_WAIT_BODY:
                if (name.equals("message"))
                    setStatus(S_NORM);
                break;

            case S_IN_BODY:
                if (name.equals("body"))
                {
                    if (msgbody == null) msgbody = "null";
                    if (msgbody != null)
                    {
                        if (msgfrom == null)
                            msgfrom = "?";
                        Message msg = new Message(msgfrom);
                        msg.setText(msgbody);
                        mojab.incommingMessage(msg);
                        msgfrom = null;
                        msgbody = null;
                    }
                    setStatus(S_NORM);
                }
                break;

            case S_WAIT_SHOW:
                if (name.equals("presence"))
                {
                    mojab.statusChanged(presfrom, presstat);
                    setStatus(S_NORM);
                }
                break;

            case S_IN_SHOW:
                if (name.equals("show"))
                    setStatus(S_WAIT_SHOW);
                break;
        }
    }
    
    public void Text(String text) 
    {
        switch (status)
        {
            case S_IN_BODY:
                if (msgbody == null)
                    msgbody = text;
                else
                    msgbody = msgbody + text;
                break;

            case S_IN_SHOW:
                if (text == null)
                    presstat = Contact.ONLINE;
                else if (text.equals("away"))
                    presstat = Contact.AWAY;
                else if (text.equals("xa"))
                    presstat = Contact.NOTAVAIL;
                else if (text.equals("dnd"))
                    presstat = Contact.DND;
                break;
        }
    }
    
    public void XMLError(String s) 
    {
        error(s);
        setStatus(S_ERROR);
    }
    
    //========================== String encoding =============================

    /**
     * Encodes a string to the XML UTF-8 format
     */
    private String encodeXML(String s)
    {
        StringBuffer result = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
        {
            int ch = s.charAt(i);
            if(ch < 128)
            {
                switch (ch)
                {
                    case 34:
                        result.append("&quot;");
                        break;
                    case 38:
                        result.append("&amp;");
                        break;
                    case 39:
                        result.append("&apos;");
                        break;
                    case 60:
                        result.append("&lt;");
                        break;
                    case 62:
                        result.append("&gt;");
                        break;
                    default:
                        result.append((char) ch);
                }
            }
            else if (ch < 2048)
            {
                result.append((char)(ch >> 6 | 0xc0));
                result.append((char)(ch & 0x3f | 0x80));
            } 
            else if(ch < 0x10000)
            {
                result.append((char)(ch >> 12 | 0xe0));
                result.append((char)(ch >> 6 & 0x3f | 0x80));
                result.append((char)(ch & 0x3f | 0x80));
            }
            else
            {
                result.append((char)(ch >> 18 | 0xf0));
                result.append((char)(ch >> 12 & 0x3f | 0x80));
                result.append((char)(ch >> 6 & 0x3f | 0x80));
                result.append((char)(ch & 0x3f | 0x80));
            }
        }

        return result.toString();
    }

    
}
