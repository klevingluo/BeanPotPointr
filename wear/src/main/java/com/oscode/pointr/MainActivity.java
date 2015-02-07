package com.oscode.pointr;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView mTextView;

    RatingBar ratingBar;
    LayerDrawable stars;
    ImageView bigarrow;
    Animation animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);



        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                ratingBar = (RatingBar) findViewById(R.id.ratingBar);
                stars = (LayerDrawable) ratingBar.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(Color.rgb(0x2d, 0x9c, 0x93), PorterDuff.Mode.SRC_ATOP);
                bigarrow = (ImageView) findViewById(R.id.big_arrow);
                animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                bigarrow.startAnimation(animation);
                final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relLayout);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        relativeLayout.addView(new LittleArrow(getApplicationContext()));
                    }
                }, 2000);
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        relativeLayout.addView(new LittleArrow(getApplicationContext()));
                    }
                }, 3000);
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        relativeLayout.addView(new LittleArrow(getApplicationContext()));
                    }
                }, 4000);
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        relativeLayout.addView(new LittleArrow(getApplicationContext()));
                    }
                }, 5000);
            }
        });




    }
    void makeLittleArrow(int degree) {

    }
    class LittleArrow extends ImageView {

        public LittleArrow(Context context) {
            super(context);
            this.setImageResource(R.drawable.small_arrow);
            setScaleType(ScaleType.FIT_START);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            setLayoutParams(layoutParams);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
            startAnimation(animation);
        }
    }
}
