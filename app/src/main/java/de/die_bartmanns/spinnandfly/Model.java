package de.die_bartmanns.spinnandfly;

import java.util.Random;

/**
 * Created by Daniel on 30.03.2018.
 */
public class Model {

    private final static Random RANDOM = new Random();
    private final static int NUMBER_BACKGROUNDS = 5;

    private static float fishViewHeight;
    private static float fishViewWidth;

    public static float getFishViewHeight() {
        return fishViewHeight;
    }

    public static void setFishViewHeight(float fishViewHeight) {
        Model.fishViewHeight = fishViewHeight;
    }

    public static float getFishViewWidth() {
        return fishViewWidth;
    }

    public static void setFishViewWidth(float fishViewWidth) {
        Model.fishViewWidth = fishViewWidth;
    }

    public static  int getNextBackgroundResId(){
        int rand = RANDOM.nextInt(NUMBER_BACKGROUNDS);
        int resId;
        if(rand == 0)
            resId = R.drawable.background1;
        else if(rand == 1)
            resId = R.drawable.background2;
        else if(rand == 2)
            resId = R.drawable.background3;
        else if(rand == 3)
            resId =R.drawable.background4;
        else
            resId =R.drawable.background5;

        return resId;
    }
}
