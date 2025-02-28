package com.vanguard.test.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "game_sales")
@Getter
@Setter
public class GameRecord {
  @Id
  @Column(name = "id")
  @CsvBindByName(column = "id")
  private Integer id;

  @Column(name = "game_no")
  @CsvBindByName(column = "game_no")
  private Byte gameNo;

  @Column(name = "game_name")
  @CsvBindByName(column = "game_name")
  private String gameName;

  @Column(name = "game_code")
  @CsvBindByName(column = "game_code")
  private String gameCode;

  @Column(name = "type")
  @CsvBindByName(column = "type")
  private Byte type;

  @Column(name = "cost_price")
  @CsvBindByName(column = "cost_price")
  private BigDecimal costPrice;

  @Column(name = "tax")
  @CsvBindByName(column = "tax")
  private BigDecimal tax;

  @Column(name = "sale_price")
  @CsvBindByName(column = "sale_price")
  private BigDecimal salePrice;

  @Column(name = "date_of_sale")
  @CsvBindByName(column = "date_of_sale")
  @CsvDate(value = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime dateOfSale;
}
