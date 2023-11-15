package me.chan.texas.renderer.ui.indicator;

public interface LoadingIndicator {

    void renderStart();

    void renderError();

    void renderSuccess();

    void dismiss();
}
