package io.github.hkusu.realmapp;

import android.app.Application;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 開発時：起動時に Realm のデータを削除(ファイル毎)
        //Realm.deleteRealmFile(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
