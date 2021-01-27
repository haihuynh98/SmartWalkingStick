package com.example.myapplication;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.VibrationEffect;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.os.Vibrator;
public class MainActivity extends Activity {
    TextView textInfo;
    Button btnCheck;
    HidBridge hidBridge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCheck = (Button) findViewById(R.id.check);
        textInfo = (TextView) findViewById(R.id.info);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


               checkInfo();
            }
        });
    }
    private void checkInfo() {
//        Vibrator v = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//        } else {
//            //deprecated in API 26
//            v.vibrate(500);
//        }

        HidBridge hidBridge = new HidBridge(this,390,1112,textInfo);
       // hidBridge = new HidBridge(this,22352,1155,textInfo);
        hidBridge.OpenDevice();
        hidBridge.StartReadingThread();
    }
}