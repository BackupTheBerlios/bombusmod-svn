/*
 * AlertCustomize.java
 */

package Client;

import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import util.StringLoader;

public class AlertCustomize {
    
    public int soundsMsgIndex=0;
    public String messagesnd="";
    public String messageSndType="tone sequence";
    
    public int soundOnlineIndex=0;
    public String soundOnline="";
    public String soundOnlineType="tone sequence";    

    public int soundOfflineIndex=0;
    public String soundOffline="";
    public String soundOfflineType="tone sequence";
    
    public int soundForYouIndex=0;
    public String soundForYou="";
    public String soundForYouType="tone sequence";
    
    public int soundComposingIndex=0;
    public String soundComposing="";
    public String soundComposingType="tone sequence";
    
    public int soundConferenceIndex=0;
    public String soundConference="";
    public String soundConferenceType="tone sequence";

    public int soundVol=100;
    
    // Singleton
    private static AlertCustomize instance;

    private Vector files[]=new StringLoader().stringLoader("/sounds/res.txt", 3);
    
    public static AlertCustomize getInstance(){
	if (instance==null) {
	    instance=new AlertCustomize();
	    instance.loadFromStorage();
	    instance.loadSoundName();
            instance.loadOnlineSoundName();            
	    instance.loadOfflineSoundName();
	    instance.loadForYouSoundName();
	    instance.loadComposingSoundName();
	    instance.loadConferenceSoundName();
	}
	return instance;
    }
    
    protected void loadFromStorage(){
        try {
            DataInputStream inputStream=NvStorage.ReadFileRecord("AlertCustomize", 0);
            
            soundVol=inputStream.readInt();
	    soundsMsgIndex=inputStream.readInt();            
            soundOnlineIndex=inputStream.readInt();
            soundOfflineIndex=inputStream.readInt();
            soundForYouIndex=inputStream.readInt();
            soundComposingIndex=inputStream.readInt();
            soundConferenceIndex=inputStream.readInt();
                        
            inputStream.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    public void saveToStorage(){
	try {
            DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
            
            outputStream.writeInt(soundVol);
	    outputStream.writeInt(soundsMsgIndex);            
	    outputStream.writeInt(soundOnlineIndex);
	    outputStream.writeInt(soundOfflineIndex);
	    outputStream.writeInt(soundForYouIndex);
            outputStream.writeInt(soundComposingIndex);
            outputStream.writeInt(soundConferenceIndex);

            NvStorage.writeFileRecord(outputStream, "AlertCustomize", 0, true);
	} catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void loadSoundName(){
        if (soundsMsgIndex>=files[0].size())
            soundsMsgIndex=0;
	messageSndType=(String) files[0].elementAt(soundsMsgIndex);
	messagesnd=(String) files[1].elementAt(soundsMsgIndex);
    }

    public void loadOnlineSoundName(){
        if (soundOnlineIndex>=files[0].size())
            soundOnlineIndex=0;
	soundOnlineType=(String) files[0].elementAt(soundOnlineIndex);
	soundOnline=(String) files[1].elementAt(soundOnlineIndex);
    }
 
    public void loadOfflineSoundName(){
        if (soundOfflineIndex>=files[0].size()) 
            soundOfflineIndex=0;
	soundOfflineType=(String) files[0].elementAt(soundOfflineIndex);
	soundOffline=(String) files[1].elementAt(soundOfflineIndex);
    }
    
    public void loadForYouSoundName(){
        if (soundForYouIndex>=files[0].size()) 
            soundForYouIndex=0;
	soundForYouType=(String) files[0].elementAt(soundForYouIndex);
	soundForYou=(String) files[1].elementAt(soundForYouIndex);
    }
    
    public void loadComposingSoundName(){
        if (soundComposingIndex>=files[0].size()) 
            soundComposingIndex=0;
	soundComposingType=(String) files[0].elementAt(soundComposingIndex);
	soundComposing=(String) files[1].elementAt(soundComposingIndex);
    }
    
    public void loadConferenceSoundName(){
        if (soundConferenceIndex>=files[0].size()) 
            soundOnlineIndex=0;
	soundConferenceType=(String) files[0].elementAt(soundConferenceIndex);
	soundConference=(String) files[1].elementAt(soundConferenceIndex);
    }
}
