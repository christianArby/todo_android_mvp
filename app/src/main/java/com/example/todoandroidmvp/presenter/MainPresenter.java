package com.example.todoandroidmvp.presenter;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.todoandroidmvp.models.TodoModel;
import com.example.todoandroidmvp.view.IMainView;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainPresenter implements IMainPresenter{

    private IMainView mainView;
    private RequestQueue queue;
    private JSONObject jsonData;
    private ArrayList<Long> timestamps;

    public MainPresenter(IMainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void onRequestData(Context context) {
        timestamps = new ArrayList<>();
        jsonData = new JSONObject();
        queue = Volley.newRequestQueue(context);
        String url ="https://us-central1-foxmike-test.cloudfunctions.net/todoDb/getTodo";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("null")) {
                            mainView.onNoDataAvailable();
                        } else {
                            try {
                                jsonData = new JSONObject(response);
                                Gson gson = new Gson();
                                JSONArray keys = jsonData.names();
                                for (int i = 0; i < keys.length(); i++) {
                                    String key = keys.getString(i);
                                    TodoModel todoModel = gson.fromJson(jsonData.get(key).toString(), TodoModel.class);
                                    timestamps.add(todoModel.getTs());
                                }
                                mainView.onDataAvailable(jsonData, timestamps);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }


    @Override
    public void onAddTodo(String text) {
        long timestamp = new Date().getTime();
        HashMap<String, Object> todo = new HashMap<>();
        todo.put("text", text);
        todo.put("completed", false);
        todo.put("ts", timestamp);

        try {
            jsonData.put(Long.toString(timestamp), todo);
            timestamps.add(timestamp);
            mainView.setAdapterData(jsonData, timestamps);
            mainView.onAddTodoResult();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addTodoToDatabase(text, timestamp);
    }

    private void addTodoToDatabase(String text, long ts) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ts", ts);
        params.put("text", text);
        params.put("completed", false);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                "https://us-central1-foxmike-test.cloudfunctions.net/todoDb/addTodo", new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("JSONPost", response.toString());
                        //pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("JSONPost", "Error: " + error.getMessage());
                //pDialog.hide();
            }
        });
        queue.add(jsonObjReq);
    }

    @Override
    public void onRemoveTodo(String key) {
        jsonData.remove(key);
        ArrayList<Long> newTimestamps = new ArrayList<>(timestamps);
        for (Long itTimestamp: timestamps) {
            if (Long.parseLong(key)==itTimestamp) {
                newTimestamps.remove(itTimestamp);
            }
        }
        timestamps = new ArrayList<>(newTimestamps);
        mainView.setAdapterData(jsonData, timestamps);
        removeTodoFromDatabase(key);
        if (timestamps.size()==0) {
            mainView.onNoDataAvailable();
        }
    }

    private void removeTodoFromDatabase(String text) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ts", text);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                "https://us-central1-foxmike-test.cloudfunctions.net/todoDb/removeTodo", new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("JSONPost", response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("JSONPost", "Error: " + error.getMessage());
            }
        });
        queue.add(jsonObjReq);
    }
}
