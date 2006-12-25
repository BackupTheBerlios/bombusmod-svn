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
    
    private String server; // for ping
    
    public String sessId;

    boolean pingSent;

    public String myId;

    public String RosterContacts;

    private int afterEol;

    private Vector RosterContactsTable;
    
    public void enableRosterNotify(boolean en){ rosterNotify=en; }
    
    /**
     * Constructor. Connects to the server and sends the jabber welcome message.
     *
     */
    
    public Stream( String server, String hostAddr) throws IOException {
        this.server=server;

        boolean waiting=Config.getInstance().istreamWaiting;

        //StreamConnection connection = (StreamConnection) Connector.open(hostAddr);
        //iostream=new Utf8IOStream(connection);
        //iostream.setStreamWaiting(waiting);
       
        /*
        dispatcher = new JabberDataBlockDispatcher();
        if( theListener != null ) {
            setJabberListener( theListener );
        }
        */
     
        //new Thread( this ). start();
        

        if (initiateAuth()!=null) {
            getMyId();
        } else {
            return;
        }

        //new Thread( this ). start();
        getRoster();
        StaticData sd=StaticData.getInstance();
        sd.roster.updateRoster(RosterContacts);

        if (initiateLogin()!=null) initiateLogin();
    }

    public String initiateAuth() throws IOException {
        StringBuffer buf=new StringBuffer();
        String body="redirect=%2F&act=auth&auth2_login=adeen&auth2_pwd=336699&auth2_save=on";

        try {            
            String uri ="socket://damochka.ru:80";

            SocketConnection conn = (SocketConnection) Connector.open( uri );
            conn.setSocketOption(SocketConnection.KEEPALIVE, 1);

            PrintStream out = new PrintStream(conn.openOutputStream());
            
            out.println( "POST /auth.phtml HTTP/1.0" );
            out.print( "Content-Type: application/x-www-form-urlencoded\r\n" +
                    "User-Agent: Damafon 2.1.12.4000\r\n" +
                    "Cookie: VIPID=3062637b04-80236754;\r\n" +
                    "Host: damochka.ru:80\r\n" +
                    "Content-Length: "+body.length()+"\r\n" +
                    "Pragma: no-cache\r\n\r\n"+body );
            out.flush();

            InputStream in = conn.openInputStream();
            int ch;

            while(( ch = in.read() ) != -1 ){
                buf.append((char) ch);
            }
            in.close();
            out.close();
            conn.close();
        }
        catch( ConnectionNotFoundException e ){
            System.out.println( "Socket could not be opened" );
        }
        catch( IOException e ){
            System.out.println( e.toString() );
        }
        String result = null;
                
        if (buf.toString().length()>0) {
            if (buf.toString().indexOf("SITEID")>-1) {
                int i=buf.toString().indexOf("SITEID");
                result=sessId=buf.toString().substring(i+7,i+39);
            }
        }
        
        System.out.println(result);
        return result;
    }
   
    
    public String getRoster() throws IOException {
        
        StringBuffer buf=new StringBuffer();
        try {            
            String uri ="socket://message.damochka.ru:80";
            String body="id="+myId+"&sid="+sessId+"&type=1";
            
            StreamConnection conn = (StreamConnection) Connector.open( uri );
            PrintStream out = new PrintStream(conn.openOutputStream());
            
            out.print( "POST /GETCLIST HTTP/1.0\r\n" +
                    "Pragma: no-cache+\r\n" +
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
        String result = null;
                
        if (buf.toString().length()>0) {
            if (buf.toString().indexOf("\r\n\r\n")>-1) {
                int i=buf.toString().indexOf("\r\n\r\n");
                result=strconv.convCp1251ToUnicode(buf.toString().substring(i+4, buf.toString().length()));
            }
        }
        
        RosterContacts=result;
        
        //RosterContacts=new Vector(RosterContactsTable.size());
        
        //System.out.println(result);
        return result;
    }
    
    public String getMyId() throws IOException {
        
        StringBuffer buf=new StringBuffer();
        try {            
            String uri ="socket://message.damochka.ru:80";
            String body="PHPSESSID="+sessId;
            
            StreamConnection conn = (StreamConnection) Connector.open( uri );
            PrintStream out = new PrintStream(conn.openOutputStream());
            
            out.print( "POST /GETIDFROMSID HTTP/1.0\r\n" +
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
        String result = null;
                
        if (buf.toString().length()>0) {
            if (buf.toString().indexOf("\r\n\r\n")>-1) {
                int i=buf.toString().indexOf("\r\n\r\n");
                result=myId=buf.toString().substring(i+4, buf.toString().length());
            }
        }
        System.out.println(result);
        return result;
    }
 
    public String initiateLogin() throws IOException {
        String uri ="socket://message.damochka.ru:80";
        StreamConnection conn = (StreamConnection) Connector.open(uri);
        StringBuffer buf=new StringBuffer();
        try {      
            String body="PHPSESSID="+sessId+"&df_ver=2.1.12.4000&ver=51";
            
            PrintStream out = new PrintStream(conn.openOutputStream());
            out.print( "POST /DAMAFON HTTP/1.0\r\n" +
                    "Host: message.damochka.ru:80\r\n" +
                    "Content-Type: application/x-www-form-urlencoded\r\n" +
                    "Pragma: no-cache\r\n" +
                    "Content-Length: 68\r\n\r\n"+body );
            out.flush();

            InputStream in = conn.openInputStream();
            int ch;

            while(( ch = in.read() ) != -1 ){
                buf.append((char) ch);
            }
            in.close();
            out.close();
            conn.close();
        }
        catch( ConnectionNotFoundException e ){
            System.out.println( "Socket could not be opened" );
        }
        catch( IOException e ){
            System.out.println( e.toString() );
        }
        String result = buf.toString();

        if (buf.toString().length()>0) {
            if (buf.toString().indexOf("\r\n\r\n")>-1) {
                int i=buf.toString().indexOf("\r\n\r\n");
                result=strconv.convCp1251ToUnicode(buf.toString().substring(i+4, buf.toString().length()));
            }
        }
        
        System.out.println(result);
        
        StaticData sd=StaticData.getInstance();
        
        try {
            while (result.indexOf("type:'0'")>-1) {
                String line=result.substring(0,result.indexOf("type:'0'"));
                result=result.substring(result.indexOf("type:'0'")+2,result.length());

                Vector MessageItem=new Vector();
                MessageItem=MessageParser(line);

                for (Enumeration e=MessageItem.elements(); e.hasMoreElements();){

                        String from=(String)e.nextElement().toString().trim();
                        String text=(String)e.nextElement().toString().trim();

                        Msg m=new Msg(Msg.MESSAGE_TYPE_IN, from, null, text);

                        sd.roster.messageStore(from, m);
                }
            }
        } catch (Exception e) {}
        return result;
    }   
    
    public void run() {
        /*
        try {
            Parser parser = new Parser( this );
            parser.parse( iostream );
            //dispatcher.broadcastTerminatedConnection( null );
        } catch( Exception e ) {
            System.out.println("Exception in parser:");
            e.printStackTrace();
            dispatcher.broadcastTerminatedConnection(e);
        }
        */
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
                "Cookie: SITEID=24825b1ae9ad72a2f5314d70448a0d08; auth2_login=adeen; lastUpdate=1166961844; auth2_clean: ok-1166961769; auth2_pwd: 0%3A2c3bae7a8869af158d85bc766d1e323635b7; VIPID: ; news_last=2; auth2_logged=1; auth2_save=1; meet_nr=1; hotlog=1\r\n\r\n" +
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
}
