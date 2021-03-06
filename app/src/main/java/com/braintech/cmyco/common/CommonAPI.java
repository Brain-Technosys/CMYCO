package com.braintech.cmyco.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.IntentCompat;
import android.util.Log;

import com.braintech.cmyco.R;
import com.braintech.cmyco.activity.HomeActivity;
import com.braintech.cmyco.activity.LoginActivity;
import com.braintech.cmyco.application.ControlApplication;
import com.braintech.cmyco.my_interface.SnakeOnClick;
import com.braintech.cmyco.sessions.PollsPref;
import com.braintech.cmyco.sessions.UserSession;
import com.braintech.cmyco.utils.AlertDialogManager;
import com.braintech.cmyco.utils.Const;
import com.braintech.cmyco.utils.JsonParser;
import com.braintech.cmyco.utils.Progress;
import com.braintech.cmyco.utils.SnackNotify;
import com.braintech.cmyco.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Braintech on 05-Nov-15.
 */
public class CommonAPI {
    AlertDialogManager alertDialogManager;
    Context context;

    UserSession userSession;
    PollsPref pollsPref;

    String gameId;
    String pollId;

    String pollOption;
    String teamId;

    public CommonAPI(Context context) {
        this.context = context;
        alertDialogManager = new AlertDialogManager();
        userSession = new UserSession(context);
        pollsPref = new PollsPref(context);
    }


    public void logout(SnakeOnClick snakeOnClick, CoordinatorLayout coordinatorLayout) {

        if (Utility.isNetworkAvailable(context)) {
            new LogoutUser().execute(userSession.getUserID());
        } else {
            SnackNotify.showSnakeBar((Activity) context, snakeOnClick, coordinatorLayout);
        }

    }

    public void logout() {
        if (Utility.isNetworkAvailable(context)) {
            new LogoutUser().execute(userSession.getUserID());
        } else {

            alertDialogManager.showAlertDialog(context, context.getString(R.string.internet_connectivity_message));
        }
    }


    //Asynchronous class to call logout API

    private class LogoutUser extends AsyncTask<String, String, String> {
        int result = -1;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser(context);

            String url = Const.GET_LOG_OUT + strings[0];

            String jsonString = jsonParser.getJSONFromUrl(url);

            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                } else {
                    msg = jsonObject.getString(Const.KEY_MSG);
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

                pollsPref.pollActivated(true);
                userSession.logout();
                pollsPref.clearPollData();
                pollsPref.clearVotingData();

                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);

                if ((context instanceof ControlApplication)) {
                    // do nothing
                } else {
                    ((Activity) context).finish();
                }


                context.startActivity(intent);

            } else if (result == 0) {
                alertDialogManager.showAlertDialog(context, msg);
            } else {
                alertDialogManager.showAlertDialog(context, context.getString(R.string.server_not_responding));
            }
        }
    }


    public void getTimeOut(SnakeOnClick snakeOnClick, CoordinatorLayout coordinatorLayout) {

        if (Utility.isNetworkAvailable(context)) {
            new GetTimeOut().execute();
        } else {
            SnackNotify.showSnakeBar((Activity) context, snakeOnClick, coordinatorLayout);
        }

    }


//Asynchronous class to call GET_TIME_OUT API

    private class GetTimeOut extends AsyncTask<String, String, String> {
        int result = -1;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser(context);

            String url = Const.GET_PLAY_CALL_TIME;
            String jsonString = jsonParser.getJSONFromUrl(url);

            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                } else {
                    msg = jsonObject.getString(Const.KEY_MSG);
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
                //do something

            } else if (result == 0) {
                alertDialogManager.showAlertDialog(context, msg);
            } else {
                alertDialogManager.showAlertDialog(context, context.getString(R.string.server_not_responding));
            }
        }
    }


    public void getActiveUsers(SnakeOnClick snakeOnClick, CoordinatorLayout coordinatorLayout) {

        if (Utility.isNetworkAvailable(context)) {
            new GetActiveUsers().execute();
        } else {
            SnackNotify.showSnakeBar((Activity) context, snakeOnClick, coordinatorLayout);
        }

    }


    //Asynchronous class to call GET_TIME_OUT API

    private class GetActiveUsers extends AsyncTask<String, String, String> {
        int result = -1;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser(context);

            String url = Const.GET_ACTIVE_USERS;
            String jsonString = jsonParser.getJSONFromUrl(url);

            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);

                    String activeUsers = jsonObject.getString(Const.KEY_ACTIVE_USER);

                    pollsPref.storeActiveUsers(activeUsers);
                } else {
                    msg = jsonObject.getString(Const.KEY_MSG);
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
                //do something
            } else if (result == 0) {
                alertDialogManager.showAlertDialog(context, msg);
            } else {
                alertDialogManager.showAlertDialog(context, context.getString(R.string.server_not_responding));
            }
        }


    }

    public void getPollData(SnakeOnClick snakeOnClick, CoordinatorLayout coordinatorLayout) {

        if (Utility.isNetworkAvailable(context)) {
            new PollData().execute();
        } else {
            if (LoginActivity.class.isInstance(context))
                SnackNotify.showSnakeBar((Activity) context, snakeOnClick, coordinatorLayout);
        }
    }


    private class PollData extends AsyncTask<String, String, String> {
        int result = -1;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser(context);

            String url = Const.GET_ACTIVE_GAME_DETAIL;
            String jsonString = jsonParser.getJSONFromUrl(url);

            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                    if (result == 1) {

                        pollsPref.storePollData(jsonString);
                    }

                } else {
                    msg = jsonObject.getString(Const.KEY_MSG);
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

                //Checking Instance of Login Activity, and go to home after successful login
                if (LoginActivity.class.isInstance(context)) {
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                    LoginActivity.loginActivity.finish();
                } else {
                    //do something
                }

            } else if (result == 0) {
                alertDialogManager.showAlertDialog(context, msg);
            } else {
                alertDialogManager.showAlertDialog(context, context.getString(R.string.server_not_responding));
            }
        }
    }


    //this method is used to do Rating from Master Screen
    public void doRating(SnakeOnClick snakeOnClick, CoordinatorLayout coordinatorLayout, String gameId, String pollId, String pollOption, String teamId) {

        this.gameId = gameId;
        this.pollId = pollId;
        this.pollOption = pollOption;
        this.teamId = teamId;

        if (Utility.isNetworkAvailable(context)) {
            new Rating().execute();
        } else {
            SnackNotify.showSnakeBar((Activity) context, snakeOnClick, coordinatorLayout);
        }
    }


    private class Rating extends AsyncTask<String, String, String> {
        int result = -1;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser(context);

            String url = Const.RATING + Const.TAG_USER_ID + userSession.getUserID() + Const.TAG_GAME_ID + gameId + Const.TAG_POLL_ID + pollId +
                    Const.TAG_POLL_OPTION + pollOption + Const.TAG_TEAM_ID + teamId;
            String jsonString = jsonParser.getJSONFromUrl(url);
            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                    if (result == 1) {
                        JSONObject jsonObjectPollData = jsonObject.getJSONObject(Const.KEY_POLL_DATA);
                        pollsPref.storePollData(jsonObjectPollData.toString());
                    }

                } else {
                    msg = jsonObject.getString(Const.KEY_MSG);
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
                //do something

            } else if (result == 0) {
                alertDialogManager.showAlertDialog(context, msg);
            } else {
                alertDialogManager.showAlertDialog(context, context.getString(R.string.server_not_responding));
            }
        }
    }


    public void setActiveUserStatus() {
        if (Utility.isNetworkAvailable(context)) {
            if (userSession.getUserID() != null) {
                new SetUserActiveStatus().execute(userSession.getUserID());
            }
        }
    }


    private class SetUserActiveStatus extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            String url = Const.SEND_USER_ACTIVE_STATUS;
            JsonParser jsonParser = new JsonParser(context);

            String jsonString = jsonParser.getJSONFromUrl(url);

            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {

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

            //Do nothing
        }
    }
}
