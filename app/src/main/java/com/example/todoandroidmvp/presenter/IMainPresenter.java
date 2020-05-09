package com.example.todoandroidmvp.presenter;

import android.content.Context;

public interface IMainPresenter {
    void onAddTodo(String text);
    void onRemoveTodo(String key);
    void onRequestData(Context context);
}
