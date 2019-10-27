package me.chan.te;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;

import me.chan.te.annotations.Hidden;
import me.chan.te.data.Glue;
import me.chan.te.data.Line;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Penalty;
import me.chan.te.data.Segment;
import me.chan.te.data.TextBox;
import me.chan.te.log.Log;
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
		Line.clean();
		Paragraph.clean();
		Penalty.clean();
		Segment.clean();
		BreakPoint.clean();
		Candidate.clean();
		Node.clean();
		Sum.clean();
		System.gc();
	}
}
