package io.github.hkusu.realmtodoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TodoListAdapter extends ArrayAdapter<TodoEntity> {
    //private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mResource;
    private TodoModel mTodoModel;

    // コンストラクタ
    public TodoListAdapter(Context context, int resource, List<TodoEntity> objects, TodoModel todoModel) {
        super(context, resource, objects);
        //mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mResource = resource;
        mTodoModel = todoModel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView != null) {
            viewHolder = (ViewHolder)convertView.getTag();
        } else {
            convertView = mLayoutInflater.inflate(mResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        TodoEntity todoEntity = getItem(position);
        viewHolder.mTextView.setText(todoEntity.getText());

        viewHolder.id = todoEntity.getId();

        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.textView) TextView mTextView;
        @Bind(R.id.button)   Button   mButton;

        int id;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @SuppressWarnings("unused")
        @OnClick(R.id.button)
        public void onButtonClick() {
            mTodoModel.removeById(id);
        }
    }
}


