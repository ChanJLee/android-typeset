package me.chan.texas.annotations;

import androidx.annotation.RestrictTo;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * 标记幂等函数
 */
@Documented
@Retention(CLASS)
@Target({METHOD})
@RestrictTo(RestrictTo.Scope.LIBRARY)
public @interface Idempotent {
}
