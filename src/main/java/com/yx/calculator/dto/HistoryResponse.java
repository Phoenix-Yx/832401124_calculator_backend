package com.yx.calculator.dto;

import com.yx.calculator.model.CalculationHistory;
import java.time.LocalDateTime;

public class HistoryResponse {
    private Long id;
    private String expression;
    private Double result;
    private LocalDateTime createdAt;

    public HistoryResponse(CalculationHistory h) {
        this.id = h.getId();
        this.expression = h.getExpression();
        this.result = h.getResult();
        this.createdAt = h.getCreatedAt();
    }

    public Long getId() { return id; }
    public String getExpression() { return expression; }
    public Double getResult() { return result; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}