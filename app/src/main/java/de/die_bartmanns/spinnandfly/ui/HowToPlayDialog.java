package de.die_bartmanns.spinnandfly.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import de.die_bartmanns.spinnandfly.R;

public class HowToPlayDialog extends DialogFragment {

    private final int RADIUS = 75;

    private ImageView fishView;
    private TextView explanationView;
    private TextView midView;

    private float fishWidth;
    private float fishHeight;

    private ValueAnimator fishAnimator;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.how_to_play_screen, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        Util.disableDarkMode(getActivity());

        fishView = dialogView.findViewById(R.id.fishView);
        explanationView = dialogView.findViewById(R.id.explanationView);
        midView = dialogView.findViewById(R.id.midView);

        fishWidth = getResources().getDimension(R.dimen.fish_view_width);
        fishHeight = getResources().getDimension(R.dimen.fish_view_height);

        midView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                animateSpinning();
                midView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        explanationView.setText(getString(R.string.spining_explanation));

        return alertDialog;
    }

    private void animateExplanation(final String newText){
        explanationView.animate().alpha(0).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                explanationView.setText(newText);
                explanationView.animate().alpha(1).setDuration(500);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void animateSpinning(){
        ViewGroup.LayoutParams layoutParams = fishView.getLayoutParams();
        layoutParams.height = (int) fishHeight;
        fishView.setLayoutParams(layoutParams);
        fishView.setX(midView.getX() - fishWidth / 2);
        fishView.setY(midView.getY() - fishHeight / 2 - RADIUS);

        Path p = new Path();
        RectF circle = new RectF(midView.getX() - RADIUS - fishWidth / 2, midView.getY() - RADIUS - fishHeight / 2,
                midView.getX() + RADIUS - fishWidth / 2, midView.getY() + RADIUS - fishHeight / 2);

        p.arcTo(circle, -90, 180);
        p.arcTo(circle, 90, 180);

        p.arcTo(circle, -90, 180);
        p.arcTo(circle, 90, 180);

        p.arcTo(circle, -90, 180);
        p.arcTo(circle, 90, 180);

        fishAnimator = ObjectAnimator.ofFloat(fishView, "x", "y", p);
        fishAnimator.setDuration(5000);
        fishAnimator.setInterpolator(null);
        fishAnimator.addListener(new SpinAnimationListener());
        fishAnimator.start();
    }

    private void animateStraightLine(){
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float screenWidth = metrics.widthPixels;

        Path p = new Path();
        p.moveTo(fishView.getX(), fishView.getY());
        p.lineTo(screenWidth, fishView.getY());
        fishAnimator = ObjectAnimator.ofFloat(fishView, "x", "y", p);
        fishAnimator.setDuration(4000);
        fishAnimator.setInterpolator(null);
        fishAnimator.addListener(new LineAnimationListener());
        fishAnimator.start();
    }

    class SpinAnimationListener implements Animator.AnimatorListener{

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animateExplanation(getString(R.string.gap_explanation));
            animateStraightLine();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    class LineAnimationListener implements Animator.AnimatorListener{
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animateExplanation(getString(R.string.spining_explanation));
            animateSpinning();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        fishAnimator.cancel();
        fishAnimator.removeAllListeners();
        super.onDismiss(dialog);
    }
}
