/*
  Copyright (c) 2000,2001 Al Sutton (al@alsutton.com)
  All rights reserved.
  Redistribution and use in source and binary forms, with or without modification, are permitted
  provided that the following conditions are met:
 
  1. Redistributions of source code must retain the above copyright notice, this list of conditions
  and the following disclaimer.
 
  2. Redistributions in binary form must reproduce the above copyright notice, this list of
  conditions and the following disclaimer in the documentation and/or other materials provided with
  the distribution.
 
  Neither the name of Al Sutton nor the names of its contributors may be used to endorse or promote
  products derived from this software without specific prior written permission.
 
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
  THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.alsutton.jabber;
import Client.Account;
import Client.Config;
import Client.StaticData;
import io.Utf8IOStream;
import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import com.alsutton.jabber.datablocks.*;
import com.alsutton.xmlparser.*;
import locale.SR;



/**
 * The stream to a jabber server.
 */

public class JabberStream implements XMLEventListener, Runnable {
    
    private Utf8IOStream iostream;
    
    /**
     * The dispatcher thread.
     */
    
    private JabberDataBlockDispatcher dispatcher;
    
    //private Vector sendQueue;
    
    private boolean rosterNotify;
    
    private String server; // for ping
    
    public boolean pingSent;
	
    public boolean loggedIn;
    
    public void enableRosterNotify(boolean en){ rosterNotify=en; }
    
    /**
     * Constructor. Connects to the server and sends the jabber welcome message.
     *
     */
    
    public JabberStream( String server, String hostAddr, boolean xmppV1, String proxy, JabberListener theListener )
    throws IOException {
        this.server=server;
        boolean waiting=Config.getInstance().istreamWaiting;
        
         StreamConnection connection;
         if (proxy==null) {
             connection = (StreamConnection) Connector.open(hostAddr);
          } else {
//#if HTTPCONNECT
//#             connection = io.HttpProxyConnection.open(hostAddr, proxy);
//#elif HTTPPOLL  
//#             connection = new io.HttpPollingConnection(hostAddr, proxy);
//#else            
            throw new IllegalArgumentException ("no proxy supported");
//#endif            
         }
 
        iostream=new Utf8IOStream(connection);
        //iostream.setStreamWaiting(waiting);
    
        dispatcher = new JabberDataBlockDispatcher(this);
        if( theListener != null ) {
            setJabberListener( theListener );
        }
        
     
        new Thread( this ). start();
        
        initiateStream(server, xmppV1, SR.MS_XMLLANG);
    }

    public void initiateStream(final String server, final boolean xmppV1, String xmlLang) throws IOException {
        StringBuffer header=new StringBuffer("<stream:stream to='" );
        header.append( server );
        header.append( "' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'" );
        if (xmppV1) header.append(" version='1.0'");
        if (xmlLang!=null) {
            header.append(" xml:lang='");
            header.append(xmlLang);
            header.append("'");
        }
        header.append( '>' );
        send(header.toString());
        header=null;
    }
	
    public void startKeepAliveTask(){
        Account account=StaticData.getInstance().account;
        if (account.keepAliveType==0) 
            return;
        keepAlive=new TimerTaskKeepAlive(account.keepAlivePeriod, account.keepAliveType);
    }
    
    /**
     * The threads run method. Handles the parsing of incomming data in its
     * own thread.
     */
    
    public void run() {
        try {
             XMLParser parser = new XMLParser( this );
             parser.parse( iostream );
             //dispatcher.broadcastTerminatedConnection( null );
        } catch( Exception e ) {
             System.out.println("Exception in parser:");
             e.printStackTrace();
             dispatcher.broadcastTerminatedConnection(e);
         }
     }
    
    /**
     * Method to close the connection to the server and tell the listener
     * that the connection has been terminated.
     */
    
    public void close() {
        if (keepAlive!=null) keepAlive.destroyTask();
        
        dispatcher.setJabberListener( null );
        try {
            //TODO: see FS#528
            try {  Thread.sleep(500); } catch (Exception e) {}
             send( "</stream:stream>" );
            int time=10;
            while (dispatcher.isActive()) {
                try {  Thread.sleep(500); } catch (Exception e) {}
                if ((--time)<0) break;
            }
             //connection.close();
        } catch( IOException e ) {
            // Ignore an IO Exceptions because they mean that the stream is
            // unavailable, which is irrelevant.
        } finally {
            dispatcher.halt();
	    iostream.close();
        }
    }
    
    /**
     * Method of sending data to the server.
     *
     * @param The data to send to the server.
     */
    public void sendKeepAlive(int type) throws IOException {
        switch (type){
            case 4:
                if (pingSent) {
                    dispatcher.broadcastTerminatedConnection(new Exception("Version Ping Timeout"));
                } else {
                    //System.out.println("Version Ping myself");
                    versionPing();
                }
                break;
            case 3:
                if (pingSent) {
                    dispatcher.broadcastTerminatedConnection(new Exception("Ping Timeout"));
                } else {
                    //System.out.println("Ping myself");
                    ping();
                }
                break;
             case 2:
                send("<iq/>");
                 break;
             case 1:
                 send(" ");
         }
     }
    
    public void send( String data ) throws IOException {
	iostream.send(new StringBuffer(data));
    }
    
    public void sendBuf( StringBuffer data ) throws IOException {
	iostream.send(data);
    }
    
    /**
     * Method of sending a Jabber datablock to the server.
     *
     * @param block The data block to send to the server.
     */
    
    public void send( JabberDataBlock block )  {
        new SendJabberDataBlock(block);
    }
    
    /**
     * Set the listener to this stream.
     */
    
    public void addBlockListener(JabberBlockListener listener) { 
        dispatcher.addBlockListener(listener);
    }
    public void cancelBlockListener(JabberBlockListener listener) { 
        dispatcher.cancelBlockListener(listener);
    }
    
    public void cancelBlockListenerByClass(Class removeClass) {
        dispatcher.cancelBlockListenerByClass(removeClass);
    }
    
    public void setJabberListener( JabberListener listener ) {
        dispatcher.setJabberListener( listener );
    }
    
    /**
     * The current class being constructed.
     */
    
    private JabberDataBlock currentBlock;
    
    /**
     * Method called when an XML tag is started in the stream comming from the
     * server.
     *
     * @param name Tag name.
     * @param attributes The tags attributes.
     */
    
    public boolean tagStarted( String name, Vector attributes ) {
        if (currentBlock!=null){
            
            currentBlock = new JabberDataBlock( name, currentBlock, attributes );
            // photo reading
            if ( name.equals("BINVAL") ){
                return true;
            }
            if (rosterNotify) if (name.equals("item")) dispatcher.rosterNotify();
            
        } else if ( name.equals( "stream:stream" ) ) {
            JabberDataBlock stream=new JabberDataBlock( name, null, attributes );
            String SessionId=stream.getAttribute("id");
            dispatcher.broadcastBeginConversation(SessionId);
        } else if ( name.equals( "message" ) )
            currentBlock = new Message( currentBlock, attributes );
        else if ( name.equals("iq") )
            currentBlock = new Iq( currentBlock, attributes );
        else if ( name.equals("presence") )
            currentBlock = new Presence( currentBlock, attributes );
        else currentBlock = new JabberDataBlock(name, null, null);
        return false;
    }
    
    /**
     * Method called when some plain text is encountered in the XML stream
     * comming from the server.
     *
     * @param text The plain text in question
     */
    
    public void plaintextEncountered( String text ) {
        if( currentBlock != null ) {
            currentBlock.setText( text );
        }
    }
    
    public void binValueEncountered( byte binVaule[] ) {
        if( currentBlock != null ) {
            //currentBlock.addText( text );
            currentBlock.addChild(binVaule);
        }
    }
    
    /**
     * The method called when a tag is ended in the stream comming from the
     * server.
     *
     * @param name The name of the tag that has just ended.
     */
    
    public void tagEnded( String name ) throws EndOfXMLException {
        if( currentBlock == null ) {
            if ( name.equals( "stream:stream" ) ) {
                dispatcher.halt();
                iostream.close();
                throw new JabberStreamShutdownException("Normal stream shutdown");
            }
            return;
        }
        
        if (currentBlock.childBlocks!=null) currentBlock.childBlocks.trimToSize();
        
        JabberDataBlock parent = currentBlock.getParent();
        if( parent == null ) {
            if (currentBlock.getTagName().equals("stream:error")) {
                XmppError xe=XmppError.decodeStreamError(currentBlock);
                
                dispatcher.halt();
                iostream.close();
                throw new JabberStreamShutdownException("Stream error: "+xe.toString());
                
            }
            dispatcher.broadcastJabberDataBlock( currentBlock );
            //System.out.println(currentBlock.toString());
        } else
            parent.addChild( currentBlock );
        currentBlock = parent;
    }
    private void versionPing() {
        JabberDataBlock ping=new Iq(null, Iq.TYPE_GET, "ping");
        ping.addChildNs("query", "jabber:iq:version");
        pingSent=true;
        send(ping);
    }

    private void ping() {
        JabberDataBlock ping=new IqPing(null, "ping");
        pingSent=true;
        send(ping);
    }

//#if ZLIB
    public void setZlibCompression() {
        iostream.setStreamCompression();
    }
//#endif
    
     public String getStreamStats() {
         return iostream.getStreamStats();
     }
    
    public int getBytesIn() {
        return iostream.getBytesR();
    }
    public int getBytesOut() {
        return iostream.getBytesS();
    }

    public long getBytes() {
        return iostream.getBytes();
    }
    
     private class TimerTaskKeepAlive extends TimerTask{
         private Timer t;
        private int verifyCtr;
        private int period;
        private int type;
        public TimerTaskKeepAlive(int periodSeconds, int type){
             t=new Timer();
            this.type=type;
            this.period=periodSeconds;
            long periodRun=periodSeconds*1000; // milliseconds
            t.schedule(this, periodRun, periodRun);
         }
         public void run() {
             try {
                 //System.out.println("Keep-Alive");
                 sendKeepAlive(type);
                 System.gc();
            } catch (Exception e) { 
                dispatcher.broadcastTerminatedConnection(e);
                //e.printStackTrace(); 
            }
         }
	
        public void destroyTask(){
            if (t!=null){
                this.cancel();
                t.cancel();
                t=null;
            }
        }
    }
    
    private TimerTaskKeepAlive keepAlive;
    
    private class SendJabberDataBlock implements Runnable {
        private JabberDataBlock data;
        public SendJabberDataBlock(JabberDataBlock data) {
            this.data=data;
            new Thread(this).start();
        }
        public void run(){
            try {
		Thread.sleep(100);
                StringBuffer buf=new StringBuffer();
                data.constructXML(buf);
                sendBuf( buf );
                buf=null;
            } catch (Exception e) {
                //e.printStackTrace(); 
            }
        }
    }
}

