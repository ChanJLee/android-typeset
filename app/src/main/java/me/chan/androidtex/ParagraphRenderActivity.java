package me.chan.androidtex;

import static me.chan.texas.renderer.ParagraphVisitor.SIG_STOP_PARA_VISIT;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.chan.androidtex.api.NiceBookApiService;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.source.ObjectSource;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;

public class ParagraphRenderActivity extends AppCompatActivity {
	private TexasView mTexasView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paragraph_render);

		// 设置整个渲染窗口的padding
		mTexasView = findViewById(R.id.text);
		mTexasView.setRendererPadding(32, 64, 32, 64);

		updateStyle();
		setupSidebar();
		setupScrollBar();
		setupClickPredicate();
		setupData();
	}

	private void updateStyle() {
		RenderOption renderOption = mTexasView.createRendererOption();
		renderOption.setTypeface(Typeface.createFromAsset(getAssets(), "opposans_b.ttf"));
		renderOption.setLineSpace(12);
		renderOption.setTextSize(this, TypedValue.COMPLEX_UNIT_SP, 18);
		renderOption.setBreakStrategy(BreakStrategy.BALANCED);
		mTexasView.refresh(renderOption);
	}

	private void setupSidebar() {
		final int segmentPaddingVertical = 48;
		final int segmentPaddingHorizontal = 32;
		mTexasView.setSegmentDecoration(new TexasView.SegmentDecoration() {
			@Override
			public void onDecorateSegment(int index, int count, Segment segment, Document document, Rect outRect) {
				if (index == 0) {
					outRect.set(segmentPaddingHorizontal, 0, segmentPaddingHorizontal, segmentPaddingVertical);
				} else if (index == count - 1) {
					outRect.set(segmentPaddingHorizontal, segmentPaddingVertical, segmentPaddingHorizontal, 0);
				} else {
					outRect.set(segmentPaddingHorizontal, segmentPaddingVertical, segmentPaddingHorizontal, segmentPaddingVertical);
				}
			}
		});
		mTexasView.setParagraphDecor(new ParagraphDecor() {
			@Override
			protected void onPreDrawDecor(Paragraph paragraph, Rect viewportOuter, Rect viewportInner) {

			}

			@Override
			protected int onLayoutDecor(Paragraph paragraph, Object spanTag, RectF spanOuter, RectF spanInner, Rect decorOuter, Rect decorInner) {
				return SIG_STOP_PARA_VISIT;
			}

			@Override
			protected void onDrawDecor(Canvas canvas, Paragraph paragraph, Rect decorOuter, Rect decorInner) {

			}

			@Override
			protected boolean onTouchEvent(MotionEvent event, Paragraph paragraph, Rect decorOuter, Rect decorInner) {
				return false;
			}
		});
	}

	/**
	 * 加载数据
	 */
	private void setupData() {
		Intent intent = getIntent();
		NiceBookApiService.getInstance().fetchSection(intent.getStringExtra(KEY_BOOK), intent.getStringExtra(KEY_SECTION))
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Consumer<Section>() {
					@Override
					public void accept(Section section) throws Exception {
						SectionAdapter adapter = new SectionAdapter(ParagraphRenderActivity.this);
						mTexasView.setAdapter(adapter);
						adapter.setSource(new ObjectSource<>(section));
					}
				});
	}

	private void setupClickPredicate() {
		// 没别的要求，一定要快！！！不然会ANR ⚠️

		// 单机谓词 判断 单机时哪些单词要被高亮
		mTexasView.setOnSpanClickedPredicate(new TexasView.OnSpanClickedPredicate() {
			@Override
			public boolean apply(@Nullable Object clickedTag, @Nullable Object tag) {
				return false;
			}
		});

		// 长按谓词
		mTexasView.setOnSpanLongClickedPredicate(new TexasView.OnSpanLongClickedPredicate() {
			@Override
			public boolean apply(@Nullable Object clickedTag, @Nullable Object tag) {
				return false;
			}
		});
	}

	/**
	 * 就是滚动条
	 */
	private void setupScrollBar() {
		mTexasView.setScrollBarDrawable(null);
	}

	// 有些机型有问题，息屏后渲染会空白
	@Override
	protected void onStart() {
		super.onStart();
		if (mTexasView != null) {
			mTexasView.resume();
		}
	}

	@Override
	protected void onStop() {
		if (mTexasView != null) {
			mTexasView.pause();
		}
		super.onStop();
	}

	/**
	 * 释放资源
	 */
	@Override
	protected void onDestroy() {
		if (mTexasView != null) {
			mTexasView.release();
		}
		super.onDestroy();
	}

	private static final String KEY_BOOK = "book";
	private static final String KEY_SECTION = "section";
	private static final String KEY_DURATION = "duration";
	public static Intent createIntent(Context context, String bookId, String sectionId, long durationMs) {
		Intent intent = new Intent(context, ParagraphRenderActivity.class);
		intent.putExtra(KEY_BOOK, bookId);
		intent.putExtra(KEY_SECTION, sectionId);
		intent.putExtra(KEY_DURATION, durationMs);
		return intent;
	}
}