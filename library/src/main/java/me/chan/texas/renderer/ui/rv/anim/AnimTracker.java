package me.chan.texas.renderer.ui.rv.anim;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class AnimTracker {
	private final HashMap<RecyclerView.ViewHolder, AnimRecord> mRecords = new HashMap<>();

	public void add(RecyclerView.ViewHolder holder, AnimRecord record) {
		mRecords.put(holder, record);
	}

	public boolean remove(RecyclerView.ViewHolder holder) {
		return mRecords.remove(holder) != null;
	}

	@Nullable
	public AnimRecord get(RecyclerView.ViewHolder holder) {
		return mRecords.get(holder);
	}

	public void advanceTo(RecyclerView.ViewHolder holder, int phase) {
		AnimRecord record = mRecords.get(holder);
		if (record != null) {
			record.phase = phase;
		}
	}

	public List<RecyclerView.ViewHolder> holdersByPhase(int phase) {
		List<RecyclerView.ViewHolder> result = new ArrayList<>();
		for (HashMap.Entry<RecyclerView.ViewHolder, AnimRecord> entry : mRecords.entrySet()) {
			if (entry.getValue().phase == phase) {
				result.add(entry.getKey());
			}
		}
		return result;
	}

	public List<RecyclerView.ViewHolder> allHolders() {
		return new ArrayList<>(mRecords.keySet());
	}

	public boolean isEmpty() {
		return mRecords.isEmpty();
	}
}