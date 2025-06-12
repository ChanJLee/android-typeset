package me.chan.texas;

import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import me.chan.texas.debug.R;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.ui.text.ParagraphView;

public class BenchmarkActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_benchmark);
		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
			Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
			return insets;
		});

		TextView textView = findViewById(R.id.raw);
		TextPaint textPaint = textView.getPaint();
		Log.d("BenchmarkActivity", "textPaint: " + textPaint.getTextSize() + " -> " + textPaint.getFontMetrics());


		ParagraphView paragraphView = findViewById(R.id.paragraph);
		RenderOption option = paragraphView.createRendererOption();
		Log.d("BenchmarkActivity", "option: " + option.getTextSize());
	}
}