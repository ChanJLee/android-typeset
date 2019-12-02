package me.chan.texas.typesetter;

import me.chan.texas.annotations.Hidden;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectFactory;

@Hidden
public class Candidate extends DefaultRecyclable {
	private static final ObjectFactory<Candidate> POOL = new ObjectFactory<>(256);

	public float demerits;
	public Node active;
	public float ratio;

	private Candidate(float demerits, float ratio, Node active) {
		reset(demerits, ratio, active);
	}

	private void reset(float demerits, float ratio, Node active) {
		this.demerits = demerits;
		this.ratio = ratio;
		this.active = active;
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		reset(Float.MAX_VALUE, -1, null);
		POOL.release(this);
	}

	public static Candidate obtain(float demerits, float ratio, Node active) {
		Candidate candidate = POOL.acquire();
		if (candidate == null) {
			return new Candidate(demerits, ratio, active);
		}
		candidate.reset(demerits, ratio, active);
		candidate.reuse();
		return candidate;
	}

	public static void clean() {
		POOL.clean();
	}
}
