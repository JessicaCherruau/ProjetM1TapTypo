package boyot.fr.TapTypo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RankActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT);
        Bundle extras = getIntent().getExtras();
        String[] tabScoreAndNom = null;
        if(extras != null) {
            String ranking = extras.getString("result");
            tabScoreAndNom = ranking.split(";");
            int X = 40;
            int Y = 40;
            for(int i=0; i < tabScoreAndNom.length; i++)
            {
                if(i == 0) {
                    TextView premier = new TextView(this);
                    premier.setText(tabScoreAndNom[i]);
                    premier.setX(X);
                    premier.setY(Y);
                    premier.setTextColor(Color.parseColor("#FBC513"));
                    layout.addView(premier, relativeParams);

                }
                else if(i == 1) {
                    TextView deuxieme = new TextView(this);
                    deuxieme.setText(tabScoreAndNom[i]);
                    deuxieme.setX(X);
                    deuxieme.setY(Y);
                    deuxieme.setTextColor(Color.parseColor("#908C7F"));
                    layout.addView(deuxieme, relativeParams);
                }
                else if(i == 2) {
                    TextView troisieme = new TextView(this);
                    troisieme.setText(tabScoreAndNom[i]);
                    troisieme.setX(X);
                    troisieme.setY(Y);
                    troisieme.setTextColor(Color.parseColor("#42350A"));
                    layout.addView(troisieme, relativeParams);
                }
                else if(i == 3)
                {
                    TextView quatrieme = new TextView(this);
                    quatrieme.setText(tabScoreAndNom[i]);
                    quatrieme.setX(X);
                    quatrieme.setY(Y);
                    quatrieme.setTextColor(Color.parseColor("#0000003"));
                    layout.addView(quatrieme, relativeParams);
                }
                else if(i == 4)
                {
                    TextView cinquieme = new TextView(this);
                    cinquieme.setText(tabScoreAndNom[i]);
                    cinquieme.setX(X);
                    cinquieme.setY(Y);
                    cinquieme.setTextColor(Color.parseColor("#000000"));
                    layout.addView(cinquieme, relativeParams);
                }

                Y = Y +40;

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rank, menu);
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
