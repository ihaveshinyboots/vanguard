package com.vanguard.test.controller;

import com.vanguard.test.model.GameRecord;
import com.vanguard.test.model.SalesSummary;
import com.vanguard.test.model.SalesSummaryResponse;
import com.vanguard.test.repository.SalesSummaryRepository;
import com.vanguard.test.service.SalesService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class SalesController {

  @Autowired private SalesService salesService;
  @Autowired private SalesSummaryRepository salesSummaryRepository;

  @GetMapping("/getGameSales")
  public ResponseEntity<Page<GameRecord>> getGameSales(
      @RequestParam(required = false) LocalDateTime from,
      @RequestParam(required = false) LocalDateTime to,
      @RequestParam(required = false) Double salePrice,
      @RequestParam(required = false) String operator,
      @RequestParam(defaultValue = "0") Integer page) {
    log.info("SalesController :: getGameSales");
    Page<GameRecord> gameSales;

    if (from != null && to != null) {
      gameSales = salesService.getGameSalesByDateRange(from, to, page);
    } else if (salePrice != null && operator != null) {
      if ("greaterThan".equalsIgnoreCase(operator)) {
        gameSales = salesService.getGameSalesBySalePriceGreaterThan(salePrice, page);
      } else if ("lesserThan".equalsIgnoreCase(operator)) {
        gameSales = salesService.getGameSalesBySalePriceLessThan(salePrice, page);
      } else {
        return ResponseEntity.badRequest().body(null);
      }
    } else {
      gameSales = salesService.getAllGameSales(page);
    }
    return ResponseEntity.ok(gameSales);
  }

  @GetMapping("/getTotalSales")
  public ResponseEntity<String> getTotalSales(
      @RequestParam() LocalDate from,
      @RequestParam() LocalDate to,
      @RequestParam() String type,
      @RequestParam(required = false) Integer gameNo) {

    log.info("SalesController :: getTotalSales");
    List<SalesSummary> salesSummaries = null;

    StringBuilder responseString = new StringBuilder();

    if (from != null && to != null) {
      if ("noOfGames".equals(type)) {
        salesSummaries = salesService.getTotalGamesSoldBetween(from, to);
        List<SalesSummaryResponse> responseList =
            salesSummaries.stream()
                .map(
                    ss ->
                        new SalesSummaryResponse(
                            ss.getPeriodStart().toString(),
                            ss.getTotalGamesSold(),
                            ss.getTotalSales()))
                .toList();
        responseList.forEach(
            response -> responseString.append(response.toTotalNoOfGamesSoldString()).append("\n"));
      }
      if ("sales".equals(type)) {
        if (gameNo == null) salesSummaries = salesService.getTotalGamesSoldBetween(from, to);
        else salesSummaries = salesService.getTotalGamesSoldBetweenByGameNo(from, to, gameNo);

        List<SalesSummaryResponse> responseList =
            salesSummaries.stream()
                .map(
                    ss ->
                        new SalesSummaryResponse(
                            ss.getPeriodStart().toString(),
                            ss.getTotalGamesSold(),
                            ss.getTotalSales()))
                .toList();
        responseList.forEach(
            response -> responseString.append(response.toTotalSalesGeneratedString()).append("\n"));
      }

      return ResponseEntity.ok(responseString.toString());
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + "from and to null");
  }
}
