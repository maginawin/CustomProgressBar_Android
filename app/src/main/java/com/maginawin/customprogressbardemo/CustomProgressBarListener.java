package com.maginawin.customprogressbardemo;

/**
 * Created by maginawin on 2017/12/14.
 */

@FunctionalInterface
public interface CustomProgressBarListener {
    void didCustomProgressBarValueChanged(CustomProgressBar bar, float value);
}
