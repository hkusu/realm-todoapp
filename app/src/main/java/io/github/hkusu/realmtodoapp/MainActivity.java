package io.github.hkusu.realmtodoapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

/**
 * 本アプリケーションの本体となるActivityクラス
 * (AndroidManifest.xmlで本クラスを指定)
 */
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ※利用FragmentはレイアウトXMLで定義 ※
    }
}
