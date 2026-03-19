package me.chan.texas;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import me.chan.texas.ext.image.Figure;
import me.chan.texas.renderer.OnClickedListenerAdapter;
import me.chan.texas.renderer.ParagraphPredicates;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.layout.Span;
import me.chan.texas.utils.TexasUtils;

public class TexasViewDemoActivity extends AppCompatActivity {

	private TexasView mTexasView;
	private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(me.chan.texas.debug.R.layout.activity_paragraph);
		mPaint.setColor(Color.RED);

		// 设置整个渲染窗口的padding
		mTexasView = findViewById(me.chan.texas.debug.R.id.text);
		mTexasView.setRendererPadding(100, 100, 30, 10);

		// 设置 decor
		// decor 就是渲染文本时，起装饰作用
		// 比如在这一段的边缘加 🔥 按钮
		// 或者设置 文本周围 的空白间距
		setupDecor();

		// 设置滚动条的样式
		setupScrollBar();

		// 设置壳子的debug信息，实际接入时无需关心
		setupDebug();

		// 高亮api接口演示
		setupHighlight();

		// 处理点击长按事件
		setupClickPredicate();

		// 设置点击事件
		setupListener();

		// 加载数据渲染
		setupData();

		// 更多api见 book parser
	}

	private void setupClickPredicate() {
		// 没别的要求，一定要快！！！不然会ANR ⚠️
		mTexasView.setSpanTouchEventHandler(new SpanTouchEventHandler() {
			@Override
			public boolean isSpanClickable(@NonNull Span box) {
				return box.getTag() != null;
			}

			@Override
			public boolean applySpanClicked(@NonNull Span clicked, @NonNull Span other) {
				return clicked.getTag() == other.getTag();
			}

			@Override
			public boolean applySpanLongClicked(@NonNull Span clicked, @NonNull Span other) {
				Object otherTag = other.getTag();
				if (otherTag == null) {
					return true;
				}

				Object clickedTag = clicked.getTag();
				if (clickedTag instanceof BookSource.SpanTag && otherTag instanceof BookSource.SpanTag) {
					BookSource.SpanTag lhs = (BookSource.SpanTag) clickedTag;
					BookSource.SpanTag rhs = (BookSource.SpanTag) otherTag;
					return TexasUtils.equals(lhs.sentId, rhs.sentId);
				}

				return false;
			}
		});
	}

	/**
	 * 点击事件
	 */
	void setupListener() {
		mTexasView.setOnClickedListener(new OnClickedListenerAdapter() {
			@Override
			public void onEmptyClicked(TexasView view, TouchEvent event) {
				Toast.makeText(TexasViewDemoActivity.this, "点击了空白", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSpanClicked(TexasView view, Paragraph paragraph, TouchEvent event, Span box) {
				Toast.makeText(TexasViewDemoActivity.this, "点击了Span", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSpanLongClicked(TexasView view, Paragraph paragraph, TouchEvent event, Span box) {
				Toast.makeText(TexasViewDemoActivity.this, "长按了Span", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSegmentClicked(TexasView view, TouchEvent event, Segment segment) {
				Toast.makeText(TexasViewDemoActivity.this, "点击了Segment", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSegmentDoubleClicked(TexasView view, TouchEvent event, Segment segment) {
				Toast.makeText(TexasViewDemoActivity.this, "双击了Segment", Toast.LENGTH_SHORT).show();
			}
		});

		mTexasView.setOnDragSelectListener(new TexasView.OnDragSelectListener() {
			@Override
			public void onDragStart(TexasView view, TouchEvent event) {

			}

			@Override
			public void onDragEnd(TexasView view, TouchEvent event) {

			}

			@Override
			public void onDragDismiss(TexasView view) {

			}
		});
	}

	/**
	 * 加载数据
	 */
	private void setupData() {
		BookSource source = new BookSource(this, mTexasView, Paragraph.TYPESET_POLICY_CJK_MIX_OPTIMIZATION, "CnEnMix.xml");
		mTexasView.setSource(source);
	}

	/**
	 * 高亮
	 */
	private void setupHighlight() {
		findViewById(me.chan.texas.debug.R.id.highlight).setOnClickListener(v ->
				mTexasView.highlightParagraphs(new ParagraphPredicates() {
					@Override
					public boolean acceptSpan(@NonNull Span box) {
						Object spanTag = box.getTag();
						if (!(spanTag instanceof BookSource.SpanTag)) {
							return false;
						}

						BookSource.SpanTag tag = (BookSource.SpanTag) spanTag;
						return "A9127P127017S210459".equals(tag.sentId);
					}

					@Override
					public boolean acceptParagraph(@NonNull Paragraph paragraph) {
						return "A9127P127017".equals(paragraph.getTag());
					}

				}, true, 0));
	}

	private void setupDecor() {
		final int paddingHorizontal = 20;
		final int paddingVertical = 20;

		// 设置每个segment周边的空白
		mTexasView.setSegmentDecoration((index, count, segment, document, outRect) -> {
			if (segment instanceof Figure) {
				outRect.set(0, index == 0 ? 0 : paddingVertical, 0, index == count - 1 ? 0 : paddingVertical);
			} else {
				outRect.set(paddingHorizontal, index == 0 ? 0 : paddingVertical, paddingHorizontal, index == count - 1 ? 0 : paddingVertical);
			}
		});
	}

	/**
	 * 就是滚动条
	 */
	private void setupScrollBar() {
		Drawable drawable = ContextCompat.getDrawable(this, me.chan.texas.debug.R.drawable.scrollbar_thumb_demo);
		mTexasView.setScrollBarDrawable(drawable);
		Drawable target = mTexasView.getScrollBarDrawable();
		if (drawable != target) {
			throw new RuntimeException("check draw failed");
		}
	}

	private void render(final String name, final TexasView texasView) {
		texasView.setSource(new BookSource(this, texasView, Paragraph.TYPESET_POLICY_CJK_MIX_OPTIMIZATION, name));
	}

	private void setupDebug() {
		findViewById(me.chan.texas.debug.R.id.scroll_content).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mTexasView.scrollToPosition(0);
			}
		});

		findViewById(me.chan.texas.debug.R.id.line_height).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RenderOption renderOption = mTexasView.createRendererOption();
				renderOption.setLineSpacingExtra(renderOption.getLineSpacingExtra() + 200);
				Log.i("TexasCore", "set line space: " + renderOption.getLineSpacingExtra());
				mTexasView.refresh(renderOption);
			}
		});

		findViewById(me.chan.texas.debug.R.id.debug).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RenderOption renderOption = mTexasView.createRendererOption();
				renderOption.setDebugEnable(!renderOption.isDebugEnable());
				mTexasView.refresh(renderOption);
			}
		});

		// test update render option
		RenderOption renderOption = mTexasView.createRendererOption();
		renderOption.setDrawEmoticonSelection(false);
		mTexasView.refresh(renderOption);

		findViewById(me.chan.texas.debug.R.id.gc).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Texas.clean();
			}
		});

		findViewById(me.chan.texas.debug.R.id.refresh).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mTexasView.redraw();
			}
		});

		final View container = findViewById(me.chan.texas.debug.R.id.option_container);
		findViewById(me.chan.texas.debug.R.id.option).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (container.getVisibility() == View.VISIBLE) {
					container.setVisibility(View.GONE);
				} else {
					container.setVisibility(View.VISIBLE);
				}
			}
		});

		findViewById(me.chan.texas.debug.R.id.button1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry1.txt", mTexasView);
			}
		});

		findViewById(me.chan.texas.debug.R.id.basetest).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry2.txt", mTexasView);
			}
		});

		findViewById(me.chan.texas.debug.R.id.raw_paragraph_view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry3.txt", mTexasView);
			}
		});

		findViewById(me.chan.texas.debug.R.id.button4).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry4.txt", mTexasView);
			}
		});

		findViewById(me.chan.texas.debug.R.id.button5).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry5.txt", mTexasView);
			}
		});

		findViewById(me.chan.texas.debug.R.id.button6).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry6.txt", mTexasView);
			}
		});

		findViewById(me.chan.texas.debug.R.id.button7).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("simple_txt.txt", mTexasView);
			}
		});

		RadioGroup radioGroup = findViewById(me.chan.texas.debug.R.id.radio_group);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RenderOption option = mTexasView.createRendererOption();
				if (checkedId == me.chan.texas.debug.R.id.text_size_9) {
					option.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 9, getResources().getDisplayMetrics()));
				} else if (checkedId == me.chan.texas.debug.R.id.text_size_18) {
					option.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));
				} else if (checkedId == me.chan.texas.debug.R.id.text_size_27) {
					option.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 27, getResources().getDisplayMetrics()));
				} else if (checkedId == me.chan.texas.debug.R.id.text_size_45) {
					option.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 45, getResources().getDisplayMetrics()));
				} else if (checkedId == me.chan.texas.debug.R.id.text_size_72) {
					option.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 72, getResources().getDisplayMetrics()));
				}
				mTexasView.refresh(option);
			}
		});

		CheckBox checkBox = findViewById(me.chan.texas.debug.R.id.checkbox);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				RenderOption option = mTexasView.createRendererOption();
				option.setBreakStrategy(isChecked ? BreakStrategy.BALANCED : BreakStrategy.SIMPLE);
				mTexasView.refresh(option);
			}
		});

		checkBox = findViewById(me.chan.texas.debug.R.id.checkbox2);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				RenderOption option = mTexasView.createRendererOption();
				option.setTextColor(isChecked ? Color.BLACK : Color.BLUE);
				mTexasView.refresh(option);
			}
		});

		Typeface typeface = Typeface.createFromAsset(getAssets(), "opposans_r.ttf");
		renderOption = mTexasView.createRendererOption();
		renderOption.setTypeface(typeface);
		mTexasView.refresh(renderOption);

		final Object ADD = new Object();
		mTexasView.setSegmentAnimator(new TexasView.SegmentAnimator() {

			@Override
			protected Animator onCreateAddAnimator(Segment segment, View itemView) {
				if (segment.getTag() != ADD) {
					return null;
				}

				AnimatorSet animatorSet = new AnimatorSet();
				animatorSet.play(ObjectAnimator.ofFloat(itemView, "alpha", 0, 1))
						.with(ObjectAnimator.ofFloat(itemView, "translationY", -itemView.getHeight(), 0));
				animatorSet.setDuration(500);
				animatorSet.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationCancel(Animator animation) {
						super.onAnimationCancel(animation);
						itemView.setAlpha(1);
						itemView.setTranslationY(0);
					}
				});
				return animatorSet;
			}

			@Override
			protected Animator onCreateRemoveAnimator(Segment segment, View view) {
				return null;
			}

			@Override
			protected Animator onCreateMoveAnimator(Segment segment, View view, int fromX, int fromY, int toX, int toY) {
				return null;
			}
		});

		findViewById(me.chan.texas.debug.R.id.add_content).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mTexasView.setSource(new TexasView.DocumentSource() {
					@Override
					protected Document onRead(TexasOption option, @Nullable Document previousDocument) {
						return new Document.Builder(previousDocument)
								.addSegment(
										0,
										Paragraph.Builder.newBuilder(option)
												.tag(ADD)
												.text("生活就像点菜，饥饿时菜会点得特别多，但吃一阵就会意识到浪费；如果慢条斯理地盘算怎么点菜，别人已经要吃完了。")
												.build()
								)
								.build();
					}
				});
			}
		});

		Selection.Styles styles = Selection.Styles.create(Color.BLUE, Color.RED);
		styles.enableFakeBold();

		findViewById(me.chan.texas.debug.R.id.anim).setOnClickListener(v -> {
			Selection selection = mTexasView.highlightParagraphs(new ParagraphPredicates() {
				@Override
				public boolean acceptSpan(@NonNull Span box) {
					return true;
				}

				@Override
				public boolean acceptParagraph(@NonNull Paragraph paragraph) {
					return "A9127P126972".equals(paragraph.getTag());
				}
			}, Selection.Styles.create(Color.BLUE, Color.RED));
			if (selection == null) {
				return;
			}

			int backgroundColor = styles.getBackgroundColor();
			int textColor = styles.getTextColor();
			float fakeBoldFactor = styles.getFakeBoldFactor();
			ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1f);
			valueAnimator.setDuration(3000);
			valueAnimator.setRepeatCount(3);
			selection.startAnimator(valueAnimator, new Selection.SelectionAnimatorListener() {
				@RequiresApi(api = Build.VERSION_CODES.O)
				@Override
				protected void onUpdate(ValueAnimator animation, Selection.Styles styles) {
					float v = (float) animation.getAnimatedValue();
					styles.setTextColor(Color.argb((int) (255 * v) /* 按需设置透明度 */, Color.red(textColor), Color.green(textColor), Color.blue(textColor)));
					styles.setBackgroundColor(Color.argb((int) (255 * v) /* 按需设置透明度 */, Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor)));
					styles.setFakeBoldFactor(fakeBoldFactor * v);
				}

				@Override
				protected void onAnimationEnd(Animator animation, boolean isReverse, Selection.Styles styles) {
					selection.clear();
				}
			});
		});
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
}
