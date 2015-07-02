package io.github.hkusu.realmapp;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;

public class TodoModel {
    private final Context mContext;
    private final Realm mRealm;
    private final EventBus mEventBus;

    public TodoModel(Context context, Realm realm, EventBus eventBus) {
        mContext = context;
        mRealm = realm;
        mEventBus = eventBus;
    }

    public List<TodoEntity> get() {
        return mRealm.allObjectsSorted(TodoEntity.class, TodoEntity.SORT_KEY, RealmResults.SORT_ORDER_ASCENDING);
    }

    // 未使用
    public TodoEntity getById(int id) {
        return mRealm.where(TodoEntity.class)
                .equalTo(TodoEntity.PRIMARY_KEY, id)
                .findFirst();
    }

    public boolean createOrUpdate(final TodoEntity todoEntity) {
        if (todoEntity.getId() == 0) {
            todoEntity.setId(getMaxId() + 1);
        }

        new AsyncTask<TodoEntity, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(TodoEntity ... todoEntities) {
                Realm realm = Realm.getInstance(mContext);
                realm.beginTransaction();
                try {
                    realm.copyToRealmOrUpdate(todoEntities[0]);
                } catch (Exception e) {
                    realm.cancelTransaction();
                    realm.close();
                    return false;
                }
                realm.commitTransaction();
                realm.close();
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccess) {
                super.onPostExecute(isSuccess);
                if (isSuccess) {
                    mEventBus.post(new TodoModelChangedEvent());
                }
            }
        }.execute(todoEntity);
        return true;
    }

    public boolean removeById(int id) {
        new AsyncTask<Integer, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Integer... ids) {
                Realm realm = Realm.getInstance(mContext);
                realm.beginTransaction();
                try {
                    realm.where(TodoEntity.class).equalTo(TodoEntity.PRIMARY_KEY, ids[0]).findAll().clear();
                } catch (Exception e) {
                    realm.cancelTransaction();
                    realm.close();
                    return false;
                }
                realm.commitTransaction();
                realm.close();
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccess) {
                super.onPostExecute(isSuccess);
                if (isSuccess) {
                    mEventBus.post(new TodoModelChangedEvent());
                }
            }
        }.execute(id);
        return true;
    }

    public int getSize() {
        return mRealm.allObjects(TodoEntity.class).size();
    }

    private int getMaxId() {
        return mRealm.where(TodoEntity.class).findAll().max(TodoEntity.PRIMARY_KEY).intValue();
    }
}
