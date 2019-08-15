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
		typesetView.setText("Unfortunately there is no API to retrieve the positions of the line breaks the browser inserted, so we'll have to resort to some trickery. By wrapping each word in an invisible <code>&lt;span&gt;</code> element and retrieving its <code>y</code> position we can find out when a new line starts. If the <code>y</code> position of the current word is different from the previous word we know a new line has started. This way a paragraph is split up in several individual lines.");
	}
}
