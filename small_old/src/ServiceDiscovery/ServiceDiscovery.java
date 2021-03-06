/*
 * ServiceDiscovery.java
 *
 * Created on 4 �?юнь 2005 г., 21:12
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ServiceDiscovery;
import Conference.ConferenceForm;
import images.RosterIcons;
import java.util.*;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.*;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import Client.*;

/**
 *
 * @author Evg_S
 */
public class ServiceDiscovery 
        extends VirtualList
        implements CommandListener,
        JabberBlockListener
{
    private final static String NS_ITEMS="http://jabber.org/protocol/disco#items";
    private final static String NS_INFO="http://jabber.org/protocol/disco#info";
    private final static String NS_REGS="jabber:iq:register";
    private final static String NS_SRCH="jabber:iq:search";
    private final static String NS_MUC="http://jabber.org/protocol/muc";
    private final static String NODE_CMDS="http://jabber.org/protocol/commands";
    
    
    private final static String strJoin="Join Conference";
    private final static String strReg="Register";
    private final static String strSrch="Search";
    private final static String strCmds="Execute";
    private final int AD_HOC_INDEX=17;
    
    private Command cmdOk=new Command("Browse", Command.SCREEN, 1); //locale
    private Command cmdRfsh=new Command("Refresh", Command.SCREEN, 2); //locale
    private Command cmdFeatures=new Command(SR.MS_FEATURES, Command.SCREEN, 3); //locale
    private Command cmdSrv=new Command(SR.MS_SERVER, Command.SCREEN, 10); //locale
    //private Command cmdAdd=new Command(SR.MS_ADD_TO_ROSTER, Command.SCREEN, 11);
    private Command cmdBack=new Command(SR.MS_BACK, Command.BACK, 98); //locale
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.EXIT, 99); //locale

    private StaticData sd=StaticData.getInstance();
    
    private Vector items;
    private Vector stackItems=new Vector();
    
    private Vector features;
    
    private Vector cmds;
    
    private String service;
    private String node;

    private int discoIcon;

    private JabberStream stream;

    
    /** Creates a new instance of ServiceDiscovery */
    public ServiceDiscovery(Display display, String service, String node) {
        super(display);

        setTitleItem(new Title(3, null, null));
        getTitleItem().addRAlign();
        getTitleItem().addElement(null);
        
        stream=sd.roster.theStream;
        stream.cancelBlockListenerByClass(this.getClass());
        stream.addBlockListener(this);
        //sd.roster.discoveryListener=this;
        
        setCommandListener(this);
        addCommand(cmdRfsh);
        addCommand(cmdSrv);
        addCommand(cmdFeatures);
        //addCommand(cmdAdd);
        addCommand(cmdCancel);
        

        addCommand(cmdBack);

        this.node=node;
        this.service=(service!=null)?service:sd.account.getServer();
        
        items=new Vector();
        features=new Vector();
        
        requestQuery(NS_INFO, "disco");
    }
    
    private String discoId(String id) {
        return id+service.hashCode();
    }
    
    public int getItemCount(){ return items.size();}
    public VirtualElement getItemRef(int index) { return (VirtualElement) items.elementAt(index);}
    
    protected void beginPaint(){ getTitleItem().setElementAt(sd.roster.getEventIcon(), 4); }
    
    
    private void titleUpdate(){
        
        getTitleItem().setElementAt(new Integer(discoIcon), 0);
        getTitleItem().setElementAt(service, 2);
        getTitleItem().setElementAt(sd.roster.getEventIcon(), 4);
	
	int size=0;
	try { size=items.size(); } catch (Exception e) {}
	String count=null;
	if (size>0) {
	    addCommand(cmdOk); 
	    count=" ("+size+") ";
	} else {
	    removeCommand(cmdOk);
	}
        getTitleItem().setElementAt(count,1);	    
    }
    
    private void requestQuery(String namespace, String id){
        discoIcon=RosterIcons.ICON_PROGRESS_INDEX; titleUpdate(); redraw();
        JabberDataBlock req=new Iq(service, Iq.TYPE_GET, discoId(id));
        JabberDataBlock qry=req.addChild("query",null);
        qry.setNameSpace(namespace);
        qry.setAttribute("node", node);

        //stream.addBlockListener(this);
        //System.out.println(">> "+req.toString());
        stream.send(req);
    }
    
    private void requestCommand(String namespace, String id){
        discoIcon=RosterIcons.ICON_PROGRESS_INDEX; titleUpdate(); redraw();
        JabberDataBlock req=new Iq(service, Iq.TYPE_SET, id);
        JabberDataBlock qry=req.addChild("command",null);
        qry.setNameSpace(namespace);
        qry.setAttribute("node", node);
        qry.setAttribute("action", "execute");

        //stream.addBlockListener(this);
        //System.out.println(req.toString());
        stream.send(req);
    }
    
    public int blockArrived(JabberDataBlock data) {
        //System.out.println("<< "+data.toString());
        
        if (!(data instanceof Iq)) return JabberBlockListener.BLOCK_REJECTED;
        String id=data.getAttribute("id");
        if (!id.startsWith("disco")) return JabberBlockListener.BLOCK_REJECTED;
        
        if (data.getTypeAttribute().equals("error")) {
            System.out.println(data.toString());
            discoIcon=RosterIcons.ICON_ERROR_INDEX;
            titleUpdate();
            //redraw();
            
            String err=((JabberDataBlock)(data.getChildBlock("error").getChildBlocks().firstElement())).getTagName();
            Alert alert=new Alert("Error: ", err, null, null /*AlertType.ALARM*/); //locale
            alert.addCommand(new Command("OK", Command.BACK, 1));
            display.setCurrent(alert, this);

            return JabberBlockListener.BLOCK_PROCESSED;
        }

        JabberDataBlock query=data.getChildBlock((id.equals("discocmd"))?"command":"query");
        Vector childs=query.getChildBlocks();
        //System.out.println(id);
        if (id.equals(discoId("disco2"))) {
            Vector items=new Vector();
            if (childs!=null)
            for (Enumeration e=childs.elements(); e.hasMoreElements(); ){
                JabberDataBlock i=(JabberDataBlock)e.nextElement();
                if (i.getTagName().equals("item")){
                    String name=i.getAttribute("name");
                    String jid=i.getAttribute("jid");
                    String node=i.getAttribute("node");
                    Object serv=null;
                    if (node==null) { 
                        serv=new DiscoContact(name,jid,0);
                    } else {
                        serv=new Node(name, node);
                    }
                    items.addElement(serv);
                }
                
                
            }
            
            try { 
                sort(items);
            } catch (Exception e) { e.printStackTrace(); };
            
            /*if (data.getAttribute("from").equals(service)) - jid hashed in id attribute*/ {
                for (Enumeration e=cmds.elements(); e.hasMoreElements();) 
                    items.insertElementAt(e.nextElement(),0);
                this.items=items;
                moveCursorHome();
                discoIcon=0; titleUpdate(); 
            }
        } else if (id.equals(discoId("disco"))) {
            Vector cmds=new Vector();
            if (childs!=null)
            for (Enumeration e=childs.elements(); e.hasMoreElements();) {
                JabberDataBlock i=(JabberDataBlock)e.nextElement();
                if (i.getTagName().equals("feature")) {
                    String var=i.getAttribute("var");
                    features.addElement(var);
                    if (var.equals(NS_MUC)) { cmds.addElement(new DiscoCommand(RosterIcons.ICON_GCJOIN_INDEX, strJoin)); }
                    if (var.equals(NS_SRCH)) { cmds.addElement(new DiscoCommand(RosterIcons.ICON_SEARCH_INDEX, strSrch)); }
                    if (var.equals(NS_REGS)) { cmds.addElement(new DiscoCommand(RosterIcons.ICON_REGISTER_INDEX, strReg)); }
                    //if (var.equals(NODE_CMDS)) { cmds.addElement(new DiscoCommand(AD_HOC_INDEX,strCmds)); } 
                }
		if (i.getTagName().equals("identity")) {
		    String category=i.getAttribute("category");
		    String type=i.getAttribute("type");
		    if (category.equals("automation") && type.equals("command-node"))  { 
			cmds.addElement(new DiscoCommand(RosterIcons.ICON_AD_HOC, strCmds)); 
		    } 
		}
            }
            /*if (data.getAttribute("from").equals(service)) */ { //FIXME!!!
                this.cmds=cmds;
                requestQuery(NS_ITEMS, "disco2");
            }
        } else if (id.startsWith ("discoreg")) {
            discoIcon=0;
            new DiscoForm(display, data, stream, "discoResult", "query");
        } else if (id.startsWith("discocmd")) {
            discoIcon=0;
            new DiscoForm(display, data, stream, "discocmd", "command");
        } else if (id.startsWith("discosrch")) {
            discoIcon=0;
            new DiscoForm(display, data, stream, "discoRSearch", "query");
        } else if (id.startsWith("discoR")) {
            String text="Successful";
            String title=data.getAttribute("type");
            if (title.equals("error")) {
                text=data.getChildBlockText("error");
            }
            Alert alert=new Alert(title, text, null, null /*AlertType.ALARM*/);
			alert.addCommand(new Command("OK", Command.BACK, 1));
            alert.setTimeout(15*1000);
            if (text=="Successful" && id.endsWith("Search") ) {
                //new SearchResult(display, data);
            } else display.setCurrent(alert, this);
        }
        redraw();
        return JabberBlockListener.BLOCK_PROCESSED;
    }
    
    public void eventOk(){
        super.eventOk();
        Object o= getFocusedObject();
        if (o!=null) 
        if (o instanceof Contact) {
            browse( ((Contact) o).jid.getJid(), null );
        }
        if (o instanceof Node) {
            browse( service, ((Node) o).getNode() );
        }
    }
    
    public void browse(String service, String node){
            State st=new State();
            st.cursor=cursor;
            st.items=items;
            st.service=this.service;
            st.node=this.node;
            st.features=features;
            stackItems.addElement(st);
            
            items=new Vector();
            features=new Vector();
            addCommand(cmdBack);
            this.service=service;
            this.node=node;
            requestQuery(NS_INFO,"disco");
    }
    
    public void commandAction(Command c, Displayable d){
	if (c==cmdOk) eventOk();
        if (c==cmdBack){ 
            if (stackItems.isEmpty()) { 
                exitDiscovery();
                return;
            }
            
            State st=(State)stackItems.lastElement();
            stackItems.removeElement(st);
            
            service=st.service;
            items=st.items;
            features=st.features;
            discoIcon=0;
            
            titleUpdate();
            moveCursorTo(st.cursor, true);
            redraw();
            
        }
        /*if (c==cmdAdd){
            exitDiscovery();
            Contact j=(Contact)getFocusedObject();
            new ContactEdit(display, j);
            return;
        }*/
        if (c==cmdRfsh) {requestQuery(NS_INFO, "disco"); }
        if (c==cmdSrv) { new ServerBox(display, service, this); }
        if (c==cmdFeatures) {new DiscoFeatures(display, service, features); }
        if (c==cmdCancel) exitDiscovery();
    }
    
    private class DiscoCommand extends IconTextElement {
        String name;
        int index;
        int icon;
        
        public DiscoCommand(int icon, String name) {
            super(RosterIcons.getInstance());
            this.icon=icon; this.name=name;
        }
        public int getColor(){ return Colors.DISCO_CMD; }
        public int getImageIndex() { return icon; }
        public String toString(){ return name; }
        public void onSelect(){
            switch (icon) {
                case RosterIcons.ICON_GCJOIN_INDEX: {
                    int rp=service.indexOf('@');
                    String room=null;
                    String server=service;
                    if (rp>0) {
                        room=service.substring(0,rp);
                        server=service.substring(rp+1);
                    }
                    new ConferenceForm(display, room, server, null, null);
                    break;
                }
                case RosterIcons.ICON_SEARCH_INDEX:
                    requestQuery(NS_SRCH, "discosrch");
                    break;
                case RosterIcons.ICON_REGISTER_INDEX:
                    requestQuery(NS_REGS, "discoreg");
                    break;
                    
                case RosterIcons.ICON_AD_HOC:
                    requestCommand(NODE_CMDS, "discocmd");
                default:
            }
        }
    }
    private void exitDiscovery(){
        stream.cancelBlockListener(this);
        destroyView();
    }
}
class State{
    public String service;
    public String node;
    public Vector items;
    public Vector features;
    public int cursor;
}
