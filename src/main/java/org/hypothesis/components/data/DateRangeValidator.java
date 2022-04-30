package org.hypothesis.components.data;

import com.vaadin.shared.ui.datefield.Resolution;

import java.util.Date;

/**
 * @author kamil
 */
public class DateRangeValidator extends com.vaadin.data.validator.DateRangeValidator implements Validator {

    public DateRangeValidator(String message, Date minValue,
                              Date maxValue) {
        super(message, minValue, maxValue, Resolution.DAY);
    }

}
