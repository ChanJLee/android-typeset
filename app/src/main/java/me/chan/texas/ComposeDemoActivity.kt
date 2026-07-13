package me.chan.texas

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.chan.texas.ext.compose.ComposeParagraphView
import me.chan.texas.ext.compose.ComposeTexasView
import me.chan.texas.ext.compose.ComposeViewSegment
import me.chan.texas.renderer.TexasView
import me.chan.texas.renderer.ui.text.ParagraphView
import me.chan.texas.text.Document
import me.chan.texas.text.DotUnderLine
import me.chan.texas.text.Paragraph
import me.chan.texas.text.RectGround

/**
 * ext-compose 演示：
 * 1. Compose 中直接使用 ComposeTexasView / ComposeParagraphView
 * 2. ComposeParagraphView 通过 ParagraphSource 渲染富文本
 * 3. Texas 文档流中通过 ComposeViewSegment 内嵌可交互的 Compose 内容
 */
class ComposeDemoActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			Column(Modifier.fillMaxSize()) {
				// ComposeParagraphView + ParagraphSource：富文本段落
				BasicText(
					text = "↓ ComposeParagraphView（ParagraphSource 富文本）",
					modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 4.dp),
					style = TextStyle(fontSize = 12.sp, color = Color.Gray),
				)
				// source 是工厂函数：每次加载创建全新实例，无需 remember
				ComposeParagraphView(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp),
					source = { createParagraphSource() },
				)

				// ComposeTexasView：整篇文档，文档流中混排 Compose 卡片
				BasicText(
					text = "↓ ComposeTexasView（文档流中内嵌 Compose 卡片）",
					modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 4.dp),
					style = TextStyle(fontSize = 12.sp, color = Color.Gray),
				)
				ComposeTexasView(
					modifier = Modifier
						.fillMaxWidth()
						.weight(1f),
					onCreate = { view -> view.setRendererPadding(30, 10, 30, 10) },
					source = { createDocumentSource() },
				)
			}
		}
	}

	/** 富文本段落：分词后为每个词加红色点状下划线，整段带黄色背景标记词 */
	private fun createParagraphSource() = object : ParagraphView.ParagraphSource() {
		override fun onRead(option: TexasOption): Paragraph {
			val text = "ParagraphSource 支持完整的富文本能力：语义分词、样式、tag 与点击交互，" +
					"viverra maecenas accumsan lacus vel facilisis."
			return Paragraph.Builder.newBuilder(option)
				.stream(text, 0, text.length) { token ->
					Paragraph.SpanStyles.obtain(token).apply {
						setTag(token.toString())
						if (token.toString().contains("富文本")) {
							setBackground(RectGround(0x66FFEB3B))
						} else {
							setForeground(DotUnderLine(AndroidColor.RED))
						}
					}
				}
				.build()
		}
	}

	private fun createDocumentSource() = object : TexasView.DocumentSource() {
		override fun onRead(option: TexasOption, previousDocument: Document?): Document {
			val builder = Document.Builder()
			builder.addSegment(
				Paragraph.Builder.newBuilder(option)
					.text("下面的卡片是内嵌在 Texas 文档流中的 Compose 内容，它和普通 Segment 一样参与排版与视图复用，且保留 Compose 自己的状态与交互能力。")
					.build()
			)
			builder.addSegment(ComposeViewSegment { CounterCard() })
			builder.addSegment(
				Paragraph.Builder.newBuilder(option)
					.text("卡片之后仍然是 Texas 排版的段落。Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. 混排的中文内容也可以正常参与两端对齐与标点挤压，一切都由排版引擎统一调度。")
					.build()
			)
			return builder.build()
		}
	}
}

@Composable
private fun CounterCard() {
	var count by remember { mutableIntStateOf(0) }
	Column(
		Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp)
			.background(Color(0xFFE8F0FE), RoundedCornerShape(12.dp))
			.clickable { count++ }
			.padding(16.dp)
	) {
		BasicText(
			text = "🎯 我是 Compose 卡片",
			style = TextStyle(fontSize = 16.sp, color = Color(0xFF1A73E8)),
		)
		Spacer(Modifier.height(8.dp))
		BasicText(
			text = "点我计数（状态由 Compose 管理）：$count",
			style = TextStyle(fontSize = 14.sp, color = Color(0xFF333333)),
		)
	}
}
