package com.braintech.cmyco.services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.braintech.cmyco.R;
import com.braintech.cmyco.activity.GameActivity;
import com.braintech.cmyco.activity.HomeActivity;
import com.braintech.cmyco.activity.LoginActivity;
import com.braintech.cmyco.common.CommonAPI;
import com.braintech.cmyco.my_interface.SnakeOnClick;
import com.braintech.cmyco.objectclasses.PollData;
import com.braintech.cmyco.objectclasses.PollOptions;
import com.braintech.cmyco.sessions.PollsPref;
import com.braintech.cmyco.utils.AlertDialogManager;
import com.braintech.cmyco.utils.Const;
import com.braintech.cmyco.utils.JsonParser;
import com.braintech.cmyco.utils.Progress;
import com.braintech.cmyco.utils.SnackNotify;
import com.braintech.cmyco.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
            new GetPollData().execute();
        } else {
//            if (LoginActivity.class.isInstance(getApplicationContext()))
//                SnackNotify.showSnakeBar((Activity) context, snakeOnClick, coordinatorLayout);
        }
    }


    private class GetPollData extends AsyncTask<String, String, String> {
        int result = -1;

        int burgerTime = 1;
        String poll_name;

        String msg;

        int poll_id;

        ArrayList<PollOptions> arrayListPollOpt;
        ArrayList<PollData> arrayListPollData;
        HashMap<Integer, ArrayList<PollOptions>> hashMapPollOptions;

        String serverTime;

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

                    serverTime = jsonObject.getString(Const.KEY_POLL_SERVER_TIME);


//                    serverTime = serverTime.replace(" | ", " ");
//                    serverTime = serverTime.replace("am", "");
//                    serverTime = serverTime.replace("pm", "");

                    // Log.e("serverTime", (Utility.getNYTime()));
                    Log.e("serverTime", serverTime);
                    Log.e("getTime", Utility.getTime());

                    String sTime = "2015-11-09 03:30:06";
                    String eTime = "2015-11-09 04:30:06";

//                    burgerTime = Utility.compareTimes(sTime, eTime);

                    Log.d("burgerTime", String.valueOf(burgerTime));

                    // serverTime=jsonObject.getString(Const.)


                    JSONArray jsonArrayPolLData = jsonObject.getJSONArray(Const.KEY_POLL_DATA);

                    arrayListPollData = new ArrayList<PollData>();

                    hashMapPollOptions = new HashMap<Integer, ArrayList<PollOptions>>();

                    for (int i = 0; i < 1; i++) {

                        JSONObject jsonObj = jsonArrayPolLData.getJSONObject(i);
                        poll_id = jsonObj.getInt(Const.KEY_POLL_ID);

                        //  Utility.getDifferenceTimeZone();

                         poll_name = jsonObj.getString(Const.KEY_POLL_NAME);
                        String poll_start_time = jsonObj.getString(Const.KEY_START_TIME);
                        String poll_end_time = jsonObj.getString(Const.KEY_END_TIME);
                        String poll_duration = jsonObj.getString(Const.KEY_POLL_DURATION);

                        PollData pollData = new PollData(poll_id, poll_name, poll_end_time, poll_start_time, poll_duration);

                        arrayListPollData.add(pollData);

                        JSONArray jsonArrayPollOptions = jsonObj.getJSONArray(Const.KEY_POLL_OPTION);

                        arrayListPollOpt = new ArrayList<PollOptions>();

                        for (int j = 0; j < jsonArrayPollOptions.length(); j++) {

                            JSONObject jsonObjOpt = jsonArrayPollOptions.getJSONObject(j);

                            String pollId = jsonObjOpt.getString(Const.KEY_POLL_ID);

                            String pollName = jsonObjOpt.getString(Const.KEY_POLL_NAME);

                            PollOptions pollOptions = new PollOptions(pollId, pollName);

                            arrayListPollOpt.add(pollOptions);
                        }

                        hashMapPollOptions.put(poll_id, arrayListPollOpt);


                    }
                } else
                    result = 0;

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
                //  addDataToListView();
                if (burgerTime == 1) {

                    PollsPref pollsPref = new PollsPref(context);

                    final Handler handler = new Handler();
                    if (!pollsPref.getPlayOption().equals(poll_id)) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                Intent intent = new Intent(context, GameActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString(Const.KEY_POLL_NAME, poll_name);
                                bundle.putInt(Const.KEY_POLL_ID,poll_id);
                                bundle.putSerializable(Const.TAG_POLL_OPTION, arrayListPollOpt);
                                intent.putExtras(bundle);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                v.vibrate(500);
//

//                                handler.postDelayed(this, 20000);
                            }
                        }, 10000);
                    } else {
                        //do nothng
                    }


//                    Intent intent = new Intent(context, GameActivity.class);
//                    intent.putExtra(Const.TAG_POLL_OPTION, arrayListPollOpt);
//                    startActivity(intent);
                }

            }
        }
    }
}
