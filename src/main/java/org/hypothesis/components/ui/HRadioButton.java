package org.hypothesis.components.ui;

import org.hypothesis.components.shared.ui.selectbutton.HRadioButtonState;

/**
 * @author kamil
 */
public class HRadioButton extends SelectButton {

    public HRadioButton() {
        super();
    }

    public HRadioButton(String caption, boolean initialState) {
        super(caption, initialState);
    }

    public HRadioButton(String caption, ClickListener listener) {
        super(caption, listener);
    }

    public HRadioButton(String caption) {
        super(caption);
    }

    @Override
    protected HRadioButtonState getState() {
        return (HRadioButtonState) super.getState();
    }

}
