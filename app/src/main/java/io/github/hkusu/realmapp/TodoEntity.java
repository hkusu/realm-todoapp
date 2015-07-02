package io.github.hkusu.realmapp;

import android.text.Editable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TodoEntity extends RealmObject {

    public static final String PRIMARY_KEY = "id";
    public static final String SORT_KEY = "id";

    @PrimaryKey
    private int id;
    private String text;

    // RealmObject に於いて 引数なしの Default Constructor を public で定義するがある
    public TodoEntity() {
    }

    public TodoEntity(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
