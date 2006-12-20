/*
 * XMLEventListener.java
 *
 * Created on 30. listopad 2003, 12:45
 */

package mojab.xml;

/**
 *
 * @author  radek
 */
public interface XMLEventListener 
{
    
    public void TagStart(java.lang.String name, java.util.Vector attrs);
    
    public void TagEnd(java.lang.String name);
    
    public void Text(String text);
    
    public void XMLError(String s);
    
}
