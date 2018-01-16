package com.maginawin.customprogressbardemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView);
        CustomProgressBar progressBar = findViewById(R.id.customProgressBar);
        progressBar.setListener((bar, value) -> textView.setText(String.format("%.2f%%", value)));
    }
}
