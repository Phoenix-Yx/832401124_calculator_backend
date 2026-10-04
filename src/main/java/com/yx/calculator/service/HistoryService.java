package com.yx.calculator.service;

import com.yx.calculator.dto.HistoryResponse;
import com.yx.calculator.repository.CalculationHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HistoryService {

    private final CalculationHistoryRepository repository;

    public HistoryService(CalculationHistoryRepository repository) {
        this.repository = repository;
    }

    public List<HistoryResponse> list() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream().map(HistoryResponse::new).toList();
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }
}