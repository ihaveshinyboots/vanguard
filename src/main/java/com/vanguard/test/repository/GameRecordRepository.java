package com.vanguard.test.repository;

import com.vanguard.test.model.GameRecord;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRecordRepository extends JpaRepository<GameRecord, Integer> {
  Page<GameRecord> findByDateOfSaleBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

  List<GameRecord> findByDateOfSaleBetween(LocalDateTime from, LocalDateTime to);

  Page<GameRecord> findBySalePriceGreaterThanEqual(Double salePrice, Pageable pageable);

  Page<GameRecord> findBySalePriceLessThanEqual(Double salePrice, Pageable pageable);

  Page<GameRecord> findAll(Pageable pageable);
}
