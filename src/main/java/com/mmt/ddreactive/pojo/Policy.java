package com.mmt.ddreactive.pojo;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Policy {


  @JsonProperty("id")
  private String id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("dimensions")
  private Map<String, DimensionType> dimensions;

  @JsonProperty("applyOn")
  private ApplyOn applyOn;

  @JsonProperty("version")
  private String version;

  @JsonProperty("updateOn")
  private Date updateOn;


  public Policy() {

  }

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("dimensions")
  public Map<String, DimensionType> getDimensions() {
    return this.dimensions;
  }

  public void setDimensions(Map<String, DimensionType> dimensions) {
    this.dimensions = dimensions;
  }

  @JsonProperty("applyOn")
  public ApplyOn getApplyOn() {
    return applyOn;
  }

  public void setApplyOn(ApplyOn applyOn) {
    this.applyOn = applyOn;
  }

  @JsonProperty("version")
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @JsonProperty("updateOn")
  @JsonFormat(pattern = "EEE MMM dd HH:mm:ss z yyyy")
  public Date getUpdateOn() {
    return updateOn;
  }

  public void setUpdateOn(Date updateOn) {
    this.updateOn = updateOn;
  }

}
