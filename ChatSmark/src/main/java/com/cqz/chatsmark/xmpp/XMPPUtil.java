package com.cqz.chatsmark.xmpp;

import java.io.IOException;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.Builder;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.android.AndroidSmackInitializer;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.address.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.delay.provider.DelayInformationProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jivesoftware.smackx.muc.provider.MUCAdminProvider;
import org.jivesoftware.smackx.muc.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.muc.provider.MUCUserProvider;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.privacy.provider.PrivacyProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.si.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
import org.jivesoftware.smackx.xhtmlim.provider.XHTMLExtensionProvider;
//import org.jivesoftware.smackx.xroster.provider.RosterExchangeProvider;
//import org.jivesoftware.smackx.xevent.provider.MessageEventProvider;

import android.util.Log;

public class XMPPUtil {
    private int SERVER_PORT = 5222;
    private String SERVER_HOST = "192.168.8.123";//192.168.8.215
    private AbstractXMPPConnection connection;
    private String SERVER_NAME = "chenqingzhen-pc";//
    private static XMPPUtil xmppUtil;

    public static XMPPUtil getInstance() {
        if (xmppUtil == null) {
            xmppUtil = new XMPPUtil();
        }
        return xmppUtil;
    }

    public AbstractXMPPConnection getConnection() {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }

    public boolean openConnection() {
        new AndroidSmackInitializer().initialize();
        try {
            if (null == connection || !connection.isAuthenticated()) {
                // XMPPConnection.DEBUG_ENABLED = true;// ����DEBUGģʽ
                // ��������
                XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration
                        .builder();
                builder.setHost(SERVER_HOST);
                builder.setServiceName(SERVER_NAME);
                builder.setPort(SERVER_PORT);
                builder.setSendPresence(true);
                builder.setCompressionEnabled(false);
                builder.setSecurityMode(SecurityMode.disabled);

                XMPPTCPConnectionConfiguration config = builder.build();
                connection = new XMPPTCPConnection(config);
                try {
                    connection.connect();
                } catch (SmackException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }// ���ӵ�������
                // ���ø���Provider��������ã�����޷��������
                configureConnection(new ProviderManager());
                return true;
            }
        } catch (XMPPException xe) {
            xe.printStackTrace();
            connection = null;
        }
        return false;
    }

    /**
     * �ر�����
     */
    public void closeConnection() {
        if (connection != null) {
            // �Ƴ��B�ӱO 
            // connection.removeConnectionListener(connectionListener);
            if (connection.isConnected())
                connection.disconnect();

            connection = null;
        }
        Log.i("XmppConnection", "�P�]�B��");
    }

    public void configureConnection(ProviderManager pm) {

        // Private Data Storage
        pm.addIQProvider("query", "jabber:iq:private",
                new PrivateDataManager.PrivateDataIQProvider());

        // Time
        try {
            pm.addIQProvider("query", "jabber:iq:time",
                    Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            Log.w("TestClient",
                    "Can't load class for org.jivesoftware.smackx.packet.Time");
        }

        // // Roster Exchange
        // pm.addExtensionProvider("x", "jabber:x:roster",
        // new RosterExchangeProvider());
        //
        // // Message Events
        // pm.addExtensionProvider("x", "jabber:x:event",
        // new MessageEventProvider());

        // Chat State
        pm.addExtensionProvider("active",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());

        // XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
                new XHTMLExtensionProvider());

        // Group Chat Invitations
        pm.addExtensionProvider("x", "jabber:x:conference",
                new GroupChatInvitation.Provider());

        // Service Discovery # Items
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
                new DiscoverItemsProvider());

        // Service Discovery # Info
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
                new DiscoverInfoProvider());

        // Data Forms
        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

        // MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
                new MUCUserProvider());

        // MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
                new MUCAdminProvider());

        // MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
                new MUCOwnerProvider());

        // Delayed Delivery
        pm.addExtensionProvider("x", "jabber:x:delay",
                new DelayInformationProvider());

        // Version
        try {
            pm.addIQProvider("query", "jabber:iq:version",
                    Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            // Not sure what's happening here.
        }

        // VCard
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

        // Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
                new OfflineMessageRequest.Provider());

        // Offline Message Indicator
        pm.addExtensionProvider("offline",
                "http://jabber.org/protocol/offline",
                new OfflineMessageInfo.Provider());

        // Last Activity
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

        // User Search
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

        // SharedGroupsInfo
        pm.addIQProvider("sharedgroup",
                "http://www.jivesoftware.org/protocol/sharedgroup",
                new SharedGroupsInfo.Provider());

        // JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses",
                "http://jabber.org/protocol/address",
                new MultipleAddressesProvider());

        // FileTransfer
        pm.addIQProvider("si", "http://jabber.org/protocol/si",
                new StreamInitiationProvider());

        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
                new BytestreamsProvider());

        // Privacy
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.SessionExpiredError());
    }

    public boolean login(String account, String password) {
        boolean succ = false;
        try {
            if (getConnection() == null)
                return false;

            getConnection().login(account, password);
            succ = true;

            // ����ھQ��B
//			Presence presence = new Presence(Presence.Type.available);
//			getConnection().sendPacket(presence);
            // ����B�ӱO 
//			connectionListener = new TaxiConnectionListener();
//			getConnection().addConnectionListener(connectionListener);

        } catch (SmackException e) {
            succ=false;
            e.printStackTrace();
        } catch (IOException e) {
            succ=false;
            e.printStackTrace();
        } catch (XMPPException xe) {
            succ=false;
            xe.printStackTrace();
        }
        return succ;
    }

    /**
     * ע��
     *
     * @param account  ע���ʺ�
     * @param password ע������
     * @return 1��ע��ɹ� 0��������û�з��ؽ��2������˺��Ѿ�����3��ע��ʧ��
     */
    public String regist(String account, String password) {
        if (getConnection() == null)
            return "0";
        AccountManager am = AccountManager.getInstance(getConnection());
        am.sensitiveOperationOverInsecureConnection(true);
        try {
            am.createAccount(account, password);
            return "1";
        } catch (NoResponseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XMPPErrorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotConnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "3";

//        Registration reg = new Registration();  
//        reg.setType(IQ.Type.set);  
//        reg.setTo(getConnection().getServiceName());  
//        // ע������createAccountע��ʱ��������UserName������jid����"@"ǰ��Ĳ��֡�  
//        reg.setUsername(account);  
//        reg.setPassword(password);  
//        // ���addAttribute����Ϊ�գ�������?����������־��android�ֻ��İɣ���������  
//        reg.addAttribute("android", "geolo_createUser_android");  
//        PacketFilter filter = new AndFilter(new PacketIDFilter(  
//                reg.getPacketID()), new PacketTypeFilter(IQ.class));  
//        PacketCollector collector = getConnection().createPacketCollector(  
//                filter);  
//        getConnection().sendPacket(reg);  
//        IQ result = (IQ) collector.nextResult(SmackConfiguration  
//                .getDefaultPacketReplyTimeout());  
//        // Stop queuing resultsֹͣ����results���Ƿ�ɹ��Ľ��  
//        collector.cancel();  
//        if (result == null) {  
//            Log.e("regist", "No response from server.");  
//            return "0";  
//        } else if (result.getType() == IQ.Type.RESULT) {  
//            Log.v("regist", "regist success.");  
//            return "1";  
//        } else { // if (result.getType() == IQ.Type.ERROR)  
//            if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {  
//                Log.e("regist", "IQ.Type.ERROR: "  
//                        + result.getError().toString());  
//                return "2";  
//            } else {  
//                Log.e("regist", "IQ.Type.ERROR: "  
//                        + result.getError().toString());  
//                return "3";  
//            }  
//        }  
    }
}
