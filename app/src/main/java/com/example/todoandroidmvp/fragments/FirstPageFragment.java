package com.example.todoandroidmvp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.todoandroidmvp.R;

public class FirstPageFragment extends Fragment {

    private EditText text;
    private ImageView inputButton;
    private OnFirstPageFragmentInteractionListener mListener;

    public FirstPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first_page, container, false);
        text = view.findViewById(R.id.text);
        inputButton = view.findViewById(R.id.inputButton);

        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(text.getText())) {
                    mListener.onFirstTodoAdded(text.getText().toString());
                }
            }
        });
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (!TextUtils.isEmpty(text.getText())) {
                        mListener.onFirstTodoAdded(text.getText().toString());
                    }
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFirstPageFragmentInteractionListener) {
            mListener = (OnFirstPageFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFirstPageFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFirstPageFragmentInteractionListener {
        void onFirstTodoAdded(String todoText);
    }
}
