package io.github.hkusu.realmapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private TodoModel mTodoModel;
    private TodoListAdapter mTodoListAdapter;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm realm = Realm.getInstance(getActivity());
        EventBus eventBus = EventBus.getDefault();
        mTodoModel = new TodoModel(realm, eventBus);
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

    @SuppressWarnings("unused")
    @OnClick(R.id.button)
    public void onButtonClick() {
        if (mEditText.getText().toString().equals("")) {
            return;
        }

        if (!mEditText.getText().toString().equals("")) {
            TodoEntity todoEntity = new TodoEntity();
            todoEntity.setText(mEditText.getText().toString());
            mTodoModel.createOrUpdate(todoEntity);
            mEditText.setText(null);
        }
    }

    @SuppressWarnings("unused")
    @OnEditorAction(R.id.editText)
    public boolean onEditTextEditorAction(KeyEvent event) {
        if (mEditText.getText().toString().equals("")) {
            return true;
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                TodoEntity todoEntity = new TodoEntity();
                todoEntity.setText(mEditText.getText().toString());
                mTodoModel.createOrUpdate(todoEntity);
                mEditText.setText(null);
            }
        }
        return true;
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(TodoModelChangedEvent event) {
        updateView();
    }

    private void updateView() {
        mTodoListAdapter.notifyDataSetChanged();
        mTextView.setText(String.valueOf(mTodoModel.getSize()));
    }
}
