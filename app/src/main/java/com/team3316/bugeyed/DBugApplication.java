package com.team3316.bugeyed;

import android.app.Application;

import net.ralphpina.permissionsmanager.PermissionsManager;

public class DBugApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PermissionsManager.init(this);
    }
}
