package com.braintech.cmyco.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.braintech.cmyco.R;
import com.braintech.cmyco.adapter.CustomAdapterPollData;
import com.braintech.cmyco.common.CommonAPI;
import com.braintech.cmyco.my_interface.SnakeOnClick;
import com.braintech.cmyco.objectclasses.PollData;
import com.braintech.cmyco.objectclasses.PollOptions;
import com.braintech.cmyco.sessions.PollsPref;
import com.braintech.cmyco.utils.AlertDialogManager;
import com.braintech.cmyco.utils.Const;
import com.braintech.cmyco.utils.Fonts;
import com.braintech.cmyco.utils.JsonParser;
import com.braintech.cmyco.utils.Progress;
import com.braintech.cmyco.utils.SnackNotify;
import com.braintech.cmyco.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class GamePlayStrategyActivity extends AppCompatActivity {

    @InjectView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @InjectView(R.id.listViewPoll)
    ListView listViewPoll;

    //@InjectView(R.id.txt_playCall)
    TextView txt_playCall;

    // @InjectView(R.id.txt_active_users)
    TextView txt_active_users;

    // @InjectView(R.id.txt_team_name)
    TextView txt_team_name;

    SnakeOnClick snakeOnClick;

    CommonAPI logoutAPI;

    PollsPref pollsPref;

    String[] pollName;

    HashMap<Integer, ArrayList<PollOptions>> hashMapPollOptions;

    ArrayList<PollData> arrayListPollData;

    AlertDialogManager alertDialogManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play_strategy);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        logoutAPI = new CommonAPI(this);

        pollsPref = new PollsPref(this);

        alertDialogManager = new AlertDialogManager();

        handleSnakeRetryCall();

        manageListViewHeaderFooter();

        setFont();

    }

    private void manageListViewHeaderFooter() {


        LayoutInflater inflater = getLayoutInflater();

        //header listView
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.layout_header_gps, listViewPoll, false);
        listViewPoll.addHeaderView(header, null, false);
        txt_team_name = (TextView) header.findViewById(R.id.txt_team_name);
        txt_playCall = (TextView) header.findViewById(R.id.txt_playCall);
        txt_active_users = (TextView) header.findViewById(R.id.txt_active_users);


//        //footer listView
//        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.layout_footer_gps, listViewPoll, false);
//        listViewPoll.addFooterView(footer, null, false);
//        txt_active_users = (TextView) footer.findViewById(R.id.txt_active_users);


    }

    private void handleSnakeRetryCall() {
        snakeOnClick = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                logoutAPI.logout(snakeOnClick, coordinatorLayout);
            }
        };
    }


    @Override
    protected void onResume() {
        super.onResume();

        new GetPollData().execute();
    }

    private void getActiveUsers() {
        if (Utility.isNetworkAvailable(this)) {
            new GetActiveUsers().execute();
        } else {
            SnackNotify.showSnakeBar(this, snakeOnClick, coordinatorLayout);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            logoutAPI.logout(snakeOnClick, coordinatorLayout);
            return true;
        } else if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private void setFont() {
        Fonts.robotoRegular(this, txt_active_users);
        Fonts.robotoRegular(this, txt_team_name);
        Fonts.robotoRegular(this, txt_playCall);
    }

    private class GetPollData extends AsyncTask<String, String, String> {

        int result = -1;
        int ignorePos;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(GamePlayStrategyActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                JsonParser jsonParser = new JsonParser(GamePlayStrategyActivity.this);

                String url = Const.GET_ACTIVE_GAME_DETAIL + "?" + Const.TAG_TEAMID + "=" + pollsPref.getTeamId();
                String jsonString = jsonParser.getJSONFromUrl(url);

                JSONObject jsonObject = new JSONObject(jsonString);

                if (jsonObject != null) {

                    result = 1;

                    JSONArray jsonArrayPolLData = jsonObject.getJSONArray(Const.KEY_POLL_DATA);

                    arrayListPollData = new ArrayList<PollData>();

                    hashMapPollOptions = new HashMap<Integer, ArrayList<PollOptions>>();

                    for (int i = 0; i < jsonArrayPolLData.length(); i++) {

                        JSONObject jsonObj = jsonArrayPolLData.getJSONObject(i);
                        int poll_id = jsonObj.getInt(Const.KEY_POLL_ID);
                        if (poll_id == 8) {
                            ignorePos = i;
                        }

                        String poll_name = jsonObj.getString(Const.KEY_POLL_NAME);
                        String poll_start_time = jsonObj.getString(Const.KEY_START_TIME);
                        String poll_end_time = jsonObj.getString(Const.KEY_END_TIME);
                        String poll_duration = jsonObj.getString(Const.KEY_POLL_DURATION);

                        String maxValue = jsonObj.getString(Const.KEY_MAX);

                        String maxId = jsonObj.getString(Const.KEY_MAX_ID);

                        PollData pollData = new PollData(poll_id, poll_name, poll_end_time, poll_start_time, poll_duration, maxId, maxValue);

                        arrayListPollData.add(pollData);

                        JSONArray jsonArrayPollOptions = jsonObj.getJSONArray(Const.KEY_POLL_OPTION);

                        ArrayList<PollOptions> arrayListPollOpt = new ArrayList<PollOptions>();

                        if (jsonArrayPollOptions.length() != 0) {

                            for (int j = 0; j < jsonArrayPollOptions.length(); j++) {

                                JSONObject jsonObjOpt = jsonArrayPollOptions.getJSONObject(j);

                                Log.e("poll", jsonObjOpt.toString());

                                String pollId = jsonObjOpt.getString(Const.KEY_POLL_ID);


                                String pollName = jsonObjOpt.getString(Const.KEY_POLL_NAME);

                                PollOptions pollOptions = new PollOptions(pollId, pollName);

                                arrayListPollOpt.add(pollOptions);
                            }
                        }
                        hashMapPollOptions.put(poll_id, arrayListPollOpt);


                    }
                } else
                    result = 0;

            } catch (NullPointerException ex) {
                ex.printStackTrace();
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
                addDataToListView(ignorePos);
            } else {
                alertDialogManager.showAlertDialog(GamePlayStrategyActivity.this, getString(R.string.server_not_responding));
            }

            getActiveUsers();
        }
    }

    private void addDataToListView(int ignorePos) {
        CustomAdapterPollData customAdapterPollData = new CustomAdapterPollData(this, arrayListPollData, hashMapPollOptions, ignorePos);
        listViewPoll.setAdapter(customAdapterPollData);
    }

    //Asynchronous class to call GET_TIME_OUT API

    private class GetActiveUsers extends AsyncTask<String, String, String> {
        int result = -1;
        String msg;

        String activeUsers;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(GamePlayStrategyActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser(GamePlayStrategyActivity.this);

            String url = Const.GET_ACTIVE_USERS;
            String jsonString = jsonParser.getJSONFromUrl(url);

            //Log.e("jsonString", jsonString);

            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);

                    activeUsers = jsonObject.getString(Const.KEY_ACTIVE_USER);
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

            if (pollsPref.getTeam() != null) {
                txt_team_name.setText(pollsPref.getTeamDetail().toUpperCase());
            }
            if (result == 1) {

                txt_active_users.setText("ACTIVE USERS: " + activeUsers);

            } else if (result == 0) {
                alertDialogManager.showAlertDialog(GamePlayStrategyActivity.this, msg);
            } else {
                alertDialogManager.showAlertDialog(GamePlayStrategyActivity.this, getString(R.string.server_not_responding));
            }
        }


    }

    @OnItemClick(R.id.listViewPoll)
    void onItemClick(int position) {


        int poll_id = arrayListPollData.get(position - 1).getPoll_id();

        Log.e("pollidClick",""+poll_id);


        if (getIntent().hasExtra(Const.TAG_POLL_ID)) {
            int pollId = getIntent().getExtras().getInt(Const.TAG_POLL_ID);
            Log.e("pollidintent",""+pollId);
            if(poll_id==4 && pollId==8)
            {
                poll_id=pollId;

                Log.e("inside","com");
            }
            if (poll_id == pollId) {
                if (pollsPref.isPollActivated()) {
                    pollsPref.pollActivated(false);

                    Progress.start(this);

                    if (pollsPref.isTimePresent()) {

                        Progress.stop();
                        //do nothing screen will automatically switch
                    } else {

                        Progress.stop();
                        pollId=poll_id;
                        passIntentOnClick(position, pollId);
                    }
                }
                else
                {
                    passIntentOnClick(position, pollId);
                }
            } else {
                passIntentOnClick(position, poll_id);
            }
        } else {

            passIntentOnClick(position, poll_id);
        }
    }

    public void passIntentOnClick(int position, int poll_id) {

        if (poll_id == 7) {
            //do nothing
        } else {

            ArrayList<PollOptions> arrayListPollOpt = new ArrayList<>();

            String pollName = arrayListPollData.get(position - 1).getPoll_name();

            arrayListPollOpt = hashMapPollOptions.get(poll_id);
            Intent intent = new Intent(this, DisabledGameActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(Const.KEY_POLL_NAME, pollName);
            bundle.putSerializable(Const.TAG_POLL_OPTION, arrayListPollOpt);
            bundle.putInt(Const.KEY_POLL_ID, poll_id);

            intent.putExtras(bundle);
            startActivity(intent);
        }
    }


}
