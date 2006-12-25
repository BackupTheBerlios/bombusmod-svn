/*
 * Account.java
 *
 * Created on 19 Март 2005 г., 21:52
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import com.alsutton.jabber.datablocks.Presence;
import images.RosterIcons;
import io.NvStorage;
import java.util.*;
import java.io.*;
import javax.microedition.midlet.MIDlet;
import midlet.Damafon;
import ui.Colors;
import ui.IconTextElement;
import ui.ImageList;
import javax.microedition.rms.*;
import javax.microedition.lcdui.*;
import Client.Roster;
import com.alsutton.jabber.*;

/**
 *
 * @author Eugene Stahov
 */
public class Account extends IconTextElement{
    
    public final static String storage="accnt_db";
            
    private String userName="adeen";
    private String password="336699";
    public boolean active;

    private String server="damochka.ru";

    private int port=80;

    private String messageServer="message.damochka.ru";
        
    /** Creates a new instance of Account */
    public Account() {
        super(RosterIcons.getInstance());
    }
    
    public static Account loadAccount(boolean launch){
	StaticData sd=StaticData.getInstance();
        
	Account a=sd.account=Account.createFromStorage(Config.getInstance().accountIndex);
        
        System.out.println("loadAccount");
        
	if (a!=null) {
            sd.roster.logoff();
	    sd.roster.resetRoster();
	}
        if (a!=null && launch){
            sd.roster.logoff();
	    sd.roster.resetRoster();
            sd.roster.myStatus=Presence.PRESENCE_ONLINE;
            //sd.roster.querysign=true;
            
            new Thread(sd.roster).start();
        }
        return a;
    }
    
    public static Account createFromDataInputStream(DataInputStream inputStream){
        
        int version=0;
        Account a=new Account();
        try {
            a.userName = inputStream.readUTF();
            a.password = inputStream.readUTF();
        } catch (IOException e) { e.printStackTrace(); }
            
        return (a.userName==null)?null:a;
    }

    public String toString(){
        StringBuffer s=new StringBuffer();
        s.append(userName);
        return s.toString();
    }
    
    public String getJid(){
        return userName;
    }
    
    public static Account createFromStorage(int index) {
        Account a=null;
        DataInputStream is=NvStorage.ReadFileRecord(storage, 0);
        if (is==null) return null;
        try {
            do {
                if (is.available()==0) {a=null; break;}
                a=createFromDataInputStream(is);
                //a.updateJidCache();
                index--;
            } while (index>-1);
            is.close();
        } catch (Exception e) { e.printStackTrace(); }
        return a;
    }
    
    public void saveToDataOutputStream(DataOutputStream outputStream){
        
        try {
            outputStream.writeUTF(userName);
            outputStream.writeUTF(password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    public int getColor(){ return Colors.LIST_INK; }
    
    protected int getImageIndex() {return active?0:5;}
    public void onSelect(){};

    public String getUserName() { return userName;  }
    public void setUserName(String userName) { this.userName = userName;  }

    public String getPassword() {  return password;  }
    public void setPassword(String password) { this.password = password;  }

    public Stream openJabberStream() throws java.io.IOException{
        String proxy=null;
	StringBuffer url=new StringBuffer();
        url.append(server);
        url.append(':');
        url.append(port);
        
        url.insert(0, "socket://");
        
        //System.out.println(url);
        return new Stream(getServer(), url.toString());    
    }

    public String getServer() {
        return server;
    }
}