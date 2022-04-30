package org.hypothesis.components.client.ui.selectbutton;

import com.vaadin.shared.ui.Connect;
import org.hypothesis.components.client.ui.VHRadioButton;
import org.hypothesis.components.shared.ui.selectbutton.HRadioButtonState;
import org.hypothesis.components.ui.HRadioButton;

/**
 * @author kamil
 */
@Connect(HRadioButton.class)
public class HRadioButtonConnector extends SelectButtonConnector {

    @Override
    public HRadioButtonState getState() {
        return (HRadioButtonState) super.getState();
    }

    @Override
    public VHRadioButton getWidget() {
        return (VHRadioButton) super.getWidget();
    }

}
