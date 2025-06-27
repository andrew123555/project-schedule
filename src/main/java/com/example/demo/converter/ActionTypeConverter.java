// src/main/java/com/example/demo/converter/ActionTypeConverter.java
package com.example.demo.converter;

import com.example.demo.model.entity.UserActivity.ActionType; // 確保路徑正確
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale; // 用於大小寫轉換

@Converter(autoApply = true) // autoApply = true 會自動將此轉換器應用到所有使用 ActionType 的字段
public class ActionTypeConverter implements AttributeConverter<ActionType, String> {

    @Override
    public String convertToDatabaseColumn(ActionType attribute) {
        if (attribute == null) {
            return null;
        }
        // 將 Java 枚舉的大寫名稱轉換為小寫存入數據庫
        // 根據你的需求，可以選擇轉換為全部小寫，或保持原樣
        // 由於你的數據庫是小寫，這裡我們轉換為小寫。
        return attribute.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public ActionType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            // 從數據庫讀取小寫的字串，轉換為大寫後再匹配 Java 枚舉
            return ActionType.valueOf(dbData.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            // 處理無法匹配的情況，例如數據庫中有無法識別的 ActionType 值
            // 這裡可以選擇拋出異常、返回 null、或者返回一個默認值
            System.err.println("Warning: Unknown ActionType value from database: " + dbData);
            // 根據你的業務邏輯，你可能想在這裡記錄日誌或拋出一個特定的 RuntimeException
            // 或者返回一個表示 "未知" 的 ActionType 枚舉
            return null; // 或者 ActionType.UNKNOWN; 如果你定義了 UNKNOWN 枚舉
        }
    }
}