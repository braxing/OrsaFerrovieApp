package it.orsaferrovie.orsaferrovieapp;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class PresentationActivity extends Activity {

    private ObjectAnimator animazione;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
        animazione = (ObjectAnimator) ObjectAnimator.ofFloat((ImageView) findViewById(R.id.imgOrSA), View.ALPHA, 0,1);
        animazione.setDuration(800);
        animazione.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                fineAnimazione();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void fineAnimazione() {
        //Avviamo l'altra activity
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        animazione.setStartDelay(500);
        animazione.start();
    }
}
