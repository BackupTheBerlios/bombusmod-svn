/*
 * Utf8IOStream.java
 *
 * Created on 18 Декабрь 2005 пїЅ., 0:52
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import javax.microedition.io.*;
import Client.Config;
import util.strconv;

/**
 *
 * @author EvgS
 */
public class Utf8IOStream implements Runnable{
    
    private StreamConnection connection;
    private InputStream inpStream;
    private OutputStream outStream;

    private boolean iStreamWaiting;

    private int bytesRecv;

    private int bytesSent;

    private OutputStreamWriter outputWriter;
    private InputStreamReader inputReader;
    
    /** Creates a new instance of Utf8IOStream */
    public Utf8IOStream(StreamConnection connection) throws IOException {
	this.connection=connection;
        try {
            Config cf=Config.getInstance();
            SocketConnection sc=(SocketConnection)connection;
            sc.setSocketOption(SocketConnection.KEEPALIVE, 1);
        } catch (Exception e) {}
	
	inpStream = connection.openInputStream();
	outStream = connection.openOutputStream();	

        inputReader = new InputStreamReader(inpStream, "UTF-8");
	outputWriter = new OutputStreamWriter(outStream,"UTF-8");
    }
    
    public void send( StringBuffer data ) throws IOException {
	
	synchronized (outStream) {
	    outputWriter.write(data.toString());
	    outStream.flush();
	}
//#if (XML_STREAM_DEBUG)        
//#         System.out.println(">> "+data);
//#endif
    }
    
    public int getNextCharacter()
    throws IOException {
	return inputReader.read();
    }
    
    public void close() {
	try { outputWriter.close(); }  catch (Exception e) {};
	try { inputReader.close();  }  catch (Exception e) {};
	try { outStream.close();    }  catch (Exception e) {};
	try { inpStream.close();    }  catch (Exception e) {};
	new Thread(this).start();
    }

    public void run() {
	try { connection.close();   }  catch (Exception e) {};
    }
    
    public String readLine() throws IOException {
	StringBuffer buf=new StringBuffer();
	
	boolean eol=false;
	while (true) {
	    int c = getNextCharacter();
	    if (c<0) { 
		eol=true;
		if (buf.length()==0) return null;
		break;
	    }
	    if (c==0x0d || c==0x0a) {
		eol=true;
		//inputstream.mark(2);
		if (c==0x0a) break;
	    }
	    else {
		if (eol) {
		    //afterEol=c;
		    //inputstream.reset();
		    break;
		}
		buf.append((char) c);
	    }
	}
	return buf.toString();
    }

    public void setStreamWaiting(boolean iStreamWaiting) {  this.iStreamWaiting = iStreamWaiting; }
}
