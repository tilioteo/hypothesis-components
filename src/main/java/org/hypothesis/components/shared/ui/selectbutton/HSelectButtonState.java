package org.hypothesis.components.shared.ui.selectbutton;

import com.vaadin.shared.AbstractFieldState;

/**
 * @author kamil
 */
public class HSelectButtonState extends AbstractFieldState {
    public int clickShortcutKeyCode = 0;
    public boolean checked = false;
    public LabelPosition labelPosition = LabelPosition.Right;
    public boolean labelVisible = true;
    /**
     * If caption should be rendered in HTML
     */
    public boolean htmlContentAllowed = false;
    public String iconAltText = "";

    {
        primaryStyleName = "v-selectbutton";
    }

    public enum LabelPosition {
        Left, Right, Top, Bottom
    }

}
