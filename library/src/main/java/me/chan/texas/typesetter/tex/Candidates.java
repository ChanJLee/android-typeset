package me.chan.texas.typesetter.tex;

import androidx.annotation.Nullable;

public class Candidates {
	private final Candidate[] mCandidates = new Candidate[4];
	private boolean isEmpty = true;

	public int size() {
		return mCandidates.length;
	}

	public void clear() {
		for (int i = 0; i < mCandidates.length; i++) {
			Candidate candidate = mCandidates[i];
			mCandidates[i] = null;
			if (candidate != null) {
				candidate.recycle();
			}
		}
		isEmpty = true;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	@Nullable
	public Candidate get(int fitness) {
		if (fitness < 0 || fitness >= mCandidates.length) {
			throw new IllegalArgumentException("fitness: " + fitness);
		}

		return mCandidates[fitness];
	}

	public void update(int fitness, float demerits, Node active, float ratio) {
		if (mCandidates[fitness] == null || demerits < mCandidates[fitness].demerits) {
			if (mCandidates[fitness] == null) {
				mCandidates[fitness] = Candidate.obtain();
			}
			Candidate candidate = mCandidates[fitness];
			candidate.active = active;
			candidate.demerits = demerits;
			candidate.ratio = ratio;
			isEmpty = false;
		}
	}
}
