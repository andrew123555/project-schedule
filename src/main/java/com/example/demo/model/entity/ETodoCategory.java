package com.example.demo.model.entity;

public enum ETodoCategory {


	    TEST,
	    MEETING,
	    OTHERS;

	    // 如果需要一個更友好的中文名稱，可以這樣做：
	    public String getDisplayName() {
	        switch (this) {
	            case TEST: return "TEST";
	            case MEETING: return "MEETING";
	            case OTHERS: return "OTHERS";
	            default: return this.name();
	        }
	    }
	}