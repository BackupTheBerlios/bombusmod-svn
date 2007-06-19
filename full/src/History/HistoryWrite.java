/*
 * HistoryWrite.java
 *
 * Created on 19 Èþíü 2007 ã., 9:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package History;

import Client.Config;
import io.file.FileIO;
import java.io.IOException;
import java.io.OutputStream;
import util.Translit;
import util.strconv;

public class HistoryWrite {
    
    private Config cf=Config.getInstance();
    
    
//#if FILE_IO    
    private int filePos;
    private FileIO file;
    private OutputStream os;
//#endif
    
    public HistoryWrite(StringBuffer body, String histRecord) {
                
       byte[] bodyMessage;

       if (cf.cp1251) {
            bodyMessage=strconv.convUnicodeToCp1251(body.toString()).getBytes();
       } else {
            bodyMessage=body.toString().getBytes();
       }
       String filename=cf.msgPath+((cf.transliterateFilenames)?Translit.translit(histRecord):histRecord)+".txt";
       file=FileIO.createConnection(filename);
        try {
            os = file.openOutputStream(0);
            writeFile(bodyMessage);
            os.close();
            os.flush();
            file.close();
        } catch (IOException ex) {
            try {
                file.close();
            } catch (IOException ex2) { }
        }
        filename=null;
        body=null;
        bodyMessage=null;
    }

    private void writeFile(byte b[]){
        try {
            os.write(b);
            filePos+=b.length;
        } catch (IOException ex) { }
    }
}
