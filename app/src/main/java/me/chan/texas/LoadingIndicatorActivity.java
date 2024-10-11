package me.chan.texas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import me.chan.texas.renderer.ui.indicator.LoadingIndicator;

public class LoadingIndicatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(me.chan.texas.debug.R.layout.activity_loading_indicator);

        LoadingIndicator loadingIndicator = findViewById(me.chan.texas.debug.R.id.indicator);
        View view = findViewById(me.chan.texas.debug.R.id.button);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingIndicator.renderLoading();
            }
        });
    }
}