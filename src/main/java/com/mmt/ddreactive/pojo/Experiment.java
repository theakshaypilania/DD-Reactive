package com.mmt.ddreactive.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Experiment {
  private String name;
  private int id;
  private List<Dimension> dimList;
}
