package com.braintech.cmyco.sessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.braintech.cmyco.utils.Const;

/**
 * Created by Braintech on 02-Nov-15.
 */
public class PollsPref {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "polls_pref";

    private static final String KEY_COACH = "coach";
    private static final String KEY_TEAM = "team";

    private static final String KEY_PLAY = "play";

    private static final String KEY_TEAM_ID = "team_id";
    private static final String KEY_OPTIONS = "options";


    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_UESRNAME = "username";
    private static final String KEY_EMAIL = "email";

    private static final String KEY_ACTIVE_GAME = "active_game";


    private static final String KEY_ACTIVE_USERS = "active_users";

    public static final String KEY_POLL_ACTIVATED = "pollActivated";
    public static final String KEY_TIME_PRESENT = "timePresent";
    public static final String KEY_POSITION = "positions";
    public static final String KEY_CURRENT_POLL_ID = "pollId";

    public static final String KEY_ACTIVITY_RUNNING = "running";


    public PollsPref(Context _context) {
        this.context = _context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void storePLAY(String playId) {
        editor.putString(KEY_PLAY, playId);
    }


    public void storeUserInfo(String user_id, String username, String email) {
        editor.putString(KEY_USER_ID, user_id);
        editor.putString(KEY_UESRNAME, username);
        editor.putString(KEY_EMAIL, email);

        editor.commit();
    }


    public void storeCoachTeamDetail(String coach, String team, String teamId) {

        editor.putString(KEY_COACH, coach);
        editor.putString(KEY_TEAM, team);
        editor.putString(KEY_TEAM_ID, teamId);

        Log.e("team", team);
        editor.commit();
    }

    public void storeGameJson(String json) {
        editor.putString(Const.KEY_GAME, json);
        editor.commit();
    }

    public void storePollData(String json) {
        editor.putString(Const.KEY_POLL_DATA, json);
        editor.commit();
    }

    public void storeTeam1Json(String json) {
        editor.putString(Const.KEY_TEAM1, json);
        editor.commit();
    }

    public void storeTeam2Json(String json) {
        editor.putString(Const.KEY_TEAM2, json);
        editor.commit();
    }


    public void storeActiveUsers(String activeUsers) {
        editor.putString(Const.KEY_ACTIVE_USER, activeUsers);
        editor.commit();
    }

    public void saveActiveGame(int activeGame) {
        editor.putInt(Const.KEY_ACTIVE_GAME, activeGame);
        editor.commit();
    }

    public void saveOptions(String options) {
        editor.putString(KEY_OPTIONS, options);
        editor.commit();
    }

    public void saveCurrentPollId( int pollId)
    {
        editor.putInt(KEY_CURRENT_POLL_ID, pollId);
        editor.commit();
    }

    public void pollActivated(Boolean value) {
        editor.putBoolean(KEY_POLL_ACTIVATED, value);
        editor.commit();
    }


    public void saveTimePresent(Boolean value) {
        editor.putBoolean(KEY_TIME_PRESENT, value);
        editor.commit();
    }

    public void saveButtonClicked(Boolean value, int pollId, int tag) {
        editor.putBoolean(Const.KEY_BUTTON_CLICKED, value);
        editor.putInt(Const.KEY_POLL_ID, pollId);
        editor.putInt(KEY_POSITION, tag);
        editor.commit();
    }

    public void ActivityRunning(Boolean value) {
        editor.putBoolean(KEY_ACTIVITY_RUNNING, value);
        editor.commit();
    }


    public String getUserID() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getTeamDetail() {
        return pref.getString(KEY_TEAM, null);
    }

    public String getTeamId() {
        return pref.getString(KEY_TEAM_ID, null);
    }

    public String getTeam() {
        return pref.getString(KEY_TEAM, null);
    }


    public int getActiveGame() {
        return pref.getInt(KEY_ACTIVE_GAME, 0);

    }

    public int getPollId() {
        return pref.getInt(Const.KEY_POLL_ID, 0);
    }

    public int getCurrentPollId() {
        return pref.getInt(KEY_CURRENT_POLL_ID, -1);
    }

    public boolean isPollActivated() {
        return pref.getBoolean(KEY_POLL_ACTIVATED, false);
    }

    public boolean isTimePresent() {
        return pref.getBoolean(KEY_TIME_PRESENT, false);
    }

    public boolean isButtonClicked() {
        return pref.getBoolean(Const.KEY_BUTTON_CLICKED, false);
    }

    public boolean isActivityRunning() {
        return pref.getBoolean(KEY_ACTIVITY_RUNNING, false);
    }

    public int getPosition() {
        return pref.getInt(KEY_POSITION, 0);

    }

//    public void clearPollData() {
//        editor.clear();
//        editor.commit();
    //
//    }

    public void clearPollData() {

        editor.remove(KEY_COACH);
        editor.remove(KEY_TEAM);
        // editor.remove(KEY_PLAY);
        editor.remove(KEY_TEAM_ID);
        editor.remove(KEY_OPTIONS);
        // editor.remove(Const.KEY_POLL_ID);
        //editor.remove(Const.KEY_BUTTON_CLICKED);
        // editor.remove(KEY_USER_ID);
        //  editor.remove(KEY_UESRNAME);
        editor.remove(KEY_ACTIVE_USERS);
      //  editor.remove(KEY_ACTIVE_GAME);
        editor.remove(KEY_POLL_ACTIVATED);
        editor.remove(KEY_TIME_PRESENT);
        // editor.remove(KEY_POSITION);
        editor.remove(KEY_ACTIVITY_RUNNING);
        editor.remove(KEY_CURRENT_POLL_ID);
        editor.commit();
    }

    public void clearVotingData()
    {
        editor.remove(Const.KEY_POLL_ID);
        editor.remove(Const.KEY_BUTTON_CLICKED);
        editor.remove(KEY_POSITION);
        editor.commit();
    }


}
