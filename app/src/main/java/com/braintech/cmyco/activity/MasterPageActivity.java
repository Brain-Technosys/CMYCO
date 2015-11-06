package com.braintech.cmyco.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.braintech.cmyco.R;
import com.braintech.cmyco.adapter.CustomAdapterPollData;
import com.braintech.cmyco.common.CommonAPI;
import com.braintech.cmyco.my_interface.SnakeOnClick;
import com.braintech.cmyco.objectclasses.PollData;
import com.braintech.cmyco.objectclasses.PollOptions;
import com.braintech.cmyco.sessions.PollsPref;
import com.braintech.cmyco.utils.Const;
import com.braintech.cmyco.utils.Progress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MasterPageActivity extends AppCompatActivity {

    @InjectView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @InjectView(R.id.listViewPoll)
    ListView listViewPoll;

    SnakeOnClick snakeOnClick;

    CommonAPI logoutAPI;

    PollsPref pollsPref;

    String[] pollName;

    HashMap<Integer, ArrayList<PollOptions>> hashMapPollOptions;

    ArrayList<PollData> arrayListPollData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_page);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        logoutAPI = new CommonAPI(this);

        pollsPref = new PollsPref(this);

        handleSnakeRetryCall();

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
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_continue)
    void goNext() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }


    private class GetPollData extends AsyncTask<String, String, String> {

        int result = -1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(MasterPageActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONObject jsonObject = new JSONObject(pollsPref.getPollData().toString());

                if(jsonObject!=null) {

                    result=1;

                    JSONArray jsonArrayPolLData = jsonObject.getJSONArray(Const.KEY_POLL_DATA);

                    arrayListPollData = new ArrayList<PollData>();

                    hashMapPollOptions = new HashMap<Integer, ArrayList<PollOptions>>();

                    for (int i = 0; i < jsonArrayPolLData.length(); i++) {

                        JSONObject jsonObj = jsonArrayPolLData.getJSONObject(i);
                        int poll_id = jsonObj.getInt(Const.KEY_POLL_ID);

                        String poll_name = jsonObj.getString(Const.KEY_POLL_NAME);
                        String poll_start_time = jsonObj.getString(Const.KEY_START_TIME);
                        String poll_end_time = jsonObj.getString(Const.KEY_END_TIME);
                        String poll_duration = jsonObj.getString(Const.KEY_POLL_DURATION);

                        PollData pollData = new PollData(poll_id, poll_name, poll_end_time, poll_start_time, poll_duration);

                        arrayListPollData.add(pollData);

                        JSONArray jsonArrayPollOptions = jsonObj.getJSONArray(Const.KEY_POLL_OPTION);

                        ArrayList<PollOptions> arrayListPollOpt = new ArrayList<PollOptions>();

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
                else
                    result=0;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Progress.stop();

            if(result==1)
            {
                addDataToListView();
            }
        }
    }

    private void addDataToListView()
    {
        CustomAdapterPollData customAdapterPollData=new CustomAdapterPollData(this,arrayListPollData,hashMapPollOptions);
        listViewPoll.setAdapter(customAdapterPollData);
    }
}
