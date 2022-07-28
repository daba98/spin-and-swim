package de.die_bartmanns.spinnandfly.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import de.die_bartmanns.spinnandfly.Data;
import de.die_bartmanns.spinnandfly.R;

/**
 * Created by Daniel on 29.03.2018.
 */
public class GameOverActivity extends Activity {


    private TextView scoreView;
    private TextView bestView;

    private int score;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_over_screen);

        Util.hideBottomNavigationBar(this);
        Util.disableDarkMode(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        score = bundle.getInt("score");

        initUI();
    }

    private void initUI(){
        scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.setText(String.valueOf(score));

        bestView = (TextView) findViewById(R.id.BestView);
        int highscore = Data.getHighscore(getApplicationContext());
        if(score > highscore){
            bestView.setText(String.valueOf(score));
            Data.storeHighscore(getApplicationContext(), score);
        }
        else
            bestView.setText(String.valueOf(highscore));
    }


    public void clickedRestart(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void clickedHome(View view){
        Intent intent = new Intent(getApplicationContext(), StartScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        }

    public void clickedFish(View view){
        Intent intent = new Intent(getApplicationContext(), ChooseFishActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {}
}
