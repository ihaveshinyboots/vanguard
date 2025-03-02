package com.vanguard.test.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vanguard.test.utils.MoneySerializer;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales_summary")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SalesSummary {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "period_start")
  private LocalDate periodStart;

  @Column(name = "period_end")
  private LocalDate periodEnd;

  @Column(name = "total_games_sold")
  private Integer totalGamesSold;

  @Column(name = "total_sales")
  @JsonSerialize(using = MoneySerializer.class)
  private BigDecimal totalSales;

  @Column(name = "game_no")
  private Byte gameNo;

  @Column(name = "total_sales_for_game")
  @JsonSerialize(using = MoneySerializer.class)
  private BigDecimal totalSalesForGame;

  public SalesSummary(
      LocalDate periodStart,
      LocalDate periodEnd,
      Integer totalGamesSold,
      BigDecimal totalSales,
      Byte gameNo,
      BigDecimal totalSalesForGame) {
    this.periodStart = periodStart;
    this.periodEnd = periodEnd;
    this.totalGamesSold = totalGamesSold;
    this.totalSales = totalSales;
    this.gameNo = gameNo;
    this.totalSalesForGame = totalSalesForGame;
  }
}
