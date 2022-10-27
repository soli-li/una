package com.una.system.manager.model.converter;

import com.una.common.Constants;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class BooleanAttributeConverter implements AttributeConverter<Boolean, String> {

  @Override
  public String convertToDatabaseColumn(final Boolean value) {
    return value != null && value ? Constants.TRUE.toString() : Constants.FALSE.toString();
  }

  @Override
  public Boolean convertToEntityAttribute(final String value) {
    return Constants.TRUE.toString().equals(value);
  }
}
