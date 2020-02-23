package org.theseinitiatives.smarthouseapp.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class App extends Application {
    private static App mApp;
    private SharedPreferences prefs;
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }
    public static App getInstance() {
        if (App.mApp == null){
            App.mApp = new App();
        }
        return App.mApp;
    }
    public SharedPreferences getPrefs(){
        return prefs;
    }
}
