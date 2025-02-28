package com.vanguard.test.repository;

import com.vanguard.test.model.SalesSummary;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesSummaryRepository extends JpaRepository<SalesSummary, Integer> {

  List<SalesSummary> findByPeriodStartGreaterThanEqualAndPeriodEndLessThanEqualAndGameNoIsNull(
      LocalDate from, LocalDate to);

  List<SalesSummary> findByPeriodStartGreaterThanEqualAndPeriodEndLessThanEqualAndGameNo(
      LocalDate from, LocalDate to, Integer gameNo);
}
