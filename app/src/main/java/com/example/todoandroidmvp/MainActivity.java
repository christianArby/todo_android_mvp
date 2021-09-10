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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.todoandroidmvp.adapters.TodosAdapter;
import com.example.todoandroidmvp.fragments.FirstPageFragment;
import com.example.todoandroidmvp.interfaces.OnRemoveTodoClickedListener;
import com.example.todoandroidmvp.presenter.IMainPresenter;
import com.example.todoandroidmvp.presenter.MainPresenter;
import com.example.todoandroidmvp.view.IMainView;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FirstPageFragment.OnFirstPageFragmentInteractionListener, IMainView {

    private TodosAdapter todosAdapter;
    private FragmentManager fragmentManager;
    private EditText edt_input;
    private ImageView btn_add_todo;
    private ConstraintLayout progress_bar_container;
    private RecyclerView recyclerView;

    IMainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        edt_input = (EditText)findViewById(R.id.edt_input);
        btn_add_todo = (ImageView)findViewById(R.id.btn_add);
        progress_bar_container = (ConstraintLayout)findViewById(R.id.progress_bar_container);

        mainPresenter = new MainPresenter(this);
        fragmentManager = this.getSupportFragmentManager();

        OnRemoveTodoClickedListener onRemoveTodoClickedListener = new OnRemoveTodoClickedListener() {
            @Override
            public void onRemoveTodoClicked(String key) {
                mainPresenter.onRemoveTodo(key);
                mainPresenter.
            }
        };

        btn_add_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainPresenter.onAddTodo(edt_input.getText().toString());
                edt_input.setText("");
            }
        });

        edt_input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (!TextUtils.isEmpty(edt_input.getText())) {
                        mainPresenter.onAddTodo(edt_input.getText().toString());
                        edt_input.setText("");
                    }
                }
                return false;
            }
        });

        todosAdapter = new TodosAdapter(new JSONObject(), new ArrayList<Long>(), onRemoveTodoClickedListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(todosAdapter);

        mainPresenter.onRequestData(getApplicationContext());
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showFirstPageFragment() {
        FirstPageFragment firstPageFragment = new FirstPageFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer,firstPageFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFirstTodoAdded(String todoText) {
        mainPresenter.onAddTodo(todoText);
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).commit();
        hideKeyboard(btn_add_todo);
    }

    @Override
    public void onAddTodoResult() {
        hideKeyboard(btn_add_todo);
    }

    @Override
    public void onNoDataAvailable() {
        showFirstPageFragment();
        progress_bar_container.setVisibility(View.GONE);
    }

    @Override
    public void onDataAvailable(JSONObject data, ArrayList<Long> timestamps) {
        todosAdapter.updateData(data, timestamps);
        progress_bar_container.setVisibility(View.GONE);
    }

    @Override
    public void setAdapterData(JSONObject data, ArrayList<Long> timestamps) {
        todosAdapter.updateData(data, timestamps);
    }
}