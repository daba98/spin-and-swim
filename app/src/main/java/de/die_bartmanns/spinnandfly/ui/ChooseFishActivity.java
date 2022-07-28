package de.die_bartmanns.spinnandfly.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Comparator;

import de.die_bartmanns.spinnandfly.Data;
import de.die_bartmanns.spinnandfly.Fish;
import de.die_bartmanns.spinnandfly.Model;
import de.die_bartmanns.spinnandfly.R;

/**
 * Created by Daniel on 02.04.2018.
 */
public class ChooseFishActivity extends Activity {

    private final int NR_FISHES = Fish.values().length;
    private int NR_COLUMNS;

    private LayoutInflater inflater;
    private Data myData;

    private int shells;

    private TableLayout table;
    private TextView shellView;
    private Fish[] fishes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_fish);

        Util.hideBottomNavigationBar(this);
        Util.disableDarkMode(this);
        init();
    }

    private void init(){
        inflater = LayoutInflater.from(getApplicationContext());
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        NR_COLUMNS = (int) (width / (getResources().getDimension(R.dimen.unlocked_item_width) +
                                    getResources().getDimension(R.dimen.margin_scroll_view) * 2));
        myData = Data.getMyData(getApplicationContext());
        shells = myData.shells;
        fishes = Fish.values();
        Arrays.sort(fishes, new Comparator<Fish>() {
            @Override
            public int compare(Fish fish1, Fish fish2) {
                if(fish1.isBuyable() && !fish2.isBuyable())
                    return -1;
                if(!fish1.isBuyable() && fish2.isBuyable())
                    return 1;
                if(fish1.isBuyable() && fish2.isBuyable()) {
                    if (fish1.getCost() < fish2.getCost())
                        return -1;
                    if (fish1.getCost() > fish2.getCost())
                        return 1;
                    return fish1.ordinal() - fish2.ordinal();
                }
                else{
                    return Math.abs(fish1.getCost()) - Math.abs(fish2.getCost());
                }
            }
        });
        initUI();
    }

    private void initUI(){
        shellView = (TextView) findViewById(R.id.shellTextView);
        shellView.setText(String.valueOf(shells));

        RelativeLayout background = findViewById(R.id.chooseFishBackground);
        background.setBackgroundResource(Model.getNextBackgroundResId());

        table =(TableLayout)findViewById(R.id.fishTable);
        fillTableLayout();

        float y = getResources().getDimension(R.dimen.table_margin_top);
        table.animate().y(y).setDuration(600).setInterpolator(new OvershootInterpolator());

    }

    private void fillTableLayout(){
        for(int i = 0; i <= NR_FISHES / NR_COLUMNS; i++)
            createtableRow(i);
    }

    private void createtableRow(int rowIdx){
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        for(int i = 0; i < NR_COLUMNS; i++){
            int fishIdx = rowIdx * NR_COLUMNS + i;
            if(fishIdx == NR_FISHES)
                break;
            Fish fish = fishes[fishIdx];
            if(myData.isFishUnlocked(fish))
                row.addView(createUnlockedView(fish));
            else if(fish.isBuyable())
                row.addView(createLockedView(fish));
            else
                row.addView(createLockedUnbuyableView(fish));
        }

        table.addView(row);
    }

    private View createUnlockedView(Fish fish){
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.unlocked_fish, null, false);
        ImageView fishView = (ImageView) layout.findViewById(R.id.fishView);
        AnimationDrawable fishAnim = fish.getFishAnim(getBaseContext());
        fishView.setImageDrawable(fishAnim);
        fishAnim.start();

        Animation upDownAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.choose_fish_anim);
        fishView.startAnimation(upDownAnim);

        addMargin(layout);
        layout.setOnClickListener(new UnlockedFishClickListener());

        return layout;
    }

    private View createLockedView(Fish fish){
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.locked_fish, null, false);
        layout.setBackground(getDrawable(R.drawable.background_anim));
        TextView costView = (TextView) layout.findViewById(R.id.costView);
        costView.setText(String.valueOf(fish.getCost()));

        ImageView icon = layout.findViewById(R.id.icon);
        icon.setVisibility(View.VISIBLE);

        addMargin(layout);
        layout.setOnClickListener(new LockedFishClickListener());

        return layout;
    }

    private View createLockedUnbuyableView(Fish fish){
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.locked_fish, null, false);
        layout.setBackground(getDrawable(R.drawable.rect_round_corn_dark_grey));
        TextView costView = (TextView) layout.findViewById(R.id.costView);
        costView.setText("???");
        costView.setTextColor(Color.WHITE);

        ImageView icon = layout.findViewById(R.id.icon);
        icon.setVisibility(View.INVISIBLE);


        addMargin(layout);
        layout.setOnClickListener(new LockedUnbuyableFishClickListener());

        return layout;
    }

    private void addMargin(View view){
        android.widget.TableRow.LayoutParams p = new android.widget.TableRow.LayoutParams();
        p.rightMargin = (int) getResources().getDimension(R.dimen.margin_table_layout);
        p.leftMargin = (int) getResources().getDimension(R.dimen.margin_table_layout);
        p.bottomMargin = (int) getResources().getDimension(R.dimen.margin_table_layout);
        p.topMargin = (int) getResources().getDimension(R.dimen.margin_table_layout);

        view.setLayoutParams(p);
    }

    private class UnlockedFishClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            TableRow row = (TableRow) v.getParent();
            int fishIdx = table.indexOfChild(row) * NR_COLUMNS + row.indexOfChild(v);
            myData.currentFish = fishes[fishIdx].ordinal();
            myData.storeData(getApplicationContext());
            onBackPressed();
        }
    }

    private class LockedFishClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            TableRow row = (TableRow) v.getParent();
            int fishIdx = table.indexOfChild(row) * NR_COLUMNS + row.indexOfChild(v);
            Fish fish = fishes[fishIdx];
            int cost = fish.getCost();
            if(cost > myData.shells){
                AnimationDrawable animationDrawable = (AnimationDrawable) v.getBackground();
                animationDrawable.stop();
                animationDrawable.setEnterFadeDuration(50);
                animationDrawable.setExitFadeDuration(300);
                animationDrawable.start();
            }
            else{
                myData.shells -= cost;
                myData.unlockedFishes.add(fish.ordinal());
                myData.storeData(getApplicationContext());
                int idx = row.indexOfChild(v);
                row.removeView(v);
                row.addView(createUnlockedView(fish), idx);
                shellView.setText(String.valueOf(myData.shells));
            }
        }
    }

    private class LockedUnbuyableFishClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            TableRow row = (TableRow) v.getParent();
            int fishIdx = table.indexOfChild(row) * NR_COLUMNS + row.indexOfChild(v);
            Fish fish = fishes[fishIdx];
            int cost = fish.getCost();
            Toast.makeText(getBaseContext(), "A highscore of at least " + Math.abs(cost) + " unlocks this secret fish", Toast.LENGTH_LONG).show();
        }
    }

    public void clickedBack(View view){
        onBackPressed();
    }
}
