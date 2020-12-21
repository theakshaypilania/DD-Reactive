package com.mmt.ddreactive.pojo;


public enum ApplyOn {

  COUPON_AMOUNT("COUPON_AMOUNT"),
  PRE_COUPON_SELLING_PRICE("PRE_COUPON_SELLING_PRICE"),
  POST_COUPON_SELLING_PRICE("POST_COUPON_SELLING_PRICE");

  private final String text;

  private ApplyOn(final String text) {
    this.text = text;
  }

  public static ApplyOn findAplyOnBasisString(String applyOnStr) {

    for (ApplyOn applyOn : ApplyOn.values()) {
      if (applyOnStr.equalsIgnoreCase(applyOn.name())) {
        return applyOn;
      }
    }
    return null;
  }

  public String getText() {
    return text;
  }

}
