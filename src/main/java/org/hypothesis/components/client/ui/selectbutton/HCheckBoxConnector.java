package org.hypothesis.components.client.ui.selectbutton;

import com.vaadin.shared.ui.Connect;
import org.hypothesis.components.client.ui.VHCheckBox;
import org.hypothesis.components.shared.ui.selectbutton.HCheckBoxState;
import org.hypothesis.components.ui.HCheckBox;

/**
 * @author kamil
 */
@Connect(HCheckBox.class)
public class HCheckBoxConnector extends SelectButtonConnector {

    @Override
    public HCheckBoxState getState() {
        return (HCheckBoxState) super.getState();
    }

    @Override
    public VHCheckBox getWidget() {
        return (VHCheckBox) super.getWidget();
    }

}
