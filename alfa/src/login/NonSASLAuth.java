/*
 * NonSASLAuth.java
 *
 * Created on 8 Июль 2006 г., 22:16
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package login;

import Client.Account;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.Stream;
import com.alsutton.jabber.datablocks.Iq;
import locale.SR;
import util.strconv;

/**
 *
 * @author evgs
 */
public class NonSASLAuth implements JabberBlockListener{
    
    private LoginListener listener;
    
    /** Creates a new instance of NonSASLAuth */
    public NonSASLAuth(Account account, String sessionId, LoginListener listener, Stream stream) {
        this.listener=listener;
        stream.addBlockListener(this);
        
        System.out.println("NonSASLAuth");
        
        listener.loginMessage(SR.MS_AUTH);
    }

    public int blockArrived(JabberDataBlock data) {
        try {
            if( data instanceof Iq ) {
                String type = (String) data.getTypeAttribute();
                String id=(String) data.getAttribute("id");
                if ( id.equals("auth-s") ) {
                    if (type.equals( "error" )) {
                        // Authorization error
                        listener.loginFailed( data.getChildBlock("error").toString() );
                        return JabberBlockListener.NO_MORE_BLOCKS;
                    } else if (type.equals( "result")) {
                        listener.loginSuccess();
                        return JabberBlockListener.NO_MORE_BLOCKS;
                    }
                }
            }
            
        } catch (Exception e) { }
        return JabberBlockListener.BLOCK_REJECTED;        
    }
    
}
