package com.mmt.ddreactive.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DimensionType {

  @JsonProperty("text")
  private String text;

  @JsonProperty("apWindowDimension")
  private boolean apWindowDimension;
  // primitive double as null not allowed
  @JsonProperty("weight")
  private double weight;
  // Wrapper Double as can be null.
  @JsonProperty("scale")
  private Double scale;

  private Double dimensionWeight;

  public DimensionType() {

  }

  /**
   * @param text
   */
  public DimensionType(final String text, boolean apWindowDimension, double weight, Double scale) {
    this.text = text;
    this.apWindowDimension = apWindowDimension;
    this.weight = weight;
    this.scale = scale;
  }

  @JsonProperty("apWindowDimension")
  public boolean isAPWindowDimension() {
    return apWindowDimension;
  }

  public void setAPWindowDimension(boolean apWindowDimension) {
    this.apWindowDimension = apWindowDimension;
  }

  @JsonProperty("weight")
  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  @JsonProperty("scale")
  public Double getScale() {
    return scale;
  }

  public void setScale(Double scale) {
    this.scale = scale;
  }

  @JsonProperty("text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Double getDimensionWeight() {
    return dimensionWeight;
  }

  public void setDimensionWeight(Double dimensionWeight) {
    this.dimensionWeight = dimensionWeight;
  }

}
