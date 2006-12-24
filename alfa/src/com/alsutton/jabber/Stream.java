package com.alsutton.jabber;
import Client.Config;
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

    public Vector RosterContacts;

    private int afterEol;

    private Vector RosterContactsTable;
    
    public void enableRosterNotify(boolean en){ rosterNotify=en; }
    
    /**
     * Constructor. Connects to the server and sends the jabber welcome message.
     *
     */
    
    public Stream( String server, String hostAddr) throws IOException {
        this.server=server;
        //boolean waiting=Config.getInstance().istreamWaiting;

        if (initiateAuth()!=null) {
            getMyId();
            
        } else {
            return;
        }

        //iostream=new Utf8IOStream(connection);
        //iostream.setStreamWaiting(waiting);
        
        //dispatcher = new JabberDataBlockDispatcher();
        //if( theListener != null ) {
        //    setJabberListener( theListener );
        //}
        

        //new Thread( this ). start();
        getRoster();
        StaticData sd=StaticData.getInstance();
        sd.roster.updateRoster(RosterContacts);

        if (initiateLogin()!=null) initiateLogin();
    }

    public String initiateAuth() throws IOException {
        StringBuffer buf=new StringBuffer();
        try {            
            String uri ="socket://81.176.79.141:80";
            String body="redirect=%2F&act=auth&auth2_login=adeen&auth2_pwd=336699&auth2_save=on";

            StreamConnection conn = (StreamConnection) Connector.open( uri );

            PrintStream out = new PrintStream(conn.openOutputStream());
            out.print( "POST /auth.phtml HTTP/1.0\r\n" +
                    "Content-Type: application/x-www-form-urlencoded\r\n" +
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
            String uri ="socket://81.176.79.150:80";
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
                result=buf.toString().substring(i+4, buf.toString().length());
            }
        }
        
        RosterParser(strconv.convUnicodeToCp1251(result));
        
        //RosterContacts=new Vector(RosterContactsTable.size());
        
        System.out.println(strconv.convUnicodeToCp1251(result));
        return result;
    }
    
    public String getMyId() throws IOException {
        
        StringBuffer buf=new StringBuffer();
        try {            
            String uri ="socket://81.176.79.150:80";
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
        String uri ="socket://81.176.79.150:80";
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
                result=myId=buf.toString().substring(i+4, buf.toString().length());
            }
        }
        
        System.out.println(strconv.convUnicodeToCp1251(result));
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

    public Vector RosterParser(String data) {
	Vector v = new Vector();
        RosterContacts=new Vector();
        
	try {
            while (data.indexOf("\r\n")>-1) {
                String line=data.substring(0,data.indexOf("\r\n"));
                RosterContacts.addElement(line.substring(0,data.indexOf(" ")));
                data=data.substring(data.indexOf("\r\n")+2,data.length());
            }
             
            
/*            
	    while(data!=null) {
                
		String line=data.substring(0,data.indexOf("/r/n"));
                
                System.out.println(line);
                
                data=data.substring(data.indexOf("/r/n")+1,data.length());
                
                RosterContacts.addElement(line);
                
                
                // String key, value;
		if (line.length()<3) data=null;

                String cell=null;
                try {
                    int indexTab=line.indexOf(0x20);
                    
                    if (indexTab<=0) continue; // process next line
                    
                    key=line.substring(0, indexTab);
                    value=line.substring(indexTab+1, line.length() );
                    hash.put(key, value);
                } catch (Exception e) { e.printStackTrace(); }
	    }
 */
        } catch (Exception e)	{ }
	return v;
    }
    
    public void cancelBlockListenerByClass(Class removeClass) {
        dispatcher.cancelBlockListenerByClass(removeClass);
    }
}
