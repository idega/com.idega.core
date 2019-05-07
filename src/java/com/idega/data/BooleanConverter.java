package com.idega.data;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.springframework.stereotype.Service;

import com.idega.util.CoreConstants;

@Service
@Converter(autoApply = true)
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
