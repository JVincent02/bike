package com.example.bike;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    SensorHandler sensorHandler;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.startBtn);
        button.setOnClickListener(this);
        sensorHandler = new SensorHandler(this,"192.168.1.4");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startBtn:
                if (button.getText().equals("Connect")) {
                    sensorHandler.run();
                    button.setText("Disconnect");
                } else if (button.getText().equals("Disconnect")) {
                    sensorHandler.stop();
                    button.setText("Connect");
                }
        }
    }
}