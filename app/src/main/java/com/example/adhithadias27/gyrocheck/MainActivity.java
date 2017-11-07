package com.example.adhithadias27.gyrocheck;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity{

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private Sensor accelerometerSensor;
    private SensorEventListener sensorEventListener;
    File dir;
    String filepath;
    String filename;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(gyroscopeSensor == null & accelerometerSensor == null){
            Toast.makeText(this, "No Gyroscope and Accelerometer!", Toast.LENGTH_SHORT).show();
            finish();
        } else if (gyroscopeSensor == null){
            Toast.makeText(this, "The device has no Gyroscope!", Toast.LENGTH_SHORT).show();
            finish();
        }else if (accelerometerSensor == null){
            Toast.makeText(this, "The device has no Accelerometer!", Toast.LENGTH_SHORT).show();
            finish();
        }

        filepath = Environment.getExternalStorageDirectory().getPath() + "/GyroApp/logdata";
        Log.v("Path", filepath);

        dir = new File(filepath );
        if (!dir.exists()) {
            dir.mkdirs();
            Toast.makeText(getBaseContext(), "Directory created", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getBaseContext(), "Directory exists", Toast.LENGTH_LONG).show();
        }

        String entry = String.format("TIMESTAMP,SENSORTYPE,X_VAL,Y_VAL,Z_VAL,,\n");
        File file = new File(dir, "output.csv");
        writeDataToFile(file, false, entry);

        sensorEventListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent sensorEvent){

                // Axis of the rotation sample, not normalized yet.
                float axisX = sensorEvent.values[0];
                float axisY = sensorEvent.values[1];
                float axisZ = sensorEvent.values[2];

                displayGyroXValue(axisX);
                displayGyroYValue(axisY);
                displayGyroZValue(axisZ);

                String entry = "";

                switch(sensorEvent.sensor.getType()) {
                    case Sensor.TYPE_GYROSCOPE:
                        displayGyroXValue(axisX);
                        displayGyroYValue(axisY);
                        displayGyroZValue(axisZ);
                        entry = String.format("%d,GYRO,%f,%f,%f,%f,%f\n", sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], 0.f, 0.f);
                        break;
                    case Sensor.TYPE_ACCELEROMETER:
                        displayAccelXValue(axisX);
                        displayAccelYValue(axisY);
                        displayAccelZValue(axisZ);
                        entry = String.format("%d,ACC,%f,%f,%f,%f,%f\n", sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], 0.f, 0.f);
                        break;
                }

                File file = new File(dir, "output.csv");
                writeDataToFile(file, true, entry);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i){

            }
        };

    }

    /**
     * Write the sensor data entry to the specified file
     * @param file
     * @param append
     * @param entry
     */
    void writeDataToFile(File file, boolean append, String entry){
        try {
            FileOutputStream f = new FileOutputStream(file, append);
            f.write(entry.getBytes());
            f.flush();
            f.close();
            Log.v("Path", filepath);
            Toast.makeText(getBaseContext(), "Data saved", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, gyroscopeSensor, 100000);
        sensorManager.registerListener(sensorEventListener, accelerometerSensor, 100000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    /**
     * Displays the Gyroscope x value
     * @param val
     */
    public void displayGyroXValue(float val){
        TextView xValueView = (TextView) findViewById(R.id.gyroscope_x_value);
        xValueView.setText(String.valueOf(val));
    }

    /**
     * Displays the Gyroscope y value
     * @param val
     */
    public void displayGyroYValue(float val){
        TextView yValueView = (TextView) findViewById(R.id.gyroscope_y_value);
        yValueView.setText(String.valueOf(val));
    }

    /**
     * Displays the Gyroscope z value
     * @param val
     */
    public void displayGyroZValue(float val){
        TextView zValueView = (TextView) findViewById(R.id.gyroscope_z_value);
        zValueView.setText(String.valueOf(val));
    }

    /**
     * Displays the Accelerometer x value
     * @param val
     */
    public void displayAccelXValue(float val){
        TextView xValueView = (TextView) findViewById(R.id.accelerometer_x_value);
        xValueView.setText(String.valueOf(val));
    }

    /**
     * Displays the Accelerometer x value
     * @param val
     */
    public void displayAccelYValue(float val){
        TextView yValueView = (TextView) findViewById(R.id.accelerometer_y_value);
        yValueView.setText(String.valueOf(val));
    }

    /**
     * Displays the Accelerometer x value
     * @param val
     */
    public void displayAccelZValue(float val){
        TextView zValueView = (TextView) findViewById(R.id.accelerometer_z_value);
        zValueView.setText(String.valueOf(val));
    }

    public String getStorageDir() {
        return this.getExternalFilesDir(null).getAbsolutePath();
    }

//    private void checkPermissions() {
//        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
//            return;
//        }
//        Toast.makeText(getBaseContext(), "Permission is already granted", Toast.LENGTH_LONG).show();
//    }

//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_CODE_ASK_PERMISSIONS:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission Granted
//                    Toast.makeText(getBaseContext(), "Permission Granted", Toast.LENGTH_LONG).show();
//                } else {
//                    // Permission Denied
//                    Toast.makeText(this, "WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
