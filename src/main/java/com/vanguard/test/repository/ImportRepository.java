package com.vanguard.test.repository;

import com.vanguard.test.model.ImportRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportRepository extends JpaRepository<ImportRecord, Integer> {}
