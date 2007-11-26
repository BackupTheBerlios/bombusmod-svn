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

import java.util.Hashtable;
import java.util.Vector;
import util.StringLoader;

public class MoodList {

    private static MoodList instance;
    
    public static MoodList getInstance() {
	if (instance==null) 
            instance=new MoodList();
	return instance;
    }

    public Vector moodList=new Vector();
    
    public static final String initMoods = "afraid.amazed.angry.annoyed.anxious.aroused.ashamed.bored.brave.calm.cold.confused.contented.cranky.curious.depressed.disappointed.disgusted.distracted.embarrassed.excited.flirtatious.frustrated.grumpy.guilty.happy.hot.humbled.humiliated.hungry.hurt.impressed.in_awe.in_love.indignant.interested.intoxicated.invincible.jealous.lonely.mean.moody.nervous.neutral.offended.playful.proud.relieved.remorseful.restless.sad.sarcastic.serious.shocked.shy.sick.sleepy.stressed.surprised.thirsty.worried.";
   
    private MoodList() {
        try {
            int p=0; int pos=0;
            while (pos<initMoods.length()) {
               p=initMoods.indexOf('.', pos);
               String mood=initMoods.substring(pos, p);
               moodList.addElement(new Mood(mood, loadString(mood)));
               pos=p+1;
            }
        } catch (Exception ex) { }
        localeMood=null;
    }
/*
    public Mood getStatus(final int mood) {
	Mood es=null;
	for (Enumeration e=moodList.elements(); e.hasMoreElements(); ){
	    es=(Mood)e.nextElement();
	    if (mood==es.getImageIndex()) 
                break;
	}
	return es;
    }
*/
    private static Hashtable localeMood;
    
    private static String loadString(String key) {
        if (localeMood==null) {
            localeMood=new StringLoader().hashtableLoader("/moods/moods.txt");
        }
        String value=(String)localeMood.get(key);
        return (value==null)?key:value;
    }
}
