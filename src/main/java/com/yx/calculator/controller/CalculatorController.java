package com.yx.calculator.controller;

import com.yx.calculator.dto.*;
import com.yx.calculator.service.CalculationService;
import com.yx.calculator.service.HistoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CalculatorController {

    private final CalculationService calculationService;
    private final HistoryService historyService;

    public CalculatorController(CalculationService calculationService, HistoryService historyService) {
        this.calculationService = calculationService;
        this.historyService = historyService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<?> calculate(@Valid @RequestBody CalculateRequest request) {
        CalculateResponse response = calculationService.calculate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<HistoryResponse>> history() {
        return ResponseEntity.ok(historyService.list());
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<?> deleteHistory(@PathVariable Long id) {
        boolean ok = historyService.deleteById(id);
        if (!ok) {
            return ResponseEntity.status(404).body(new ErrorResponse("记录不存在，id=" + id));
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/history")
    public ResponseEntity<Void> clearHistory() {
        historyService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}