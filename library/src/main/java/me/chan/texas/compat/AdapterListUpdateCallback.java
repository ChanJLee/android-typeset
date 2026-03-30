/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.chan.texas.compat;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ListUpdateCallback that dispatches update events to the given adapter.
 *
 */
public final class AdapterListUpdateCallback implements ListUpdateCallback {
	private static final boolean DEBUG = false;

	@NonNull
	private final RecyclerView.Adapter mAdapter;

	/**
	 * Creates an AdapterListUpdateCallback that will dispatch update events to the given adapter.
	 *
	 * @param adapter The Adapter to send updates to.
	 */
	public AdapterListUpdateCallback(@NonNull RecyclerView.Adapter adapter) {
		mAdapter = adapter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onInserted(int position, int count) {
		if (DEBUG) {
			Log.d("chan_debug", "insert: " + position + count);
		}
		mAdapter.notifyItemRangeInserted(position, count);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onRemoved(int position, int count) {
		if (DEBUG) {
			Log.d("chan_debug", "remove: " + position + count);
		}
		mAdapter.notifyItemRangeRemoved(position, count);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onMoved(int fromPosition, int toPosition) {
		if (DEBUG) {
			Log.d("chan_debug", "move: " + fromPosition + toPosition);
		}
		mAdapter.notifyItemMoved(fromPosition, toPosition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onChanged(int position, int count, Object payload) {
		if (DEBUG) {
			Log.d("chan_debug", "change: " + position + count);
		}
		mAdapter.notifyItemRangeChanged(position, count, payload);
	}
}
