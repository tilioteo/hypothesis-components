package org.hypothesis.components.ui;

import com.vaadin.ui.DateField;
import org.hypothesis.ui.Field;

import java.util.Date;

public class HDateField extends DateField implements Field {

    public HDateField() {
        super();
    }

    @Override
    public void setValue(Date newValue) throws ReadOnlyException {
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
