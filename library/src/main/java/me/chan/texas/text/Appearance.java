package me.chan.texas.text;

import android.graphics.Canvas;
import android.graphics.Paint;

import me.chan.texas.misc.RectF;

import androidx.annotation.AnyThread;

import me.chan.texas.renderer.RendererContext;


public abstract class Appearance {
	
	@AnyThread
	public abstract void draw(Canvas canvas, Paint paint, RectF inner, RectF outer, RendererContext context);
}
