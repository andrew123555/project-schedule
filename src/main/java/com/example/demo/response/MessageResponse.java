package com.example.demo.response;


public class MessageResponse {
    private String message;
    private Object data; // 這可以儲存任何類型的資料，包括 String

    // 只用於訊息的建構子
    public MessageResponse(String message) {
        this.message = message;
    }

    // 用於訊息和資料 (作為 Object) 的建構子 - 這是我之前建議的
    public MessageResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    // ⭐⭐⭐ 新增此建構子 ⭐⭐⭐
    // 用於訊息和資料 (作為 String) 的建構子 - 直接解決錯誤
    public MessageResponse(String message, String data) {
        this.message = message;
        this.data = data; // 將 String 類型的資料賦值給 Object 欄位
    }

    // Getters 和 Setters (Lombok 的 @Data 會自動生成這些，或者您手動添加)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}