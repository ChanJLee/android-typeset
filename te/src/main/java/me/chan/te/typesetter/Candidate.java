package me.chan.te.typesetter;

import me.chan.te.annotations.Hidden;
import me.chan.te.data.Recyclable;
import me.chan.te.misc.ObjectFactory;

@Hidden
public class Candidate implements Recyclable {
	private static final ObjectFactory<Candidate> POOL = new ObjectFactory<>(200);

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
		reset(Float.MAX_VALUE, -1, null);
		POOL.release(this);
	}

	public static Candidate obtain(float demerits, float ratio, Node active) {
		Candidate candidate = POOL.acquire();
		if (candidate == null) {
			return new Candidate(demerits, ratio, active);
		}
		candidate.reset(demerits, ratio, active);
		return candidate;
	}
}
