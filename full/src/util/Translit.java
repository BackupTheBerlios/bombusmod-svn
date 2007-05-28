/*
 * Translit.java
 *
 * Created on 25.04.2007, 10:30
 *
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
 */

package util;

public class Translit {

    static char Translit_chars[][] = new char[106][6];
    static int Translit_count[] = new int[106];
    static boolean full=false;
    
    static void fillarrays()
    {
        int i = 40;
        int j = 0;
        Translit_chars[i] = "A".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "B".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "V".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "G".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "D".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "E".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[25] = "Yo".toCharArray();
        Translit_count[25] = 2;
        j++;
        Translit_chars[i] = "Zh".toCharArray();
        Translit_count[i] = 2;
        j++;
        i++;
        Translit_chars[i] = "Z".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "I".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "J".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "K".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "L".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "M".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "N".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "O".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "P".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "R".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "S".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "T".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "U".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "F".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "H".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "C".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "Ch".toCharArray();
        Translit_count[i] = 2;
        j++;
        i++;
        Translit_chars[i] = "Sh".toCharArray();
        Translit_count[i] = 2;
        j++;
        i++;
        Translit_chars[i] = "Sch".toCharArray();
        Translit_count[i] = 3;
        j++;
        i++;
        Translit_chars[i] = "'".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "Y".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "'".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "E".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "Yu".toCharArray();
        Translit_count[i] = 2;
        j++;
        i++;
        Translit_chars[i] = "Ya".toCharArray();
        Translit_count[i] = 2;
        j++;
        i++;
        Translit_chars[i] = "a".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "b".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "v".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "g".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "d".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "e".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[105] = "yo".toCharArray();
        Translit_count[105] = 2;
        j++;
        Translit_chars[i] = "zh".toCharArray();
        Translit_count[i] = 2;
        j++;
        i++;
        Translit_chars[i] = "z".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "i".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "j".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "k".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "l".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "m".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "n".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "o".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "p".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "r".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "s".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "t".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "u".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "f".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "h".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "c".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "ch".toCharArray();
        Translit_count[i] = 2;
        j++;
        i++;
        Translit_chars[i] = "sh".toCharArray();
        Translit_count[i] = 2;
        j++;
        i++;
        Translit_chars[i] = "sch".toCharArray();
        Translit_count[i] = 3;
        j++;
        i++;
        Translit_chars[i] = "'".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "y".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "'".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "e".toCharArray();
        Translit_count[i] = 1;
        j++;
        i++;
        Translit_chars[i] = "yu".toCharArray();
        Translit_count[i] = 2;
        j++;
        i++;
        Translit_chars[i] = "ya".toCharArray();
        Translit_count[i] = 2;
        j++;
        i++;
    }
    
    public static String translit(String s)
    {
        if (!full) fillarrays();
        char ac[] = new char[s.length() * 3];
        char ac1[] = s.toCharArray();
        int l = -1;
        for(int i = 0; i <= s.length() - 1; i++)
        {
            char c = ac1[i];
            if((c >= '\u0410') & (c <= '\u044F') || (c == '\u0401' || c == '\u0451'))
            {
                int k = c - 1000;
                for(int j = 0; j < Translit_count[k]; j++)
                {
                    l++;
                    ac[l] = Translit_chars[c - 1000][j];
                }

            } else
            {
                l++;
                ac[l] = ac1[i];
            }
        }

        return new String(ac, 0, l + 1);
    }
    
}
