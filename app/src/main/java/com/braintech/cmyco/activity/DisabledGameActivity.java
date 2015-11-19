package com.braintech.cmyco.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.braintech.cmyco.R;
import com.braintech.cmyco.common.CommonAPI;
import com.braintech.cmyco.my_interface.SnakeOnClick;
import com.braintech.cmyco.objectclasses.PollOptions;
import com.braintech.cmyco.sessions.PollsPref;
import com.braintech.cmyco.sessions.UserSession;
import com.braintech.cmyco.utils.AlertDialogManager;
import com.braintech.cmyco.utils.Const;
import com.braintech.cmyco.utils.Fonts;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DisabledGameActivity extends AppCompatActivity {

    @InjectView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.lbl_defence)
    TextView defenceTextView;

    @InjectView(R.id.linLayTextView)
    LinearLayout linLayTextView;

    @InjectView(R.id.chart)
    BarChart chart;

//    @InjectView(R.id.playCallLayout)
//    LinearLayout playCallLayout;

    RadioButton[] catRadioButtons;

    UserSession userSession;
    PollsPref pollsPref;

    AlertDialogManager alertDialogManager;

    ArrayList<HashMap<String, String>> catDefenceArrayList;
    ArrayList<String> graphLabelXAxis;
    ArrayList<BarEntry> valueSet;

    ArrayList<PollOptions> arrayListPollOpt;

    String[] xTitle = {"1", "2", "3", "4", "5"};
    String[] barDataStrings = {"0", "0", "0", "0", "0"};

    String pollName;
    int pollId;
    int maxY = 1000;

    String txtLogo;

    boolean firstTime = true;

    String graphItemColor = "#14DDF9";
    String graphBgColor = "#010f1a";

    SnakeOnClick snakeOnClickForLogout;
    SnakeOnClick snakeOnClickGetGraph;

    // graph colors
    String blue = "#14DDF9";
    String color2 = "#22ca51";
    String color3 = "#f56200";
    String color4 = "#e8ba00";
    String color5 = "#9b59b6";
    String color6 = "#c0392b";
    String color7 = "#14DDF9";


    CommonAPI logoutAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disabled_game);

        ButterKnife.inject(this);

        handleToolbar();

     //   playCallLayout.setVisibility(View.GONE);

        alertDialogManager = new AlertDialogManager();

        catDefenceArrayList = new ArrayList<>();

        logoutAPI = new CommonAPI(this);

        userSession = new UserSession(this);
        pollsPref = new PollsPref(this);


        //Preparing data for graph
        // getGraphData(xTitle, barDataStrings);

        handleLogoutRetry();
        handleGraphRetry();

        // here we are Showing graph
        //  handleGraph(maxY);

        if (getIntent().hasExtra(Const.TAG_POLL_OPTION)) {

            Bundle bundle = getIntent().getExtras();

            arrayListPollOpt = (ArrayList<PollOptions>) bundle.getSerializable(Const.TAG_POLL_OPTION);
            pollId = bundle.getInt(Const.KEY_POLL_ID);
            pollName = bundle.getString(Const.KEY_POLL_NAME, pollName);

            defenceTextView.setText(pollName);

            createOptionsTextView();
        }

        setGraphColor();
        callgraphAPI();
    }

    private void setGraphColor() {
        if (pollId == 1) {
            graphItemColor = blue;
        } else if (pollId == 2) {
            graphItemColor = color2;
        } else if (pollId == 3) {
            graphItemColor = color3;
        } else if (pollId == 4) {
            graphItemColor = color4;
        } else if (pollId == 5) {
            graphItemColor = color5;
        } else if (pollId == 6) {
            graphItemColor = color6;
        } else if (pollId == 7) {
            graphItemColor = color7;
        } else {
            graphItemColor = blue;
        }
    }

    private void handleLogoutRetry() {
        snakeOnClickForLogout = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                logoutAPI.logout(snakeOnClickForLogout, coordinatorLayout);
            }
        };

    }

    private void createOptionsTextView() {
        if (arrayListPollOpt.size() != 0) {
            for (int i = 0; i < arrayListPollOpt.size(); i++) {

                //Inflating textView
                View tvView = getLayoutInflater().inflate(R.layout.textview_layout, null);
                TextView textView = (TextView) tvView.findViewById(R.id.tvCat);
                textView.setText(String.valueOf(i + 1) + ".  " + arrayListPollOpt.get(i).getPoll_name());

                //apply font
                Fonts.robotoRegular(this,textView);

                linLayTextView.addView(tvView);
            }
        }
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
        leftAxis.setLabelCount(2, true);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setEnabled(true);
        leftAxis.setAxisLineColor(Color.parseColor(graphItemColor));
        leftAxis.setAxisMinValue(0);
        leftAxis.setAxisMaxValue(maxY);


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
        // chart.setScaleMinima(2f, 1f);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        } else if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    //---------------------------------------------- Start handling graph API ---------------------------------------


    private void callgraphAPI() {
        //getting graph data
        if (Utility.isNetworkAvailable(DisabledGameActivity.this)) {
            new GetGraph().execute();
        } else {
            //show snake bar
            SnackNotify.showSnakeBar(DisabledGameActivity.this, snakeOnClickGetGraph, coordinatorLayout);
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
        String resultMaxPoll;
        Long playCallDur;
        String maxId;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(DisabledGameActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {

            JsonParser jsonParser = new JsonParser(DisabledGameActivity.this);

            String url = Const.GET_GRAPH + "team_id=" + pollsPref.getTeamId() + Const.TAG_GAME_ID + pollsPref.getActiveGame() + Const.TAG_POLL_ID + pollId;

           // Log.e("url", url);

            String jsonString = jsonParser.getJSONFromUrl(url);

            //Log.e("jsonString", jsonString);

            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                if (jsonObject != null) {


                    result = jsonObject.getInt(Const.KEY_RESULT);
                    if (result == 1) {
                        JSONObject jsonObjectData = jsonObject.getJSONObject(Const.KEY_DATA);

                        if (jsonObjectData != null) {

                            //Getting data for graph
                            JSONObject jsonObjectPollOption = jsonObjectData.getJSONObject("PollOption");

                            if (jsonObjectPollOption != null) {
                                int pollLength = jsonObjectPollOption.length();

                                xTitle = new String[pollLength];
                                barDataStrings = new String[pollLength];

                                for (int i = 0; i < pollLength; i++) {
                                    xTitle[i] = String.valueOf(i + 1);
                                    barDataStrings[i] = jsonObjectPollOption.getString(String.valueOf(i + 1));

                                }
                            }

                            //getting max Poll result
                            resultMaxPoll = "PLAY CALL :" + jsonObjectData.getString(String.valueOf("max_id"));
                            maxId = jsonObjectData.getString(String.valueOf("max_id")) + "." + jsonObjectData.getString(String.valueOf("max"));

                            //set max value for Graph Y axis
                            maxY = jsonObjectData.getInt("max_value");

                            //set duration
                            playCallDur = Long.parseLong(jsonObjectData.getString("playcall_time"));

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

            } else if (result == 0) {
                alertDialogManager.showAlertDialog(DisabledGameActivity.this, msg);
            } else {
                alertDialogManager.showAlertDialog(DisabledGameActivity.this, getString(R.string.server_not_responding));
            }
        }
    }


}
