package com.mmt.ddreactive.repository;

import com.mmt.ddreactive.model.ExperimentTbl;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ExperimentRepository extends R2dbcRepository<ExperimentTbl, Integer> {
  @Query("select * from dd_reactive where id in (:listOfIds)")
  Flux<ExperimentTbl> findAllByIds(List<Integer> listOfIds);
}
