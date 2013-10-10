package com.github.dwa012.reaper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Invoice {

  private String billingAddress; // Generated from DB => attribute: billingAddress, data-type: string
  private String billingCity; // Generated from DB => attribute: billingCity, data-type: string
  private String billingCountry; // Generated from DB => attribute: billingCountry, data-type: string
  private String billingPostalCode; // Generated from DB => attribute: billingPostalCode, data-type: string
  private String billingState; // Generated from DB => attribute: billingState, data-type: string
  private int customerId; // Generated from DB => attribute: customerId, data-type: integer
  private Date invoiceDate; // Generated from DB => attribute: invoiceDate, data-type: datetime
  private int invoiceId; // Generated from DB => attribute: invoiceId, data-type: integer
  private float  total; // Generated from DB => attribute: total, data-type: decimal
 private Date version;

  @JsonProperty("BillingAddress")
  public void setBillingAddress(String billingAddress) {
    this.billingAddress = billingAddress;
  }

  public String getBillingAddress() {
    return this.billingAddress;
  }

  @JsonProperty("BillingCity")
  public void setBillingCity(String billingCity) {
    this.billingCity = billingCity;
  }

  public String getBillingCity() {
    return this.billingCity;
  }

  @JsonProperty("BillingCountry")
  public void setBillingCountry(String billingCountry) {
    this.billingCountry = billingCountry;
  }

  public String getBillingCountry() {
    return this.billingCountry;
  }

  @JsonProperty("BillingPostalCode")
  public void setBillingPostalCode(String billingPostalCode) {
    this.billingPostalCode = billingPostalCode;
  }

  public String getBillingPostalCode() {
    return this.billingPostalCode;
  }

  @JsonProperty("BillingState")
  public void setBillingState(String billingState) {
    this.billingState = billingState;
  }

  public String getBillingState() {
    return this.billingState;
  }

  @JsonProperty("CustomerId")
  public void setCustomerId(int customerId) {
    this.customerId = customerId;
  }

  public int getCustomerId() {
    return this.customerId;
  }

  @JsonProperty("InvoiceDate")
  public void setInvoiceDate(Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public Date getInvoiceDate() {
    return this.invoiceDate;
  }

  @JsonProperty("InvoiceId")
  public void setInvoiceId(int invoiceId) {
    this.invoiceId = invoiceId;
  }

  public int getInvoiceId() {
    return this.invoiceId;
  }

  @JsonProperty("Total")
  public void setTotal(float  total) {
    this.total = total;
  }

  public float  getTotal() {
    return this.total;
  }


  public void setVersion(Date version) {
    this.version = version;
  }

  public Date getVersion() {
    return this.version;
  }

  public int getPrimaryKey(){
     return invoiceId;
  }
}
