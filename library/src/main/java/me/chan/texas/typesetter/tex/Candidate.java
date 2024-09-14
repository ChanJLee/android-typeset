package me.chan.texas.typesetter.tex;

import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

@RestrictTo(LIBRARY)
public class Candidate extends DefaultRecyclable {
	private static final ObjectPool<Candidate> POOL = new ObjectPool<>(128);
	static {
		Texas.registerLifecycleCallback(new Texas.LifecycleCallback() {
			@Override
			public void onClean() {
				POOL.clean();
			}
		});
	}

	public float demerits;
	public Node active;
	public float ratio;

	@Override
	protected void onRecycle() {
		ratio = demerits = 0;
		active = null;
		POOL.release(this);
	}

	public static Candidate obtain() {
		Candidate candidate = POOL.acquire();
		if (candidate == null) {
			return new Candidate();
		}
		candidate.reuse();
		return candidate;
	}

	public static Candidate obtain(float demerits, float ratio, Node active) {
		Candidate candidate = POOL.acquire();
		if (candidate == null) {
			candidate = new Candidate();
		}
		candidate.active = active;
		candidate.demerits = demerits;
		candidate.ratio = ratio;
		candidate.reuse();
		return candidate;
	}
}
