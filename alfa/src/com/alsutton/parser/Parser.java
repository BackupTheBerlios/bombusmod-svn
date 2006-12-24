package com.alsutton.parser;

/**
 * The main Parser class.
 */

import io.Utf8IOStream;
import java.io.*;

import java.util.*;

public class Parser
{
  Utf8IOStream iostream;

  private EventListener eventHandler;


  public Parser( EventListener _eventHandler )
  {
    eventHandler = _eventHandler;
  }

  private StringBuffer streamData = new StringBuffer(16);

  
  private String readUntilEnd()
    throws IOException
  {
    //StringBuffer streamData = new StringBuffer(16);
    streamData.setLength(0);
    streamData.append(iostream.readLine());

    String returnData = streamData.toString();
    return returnData;
  }


  public void  parse ( Utf8IOStream iostream )
    throws IOException
  {
    this.iostream=iostream;

    System.out.println(this.streamData.toString());
 }
}
