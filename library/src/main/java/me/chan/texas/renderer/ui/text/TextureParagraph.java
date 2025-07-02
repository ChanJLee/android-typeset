package me.chan.texas.renderer.ui.text;

import android.graphics.Canvas;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.text.Paragraph;
import me.chan.texas.utils.concurrency.Worker;


@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface TextureParagraph {
	
	@Nullable
	Canvas lockCanvas(int width, int height);

	
	void unlockCanvasAndPost(Canvas canvas);

	
	void getLocationOnScreen(int[] location);

	
	void render(@NonNull Paragraph paragraph,
				@NonNull PaintSet paintSet,
				@NonNull RenderOption renderOption,
				@Nullable ParagraphDecor decor,
				@Nullable SpanTouchEventHandler spanClickedEventHandler);

	
	void setOnTextSelectedListener(OnSelectedChangedListener onTextSelectedListener);

	
	Worker.Token getToken();

	
	void syncUI();

	
	Paragraph getParagraph();

	void clear();

	int getHeight();

	ViewGroup.LayoutParams getLayoutParams();

	void setLayoutParams(ViewGroup.LayoutParams layoutParams);

	void setOnMeasureInterceptor(OnMeasureInterceptor interceptor);
}
