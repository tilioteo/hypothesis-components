package org.hypothesis.components.data;

import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.validator.DoubleRangeValidator;

import java.util.Locale;

/**
 * @author kamil
 */
public class NumberRangeValidator extends DoubleRangeValidator implements Validator {

    private final StringToDoubleConverter converter;

    public NumberRangeValidator(String message, Double minValue, Double maxValue) {
        super(message, minValue, maxValue);
        converter = new StringToDoubleConverter();
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        Double doubleValue = null;
        if (value != null && Double.class.isAssignableFrom(value.getClass())) {
            doubleValue = (Double) value;
        } else if (value instanceof String) {
            try {
                doubleValue = converter.convertToModel((String) value, Double.class, Locale.ENGLISH);
            } catch (ConversionException e) {
            }
        }

        super.validate(doubleValue);
    }

}
