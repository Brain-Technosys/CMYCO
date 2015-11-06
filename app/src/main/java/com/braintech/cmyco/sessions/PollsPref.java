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

    private static final String KEY_ACTIVE_USERS = "active_users";



    public PollsPref(Context _context) {
        this.context = _context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void storeCoachTeamDetail(String coach, String team) {
        editor.putString(KEY_COACH, coach);
        editor.putString(KEY_TEAM, team);

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
        editor.putString(Const.KEY_ACTIVE_USER,activeUsers);
        editor.commit();
    }

    public String getCoachDetail() {
        return pref.getString(KEY_COACH, null);
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

    public String getTeam() {
        return pref.getString(KEY_TEAM, null);
    }


    public void clearPollData() {
        editor.clear();
        editor.commit();
    }
}
