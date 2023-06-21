package com.mgonzalez.roshkadevsafio.dto;

public class ErrorDetailsDTO {
    private String code;
    private String error;

    public ErrorDetailsDTO(String code, String error) {
        this.code = code;
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
