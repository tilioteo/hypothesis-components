package org.hypothesis.components.data;

import com.vaadin.data.util.converter.Converter.ConversionException;

import java.util.Locale;

/**
 * @author kamil
 */
public class IntegerValidator extends AbstractValidator {

    private final StringToIntegerConverter converter;

    public IntegerValidator(String message) {
        super(message);
        converter = new StringToIntegerConverter();
    }

    @Override
    public boolean isValid(Object value) {
        if (null == value || Integer.class.isAssignableFrom(value.getClass())) {
            return true;
        }

        if (!(value instanceof String)) {
            return false;
        }

        try {
            @SuppressWarnings("unused")
            Integer integerValue = converter.convertToModel((String) value, Integer.class, Locale.ENGLISH);
            return true;
        } catch (ConversionException e) {
        }
        return false;
    }

}
