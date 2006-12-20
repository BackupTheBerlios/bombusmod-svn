/*
 * Attribute.java
 *
 * Created on 30. listopad 2003, 12:47
 */

package mojab.xml;

/**
 *
 * @author  radek
 */
public class Attribute 
{
    
    /** Holds value of property name. */
    private String name;
    
    /** Holds value of property value. */
    private String value;
    
    /** Creates a new instance of Attribute */
    public Attribute(String name, String value) 
    {
        this.name = new String(name);
        this.value = new String(value);
    }
    
    /** Getter for property name.
     * @return Value of property name.
     *
     */
    public String getName() {
        return this.name;
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     *
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /** Getter for property value.
     * @return Value of property value.
     *
     */
    public String getValue() {
        return this.value;
    }
    
    /** Setter for property value.
     * @param value New value of property value.
     *
     */
    public void setValue(String value) {
        this.value = value;
    }
    
}
