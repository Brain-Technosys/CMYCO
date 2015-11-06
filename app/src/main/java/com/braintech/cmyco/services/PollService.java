package com.braintech.cmyco.services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.braintech.cmyco.R;
import com.braintech.cmyco.activity.HomeActivity;
import com.braintech.cmyco.activity.LoginActivity;
import com.braintech.cmyco.common.CommonAPI;
import com.braintech.cmyco.my_interface.SnakeOnClick;
import com.braintech.cmyco.sessions.PollsPref;
import com.braintech.cmyco.utils.AlertDialogManager;
import com.braintech.cmyco.utils.Const;
import com.braintech.cmyco.utils.JsonParser;
import com.braintech.cmyco.utils.Progress;
import com.braintech.cmyco.utils.SnackNotify;
import com.braintech.cmyco.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class PollService extends Service {

    //mBinder: An Ibinder object which shall be used by clients to interact with service
    public final IBinder mBinder = new ServiceBinder();

    Context context;


    //Class ServiceBinder: As this service is private to the application and runs
    //in the same process as the client, you should create your interface by extending
    // the Binder class and returning an instance of it from onBind().
    // The client receives the Binder and can use it to directly access public methods
    // available in either the Binder implementation or even the Service.

    public class ServiceBinder extends Binder {
        public PollService getService() {
            return PollService.this;
        }
    }


    public PollService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    CommonAPI commonAPI;

    @Override
    public void onCreate() {
        super.onCreate();

        commonAPI = new CommonAPI(getApplicationContext());

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("Service", "Started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("Service", "Destroyed");
    }


    public void getPollData(Context context) {

        Toast.makeText(getApplicationContext(), "Poll Service", Toast.LENGTH_LONG);
        this.context = context;
        if (Utility.isNetworkAvailable(getApplicationContext())) {
            new PollData().execute();
        } else {
//            if (LoginActivity.class.isInstance(getApplicationContext()))
//                SnackNotify.showSnakeBar((Activity) context, snakeOnClick, coordinatorLayout);
        }
    }


    private class PollData extends AsyncTask<String, String, String> {
        int result = -1;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(getApplicationContext());
        }

        @Override
        protected String doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser(context);

            String url = Const.GET_ACTIVE_GAME_DETAIL;
            String jsonString = jsonParser.getJSONFromUrl(url);

            Log.e("jsonString", jsonString);

            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                    if (result == 1) {
                        JSONObject jsonObjectPollData = jsonObject.getJSONObject(Const.KEY_POLL_DATA);
                        PollsPref pollsPref = new PollsPref(context);
                        pollsPref.storePollData(jsonObjectPollData.toString());

                    }

                } else {
                    msg = jsonObject.getString(Const.KEY_MSG);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Progress.stop();

            if (result == 1) {

                //Checking Instance of Login Activity, and go to home after successful login
//                if (LoginActivity.class.isInstance(context)) {
//                    Intent intent = new Intent(context, HomeActivity.class);
//                    context.startActivity(intent);
//                    LoginActivity.loginActivity.finish();
//                } else {
//                    //do something
//                }

            } else if (result == 0) {

                //alertDialogManager.showAlertDialog(context, msg);
            } else {
                //alertDialogManager.showAlertDialog(context, context.getString(R.string.server_not_responding));
            }
        }
    }


}
