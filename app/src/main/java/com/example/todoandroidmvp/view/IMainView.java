package com.example.todoandroidmvp.view;

import org.json.JSONObject;

import java.util.ArrayList;

public interface IMainView {
    void onAddTodoResult();
    void onNoDataAvailable();
    void onDataAvailable(JSONObject data, ArrayList<Long> timestamps);
    void setAdapterData(JSONObject data, ArrayList<Long> timestamps);
}
