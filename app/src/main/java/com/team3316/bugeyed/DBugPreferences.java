package com.team3316.bugeyed;

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

    public int get(String key, int defaultValue, boolean isInitializing) {
        try {
            if (isInitializing) {
                this._prefrencesMap.put(key, defaultValue);
                return this._sharedPrefrences.getInt(key, defaultValue);
            }
            return this._prefrencesMap.get(key);
        } catch (NullPointerException e) {
            return defaultValue;
        }
    }

    public void set(String key, int value, boolean isDone) {
        if (isDone) {
            Log.d(this.getClass().getSimpleName(), "Setting " + key + " to " + value + " on device cache");
            this._sharedPrefrences.edit().putInt(key, value).apply();
        } else {
            Log.d(this.getClass().getSimpleName(), "Setting " + key + " to " + value + " on hashmap cache");
            this._prefrencesMap.put(key, value);
        }
    }
}
