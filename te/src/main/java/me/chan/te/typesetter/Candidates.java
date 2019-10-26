package me.chan.te.typesetter;

import me.chan.te.data.Recyclable;
import me.chan.te.misc.ObjectFactory;

/**
 * TODO 负优化了
 */
public class Candidates implements Recyclable {
	public static final int CLASS_0 = 0;
	public static final int CLASS_1 = 1;
	public static final int CLASS_2 = 2;
	public static final int CLASS_3 = 3;

	private static final ObjectFactory<Candidates> POOL = new ObjectFactory<>(64);

	private Candidate[] mCandidates = new Candidate[4];

	private Candidates() {
	}

	@Override
	public void recycle() {
		for (int i = 0; i < mCandidates.length; ++i) {
			Candidate candidate = mCandidates[i];
			if (candidate == null) {
				continue;
			}
			candidate.recycle();
			mCandidates[i] = null;
		}
		POOL.release(this);
	}

	public Candidate[] getCandidates() {
		return mCandidates;
	}

	public static int computeClazz(float ratio) {
		if (ratio < -0.5) {
			return CLASS_0;
		} else if (ratio <= 0.5) {
			return CLASS_1;
		} else if (ratio <= 1) {
			return CLASS_2;
		} else {
			return CLASS_3;
		}
	}

	public void chooseCandidate(float demerits, float ratio, Node active, int currentClass) {
		if (mCandidates[currentClass] == null || demerits < mCandidates[currentClass].demerits) {
			if (mCandidates[currentClass] == null) {
				mCandidates[currentClass] = Candidate.obtain(demerits, ratio, active);
				return;
			}

			mCandidates[currentClass].active = active;
			mCandidates[currentClass].demerits = demerits;
			mCandidates[currentClass].ratio = ratio;
		}
	}

	public static Candidates obtain() {
		Candidates candidates = POOL.acquire();
		if (candidates == null) {
			return new Candidates();
		}
		return candidates;
	}
}
