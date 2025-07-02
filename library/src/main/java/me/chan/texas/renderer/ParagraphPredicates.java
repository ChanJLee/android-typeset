package me.chan.texas.renderer;

import androidx.annotation.Nullable;

import me.chan.texas.annotations.Idempotent;

public interface ParagraphPredicates {

	
	@Idempotent
	boolean acceptSpan(@Nullable Object spanTag);

	
	@Idempotent
	boolean acceptParagraph(@Nullable Object paragraphTag);
}
