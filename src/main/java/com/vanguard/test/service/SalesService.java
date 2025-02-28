package com.vanguard.test.service;

import com.vanguard.test.model.GameRecord;
import com.vanguard.test.model.SalesSummary;
import com.vanguard.test.repository.GameRecordRepository;
import com.vanguard.test.repository.SalesSummaryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SalesService {
  @Autowired private GameRecordRepository gameRecordRepository;
  @Autowired private SalesSummaryRepository salesSummaryRepository;

  public Page<GameRecord> getAllGameSales(Integer page) {
    Pageable pageable = PageRequest.of(page, 100);
    log.info("Getting all Game Sales");
    return gameRecordRepository.findAll(pageable);
  }

  // Fetch game sales within a specific date range with pagination
  public Page<GameRecord> getGameSalesByDateRange(
      LocalDateTime from, LocalDateTime to, Integer page) {
    Pageable pageable = PageRequest.of(page, 100);
    log.info("Getting Game Sales By Date Range from: {} to: {}", from, to);
    return gameRecordRepository.findByDateOfSaleBetween(from, to, pageable);
  }

  public Page<GameRecord> getGameSalesBySalePriceGreaterThan(Double salePrice, Integer page) {
    Pageable pageable = PageRequest.of(page, 100);
    log.info("Getting Game Sales By Sales Price > {}", salePrice);
    return gameRecordRepository.findBySalePriceGreaterThanEqual(salePrice, pageable);
  }

  public Page<GameRecord> getGameSalesBySalePriceLessThan(Double salePrice, Integer page) {
    Pageable pageable = PageRequest.of(page, 100);
    log.info("Getting Game Sales By Sales Price < {}", salePrice);
    return gameRecordRepository.findBySalePriceLessThanEqual(salePrice, pageable);
  }

  public List<SalesSummary> getTotalGamesSoldBetween(LocalDate start, LocalDate to) {
    log.info("Getting total games sold Between {}, {}", start, to);
    return salesSummaryRepository
        .findByPeriodStartGreaterThanEqualAndPeriodEndLessThanEqualAndGameNoIsNull(start, to);
  }

  public List<SalesSummary> getTotalGamesSoldBetweenByGameNo(
      LocalDate start, LocalDate to, Integer gameNo) {
    log.info("Getting total games sold Between {}, {} by GameNumber {}", start, to, gameNo);
    return salesSummaryRepository
        .findByPeriodStartGreaterThanEqualAndPeriodEndLessThanEqualAndGameNo(start, to, gameNo);
  }
}
