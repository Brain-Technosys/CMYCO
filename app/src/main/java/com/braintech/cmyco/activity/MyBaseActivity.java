package com.braintech.cmyco.activity;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Braintech on 11/26/2015.
 */
public class MyBaseActivity extends AppCompatActivity {


        public static final long DISCONNECT_TIMEOUT = 10000; // 5 min = 5 * 60 * 1000 ms

        private Handler disconnectHandler = new Handler(){
            public void handleMessage(Message msg) {
            }
        };

        private Runnable disconnectCallback = new Runnable() {
            @Override
            public void run() {

                Log.e("timmer","running");
                // Perform any required operation on disconnect
            }
        };

        public void resetDisconnectTimer(){
            disconnectHandler.removeCallbacks(disconnectCallback);
            disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
        }

        public void stopDisconnectTimer(){
            disconnectHandler.removeCallbacks(disconnectCallback);
        }

        @Override
        public void onUserInteraction(){
            resetDisconnectTimer();

            Log.e("user", "interaction");
        }

        @Override
        public void onResume() {
            super.onResume();
            resetDisconnectTimer();
        }

        @Override
        public void onStop() {
            super.onStop();
            stopDisconnectTimer();

            Log.e("on","stop");
        }
}



