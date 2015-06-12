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

    public Button btn;
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
        connexionThread = new ConnexionThread((InetAddress) intent.getSerializableExtra("inetAddress"),
                (int) intent.getIntExtra("port", 0));
        connexionThread.start();

        btn = (Button) findViewById(R.id.SendButton);
        txt = (EditText) findViewById(R.id.editText);
        list = (ListView) findViewById(R.id.listView);

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        list.setAdapter(listAdapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Send button clicked");
                if (connexionThread != null) {
                    connexionThread.write(txt.getText().toString());
                }
                txt.setText("");
            }
        });
        Log.d(TAG, "setOnClickListener done");
        thread = new ConnexionThreadReaderThread();
        Log.d(TAG, "new ConnexionThreadReaderThread() done");
    }

    @Override
    protected void onResume() {
        super.onResume();
        thread.start();
    }

    @Override
    protected void onPause() {
        thread.keepgoing = false;
        super.onPause();
    }
}
