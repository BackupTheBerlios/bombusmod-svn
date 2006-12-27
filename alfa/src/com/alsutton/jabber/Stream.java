package com.alsutton.jabber;
import Client.Config;
import Client.Msg;
import Client.Roster;
import Client.StaticData;
import com.alsutton.parser.EventListener;
import com.alsutton.parser.Parser;
import com.sun.midp.io.BufferedConnectionAdapter;
import io.Utf8IOStream;
import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import com.alsutton.jabber.datablocks.*;
import login.NonSASLAuth;
import util.StringLoader;
import util.strconv;



/**
 * The stream to a jabber server.
 */

public class Stream implements EventListener, Runnable {
    
    private Utf8IOStream iostream;
    
    /**
     * The dispatcher thread.
     */
    
    private JabberDataBlockDispatcher dispatcher;
    
    private boolean rosterNotify;
    
    public static String sessId;

    public static String myId;

    public String RosterContacts;

    private String userName;

    private String passWord;

    
    public void enableRosterNotify(boolean en){ rosterNotify=en; }
    
    /**
     * Constructor. Connects to the server and sends the jabber welcome message.
     *
     */
    
    public Stream( String userName, String passWord) throws IOException {
        
        this.userName=userName;
        this.passWord=passWord;
        
        new Thread( this ). start();
    }

    private void initiateAuth() throws IOException {
        String uri ="http://damochka.ru/auth.phtml";
        String requeststring="redirect=%2F&act=auth&auth2_login="+userName+"&auth2_pwd="+passWord+"&auth2_save=on";
        byte[] request_body = requeststring.getBytes();
        String buf=null;
        StringBuffer buf2=new StringBuffer();
        
        HttpConnection http = null;
        OutputStream oStrm= null;
        InputStream iStrm = null;
        
        try{
            http = (HttpConnection) Connector.open(uri, Connector.READ_WRITE);
            http.setRequestMethod(HttpConnection.POST);
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setRequestProperty("User-Agent","Damafon 2.1.12.4000");
            http.setRequestProperty("Cookie","VIPID=3062637b04-80236754;");
            http.setRequestProperty("Pragma","no-cache");
            http.setRequestProperty("Host","damochka.ru:80");
            http.setRequestProperty("Content-Length", Integer.toString(requeststring.length()));
            
            oStrm = http.openOutputStream();
            oStrm.write(request_body);

            int sid=30;
            for(int i=0; i < sid; i++) {
                if (http.getHeaderField(i).indexOf("SITEID=")>-1) {
                    buf=http.getHeaderField(i);
                    break;
                }
            }
            
            iStrm = http.openInputStream();
            int ch;
            
            while( ( ch = iStrm.read() ) != -1 ){
                buf2.append((char) ch);
            }
         }
            finally
            {
            // Clean up
            if (iStrm != null) iStrm.close();
            if (oStrm != null) oStrm.close();
            if (http != null) http.close();
        }
                
        if (buf.indexOf("SITEID=")>-1) {
            int i=buf.indexOf("SITEID=")+7;
            int i2=buf.indexOf(";",i);
            sessId=buf.substring(i,i2);
            //System.out.println(sessId);
        }
    }
    
    private void getMyId() throws IOException {
        String uri ="http://message.damochka.ru/GETIDFROMSID";
        String requeststring="PHPSESSID="+sessId;
        byte[] request_body = requeststring.getBytes();
        
        HttpConnection http = null;
        OutputStream oStrm= null;
        InputStream iStrm = null;
        
        
        StringBuffer buf=new StringBuffer();
        try {            
            http = (HttpConnection) Connector.open(uri, Connector.READ_WRITE);
            http.setRequestMethod(HttpConnection.POST);
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setRequestProperty("Pragma","no-cache");
            http.setRequestProperty("Host","message.damochka.ru:80");
            http.setRequestProperty("Content-Length", Integer.toString(requeststring.length()));
            
            oStrm = http.openOutputStream();
            oStrm.write(request_body);


            iStrm = http.openInputStream();
            int ch;
            
            while( ( ch = iStrm.read() ) != -1 ){
                buf.append((char) ch);
            }
        }
        finally
        {
            // Clean up
            if (iStrm != null) iStrm.close();
            if (oStrm != null) oStrm.close();
            if (http != null) http.close();
        }
                
        if (buf.toString().length()>0) {
            myId=buf.toString();
        }
        //System.out.println(myId);
    }
 
    private void getRoster() throws IOException {
        String uri ="http://message.damochka.ru/GETCLIST";
        String requeststring="id="+myId+"&sid="+sessId+"&type=1";
        byte[] request_body = requeststring.getBytes();
        
        HttpConnection http = null;
        OutputStream oStrm= null;
        InputStream iStrm = null;
        
        
        StringBuffer buf=new StringBuffer();
        try {            
            http = (HttpConnection) Connector.open(uri, Connector.READ_WRITE);
            http.setRequestMethod(HttpConnection.POST);
            http.setRequestProperty("Pragma","no-cache");
            http.setRequestProperty("Host","message.damochka.ru:80");
            http.setRequestProperty("Content-Length", Integer.toString(requeststring.length()));
            
            oStrm = http.openOutputStream();
            oStrm.write(request_body);


            iStrm = http.openInputStream();
            int ch;
            
            while( ( ch = iStrm.read() ) != -1 ){
                buf.append((char) ch);
            }
        }
        finally
        {
            // Clean up
            if (iStrm != null) iStrm.close();
            if (oStrm != null) oStrm.close();
            if (http != null) http.close();
        }
        
        if (buf.toString().length()>0) {
            RosterContacts=strconv.convCp1251ToUnicode(buf.toString());
        }
        
        //System.out.println(RosterContacts);
    }

    private void initiateLogin() throws IOException {
        String uri ="http://message.damochka.ru/DAMAFON";
        String requeststring="PHPSESSID="+sessId+"&df_ver=2.1.12.4000&ver=51";
        byte[] request_body = requeststring.getBytes();
        String messagebuffer=null;
        
        HttpConnection http = null;
        OutputStream oStrm= null;
        InputStream iStrm = null;
        
        StringBuffer buf=new StringBuffer();

        try {      
            http = (HttpConnection) Connector.open(uri, Connector.READ_WRITE);
            http.setRequestMethod(HttpConnection.POST);
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setRequestProperty("Pragma","no-cache");
            http.setRequestProperty("Host","message.damochka.ru:80");
            http.setRequestProperty("Content-Length", Integer.toString(requeststring.length()));
            
            oStrm = http.openOutputStream();
            oStrm.write(request_body);


            iStrm = http.openInputStream();
            int ch;
            
            while( ( ch = iStrm.read() ) != -1 ){
                buf.append((char) ch);
            }
        }
        finally
        {
            // Clean up
            if (iStrm != null) iStrm.close();
            if (oStrm != null) oStrm.close();
            if (http != null) http.close();
        }

        if (buf.toString().length()>0) {
            messagebuffer=strconv.convCp1251ToUnicode(buf.toString());
        }
        
        StaticData sd=StaticData.getInstance();
        
        try {
            while (messagebuffer.indexOf("type:'0'")>-1) {
                String line=messagebuffer.substring(messagebuffer.indexOf("type:'0'"),messagebuffer.length());
                messagebuffer=line.substring(messagebuffer.indexOf("type:'0'")+9);

                Vector MessageItem=new Vector();
                MessageItem=MessageParser(line);
                
                sd.roster.errorLog(line);

                for (Enumeration e=MessageItem.elements(); e.hasMoreElements();){

                        String from=(String)e.nextElement().toString().trim();
                        String text=(String)e.nextElement().toString().trim();
                        
                        //System.out.println(text);
                        
                        Msg m=new Msg(Msg.MESSAGE_TYPE_IN, from, null, text);

                        sd.roster.messageStore(from, m);
                }
            }
        } catch (Exception e) {}
        initiateLogin();
    }   
    
    public void run() {
        StaticData sd=StaticData.getInstance();
        try {
            initiateAuth();

            if (sessId!=null) {
                getMyId();
                sd.roster.setProgress("SessId", 45);
            } else {
                //System.out.println("не получили сессию!");
                sd.roster.setProgress("SessId failed", 0);
                sd.roster.errorLog("SessId failed");
                return;
            }

            if (myId!=null) { 
                getRoster();
                sd.roster.setProgress("myId", 50);
            } else {
                //System.out.println("не получили свой id!");
                sd.roster.setProgress("myId failed", 0);
                sd.roster.errorLog("myId failed");
                return;
            }

            if (RosterContacts!=null) {
                sd.roster.updateRoster(RosterContacts);
                sd.roster.setProgress("Roster", 60);
            } else {
                //System.out.println("не получили ростер, он пустой?!");
                sd.roster.setProgress("Roster clear?", 65);
                sd.roster.errorLog("Roster clear?");
            }

            initiateLogin();
            sd.roster.setProgress("Login", 80);
        } catch( Exception e ) {
            System.out.println("Exception:");
            sd.roster.errorLog(e.toString());
            e.printStackTrace();
        }
    }
    
    public Vector MessageParser(String data) {
	Vector v = new Vector();
        String line=null;
        int cnt=0;
        int pos=0;
        int pos2=0;
        int pos3=0;
        
        System.out.println("MessageParser");
        
	try {
            while (data.indexOf("fromid:'",pos)>-1) {
                //int i=messagebuffer.indexOf("type:'")+6;
                //int i2=messagebuffer.indexOf("'",i);
                //sessId=messagebuffer.substring(i,i2);
                
                    pos2=data.indexOf("fromid:'",pos)+8;
                    pos3=data.indexOf("'",pos2);
                    line=data.substring(pos2,pos3);
                    System.out.println(line);
                    pos=pos3;
                    v.addElement(line);
                    line=null;
                    
                    pos2=data.indexOf("text:'",pos)+6;
                    pos3=data.indexOf("'",pos2);
                    line=data.substring(pos2,pos3);
                    System.out.println(line);
                    pos=pos3;
                    v.addElement(line);
                    data=data.substring(pos3,data.length());
                    line=null;
            }
        } catch (Exception e)	{ }
	return v;
    }
    

    public void close() {
        
        //dispatcher.setJabberListener( null );
        
        
        String logoff="POST /auth.phtml?act=logout HTTP/1.0\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n" +
                "User-Agent: Damafon 2.1.12.4000\r\n" +
                "Pragma: no-cache\r\n" +
                "Host: damochka.ru:80\r\n" +
                "Content-Length: 12\r\n" +
                "Cookie: SITEID="+sessId+"; auth2_login="+userName+"; lastUpdate=1166961844; auth2_clean: ok-1166961769; auth2_pwd: 0%3A2c3bae7a8869af158d85bc766d1e323635b7; VIPID: ; news_last=2; auth2_logged=1; auth2_save=1; meet_nr=1; hotlog=1\r\n\r\n" +
                "redirect=%2F";
        try {
            send( logoff );
            try {  Thread.sleep(500); } catch (Exception e) {};
        } catch( IOException e ) {
            // Ignore an IO Exceptions because they mean that the stream is
            // unavailable, which is irrelevant.
        } finally {
	    iostream.close();
            //dispatcher.halt();
        }
    }
    
    public void send( String data ) throws IOException {
        // iostream.send(new StringBuffer(data));
    }

    public void plaintextEncountered(String text) {
    }

    public void setJabberListener( JabberListener listener ) {
        dispatcher.setJabberListener( listener );
    }

    public void addBlockListener(JabberBlockListener listener) { 
        dispatcher.addBlockListener(listener);
    }
    public void cancelBlockListener(JabberBlockListener listener) { 
        dispatcher.cancelBlockListener(listener);
    }

    public void cancelBlockListenerByClass(Class removeClass) {
        dispatcher.cancelBlockListenerByClass(removeClass);
    }
    
    public static void sendMessage(final String to, final String message) {
        StringBuffer buf=new StringBuffer();
        try {            
            String uri ="socket://message.damochka.ru:80";
            String body="fromid="+myId+"&toid="+to+"&myid="+myId+"&ses_id="+sessId+"&smsg="+message+"&font_size=9&font_color=black&background_color=white&sendsms=1&inform=0";

            StreamConnection conn = (StreamConnection) Connector.open( uri );
            PrintStream out = new PrintStream(conn.openOutputStream());
            
            out.print( "POST /SMS HTTP/1.0\r\n" +
                "Pragma: no-cache\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n" +
                "Host: message.damochka.ru:80\r\n" +
                "Content-Length: "+body.length()+"\r\n\r\n"+body);
            out.flush();

            InputStream in = conn.openInputStream();
            int ch;
            
            while( ( ch = in.read() ) != -1 ){
                buf.append((char) ch);
            }
            
            in.close();
            out.close();
            conn.close();
        }
        catch(ConnectionNotFoundException e){ System.out.println("Socket could not be opened"); } catch(IOException e){ System.out.println(e.toString()); }
    }
}
