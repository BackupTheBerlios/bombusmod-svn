/*
 * Message.java
 *
 * Created on 20. prosinec 2003, 15:44
 */

import java.util.*;

/**
 *
 * @author  radek
 */
public class Message 
{
    private String jid;
    private String text;
    
    public Message(String jid)
    {
        this.jid = new String(jid);
        text = "";
    }

    public void setText(String text)
    {
        this.text = new String(text);
    }
    
    public String getJID()
    {
        return jid;
    }
    
    public String getText()
    {
        return text;
    }
    
}
