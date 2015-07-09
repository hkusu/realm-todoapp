package io.github.hkusu.realmtodoapp;

import android.os.AsyncTask;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Todoデータ操作モデルクラス
 */
public class TodoModel {
    /** シングルトンインスタンス */
    private static TodoModel INSTANCE = new TodoModel();
    /** Realmのインスタンス(データ参照用) */
    private Realm mRealm = Realm.getDefaultInstance();

    /**
     * 利用元にシングルトンインスタンスを返す
     *
     * @return Todoデータ操作モデルのインスタンス
     */
    public static TodoModel getInstance() {
        return INSTANCE;
    }

    /**
     * コンストラクタ *外部からのインスタンス作成は禁止*
     */
    private TodoModel() {
    }

    /**
     * Todoデータ全件を取得(降順)
     *
     * @return TodoデータのList(RealmResult型であるためDBの変更内容は動的に反映される)
     */
    public List<TodoEntity> get() {
        return mRealm.allObjectsSorted(TodoEntity.class, TodoEntity.SORT_KEY, RealmResults.SORT_ORDER_DESCENDING);
    }

    /**
     * idをキーにTodoデータを取得 ※現状は未使用*
     *
     * @param id 検索対象のTodoデータのid
     * @return Todoデータ(1件)
     */
    public TodoEntity getById(int id) {
        return mRealm.where(TodoEntity.class)
                .equalTo(TodoEntity.PRIMARY_KEY, id)
                .findFirst();
    }

    /**
     * Todoデータを登録
     *
     * @param todoEntity 登録するTodoデータ
     * @return 成否
     */
    public boolean createOrUpdate(final TodoEntity todoEntity) {
        if (todoEntity.getId() == 0) {
            // 登録されているTodoデータの最大idを取得し、+1 したものをidとする(つまり連番)
            todoEntity.setId(getMaxId() + 1);
        }

        // 念のため別スレッドで非同期に実行
        new AsyncTask<TodoEntity, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(TodoEntity ... todoEntities) {
                // 現状でRealmインスタンスはスレッドをまたげないため新たにインスタンスを取得
                Realm realm = Realm.getDefaultInstance();
                // トランザクション開始
                realm.beginTransaction();
                try {
                    // idにプライマリキーを張ってあるため既に同一idのデータが存在していれば更新となる
                    realm.copyToRealmOrUpdate(todoEntities[0]);
                    // コミット
                    realm.commitTransaction();
                } catch (Exception e) {
                    // ロールバック
                    realm.cancelTransaction();
                    return false;
                } finally {
                    // 接続を閉じる
                    realm.close();
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccess) {
                super.onPostExecute(isSuccess);
                if (isSuccess) {
                    // データが変更された旨をEventBusで通知
                    EventBus.getDefault().post(new ChangedEvent());
                }
            }
        }.execute(todoEntity);

        return true;
    }

    /**
     * idをキーにTodoデータを削除
     *
     * @param id 削除対象のTodoデータのid
     * @return 成否
     */
    public boolean removeById(int id) {
        // 念のため別スレッドで非同期に実行
        new AsyncTask<Integer, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Integer... ids) {
                // 現状でRealmインスタンスはスレッドをまたげないため新たにインスタンスを取得
                Realm realm = Realm.getDefaultInstance();
                // トランザクション開始
                realm.beginTransaction();
                try {
                    // idに一致するレコードを削除
                    realm.where(TodoEntity.class).equalTo(TodoEntity.PRIMARY_KEY, ids[0]).findAll().clear();
                    // コミット
                    realm.commitTransaction();
                } catch (Exception e) {
                    // ロールバック
                    realm.cancelTransaction();
                    return false;
                } finally {
                    // 接続を閉じる
                    realm.close();
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccess) {
                super.onPostExecute(isSuccess);
                if (isSuccess) {
                    // データが変更された旨をEventBusで通知
                    EventBus.getDefault().post(new ChangedEvent());
                }
            }
        }.execute(id);

        return true;
    }

    /**
     * Todoデータの件数を取得
     *
     * @return 件数
     */
    public int getSize() {
        return mRealm.allObjects(TodoEntity.class).size();
    }

    /**
     * 登録されているTodoデータの最大idを取得
     *
     * @return 最大id
     */
    private int getMaxId() {
        return mRealm.where(TodoEntity.class).findAll().max(TodoEntity.PRIMARY_KEY).intValue();
    }

    /**
     * Realmの接続を切断 *以降は利用できなくなるので注意*
     */
    public void closeRealm() {
        mRealm.close();
    }

    /**
     * EventBus用のイベントクラス
     */
    public static class ChangedEvent {
        // 特に渡すデータは無し
    }
}
