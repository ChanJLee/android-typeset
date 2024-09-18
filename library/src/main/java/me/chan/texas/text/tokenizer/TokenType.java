package me.chan.texas.text.tokenizer;

import androidx.annotation.RestrictTo;

import me.chan.texas.misc.BitBucket;

public interface TokenType {

	/**
	 * @param bucket    用于存储属性的容器
	 * @param attribute 属性
	 * @return 是否满足条件
	 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	boolean check(BitBucket bucket, int attribute);


	class Symbol implements TokenType {

		// 避头尾
		public static final int SYMBOL_KINSOKU_AVOID_HEADER = 1;
		public static final int SYMBOL_KINSOKU_AVOID_TAIL = 2;
		public static final int SYMBOL_KINSOKU_MASK = 3;

		// 挤压
		public static final int SYMBOL_SQUISH_LEFT = 4;
		public static final int SYMBOL_SQUISH_RIGHT = 5;

		public static final int SYMBOL_SQUISH_MASK = 6;

		// 拉伸
		public static final int SYMBOL_STRETCH_LEFT = 7;
		public static final int SYMBOL_STRETCH_RIGHT = 8;

		public static final int SYMBOL_STRETCH_MASK = 9;

		public static final int SYMBOL_TYPEFACE_MASK = 10;

		public static final int SYMBOL_FULL_WIDTH = 11;

		// https://www.compart.com/en/unicode/block/U+3000

		@Override
		public boolean check(BitBucket bucket, int attribute) {
			if (attribute < SYMBOL_KINSOKU_MASK) {
				return bucket.get(attribute);
			}

			if (attribute == SYMBOL_KINSOKU_MASK) {
				return bucket.get(SYMBOL_KINSOKU_AVOID_HEADER) || bucket.get(SYMBOL_KINSOKU_AVOID_TAIL);
			}

			if (attribute < SYMBOL_SQUISH_MASK) {
				return bucket.get(attribute);
			}

			if (attribute == SYMBOL_SQUISH_MASK) {
				return bucket.get(SYMBOL_SQUISH_LEFT) || bucket.get(SYMBOL_SQUISH_RIGHT);
			}

			if (attribute < SYMBOL_STRETCH_MASK) {
				return bucket.get(attribute);
			}

			if (attribute == SYMBOL_STRETCH_MASK) {
				return bucket.get(SYMBOL_STRETCH_LEFT) || bucket.get(SYMBOL_STRETCH_RIGHT);
			}

			if (attribute == SYMBOL_TYPEFACE_MASK) {
				return bucket.get(SYMBOL_KINSOKU_MASK) || bucket.get(SYMBOL_SQUISH_MASK) || bucket.get(SYMBOL_STRETCH_MASK);
			}

			throw new IllegalArgumentException("unknown attribute: " + attribute);
		}
	}

	class Word implements TokenType {
		// 一般是拉丁
		public static final int CONTEXT_SENSITIVE = 1;

		public static final int HYPHEN_ENABLE = 2;

		public static final int FULL_WIDTH = 3;

		public static final int RTL = 4;

		@Override
		public boolean check(BitBucket bucket, int attribute) {
			return false;
		}
	}

	class Control implements TokenType {

		@Override
		public boolean check(BitBucket bucket, int attribute) {
			return false;
		}
	}

	class Blank implements TokenType {

		@Override
		public boolean check(BitBucket bucket, int attribute) {
			return false;
		}
	}
}
