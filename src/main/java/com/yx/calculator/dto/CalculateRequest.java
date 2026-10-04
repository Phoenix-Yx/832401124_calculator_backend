package com.yx.calculator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CalculateRequest {

    @NotBlank(message = "expression 不能为空")
    @Size(max = 256, message = "表达式长度不能超过 256")
    private String expression;

    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }
}