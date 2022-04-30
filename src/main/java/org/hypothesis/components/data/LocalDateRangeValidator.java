package org.hypothesis.components.data;

import com.vaadin.data.validator.RangeValidator;

import java.time.LocalDate;

public class LocalDateRangeValidator extends RangeValidator<LocalDate> {

    public LocalDateRangeValidator(String errorMessage, LocalDate minValue, LocalDate maxValue) {
        super(errorMessage, LocalDate.class, minValue, maxValue);
    }
}
