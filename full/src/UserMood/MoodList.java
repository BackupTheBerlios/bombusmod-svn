/*
 * MoodList.java
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

import java.util.Enumeration;
import java.util.Vector;

public class MoodList {

    private static MoodList instance;
    
    public static MoodList getInstance() {
	if (instance==null) 
            instance=new MoodList();
	return instance;
    }

    public Vector moodList;

    private MoodList() {
        moodList=new Vector();
        
        try {
            for (int i=0; i<Mood.MOODS.length;i++) {
                moodList.addElement(new Mood(i,Mood.MOODS[i], null));
                //System.out.println("private String MOOD_"+Mood.MOODS[i].toUpperCase()+"=\""+Mood.MOODS[i]+"\";");
            }
        } catch (Exception ex) { }
    }
    
    public Mood getStatus(final int mood) {
	Mood es=null;
	for (Enumeration e=moodList.elements(); e.hasMoreElements(); ){
	    es=(Mood)e.nextElement();
	    if (mood==es.getImageIndex()) break;
	}
	return es;
    }
}