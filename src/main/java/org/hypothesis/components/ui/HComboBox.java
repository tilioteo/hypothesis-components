package org.hypothesis.components.ui;

import com.vaadin.ui.ComboBox;
import org.hypothesis.ui.Field;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
public class HComboBox extends ComboBox implements Field {

    public HComboBox() {
        super();
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException {
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
