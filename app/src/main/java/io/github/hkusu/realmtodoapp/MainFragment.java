package io.github.hkusu.realmtodoapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import de.greenrobot.event.EventBus;
import io.realm.Realm;

/**
 * 本アプリケーションの画面本体となるFragmentクラス
 */
public class MainFragment extends Fragment {
    @Bind(R.id.editText) EditText mEditText;
    @Bind(R.id.button)   Button   mButton;
    @Bind(R.id.textView) TextView mTextView;
    @Bind(R.id.listView) ListView mListView;

    /** Realmのインスタンス(データ参照用) */
    private Realm mRealm;
    /** Todoデータ操作モデルのインスタンス */
    private TodoModel mTodoModel;
    /** Todoデータ表示用ListViewにセットするListAdapter */
    private TodoListAdapter mTodoListAdapter;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Realmのインスタンを取得
        mRealm = Realm.getDefaultInstance();
        // EventBusのインスタンスを取得
        EventBus eventBus = EventBus.getDefault();
        // Todoデータ操作モデルを作成
        mTodoModel = new TodoModel(mRealm, eventBus);
        // 起動時にソフトウェアキーボードが表示されないようにする
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view); // ButterKnife
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // ListAdapterを作成
        mTodoListAdapter = new TodoListAdapter(
                getActivity(),
                R.layout.adapter_user_list,
                mTodoModel.get(), // ListViewに表示するデータセット
                mTodoModel // Todoデータの更新用に渡しておく
        );
        // ListViewにAdapterをセット
        mListView.setAdapter(mTodoListAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 画面の初期表示
        updateView();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this); // EventBus
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this); // EventBus
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this); // ButterKnife
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRealm != null) {
            // 接続を閉じる
            mRealm.close();
        }
    }

    /**
     * [登録]ボタン押下
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.button)
    public void onButtonClick() {
        // 入力内容が空の場合は何もしない
        if (mEditText.getText().toString().equals("")) {
            return;
        }
        // Todoデータを登録
        registerTodo();
    }

    /**
     * 入力エリアでEnter
     *
     * @param  event キーイベント
     * @return イベント処理結果(trueは消化済み)
     */
    @SuppressWarnings("unused")
    @OnEditorAction(R.id.editText)
    public boolean onEditTextEditorAction(KeyEvent event) {
        // 入力内容が空の場合は何もしない
        if (mEditText.getText().toString().equals("")) {
            return true;
        }
        // 前半はソフトウェアキーボードのEnterキーの判定、後半は物理キーボードでの判定
        if (event == null || (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
            // Todoデータを登録
            registerTodo();
        }
        return true;
    }

    /**
     * EventBusからの通知の購読
     *
     * @param event EventBusで発行されたイベント
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(TodoModelChangedEvent event) {
        // 画面の表示を更新
        updateView();
    }

    /**
     * 画面での入力内容をRealmへ登録するPrivateメソッド
     */
    private void registerTodo() {
        // Todoデータを作成
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setText(mEditText.getText().toString());
        // データ操作モデルを通して登録
        mTodoModel.createOrUpdate(todoEntity);
        // 入力内容は空にする
        mEditText.setText(null);
        // ソフトウェアキーボードを隠す
        ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 画面の表示を更新するPrivateメソッド
     */
    private void updateView() {
        // データセットの変更があった旨をAdapterへ通知
        mTodoListAdapter.notifyDataSetChanged();
        // Todoデータの件数を更新
        mTextView.setText(String.valueOf(mTodoModel.getSize()));
    }
}
