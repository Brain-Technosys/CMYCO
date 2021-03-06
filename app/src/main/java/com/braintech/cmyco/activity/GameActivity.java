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
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.braintech.cmyco.R;
import com.braintech.cmyco.application.ControlApplication;
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


    @InjectView(R.id.chart)
    BarChart chart;

    @InjectView(R.id.txtViewTimer)
    TextView txtViewTimer;

    @InjectView(R.id.txtViewTimerText)
    TextView txtViewTimerText;

    @InjectView(R.id.ll_cat_no)
    LinearLayout ll_cat_no;

    @InjectView(R.id.txtPlayCall)
    TextView txtPlayCall;

    @InjectView(R.id.playCallLayout)
    LinearLayout playCallLayout;

    RadioButton[] catRadioButtons;

    UserSession userSession;
    PollsPref pollsPref;

    ArrayList<PollOptions> catDefenceArrayList;
    ArrayList<String> graphLabelXAxis;
    ArrayList<BarEntry> valueSet;

    String[] xTitle = {"1", "2", "3", "4", "5"};
    String[] barDataStrings = {"750", "600", "300", "450", "500"};


    int pollId;
    int tag; //for rating
    int maxY = 1000;

    boolean isActivityStarted = false;
    boolean isButtonClicked;

    String graphItemColor = "#14DDF9";
    String graphBgColor = "#010f1a";

    // graph colors
    String blue = "#14DDF9";
    String color2 = "#22ca51";
    String color3 = "#f56200";
    String color4 = "#e8ba00";
    String color5 = "#9b59b6";
    String color6 = "#c0392b";
    String color7 = "#14DDF9";

    String pollName;

    long pollDuration;

    SnakeOnClick snakeOnClickForLogout;
    SnakeOnClick snakeOnClickGetGraph;
    SnakeOnClick snakeOnClickDoRating;

    CommonAPI logoutAPI;

    boolean showPlayCall = false;

    AlertDialogManager alertDialogManager;

    private static final String TAG = InstructionActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ButterKnife.inject(this);

        handleToolbar();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        alertDialogManager = new AlertDialogManager();

        catDefenceArrayList = new ArrayList<>();

        logoutAPI = new CommonAPI(this);

        userSession = new UserSession(this);
        pollsPref = new PollsPref(this);

        alertDialogManager = new AlertDialogManager();


        setFonts();

        if (getIntent().hasExtra(Const.TAG_POLL_OPTION)) {
            Bundle bundle = getIntent().getExtras();
            catDefenceArrayList = (ArrayList<PollOptions>) bundle.getSerializable(Const.TAG_POLL_OPTION);

            pollId = bundle.getInt(Const.KEY_POLL_ID);
            Log.e("pollid", "" + pollId);
            if (pollId == 4) {
                pollName = "Substitution Player Out";
            } else if (pollId == 8) {
                pollName = "Substitution Player In";
            } else
                pollName = bundle.getString(Const.KEY_POLL_NAME, pollName);

            pollDuration = bundle.getLong(Const.KEY_POLL_DURATION);

            pollDuration = pollDuration * 1000;

            // isButtonClicked = bundle.getBoolean(Const.KEY_BUTTON_CLICKED);

            Log.e("pollDuration", "" + pollDuration);


            defenceTextView.setText(pollName);

            setGraphColor();

            setDefenceCat();
        }

        new CountDownTimer(pollDuration, 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

            public void onTick(long millisUntilFinished) {

                Log.e("isButtonClicked", "" + isButtonClicked);
                if (!isButtonClicked) {
                    txtViewTimerText.setText("Time Left: ");
                } else {
                    txtViewTimerText.setText("Waiting for playcall: ");
                }
                txtViewTimer.setText("" + millisUntilFinished / 1000);

            }

            public void onFinish() {

                showPlayCall = true;

                disableRadioButtons();
                txtViewTimer.setVisibility(View.GONE);
                txtViewTimerText.setVisibility(View.GONE);
            }
        }
                .start();

        //Preparing data for graph
        //   getGraphData(xTitle, barDataStrings);

        handleLogoutRetry();
        handleRatingRetry();
        handleGraphRetry();

        // here we are Showing graph
        //  handleGraph(maxY);

        //sending user active status to server
        CommonAPI commonAPI = new CommonAPI(this);
        commonAPI.setActiveUserStatus();
    }

    public void setFonts() {
        Fonts.robotoRegular(this, defenceTextView);
        Fonts.robotoRegular(this, txtPlayCall);
        Fonts.robotoRegular(this, txtViewTimer);
        Fonts.robotoRegular(this, txtViewTimerText);
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
                isActivityStarted = true;
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

        if (maxY == 0) {
            maxY = 5;
        }

        //Handling graph X axis Content
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setTextSize(10);
        xAxis.setDrawGridLines(false);
        xAxis.setValues(graphLabelXAxis);
        xAxis.setAxisLineColor(Color.parseColor(graphItemColor));
        xAxis.setAxisLineWidth(2);
        xAxis.setTextColor(Color.parseColor(graphItemColor));

        //Handling graph Y axis(Left) Content

        YAxis leftAxis = chart.getAxisLeft();
        // leftAxis.setDrawAxisLine(false);
        leftAxis.setTextColor(Color.parseColor(graphItemColor));
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setDrawGridLines(false);
        leftAxis.setLabelCount(6, true);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setEnabled(true);
        leftAxis.setAxisLineColor(Color.parseColor(graphItemColor));
        leftAxis.setAxisMinValue(0);
        leftAxis.setAxisLineWidth(2);
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
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
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

    private float getYAxisData(int maxY, int i) {
        float rem;
        if (maxY == 1) {
            rem = 2;
        } else {
            rem = maxY % 2;
            rem = maxY + rem;
        }
        return rem;
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        //Showing Bar and height of bar
        BarDataSet barDataSet = new BarDataSet(valueSet, "CMYCO");
        barDataSet.setColor(Color.parseColor(graphItemColor));
        barDataSet.setValueTextSize(10.00f);
        barDataSet.setValueTextColor(Color.parseColor(graphBgColor));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet);

        return dataSets;
    }


    //method to handle toolbar(custom Action bar)
    private void handleToolbar() {
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);

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


        isActivityStarted = true;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            //clearing session from sheared preference
            logoutAPI.logout(snakeOnClickForLogout, coordinatorLayout);

            return true;
        } else if (id == android.R.id.home) {
            //sending user active status to server
            CommonAPI commonAPI = new CommonAPI(this);
            commonAPI.setActiveUserStatus();

            passIntent();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //sending user active status to server
        CommonAPI commonAPI = new CommonAPI(this);
        commonAPI.setActiveUserStatus();

        passIntent();
    }

    private void passIntent() {

        Intent intent = new Intent(GameActivity.this, GamePlayStrategyActivity.class);
        intent.putExtra(Const.TAG_POLL_ID, pollId);

        pollsPref.saveCurrentPollId(pollId);

        isActivityStarted = true;

        startActivity(intent);
        finish();
    }

    //Setting Layout

    private void setDefenceCat() {

        //Inflating category radio button and TextView
        catRadioButtons = new RadioButton[catDefenceArrayList.size()];

        //setting radio buttons
        for (int i = 0; i < catDefenceArrayList.size(); i++) {

            //Inflating textView

            View tvView = getLayoutInflater().inflate(R.layout.radio_btn_layout, null);
            TextView textView = (TextView) tvView.findViewById(R.id.tvCat);
            catRadioButtons[i] = (RadioButton) tvView.findViewById(R.id.rbCat);
            textView.setText(String.valueOf(i + 1));

            //Creating Dynamic Radio Button

            catRadioButtons[i].setId(Integer.parseInt(catDefenceArrayList.get(i).getPoll_id()));
            catRadioButtons[i].setText(catDefenceArrayList.get(i).getPoll_name());
            catRadioButtons[i].setTextColor(Color.parseColor("#FFFFFF"));
            catRadioButtons[i].setTag(i);
            ll_cat_no.addView(tvView);

            Fonts.robotoRegular(this, catRadioButtons[i]);

            //Adding Views

            if (isButtonClicked) {
                int position = pollsPref.getPosition();
                if (i == position) {
                    catRadioButtons[i].setChecked(true);

                }


            }

            //Applying click Listener on category radio button
            catRadioButtons[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    isButtonClicked = true;

                    disableRadioButtons();

                    tag = Integer.parseInt(compoundButton.getTag().toString());

                    pollsPref.saveButtonClicked(true, pollId, tag);

                    callRatingApi(tag);

                    //sending user active status to server
                    CommonAPI commonAPI = new CommonAPI(GameActivity.this);
                    commonAPI.setActiveUserStatus();

                }
            });


        }
        if (isButtonClicked) {
            disableRadioButtons();
        }
    }

    private void disableRadioButtons() {


        for (int i = 0; i < catRadioButtons.length; i++) {
            catRadioButtons[i].setEnabled(false);

        }
        callgraphAPI();

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

                String jsonString = jsonParser.getJSONFromUrl(url);
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                    msg = jsonObject.getString(Const.KEY_MSG);
                    if (result == 1) {


                    }

                }
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
        String resultMaxPoll;
        Long playCallDur;
        String maxId;
        String sequence;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(GameActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {

            JsonParser jsonParser = new JsonParser(GameActivity.this);

            String url = Const.GET_GRAPH + "team_id=" + pollsPref.getTeamId() + Const.TAG_GAME_ID + pollsPref.getActiveGame() + Const.TAG_POLL_ID + pollId;

            //Log.e("graphurl", url);

            String jsonString = jsonParser.getJSONFromUrl(url);

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

                            sequence = "PLAY CALL :" + jsonObjectData.getString(String.valueOf("sequence"));

                            //set max value for Graph Y axis
                            maxY = jsonObjectData.getInt("max_value");

                            //set duration
                            playCallDur = Long.parseLong(jsonObjectData.getString("playcall_time"));
                            playCallDur = playCallDur * 1000;

                        }


                    } else {
                        msg = jsonObject.getString(Const.KEY_MSG);
                    }
                }
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
                getGraphData(xTitle, barDataStrings);
                handleGraph(maxY);
                // show play call

                if (showPlayCall) {
                    playCallLayout.setVisibility(View.VISIBLE);
                    txtPlayCall.setText(sequence);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms

                            pollsPref.saveOptions(maxId);
                            pollsPref.pollActivated(false);

                            if (!isActivityStarted) {
                                pollsPref.saveButtonClicked(false, 0, 0);
                                pollsPref.saveCurrentPollId(-1);
                                Intent intent = new Intent(GameActivity.this, GamePlayStrategyActivity.class);
                                finish();
                                startActivity(intent);
                            }
                        }
                    }, playCallDur);
                }

            } else if (result == 0) {
                alertDialogManager.showAlertDialog(GameActivity.this, msg);
            } else {
                alertDialogManager.showAlertDialog(GameActivity.this, getString(R.string.server_not_responding));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onResume() {
        super.onResume();

        isButtonClicked = false;
    }
}
