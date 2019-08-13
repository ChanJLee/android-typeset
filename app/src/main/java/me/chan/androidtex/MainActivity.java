package me.chan.androidtex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import me.chan.typeset.TypesetView;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TypesetView typesetView = findViewById(R.id.view);
		typesetView.setText("hello word");
	}
}
