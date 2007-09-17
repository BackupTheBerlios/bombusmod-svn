/*
 * userKey.java
 *
 * Created on 14 �������� 2007 �., 10:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.keys;

import Client.StaticData;
import images.RosterIcons;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ui.ColorScheme;
import ui.IconTextElement;

/**
 *
 * @author User
 */
public class userKey extends IconTextElement {
    public final static String storage="keys_db";
            
    private int    commandId = 0;
    private int    key       = -1;
    public boolean active    = false;

    public userKey() {
        super(RosterIcons.getInstance());
    }
    
    public static userKey loadUserKey(int index){
	StaticData sd=StaticData.getInstance();
	userKey u=userKey.createFromStorage(index);
        return u;
    }
    
    public String toString(){
        StringBuffer s=new StringBuffer("");
        s.append("(* + ");
        s.append(userKeyExec.getInstance().getKeyDesc(key));
        s.append(") ");
        s.append(getDesc());
        
        return s.toString();
    }
    
    public String getDesc(){
        return userKeyExec.getInstance().getDesc(commandId);
    }
    
    public static userKey createFromStorage(int index) {
        userKey u=null;
        DataInputStream is=NvStorage.ReadFileRecord(storage, 0);
        if (is==null) return null;
        try {
            do {
                if (is.available()==0) {u=null; break;}
                u=createFromDataInputStream(is);
                index--;
            } while (index>-1);
            is.close();
        } catch (Exception e) { /*e.printStackTrace();*/ }
        return u;
    }    
    
    public static userKey createFromDataInputStream(DataInputStream inputStream){
        userKey u=new userKey();
        try {
            u.commandId  = inputStream.readInt();
            u.key        = inputStream.readInt();
            u.active     = inputStream.readBoolean();
        } catch (IOException e) { /*e.printStackTrace();*/ }
            
        return (u.key==-1)?null:u;
    }
    
    public void saveToDataOutputStream(DataOutputStream outputStream){
        try {
            outputStream.writeInt(commandId);
            outputStream.writeInt(key);	    
	    outputStream.writeBoolean(active);	    
        } catch (IOException e) { }
        
    }
    
    
    public int getCommandId() { return commandId; }
    
    public int getKey(){ return (key<0)?0:key; }
    
    public boolean getActive () { return active; }
    
    
    
    public void setCommand(int descId) { this.commandId = descId; }

    public void setKey(int key) { this.key = key; }

    public void setActive(boolean active) { this.active = active; }
    
    
    
    public int getColor(){ return ColorScheme.LIST_INK; }
    
    protected int getImageIndex() {return active?0:5;}
    public void onSelect(){};
    
    public String getTipString() { return null; }

    public String getSecondString() { return null; }
}

