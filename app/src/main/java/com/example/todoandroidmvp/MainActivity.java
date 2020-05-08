package com.example.todoandroidmvp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.todoandroidmvp.adapters.TodosAdapter;
import com.example.todoandroidmvp.fragments.FirstPageFragment;
import com.example.todoandroidmvp.interfaces.OnRemoveTodoClickedListener;
import com.example.todoandroidmvp.models.TodoModel;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements FirstPageFragment.OnFirstPageFragmentInteractionListener {

    private RequestQueue queue;
    private JSONObject jsonData;
    private ArrayList<Long> timestamps;
    private TodosAdapter todosAdapter;
    private FragmentManager fragmentManager;
    private EditText edt_input;
    private ImageView btn_add_todo;
    private ConstraintLayout progress_bar_container;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        edt_input = (EditText)findViewById(R.id.edt_input);
        btn_add_todo = (ImageView)findViewById(R.id.btn_add);
        progress_bar_container = (ConstraintLayout)findViewById(R.id.progress_bar_container);

        timestamps = new ArrayList<>();
        jsonData = new JSONObject();
        fragmentManager = this.getSupportFragmentManager();

        OnRemoveTodoClickedListener onRemoveTodoClickedListener = new OnRemoveTodoClickedListener() {
            @Override
            public void onRemoveTodoClicke(String key) {
                removeTodo(key);
            }
        };

        btn_add_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTodo(edt_input.getText().toString());
                edt_input.setText("");
            }
        });

        edt_input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (!TextUtils.isEmpty(edt_input.getText())) {
                        addTodo(edt_input.getText().toString());
                        edt_input.setText("");
                    }
                }
                return false;
            }
        });

        // Setup adapter end recyclerView
        todosAdapter = new TodosAdapter(jsonData, timestamps, onRemoveTodoClickedListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(todosAdapter);

        // Request Data
        requestData();
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void requestData() {
        queue = Volley.newRequestQueue(this);
        String url ="https://us-central1-foxmike-test.cloudfunctions.net/todoDb/getTodo";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("null")) {
                            showFirstPageFragment();
                            progress_bar_container.setVisibility(View.GONE);
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
                                todosAdapter.updateData(jsonData, timestamps);
                                progress_bar_container.setVisibility(View.GONE);
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
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void showFirstPageFragment() {
        FirstPageFragment firstPageFragment = new FirstPageFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer,firstPageFragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onFirstTodoAdded(String todoText) {
        addTodo(todoText);
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).commit();

    }

    private void addTodo(String text) {
        hideKeyboard(btn_add_todo);
        long timestamp = new Date().getTime();
        HashMap<String, Object> todo = new HashMap<>();
        todo.put("text", text);
        todo.put("completed", false);
        todo.put("ts", timestamp);

        try {
            jsonData.put(Long.toString(timestamp), todo);
            timestamps.add(timestamp);
            todosAdapter.updateData(jsonData, timestamps);
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

    private void removeTodo(String text) {

        jsonData.remove(text);
        ArrayList<Long> newTimestamps = new ArrayList<>(timestamps);

        for (Long itTimestamp: timestamps) {
            if (Long.parseLong(text)==itTimestamp) {
                newTimestamps.remove(itTimestamp);
            }
        }
        timestamps = new ArrayList<>(newTimestamps);
        todosAdapter.updateData(jsonData, timestamps);
        removeTodoFromDatabase(text);
        if (timestamps.size()==0) {
            showFirstPageFragment();
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