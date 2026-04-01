package me.chan.texas;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import me.chan.texas.debug.R;
import me.chan.texas.renderer.ui.text.ParagraphView;

public class ParagraphMeasureBenchmarkActivity extends AppCompatActivity {
	private static final String TAG = "MeasureBenchmark";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EdgeToEdge.enable(this);
		setContentView(R.layout.activity_paragraph_measure_benchmark);
		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
			Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
			return insets;
		});

		findViewById(R.id.main).post(() -> collectMeasureResults(getWindow().getDecorView()));
	}

	private void collectMeasureResults(View view) {
		if (view instanceof ParagraphView) {
			ParagraphView pv = (ParagraphView) view;
			Log.d(TAG, String.format("%-30s  measured: %5d × %-5d  padding: [%d, %d, %d, %d]",
					pv.getTag(),
					pv.getMeasuredWidth(), pv.getMeasuredHeight(),
					pv.getPaddingLeft(), pv.getPaddingTop(),
					pv.getPaddingRight(), pv.getPaddingBottom()));
		}
		if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) view;
			for (int i = 0; i < vg.getChildCount(); i++) {
				collectMeasureResults(vg.getChildAt(i));
			}
		}
	}
}
