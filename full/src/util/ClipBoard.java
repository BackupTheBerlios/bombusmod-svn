/*
 * ClipBoard.java
 */

package util;

public class ClipBoard
{
      private static char[] _clipBoard = new char[4096];
      private static String s="";
      
      public String getClipBoard() {
          return s;
      }
      
      public void  setClipBoard(String str) {
          s=str;
      }
      
      public boolean isEmpty() {
          boolean empty=(s.length()>0)?false:true;
          return empty;
      }
}
