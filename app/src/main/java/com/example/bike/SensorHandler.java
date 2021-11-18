package com.example.bike;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.DecimalFormat;

public class SensorHandler implements SensorEventListener {

    SensorManager sensorManager;
    Sensor rot;

    Handler timerHandler;
    Runnable timerRunnable;

    Boolean isCalibrated=false;
    int mode=0;//0=manual,1=manual-fire,2=auto,3=auto-fire
    int auto=0;

    DatagramSocket datagramSocket;

    DecimalFormat f = new DecimalFormat("0");
    float[] matrix = new float[9];
    float[] angles = new float[3];
    float offsetYaw;
    float offsetPitch;
    String[] deg = new String[3];
    String ip;
    MainActivity activity;

    public SensorHandler(MainActivity activity, String ip) {
        this.ip=ip;
        this.activity = activity;
        try {
            datagramSocket = new DatagramSocket(8888);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        sensorManager = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
        rot = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this,rot,10000);
        timerHandler = new Handler();
        timerRunnable = new Runnable() {

            @Override
            public void run() {
                float[] degrees=getDegrees(angles);
                if(!isCalibrated){
                    offsetYaw=degrees[0]-90;
                    offsetPitch=degrees[2]-(70);
                    isCalibrated=true;
                }
                degrees[0]=degrees[0]-offsetYaw;
                degrees[2]=degrees[2]-offsetPitch;
                for(int i=0;i<degrees.length;i++) {
                    deg[i] = f.format(degrees[i]);
                }
                Log.v("x = ",deg[0]);
                Log.v("y = ",deg[1]);
                Log.v("z = ",deg[2]);
                MessageSender messageSender = new MessageSender(datagramSocket,ip);
                messageSender.execute(deg[1]);
                timerHandler.postDelayed(this, 5);
            }
        };
    }

    public void run(){
        timerHandler.postDelayed(timerRunnable,0);
    }
    public void stop(){
        timerHandler.removeCallbacks(timerRunnable);
    }
    public void fire(Boolean f){
        if(f){
            mode=1+auto;
        }else{
            mode=0+auto;
        }
        //Toast.makeText(activity,mode+"",Toast.LENGTH_SHORT).show();
    }
    public void auto(Boolean a){
        if(a){
            auto=0;
        }else{
            auto=2;
        }
    }
    @Override
    public void onSensorChanged(SensorEvent e) {
        if (e.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(matrix,e.values);
            SensorManager.getOrientation(matrix,angles);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private float[] getDegrees(float[] angles){
        float[] degrees= new float[3];
        for(int i=0;i<angles.length;i++) {
            degrees[i]= 180f + (angles[i] * 57.296f);
        }
        return degrees;
    }
    private float limiter(float a){
        if(a<0)return 0;
        else if(a>180)return 180;
        else return a;
    }
}
