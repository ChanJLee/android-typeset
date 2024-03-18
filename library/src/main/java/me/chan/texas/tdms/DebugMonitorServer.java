package me.chan.texas.tdms;

import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;

import androidx.annotation.RestrictTo;

import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.TypesetContext;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.TextBox;
import me.chan.texas.utils.TexasUtils;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class DebugMonitorServer {
	private static String sServerUrl;

	public static void setServerUrl(String url) {
		sServerUrl = url;
	}

	public static void send(Document document,
							PaintSet paintSet,
							TextAttribute attribute,
							int width,
							RenderOption renderOption) {
		if (TextUtils.isEmpty(sServerUrl)) {
			return;
		}

		StringBuilder builder = new StringBuilder(2048);
		builder.append("{");

		// write version
		builder.append("\"version\":")
				.append("\"1.0.0\"")
				.append(",");

		// write density
		builder.append("\"density\":")
				.append(TexasUtils.getDensity())
				.append(",");

		// 单个西文字符宽度
		builder.append("\"font_width\":")
				.append(attribute.getHyphenWidth())
				.append(",");

		// 文字大小
		TextPaint paint = paintSet.getPaint();
		builder.append("\"text_size\":")
				.append(paint.getTextSize())
				.append(",");

		// 行宽
		builder.append("\"width\":")
				.append(width)
				.append(",");

		builder.append("\"paragraphs\":[");

		SerializableParagraphVisitor visitor = new SerializableParagraphVisitor();
		visitor.mBuilder = builder;
		for (int i = 0; i < document.getSegmentCount(); ++i) {
			Segment segment = document.getSegment(i);
			if (!(segment instanceof Paragraph)) {
				continue;
			}

			Paragraph paragraph = (Paragraph) segment;
			try {
				visitor.visit(paragraph, renderOption);
			} catch (ParagraphVisitor.VisitException ignore) {
				/* do nothing */
			}
		}
		builder.append("]}");

		String json = builder.toString();
		// todo
		System.out.println(json);
	}

	private static class SerializableParagraphVisitor extends ParagraphVisitor {
		private boolean mFirstParagraph = true;
		private StringBuilder mBuilder;
		private Layout mLayout;

		@Override
		protected void onVisitParagraphStart(Paragraph paragraph) {
			if (!mFirstParagraph) {
				mBuilder.append(",");
			}

			mFirstParagraph = false;
			mBuilder.append("{");

			// write padding
			Rect rect = paragraph.getRect();
			if (rect != null) {
				mBuilder.append("\"padding\":[")
						.append(rect.left)
						.append(",")
						.append(rect.top)
						.append(",")
						.append(rect.right)
						.append(",")
						.append(rect.bottom)
						.append("],");
			}

			mBuilder.append("\"lines\":[");
			mLayout = paragraph.getLayout();
		}

		@Override
		protected void onVisitParagraphEnd(Paragraph paragraph) {
			mBuilder.append("]}");
		}


		private boolean mNewline = false;

		@Override
		protected void onVisitLineStart(Line line, float x, float y) {
			if (line != mLayout.getLine(0)) {
				mBuilder.append(",");
			}

			mBuilder.append("{");
			mBuilder.append("\"x\":")
					.append(x)
					.append(",")
					.append("\"y\":")
					.append(y)
					.append(",");

			// spans
			mBuilder.append("\"spans\":[");
			mNewline = true;
		}

		@Override
		protected void onVisitLineEnd(Line line, float x, float y) {
			mBuilder.append("]}");
		}

		@Override
		protected void onVisitBox(Box box, RectF inner, RectF outer, TypesetContext context) {
			if (!mNewline) {
				mBuilder.append(",");
			}
			mNewline = false;

			mBuilder.append("{");
			mBuilder.append("\"region\":[")
					.append(inner.left)
					.append(",")
					.append(inner.top)
					.append(",")
					.append(inner.right)
					.append(",")
					.append(inner.bottom)
					.append("]");
			if (box instanceof TextBox) {
				TextBox textBox = (TextBox) box;
				mBuilder.append(",\"text\":\"")
						.append(textBox.getText(), textBox.getStart(), textBox.getEnd())
						.append("\"");
			}
			mBuilder.append("}");
		}
	}
}
