package org.hypothesis.components.data;

/**
 * @author kamil
 */
public abstract class AbstractValidator implements Validator {

    private final String message;

    public AbstractValidator(String message) {
        this.message = message;
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        if (!isValid(value)) {
            throw new InvalidValueException(message);
        }
    }

}
