package org.hypothesis.components.client.ui.timerlabel;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import org.hypothesis.components.client.ui.VHTimerLabel;
import org.hypothesis.components.shared.ui.timerlabel.HTimerLabelState;
import org.hypothesis.components.ui.HTimerLabel;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@Connect(HTimerLabel.class)
public class HTimerLabelConnector extends AbstractComponentConnector {

    @Override
    public VHTimerLabel getWidget() {
        return (VHTimerLabel) super.getWidget();
    }

    @Override
    public HTimerLabelState getState() {
        return (HTimerLabelState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        if (stateChangeEvent.hasPropertyChanged("timeFormat")) {
            getWidget().setTimeFormat(getState().timeFormat);
        }

        if (stateChangeEvent.hasPropertyChanged("updateInterval")) {
            getWidget().setUpdateInterval(getState().updateInterval);
        }

        if (stateChangeEvent.hasPropertyChanged("timer")) {
            getWidget().registerTimer(getState().timer);
        }
    }

}
