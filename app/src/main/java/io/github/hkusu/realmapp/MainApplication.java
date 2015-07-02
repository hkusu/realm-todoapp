package io.github.hkusu.realmapp;

import android.app.Application;

import io.realm.Realm;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 起動時に Realm のデータを削除(ファイル毎)
        Realm.deleteRealmFile(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
