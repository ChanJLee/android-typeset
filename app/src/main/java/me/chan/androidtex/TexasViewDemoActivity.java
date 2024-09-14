package me.chan.androidtex;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;

import me.chan.texas.Texas;
import me.chan.texas.adapter.TextAdapter;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.source.AssetsTextSource;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.Figure;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.utils.TexasUtils;

public class TexasViewDemoActivity extends AppCompatActivity {

	private TexasView mTexasView;
	private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paragraph);
		mPaint.setColor(Color.RED);

		// 设置整个渲染窗口的padding
		mTexasView = findViewById(R.id.text);
		mTexasView.setRendererPadding(30, 10, 30, 10);

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
			public boolean isSpanClickable(Object tag) {
				return true;
			}

			// 单机谓词 判断 单机时哪些单词要被高亮
			@Override
			public boolean applySpanClicked(@Nullable Object clickedTag, @Nullable Object otherTag) {
				return clickedTag == otherTag;
			}

			@Override
			public boolean applySpanLongClicked(@Nullable Object clickedTag, @Nullable Object otherTag) {
				if (clickedTag instanceof BookParser.SpanTag && otherTag instanceof BookParser.SpanTag) {
					BookParser.SpanTag lhs = (BookParser.SpanTag) clickedTag;
					BookParser.SpanTag rhs = (BookParser.SpanTag) otherTag;
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
		mTexasView.setOnClickedListener(new TexasView.OnClickedListener() {
			@Override
			public void onSpanClicked(TouchEvent event, Object tag) {

			}

			@Override
			public void onSpanLongClicked(TouchEvent event, Object tag) {

			}

			@Override
			public void onSegmentClicked(TouchEvent event, Object tag) {
				Toast.makeText(TexasViewDemoActivity.this, "点击了Segment", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onEmptyClicked(TouchEvent event) {
				Toast.makeText(TexasViewDemoActivity.this, "点击了空白", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSegmentDoubleClicked(TouchEvent event, Object tag) {
				Toast.makeText(TexasViewDemoActivity.this, "双击", Toast.LENGTH_SHORT).show();
			}
		});

		mTexasView.setOnDragSelectListener(new TexasView.OnDragSelectListener() {
			@Override
			public void onDragStart(TouchEvent event) {

			}

			@Override
			public void onDragEnd(TouchEvent event) {

			}

			@Override
			public void onDragDismiss() {

			}
		});
	}

	/**
	 * 加载数据
	 */
	private void setupData() {
		BookParser adapter = new BookParser(this, mTexasView, Paragraph.TYPESET_POLICY_CJK_OPTIMIZATION);
		mTexasView.setAdapter(adapter);
		try {
			adapter.setSource(new AssetsTextSource(this, "CnEnMix.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 高亮
	 */
	private void setupHighlight() {
		findViewById(R.id.highlight).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mTexasView.highlightParagraphs(new TexasView.HighlightPredicate() {
					@Override
					public boolean apply(@Nullable Object paragraphTag, @Nullable Object spanTag) {
						if (!"A9127P127017".equals(paragraphTag)) {
							return false;
						}

						if (!(spanTag instanceof BookParser.SpanTag)) {
							return false;
						}

						BookParser.SpanTag tag = (BookParser.SpanTag) spanTag;
						return "A9127P127017S210459".equals(tag.sentId);
					}
				}, true, 0);
			}
		});
	}

	private void setupDecor() {
		final int paddingHorizontal = 20;
		final int paddingVertical = 20;

		// 设置每个segment周边的空白
		mTexasView.setSegmentDecoration(new TexasView.SegmentDecoration() {
			@Override
			public void onDecorateSegment(int index, int count, Segment segment, Document document, Rect outRect) {
				if (segment instanceof Figure) {
					outRect.set(0, index == 0 ? 0 : paddingVertical, 0, index == count - 1 ? 0 : paddingVertical);
				} else {
					outRect.set(paddingHorizontal, index == 0 ? 0 : paddingVertical, paddingHorizontal, index == count - 1 ? 0 : paddingVertical);
				}
			}
		});

		// 安装文章边缘的🔥按钮
		setupSidebar();
	}

	/**
	 * 安装side bar控件
	 */
	private void setupSidebar() {
		// side bar 的渲染和 普通view一样 都会经历 layout 的过程
		// 不过文本引擎里有点特别
		// 会先 onPreDrawDecor，用户可以在这个接口里做一些清理工作
		// 因为 ParagraphDecor 是全局共享的，每绘制一个 Paragraph 都会调用
		// 调用完 然后经历 onLayoutDecor 让你去准备这一段落的 渲染信息，比如你探测到这段
		// 有句子id是 A9127P126972S210390，我便在这个句子旁边画个🔥
		// 最后会 onDrawDecor 让你去绘制🔥
		final Drawable fireDrawable = ContextCompat.getDrawable(this, R.drawable.fire);
		ParagraphDecor paragraphDecor = new ParagraphDecor() {
			private boolean mClicked = false;
			private boolean mDraw = false;
			private final Rect mDest = new Rect();

			@Override
			protected void onPreDrawDecor(Paragraph paragraph, Rect viewportOuter, Rect viewportInner) {
				// 准备绘制 decor
				mClicked = false;
				mDraw = false;
			}

			@Override
			protected int onLayoutDecor(Paragraph paragraph, Object spanTag, RectF spanOuter, RectF spanInner, Rect decorOuter, Rect decorInner) {
				// 准备decor的布局
				if (!(spanTag instanceof BookParser.SpanTag)) {
					return ParagraphVisitor.SIG_NORMAL;
				}

				BookParser.SpanTag tag = (BookParser.SpanTag) spanTag;
				if (!"A9127P126972S210390".equals(tag.sentId)) {
					return ParagraphVisitor.SIG_NORMAL;
				}

				mDraw = true;
				mDest.set(decorOuter.right - 40, (int) spanOuter.bottom - 40, decorOuter.right, (int) spanOuter.bottom);
				return ParagraphVisitor.SIG_STOP_PARA_VISIT;
			}

			@Override
			protected void onDrawDecor(Canvas canvas, Paragraph paragraph, Rect decorOuter, Rect decorInner) {
				if (!mDraw) {
					return;
				}

				fireDrawable.setBounds(mDest);

				// 选中了就变色
				fireDrawable.setTint(mClicked ? Color.RED : Color.GRAY);
				fireDrawable.draw(canvas);
			}

			@Override
			protected boolean onTouchEvent(MotionEvent event, Paragraph paragraph, Rect decorOuter, Rect decorInner) {
				if (!mDraw) {
					return false;
				}

				// 需要根据 onCollectDecorRenderInfo 缓存区域去判断事件点击
				float x = event.getX();
				float y = event.getY();

				if (mDest.top - 10 < y && mDest.bottom + 10 > y &&
						mDest.left - 10 < x && mDest.right + 10 > x) {
					mClicked = true;
					mTexasView.selectParagraphs(new TexasView.SelectionPredicate() {
						@Override
						public boolean apply(Object paragraphTag, Object spanTag) {
							if (!(spanTag instanceof BookParser.SpanTag)) {
								return false;
							}

							BookParser.SpanTag tag = (BookParser.SpanTag) spanTag;
							return "A9127P126972S210390".equals(tag.sentId);
						}
					});
					return true;
				}
				return false;
			}
		};
		mTexasView.setParagraphDecor(paragraphDecor);
	}

	/**
	 * 就是滚动条
	 */
	private void setupScrollBar() {
		Drawable drawable = ContextCompat.getDrawable(this, R.drawable.scrollbar_thumb_demo);
		mTexasView.setScrollBarDrawable(drawable);
		Drawable target = mTexasView.getScrollBarDrawable();
		if (drawable != target) {
			throw new RuntimeException("check draw failed");
		}
	}

	private void render(final String name, final TexasView texasView) {
		try {
			TextAdapter adapter = new TextAdapter();
			adapter.setSource(new AssetsTextSource(this, name));
			texasView.setAdapter(adapter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setupDebug() {
		findViewById(R.id.scroll_content).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mTexasView.scrollToPosition(0);
			}
		});

		findViewById(R.id.line_height).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RenderOption renderOption = mTexasView.createRendererOption();
				renderOption.setLineSpace(renderOption.getLineSpace() + 200);
				Log.i("TexasCore", "set line space: " + renderOption.getLineSpace());
				mTexasView.refresh(renderOption);
			}
		});

		findViewById(R.id.debug).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RenderOption renderOption = mTexasView.createRendererOption();
				renderOption.setEnableDebug(!renderOption.isEnableDebug());
				mTexasView.refresh(renderOption);
			}
		});

		// test update render option
		RenderOption renderOption = mTexasView.createRendererOption();
		renderOption.setDrawEmoticonSelection(false);
		mTexasView.refresh(renderOption);

		findViewById(R.id.gc).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Texas.clean();
			}
		});

		findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mTexasView.redraw();
			}
		});

		final View container = findViewById(R.id.option_container);
		findViewById(R.id.option).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (container.getVisibility() == View.VISIBLE) {
					container.setVisibility(View.GONE);
				} else {
					container.setVisibility(View.VISIBLE);
				}
			}
		});

		findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry1.txt", mTexasView);
			}
		});

		findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry2.txt", mTexasView);
			}
		});

		findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry3.txt", mTexasView);
			}
		});

		findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry4.txt", mTexasView);
			}
		});

		findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry5.txt", mTexasView);
			}
		});

		findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry6.txt", mTexasView);
			}
		});

		findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("simple_txt.txt", mTexasView);
			}
		});

		RadioGroup radioGroup = findViewById(R.id.radio_group);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RenderOption option = mTexasView.createRendererOption();
				if (checkedId == R.id.text_size_9) {
					option.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 9, getResources().getDisplayMetrics()));
				} else if (checkedId == R.id.text_size_18) {
					option.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));
				} else if (checkedId == R.id.text_size_27) {
					option.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 27, getResources().getDisplayMetrics()));
				} else if (checkedId == R.id.text_size_45) {
					option.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 45, getResources().getDisplayMetrics()));
				} else if (checkedId == R.id.text_size_72) {
					option.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 72, getResources().getDisplayMetrics()));
				}
				mTexasView.refresh(option);
			}
		});

		CheckBox checkBox = findViewById(R.id.checkbox);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				RenderOption option = mTexasView.createRendererOption();
				option.setBreakStrategy(isChecked ? BreakStrategy.BALANCED : BreakStrategy.SIMPLE);
				mTexasView.refresh(option);
			}
		});

		checkBox = findViewById(R.id.checkbox2);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				RenderOption option = mTexasView.createRendererOption();
				option.setTextColor(isChecked ? Color.BLACK : Color.BLUE);
				mTexasView.refresh(option);
			}
		});
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
}
