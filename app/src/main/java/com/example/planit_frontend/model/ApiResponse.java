package com.example.planit_frontend.model;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    private T data;
    private String type;
    private boolean success;  // Add a success flag to indicate success or failure

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
