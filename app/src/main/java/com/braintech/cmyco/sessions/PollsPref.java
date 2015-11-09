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
<<<<<<< HEAD
    private static final String KEY_PLAY = "play";
=======
    private static final String KEY_TEAM_ID = "team_id";
    private static final String KEY_OPTIONS = "options";



    private static final String KEY_USER_ID="user_id";
    private static final String KEY_UESRNAME="username";
    private static final String KEY_EMAIL="email";

    private static final String KEY_ACTIVE_GAME="active_game";
>>>>>>> f256a5ec46d91b66fd517f6d9478407e6fb4a525

    private static final String KEY_ACTIVE_USERS = "active_users";


    public PollsPref(Context _context) {
        this.context = _context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

<<<<<<< HEAD

    public void storePLAY(String playId) {
        editor.putString(KEY_PLAY, playId);
    }

    public void storeCoachTeamDetail(String coach, String team) {
=======
    public void storeUserInfo(String user_id,String username,String email)
    {
        editor.putString(KEY_USER_ID, user_id);
        editor.putString(KEY_UESRNAME, username);
        editor.putString(KEY_EMAIL, email);

        editor.commit();
    }

    public void storeCoachTeamDetail(String coach, String team,String teamId) {
>>>>>>> f256a5ec46d91b66fd517f6d9478407e6fb4a525
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

    public void saveActiveGame(int activeGame)
    {
        editor.putInt(Const.KEY_ACTIVE_GAME, activeGame);
        editor.commit();
    }

    public void saveOptions(String options)
    {
        editor.putString(KEY_OPTIONS, options);
        editor.commit();
    }

    public String getCoachDetail() {
        return pref.getString(KEY_COACH, null);
    }

    public String getUserID() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getTeamDetail() {
        return pref.getString(KEY_TEAM, null);
    }

    public String getTeam1Detail() {
        return pref.getString(Const.KEY_TEAM1, null);
    }

    public String getTeam2Detail() {
        return pref.getString(Const.KEY_TEAM2, null);
    }

    public String getPollData() {
        return pref.getString(Const.KEY_POLL_DATA, null);
    }

    public String getOptions() {
        return pref.getString(KEY_OPTIONS, null);
    }

    public String getTeamId() {
        return pref.getString(KEY_TEAM_ID, null);
    }

    public String getTeam() {
        return pref.getString(KEY_TEAM, null);
    }

<<<<<<< HEAD
    public String getPlayOption() {
        return pref.getString(KEY_PLAY, "KEY");
=======
    public int getActiveGame() {
        return pref.getInt(KEY_ACTIVE_GAME, 0);
>>>>>>> f256a5ec46d91b66fd517f6d9478407e6fb4a525
    }


    public void clearPollData() {
        editor.clear();
        editor.commit();
    }
}
