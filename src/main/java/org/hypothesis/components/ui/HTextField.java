package org.hypothesis.components.ui;

import com.vaadin.ui.TextField;
import org.hypothesis.ui.Field;

public class HTextField extends TextField implements Field {

    public HTextField() {
        super();
    }

    @Override
    public void setValue(String newValue) throws ReadOnlyException {
        boolean readOnly = false;
        if (isReadOnly()) {
            readOnly = true;
            setReadOnly(false);
        }
        super.setValue(newValue);

        if (readOnly) {
            setReadOnly(true);
        }
    }

}
