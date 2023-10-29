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
    /*
     * 刷新当前内容
     * */
    LOAD_REFRESH
}