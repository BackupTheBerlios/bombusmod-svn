/*
 * Contact.java
 *
 * Created on 14. prosinec 2003, 21:04
 */

import java.lang.*;

/**
 *
 * @author  radek
 */
public class Contact 
{
    public static final int OFFLINE = 0;
    public static final int NOTAVAIL = 1;
    public static final int AWAY = 2;
    public static final int DND = 3;
    public static final int ONLINE = 4;
    
    public String name;
    public String jid;
    public int status;
    
    public Contact(String name, String jid)
    {
        this.name = name;
        this.jid = jid;
        this.status = OFFLINE;
    }
    
    public boolean greaterThan(Contact other)
    {
        if (status != other.status)
            return (status < other.status);
        else
            return (name.compareTo(other.name) > 0);
    }
    
    //======================================================================
    
    public static String stripResource(String jid)
    {
        int r = jid.indexOf('/');
        if (r == -1)
            return jid;
        else
            return jid.substring(0, r);
    }

}
