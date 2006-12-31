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
import com.alsutton.jabber.JabberBlockListener;
import io.Utf8IOStream;
import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import com.alsutton.jabber.datablocks.*;
import com.alsutton.parser.*;
import util.StringLoader;



/**
 * The stream to a jabber server.
 */

public class JabberStream implements EventListener, Runnable {
    
    private Utf8IOStream iostream;
    
    /**
     * The dispatcher thread.
     */
    
    private JabberDataBlockDispatcher dispatcher;
    
    private String server; // for ping
    
    /**
     * Constructor. Connects to the server and sends the jabber welcome message.
     *
     */
    
    public JabberStream( String server, String hostAddr, boolean xmppV1, String proxy, JabberListener theListener )
    throws IOException {
        this.server=server;
     
        new Thread( this ). start();
    }
    
    /**
     * The threads run method. Handles the parsing of incomming data in its
     * own thread.
     */
    public void run() {
        try {
            //Parser parser = new Parser( this );
            //parser.parse( iostream );
            //dispatcher.broadcastTerminatedConnection( null );
        } catch( Exception e ) {
            //System.out.println("Exception in parser:");
            //e.printStackTrace();
        }
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
    
    public void setJabberListener( JabberListener listener ) {
        dispatcher.setJabberListener( listener );
    }
    
    private JabberDataBlock currentBlock;
    
    public boolean tagStarted( String name, Hashtable attributes ) { return false; }
    
    public void tagEnded( String name ) {}

    public void plaintextEncountered(String text) { }
}
