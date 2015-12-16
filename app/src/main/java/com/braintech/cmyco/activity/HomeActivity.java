package com.braintech.cmyco.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;

import com.braintech.cmyco.R;
import com.braintech.cmyco.adapter.SpinnerAdapter;
import com.braintech.cmyco.common.CommonAPI;
import com.braintech.cmyco.my_interface.SnakeOnClick;
import com.braintech.cmyco.sessions.PollsPref;
import com.braintech.cmyco.sessions.UserSession;
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
import butterknife.OnClick;
import butterknife.OnItemSelected;


public class HomeActivity extends MyBaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.coach_spinner)
    Spinner coachSpinner;

    @InjectView(R.id.team_spinner)
    Spinner teamSpinner;

    @InjectView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @InjectView(R.id.btn_continue)
    Button continueButton;

    ArrayList<HashMap<String, String>> arrayListCoach;
    ArrayList<HashMap<String, String>> arrayListTeam;

    SpinnerAdapter coachAdapter;
    SpinnerAdapter teamAdapter;

    boolean doubleBackToExitPressedOnce = false;
    boolean firstTime = true;

    UserSession userSession;
    PollsPref pollsPref;

    AlertDialogManager alertDialogManager;

    SnakeOnClick snakeOnClick;
    SnakeOnClick snakeRetryCoach;

    CommonAPI logoutAPI;

    String teamName;
    String teamId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.inject(this);

        manageToolbar();

        userSession = new UserSession(this);

        pollsPref = new PollsPref(this);

        logoutAPI = new CommonAPI(this);

        alertDialogManager = new AlertDialogManager();

        setFont();

        setSpinners();

        handleSnakeRetryCall();


    }

    private void handleSnakeRetryCall() {
        snakeOnClick = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                logoutAPI.logout(snakeOnClick, coordinatorLayout);
            }
        };

        snakeRetryCoach = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                callGetCoachDataAPI();
            }
        };
    }

    private void manageToolbar() {
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

    }

    private void setFont() {
        Fonts.robotoRegularToButton(this, continueButton);
    }

    private void setSpinners() {

        setCoachSpinner();

        setTeamSpinner();

    }

    private void setTeamSpinner() {
        HashMap<String, String> hashMapTeam = new HashMap<>();

        hashMapTeam.put(Const.KEY_ID, "0");
        hashMapTeam.put(Const.KEY_NAME, getString(R.string.spn_team_title));

        arrayListTeam = new ArrayList<>();
        arrayListTeam.add(hashMapTeam);

        teamAdapter = new SpinnerAdapter(HomeActivity.this, arrayListTeam);
        teamSpinner.setAdapter(teamAdapter);
    }

    private void setCoachSpinner() {


        HashMap<String, String> hashMapCoach = new HashMap<>();

        hashMapCoach.put(Const.KEY_ID, "0");
        hashMapCoach.put(Const.KEY_NAME, getString(R.string.spn_coach_title));

        arrayListCoach = new ArrayList<>();
        arrayListCoach.add(hashMapCoach);

        coachAdapter = new SpinnerAdapter(HomeActivity.this, arrayListCoach);
        coachSpinner.setAdapter(coachAdapter);

        callGetCoachDataAPI();
    }

    private void callGetCoachDataAPI() {
        if (Utility.isNetworkAvailable(this))
            new GetCoachData().execute();
        else
            SnackNotify.showSnakeBar(this, snakeRetryCoach, coordinatorLayout);
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        SnackNotify.showSnakeBarForBackPress(this, coordinatorLayout);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private class GetCoachData extends AsyncTask<String, String, String> {
        int result = -1;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(HomeActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser(HomeActivity.this);
            arrayListCoach = new ArrayList<>();

            HashMap<String, String> hashMapCoachTitle = new HashMap<>();

            hashMapCoachTitle.put(Const.KEY_ID, "0");
            hashMapCoachTitle.put(Const.KEY_NAME, getString(R.string.spn_coach_title));




            arrayListCoach.add(hashMapCoachTitle);

            String url = Const.GET_COACH_RESULT;

            String urlString = jsonParser.getJSONFromUrl(url);

            try {


                JSONObject jsonObject = new JSONObject(urlString);

                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                    if (result == 1) {
                        JSONArray jsonArrayTeam = jsonObject.getJSONArray(Const.KEY_DATA);

                        for (int i = 0; i < jsonArrayTeam.length(); i++) {

                            JSONObject jsonObjectCoach = jsonArrayTeam.getJSONObject(i);
                            HashMap<String, String> hashMapCoach = new HashMap<>();

                            hashMapCoach.put(Const.KEY_ID, jsonObjectCoach.getString(Const.KEY_ID));
                            hashMapCoach.put(Const.KEY_NAME, jsonObjectCoach.getString(Const.KEY_NAME));

                            arrayListCoach.add(hashMapCoach);
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
                coachAdapter = new SpinnerAdapter(HomeActivity.this, arrayListCoach);
                coachSpinner.setAdapter(coachAdapter);
            } else if (result == 0) {
                alertDialogManager.showAlertDialog(HomeActivity.this, msg);
            } else {
                alertDialogManager.showAlertDialog(HomeActivity.this, getString(R.string.server_not_responding));
            }
        }
    }

    private class GetTeamData extends AsyncTask<String, String, String> {
        int result = -1;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(HomeActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {


            try {

                JsonParser jsonParser = new JsonParser(HomeActivity.this);
                arrayListTeam = new ArrayList<>();
                HashMap<String, String> hashMapTeamTitle = new HashMap<>();

                hashMapTeamTitle.put(Const.KEY_ID, "0");
                hashMapTeamTitle.put(Const.KEY_NAME, getString(R.string.spn_team_title));

                arrayListTeam.add(hashMapTeamTitle);

                String url = Const.GET_TEAM_RESULT + strings[0];
                String urlString = jsonParser.getJSONFromUrl(url);


                JSONObject jsonObject = new JSONObject(urlString);

                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);

                    if (result == 1) {


                        JSONArray jsonArrayTeamData = jsonObject.getJSONArray(Const.KEY_DATA);

                        for (int i = 0; i < jsonArrayTeamData.length(); i++) {


                            JSONObject jsonObjectGetTeamData = jsonArrayTeamData.getJSONObject(i);
                            HashMap<String, String> hashMapTeam = new HashMap<>();

                            hashMapTeam.put(Const.KEY_ID, jsonObjectGetTeamData.getString(Const.KEY_ID));
                            hashMapTeam.put(Const.KEY_NAME, jsonObjectGetTeamData.getString(Const.KEY_NAME));

                            arrayListTeam.add(hashMapTeam);
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
                teamAdapter = new SpinnerAdapter(HomeActivity.this, arrayListTeam);
                teamSpinner.setAdapter(teamAdapter);
                teamAdapter.notifyDataSetChanged();
            } else if (result == 0) {
                alertDialogManager.showAlertDialog(HomeActivity.this, msg);
            } else {
                alertDialogManager.showAlertDialog(HomeActivity.this, getString(R.string.server_not_responding));
            }
        }
    }


    @OnItemSelected(R.id.coach_spinner)
    public void getSelectedCoach(int pos) {


        if (pos == 0) {
            //do nothing
        } else {
            if (Utility.isNetworkAvailable(this))
                new GetTeamData().execute(arrayListCoach.get(pos).get(Const.KEY_ID));
            else {
                SnackNotify.showSnakeBar(this, snakeRetryCoach, coordinatorLayout);
            }
        }
    }

    @OnItemSelected(R.id.team_spinner)
    public void getSelectedTeam(int pos) {
        if (coachSpinner.getSelectedItemPosition() == 0) {
            teamSpinner.setEnabled(false);
            if (!firstTime) {
                alertDialogManager.showAlertDialog(this, getString(R.string.alert_no_coach_selected));
                firstTime = false;
            }
        } else {
            teamSpinner.setEnabled(true);

            teamName = arrayListTeam.get(pos).get(Const.KEY_NAME);

            teamId = arrayListTeam.get(pos).get(Const.KEY_ID);

        }

    }

    @OnClick(R.id.btn_continue)
    public void soGraph() {

        if (coachSpinner.getSelectedItemPosition() == 0) {
            alertDialogManager.showAlertDialog(HomeActivity.this, getString(R.string.home_continue_error));
        } else if (teamSpinner.getSelectedItemPosition() == 0) {
            alertDialogManager.showAlertDialog(HomeActivity.this, getString(R.string.alert_no_team_selected));
        } else {
            PollsPref pollsPref = new PollsPref(HomeActivity.this);
            pollsPref.storeCoachTeamDetail(arrayListCoach.get(coachSpinner.getSelectedItemPosition()).get(Const.KEY_NAME), teamName, teamId);
            Intent intent = new Intent(HomeActivity.this, InstructionActivity.class);
            startActivity(intent);
        }
    }
}
