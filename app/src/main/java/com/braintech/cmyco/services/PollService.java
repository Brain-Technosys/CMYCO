package com.braintech.cmyco.services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.widget.Toast;

import com.braintech.cmyco.R;
import com.braintech.cmyco.activity.GameActivity;
import com.braintech.cmyco.activity.GamePlayStrategyActivity;
import com.braintech.cmyco.activity.HomeActivity;
import com.braintech.cmyco.activity.LoginActivity;
import com.braintech.cmyco.activity.NoGameActivity;
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
import com.braintech.cmyco.utils.TimeCheck;
import com.braintech.cmyco.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class PollService extends Service {

    //mBinder: An Ibinder object which shall be used by clients to interact with service
    public final IBinder mBinder = new ServiceBinder();

    Context context;

    PollsPref pollsPref;

    Boolean isButtonClicked;


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

//        throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    CommonAPI commonAPI;

    @Override
    public void onCreate() {
        super.onCreate();

        commonAPI = new CommonAPI(getApplicationContext());

        pollsPref = new PollsPref(this);

        pollsPref.pollActivated(false);


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    public void getPollData(Context context) {

      //  Toast.makeText(getApplicationContext(), "Poll Service", Toast.LENGTH_LONG);
        this.context = context;
        if (Utility.isNetworkAvailable(getApplicationContext())) {
            new GetPollData().execute();
        } else {
        }
    }


    private class GetPollData extends AsyncTask<String, String, String> {
        int result = -1;

        String poll_end_time;
        String poll_start_time;


        String msg;

        String serverTime;
        String timeZone;


        ArrayList<PollOptions> arrayListPollOpt;
        ArrayList<PollData> arrayListPollData;
        HashMap<Integer, ArrayList<PollOptions>> hashMapPollOptions;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(getApplicationContext());
        }

        @Override
        protected String doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser(context);

            String url = Const.GET_ACTIVE_GAME_DETAIL + "?" + Const.TAG_TEAMID + "=" + pollsPref.getTeamId();
           Log.e("URL", url);
            String jsonString = jsonParser.getJSONFromUrl(url);

            try {
                JSONObject jsonObject = new JSONObject(jsonString);


                if (jsonObject != null) {

                    result = jsonObject.getInt(Const.KEY_RESULT);

                    if (result == 1) {

                        serverTime = jsonObject.getString(Const.KEY_POLL_SERVER_TIME);
                        timeZone = jsonObject.getString(Const.KEY_TIME_ZONE);


                        JSONArray jsonArrayPolLData = jsonObject.getJSONArray(Const.KEY_POLL_DATA);

                        arrayListPollData = new ArrayList<PollData>();

                        hashMapPollOptions = new HashMap<Integer, ArrayList<PollOptions>>();

                        for (int i = 0; i < jsonArrayPolLData.length(); i++) {

                            JSONObject jsonObj = jsonArrayPolLData.getJSONObject(i);

                            int poll_id = jsonObj.getInt(Const.KEY_POLL_ID);

                            String poll_name = jsonObj.getString(Const.KEY_POLL_NAME);

                            String startTime = jsonObj.getString(Const.KEY_START_TIME);

                            String endTime = jsonObj.getString(Const.KEY_END_TIME);

                            String maxValue = jsonObj.getString(Const.KEY_MAX);

                            String maxId = jsonObj.getString(Const.KEY_MAX_ID);


                            if (!startTime.equals("null")) {
                                poll_start_time = Utility.getCurrentTime(startTime, timeZone);
                            }

                            if (!endTime.equals("null")) {
                                poll_end_time = Utility.getCurrentTime(endTime, timeZone);
                            }

                            String poll_duration = jsonObj.getString(Const.KEY_POLL_DURATION);

                            PollData pollData = new PollData(poll_id, poll_name, poll_start_time, poll_end_time, poll_duration, maxId, maxValue);

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
                    }
                } else
                    result = 0;

            } catch (NullPointerException e) {
                e.printStackTrace();
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

                String server_time = Utility.convertDateFormat(serverTime);

                String currentTime = Utility.getCurrentTime(server_time, timeZone);

                for (int i = 0; i < arrayListPollData.size(); i++) {
                    String startTime = arrayListPollData.get(i).getPoll_start_time();
                    String endTime = arrayListPollData.get(i).getPoll_end_time();

                    int poll_id = arrayListPollData.get(i).getPoll_id();

                    String poll_name = arrayListPollData.get(i).getPoll_name();

                    /*if (startTime != null) {

                        try {
                            if (TimeCheck.isTimeBetweenTwoTime(startTime, endTime, currentTime)) {


                                pollsPref.saveTimePresent(true);

                                if (pollsPref.getPollId() == poll_id) {
                                    isButtonClicked = pollsPref.isButtonClicked();

                                } else {
                                    isButtonClicked = false;

                                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                    v.vibrate(500);

                                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                    mp.start();
                                }

                                pollsPref.pollActivated(true);

                                arrayListPollOpt.clear();

                                long poll_duration = Utility.findTimeDifference(endTime, currentTime);

                                arrayListPollOpt = hashMapPollOptions.get(poll_id);

                                if (poll_id == 4) {
                                    poll_name = "Substitution Player In";
                                } else if (poll_id == 8) {

                                    poll_name = "Substitution Player Out";
                                }
                                Intent intent = new Intent(context, GameActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString(Const.KEY_POLL_NAME, poll_name);
                                bundle.putInt(Const.KEY_POLL_ID, poll_id);
                                bundle.putLong(Const.KEY_POLL_DURATION, poll_duration);
                                bundle.putSerializable(Const.TAG_POLL_OPTION, arrayListPollOpt);
                                bundle.putBoolean(Const.KEY_BUTTON_CLICKED, isButtonClicked);
                                intent.putExtras(bundle);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                if (pollsPref.isActivityRunning())
                                    GamePlayStrategyActivity.gamePlayStrategyActivity.finish();

                                startActivity(intent);

                                break;
                            }
                            else
                              System.out.println("Given time doesn't lies between two times");
                        } catch (ParseException ex) {

                            ex.printStackTrace();
                        }
                    }*/

                }

            } else if (result == 0) {
                Intent intent = new Intent(context, NoGameActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                pollsPref.clearPollData();
                ((Activity) context).finish();

            } else {
              //  Toast.makeText(context, getString(R.string.server_not_responding), Toast.LENGTH_LONG).show();
            }
        }
    }


}
