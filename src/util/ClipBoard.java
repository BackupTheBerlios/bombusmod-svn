/*
 * ClipBoard.java
 *
 * Created on 23 ќкт€брь 2006 г., 14:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

public class ClipBoard
{
      private static char[] _clipBoard = new char[4096];
      private static int _clipBoardCount = 0;
      private int beginOffset = 0;
      private boolean clipBoarded = false;
      public static String s;

      public ClipBoard(String string)
      {
            s = string;
      }
}
