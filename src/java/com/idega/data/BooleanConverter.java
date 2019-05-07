package com.idega.data;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.idega.util.CoreConstants;

@Converter(autoApply = false)
public class BooleanConverter implements AttributeConverter<Boolean, Character> {
 
    @Override
    public Character convertToDatabaseColumn(Boolean attribute) {
        if (attribute != null) {
            if (attribute) {
                return CoreConstants.CHAR_Y;
            } else {
                return CoreConstants.CHAR_N;
            }
                 
        }
        return null;
    }
 
    @Override
    public Boolean convertToEntityAttribute(Character dbData) {
    	if (dbData == null) {
    		return null;
    	}
    	return dbData.equals(CoreConstants.CHAR_Y);
    }
     
}
