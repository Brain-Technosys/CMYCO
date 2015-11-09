package com.braintech.cmyco.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.braintech.cmyco.utils.Progress;
import com.github.mikephil.charting.charts.BarChart;
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

    RadioButton[] catRadioButtons;

    UserSession userSession;
    PollsPref pollsPref;

    AlertDialogManager alertDialogManager;

    ArrayList<PollOptions> catDefenceArrayList;
    ArrayList<String> graphLabelXAxis;
    ArrayList<BarEntry> valueSet;

    String[] xTitle = {"1", "2", "3", "4", "5"};
    String[] barDataStrings = {"750", "600", "300", "450", "500"};

    String txtLogo;

    boolean firstTime = true;

    String graphItemColor = "#14DDF9";
    String graphBgColor = "#010f1a";

    SnakeOnClick snakeOnClickForLogout;

    CommonAPI logoutAPI;

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

       /* if (pollsPref.getCoachDetail().equals(null)) {
            alertDialogManager.showAlertDialog(this, getString(R.string.alert_no_data));
        } else {
            new GetDefenceDataForRadioButton().execute();
        }*/

        if (getIntent().hasExtra(Const.TAG_POLL_OPTION)) {
            Bundle bundle = getIntent().getExtras();
            catDefenceArrayList = (ArrayList<PollOptions>) bundle.getSerializable(Const.TAG_POLL_OPTION);

            setDefenceCat();
        }

        new CountDownTimer(30000, 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

            public void onTick(long millisUntilFinished) {
                txtViewTimer.setText("Time Left: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                finish();
            }
        }
                .start();

        //Preparing data for graph
        getGraphData(xTitle, barDataStrings);

        handleLogoutRetry();

        // here we are Showing graph
        handleGraph();
    }

    private void handleLogoutRetry() {
        snakeOnClickForLogout = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                logoutAPI.logout(snakeOnClickForLogout, coordinatorLayout);
            }
        };

    }

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
    private void handleGraph() {

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

        // setting logo
        defenceTextView.setText(txtLogo);

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

            // setting first radio button checked for the first time
            if (i == 0 && firstTime) {
                catRadioButtons[i].setChecked(true);
                firstTime = false;
            }

            //Adding Views
            defenceRadioGroup.addView(catRadioButtons[i]);

            //Applying click Listener on category radio button
            catRadioButtons[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                    for(int i = 0; i < defenceRadioGroup.getChildCount(); i++) {
                        ((RadioButton) defenceRadioGroup.getChildAt(i)).setEnabled(false);

                        txtViewTimer.setVisibility(View.GONE);
                    }

                }
            });

        }
    }

}
