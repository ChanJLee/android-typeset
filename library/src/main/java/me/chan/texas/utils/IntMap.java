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

package me.chan.texas.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class IntMap<E> implements Cloneable {
	private static final Object DELETED = new Object();
	private boolean mGarbage = false;

	private int[] mKeys;
	private Object[] mValues;
	private int mSize;

	
	public IntMap() {
		this(10);
	}

	
	public IntMap(int initialCapacity) {
		mKeys = new int[initialCapacity];
		mValues = new Object[initialCapacity];
		mSize = 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public IntMap<E> clone() {
		IntMap<E> clone;
		try {
			clone = (IntMap<E>) super.clone();
			clone.mKeys = mKeys.clone();
			clone.mValues = mValues.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e); 
		}
		return clone;
	}

	
	@Nullable
	@SuppressWarnings("NullAway") 
	public E get(int key) {





		return get(key, null);
	}

	
	@SuppressWarnings("unchecked")
	public E get(int key, E valueIfKeyNotFound) {
		int i = binarySearch(mKeys, mSize, key);

		if (i < 0 || mValues[i] == DELETED) {
			return valueIfKeyNotFound;
		} else {
			return (E) mValues[i];
		}
	}

	
	public void delete(int key) {
		int i = binarySearch(mKeys, mSize, key);

		if (i >= 0) {
			if (mValues[i] != DELETED) {
				mValues[i] = DELETED;
				mGarbage = true;
			}
		}
	}

	
	public void remove(int key) {
		delete(key);
	}

	
	public void removeAt(int index) {
		if (mValues[index] != DELETED) {
			mValues[index] = DELETED;
			mGarbage = true;
		}
	}

	
	public void removeAtRange(int index, int size) {
		final int end = Math.min(mSize, index + size);
		for (int i = index; i < end; i++) {
			removeAt(i);
		}
	}

	private void gc() {


		int n = mSize;
		int o = 0;
		int[] keys = mKeys;
		Object[] values = mValues;

		for (int i = 0; i < n; i++) {
			Object val = values[i];

			if (val != DELETED) {
				if (i != o) {
					keys[o] = keys[i];
					values[o] = val;
					values[i] = null;
				}

				o++;
			}
		}

		mGarbage = false;
		mSize = o;


	}

	
	public void put(int key, E value) {
		int i = binarySearch(mKeys, mSize, key);

		if (i >= 0) {
			mValues[i] = value;
		} else {
			i = ~i;

			if (i < mSize && mValues[i] == DELETED) {
				mKeys[i] = key;
				mValues[i] = value;
				return;
			}

			if (mGarbage && mSize >= mKeys.length) {
				gc();


				i = ~binarySearch(mKeys, mSize, key);
			}

			if (mSize >= mKeys.length) {
				int n = mSize * 2;

				int[] nkeys = new int[n];
				Object[] nvalues = new Object[n];


				System.arraycopy(mKeys, 0, nkeys, 0, mKeys.length);
				System.arraycopy(mValues, 0, nvalues, 0, mValues.length);

				mKeys = nkeys;
				mValues = nvalues;
			}

			if (mSize - i != 0) {

				System.arraycopy(mKeys, i, mKeys, i + 1, mSize - i);
				System.arraycopy(mValues, i, mValues, i + 1, mSize - i);
			}

			mKeys[i] = key;
			mValues[i] = value;
			mSize++;
		}
	}

	
	public void putAll(@NonNull IntMap<? extends E> other) {
		for (int i = 0, size = other.size(); i < size; i++) {
			put(other.keyAt(i), other.valueAt(i));
		}
	}

	
	public int size() {
		if (mGarbage) {
			gc();
		}

		return mSize;
	}

	
	public boolean isEmpty() {
		return size() == 0;
	}

	
	public int keyAt(int index) {
		if (mGarbage) {
			gc();
		}

		return mKeys[index];
	}

	
	@SuppressWarnings("unchecked")
	public E valueAt(int index) {
		if (mGarbage) {
			gc();
		}

		return (E) mValues[index];
	}

	
	public void setValueAt(int index, E value) {
		if (mGarbage) {
			gc();
		}

		mValues[index] = value;
	}

	
	public int indexOfKey(int key) {
		if (mGarbage) {
			gc();
		}

		return binarySearch(mKeys, mSize, key);
	}

	
	public int indexOfValue(E value) {
		if (mGarbage) {
			gc();
		}

		for (int i = 0; i < mSize; i++)
			if (mValues[i] == value)
				return i;

		return -1;
	}

	
	public boolean containsKey(int key) {
		return indexOfKey(key) >= 0;
	}

	
	public boolean containsValue(E value) {
		return indexOfValue(value) >= 0;
	}

	
	public void clear() {
		int n = mSize;
		Object[] values = mValues;

		for (int i = 0; i < n; i++) {
			values[i] = null;
		}

		mSize = 0;
		mGarbage = false;
	}

	
	public void append(int key, E value) {
		if (mSize != 0 && key <= mKeys[mSize - 1]) {
			put(key, value);
			return;
		}

		if (mGarbage && mSize >= mKeys.length) {
			gc();
		}

		int pos = mSize;
		if (pos >= mKeys.length) {
			int n = mKeys.length * 2;

			int[] nkeys = new int[n];
			Object[] nvalues = new Object[n];


			System.arraycopy(mKeys, 0, nkeys, 0, mKeys.length);
			System.arraycopy(mValues, 0, nvalues, 0, mValues.length);

			mKeys = nkeys;
			mValues = nvalues;
		}

		mKeys[pos] = key;
		mValues[pos] = value;
		mSize = pos + 1;
	}

	
	@Override
	public String toString() {
		if (size() <= 0) {
			return "{}";
		}

		StringBuilder buffer = new StringBuilder(mSize * 28);
		buffer.append('{');
		for (int i = 0; i < mSize; i++) {
			if (i > 0) {
				buffer.append(", ");
			}
			int key = keyAt(i);
			buffer.append(key);
			buffer.append('=');
			Object value = valueAt(i);
			if (value != this) {
				buffer.append(value);
			} else {
				buffer.append("(this Map)");
			}
		}
		buffer.append('}');
		return buffer.toString();
	}

	static int binarySearch(int[] array, int size, int value) {
		int lo = 0;
		int hi = size - 1;

		while (lo <= hi) {
			int mid = (lo + hi) >>> 1;
			int midVal = array[mid];

			if (midVal < value) {
				lo = mid + 1;
			} else if (midVal > value) {
				hi = mid - 1;
			} else {
				return mid;  
			}
		}
		return ~lo;  
	}

	public int capacity() {
		return mKeys.length;
	}
}
