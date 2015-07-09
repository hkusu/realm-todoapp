package io.github.hkusu.realmtodoapp;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * 本アプリケーションのApplicationクラス
 * (AndroidManifest.xmlで本クラスを指定)
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Realm の初期設定
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        //Realm.deleteRealm(realmConfiguration); // 起動時に Realm のデータを削除(開発時)
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // Realm の接続を閉じる（どのみちアプリケーションが終了するが念のため）
        TodoModel.getInstance().closeRealm();
    }
}
