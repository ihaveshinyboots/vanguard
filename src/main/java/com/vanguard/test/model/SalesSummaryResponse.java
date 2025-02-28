package com.vanguard.test.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
public class SalesSummaryResponse {
  private String date;
  private Integer totalGamesSold;
  private BigDecimal totalSales;

  public String toTotalNoOfGamesSoldString() {
    return "Date: " + date + ", Total Games Sold: " + totalGamesSold;
  }

  public String toTotalSalesGeneratedString() {
    return "Date: " + date + ", Total Sales: $" + totalSales;
  }
}
