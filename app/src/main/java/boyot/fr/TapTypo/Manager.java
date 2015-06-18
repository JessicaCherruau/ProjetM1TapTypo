package boyot.fr.TapTypo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Looper;
import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Manager extends BroadcastReceiver {
    private static final String TAG = "taptypo.Manager";

    private static final String SERVICE_TYPE = "_presence._tcp";
    private static final String INSTANCE_NAME = "_taptypo";
    private static final String LISTEN_PORT_KEY = "listenport";

    private int lastPortNumber;

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private ManagerUser managerUser;
    ArrayList<WifiP2pService> wifiP2pServicesList = new ArrayList<>();
    private WifiP2pDnsSdServiceRequest serviceRequest;
    private int connectionPort = 0;
    private GroupOwnerThread groupOwnerThread = null;

    private class WifiP2pService {
        public WifiP2pDevice wifiP2pDevice;
        public int port;

        public WifiP2pService(WifiP2pDevice wifiP2pDevice, int port) {
            this.wifiP2pDevice = wifiP2pDevice;
            this.port = port;
        }
    }

    public interface ManagerUser{
        Looper getMainLooper();
        Context getContext();
        WifiP2pManager getWifiP2pManager();

        void setConnexionThread(InetAddress dstAddress, int port);
        void refreshList(ArrayList<String> newList);
    }

    public interface SocketHandlerThread{
        public void write(String message);
        public String read();
        public boolean keepgoing=true;
    }

    public Manager(ManagerUser managerUser){

        this.managerUser = managerUser;
        this.wifiP2pManager = managerUser.getWifiP2pManager();
        this.channel = wifiP2pManager.initialize(managerUser.getContext(), managerUser.getMainLooper(), null);
    }

    public void connect(int position){
        // Picking the first device found on the network.
        WifiP2pDevice device = wifiP2pServicesList.get(position).wifiP2pDevice;
        connectionPort = wifiP2pServicesList.get(position).port;

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 0;
        // demande à ne pas être group owner

        if (serviceRequest != null) {
            wifiP2pManager.removeServiceRequest(channel, serviceRequest,
                    new MyActionListener("removeServiceRequest"));
        }
        wifiP2pManager.connect(channel, config, new MyActionListener("connect"));
    }

    public void discoverService() {
        wifiP2pManager.setDnsSdResponseListeners(channel, dnsSdServiceResponseListener, dnsSdTxtRecordListener);
        wifiP2pManager.addServiceRequest(channel, serviceRequest = WifiP2pDnsSdServiceRequest.newInstance(), new MyActionListener("addServiceRequest"));
        wifiP2pManager.discoverServices(channel, new MyActionListener("discoverServices"));
    }

    public void startRegistration() {
        this.groupOwnerThread = new GroupOwnerThread(this);
        groupOwnerThread.start();
        //  Create a string map containing information about your service.
        HashMap record = new HashMap();
        record.put(LISTEN_PORT_KEY, String.valueOf(groupOwnerThread.getLocalPort()));
        Log.e(TAG, "port : " + groupOwnerThread.getLocalPort());
        //record.put("buddyname", "John Doe" + (int) (Math.random() * 1000));
        //record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(INSTANCE_NAME, SERVICE_TYPE, record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        wifiP2pManager.addLocalService(channel, serviceInfo, new MyActionListener("addLocalService"));
        discoverService();
        //wifiP2pManager.setDnsSdResponseListeners(channel, dnsSdServiceResponseListener, dnsSdTxtRecordListener);
        //wifiP2pManager.addServiceRequest(channel, serviceRequest = WifiP2pDnsSdServiceRequest.newInstance(), new MyActionListener("addServiceRequest"));
        //wifiP2pManager.discoverServices(channel, new MyActionListener("discoverServices"));
        //nécessaire pour que le service soit visible
        new Thread(){
            @Override
            public void run() {
                managerUser.setConnexionThread(null, groupOwnerThread.getLocalPort());
            }
        }.start();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d(TAG, "on receive WIFI_P2P_STATE_CHANGED_ACTION, wifi p2p enabled");
                //activity.setIsWifiP2pEnabled(true);
            } else {
                Log.e(TAG, "ATTENTION : WIFI DISABLED");
                //activity.setIsWifiP2pEnabled(false);
                //TODO faire qque chose d'utile ...
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // We are connected with the other device, request connection
                // info to find group owner IP
                wifiP2pManager.requestConnectionInfo(channel, connectionInfoListener);
            }
            Log.d(TAG, "on receive WIFI_P2P_CONNECTION_CHANGED_ACTION, isConnected : " + networkInfo.isConnected());
        }
    }

    //rafraichit la vue
    private void refreshList() {
        // If an AdapterView is backed by this data, notify it
        // of the change.  For instance, if you have a ListView of available
        // peers, trigger an update.
        ArrayList<String> newList = new ArrayList<>();
        for (int i=0; i<wifiP2pServicesList.size(); i++){
            newList.add(wifiP2pServicesList.get(i).wifiP2pDevice.deviceName);
        }
        managerUser.refreshList(newList);
        if (wifiP2pServicesList.size() == 0) {
            Log.d(TAG, "No devices found");
            return;
        }
    }

    public void clear(){
       // wifiP2pManager.removeGroup(channel, new MyActionListener("removeGroup"));
        wifiP2pManager.clearLocalServices(channel, new MyActionListener("clearLocalServices"));
        wifiP2pManager.clearServiceRequests(channel, new MyActionListener("clearServiceRequests"));
        wifiP2pManager.cancelConnect(channel, new MyActionListener("cancelConnect"));
        wifiP2pManager.stopPeerDiscovery(channel, new MyActionListener("stopPeerDiscovery"));

        if(groupOwnerThread!=null){
            groupOwnerThread.keepgoing = false;
        }
    }


    //////////////////////// nested classes ///////////////////


    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(final WifiP2pInfo info) {

            // InetAddress from WifiP2pInfo struct.
            InetAddress groupOwnerAddress = info.groupOwnerAddress;

            // After the group negotiation, we can determine the group owner.
            if (info.groupFormed && info.isGroupOwner) {
                //socketHandlerThread = new GroupOwnerThread();
                //socketHandlerThread.start();
                //Fait dans StartRegistration()

                // Do whatever tasks are specific to the group owner.
                // One common case is creating a server thread and accepting
                // incoming connections.
            } else if (info.groupFormed && connectionPort!=0) {
                managerUser.setConnexionThread(info.groupOwnerAddress, connectionPort);
                // The other device acts as the client. In this case,
                // you'll want to create a client thread that connects to the group
                // owner.
            }
            connectionPort = 0;
            Log.d(TAG, "onConnectionInfoAvailable, " + info.toString());
            Log.d(TAG, "port number " + lastPortNumber);
            //wifiP2pManager.removeGroup(channel, new MyActionListener("removeGroup"));
        }
    };


    private class MyActionListener implements WifiP2pManager.ActionListener{
        String tag;
        MyActionListener(String tag){
            this.tag = tag;
        }
        @Override
        public void onSuccess() {
            Log.d(TAG, tag + " success");
        }
        @Override
        public void onFailure(int reason) {
            switch(reason){
                case WifiP2pManager.BUSY:
                    Log.e(TAG, tag + " failure, BUSY");
                    break;
                case WifiP2pManager.ERROR:
                    Log.e(TAG, tag + " failure, ERROR");
                    break;
                case WifiP2pManager.P2P_UNSUPPORTED:
                    Log.e(TAG, tag + " failure, P2P_UNSUPPORTED");
                    break;
            }
        }
    }

    ////////////////////// DNS sd listeners

    WifiP2pManager.DnsSdServiceResponseListener dnsSdServiceResponseListener =
        new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice wifiP2pDevice) {
                if (registrationType.contains(SERVICE_TYPE) && instanceName.contains(INSTANCE_NAME)) {
                    wifiP2pServicesList.add(new WifiP2pService(wifiP2pDevice, lastPortNumber));
                    refreshList();
                    Log.d(TAG, "onBonjourServiceAvailable " + instanceName);
                }
            }
        };

    WifiP2pManager.DnsSdTxtRecordListener dnsSdTxtRecordListener = new WifiP2pManager.DnsSdTxtRecordListener() {
        @Override
        /* Callback includes:
         * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
         * record: TXT record dta as a map of key/value pairs.
         * device: The device running the advertised service.*/
        public void onDnsSdTxtRecordAvailable(
                String fullDomain, Map record, WifiP2pDevice wifiP2pDevice) {
            lastPortNumber = Integer.parseInt((String) record.get(LISTEN_PORT_KEY));
            //TODO : moche
            Log.d(TAG, "DnsSdTxtRecord available - " + record.toString());
            //buddies.put(device.deviceAddress, record.get("buddyname"));
            //wifiP2pDeviceList.add(wifiP2pDevice);
            //refreshList();

            //rien ici dans la demo
        }
    };

    public int getClientNumber(){
        if(groupOwnerThread!=null){
            return groupOwnerThread.getClientCount();
        } else return 0;
    }
}
