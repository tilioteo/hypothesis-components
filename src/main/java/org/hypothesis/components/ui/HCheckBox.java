package org.hypothesis.components.ui;

import org.hypothesis.components.shared.ui.selectbutton.HCheckBoxState;

/**
 * @author kamil
 */
public class HCheckBox extends SelectButton {

    public HCheckBox() {
        super();
    }

    public HCheckBox(String caption, boolean initialState) {
        super(caption, initialState);
    }

    public HCheckBox(String caption, ClickListener listener) {
        super(caption, listener);
    }

    public HCheckBox(String caption) {
        super(caption);
    }

    @Override
    protected HCheckBoxState getState() {
        return (HCheckBoxState) super.getState();
    }

}
