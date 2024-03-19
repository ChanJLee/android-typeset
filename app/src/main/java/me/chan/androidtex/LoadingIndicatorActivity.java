package me.chan.androidtex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import me.chan.texas.renderer.ui.indicator.LoadingIndicator;

public class LoadingIndicatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_indicator);

        LoadingIndicator loadingIndicator = findViewById(R.id.indicator);
        View view = findViewById(R.id.button);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingIndicator.renderLoading();
            }
        });
    }
}