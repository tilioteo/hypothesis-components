package org.hypothesis.components.ui;

import com.vaadin.ui.themes.ValoTheme;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kamil
 */
public class HButtonPanel extends MultipleComponentPanel<HButton> {

    private final List<HButton.ClickListener> clickListeners = new ArrayList<>();
    HButton selected = null;
    private String[] captions;

    public HButtonPanel() {
        super();
        setSizeUndefined();
        addStyleName(ValoTheme.PANEL_BORDERLESS);
    }

    public void addButtonClickListener(HButton.ClickListener buttonClickListener) {
        this.clickListeners.add(buttonClickListener);

        for (HButton button : childList) {
            button.addClickListener(buttonClickListener);
        }
    }

    public void setCaptions(String[] captions) {
        this.captions = captions;

        addChildren();
    }

    protected void addChildren() {
        int i = 1;
        removeAllChildren();

        for (String caption : captions) {
            if (null == caption) {
                caption = "";
            }
            HButton button = new HButton();
            button.setCaption(caption);
            button.setData(String.format("%s_%d", this.getData() != null ? this.getData() : "", i++));

            for (HButton.ClickListener listener : clickListeners) {
                button.addClickListener(listener);
            }

            addChild(button);
        }
        updateContent();
    }

    public HButton getSelected() {
        return selected;
    }

    public void setSelected(HButton button) {
        this.selected = button;
    }

    public void clearSelection() {
        if (selected != null) {
            selected = null;
        }
        getUI().focus();
    }
}
