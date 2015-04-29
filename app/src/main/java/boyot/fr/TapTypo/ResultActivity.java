package boyot.fr.TapTypo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import boyot.fr.TapTypo.R;

public class ResultActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Statistics result = extras.getParcelable("result");
            TextView duree = (TextView) findViewById(R.id.duree);
            duree.setText(duree.getText()+" "+result.getTimelapse());
            TextView erreur = (TextView) findViewById(R.id.nberreurs);
            erreur.setText(erreur.getText()+" "+result.getNbErrors());
            TextView longest = (TextView) findViewById(R.id.longest_streak);
            longest.setText(longest.getText()+" "+result.getLongestStreak());
            TextView score = (TextView) findViewById(R.id.score);
            score.setText(score.getText()+" "+result.getScore());
        }
        Button buttonrejouer = (Button) findViewById(R.id.rejouer);
        buttonrejouer.setOnClickListener(this);
    }

    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.rejouer:
                Intent intent = new Intent(getApplicationContext(), TapTypoActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
