package org.hypothesis.components.data;

import com.vaadin.data.util.converter.Converter.ConversionException;

import java.util.Locale;

/**
 * @author kamil
 */
public class NumberValidator extends AbstractValidator {

    private final StringToDoubleConverter converter;

    public NumberValidator(String message) {
        super(message);
        converter = new StringToDoubleConverter();
    }

    @Override
    public boolean isValid(Object value) {
        if (null == value || Number.class.isAssignableFrom(value.getClass())) {
            return true;
        }

        if (!(value instanceof String)) {
            return false;
        }

        try {
            @SuppressWarnings("unused")
            Number numberValue = converter.convertToModel((String) value, Double.class, Locale.ENGLISH);
            return true;
        } catch (ConversionException e) {
        }
        return false;
    }

}
