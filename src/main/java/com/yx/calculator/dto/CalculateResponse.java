package com.yx.calculator.dto;

import java.time.LocalDateTime;

public class CalculateResponse {
    private boolean success;
    private Long id;
    private String expression;
    private Double result;
    private LocalDateTime createdAt;

    public CalculateResponse() {}

    public CalculateResponse(boolean success, Long id, String expression, Double result, LocalDateTime createdAt) {
        this.success = success;
        this.id = id;
        this.expression = expression;
        this.result = result;
        this.createdAt = createdAt;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }
    public Double getResult() { return result; }
    public void setResult(Double result) { this.result = result; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}