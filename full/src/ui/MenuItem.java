/*
 * MenuItem.java
 *
 * Created on 2 Апрель 2005 г., 13:22
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;

import images.RosterIcons;

/**
 *
 * @author Eugene Stahov
 */
public class MenuItem extends IconTextElement
{
    
    /** Creates a new instance of MenuItem */
    public int index;
    private String name;

    private int iconIndex;
    
    public MenuItem(String name, int index, int iconIndex) {
        super(RosterIcons.getInstance());
        this.index=index;
	this.name=name;
        this.iconIndex=iconIndex;
    }

    protected int getImageIndex() { return iconIndex;  }
    public int getColor() { return Colors.LIST_INK; }
    public String toString(){ return name; }
}
