package boyot.fr.TapTypo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.net.InetAddress;
import java.util.ArrayList;


public class HostActivity extends Activity implements Manager.ManagerUser {
    private static final String TAG = "taptypo.HostActivity";

    private Manager manager;
    private final IntentFilter intentFilter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

       // WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
       // wifiManager.setWifiEnabled(true);
       // wifiManager.disconnect();

        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }

    @Override
    protected void onStart() {
        super.onStart();
        manager = new Manager(this);
        manager.startRegistration();
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
        //manager.clear();
        super.onStop();
    }



    ///////////////////////////// implementation de l'interface ManagerUser
    @Override
    public void refreshList(ArrayList<String> newList){
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
    public void setConnexionThread(InetAddress inetAddress, int port) {
        //connexionThread.start();

        Intent intent = new Intent(this, WaitingRoomActivity.class);
        intent.putExtra("inetAddress", (InetAddress) null);
        intent.putExtra("port", port);
        startActivity(intent);
    }
}
