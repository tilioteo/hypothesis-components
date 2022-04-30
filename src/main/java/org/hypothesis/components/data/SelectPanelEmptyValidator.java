package org.hypothesis.components.data;

import java.util.Collection;

/**
 * @author kamil
 */
public class SelectPanelEmptyValidator extends EmptyValidator {

    public SelectPanelEmptyValidator(String message) {
        super(message);
    }

    @Override
    public boolean isValid(Object value) {
        return value instanceof Collection<?> && !((Collection<?>) value).isEmpty();
    }

}
