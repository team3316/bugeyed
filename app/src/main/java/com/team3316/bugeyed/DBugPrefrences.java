package com.team3316.bugeyed;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class DBugPrefrences {
    /*
     * Singleton stuff
     */
    private static DBugPrefrences _prefrences;
    public static DBugPrefrences getInstance() {
        if (_prefrences == null)
            _prefrences = new DBugPrefrences();
        return _prefrences;
    }

    private static final String PREFERENCE_FILE_KEY = "com.team3316.bugeyed.PREFERENCE_FILE_KEY";

    private SharedPreferences _sharedPrefrences;
    private HashMap<String, Integer> _prefrencesMap;

    private DBugPrefrences() {
        _sharedPrefrences = DBugApplication.getContext().getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
    }
}
