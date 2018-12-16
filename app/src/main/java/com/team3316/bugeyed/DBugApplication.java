package com.team3316.bugeyed;

import android.app.Application;
import android.content.Context;

import net.ralphpina.permissionsmanager.PermissionsManager;

public class DBugApplication extends Application {
    private static Context _ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        PermissionsManager.init(this);
        DBugApplication._ctx = getApplicationContext();
    }

    public static Context getContext() {
        return DBugApplication._ctx;
    }
}
