package io.github.hkusu.realmtodoapp;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        //Realm.deleteRealm(realmConfiguration); // 起動時に Realm のデータを削除(開発時)
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
