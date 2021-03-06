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
import android.net.Uri;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        SensorEventListener,
        DataApi.DataListener {

    private TextView mTextView;
    static final float ALPHA = 0.75f;

    RatingBar ratingBar;
    LayerDrawable stars;
    ImageView bigarrow;
    Animation animation;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    ArrayList<Locals> locations = new ArrayList<Locals>();
    ScrollView scrollView;

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
    private float direction = 0;
    ArrayList<LittleArrow> arrows = new ArrayList<LittleArrow>();
    TextView name;
    TextView distance;
    TextView description;
    RelativeLayout relativeLayout;
    LinearLayout linearLayout;

    class LocalsComparator implements Comparator<Locals>{

        @Override
        public int compare(Locals lhs, Locals rhs) {
            float lhsd = lhs.getDegrees();
            float rhsd = rhs.getDegrees();
            if (lhsd == rhsd) return 0;
            lhsd = Math.abs(Math.abs(lhsd) - 180);
            rhsd = Math.abs(Math.abs(rhsd) - 180);
            if (lhsd > rhsd) return 1;
            else return -1;

        }
    }

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
//                bigarrow.setVisibility(View.INVISIBLE);
                name = (TextView) findViewById(R.id.place_name);
                distance = (TextView) findViewById(R.id.distance);
                description = (TextView) findViewById(R.id.description);
//                animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
//                bigarrow.startAnimation(animation);
                relativeLayout = (RelativeLayout) findViewById(R.id.relLayout);
                scrollView = (ScrollView) findViewById(R.id.scrollView);
                linearLayout = (LinearLayout) findViewById(R.id.linlayout);
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable(){
//                    @Override
//                    public void run() {
//                        arrows.add(new LittleArrow(getApplicationContext(), 35));
//                        relativeLayout.addView(arrows.get(0));
//                    }
//                }, 2000);
//                handler.postDelayed(new Runnable(){
//                    @Override
//                    public void run() {
//                        arrows.add(new LittleArrow(getApplicationContext(), 90));
//                        relativeLayout.addView(arrows.get(1));
//                    }
//                }, 3000);
//                handler.postDelayed(new Runnable(){
//                    @Override
//                    public void run() {
//                        arrows.add(new LittleArrow(getApplicationContext(), 270));
//                        relativeLayout.addView(arrows.get(2));
//                    }
//                }, 4000);
//                handler.postDelayed(new Runnable(){
//                    @Override
//                    public void run() {
//                        arrows.add(new LittleArrow(getApplicationContext(), 300));
//                        relativeLayout.addView(arrows.get(3));
//                        Log.d("Pointr", "last point loaded");
//                    }
//                }, 5000);

            }
        });




    }
    void makeLittleArrow(int degree) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/places") == 0) {
                    Log.d("Watch", "Data changed!");
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    ArrayList<String> locations = dataMap.getStringArrayList("com.oscode.pointr.key.data");
                    this.locations = importData(locations);
                    final Locals front = maxLocal(this.locations);
                    final ArrayList<Locals>locs2 = this.locations;
                    Log.d("CLOSEST LOCAL", front.getName()+front.getDistance()+front.getRating()+front.getDegrees());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            name.setText(front.getName());
                            distance.setText(front.getDistance() + "m");
                            ratingBar.setRating(front.getRating());
                            description.setText(front.getDescription());
                            bigarrow.setRotation(front.getDegrees());
                            for (Locals l : locs2) {
                                arrows.add(new LittleArrow(getApplicationContext(), l.getDegrees() + direction));
                            }
                            for (LittleArrow l : arrows) {
                                relativeLayout.addView(l);
                            }
                        }
                    });


//                    relativeLayout.invalidate();
                    Log.d("Total number of arrows", arrows.size()+"");

//                    Wearable.DataApi.deleteDataItems(mGoogleApiClient, item.getUri());
                }

            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    public Locals maxLocal(ArrayList<Locals> locs) {
        int index = 0;
        LocalsComparator c = new LocalsComparator();
        for (int n = 0; n < locs.size(); n++) {
            Locals l = locs.get(n);
            if (c.compare(locs.get(index), locs.get(n)) < 0)
            {
                index = n;
            }
        }
        return locs.get(index);
    }

    private ArrayList<Locals> importData(ArrayList<String> dat){
        ArrayList<Locals> ret = new ArrayList<Locals>();
        for(String s : dat){
            String[] s2 = s.split("\t");
            Locals l = new Locals(s2[0], (float)Double.parseDouble(s2[1]), Double.parseDouble(s2[2]),Double.parseDouble(s2[3]), s2[4]);
            l.updateDistance(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            ret.add(l);
        }
        return ret;
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
        public void setRotate(float d) {
            setRotation(d);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    protected float[] lowPass(float[] input, float[] output) {
        if (output == null) return input;
        for (int i=0; i<input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
//            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
//            mLastAccelerometer = lowPass(mLastAccelerometer.clone(), mLastAccelerometer);
//            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometer = lowPass(mLastMagnetometer.clone(), mLastMagnetometer);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
//            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, null);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
//            float azimuthInRadians = mLastAccelerometer[2];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            float olddirection;
            olddirection = direction;
            direction = -azimuthInDegress;
//            Log.d("SensorChange", "" + direction);
            float delta = (direction - olddirection);

//            if (bigarrow != null) {
//                bigarrow.setRotation((bigarrow.getRotation()+delta)%360);
//            }
            if (scrollView.getScrollY() == 0) {
                for (int n = 0; n < this.locations.size(); n++) {
//                if (delta > 3)
                    this.locations.get(n).setDegrees((this.locations.get(n).getDegrees() + delta) % 360);
                    arrows.get(n).setRotate(this.locations.get(n).getDegrees());
                }
            }
        }

        if (this.locations.size() != 0) {
            final Locals front = maxLocal(this.locations);
//            Log.d("CLOSEST LOCAL", front.getName()+front.getDistance()+front.getRating()+front.getDegrees());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    name.setText(front.getName());
                    distance.setText((int)front.getDistance() + "m");
                    ratingBar.setRating(front.getRating());
                    bigarrow.setRotation(front.getDegrees());
                    description.setText(front.getDescription());
                }
            });
        }
//        direction = (float) (event.values[0]);


    }


    @Override
    public void onConnected(Bundle bundle) {

        Wearable.DataApi.addListener(mGoogleApiClient, this);
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
        sendLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("OUR GPS", "Location: " + location.getLatitude() + ", " + location.getLongitude());
        for(Locals l : locations){
            l.updateDistance(location.getLatitude(), location.getLongitude());
        }
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
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
        mGoogleApiClient.connect();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private void sendLocation() {
        ArrayList<String> latlong = new ArrayList<String>();
        latlong.add(mLastLocation.getLatitude() + "");
        latlong.add(mLastLocation.getLongitude()+"");
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/location");
        putDataMapReq.getDataMap().putStringArrayList("com.oscode.pointr.key.location", latlong);
        Calendar c = Calendar.getInstance();
        putDataMapReq.getDataMap().putInt("com.oscode.pointr.key.time", c.get(Calendar.SECOND));
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }
}
