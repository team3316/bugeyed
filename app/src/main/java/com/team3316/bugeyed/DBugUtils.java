package com.team3316.bugeyed;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

public class DBugUtils {
    public static Activity unwrap(Context ctx) {
        while (!(ctx instanceof Activity) && ctx instanceof ContextWrapper) {
            ctx = ((ContextWrapper) ctx).getBaseContext();
        }

        return (Activity) ctx;
    }
}
