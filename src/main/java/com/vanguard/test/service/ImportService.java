package com.vanguard.test.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.vanguard.test.model.GameRecord;
import com.vanguard.test.model.ImportRecord;
import com.vanguard.test.model.SalesSummary;
import com.vanguard.test.repository.GameRecordRepository;
import com.vanguard.test.repository.ImportRepository;
import com.vanguard.test.repository.SalesSummaryRepository;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class ImportService {
  @Autowired private JdbcTemplate jdbcTemplate;
  @Autowired private GameRecordRepository gameRecordRepository;
  @Autowired private SalesSummaryRepository salesSummaryRepository;
  @Autowired private ImportRepository importRepository;

  public List<LocalDateTime> processCSV(MultipartFile file) throws Exception {
    List<LocalDateTime> dates = new ArrayList<>();

    try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

      CsvToBean<GameRecord> csvToBean =
          new CsvToBeanBuilder<GameRecord>(reader)
              .withType(GameRecord.class)
              .withIgnoreLeadingWhiteSpace(true)
              .build();

      Iterator<GameRecord> recordIterator = csvToBean.iterator();
      log.info("Validating records...");
      int rowNumber = 1;

      // Initialize variables for calculating start and end dates
      LocalDateTime startDate = null;
      LocalDateTime endDate = null;

      while (recordIterator.hasNext()) {
        GameRecord record = recordIterator.next();
        validateRecord(record, rowNumber);
        rowNumber++;

        // Calculate the earliest (startDate) and latest (endDate) date of sale from the records
        LocalDateTime recordDate = record.getDateOfSale();
        if (startDate == null || recordDate.isBefore(startDate)) {
          startDate = recordDate; // Update start date if a new earlier date is found
        }
        if (endDate == null || recordDate.isAfter(endDate)) {
          endDate = recordDate; // Update end date if a new later date is found
        }
      }

      log.info("Loading data in DB...");

      loadDataInFile(multipartToFile(file));
      dates.add(startDate);
      dates.add(endDate);
      return dates;
    }
  }

  private void validateRecord(GameRecord record, int rowNumber) {

    if (record.getId() != rowNumber) {
      throw new IllegalArgumentException("Invalid ID at row " + rowNumber);
    }

    if (record.getGameNo() < 1 || record.getGameNo() > 100) {
      throw new IllegalArgumentException("Invalid game_no at row " + rowNumber);
    }

    if (record.getGameName() == null || record.getGameName().length() > 20) {
      throw new IllegalArgumentException("Invalid game_name at row " + rowNumber);
    }

    if (record.getGameCode() == null || record.getGameCode().length() > 5) {
      throw new IllegalArgumentException("Invalid game_code at row " + rowNumber);
    }

    if (record.getType() != 1 && record.getType() != 2) {
      throw new IllegalArgumentException("Invalid type at row " + rowNumber);
    }

    if (record.getCostPrice().compareTo(new BigDecimal("100")) > 0) {
      throw new IllegalArgumentException("Invalid cost_price at row " + rowNumber);
    }

    BigDecimal expectedSalePrice =
        record.getCostPrice().multiply(record.getTax().add(new BigDecimal(1)));
    if (record.getSalePrice().compareTo(expectedSalePrice.setScale(2, RoundingMode.DOWN)) != 0) {
      throw new IllegalArgumentException("Invalid sale_price at row " + rowNumber);
    }
  }

  private void loadDataInFile(File tempFile) {
    String sql =
        "LOAD DATA LOCAL INFILE '"
            + tempFile.getAbsolutePath().replace("'", "''")
            + "' INTO TABLE game_sales "
            + "FIELDS TERMINATED BY ',' "
            + "LINES TERMINATED BY '\n' "
            + "IGNORE 1 LINES";

    jdbcTemplate.update(sql);
  }

  private File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException {
    File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + "tmpFile");
    multipart.transferTo(convFile);
    return convFile;
  }

  public ImportRecord addCsvImportLog(String fileName) {
    log.info("Adding import log for filename {}", fileName);
    return importRepository.save(new ImportRecord(null, fileName, "Updating"));
  }

  @Async
  public void aggregateSalesDataAsync(
      LocalDateTime startDate, LocalDateTime endDate, ImportRecord importRecord) {
    log.info("Aggregating sales data from {} to {}", startDate, endDate);

    while (!startDate.toLocalDate().isAfter(endDate.toLocalDate())) {
      aggregateDailySales(startDate.toLocalDate());
      startDate = startDate.plusDays(1);
    }

    importRepository.save(
        new ImportRecord(importRecord.getId(), importRecord.getFileName(), "DONE"));
  }

  public void aggregateDailySales(LocalDate date) {
    log.info("Aggregating sales data for the day: {}", date);

    List<GameRecord> gameSales =
        gameRecordRepository.findByDateOfSaleBetween(date.atStartOfDay(), date.atTime(23, 59, 59));

    Integer totalGamesSold = gameSales.size();
    BigDecimal totalSales =
        gameSales.stream().map(GameRecord::getSalePrice).reduce(BigDecimal.ZERO, BigDecimal::add);

    Map<Byte, Long> gamesSoldPerGame =
        gameSales.stream()
            .collect(Collectors.groupingBy(GameRecord::getGameNo, Collectors.counting()));

    Map<Byte, BigDecimal> salesPerGame =
        gameSales.stream()
            .collect(
                Collectors.groupingBy(
                    GameRecord::getGameNo,
                    Collectors.mapping(
                        GameRecord::getSalePrice,
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

    salesSummaryRepository.save(
        new SalesSummary(date, date, totalGamesSold, totalSales, null, totalSales));

    salesPerGame.forEach(
        (gameNo, totalSaleForGame) -> {
          long totalGamesForGame = gamesSoldPerGame.getOrDefault(gameNo, 0L);
          salesSummaryRepository.save(
              new SalesSummary(
                  date, date, (int) totalGamesForGame, totalSaleForGame, gameNo, totalSaleForGame));
        });

    log.info("Finished aggregating daily sales for {}", date);
  }
}
