package com.example.coldnew.hellothings;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "HelloThings";
    private static final String LED = "BCM21";
    private static final int INTERVAL_BETWEEN_BLINKS_MS = 1000;

    private Handler mHandler = new Handler();
    private Gpio mLedGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PeripheralManagerService service = new PeripheralManagerService();
        Log.d(TAG, "Available GPIOs: " + service.getGpioList());

        try {
            mLedGpio = service.openGpio(LED);
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio.setValue(true);
            Log.i(TAG, "Start blinking LED by GPIO21");
              mHandler.post(mBlinkRunnable);
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }

    private Runnable mBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLedGpio == null) {
                return;
            }
            try {
                mLedGpio.setValue(!mLedGpio.getValue());  // Toggle the GPIO state
                Log.d(TAG, "GPIO21 set to " + mLedGpio.getValue());
                mHandler.postDelayed(mBlinkRunnable, INTERVAL_BETWEEN_BLINKS_MS);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mBlinkRunnable); // <---- Add this
        Log.i(TAG, "Closing LED GPIO21 pin");
        try {
            mLedGpio.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        } finally {
            mLedGpio = null;
        }
    }

}
