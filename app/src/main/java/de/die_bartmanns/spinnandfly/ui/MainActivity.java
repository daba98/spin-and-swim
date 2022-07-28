package de.die_bartmanns.spinnandfly.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.Arrays;
import java.util.Random;

import de.die_bartmanns.spinnandfly.Data;
import de.die_bartmanns.spinnandfly.Fish;
import de.die_bartmanns.spinnandfly.Model;
import de.die_bartmanns.spinnandfly.Position;
import de.die_bartmanns.spinnandfly.R;

public class MainActivity extends Activity {

    private final int RADIUS = 100;
    private final Random RANDOM = new Random();
    private  int MIN_GAP_WEIGHT;
    private  int WEIGHT_SUM;

    private ImageView fishView;
    private ImageView shellView;
    private TextView midView;
    private TextView gap;
    private TextView lowerBar;
    private TextView topBar;
    private TextView scoreView;
    private ImageView background;

    private LinearLayout obstacle;
    private RelativeLayout layout;
    private HorizontalScrollView scrollView;

    private Fish fish;
    private Position mid;
    private AnimationDrawable fishAnim;
    private Data myData;

    private int screenHeight;
    private int screenWidth;
    private int score = 0;
    private int shells = 0;
    private int moveBackgroundCounter;

    private float fishWidth;
    private float fishHeight;
    private float obstacleStartingX;

    private float lastX;

    private boolean isSpinning = true;
    private boolean clockwise = true;
    private boolean goldenShell;
    private boolean collectedShell = false;
    private boolean showedAd = false;
    private boolean animationCanceled = false;

    private ValueAnimator fishAnimator;
    private ValueAnimator obstacleAnimator;
    private ValueAnimator shellAnimator;

    private RewardedAd rewardedVideoAd;


    @Override
    protected void onPostResume() {
        super.onPostResume();
        Util.hideBottomNavigationBar(this);
        Util.disableDarkMode(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        if(!isTestDevice())
            initRewardedAd();
        init();

        if (savedInstanceState == null) {
            layout.setVisibility(View.INVISIBLE);

            final int revealX = screenWidth / 2;
            final int revealY = screenHeight / 2;

            ViewTreeObserver viewTreeObserver = layout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }

        setUpSpinning();
    }

    private boolean isTestDevice() {
        String testLabSetting = Settings.System.getString(getContentResolver(), "firebase.test.lab");
        return "true".equals(testLabSetting);
    }

    private void initRewardedAd() {
        AdRequest videoAdRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, getString(R.string.ad_unit_id_rewarded_video),
                videoAdRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        rewardedVideoAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        rewardedVideoAd = rewardedAd;
                        rewardedVideoAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                // Called when ad fails to show.
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                rewardedVideoAd = null;
                                if(showedAd)
                                    reviveAnim();
                                else
                                    startGameOverScreen();
                            }
                        });
                    }
                });
    }

    protected void revealActivity(int x, int y) {
        float finalRadius = (float) (Math.max(screenWidth, screenHeight) * 1.1);

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(layout, x, y, 0, finalRadius);
        circularReveal.setDuration(800);
        circularReveal.setInterpolator(new AccelerateInterpolator());

        layout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    private void init(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        WEIGHT_SUM = screenHeight;

        myData = Data.getMyData(getApplicationContext());

        fish = Fish.values()[myData.currentFish];
        fishWidth = getResources().getDimension(R.dimen.fish_view_width);
        fishHeight = getResources().getDimension(R.dimen.fish_view_height);
        fishAnim = fish.getFishAnim(getBaseContext());

        MIN_GAP_WEIGHT = Math.min((int) (3 * fishHeight), screenHeight / 2);

        Model.setFishViewHeight(fishHeight);
        Model.setFishViewWidth(fishWidth);

        mid = new Position(screenWidth / 4f, screenHeight / 2f);

        showedAd = false;
        initUI();
    }

    private void initUI(){
        layout = (RelativeLayout) findViewById(R.id.layout);
        layout.setOnClickListener(new ClickListener());

        fishView = (ImageView) findViewById(R.id.fishView);
        ViewGroup.LayoutParams layoutParams = fishView.getLayoutParams();
        layoutParams.height = (int) fishHeight;
        fishView.setLayoutParams(layoutParams);
        fishView.setX(screenWidth / 4f - fishWidth / 2);
        fishView.setY(screenHeight / 2f - fishHeight / 2 - RADIUS);
        fishView.setImageDrawable(fishAnim);

        midView = (TextView) findViewById(R.id.midView);
        gap = findViewById(R.id.gap);
        topBar = (TextView) findViewById(R.id.topBar);
        lowerBar = (TextView) findViewById(R.id.lowerBar);
        scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.setText("0");
        shellView = (ImageView) findViewById(R.id.shellView);

        background = (ImageView) findViewById(R.id.background);
        background.setImageResource(Model.getNextBackgroundResId());


        obstacle = (LinearLayout) findViewById(R.id.obstacle);
        obstacle.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        obstacleStartingX = obstacle.getX();
                        generateObstacle();
                        obstacle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        showShell();
                    }
                });

        scrollView = (HorizontalScrollView) findViewById(R.id.scrollView);
        AdView mAdView = findViewById(R.id.adView);
        if(!isTestDevice()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }

    private void startFishAnim(){
        fishView.setImageDrawable(fishAnim);
        fishAnim.start();
    }

    private void stopFishAnim(){
        fishAnim.stop();
        fishView.setImageResource(fish.getMainDrawResId());
    }

    private void setUpSpinning(){
        Path path;
        if(fishView.getY() <= screenHeight / 2f){
            mid = new Position(screenWidth / 4f, fishView.getY() + RADIUS);

            path = new Path();
            path.addCircle(mid.getX(), mid.getY(), RADIUS, Path.Direction.CW);
            clockwise = true;
        }
        else{
            mid = new Position(screenWidth / 4f, fishView.getY() - RADIUS);

            path = new Path();
            path.addCircle(mid.getX(), mid.getY(), RADIUS, Path.Direction.CCW);
            clockwise = false;
        }

        midView.setX(mid.getX() + fishWidth / 2);
        midView.setY(mid.getY() + fishHeight / 2);
        midView.setVisibility(View.VISIBLE);
        lastX = screenWidth;


        fishAnimator = ObjectAnimator.ofFloat(fishView, "x", "y", path);
        fishAnimator.setDuration(1200);
        fishAnimator.setInterpolator(null);
        fishAnimator.removeAllListeners();
        fishAnimator.setRepeatCount(ValueAnimator.INFINITE);
        fishAnimator.start();
        isSpinning = true;
    }

    private void generateObstacle(){
        obstacle.setWeightSum(WEIGHT_SUM);

        int gapWeight = MIN_GAP_WEIGHT + RANDOM.nextInt(WEIGHT_SUM / 3);
        LinearLayout.LayoutParams gapParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                gapWeight);
        gap.setLayoutParams(gapParams);

        int topWeight = 1 + RANDOM.nextInt(WEIGHT_SUM - gapWeight);
        LinearLayout.LayoutParams topParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                topWeight);
        topBar.setLayoutParams(topParams);

        int lowerWeight = WEIGHT_SUM - gapWeight - topWeight;
        LinearLayout.LayoutParams lowerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                lowerWeight);
        lowerBar.setLayoutParams(lowerParams);

        gap.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        generateShell();
                        obstacle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }

    private void generateShell(){
        collectedShell = false;
        float gapTop = gap.getY();
        int gapHeight = gap.getHeight();
        shellView.setY(gapTop + (gapHeight / 2) - (shellView.getHeight() / 2));

        int rand = RANDOM.nextInt(10);
        if(rand == 0) {
            shellView.setBackgroundResource(R.drawable.shell_gold);
            goldenShell = true;
        }
        else {
            shellView.setBackgroundResource(R.drawable.shell);
            goldenShell = false;
        }
    }

    private void startGameOverScreen(){
        myData.shells += shells;
        myData.storeData(getApplicationContext());
        scoreView.setVisibility(View.INVISIBLE);

        Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putInt("score", score);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void updateScore() {
        score++;
        scoreView.setText(String.valueOf(score));
        Animation textAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_anim);
        scoreView.startAnimation(textAnim);
    }

    private void startDeathAnim(){
        fishView.animate().rotationBy(90).setDuration(1000);
        fishView.animate().y(screenHeight - fishWidth).setDuration(1000);
        if(showedAd || rewardedVideoAd == null)
            startGameOverScreen();

        else
            showAdDialog();
    }

    private void die(){
        obstacleAnimator.cancel();
        fishAnimator.cancel();
        shellAnimator.cancel();
        stopFishAnim();
        startDeathAnim();
    }

    private void showAdDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.ad_popup);

        Button adButton = dialog.findViewById(R.id.adButton);
        adButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(rewardedVideoAd != null){
                    rewardedVideoAd.show(MainActivity.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            showedAd = true;
                        }
                    });
                }
            }
        });

        Button noButton = dialog.findViewById(R.id.noButton);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startGameOverScreen();
            }
        });

        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        int ui_flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        dialog.getWindow().getDecorView().setSystemUiVisibility(ui_flags);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isSpinning) {
                
                startFishAnim();
                midView.setVisibility(View.INVISIBLE);
                fishAnimator.cancel();
                isSpinning = false;

                moveBackgroundCounter = 0;

                float slope = getSlope();
                boolean movesForward = midView.getY() > fishView.getY() + fishHeight / 2;
                if(!clockwise)
                    movesForward = !movesForward;

                if(movesForward) {
                    Path fishPath = createFishPathForward(slope);
                    Path obstaclePath = getObstaclePath();
                    Path shellPath = getShellPath();
                    long animationTime = computeAnimationTime(fishPath, movesForward);

                    fishAnimator = ObjectAnimator.ofFloat(fishView, "x", "y", fishPath);
                    fishAnimator.setDuration(animationTime);
                    fishAnimator.setInterpolator(null);
                    fishAnimator.start();

                    obstacleAnimator = ObjectAnimator.ofFloat(obstacle, "x", "y", obstaclePath);
                    obstacleAnimator.setDuration(animationTime);
                    obstacleAnimator.setInterpolator(null);
                    obstacleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            float x = (float) valueAnimator.getAnimatedValue("x");
                            if(moveBackgroundCounter % 10 == 0 && scrollView.canScrollHorizontally(1)){
                                scrollView.scrollBy(1, 0);
                            }
                            moveBackgroundCounter++;
                            if (fishView.getX() < x && fishView.getX() + fishWidth > x) {
                                if (!isFishInGap())
                                    die();
                                else
                                    collectShellIfPossible();
                            } else if (lastX < x) {
                                shellAnimator.cancel();
                                shellView.clearAnimation();
                                shellView.setVisibility(View.INVISIBLE);
                                generateObstacle();
                            }

                            lastX = x;
                        }
                    });
                    obstacleAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            animationCanceled = false;
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (!animationCanceled) {
                                showShell();
                                updateScore();
                                setUpSpinning();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                            animationCanceled = true;
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {}
                    });
                    obstacleAnimator.start();

                    shellAnimator = ObjectAnimator.ofFloat(shellView, "x", "y", shellPath);
                    shellAnimator.setDuration(animationTime);
                    shellAnimator.setInterpolator(null);
                    shellAnimator.start();
                }
                else if(!movesForward){
                    Path fishPath = createFishPathBackward(slope);
                    long animationTime = computeAnimationTime(fishPath, movesForward);

                    fishAnimator = ObjectAnimator.ofFloat(fishView, "x", "y", fishPath);
                    fishAnimator.setDuration(animationTime);
                    fishAnimator.setInterpolator(null);
                    fishAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {}

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            midView.setVisibility(View.VISIBLE);
                            fishView.setX(mid.getX());
                            fishView.setY(clockwise ? mid.getY() - RADIUS : mid.getY() + RADIUS);
                            setUpSpinning();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {}

                        @Override
                        public void onAnimationRepeat(Animator animator) {}
                    });
                    fishAnimator.start();
                }
            }
        }
    }

    private void showShell(){
        shellView.setX(obstacleStartingX);
        shellView.setAlpha(0f);
        shellView.setVisibility(View.VISIBLE);
        shellView.animate().alpha(1).setDuration(800);

        Animation shellAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shell_anim);
        shellView.startAnimation(shellAnim);
    }

    private float getSlope(){
        float fishMidX = fishView.getX() + fishWidth / 2;
        float fishMidY = fishView.getY() + fishHeight / 2;

        return -1 / ((fishMidY  - midView.getY()) / (fishMidX - midView.getX()));
    }

    private Path getObstaclePath(){
        Path path = new Path();
        path.moveTo(obstacle.getX(), 0);
        path.lineTo(0, 0);
        path.moveTo(screenWidth, 0);
        path.lineTo(obstacle.getX(), 0);

        return path;
    }

    private Path getShellPath(){
        Path path = new Path();
        float y = shellView.getY();
        path.moveTo(shellView.getX(), y);
        path.lineTo(0, y);
        path.moveTo(screenWidth, y);
        path.lineTo(shellView.getX(), y);

        return path;
    }

    private Path createFishPathForward(float slope){
        Path path = new Path();
        float currentX = fishView.getX();
        float currentY = fishView.getY();
        float coveredXDistance = 0;
        float xDistanceToCover = screenWidth - (currentX - mid.getX());
        path.moveTo(currentX, currentY);

        if(isCloseToZero(slope)){
            return path;
        }

        while(coveredXDistance < xDistanceToCover) {
            float targetY = currentY;
            if (slope < 0) {
                targetY = Math.max(0, currentY + (xDistanceToCover - coveredXDistance) * slope);
            }
            else if(slope > 0){
                targetY = Math.min(screenHeight - fishHeight, currentY + (xDistanceToCover - coveredXDistance) * slope);
            }

            if(targetY == currentY)
                return path;

            path.lineTo(currentX, targetY);
            coveredXDistance += Math.abs((targetY - currentY) / slope);
            if(coveredXDistance == 0)
                coveredXDistance += xDistanceToCover / 15;
            currentY = targetY;
            if(currentY == 0 || currentY == screenHeight - fishHeight)
                slope = -slope;
        }

        return path;
    }

    private Path createFishPathBackward(float slope){
        Path path = new Path();
        float currentX = fishView.getX();
        float xDistanceToCover = currentX + fishWidth;
        float currentY = fishView.getY();
        float coveredXDistance = 0;
        path.moveTo(currentX, currentY);

        if(isCloseToZero(slope)){
            path.lineTo(0 - fishWidth, currentY);
            return path;
        }

        while(coveredXDistance < xDistanceToCover) {
            float targetY = currentY;
            if (slope > 0) {
                targetY = Math.max(0, currentY - (xDistanceToCover - coveredXDistance) * slope);
            }
            else if(slope < 0){
                targetY = Math.min(screenHeight - fishHeight, currentY - (xDistanceToCover - coveredXDistance) * slope);
            }

            if(targetY == currentY) {
                return path;
            }
            float targetX = currentX - Math.abs((targetY - currentY) / slope);
            if(targetX == currentX)
                targetX -= xDistanceToCover / 15;
            path.lineTo(targetX, targetY);
            coveredXDistance += Math.abs((targetY - currentY) / slope);
            currentY = targetY;
            currentX = targetX;
            if(currentY == 0 || currentY == screenHeight - fishHeight)
                slope = -slope;
        }

        return path;
    }

    private boolean isCloseToZero(float f){
        return -0.02 <= f && f <= 0.02;
    }

    private long computeAnimationTime(Path path, boolean moveForward){
        float length = getPathLength(path);
        long MIN_TIME;
        float minDistance;
        if(moveForward) {
            Path minPath = new Path();
            minPath.moveTo(0, 0);
            minPath.lineTo(screenWidth, 0);
            minDistance = getPathLength(minPath);
            MIN_TIME = 2000;
        }
        else{
            MIN_TIME = 500;
            minDistance = fishView.getX() + fishWidth;
        }
        return (long) (MIN_TIME * (1 + length / minDistance));
    }

    private float getPathLength(Path path){
        PathMeasure pathMeasure = new PathMeasure(path, false);
        float length = pathMeasure.getLength();
        while(pathMeasure.nextContour())
            length += pathMeasure.getLength();
        return length;
    }

    private boolean isFishInGap(){
        float gapTop = gap.getY();
        float gapBottom = gapTop + gap.getHeight();
        float fishTop = fishView.getY();
        float fishBottom = fishTop + fishHeight;

        return fishTop >= gapTop && fishBottom <= gapBottom;
    }

    private void collectShellIfPossible() {
        if (!collectedShell) {
            float shellTop = shellView.getY();
            float shellBottom = shellTop + shellView.getHeight();
            float fishTop = fishView.getY();
            float fishBottom = fishTop + fishView.getHeight();

            if ((shellTop < fishBottom && shellBottom > fishBottom) || (shellTop < fishTop && shellBottom > fishTop) || (shellTop > fishTop && shellBottom < fishBottom)) {
                shellView.clearAnimation();
                shellView.setVisibility(View.INVISIBLE);
                if (goldenShell) {
                    collectedShell = true;
                    shells = shells + 5;
                    gap.setText("+5");
                    gap.setTextColor(Color.parseColor("#daa520"));
                } else {
                    collectedShell = true;
                    shells++;
                    gap.setText("+1");
                    gap.setTextColor(Color.parseColor("#f68ace"));
                }
                gap.setAlpha(1);
                gap.animate().alpha(0).setDuration(1500).start();
            }
        }
    }

    private void reviveAnim(){
        fishView.animate().rotationBy(-90).setDuration(1000);
        fishView.animate().y(screenHeight / 2 - RADIUS - fishHeight / 2).setDuration(1000);
        obstacle.animate().x(obstacleStartingX).setDuration(1000);
        shellView.animate().x(obstacleStartingX).setDuration(1000);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fishView.setY(screenHeight / 2 - RADIUS - fishHeight / 2);
                setUpSpinning();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(fishAnimator != null)
            fishAnimator.cancel();
        if(obstacleAnimator != null)
            obstacleAnimator.cancel();
        if(shellAnimator != null)
            shellAnimator.cancel();
        super.onBackPressed();
    }
}
