package com.oscode.pointr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        SensorEventListener {

    private TextView mTextView;

    RatingBar ratingBar;
    LayerDrawable stars;
    ImageView bigarrow;
    Animation animation;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    /**
     * variables for compass sensing
     */
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;

    private double latitude;
    private double longitude;
    private float trueDirection;
    private float direction = 0;
    ArrayList<LittleArrow> arrows = new ArrayList<LittleArrow>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        configureGPS();

                stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                ratingBar = (RatingBar) findViewById(R.id.ratingBar);
                stars = (LayerDrawable) ratingBar.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(Color.rgb(0x2d, 0x9c, 0x93), PorterDuff.Mode.SRC_ATOP);
                bigarrow = (ImageView) findViewById(R.id.big_arrow);
//                animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
//                bigarrow.startAnimation(animation);
                final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relLayout);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        arrows.add(new LittleArrow(getApplicationContext(), 35));
                        relativeLayout.addView(arrows.get(0));
                    }
                }, 2000);
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        arrows.add(new LittleArrow(getApplicationContext(), 90));
                        relativeLayout.addView(arrows.get(1));
                    }
                }, 3000);
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        arrows.add(new LittleArrow(getApplicationContext(), 270));
                        relativeLayout.addView(arrows.get(2));
                    }
                }, 4000);
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        arrows.add(new LittleArrow(getApplicationContext(), 300));
                        relativeLayout.addView(arrows.get(3));
                        Log.d("Pointr", "last point loaded");
                    }
                }, 5000);

            }
        });




    }
    void makeLittleArrow(int degree) {

    }
    class LittleArrow extends ImageView {

        public float getDegree() {
            return degree;
        }

        public void setDegree(float degree) {
            this.degree = degree;
        }

        private float degree = 0.0f;
        public LittleArrow(Context context, float d) {
            super(context);
            this.setImageResource(R.drawable.small_arrow);
            degree = d;
            setScaleType(ScaleType.FIT_START);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            setLayoutParams(layoutParams);
            setRotation(degree);
        }

        public void rotate(float delta) {
            setRotation(degree = (degree + delta) % 360);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            trueDirection = -azimuthInDegress;
            Log.d("SensorChange", "" + direction);
            float delta = (float) ((direction - trueDirection) * 0.95);
            for (LittleArrow a : arrows) {
                a.rotate(delta);
            }
            if (bigarrow != null) {
                bigarrow.setRotation((bigarrow.getRotation()+delta)%360);
            }
            direction += delta;
        }


//        direction = (float) (event.values[0]);


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

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
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
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
}
