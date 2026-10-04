package com.yx.calculator.service;

import com.yx.calculator.dto.CalculateRequest;
import com.yx.calculator.dto.CalculateResponse;
import com.yx.calculator.model.CalculationHistory;
import com.yx.calculator.parser.ExpressionParser;
import com.yx.calculator.repository.CalculationHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CalculationService {

    private final CalculationHistoryRepository repository;

    public CalculationService(CalculationHistoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CalculateResponse calculate(CalculateRequest request) {
        String expr = normalize(request.getExpression());
        double result = ExpressionParser.evaluate(expr);
        CalculationHistory history = new CalculationHistory(expr, result, LocalDateTime.now());
        repository.save(history);
        return new CalculateResponse(true, history.getId(), expr, result, history.getCreatedAt());
    }

    private String normalize(String expr) {
        return expr.replace('×', '*').replace('÷', '/').replace('x', '*');
    }
}