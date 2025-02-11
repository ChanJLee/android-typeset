package me.chan.texas.renderer.ui.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;

import me.chan.texas.R;
import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.AndroidMeasurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.HyphenStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.layout.Box;
import me.chan.texas.utils.TexasUtils;

public class EditableParagraphView extends FrameLayout {

	private final PaintSet mPaintSet;
	private final AndroidMeasurer mMeasurer;
	private final TextAttribute mTextAttribute;
	private final TextureParagraphView0 mRender;
	private RenderOption mRenderOption;
	private final SpanTouchEventHandler mSpanTouchEventHandler = new SpanTouchEventHandler() {
		@Override
		public boolean isSpanClickable(@Nullable Object tag) {
			return false;
		}

		@Override
		public boolean applySpanClicked(@Nullable Object clickedTag, @Nullable Object otherTag) {
			return false;
		}

		@Override
		public boolean applySpanLongClicked(@Nullable Object clickedTag, @Nullable Object otherTag) {
			return false;
		}
	};

	public EditableParagraphView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public EditableParagraphView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		@SuppressLint("CustomViewStyleable") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.me_chan_texas_EditableParagraphView, defStyleAttr, 0);
		try {
			mRenderOption = createRenderOption(context, typedArray);
			mPaintSet = new PaintSet(mRenderOption);
			mMeasurer = new AndroidMeasurer(mPaintSet);
			mTextAttribute = new TextAttribute(mMeasurer);
			mRender = new TextureParagraphView0(context);
			addView((View) mRender, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
			OnSelectedChangedListener onSelectedChangedListener = new OnSelectedChangedListener() {
				@Override
				public boolean onSegmentClicked(TouchEvent e, Paragraph paragraph, int eventType) {
					return false;
				}

				@Override
				public boolean onBoxSelected(TouchEvent e, Paragraph paragraph, @EventType int eventType, Box box) {
					return false;
				}
			};
			mRender.setOnTextSelectedListener(onSelectedChangedListener);

			String text = typedArray.getString(R.styleable.me_chan_texas_EditableParagraphView_me_chan_texas_EditableParagraphView_text);
			if (!TextUtils.isEmpty(text)) {
				setText(text);
			}
			checkUIThreadPriority();
		} finally {
			typedArray.recycle();
		}
	}

	private RenderOption createRenderOption(Context context, TypedArray typedArray) {
		Resources resources = getResources();
		RenderOption renderOption = new RenderOption();

		// 设置字体颜色
		renderOption.setTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_EditableParagraphView_me_chan_texas_EditableParagraphView_textColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_text_color)
				)
		);

		// 设置字体
		renderOption.setTypeface(Texas.getDefaultTypeface());
		String typefacePath = typedArray.getString(R.styleable.me_chan_texas_EditableParagraphView_me_chan_texas_EditableParagraphView_typefaceAssets);
		if (!TextUtils.isEmpty(typefacePath)) {
			WeakReference<Typeface> typefaceWeakReference = TexasView.TYPEFACE_CACHE.get(typefacePath);
			Typeface typeface;
			if (typefaceWeakReference != null && (typeface = typefaceWeakReference.get()) != null) {
				renderOption.setTypeface(typeface);
			} else {
				typeface = TexasUtils.createTypefaceFromAsset(context, typefacePath);
				typefaceWeakReference = new WeakReference<>(typeface);
				TexasView.TYPEFACE_CACHE.put(typefacePath, typefaceWeakReference);
				renderOption.setTypeface(typeface);
			}
		}

		// 设置字体大小
		renderOption.setTextSize(
				typedArray.getDimension(R.styleable.me_chan_texas_EditableParagraphView_me_chan_texas_EditableParagraphView_textSize,
						TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_SP,
								TexasView.DEFAULT_TEXT_SIZE,
								resources.getDisplayMetrics()
						)
				)
		);

		// 行间距
		renderOption.setLineSpace(
				typedArray.getDimension(R.styleable.me_chan_texas_EditableParagraphView_me_chan_texas_EditableParagraphView_lineSpace,
						TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP,
								TexasView.DEFAULT_LINE_SPACE,
								resources.getDisplayMetrics()
						)
				)
		);

		// 选中字体的背景色
		renderOption.setSelectedBackgroundColor(
				typedArray.getColor(R.styleable.me_chan_texas_EditableParagraphView_me_chan_texas_EditableParagraphView_selectedBackgroundColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_theme_color)
				)
		);

		// 选中字体的颜色
		renderOption.setSelectedTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_EditableParagraphView_me_chan_texas_EditableParagraphView_selectedTextColor, Color.WHITE)
		);

		// 选中span的背景色
		renderOption.setSelectedByLongClickBackgroundColor(
				ContextCompat.getColor(context, R.color.me_chan_texas_span_bg_color)
		);

		// 选中span的字体颜色
		renderOption.setSelectedByLongClickTextColor(
				ContextCompat.getColor(context, R.color.me_chan_texas_text_color)
		);

		// 断字策略
		int breakStrategy = typedArray.getInt(R.styleable.me_chan_texas_EditableParagraphView_me_chan_texas_EditableParagraphView_breakStrategy, TexasView.BREAK_STRATEGY_BALANCE);
		renderOption.setBreakStrategy(
				breakStrategy == TexasView.BREAK_STRATEGY_SIMPLE ?
						BreakStrategy.SIMPLE : BreakStrategy.BALANCED
		);

		// 是否可选单词
		renderOption.setWordSelectable(false);

		// 断字策略
		int hyphenStrategy = typedArray.getInt(R.styleable.me_chan_texas_EditableParagraphView_me_chan_texas_EditableParagraphView_hyphenStrategy, TexasView.HYPHEN_STRATEGY_US);
		renderOption.setHyphenStrategy(
				hyphenStrategy == TexasView.HYPHEN_STRATEGY_UK ?
						HyphenStrategy.UK : HyphenStrategy.US
		);

		// lazy 渲染模式优化
		renderOption.setEnableLazyRender(
				false
		);

		// 高亮span文字颜色
		renderOption.setSpanHighlightTextColor(
				ContextCompat.getColor(context, R.color.me_chan_texas_theme_color)
		);

		// 加载中背景色
		renderOption.setLoadingBackgroundColor(
				ContextCompat.getColor(context, R.color.me_chan_texas_loading_bg)
		);

		// 自由划线水滴颜色
		renderOption.setDragViewColor(
				ContextCompat.getColor(context, R.color.me_chan_texas_drag_view_color)
		);

		// 设置选中圆角半径
		renderOption.setSelectedBackgroundRoundRadius(
				typedArray.getDimension(
						R.styleable.me_chan_texas_EditableParagraphView_me_chan_texas_EditableParagraphView_selectedBackgroundRoundRadius,
						TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP,
								3,
								getResources().getDisplayMetrics()
						)
				)
		);

		// 是否开启兼容模式
		renderOption.setCompatMode(false);

		return renderOption;
	}

	public void setText(@NonNull CharSequence text) {
		setText(text, 0, text.length());
	}

	public void setText(@NonNull CharSequence text, int start, int end) {
		TexasOption option = new TexasOption(Hyphenation.getInstance(), mMeasurer, mTextAttribute, mRenderOption);
		Paragraph paragraph = Paragraph.Builder.newBuilder(option)
				.text(text, start, end)
				.build();
		mRender.render(paragraph, mPaintSet, mRenderOption, null, mSpanTouchEventHandler);
	}

	private static void checkUIThreadPriority() {
		// On Android 8+, UI thread's priority already increase from 0 to -10(THREAD_PRIORITY_VIDEO),
		// higher than URGENT_DISPLAY (-8), we at least adjust to URGENT_DISPLAY when on 7 or under,
		// and it will help to improve TextureView performance
		try {
			int priority = Process.getThreadPriority(0);
			if (priority <= Process.THREAD_PRIORITY_URGENT_DISPLAY) {
				Log.i("Texas", "UI thread priority=" + priority + ", don't need to raise!");
				return;
			}

			Log.i("Texas", "UI thread priority=" + priority + ", need to raise!");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
				Process.setThreadPriority(Process.THREAD_PRIORITY_VIDEO);
			} else {
				Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY);
			}
		} catch (Throwable t) {
			Log.w("Texas", t);
		}
	}
}
