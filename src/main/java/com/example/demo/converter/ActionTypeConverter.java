// src/main/java/com/example/demo/converter/ActionTypeConverter.java
package com.example.demo.converter;

import com.example.demo.model.entity.UserActivity.ActionType; 
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale; 

@Converter(autoApply = true) 
public class ActionTypeConverter implements AttributeConverter<ActionType, String> {

    @Override
    public String convertToDatabaseColumn(ActionType attribute) {
        if (attribute == null) {
            return null;
        }
       
        return attribute.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public ActionType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            return ActionType.valueOf(dbData.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            System.err.println("Warning: Unknown ActionType value from database: " + dbData);
            return null; 
        }
    }
}