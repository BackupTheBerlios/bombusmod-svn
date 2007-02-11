/*
 * ClipBoard.java
 */

package util;

public class ClipBoard
{
      private static StringBuffer _clipBoard=new StringBuffer();
      
      public String getClipBoard() {
          return _clipBoard.toString();
      }
      
      public void  setClipBoard(String str) {
          _clipBoard.append(str);
      }
      
      public boolean isEmpty() {
          boolean empty=(_clipBoard.toString().length()>0)?false:true;
          return empty;
      }
}
