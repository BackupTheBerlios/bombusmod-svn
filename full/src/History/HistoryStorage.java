/*
 * HistoryStorage.java
 *
 * Created on 13.11.2006, 14:49
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

package History;

import Client.Config;
import Client.Contact;
import Client.Msg;
import io.file.FileIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import util.Translit;
import util.strconv;

public class HistoryStorage {
    final static int SEARCH_MARKER= 0;
    final static int SEARCH_DATE  = 1;
    final static int SEARCH_FROM  = 2;
    final static int SEARCH_SUBJ  = 3;
    final static int SEARCH_BODY  = 4;
    final static int SEARCH_BREAK = 5;   
    
    public Vector recentList;
    
    private int count=4;

    private int afterEol;
    
    private String history;
    
    private Config cf=Config.getInstance();

    private int pos=0;
    
    public HistoryStorage(String bareJid) {
        String filename=bareJid;
//#ifdef TRANSLIT
//#        filename=cf.msgPath+((cf.transliterateFilenames)?Translit.translit(filename):filename)+".txt";
//#else
       filename=cf.msgPath+filename+".txt";
//#endif
       
       this.history = loadHistory(filename);
   }
    
   private String loadHistory(String fileName) {
        byte[] bodyMessage;
        String archive="";
        bodyMessage=readFile(fileName);

        if (bodyMessage!=null) {
            if (cf.cp1251) {
                archive=strconv.convCp1251ToUnicode(new String(bodyMessage, 0, bodyMessage.length));
            } else {
                archive=new String(bodyMessage, 0, bodyMessage.length);
            }
        }

        return archive;
   }

    public Vector importData() {
        Vector vector=new Vector();
        
        if (history!=null) {
            int count = 0;
            int state = SEARCH_MARKER;
            String date = null; String from = null; String subj = null; String body = null;  String marker = "";

            pos = history.length();

            try {
                while (true) {
                    switch (state) {
                        case SEARCH_MARKER:
                            marker = findBlock('\05','\05');
                            if (marker!="") state = SEARCH_BODY; else state = SEARCH_BREAK;
                            break; 
                        case SEARCH_BODY:
                            body = findBlock('\04','\04');
                            if (body!="") {
                                state = SEARCH_SUBJ;
                            } else state = SEARCH_BREAK;
                            break; 
                        case SEARCH_SUBJ:
                            subj = findBlock('\03','\03');
                            state = SEARCH_FROM;
                            break;
                        case SEARCH_FROM:
                            from = findBlock('\02','\02');
                            if (from!="") state = SEARCH_DATE; else state = SEARCH_BREAK;
                            break;
                        case SEARCH_DATE:
                            date = findBlock('\01','\01');
                            if (date!="") {
                                state = SEARCH_MARKER;
                                if (Integer.parseInt(marker)!=Msg.MESSAGE_MARKER_PRESENCE) {
                                    //System.out.println(marker+" "+date+" "+from+" "+subj+" "+body);
                                    vector.insertElementAt(processMessage (marker, date, from, subj, body), 0);
                                    count++;
                                }
                            } else state = SEARCH_BREAK;
                            break;
                    }

                    if (state == SEARCH_BREAK || count>4) {
                        //System.out.println("end search at "+state+" with count: "+count);
                        break;
                    }
                }
            } catch (Exception e)	{ /*System.out.println(e.toString()); */}
        }
        history = null;
        return vector;
    }
    
    private Msg processMessage (String marker, String date, String from, String subj, String body) {
        int msgType=Msg.MESSAGE_TYPE_HISTORY;
        
        int mrk = Integer.parseInt(marker);
        
        switch (mrk) {
            case Msg.MESSAGE_MARKER_IN:
                msgType=Msg.MESSAGE_TYPE_IN;
                break;
            case Msg.MESSAGE_MARKER_OUT:
                msgType=Msg.MESSAGE_TYPE_OUT;
                break;
            case Msg.MESSAGE_MARKER_PRESENCE:
                msgType=Msg.MESSAGE_TYPE_PRESENCE;
                break;
        }
        
        Msg msg=new Msg(msgType,from,subj,body);
        msg.setDayTime(date);
        
        return msg;
    }
    
    private String findBlock (char start, char end){
        String block = "";
        int end_pos=history.lastIndexOf(end, pos);
        if (end_pos>-1) {
            pos=end_pos-1;
            int start_pos=history.lastIndexOf(start, pos);
            if (start_pos>-1) {
                pos=start_pos-1;
                block=history.substring(start_pos+1, end_pos);
            }
        }
        return block;
    }
    
    private byte[] readFile(String arhPath){
        byte[] b = null; int maxSize=2048;
        FileIO f=FileIO.createConnection(arhPath);
        try {
            InputStream is=f.openInputStream(); 
            int fileSize = (int)f.fileSize();
            if (fileSize>maxSize){
                b=new byte[maxSize];
                is.skip(fileSize-maxSize);
                is.read(b);
            } else {
                b=new byte[fileSize];
                is.read(b);
            }
            is.close(); f.close();
        } catch (Exception e) { try { f.close(); } catch (IOException ex2) { } }
        
        if (b!=null) { return b; }
        return null;
    }    
}
