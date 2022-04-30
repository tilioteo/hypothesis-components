package org.hypothesis.components.ui;

import com.vaadin.ui.TextArea;
import org.hypothesis.ui.Field;

public class HTextArea extends TextArea implements Field {

    public HTextArea() {
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
