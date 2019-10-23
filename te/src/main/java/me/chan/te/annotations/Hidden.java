package me.chan.te.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来标记隐藏api，隐藏api未来会在某天修改暴露的接口
 * 修改可以是删除
 */
@Documented
@Inherited
@Retention(RetentionPolicy.CLASS)
@Target(value = {ElementType.TYPE, ElementType.METHOD})
public @interface Hidden {
}
