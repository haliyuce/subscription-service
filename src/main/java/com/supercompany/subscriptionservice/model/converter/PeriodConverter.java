package com.supercompany.subscriptionservice.model.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Period;

@Converter
public class PeriodConverter implements AttributeConverter<Period, String> {
    @Override
    public String convertToDatabaseColumn(Period period) {
        return period.toString();
    }

    @Override
    public Period convertToEntityAttribute(String s) {
        return Period.parse(s);
    }
}
