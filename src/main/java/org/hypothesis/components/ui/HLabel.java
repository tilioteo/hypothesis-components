package org.hypothesis.components.ui;

import com.vaadin.ui.Label;

import static com.vaadin.shared.ui.label.ContentMode.HTML;

public class HLabel extends Label {

    public HLabel() {
        super();
        setContentMode(HTML);
    }

    @Override
    public String getCaption() {
        return getValue();
    }

    @Override
    public void setCaption(String caption) {
        setValue(caption);
    }

}
