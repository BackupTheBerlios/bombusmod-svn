/*
 * ClipBoard.java
 */

package util;

public class ClipBoard
{
      private static String _clipBoard="";
      
      public String getClipBoard() {
          return _clipBoard;
      }
      
      public void  setClipBoard(String str) {
          _clipBoard=str;
      }
      
      public boolean isEmpty() {
          boolean empty=(_clipBoard.length()>0)?false:true;
          return empty;
      }
}
