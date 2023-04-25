package com.github.cdflynn.android.checksample;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import nl.schmit.animationView.CrossView;
import nl.schmit.animationView.ExclamationView;

public class MainActivity extends AppCompatActivity {

    static class Views {

        View button;
        ExclamationView check;

        Views(MainActivity activity) {
            button = activity.findViewById(R.id.button);
            check = (ExclamationView) activity.findViewById(R.id.check);
        }
    }

    private Views mViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViews = new Views(this);
        mViews.button.setOnClickListener(mClickListener);
    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mViews.check.check();
        }
    };
}
