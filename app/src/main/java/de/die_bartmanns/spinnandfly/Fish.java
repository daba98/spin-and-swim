package de.die_bartmanns.spinnandfly;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;

/**
 * Created by Daniel on 02.04.2018.
 */
public enum Fish {

    BLUE_FISH(R.drawable.fish, R.drawable.fisch_2, 0),
    RED_FISH(R.drawable.fisch_rot, R.drawable.fisch_rot2, 10),
    ORANGE(R.drawable.fisch_orange, R.drawable.fisch_orange2, 10),
    PURPLE(R.drawable.fisch_lila, R.drawable.fisch_lila2, 10),

    STRIPED_DARK(R.drawable.gestreift_dunkel, R.drawable.gestreift_dunkel2, 20),
    GREEN_FISH(R.drawable.fisch_gruen, R.drawable.fisch_gruen2, 20),
    YELLOW_FISH(R.drawable.fisch_gelb, R.drawable.fisch_gelb2, 20),
    HEARTFISH_ORANGE(R.drawable.herzfisch_orange, R.drawable.herzfisch_orange2, 20),
    YIN_YANG_GREEN(R.drawable.yin_yang_gruen, R.drawable.yin_yang_gruen2, 20),

    THINFISH_RED(R.drawable.fisch_thin_rot, R.drawable.fisch_thin_rot2, 40),
    CLOWNFISH(R.drawable.clownfisch, R.drawable.clownfisch2, 40),
    ROSA(R.drawable.fisch_rosa, R.drawable.fisch_rosa2, 40),
    HEARTFISH_PURPLE(R.drawable.herzfisch_lila, R.drawable.herzfisch_lila2, 40),
    STRIPED_YELLOW(R.drawable.gestreift_gelb, R.drawable.gestreift_gelb2, 40),

    THINFISH_BLUE(R.drawable.fisch_thin_blau, R.drawable.fisch_thin_blau2, 50),
    STRIPED_PURPLE(R.drawable.gestreift_lila, R.drawable.gestreift_lila2, 50),
    DARK_GREEN(R.drawable.fisch_dunkelgruen, R.drawable.fisch_dunkelgruen2, 50),
    FISH_ORANGE_2(R.drawable.fisch_farbverlauf_orange, R.drawable.fisch_farbverlauf_orange2, 50),
    JELLYFISH_RED(R.drawable.qualle_rot, R.drawable.qualle_rot2, 50),
    BROWN(R.drawable.fisch_braun, R.drawable.fisch_braun2, 50),
    ANGLERFISH_BLUE(R.drawable.anglerfisch_blau, R.drawable.anglerfisch_blau2, 50),
    THINFISH_GREY(R.drawable.fisch_thin_grau, R.drawable.fisch_thin_grau2, 50),

    STRIPED_GREEN(R.drawable.gestreift_gruen, R.drawable.gestreift_gruen2, 80),
    HEARTFISH_YELLOW(R.drawable.herzfisch, R.drawable.herzfisch2, 80),
    DIAMONDFISH_RED(R.drawable.karofisch_rot, R.drawable.karofisch_rot2, 80),
    KALMAR_BLUE(R.drawable.kalmar_blau, R.drawable.kalmar_blau2, 80),
    FISH_MASK_YELLOW(R.drawable.fisch_maske_gelb, R.drawable.fisch_maske_gelb2, 80),
    HEARTFISH_WHITE(R.drawable.herzfisch_weiss, R.drawable.herzfisch_weiss2, 80),
    DIAMONDFISH_YELLOW(R.drawable.karofisch_gelb, R.drawable.karofisch_gelb2, 80),


    SHARK(R.drawable.shark, R.drawable.shark2, 100),
    DIAMONDFISH_GREEN(R.drawable.karofisch_gruen, R.drawable.karofisch_gruen2, 100),
    HEARTFISH_RED(R.drawable.herzfisch_rot, R.drawable.herzfisch_rot2, 100),
    FAT_FISH_GREEN(R.drawable.fisch4_gruen, R.drawable.fisch4_gruen2, 100),
    YIN_YANG_BLACK(R.drawable.yin_yang_schwarz, R.drawable.yin_yang_schwarz2, 100),
    BLOWFISH_RED(R.drawable.kugelfisch_rot, R.drawable.kugelfisch_rot2klein, 100),
    THINFISH_WHITE(R.drawable.fisch_thin_weiss, R.drawable.fisch_thin_weiss2, 100),
    FISH_MASK_DARK(R.drawable.fisch_maske_schwarz, R.drawable.fisch_maske_schwarz2, 100),
    JELLYFISH_BLUE(R.drawable.qualle_blau, R.drawable.qualle_blau2, 100),
    TWO_COLOUR_FISH_RED(R.drawable.fisch_zweifarbig, R.drawable.fisch_zweifarbig2, 100),
    STRIPED_PINK(R.drawable.gestreift_pink, R.drawable.gestreift_pink2, 100),
    HEARTFISH_GREEN(R.drawable.herzfisch_gruen, R.drawable.herzfisch_gruen2, 100),
    KALMAR_BLACK(R.drawable.kalmar_schwarz, R.drawable.kalmar_schwarz2, 100),
    FISH_GREEN_2(R.drawable.fisch_gruen_version2, R.drawable.fisch_gruen_version2_2, 100),


    BLOWFISH(R.drawable.kugelfisch, R.drawable.kugelfisch2klein, 120),
    ANGLERFISH(R.drawable.anglerfisch, R.drawable.anglerfisch2, 120),
    DIAMONDFISH_BLUE(R.drawable.karofisch_blau, R.drawable.karofisch_blau2, 120),
    TWO_COLOUR_FISH_YELLOW(R.drawable.fisch_zweifarbig_gelb, R.drawable.fisch_zweifarbig_gelb2, 120),
    FAT_FISH_BLUE(R.drawable.fisch4_blau, R.drawable.fisch4_blau2, 120),
    YIN_YANG_BROWN(R.drawable.yin_yang_braun, R.drawable.yin_yang_braun2, 120),

    THINFISH_GREEN(R.drawable.fisch_thin_gruen, R.drawable.fisch_thin_gruen2, 150),
    FISH_ANGEL(R.drawable.fisch_heiligenschein, R.drawable.fisch_heiligenschein2, 150),
    KALMAR_RED(R.drawable.kalmar_rot, R.drawable.kalmar_rot2, 150),
    TWO_COLOUR_FISH_PURPLE(R.drawable.fisch_zweifarbig_lila, R.drawable.fisch_zweifarbig_lila2, 150),
    FAT_FISH_RED(R.drawable.fisch4_rot, R.drawable.fisch4_rot2, 150),
    ANGLERFISH_RED(R.drawable.anglerfisch_rot, R.drawable.anglerfisch_rot2, 150),
    BLOWFISH_BLUE(R.drawable.kugelfisch_blau, R.drawable.kugelfisch_blau2klein, 150),
    JELLYFISH_PURPLE(R.drawable.qualle_lila, R.drawable.qualle_lila2, 150),


    ORCA(R.drawable.orka, R.drawable.orka2, 200),
    FISH_MASK(R.drawable.fisch_maske_rot, R.drawable.fisch_maske_rot2, 200),
    TWO_COLOUR_FISH_TURQUOIS(R.drawable.fisch_zweifarbig_tuerkis, R.drawable.fisch_zweifarbig_tuerkis2, 200),
    FISH_YELLOW(R.drawable.fisch_gelb_version2, R.drawable.fisch_gelb_version2_2, 200),
    ANGLERFISH_DARK(R.drawable.anglerfisch_dunkel, R.drawable.anglerfisch_dunkel2, 200),
    FAT_FISH_YELLOW(R.drawable.fisch4_gelb, R.drawable.fisch4_gelb2, 200),

    SCELETT_FISH(R.drawable.skelett_fisch, R.drawable.skelett_fisch2, 300),
    TWO_COLOUR_FISH_WHITE(R.drawable.zebrafisch, R.drawable.zebrafisch2, 300),
    FISH_RED(R.drawable.fisch_rot_version2, R.drawable.fisch_rot_version2_2, 300),




    RED_WHITE(R.drawable.fisch_rot_weiss, R.drawable.fisch_rot_weiss2, -10),
    WHALE(R.drawable.wal, R.drawable.wal2, -20),
    RETRO(R.drawable.retro_goldfisch, R.drawable.retro_goldfisch2, -30),
    FISH_CROWN(R.drawable.fisch_krone, R.drawable.fisch_krone2, -40);


    private final int mainDrawResId;
    private final int secDrawResId;
    private final int cost;

    Fish(int draw1ResId, int draw2ResId, int cost){
        mainDrawResId = draw1ResId;
        secDrawResId = draw2ResId;
        this.cost = cost;
    }

    public int getMainDrawResId(){return mainDrawResId;}

    public int getSecDrawResId(){return secDrawResId;}

    public int getCost(){return cost;}

    public boolean isBuyable(){return cost >= 0;}

    public AnimationDrawable getFishAnim(Context context){
        AnimationDrawable anim = new AnimationDrawable();
        anim.addFrame(context.getDrawable(mainDrawResId), 400);
        anim.addFrame(context.getDrawable(secDrawResId), 400);
        return anim;
    }

}
