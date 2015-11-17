package com.braintech.cmyco.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.braintech.cmyco.R;
import com.braintech.cmyco.common.CommonAPI;
import com.braintech.cmyco.my_interface.SnakeOnClick;
import com.braintech.cmyco.objectclasses.PollOptions;
import com.braintech.cmyco.sessions.PollsPref;
import com.braintech.cmyco.sessions.UserSession;
import com.braintech.cmyco.utils.AlertDialogManager;
import com.braintech.cmyco.utils.Const;
import com.braintech.cmyco.utils.JsonParser;
import com.braintech.cmyco.utils.Progress;
import com.braintech.cmyco.utils.SnackNotify;
import com.braintech.cmyco.utils.Utility;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GameActivity extends AppCompatActivity {

    @InjectView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.lbl_defence)
    TextView defenceTextView;

    @InjectView(R.id.rg_defence)
    RadioGroup defenceRadioGroup;

    @InjectView(R.id.chart)
    BarChart chart;

    @InjectView(R.id.txtViewTimer)
    TextView txtViewTimer;

    @InjectView(R.id.ll_cat_no)
    LinearLayout ll_cat_no;

    @InjectView(R.id.txtPlayCall)
    TextView txtPlayCall;

    RadioButton[] catRadioButtons;

    UserSession userSession;
    PollsPref pollsPref;

    ArrayList<PollOptions> catDefenceArrayList;
    ArrayList<String> graphLabelXAxis;
    ArrayList<BarEntry> valueSet;

    String[] xTitle = {"1", "2", "3", "4", "5"};
    String[] barDataStrings = {"750", "600", "300", "450", "500"};

    String txtLogo;

    int pollId;
    int tag; //for rating
    int maxY = 1000;

    boolean firstTime = true;

    String graphItemColor = "#14DDF9";
    String graphBgColor = "#010f1a";

    String pollName;

    long pollDuration;

    SnakeOnClick snakeOnClickForLogout;
    SnakeOnClick snakeOnClickGetGraph;
    SnakeOnClick snakeOnClickDoRating;

    CommonAPI logoutAPI;

    boolean TIMER = true;

    AlertDialogManager alertDialogManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ButterKnife.inject(this);

        handleToolbar();
        alertDialogManager = new AlertDialogManager();

        catDefenceArrayList = new ArrayList<>();

        logoutAPI = new CommonAPI(this);

        userSession = new UserSession(this);
        pollsPref = new PollsPref(this);

        alertDialogManager = new AlertDialogManager();
        //

       /* if (pollsPref.getCoachDetail().equals(null)) {
            alertDialogManager.showAlertDialog(this, getString(R.string.alert_no_data));
        } else {
            new GetDefenceDataForRadioButton().execute();
        }*/

        if (getIntent().hasExtra(Const.TAG_POLL_OPTION)) {
            Bundle bundle = getIntent().getExtras();
            catDefenceArrayList = (ArrayList<PollOptions>) bundle.getSerializable(Const.TAG_POLL_OPTION);

            pollId = bundle.getInt(Const.KEY_POLL_ID);
            pollName = bundle.getString(Const.KEY_POLL_NAME, pollName);
            pollDuration = bundle.getLong(Const.KEY_POLL_DURATION);
            defenceTextView.setText(pollName);

            setDefenceCat();
        }

        new CountDownTimer(pollDuration, 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

            public void onTick(long millisUntilFinished) {
                txtViewTimer.setText("Time Left: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext

                // if(txtViewTimer.getText().)


            }

            public void onFinish() {

                TIMER = false;

                disableRadioButtons();
            }
        }
                .start();

        //Preparing data for graph
        getGraphData(xTitle, barDataStrings);

        handleLogoutRetry();
        handleRatingRetry();
        handleGraphRetry();

        // here we are Showing graph
        handleGraph(maxY);
    }


    private void handleLogoutRetry() {
        snakeOnClickForLogout = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                logoutAPI.logout(snakeOnClickForLogout, coordinatorLayout);
            }
        };

    }

    //-------------------------------------------------- Handle graph ------------------------------------

    private void getGraphData(String[] xTStrings, String[] barStrings) {

        //X axis title, currently it is static
        graphLabelXAxis = new ArrayList<>();
        for (int i = 0; i < xTStrings.length; i++) {
            graphLabelXAxis.add(xTStrings[i]);
        }

        //getting data for
        valueSet = new ArrayList<>();
        for (int i = 0; i < barStrings.length; i++) {
            BarEntry v1e1 = new BarEntry(Float.parseFloat(barStrings[i]), i); // 1
            valueSet.add(v1e1);
        }


    }


    //Handling Graph
    private void handleGraph(int maxY) {

        //Handling graph X axis Content
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setTextSize(10);
        xAxis.setDrawGridLines(false);
        xAxis.setValues(graphLabelXAxis);
        xAxis.setAxisLineColor(Color.parseColor(graphItemColor));
        xAxis.setTextColor(Color.parseColor(graphItemColor));

        //Handling graph Y axis(Left) Content

        YAxis leftAxis = chart.getAxisLeft();
        // leftAxis.setDrawAxisLine(false);
        leftAxis.setTextColor(Color.parseColor(graphItemColor));
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setDrawGridLines(false);
        leftAxis.setLabelCount(5, true);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setEnabled(true);
        leftAxis.setAxisLineColor(Color.parseColor(graphItemColor));
        leftAxis.setAxisMinValue(0);
        leftAxis.setAxisMaxValue(1000);

        //Handling graph Y axis(Right) Content. making it invisible

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setTextColor(Color.parseColor(graphBgColor));
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(0, false);


        // add a nice and smooth animation
        chart.animateY(2500);

        //Adding data to graph
        BarData data = new BarData(xAxis.getValues(), getDataSet());
        chart.setData(data);

        //Add description here
        chart.setDescription("");

        //You can add grid (background through here,currently it is no needed
        chart.setDrawGridBackground(false);
        //  chart.setGridBackgroundColor(Color.parseColor("#010f1a"));

        chart.setPinchZoom(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.animateXY(2000, 2000);
        chart.invalidate();

        //hide information chart from bottom
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        //set text for no play data
        chart.setDescription("");
        chart.setNoDataText("No chart data available."); // this is the top line
        chart.setNoDataTextDescription("..."); // this is one line below the no-data-text
        chart.invalidate();
    }


    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        //Showing Bar and height of bar
        BarDataSet barDataSet = new BarDataSet(valueSet, "CMYCO");
        barDataSet.setColor(Color.parseColor(graphItemColor));
        barDataSet.setValueTextSize(5.00f);
        barDataSet.setValueTextColor(Color.parseColor(graphBgColor));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet);

        return dataSets;
    }


    //method to handle toolbar(custom Action bar)
    private void handleToolbar() {
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //menu item id
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            //clearing session from sheared preference
            logoutAPI.logout(snakeOnClickForLogout, coordinatorLayout);

            return true;
        }
//        else if (id == android.R.id.home) {
//            this.finish();
//        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if (!TIMER) {
            super.onBackPressed();

            Intent intent = new Intent(GameActivity.this, MasterPageActivity.class);
            startActivity(intent);
        }

    }

    /* //Asynchronous class to get defence category data from json stored at sheared Preference
        private class GetDefenceDataForRadioButton extends AsyncTask<String, String, String> {
            int result = 0;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                Progress.start(GameActivity.this);
            }

            @Override
            protected String doInBackground(String... strings) {

                try {
                    JSONObject jsonObject = new JSONObject(pollsPref.getPollData().toString());

                    if (jsonObject != null) {
                        JSONArray jsonArrayPoleData = jsonObject.getJSONArray(Const.KEY_DATA);

                        for (int j = 0; j < jsonArrayPoleData.length(); j++) {
                            JSONObject jsonObjectPollOptions = jsonArrayPoleData.getJSONObject(j);

                            int id = jsonObjectPollOptions.getInt(Const.KEY_ID);

                            txtLogo = jsonObjectPollOptions.getString(Const.KEY_NAME);

                            JSONArray jsonArrayPollOptions = jsonObjectPollOptions.getJSONArray(Const.KEY_POLL_OPTION);

                            for (int k = 0; k < jsonArrayPollOptions.length(); k++) {
                                JSONObject jsonObjectPollCat = jsonArrayPollOptions.getJSONObject(k);

                                result = 0;

                                if (id == 1) {
                                    //Defence Data
                                    HashMap<String, String> defencePollCatStrings = new HashMap<>();
                                    defencePollCatStrings.put(Const.KEY_ID, jsonObjectPollCat.getString(Const.KEY_ID));
                                    defencePollCatStrings.put(Const.KEY_NAME, jsonObjectPollCat.getString(Const.KEY_NAME));
                                    catDefenceArrayList.add(defencePollCatStrings);
                                    result = 1;


                                } else if (id == 2) {
                                    result = 1;
                                } else if (id == 3) {
                                    result = 1;
                                } else if (id == 4) {
                                    result = 1;
                                } else if (id == 5) {
                                    result = 1;
                                }
                            }
                        }


                    } else {
                        result = 0;
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
                    setDefenceCat();
                } else if (result == 0) {
                    alertDialogManager.showAlertDialog(GameActivity.this, getString(R.string.alert_no_data));
                }
            }
        }*/
//
    //Setting Layout

    private void setDefenceCat() {

        //Inflating category radio button and TextView
        catRadioButtons = new RadioButton[catDefenceArrayList.size()];

        //setting radio buttons
        for (int i = 0; i < catDefenceArrayList.size(); i++) {

            //Inflating textView
            View tvView = getLayoutInflater().inflate(R.layout.textview_layout, null);
            TextView textView = (TextView) tvView.findViewById(R.id.tvCat);
            textView.setText(String.valueOf(i + 1));
            ll_cat_no.addView(tvView);

            //Creating Dynamic Radio Button

            catRadioButtons[i] = new RadioButton(GameActivity.this);
            catRadioButtons[i].setId(Integer.parseInt(catDefenceArrayList.get(i).getPoll_id()));
            catRadioButtons[i].setText(catDefenceArrayList.get(i).getPoll_name());
            catRadioButtons[i].setTextColor(Color.parseColor("#FFFFFF"));
            catRadioButtons[i].setTag(i);

            // setting first radio button checked for the first time
//            if (i == 0 && firstTime) {
//                catRadioButtons[i].setChecked(true);
//                firstTime = false;
//            }

            //Adding Views
            defenceRadioGroup.addView(catRadioButtons[i]);

            //Applying click Listener on category radio button
            catRadioButtons[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    disableRadioButtons();

                    tag = Integer.parseInt(compoundButton.getTag().toString());

                    pollsPref.saveOptions(tag + catDefenceArrayList.get(tag).getPoll_name());

                    callRatingApi(tag);

                }
            });

        }
    }

    private void disableRadioButtons() {
        callgraphAPI();
        for (int i = 0; i < defenceRadioGroup.getChildCount(); i++) {
            ((RadioButton) defenceRadioGroup.getChildAt(i)).setEnabled(false);
        }
    }


    //---------------------------------------------  Rating API-----------------------------------------------
    private void handleRatingRetry() {
        snakeOnClickDoRating = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                callRatingApi(tag);
            }
        };
    }

    private void callRatingApi(int tag) {
        if (Utility.isNetworkAvailable(this)) {
            new PostRating().execute(tag);
        } else {
            SnackNotify.showSnakeBar(GameActivity.this, snakeOnClickDoRating, coordinatorLayout);
        }
    }

    private class PostRating extends AsyncTask<Integer, String, String> {
        JsonParser jsonParser;
        int result = -1;
        int activeGame;
        String msg = "";
        HashMap<String, String> hashMapLoginDetail;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            hashMapLoginDetail = new HashMap<>();
            Progress.start(GameActivity.this);
        }

        @Override
        protected String doInBackground(Integer... params) {
            jsonParser = new JsonParser(GameActivity.this);


            int position = params[0];

            try {

                String url = Const.RATING + "?user_id=" + pollsPref.getUserID() + "&game_id=" + pollsPref.getActiveGame() + "&poll_id=" + pollId + "&poll_option=" + catDefenceArrayList.get(position).getPoll_id() + "&team_id=" + pollsPref.getTeamId();

                Log.e("url", url);

                String jsonString = jsonParser.getJSONFromUrl(url);
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                    msg = jsonObject.getString(Const.KEY_MSG);
                    if (result == 1) {


                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (result == 1) {
                alertDialogManager.showAlertDialog(GameActivity.this, "Your vote has been submitted successfully");
            } else if (result == 0) {
                alertDialogManager.showAlertDialog(GameActivity.this, msg);
            } else {
                alertDialogManager.showAlertDialog(GameActivity.this, getString(R.string.server_not_responding));
            }


            Progress.stop();
        }
    }

    //----------------------------------------------Start handling graph API ---------------------------------------

    private void callgraphAPI() {
        //getting graph data
        if (Utility.isNetworkAvailable(GameActivity.this)) {
            new GetGraph().execute();
        } else {
            //show snake bar
            SnackNotify.showSnakeBar(GameActivity.this, snakeOnClickGetGraph, coordinatorLayout);
        }
    }


    private void handleGraphRetry() {
        snakeOnClickGetGraph = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                callgraphAPI();
            }
        };
    }

    //Getting graph Data from API
    private class GetGraph extends AsyncTask<String, String, String> {

        int result = -1;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(GameActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {

            JsonParser jsonParser = new JsonParser(GameActivity.this);

            String url = Const.GET_GRAPH + "team_id=" + pollsPref.getTeamId() + Const.TAG_GAME_ID + pollsPref.getActiveGame() + Const.TAG_POLL_ID + pollId;

            Log.e("url", url);

            String jsonString = jsonParser.getJSONFromUrl(url);

            Log.e("jsonString", jsonString);

            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                    if (result == 1) {
                        JSONArray jsonArrayGraphData = jsonObject.getJSONArray(Const.KEY_DATA);


                        for (int i = 0; i < jsonArrayGraphData.length(); i++) {
                            JSONObject jsonObjectPoleOption = jsonArrayGraphData.getJSONObject(i);

                            if (jsonObjectPoleOption != null) {
                                JSONArray jsonArrayGraph = jsonObjectPoleOption.getJSONArray("PollOption");

                                xTitle = new String[jsonArrayGraph.length()];
                                barDataStrings = new String[jsonArrayGraph.length()];

                                for (int j = 0; j < jsonArrayGraph.length(); j++) {
                                    JSONObject jsonObjectData = jsonArrayGraph.getJSONObject(i);

                                    xTitle[j] = String.valueOf(j + 1);
                                    //  barDataStrings[j] = jsonObjectData.getString();

                                }

                                //   barDataStrings[i] = jsonObjectGraph.getString()
                            }
                        }
                    } else {
                        msg = jsonObject.getString(Const.KEY_MSG);
                    }
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
                getGraphData(xTitle, barDataStrings);
                handleGraph(maxY);
                // show play call

            } else if (result == 0) {
                alertDialogManager.showAlertDialog(GameActivity.this, msg);
            } else {
                alertDialogManager.showAlertDialog(GameActivity.this, getString(R.string.server_not_responding));
            }
        }
    }
}
