package com.vanguard.test.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "import_record")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportRecord {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "status")
  private String status;
}
