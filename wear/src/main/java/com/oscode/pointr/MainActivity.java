package com.oscode.pointr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private TextView mTextView;

    RatingBar ratingBar;
    LayerDrawable stars;
    ImageView bigarrow;
    Animation animation;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        configureGPS();

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
                        Log.d("Pointr", "last point loaded");
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


    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3000)
                .setFastestInterval(3000);

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, locationRequest, (LocationListener) this)
                .setResultCallback(new ResultCallback() {
                    @Override
                    public void onResult(Result status) {
                        if (status.getStatus().isSuccess()) {
                            if (Log.isLoggable("IMPORTANT_TAG", Log.DEBUG)) {
                                Log.d("IMPORTANT_TAG", "Successfully requested location updates");
                            }
                        } else {
                            Log.e("IMPORTANT_TAG", "Failed in requesting location updates, ");
                        }
                    }
                });
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d("OUR GPS", "Location: " + mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude());
        }
        else {
            Log.d("OUR GPS", "Location: null");
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("OUR GPS", "Location: " + location.getLatitude() + ", " + location.getLongitude());
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void configureGPS() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)  // used for data layer API
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (Log.isLoggable("IMPORTANT_TAG", Log.DEBUG)) {
            Log.d("IMPORTANT_TAG", "connection to location client suspended");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
}
