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
    private SensorEventListener gyroscopeEventListener;
    File dir;
    String filepath;

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

        if (gyroscopeSensor == null){
            Toast.makeText(this, "The device has no Gyroscope!", Toast.LENGTH_SHORT).show();
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

        gyroscopeEventListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent sensorEvent){

                final float EPSILON = 0.02f;

                // Axis of the rotation sample, not normalized yet.
                float axisX = sensorEvent.values[0];
                float axisY = sensorEvent.values[1];
                float axisZ = sensorEvent.values[2];

                displayXValue(axisX);
                displayYValue(axisY);
                displayZValue(axisZ);

                // Calculate the angular speed of the sample
                float omegaMagnitude = (float) Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

                // Normalize the rotation vector if it's big enough to get the axis
                // (that is, EPSILON should represent your maximum allowable margin of error)
                if (omegaMagnitude > EPSILON) {
                    axisX /= omegaMagnitude;
                    axisY /= omegaMagnitude;
                    axisZ /= omegaMagnitude;
                }

/*                if(sensorEvent.values[2] > 0.5f){
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                }else if(sensorEvent.values[2] < -0.5f){
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                }*/

                String entry = String.valueOf(axisX) + "," + String.valueOf(axisY) + "," + String.valueOf(axisZ) + "\n";

                /*
                File dir = new File(getStorageDir());
                Log.v("Path", getStorageDir());

                if (!dir.exists()) {
                    // make directory if not available
                    Boolean dirsMade = dir.mkdir();
                    Log.v("Gyro", dirsMade.toString());         // log if make directory operation was successful or not
                }

                File file = new File(dir, "output.csv");
                try {
                    FileOutputStream f = new FileOutputStream(file, true);
                    f.write(entry.getBytes());
                    f.flush();
                    f.close();
                    // Toast.makeText(getBaseContext(), "Data saved", Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
*/

                File file = new File(dir, "output.csv");

                try {
                    FileOutputStream f = new FileOutputStream(file, true);
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

//                FileOutputStream outputStream;
//                File dir = new File(getStorageDir());
//
//                Log.v("Path", getStorageDir());
//
//                if (!dir.exists()) {
//                    // make directory if not available
//                    Boolean dirsMade = dir.mkdir();
//                    Log.v("Gyro", dirsMade.toString());         // log if make directory operation was successful or not
//                }
//
//                File file = new File(dir, "output.csv");
//
//                try {
//                    outputStream = openFileOutput(file, Context.MODE_PRIVATE);
//                    outputStream.write(entry.getBytes());
//                    outputStream.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i){

            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, 100000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroscopeEventListener);
    }

    public void displayXValue(float val){
        TextView xValueView = (TextView) findViewById(R.id.x_value);
        xValueView.setText(String.valueOf(val));
    }

    public void displayYValue(float val){
        TextView yValueView = (TextView) findViewById(R.id.y_value);
        yValueView.setText(String.valueOf(val));
    }

    public void displayZValue(float val){
        TextView zValueView = (TextView) findViewById(R.id.z_value);
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
