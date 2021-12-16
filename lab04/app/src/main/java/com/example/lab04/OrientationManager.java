package com.example.lab04;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.SyncStateContract;

public class OrientationManager implements SensorEventListener {
    private SensorManager manager;
    private Sensor accelerometer;
    private Sensor magnometer;
    private float[] accelOutput;
    private float[] magOutput;
    private float[] orientation = new float[9];

    public float[] getOrientation() {
        return orientation;
    }
    private float[] startOrientation = null;
    public float[] getStartOrientation(){
        return startOrientation;
    }
    public void newGame(){
        startOrientation = null;
    }
    public OrientationManager(){
        manager = (SensorManager) Constants.CURRENT_CONTEXT.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnometer = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void register(){
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_GAME);
    }
    public void pause(){
        manager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelOutput = sensorEvent.values;
        }
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            magOutput = sensorEvent.values;
        }
        if (accelOutput != null && magOutput != null){
            float[] R = new float[9];
            float[] I = new float[9];
            boolean status = SensorManager.getRotationMatrix(R, I, accelOutput, magOutput);
            if (status){
                SensorManager.getOrientation(R, orientation);
                if (startOrientation == null){
                    startOrientation = new float[orientation.length];
                    System.arraycopy(orientation, 0, startOrientation, 0, orientation.length);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
