package io.github.hkusu.realmapp;

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

public class MainFragment extends Fragment {
    @Bind(R.id.editText) EditText mEditText;
    @Bind(R.id.button)   Button   mButton;
    @Bind(R.id.textView) TextView mTextView;
    @Bind(R.id.listView) ListView mListView;

    private Realm mRealm;
    private TodoModel mTodoModel;
    private TodoListAdapter mTodoListAdapter;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(getActivity());
        EventBus eventBus = EventBus.getDefault();
        mTodoModel = new TodoModel(getActivity(), mRealm, eventBus);
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
        mTodoListAdapter = new TodoListAdapter(
                getActivity(),
                R.layout.adapter_user_list,
                mTodoModel.get(),
                mTodoModel
        );
        mListView.setAdapter(mTodoListAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
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
        mRealm.close();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.button)
    public void onButtonClick() {
        if (mEditText.getText().toString().equals("")) {
            return;
        }
        registerTodo();
    }

    @SuppressWarnings("unused")
    @OnEditorAction(R.id.editText)
    public boolean onEditTextEditorAction(KeyEvent event) {
        if (mEditText.getText().toString().equals("")) {
            return true;
        }
        if (event == null || (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
            registerTodo();
        }
        return true;
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(TodoModelChangedEvent event) {
        updateView();
    }

    private void registerTodo() {
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setText(mEditText.getText().toString());
        mTodoModel.createOrUpdate(todoEntity);
        mEditText.setText(null);
        ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    private void updateView() {
        mTodoListAdapter.notifyDataSetChanged();
        mTextView.setText(String.valueOf(mTodoModel.getSize()));
    }
}
