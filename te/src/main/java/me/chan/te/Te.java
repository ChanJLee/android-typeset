package me.chan.te;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;

import com.bumptech.glide.request.target.ViewTarget;

import me.chan.te.annotations.Hidden;
import me.chan.te.text.DrawableBox;
import me.chan.te.text.Glue;
import me.chan.te.text.Penalty;
import me.chan.te.text.TextBox;
import me.chan.te.log.Log;
import me.chan.te.text.Background;
import me.chan.te.text.Document;
import me.chan.te.text.Figure;
import me.chan.te.text.Page;
import me.chan.te.text.Paragraph;
import me.chan.te.text.UnderLine;
import me.chan.te.typesetter.BreakPoint;
import me.chan.te.typesetter.Candidate;
import me.chan.te.typesetter.Node;
import me.chan.te.typesetter.Sum;

public class Te {
	public static void init(Application application) {
		application.registerComponentCallbacks(new ComponentCallbacks() {
			@Override
			public void onConfigurationChanged(Configuration newConfig) {

			}

			@Override
			public void onLowMemory() {
				clean();
			}
		});

		try {
			ViewTarget.setTagId(R.id.me_chan_te_glide);
		} catch (Exception ignore) {
		}
	}

	/**
	 * do clean
	 */
	@Hidden
	public static void clean() {
		Log.i("Te", "clean text engine memory");
		// do clean
		// add engine clean code
		TextBox.clean();
		Glue.clean();
		Paragraph.Line.clean();
		Paragraph.clean();
		Penalty.clean();
		BreakPoint.clean();
		Candidate.clean();
		Node.clean();
		Sum.clean();
		Document.clean();
		Figure.clean();
		DrawableBox.clean();
		Background.clean();
		UnderLine.clean();
		Paragraph.Builder.clean();
		Page.clean();
		TextBox.Attribute.clean();
		System.gc();
	}
}
