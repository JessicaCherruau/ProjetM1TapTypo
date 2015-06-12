package boyot.fr.TapTypo;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class JoinActivity extends ListActivity implements Manager.ManagerUser {
    private static final String TAG = "taptypo.JoinActivity";

    private Manager manager;
    private final IntentFilter intentFilter = new IntentFilter();
    private ArrayAdapter<String> peerListAdapter;
    private ConnexionThread connexionThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        //wifiManager.setWifiEnabled(false);
        //wifiManager.setWifiEnabled(true);

        //setContentView(R.layout.activity_host);
        peerListAdapter = new ArrayAdapter<String>(this/*, R.id.listView*/, android.R.layout.simple_list_item_1, new ArrayList<String>());
        setListAdapter(peerListAdapter);

        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        manager = new Manager(this);
        //manager.startRegistration(serverSocket.getLocalPort());
        manager.discoverService();

    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(manager, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(manager);
    }

    @Override
    protected void onStop() {
        manager.clear();
        super.onStop();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        manager.connect(position);
    }

    ///////////////////////////// implementation de l'interface ManagerUser
    @Override
    public void refreshList(ArrayList<String> newList){
        peerListAdapter.clear();
        if(!newList.isEmpty()){
            peerListAdapter.addAll(newList);
        }
        else {
            peerListAdapter.add("Liste vide :(");
        }
        peerListAdapter.notifyDataSetChanged();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public WifiP2pManager getWifiP2pManager() {
        return (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
    }

    @Override
    public void setConnexionThread(InetAddress dstAddress, int port) {

        Intent intent = new Intent(this, WaitingRoomActivity.class);
        intent.putExtra("inetAddress", (InetAddress) dstAddress);
        intent.putExtra("port", port);
        startActivity(intent);
    }
}
