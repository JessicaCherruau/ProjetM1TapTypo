package boyot.fr.TapTypo;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.net.InetAddress;
import java.util.ArrayList;


public class WaitingRoomActivity extends Activity {
    private static final String TAG = "taptypo.WaitingRoomA.";

    boolean isHost = false;
    InetAddress inetAddress;
    int port;

    public Button sendButton;
    public Button playButton;
    public EditText txt;
    public ListView list;
    public ArrayAdapter listAdapter;
    private ConnexionThreadReaderThread thread;
    private ConnexionThread connexionThread = null;

    private Handler handler = new Handler();

    public class ConnexionThreadReaderThread extends Thread{
        boolean keepgoing = true;

        @Override
        public void run() {
            while(keepgoing){
                Log.d(TAG, "Thread launched");
                while(true) {
                    if (connexionThread != null){
                        Log.v(TAG, "ConnexionThreadReaderThread running");
                        final String message = connexionThread.read();
                        if (message != null) {
                            if(message.contains(GroupOwnerThread.LAUNCH_GAME)){
                                this.keepgoing = false;
                                Intent intent = new Intent(getApplicationContext(), TapTypoClientActivity.class);
                                intent.putExtra("inetAddress", (InetAddress) null);
                                intent.putExtra("port", port);
                                intent.putExtra("nomJoueur", message.replace(GroupOwnerThread.LAUNCH_GAME+":", ""));
                                startActivity(intent);
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listAdapter.add(message);
                                    listAdapter.notifyDataSetChanged();
                                }
                            });
                            Log.d(TAG, "manager.read() : " + message);
                        }
                        else {
                            try {
                                sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        Intent intent = getIntent();
        isHost = (boolean) intent.getBooleanExtra("isHost", false);
        inetAddress = (InetAddress) intent.getSerializableExtra("inetAddress");
        port = (int) intent.getIntExtra("port", 0);

        connexionThread = new ConnexionThread(inetAddress, port);
        connexionThread.start();

        sendButton = (Button) findViewById(R.id.SendButton);
        playButton = (Button) findViewById(R.id.PlayButton);
        txt = (EditText) findViewById(R.id.editText);
        list = (ListView) findViewById(R.id.listView);

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        list.setAdapter(listAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Send button clicked");
                if (connexionThread != null) {
                    connexionThread.write(txt.getText().toString());
                }
                txt.setText("");
            }
        });

        if(isHost) {
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Play button clicked");
                    int nbPlayers = 1;
                    String name = "";
                    if (connexionThread != null) {
                        connexionThread.write(GroupOwnerThread.LAUNCH_GAME);    //lance une requête pour obtenir le nombre de joueurs
                        String _nbPlayers = null;
                        while((_nbPlayers = connexionThread.read()) == null);
                        nbPlayers = Integer.parseInt(_nbPlayers);

                        //receive his name
                        while((name = connexionThread.read()) == null);
                    }
                    thread.keepgoing = false;
                    Intent intent = new Intent(getApplicationContext(), TapTypoHostActivity.class);
                    intent.putExtra("nbPlayers", nbPlayers);
                    intent.putExtra("inetAddress", (InetAddress) null);
                    intent.putExtra("port", port);
                    intent.putExtra("nomJoueur", name);
                    startActivity(intent);
                }
            });
        }

        thread = new ConnexionThreadReaderThread();
        Log.d(TAG, "new ConnexionThreadReaderThread() done");
    }

    @Override
    protected void onResume() {
        super.onResume();
        thread.start();
    }

    @Override
    protected void onStop() {
        thread.keepgoing = false;
        super.onPause();
    }
}
