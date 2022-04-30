package org.hypothesis.components.shared.ui.timerlabel;

import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.label.LabelState;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
public class HTimerLabelState extends LabelState {
    public String timeFormat = "HH:mm:ss.S";
    public Connector timer = null;
    public int updateInterval = 100;

    {
        primaryStyleName = "v-timerlabel";
    }

}
