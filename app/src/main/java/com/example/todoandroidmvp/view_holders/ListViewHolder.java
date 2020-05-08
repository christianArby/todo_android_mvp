package com.example.todoandroidmvp.view_holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoandroidmvp.R;
import com.example.todoandroidmvp.interfaces.OnRemoveTodoClickedListener;

public class ListViewHolder extends RecyclerView.ViewHolder {

    View mView;



    public ListViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setTodoText(String todoText) {
        TextView todoTV = mView.findViewById(R.id.todoText);
        todoTV.setText(todoText);
    }

    public void setOnRemoveTodoClickListener(final String key, final OnRemoveTodoClickedListener onRemoveTodoClickListener) {
        ImageView btn_remove_todo = mView.findViewById(R.id.btn_remove);
        btn_remove_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRemoveTodoClickListener.onRemoveTodoClicke(key);
            }
        });

    }
}
