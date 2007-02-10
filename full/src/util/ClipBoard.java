/*
 * ClipBoard.java
 */

package util;

public class ClipBoard
{
      private static char[] _clipBoard = new char[4096];
      private static int _clipBoardCount = 0;
      private int beginOffset = 0;
      private boolean clipBoarded = false;
      private static String s;

      public ClipBoard() { }
      
      public String getClipBoard() {
          return s;
      }
      
      public void  setClipBoard(String str) {
          s=str;
      }
}
