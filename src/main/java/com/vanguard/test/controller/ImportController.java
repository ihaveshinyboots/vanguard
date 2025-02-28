package com.vanguard.test.controller;

import com.vanguard.test.model.ImportRecord;
import com.vanguard.test.service.ImportService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/import")
@Slf4j
public class ImportController {

  @Autowired private ImportService importService;

  @PostMapping
  public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
    log.info("ImportController");

    try {
      List<LocalDateTime> dates = importService.processCSV(file);
      ImportRecord importRecord = importService.addCsvImportLog(file.getOriginalFilename());
      importService.aggregateSalesDataAsync(dates.get(0), dates.get(1), importRecord);

      return ResponseEntity.ok("File processed successfully");
    } catch (Exception e) {
      log.error("Error: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    }
  }
}
