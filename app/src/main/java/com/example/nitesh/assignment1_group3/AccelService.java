package com.example.nitesh.assignment1_group3;



/**
 * Created by Nitesh on 30-09-2017.
 */

        import android.app.Service;
        import android.content.Context;
        import android.content.Intent;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.Binder;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.IBinder;
        import android.os.Message;
        import android.telephony.SmsManager;
        import android.widget.Toast;

public class AccelService extends Service implements SensorEventListener{

    private SensorManager accelManage;
    private Sensor senseAccel;
    public Handler handler;
    float accelValuesX[] = new float[128];
    float accelValuesY[] = new float[128];
    float accelValuesZ[] = new float[128];
    private String tableName = null;

    int index = 0;
    int k=0;
    Bundle b;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // TODO Auto-generated method stub
        Sensor mySensor = sensorEvent.sensor;
        System.out.println("Sending message outside if...."+handler);
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER && handler!=null) {
            Bundle b = new Bundle();
            Message msg = handler.obtainMessage();
            b.putFloat("x", sensorEvent.values[0]);
            b.putFloat("y", sensorEvent.values[1]);
            b.putFloat("z", sensorEvent.values[2]);
            msg.setData(b);
            System.out.println("Sending message inside if....");
            handler.sendMessage(msg);
        }
    }

    public void callFallRecognition(){
        float prev = 0;
        float curr = 0;
        prev = 10;
        for(int i=11;i<128;i++){
            curr = accelValuesZ[i];
            if(Math.abs(prev - curr) > 10 ){
                Toast.makeText(this, "Fall detected", Toast.LENGTH_LONG).show();
                sendSMS();
            }

        }


    }

    public void callGestureRecognition(){
        float avgX = 0;
        float avgY = 0;
        float avgZ = 0;
        for(int i=0;i<128;i++){
            avgX = avgX + accelValuesX[i];
            avgY = avgY + accelValuesY[i];
            avgZ = avgZ + accelValuesZ[i];

        }
        avgX = avgX/128;
        avgY = avgY/128;
        avgZ = avgZ/128;

        boolean left = true;

        int zeroCrossingX = 0;
        if(accelValuesX[0] >= avgX){
            left = true;
            for(int i=0;i<128;i+=8){
                if(left){
                    if(accelValuesX[i] < avgX){
                        zeroCrossingX++;
                        left = false;
                    }
                }else{
                    if(accelValuesX[i] >= avgX){
                        zeroCrossingX++;
                        left = true;
                    }
                }
            }
        }else{
            left = false;
            for(int i=0;i<128;i+=8){
                if(left){
                    if(accelValuesX[i] < avgX){
                        zeroCrossingX++;
                        left = false;
                    }
                }else{
                    if(accelValuesX[i] >= avgX){
                        zeroCrossingX++;
                        left = true;
                    }
                }
            }
        }

        int zeroCrossingY = 0;
        if(accelValuesY[0] >= avgY){
            left = true;
            for(int i=0;i<128;i+=8){
                if(left){
                    if(accelValuesY[i] < avgY){
                        zeroCrossingY++;
                        left = false;
                    }
                }else{
                    if(accelValuesY[i] >= avgY){
                        zeroCrossingY++;
                        left = true;
                    }
                }
            }
        }else{
            left = false;
            for(int i=0;i<128;i+=8){
                if(left){
                    if(accelValuesY[i] < avgY){
                        zeroCrossingY++;
                        left = false;
                    }
                }else{
                    if(accelValuesY[i] >= avgY){
                        zeroCrossingY++;
                        left = true;
                    }
                }
            }
        }

        int zeroCrossingZ = 0;
        if(accelValuesZ[0] >= avgZ){
            left = true;
            for(int i=0;i<128;i+=8){
                if(left){
                    if(accelValuesZ[i] < avgZ){
                        zeroCrossingZ++;
                        left = false;
                    }
                }else{
                    if(accelValuesZ[i] >= avgZ){
                        zeroCrossingZ++;
                        left = true;
                    }
                }
            }
        }else{
            left = false;
            for(int i=0;i<128;i+=8){
                if(left){
                    if(accelValuesZ[i] < avgZ){
                        zeroCrossingZ++;
                        left = false;
                    }
                }else{
                    if(accelValuesZ[i] >= avgZ){
                        zeroCrossingZ++;
                        left = true;
                    }
                }
            }
        }

        if(zeroCrossingX > 7 || zeroCrossingY > 7 || zeroCrossingZ > 7){

            Toast.makeText(this, "Hi gesture", Toast.LENGTH_LONG).show();
            if(k == 0){
                sendSMS();
            }
            zeroCrossingX = 0;
            zeroCrossingY = 0;
            zeroCrossingZ = 0;
            k++;

        }


    }

    public void sendSMS() {

        String phoneNumber = b.getString("phone");
        Toast.makeText(AccelService.this, phoneNumber, Toast.LENGTH_LONG).show();
        String message = "Fall detected";

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onCreate(){
        System.out.println("\n\nOncreate of accelerometer....\n");
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        accelManage = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senseAccel = accelManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManage.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);


    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("\n\nOn START of accelerometer....\n");
        b = intent.getExtras();
        String newTableName = b.getString("tableName");
        Toast.makeText(AccelService.this, newTableName, Toast.LENGTH_LONG).show();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        //k = 0;*/
        this.tableName = newTableName;
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub

        return null;
    }

    public class LocalBinder extends Binder {
        public AccelService getInstance(){
            //System.out.println("\n\n\nReturning accelerometer service\n\n\n");
            return AccelService.this;
        }
    }

    public void setHandler(Handler handler){
        System.out.println("\n\nSetting handler"+handler+"\n\n");
        this.handler = handler;
    }

}

