package de.die_bartmanns.spinnandfly;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 04.04.2018.
 */
public class Data {
    private static final String PREFS_NAME = "Meal";
    public static final String KEY_CURRENT_FISH_IDX = "fishIdx";
    public static final String KEY_SHELLS = "shells";
    public static final String KEY_HIGHSCORE = "highscore";
    public static final String KEY_UNLOCKED_FISHES_SIZE = "unlockedFishesSize";
    public static final String KEY_UNLOCKED_FISH_IDX = "unlockedFishIdx";

    public int currentFish;
    public int shells;
    public int highscore;
    public List<Integer> unlockedFishes = new ArrayList<Integer>(){{add(0);}};


    public static int getHighscore(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_HIGHSCORE, 0);
    }

    public static int getShells(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_SHELLS, 0);
    }

    public static int getCurrentFishIdx(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_CURRENT_FISH_IDX, 0);
    }

    public static Data getMyData(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Data data = new Data();
        data.currentFish = prefs.getInt(KEY_CURRENT_FISH_IDX, 0);
        data.shells = prefs.getInt(KEY_SHELLS, 0);
        data.highscore = prefs.getInt(KEY_HIGHSCORE, 0);
        int listSize = prefs.getInt(KEY_UNLOCKED_FISHES_SIZE, 0);
        for(int i = 0; i < listSize; i++){
            data.unlockedFishes.add(prefs.getInt(KEY_UNLOCKED_FISH_IDX + i, 0));
        }

        return data;
    }

    public static void storeHighscore(Context context, int highscore){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(KEY_HIGHSCORE, highscore);
        editor.commit();
    }

    public void storeData(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(KEY_CURRENT_FISH_IDX, currentFish);
        editor.putInt(KEY_SHELLS, shells);
        editor.putInt(KEY_HIGHSCORE, highscore);
        editor.putInt(KEY_UNLOCKED_FISHES_SIZE, unlockedFishes.size());
        for(int i = 0; i < unlockedFishes.size(); i++){
            editor.putInt(KEY_UNLOCKED_FISH_IDX + i, unlockedFishes.get(i));
        }

        editor.commit();
    }

    public boolean isFishUnlocked(Fish fish){
        return unlockedFishes.contains(fish.ordinal())|| (!fish.isBuyable() && highscore >= Math.abs(fish.getCost()));
    }
}
