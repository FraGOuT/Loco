package com.codeforfun.himanshu.loco;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {

    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder surfaceHolder;

    private View mQuestionCard;
    private CardView mVideoCardView;


    private static final String VIDEO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/VCHAT/chat/video/myvideo.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mVideoCardView = (CardView) findViewById(R.id.videoCardView);
        mQuestionCard = findViewById(R.id.questionCard);

        surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(this);

        runTask();
    }


    void runTask(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //Convert full screen video to small scree
                    minimizeVideo();
                    Log.i("TAGG","Minimize Video Complete");

                    //After that wait for 5 sec
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //Repeat the same process
                    maximizeVideo();
                    Log.i("TAGG","Maximize Video Complete");
                }
            }
        }).start();
    }


    private void performCircularRevel(View cardView){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = cardView.getWidth() / 2;
            int cy = cardView.getHeight() / 2;

            float finalRadius = (float) Math.hypot(cx, cy);
            Log.i("TAGG","final Rad = "+finalRadius);
            Animator anim = ViewAnimationUtils.createCircularReveal(cardView, cx, cy, 0, finalRadius);
            cardView.setVisibility(View.VISIBLE);
            anim.start();
        } else {
            cardView.setVisibility(View.VISIBLE);
        }
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }


    private void maximizeVideo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                mVideoCardView.setLayoutParams(params);

                mQuestionCard.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void minimizeVideo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mQuestionCard.setVisibility(View.INVISIBLE);

                LayoutParams params = new LayoutParams((int)convertDpToPixel(100),(int)convertDpToPixel(100));
                params.setMargins(0,(int)convertDpToPixel(93),0,0);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                mVideoCardView.setLayoutParams(params);

                performCircularRevel(mQuestionCard);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if(mMediaPlayer != null)
            mMediaPlayer.release();
        mMediaPlayer = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDisplay(holder);

        try{
            mMediaPlayer.setDataSource(VIDEO_PATH);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(MainActivity.this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
    }
}
