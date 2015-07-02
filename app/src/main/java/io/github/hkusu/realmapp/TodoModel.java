package io.github.hkusu.realmapp;

import android.content.Context;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;

public class TodoModel {

    private final Realm mRealm;
    private final EventBus mEventBus;

    public TodoModel(Realm realm, EventBus eventBus) {
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

    public boolean createOrUpdate(TodoEntity todoEntity) {
        if (todoEntity.getId() == 0) {
            todoEntity.setId(getMaxId() + 1);
        }
        mRealm.beginTransaction();
        try {
            mRealm.copyToRealmOrUpdate(todoEntity);
        } catch (Exception e) {
            mRealm.cancelTransaction();
            return false;
        }
        mRealm.commitTransaction();
        mEventBus.post(new TodoModelChangedEvent());
        return true;
    }

    public boolean removeById(int id) {
        mRealm.beginTransaction();
        try {
            mRealm.where(TodoEntity.class).equalTo(TodoEntity.PRIMARY_KEY, id).findAll().clear();
        } catch (Exception e) {
            mRealm.cancelTransaction();
            return false;
        }
        mRealm.commitTransaction();
        mEventBus.post(new TodoModelChangedEvent());
        return true;
    }

    public int getSize() {
        return mRealm.allObjects(TodoEntity.class).size();
    }

    private int getMaxId() {
        return mRealm.where(TodoEntity.class).findAll().max(TodoEntity.PRIMARY_KEY).intValue();
    }
}
