package org.hypothesis.components.data;

/**
 * @author kamil
 */
public class EmptyValidator extends AbstractValidator {

    public EmptyValidator(String message) {
        super(message);
    }

    @Override
    public boolean isValid(Object value) {
        if (!(value instanceof String)) {
            return false;
        }

        return ((String) value).trim().length() > 0;
    }

}
