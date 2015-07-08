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
import de.greenrobot.event.EventBus;

/**
 * Todoデータ表示用のListAdapterクラス
 */
public class TodoListAdapter extends ArrayAdapter<TodoEntity> {
    //private Context mContext;
    /** LayoutInflater(Android) */
    private final LayoutInflater mLayoutInflater;
    /** レイアウトXMLファイルのid */
    private final int mResource;

    public TodoListAdapter(Context context, int resource, List<TodoEntity> objects) {
        super(context, resource, objects);
        //mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        // 一度生成したViewを再利用
        if (convertView != null) {
            viewHolder = (ViewHolder)convertView.getTag();
        } else {
            convertView = mLayoutInflater.inflate(mResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        // この行のTodoデータを取得
        TodoEntity todoEntity = getItem(position);
        // Todoのテキストを表示
        viewHolder.mTextView.setText(todoEntity.getText());
        // [削除]ボタン用にidを保持
        viewHolder.id = todoEntity.getId();

        return convertView;
    }

    /**
     * ViewHolder
     */
    static class ViewHolder {
        @Bind(R.id.textView) TextView mTextView;
        @Bind(R.id.button)   Button   mButton;

        /** Todoデータのid */
        private int id;

        ViewHolder(View view) {
            ButterKnife.bind(this, view); // ButterKnife
        }

        /**
         * [削除]ボタン押下
         */
        @SuppressWarnings("unused")
        @OnClick(R.id.button)
        public void onButtonClick() {
            // EventBus経由でボタンが変更された旨を通知
            EventBus.getDefault().post(new RemoveButtonClickedEvent(id));
        }
    }

    /**
     * EventBus用のイベントクラス
     */
    public static class RemoveButtonClickedEvent {
        /** Todoデータのid */
        private int id;

        /**
         * コンストラクタ
         *
         * @param id 削除対象のTodoデータのid
         */
        public RemoveButtonClickedEvent(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}