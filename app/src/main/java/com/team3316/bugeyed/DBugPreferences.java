package com.team3316.bugeyed;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashMap;

public class DBugPreferences {
    /*
     * Singleton stuff
     */
    private static DBugPreferences _prefrences;
    public static DBugPreferences getInstance() {
        if (_prefrences == null)
            _prefrences = new DBugPreferences();
        return _prefrences;
    }


    private SharedPreferences _sharedPrefrences;
    private HashMap<String, Integer> _prefrencesMap;

    private DBugPreferences() {
        this._sharedPrefrences = PreferenceManager.getDefaultSharedPreferences(DBugApplication.getContext());

        this._prefrencesMap = new HashMap<>();
    }

    public int get(String key, int defaultValue) {
        try {
           return this._sharedPrefrences.getInt(key, this._prefrencesMap.get(key));
        } catch (NullPointerException e) {
            Log.d(this.getClass().getName(), "Item " + key + " not in preferences");
            this.set(key, defaultValue);
            return defaultValue;
        }
    }

    public void set(String key, int value) {
        this._prefrencesMap.put(key, value);
        this._sharedPrefrences.edit().putInt(key, value).apply();
    }
}
