package com.example.nitesh.assignment1_group3;



/**
 * Created by Nitesh on 30-09-2017.
 */
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;

/**
 * Created by Nitesh Gupta on 01-10-2017.
 */

public class AccelService extends Service implements SensorEventListener{


    public final IBinder lB = new LocalBinder();
    public Handler hndlr;
    private Sensor snsr;
    private SensorManager snsrMngr;
    long prevSaved;
    static int SAMPLING_FREQ = 1000;

    //private long sensorReferenceTime;

    public AccelService(){

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //To sample at frequency 1Hz, we need to find the current time minus the previous save time
        long timeDiff = (System.currentTimeMillis() - prevSaved);
        // Compare it with the sampling frequency i.e. 1HZ
        if ( timeDiff > SAMPLING_FREQ && hndlr !=null ) {
            prevSaved = System.currentTimeMillis();
            float xval = event.values[0];
            float yval = event.values[1];
            float zval = event.values[2];

            Message msg = hndlr.obtainMessage();

            Bundle bndl = new Bundle();
            bndl.putString("timestamp", String.valueOf(prevSaved));
            bndl.putFloat("x", xval);
            bndl.putFloat("y", yval);
            bndl.putFloat("z",zval);

            msg.setData(bndl);
            hndlr.sendMessage(msg);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return lB;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class LocalBinder extends Binder
    {
        public AccelService getInstance(){return AccelService.this;}
    }

    public void onCreate() {
        //Log.w("Tag: ", "on create called");
        prevSaved = System.currentTimeMillis();
        snsrMngr = (SensorManager)  getSystemService(Context.SENSOR_SERVICE);
        snsr  = snsrMngr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        snsrMngr.registerListener((SensorEventListener) this, snsr, 1000000);
        //sensorReferenceTime = System.currentTimeMillis();
    }

    public void setHandler(Handler handler){this.hndlr = handler;}
}