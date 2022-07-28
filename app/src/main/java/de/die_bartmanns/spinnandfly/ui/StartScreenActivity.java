package de.die_bartmanns.spinnandfly.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.die_bartmanns.spinnandfly.Data;
import de.die_bartmanns.spinnandfly.Fish;
import de.die_bartmanns.spinnandfly.Model;
import de.die_bartmanns.spinnandfly.R;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Daniel on 04.04.2018.
 */
public class StartScreenActivity extends AppCompatActivity {

    private final Random RANDOM = new Random();

    private RelativeLayout layout;
    private RelativeLayout fishAnimLayout;
    private ImageView fishView;
    private TextView shellCounterView;
    private TextView whiteView;
    private Data myData;
    private Handler handler;

    private int height;
    private int width;
    private List<ImageView> fishes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.start_screen);

        MobileAds.setRequestConfiguration(new RequestConfiguration.Builder()
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_PG)
                .setTestDeviceIds(Arrays.asList("2C80123BA1634023258A473FCA77A888"))
        .build());

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

    @Override
    protected void onResume() {
        Util.hideBottomNavigationBar(this);
        Util.disableDarkMode(this);
        init();
        super.onResume();
    }

    private void init(){
        myData = Data.getMyData(getApplicationContext());
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        height = metrics.heightPixels;
        width = metrics.widthPixels;
        initUI();

        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int i = RANDOM.nextInt(5);
                if(i == 1)
                    animateFishes();
                handler.postDelayed(this, 100);
            }
        };

        handler.postDelayed(runnable, 100);
    }

    private void initUI(){
        whiteView = (TextView) findViewById(R.id.whiteView);
        whiteView.setVisibility(View.INVISIBLE);
        fishView = (ImageView) findViewById(R.id.fishView);
        AnimationDrawable fishAnim = Fish.values()[myData.currentFish].getFishAnim(getBaseContext());
        fishView.setImageDrawable(fishAnim);
        fishAnim.start();

        Animation upDownAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fish_start_screen);
        fishView.startAnimation(upDownAnim);

        shellCounterView = (TextView) findViewById(R.id.shellcountView);
        shellCounterView.setText(String.valueOf(myData.shells));

        fishAnimLayout = (RelativeLayout) findViewById(R.id.fishAnimLayout);

        layout = (RelativeLayout) findViewById(R.id.startLayout);
        layout.setBackgroundResource(Model.getNextBackgroundResId());
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFishes();
                circularTransitionAnim();
            }
        });

        ImageButton shellButton = findViewById(R.id.shellButton);
        shellButton.setColorFilter(getColor(R.color.dark_grey), android.graphics.PorterDuff.Mode.SRC_IN);

        ImageButton fishButton = findViewById(R.id.fishButton);
        fishButton.setColorFilter(getColor(R.color.dark_grey), android.graphics.PorterDuff.Mode.SRC_IN);

        ImageButton helpButton = findViewById(R.id.helpButton);
        helpButton.setColorFilter(getColor(R.color.dark_grey), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    protected void circularTransitionAnim() {
        int revealX = (int) (layout.getX() + layout.getWidth() / 2);
        int revealY = (int) (layout.getY() + layout.getHeight() / 2);
        float finalRadius = (float) (Math.max(layout.getWidth(), layout.getHeight()) * 1.1);

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(layout, revealX, revealY, finalRadius, 0);
        circularReveal.setDuration(800);
        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                whiteView.setVisibility(View.VISIBLE);

                Intent intent = new Intent(StartScreenActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        circularReveal.start();
    }

    public void clickedFish(View view){
        clearFishes();
        Intent intent = new Intent(getApplicationContext(), ChooseFishActivity.class);
        startActivity(intent);
    }

    public void clickedShell(View view){
        clearFishes();
        Intent intent = new Intent( getApplicationContext(), BuyShellsActivity.class);
        startActivity(intent);
    }

    public void clickedHelp(View view){
        HowToPlayDialog dialog = new HowToPlayDialog();
        dialog.show(getSupportFragmentManager(), "TEST");
    }


    private void animateFishes(){
        final ImageView fish = createFish();
        fishAnimLayout.addView(fish);
        fish.animate().x(width + 500).y(RANDOM.nextInt(height)).setDuration(6000 + RANDOM.nextInt(3000));
        fishes.add(fish);

        CountDownTimer timer = new CountDownTimer(9000, 9000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                fishAnimLayout.removeView(fish);
                fishes.remove(fish);
            }
        };
        timer.start();
    }

    private ImageView createFish(){
        ImageView view = new ImageView(getApplicationContext());
        float scale = (60 + RANDOM.nextInt(40)) / 100f;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (scale * fishView.getWidth()), (int) (scale * fishView.getHeight()));
        view.setLayoutParams(params);
        view.setY(RANDOM.nextInt(height - fishView.getHeight()));
        view.setX(0 - fishView.getWidth());
        Fish fish = Fish.values()[RANDOM.nextInt(Fish.values().length)];
        AnimationDrawable fishSwimmingAnim = getFishSwimmingAnim(fish);
        view.setImageDrawable(fishSwimmingAnim);
        fishSwimmingAnim.start();

        return view;
    }

    private AnimationDrawable getFishSwimmingAnim(Fish fish){
        AnimationDrawable anim = new AnimationDrawable();
        anim.addFrame(getDrawable(fish.getMainDrawResId()), 400);
        anim.addFrame(getDrawable(fish.getSecDrawResId()), 400);
        return anim;
    }

    private void clearFishes(){
        handler.removeCallbacksAndMessages(null);
        for(ImageView fish : fishes)
            fishAnimLayout.removeView(fish);

        fishes.clear();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus){
            Util.hideBottomNavigationBar(this);
        }
    }
}
