package me.chan.texas.renderer;

/**
 * 策略
 */
public enum LoadingStrategy {
    /**
     * 加载之前
     */
    LOAD_PREVIOUS,
    /**
     * 加载更多
     */
    LOAD_MORE,
    /**
     * 刷新当前内容，但不清除已经加载的数据
     */
    LOAD_REFRESH,
    /**
     * 重新加载数据
     * */
    LOAD_RELOAD
}