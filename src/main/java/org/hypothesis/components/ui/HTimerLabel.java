package org.hypothesis.components.ui;

import org.hypothesis.components.shared.ui.timerlabel.HTimerLabelState;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
public class HTimerLabel extends HLabel {

    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss.S";

    private HTimer timer = null;

    public HTimerLabel() {
        super();
    }

    public String getTimeFormat() {
        return getState().timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        getState().timeFormat = timeFormat;
    }

    @Override
    protected HTimerLabelState getState() {
        return (HTimerLabelState) super.getState();
    }

    public HTimer getTimer() {
        return timer;
    }

    public void setTimer(HTimer timer) {
        this.timer = timer;
        getState().timer = timer;
    }

}
