/*
 * SR.java
 *
 * Created on 19.03.2006, 15:06
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
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
 */

package locale;

import Client.Config;
import java.util.Hashtable;
import util.StringLoader;

public class SR {
    
    public   static String MS_JID = loadString( "Jid" );
    public   static String MS_LOADING = loadString( "Loading" );
    public   static String MS_PRIVACY_LISTS = loadString( "Privacy Lists" );
    //public   static String MS_EXISTING_GROUPS = loadString( "Existing groups" );
    public   static String MS_MESSAGE_FONT = loadString( "Message font" );
    public   static String MS_ROSTER_FONT = loadString( "Roster font" );
    public   static String MS_PASTE_BODY = loadString( "Paste Body" );
    public   static String MS_CONFIG_ROOM = loadString( "Configure Room" );
    public   static String MS_PASTE_SUBJECT = loadString( "Paste Subject" );
    public   static String MS_DISCO = loadString( "Service Discovery" );
    public   static String MS_USER_JID = loadString( "User JID" );
    public   static String MS_NEW_LIST = loadString( "New list" );
    public   static String MS_NOLOGIN = loadString( "Select (no login)" );
    public   static String MS_PRIVACY_RULE = loadString( "Privacy rule" );
    public   static String MS_SSL = loadString( "use SSL" );
    public   static String MS_MODIFY = loadString( "Modify" );
    public   static String MS_UPDATE = loadString( "Update" );
    public   static String MS_ACCOUNT_NAME = loadString( "Account name" );
    public   static String MS_GMT_OFFSET = loadString( "GMT offset" );
    public   static String MS_TIME_SETTINGS = loadString( "Time settings (hours)" );
    public   static String MS_CONNECTED = loadString( "Connected" );
    public   static String MS_CONNECT_TO = loadString( "Connect to " );
    public   static String MS_ALERT_PROFILE = loadString( "Alert Profile" );
    public   static String MS_MOVE_UP = loadString( "Move Up" );
    public   static String MS_OWNERS = loadString( "Owners" );
    public   static String MS_OK = loadString( "Ok" );
    public   static String MS_APP_MINIMIZE = loadString( "Minimize" );
    public   static String MS_ROOM = loadString( "Room" );
    public   static String MS_MESSAGES = loadString( "Messages" );
    public   static String MS_REFRESH = loadString( "Refresh" );
    public   static String MS_RESOLVE_NICKNAMES = loadString( "Resolve Nicknames" );
    public   static String MS_PRIVACY_ACTION = loadString( "Action" );
    public   static String MS_BAN = loadString( "Ban" );
    public   static String MS_LEAVE_ROOM = loadString( "Leave Room" );
    public   static String MS_PASSWORD = loadString( "Password" );
    public   static String MS_ITEM_ACTIONS = loadString( "Actions >" );
    public   static String MS_ACTIVATE = loadString( "Activate" );
    public   static String MS_AFFILIATION = loadString( "Affiliation" );
    public   static String MS_ACCOUNTS = loadString( "Accounts" );
    public   static String MS_DELETE_LIST = loadString( "Delete list" );
    public   static String MS_ACCOUNT_= loadString( "Account >" );
    public   static String MS_SHOWOFFLINES = loadString( "Show Offlines" );
    public   static String MS_SELECT = loadString( "Select" );
    public   static String MS_SUBJECT = loadString( "Subject" );
    public   static String MS_GROUP_MENU = loadString( "Group menu" );
    public   static String MS_APP_QUIT = loadString( "Quit" );
    public   static String MS_ROSTERADD = loadString( "Add to roster" );
    public   static String MS_EDIT_LIST = loadString( "Edit list" );
    public   static String MS_REGISTERING = loadString( "Registering" );
    public   static String MS_DONE = loadString( "Done" );
    public   static String MS_ERROR_ = loadString( "Error: " );
    public   static String MS_BROWSE = loadString( "Browse" );
    public   static String MS_SAVE_LIST = loadString( "Save list" );
    public   static String MS_KEEPALIVE_PERIOD = loadString( "Keep-Alive period" );
    public   static String MS_NEWGROUP = loadString( "<New Group>" );
    public   static String MS_SEND = loadString( "Send" );
    public   static String MS_PRIORITY = loadString( "Priority" );
    public   static String MS_FAILED = loadString( "Failed" );
    public   static String MS_SET_PRIORITY = loadString( "Set Priority" );
    public   static String MS_DELETE_RULE = loadString( "Delete rule" );
    public   static String MS_IGNORE_LIST = loadString( "Ignore-List" );
    public   static String MS_ROSTER_REQUEST = loadString( "Roster request" );
    public   static String MS_PRIVACY_TYPE = loadString( "Type" );
    public   static String MS_NAME = loadString( "Name" );
    public   static String MS_USERNAME = loadString( "Username" );
    public   static String MS_FULLSCREEN = loadString( "fullscreen" );
    public   static String MS_ALL_PRIORITIES = loadString( "All Priorities" );
    public   static String MS_ADD_BOOKMARK = loadString( "Add bookmark" );
    public   static String MS_CONFERENCES_ONLY = loadString( "conferences only" );
    public   static String MS_CLIENT_INFO = loadString( "Client Version" );
    public   static String MS_DISCARD = loadString( "Discard Search" );
    public   static String MS_SEARCH_RESULTS = loadString( "Search Results" );
    public   static String MS_GENERAL = loadString( "General" );
    public   static String MS_MEMBERS = loadString( "Members" );
    public   static String MS_ADD_CONTACT = loadString( "Add Contact" );
    public   static String MS_SUBSCRIPTION = loadString( "Subscription" );
    public   static String MS_STATUS_MENU = loadString( "Status >" );
    public   static String MS_JOIN = loadString( "Join" );
    public   static String MS_STARTUP_ACTIONS = loadString( "Startup actions" );
    public   static String MS_SERVER = loadString( "Server" );
    public   static String MS_ADMINS = loadString( "Admins" );
    public   static String MS_MK_ILIST = loadString( "Make Ignore-List" );
    public   static String MS_OPTIONS = loadString( "Options" );
    public   static String MS_DELETE = loadString( "Delete" );
    public   static String MS_DELETE_ASK = loadString( "Delete contact?" );
    public   static String MS_SUBSCRIBE = loadString( "Authorize" );
    public   static String MS_NICKNAMES = loadString( "Nicknames" );
    public   static String MS_ENT_SETUP = loadString( "Entering setup" );
    public   static String MS_ADD_ARCHIVE = loadString( "to Archive" );
    public   static String MS_BACK = loadString( "Back" );
    public   static String MS_HEAP_MONITOR = loadString( "heap monitor" );
    public   static String MS_HIDE_SPLASH = loadString( "Hide Splash" );
    public   static String MS_MESSAGE = loadString( "Message" );
    public   static String MS_OTHER = loadString( "<Other>" );
    public   static String MS_HISTORY = loadString( "history -" );
    public   static String MS_APPEND = loadString( "Append" );
    public   static String MS_ACTIVE_CONTACTS = loadString( "Active Contacts" );
    public   static String MS_SELECT_NICKNAME = loadString( "Select nickname" );
    public   static String MS_GROUP = loadString( "Group" );
    public   static String MS_JOIN_CONFERENCE = loadString( "Join conference" );
    public   static String MS_NO = loadString( "No" );
    public   static String MS_REENTER = loadString( "Re-Enter Room" );
    public   static String MS_NEW_MESSAGE = loadString( "New Message" );
    public   static String MS_ADD = loadString( "Add" );
    public   static String MS_LOGON = loadString( "Logon" );
    public   static String MS_LOGINPGS = loadString( "Login in progress" );
    public   static String MS_STANZAS = loadString( "Stanzas" );
    public   static String MS_AT_HOST = loadString( "at Host" );
    public   static String MS_AUTO_CONFERENCES = loadString( "join conferences" );
    public   static String MS_STATUS = loadString( "Status" );
    public   static String MS_SMILES_TOGGLE = loadString( "Smiles" );
    public   static String MS_CONTACT = loadString( "Contact >" );
    public final static String MS_SLASHME = "/me";
    public   static String MS_ORDER = loadString( "Order" );
    public   static String MS_OFFLINE_CONTACTS = loadString( "offline contacts" );
    public   static String MS_TRANSPORT = loadString( "Transport" );
    public   static String MS_COMPOSING_EVENTS = loadString( "composing events" );
    public   static String MS_ADD_SMILE = loadString( "Add Smile" );
    public   static String MS_NICKNAME = loadString( "Nickname" );
    public   static String MS_REVOKE_VOICE = loadString( "Revoke Voice" );
    public   static String MS_NOT_IN_LIST = loadString( "Not-in-list" );
    public   static String MS_COMMANDS = loadString( "Commands" );
    public   static String MS_CHSIGN = loadString( "- (Sign)" );
    public   static String MS_SETDEFAULT = loadString( "Set default" );
    public   static String MS_BANNED = loadString( "Outcasts (Ban)" );
    public   static String MS_SET_AFFILIATION = loadString( "Set affiliation to" );
    public   static String MS_HIDE_OFFLINES = loadString( "Hide Offlines" );
    public   static String MS_REGISTER_ACCOUNT = loadString( "Register Account" );
    public   static String MS_AUTOLOGIN = loadString( "autologin" );
    public   static String MS_LOGOFF = loadString( "Logoff" );
    public   static String MS_PUBLISH = loadString( "Publish" );
    public   static String MS_SUBSCR_REMOVE = loadString( "Remove subscription" );
    public   static String MS_SET = loadString( "Set" );
    public   static String MS_APPLICATION = loadString( "Application" );
    public   static String MS_BOOKMARKS = loadString( "Bookmarks" );
    public   static String MS_TEST_SOUND = loadString( "Test sound" );
    public   static String MS_STARTUP = loadString( "Startup" );
    public   static String MS_EDIT_RULE = loadString( "Edit rule" );
    public   static String MS_CANCEL = loadString( "Cancel" );
    public   static String MS_CLOSE = loadString( "Close" );
    public   static String MS_ARCHIVE = loadString( "Archive" );
    public   static String MS_FREE = loadString( "free " );
    public   static String MS_CONFERENCE = loadString( "Conference" );
    public   static String MS_SOUND = loadString( "Sound" );
    public   static String MS_LOGIN_FAILED = loadString( "Login failed" );
    public   static String MS_DISCOVER = loadString( "Browse" ); //"Discover"
    public   static String MS_NEW_JID = loadString( "New Jid" );
    public   static String MS_PLAIN_PWD = loadString( "plain-text password" );
    public   static String MS_PASTE_NICKNAME = loadString( "Paste Nickname" );
    public   static String MS_KICK = loadString( "Kick" );
    public   static String MS_CLEAR_LIST = loadString( "Remove readed" );
    public   static String MS_GRANT_VOICE = loadString( "Grant Voice" );
    public   static String MS_MOVE_DOWN = loadString( "Move Down" );
    public   static String MS_QUOTE = loadString( "Quote" );
    public   static String MS_ROSTER_ELEMENTS = loadString( "Roster elements" );
    public   static String MS_ENABLE_POPUP = loadString( "popup from background" );
    public   static String MS_SMILES = loadString( "smiles" );
    public   static String MS_ABOUT = loadString( "About" );
    public   static String MS_RESOURCE = loadString( "Resource" );
    public   static String MS_DISCONNECTED = loadString( "Disconnected" );
    public   static String MS_EDIT = loadString( "Edit" );
    public   static String MS_HOST_IP = loadString( "Host name/IP (optional)" );
    public   static String MS_ADD_RULE = loadString( "Add rule" );
    public   static String MS_ALL_STATUSES = loadString( "for all status types" );
    public   static String MS_PASTE_JID = loadString( "Paste Jid" );
    public   static String MS_GOTO_URL = loadString( "Goto URL" );
    public   static String MS_CLOCK = loadString( "Clock -" );
    public   static String MS_LOGIN = loadString( "Login" );
    public   static String MS_CLOCK_OFFSET = loadString( "Clock offset" );
    public   static String MS_YES = loadString( "Yes" );
    public   static String MS_FLASHBACKLIGHT = loadString( "flash backlight" );
    public   static String MS_SUSPEND = loadString( "Suspend" );
    public   static String MS_ALERT_PROFILE_CMD = loadString( "Alert Profile >" );
    public   static String MS_MY_VCARD = loadString( "My vCard" );
    public   static String MS_TRANSPORTS = loadString( "transports" );
    public   static String MS_NEW_ACCOUNT = loadString( "New Account" );
    public   static String MS_SELF_CONTACT = loadString( "self-contact" );
    public   static String MS_VCARD = loadString( "vCard" );
    public   static String MS_SET_SUBJECT = loadString( "Set Subject" );
    public   static String MS_TOOLS = loadString( "Tools" );
    public   static String MS_JABBER_TOOLS = loadString( "Jabber Tools" );
    public   static String MS_PORT = loadString( "Port" );
    public   static String MS_RESUME = loadString( "Resume Message" );
    public   static String MS_PROXY_ENABLE = loadString( "proxy CONNECT" );
    public   static String MS_PROXY_HOST = loadString( "Proxy name/IP" );
    public   static String PROXY_PORT = loadString( "Proxy port" );
    public   static String MS_ARE_YOU_SURE_WANT_TO_DISCARD = loadString( "Are You sure want to discard " );
    public   static String MS_FROM_OWNER_TO = loadString( " from OWNER to " );
    public   static String MS_MODIFY_AFFILIATION = loadString( "Modify affiliation" );
    public   static String MS_AUTOFOCUS = loadString( "Autofocus" );
    public   static String MS_ADD_TO_ROSTER = loadString( "Add to roster" );
    public   static String MS_CLEAR=loadString( "Clear" );
    public   static String MS_ALT_LANG="langfile";
    public   static String MS_GRANT_MEMBERSHIP=loadString("Grant Membership");
    public   static String MS_SELLOGIN = loadString( "Connect" );
//--toon
    public   static String MS_UNAFFILIATE = loadString("Unaffiliate");
    public   static String MS_GRANT_MODERATOR = loadString("Grant Moderator");
    public   static String MS_REVOKE_MODERATOR = loadString("Revoke Moderator");
    public   static String MS_GRANT_ADMIN = loadString("Grant Admin");
    public   static String MS_GRANT_OWNERSHIP = loadString("Grant Ownership");
//--toon
    
    public   static String MS_VIZITORS_FORBIDDEN=loadString("Visitors are not allowed to send messages to all occupants");
    public   static String MS_IS_INVITING_YOU=loadString(" is inviting You to ");
    public   static String MS_ASK_SUBSCRIPTION=loadString( "Ask subscription");
    public   static String MS_GRANT_SUBSCRIPTION=loadString("Grant subscription");
    public   static String MS_INVITE=loadString("Invite to conference");
    public   static String MS_INVITE_REASON=loadString("Reason");
    public   static String MS_YOU_HAVE_BEEN_INVITED=loadString("You have been invited to ");
    public final static String MS_SURE_CLEAR="Are You sure want to clear messagelist?";
    public   static String MS_DISCO_ROOM=loadString("Participants");
    public   static String MS_CAPS_STATE=loadString("Abc");
    
    public   static String MS_STORE_PRESENCE = loadString( "room presences" );
    
    public   static String MS_IS_NOW_KNOWN_AS=loadString(" is now known as ");
    public   static String MS_WAS_BANNED=loadString(" was banned ");
    public   static String MS_WAS_KICKED=loadString(" was kicked ");
    public   static String MS_HAS_BEEN_KICKED_BECAUSE_ROOM_BECAME_MEMBERS_ONLY=loadString(" has been kicked because room became members-only");
    public   static String MS_HAS_LEFT_CHANNEL=loadString(" has left the channel");
    public   static String MS_HAS_JOINED_THE_CHANNEL_AS=loadString(" has joined the channel as ");
    public   static String MS_AND=loadString(" and ");
    public   static String MS_IS_NOW=loadString(" is now ");    
    
    public   static String MS_TOKEN=loadString("Google token request");
    
    public final static String MS_SASL="SASL login";
    public final static String MS_FEATURES="Features";
    public final static String MS_SHOWPWD="Show password";
    public final static String MS_NO_VERSION_AVAILABLE="No client version available";
    public final static String MS_MSG_LIMIT="Message limit";
    
    public static String MS_OPENING_STREAM=loadString("Opening stream");
    public static String MS_ZLIB=loadString("Using compression");
    public static String MS_AUTH=loadString("Authenticating");
    public static String MS_RESOURCE_BINDING=loadString("Resource binding");
    public static String MS_SESSION=loadString("Initiating session");
    
    public final static String MS_TEXTWRAP="Text wrapping";
    public final static String MS_TEXTWRAP_CHARACTER="by chars";
    public final static String MS_TEXTWRAP_WORD="by words";

    public final static String MS_INFO="Info";
    
    public  static String MS_ONLINE=loadString("online");
    
    public  static String MS_ERROR=loadString("error");
    public  static String MS_CHAT=loadString("chat");
    public  static String MS_AWAY=loadString("away");
    public  static String MS_XA=loadString("xa");
    public  static String MS_DND=loadString("dnd");
    public  static String MS_INVISIBLE=loadString("invisible");
    public  static String MS_OFFLINE=loadString("offline");
    
    

    public static String MS_REPLY=loadString("Reply");
    public final static String MS_DIRECT_PRESENCE=loadString("Send status");
    
    public final static String MS_CONFIRM_BAN=loadString("Are you sure want to BAN this person?");
    public final static String MS_NO_REASON=loadString("No reason");

    public final static String MS_RECENT=loadString("Recent");
    public final static String MS_CAMERASHOT=loadString("Shot");

    public final static String MS_SELECT_FILE=loadString("Select file");
    public final static String MS_LOAD_PHOTO=loadString("Load Photo");
    public final static String MS_CLEAR_PHOTO=loadString("Clear Photo");
    public final static String MS_CAMERA=loadString("Camera");
    
    public final static String MS_HIDE_FINISHED=loadString("Hide finished");
    public final static String MS_TRANSFERS=loadString("Transfer tasks");


    public static String MS_COLOR_TUNE=loadString("Color tune");
    public static String MS_LOAD_SKIN=loadString("Load Scheme");
    
    public static String MS_SELECT_HISTORY_FOLDER=loadString("Select history folder");
    public static String CLASSIC_CHAT=loadString("Classic chat");
    public static String MS_NEW_MENU=loadString("Show new menu");
    public static String MS_SOUND_VOLUME=loadString("Sound volume");
    public static String MS_LANGUAGE=loadString("Language");
    public static String MS_SAVE_HISTORY=loadString("Save history");
    public static String MS_SAVE_PRESENCES=loadString("Save presences");
    public static String MS_SAVE_HISTORY_CONF=loadString("Save conference history");
    public static String MS_SAVE_PRESENCES_CONF=loadString("Save conference presences");
    public static String MS_1251_CORRECTION=loadString("1251 correction");
    public static String MS_HISTORY_FOLDER=loadString("History folder");
    public static String MS_AUTOSTATUS=loadString("AutoStatus");
    public static String MS_AUTOSTATUS_TIME=loadString("AutoStatus time (min)");

    public static String MS_COPY=loadString("Copy");
    public static String MS_PASTE=loadString("Paste");
    
    public static String MS_SAVE_TEMPLATE=loadString("Save template");
    public static String MS_TEMPLATE=loadString("Template");


    public static String MS_HAS_SET_TOPIC_TO=loadString("has set topic to");

    public static String MS_SEEN=loadString("Seen");
    public static String MS_IDLE=loadString("Idle");

    public static String MS_MAIN_MENU=loadString("Main menu");

    public static String MS_ROOT=loadString("Root");
    public static String MS_FILE_TRANSFERS=loadString("File transfers");

    public static String MS_CHOOSE_STATUS=loadString("Choose status");
    public static String MS_ADD_STATUS=loadString("Add status");
    public static String MS_REMOVE_STATUS=loadString("Remove status");

    public static String MS_USER=loadString("User");
    public static String MS_REASON=loadString("Reason");

    public static String MS_ADRESS=loadString("Address");
    public static String MS_EXPORT_TO_FILE=loadString("Export to file");

    public static String MS_VIEW=loadString("View");
    public static String MS_STOP=loadString("Stop");

    public static String MS_PATH=loadString("Path");
    public static String MS_ACCEPT_FILE=loadString("Accept file");
    public static String MS_FILE=loadString("File");
    public static String MS_SAVE_TO=loadString("Save To");
    public static String MS_SENDER=loadString("Sender");
    public static String MS_SIZE=loadString("Size");
    public static String MS_DESCRIPTION=loadString("Description");
    public static String MS_REJECTED=loadString("Rejected");
    public static String MS_SEND_FILE=loadString("Send file");
    public static String MS_TO=loadString("To");
    public static String MS_CANT_OPEN_FILE=loadString("Can't open file");
    
    public static String MS_NEW=loadString("New");
    public static String MS_NEW_TEMPLATE=loadString("New Template");

    public static String MS_SAVE_PHOTO=loadString("Save photo");

    public static String MS_USER_REMOVED_AUTORIZATION=loadString("User has removed autorization");
    public static String MS_YOU_ARE_NOW_AUTHORIZED=loadString("You are now autorized");
    public static String MS_USER_REQUEST_AUTORIZATION=loadString("User request autorization");
	
    public final static String MS_SURE_DELETE="Are you sure want to delete this message?";

    public static String MS_MESSAGE_FOR_ME="Message for me";
    
    public static String MS_BALLOON_INK=loadString("balloon ink");
    public static String MS_BALLOON_BGND=loadString("balloon background");
    public static String MS_LIST_BGND=loadString("messagelist & roster background");
    public static String MS_LIST_BGND_EVEN=loadString("messagelist & roster even lines");
    public static String MS_LIST_INK=loadString("messagelist & roster & common font");
    public static String MS_MSG_SUBJ=loadString("message subject");
    public static String MS_MSG_HIGHLIGHT=loadString("message highlight");
    public static String MS_DISCO_CMD=loadString("service discovery commands");
    public static String MS_BAR_BGND=loadString("Header & Bottom background");
    public static String MS_BAR_INK=loadString("Header font");
    public static String MS_CONTACT_DEFAULT=loadString("contact default");
    public static String MS_CONTACT_CHAT=loadString("contact chat");
    public static String MS_CONTACT_AWAY=loadString("contact away");
    public static String MS_CONTACT_XA=loadString("contact extended away");
    public static String MS_CONTACT_DND=loadString("contact do not disturb");
    public static String MS_GROUP_INK=loadString("group color");
    public static String MS_BLK_INK=loadString("keylock font");
    public static String MS_BLK_BGND=loadString("keylock background");
    public static String MS_MESSAGE_IN=loadString("message incoming");
    public static String MS_MESSAGE_OUT=loadString("message outgoing");
    public static String MS_MESSAGE_PRESENCE=loadString("message presence");
    public static String MS_MESSAGE_AUTH=loadString("message auth");
    public static String MS_MESSAGE_HISTORY=loadString("message history");
    public static String MS_PGS_REMAINED=loadString("progress bar remained");
    public static String MS_PGS_COMPLETE=loadString("progress bar complete");
    public static String MS_PGS_BORDER=loadString("progress border");
    public static String MS_PGS_BGND=loadString("progress bar background");
    public static String MS_HEAP_TOTAL=loadString("Heap mon total");
    public static String MS_HEAP_FREE=loadString("Heap mon free");
    public static String MS_CURSOR_BGND=loadString("Cursor background");
    public static String MS_CURSOR_OUTLINE=loadString("Cursor ink & outline");
    public static String MS_SCROLL_BRD=loadString("Scroll border");
    public static String MS_SCROLL_BAR=loadString("Scroll bar");
    public static String MS_SCROLL_BGND=loadString("Scroll back");    

    public static String MS_SOUNDS_OPTIONS=loadString("Sounds options");
	
    public static String MS_NEW_BOOKMARK=loadString("New conference");
	
    public static String MS_DECLINE=loadString("Decline");
    public static String MS_AUTH_NEW=loadString("Authorize new contacts");
    public static String MS_AUTH_AUTO=loadString("[auto-subscribe]");
	
    public final static String MS_KEEPALIVE="Keep-alive";
    
    public static String MS_TIME=loadString("Time");
        
    public final static String MS_HAS_BEEN_UNAFFILIATED_AND_KICKED_FROM_MEMBERS_ONLY_ROOM=" has been unaffiliated and kicked from members-only room";
    
    public final static String MS_AWAY_PERIOD="Minutes before away";
    public final static String MS_AWAY_TYPE="Automatic Away";
    public final static String MS_AWAY_OFF="disabled";
    public final static String MS_AWAY_LOCK="keyblock / flip";
    public final static String MS_AWAY_IDLE="idle";
    
    public  static String MS_ROLE_PARTICIPANT=loadString("participant");
    public  static String MS_ROLE_MODERATOR=loadString("moderator");
    public  static String MS_ROLE_VISITOR=loadString("visitor");
    
    public  static String MS_AFFILIATION_NONE=loadString("none");
    public  static String MS_AFFILIATION_MEMBER=loadString("member");
    public  static String MS_AFFILIATION_ADMIN=loadString("admin");
    public  static String MS_AFFILIATION_OWNER=loadString("owner");
    
    public  static String MS_SEC1=loadString("second");
    public  static String MS_SEC2=loadString("seconds");
    public  static String MS_SEC3=loadString("seconds_");
    
    public  static String MS_MIN1=loadString("minute");
    public  static String MS_MIN2=loadString("minutes");
    public  static String MS_MIN3=loadString("minutes_");
    
    public  static String MS_HOUR1=loadString("hour");
    public  static String MS_HOUR2=loadString("hours");
    public  static String MS_HOUR3=loadString("hours_");
    
    public  static String MS_DAY1=loadString("day");
    public  static String MS_DAY2=loadString("days");
    public  static String MS_DAY3=loadString("days_");
    
    public static String MS_XMLLANG;
    public static String MS_IFACELANG;

    
    private SR() { }
    
    private static Hashtable lang;
    
    private static String loadString(String key) {
        if (lang==null) {
            String langFile=Config.getInstance().langFileName();
            if (langFile==null) lang=new Hashtable(); 
            else lang=new StringLoader().hashtableLoader(langFile);
            System.out.print("Loading locale ");
            System.out.println(langFile);
            MS_XMLLANG=(String)lang.get("xmlLang");
            
            MS_IFACELANG=MS_XMLLANG;
            if (MS_IFACELANG==null) MS_IFACELANG="en";
        }
        String value=(String)lang.get(key);
//#if LOCALE_DEBUG
        if (value==null) {
            if (Config.getInstance().lang!=0) {
                System.out.print("Can't find local string for <");
                System.err.print(key);
                System.err.println('>');
            }
        }
//#endif
        return (value==null)?key:value;
    }

    public static void loaded() {
        lang=null;
    }
}
