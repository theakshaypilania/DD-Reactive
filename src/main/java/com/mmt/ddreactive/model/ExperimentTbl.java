package com.mmt.ddreactive.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table("dd_reactive")
public class ExperimentTbl {

  @Id
  @Column("id")
  private int id;

  @Column("htlseq")
  private int hotelSeq;

  @Column("policy_id")
  private int policyId;
}
