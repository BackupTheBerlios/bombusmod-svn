/*
 * Jid.java
 *
 * Created on 4 Март 2005 г., 1:25
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import util.strconv;

/**
 *
 * @author Eugene Stahov
 */
public class Jid {
    
    private String id;
    private String sex;
    private String client;
    private String status;
    private String nickName;
    
    /** Creates a new instance of Jid */
    public Jid(String s) {
        setJid(s);
    }
    
    public void setId(String s){
        id=s;
    }
    /** Compares two Jids */
    public boolean equals(Jid j) {
        if (j==null) return false;
        
        if (!id.equals(j.id)) return false;
        
        return true;
    }
    
   
    /** выделение имени */
    public String getId(){ return id; }

    private void setJid(String s) {
        id=s;
    }
}
