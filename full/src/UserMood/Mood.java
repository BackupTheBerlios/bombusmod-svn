/*
 * Mood.java
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package UserMood;

import images.SmilesIcons;
import ui.ColorScheme;
import ui.IconTextElement;

public class Mood extends IconTextElement {
    
    private String name;
    private String text;
    //private String locale;
    int index;

    public Mood(int index, String name, String text) {
        super(SmilesIcons.getInstance());
        this.text=MoodLocale.loadString(text);
        this.name=name;
        this.index=index;
        //locale=MoodLocale.loadString(name);
    }
    
    public String toString(){ 
        StringBuffer s=new StringBuffer(text);
        if (text!=null && text.length()>0) {
            s.append(" (");
            s.append(text);
            s.append(")");
        }
        return s.toString();
    }
    
    public int getColor(){ return ColorScheme.LIST_INK;}

    public String getName() { return name; }
    public void setName(String s) { name=s; }
    
    public String getMessage() { return text; }
    public void setMessage(String s) { text=s; }

    public String getSecondString() {
        return null;
    }
    
    //public String getLocale() { return locale; }

    public int getImageIndex(){ return -1;}
    
    public static final String[] MOODS = {
        "afraid", "amazed", "angry", "annoyed", "anxious", "aroused",
        "ashamed", "bored", "brave", "calm", "cold", "confused",
        "contented", "cranky", "curious", "depressed", "disappointed", "disgusted", 
        "distracted", "embarrassed", "excited", "flirtatious", "frustrated", "grumpy",
        "guilty", "happy", "hot", "humbled", "humiliated", "hungry",
        "hurt", "impressed", "in_awe", "in_love", "indignant", "interested",
        "intoxicated", "invincible", "jealous", "lonely", "mean", "moody",
        "nervous", "neutral", "offended", "playful", "proud", "relieved",
        "remorseful", "restless", "sad", "sarcastic", "serious", "shocked",
        "shy", "sick", "sleepy", "stressed", "surprised", "thirsty",
        "worried"
    };
}