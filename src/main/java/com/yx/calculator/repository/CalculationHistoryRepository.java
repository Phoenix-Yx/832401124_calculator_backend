package com.yx.calculator.repository;

import com.yx.calculator.model.CalculationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalculationHistoryRepository extends JpaRepository<CalculationHistory, Long> {

    List<CalculationHistory> findAllByOrderByCreatedAtDesc();
}