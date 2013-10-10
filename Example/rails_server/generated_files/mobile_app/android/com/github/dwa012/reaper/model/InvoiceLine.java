package com.github.dwa012.reaper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceLine {

  private int invoiceId; // Generated from DB => attribute: invoiceId, data-type: integer
  private int invoiceLineId; // Generated from DB => attribute: invoiceLineId, data-type: integer
  private int quantity; // Generated from DB => attribute: quantity, data-type: integer
  private int trackId; // Generated from DB => attribute: trackId, data-type: integer
  private float  unitPrice; // Generated from DB => attribute: unitPrice, data-type: decimal
 private Date version;

  @JsonProperty("InvoiceId")
  public void setInvoiceId(int invoiceId) {
    this.invoiceId = invoiceId;
  }

  public int getInvoiceId() {
    return this.invoiceId;
  }

  @JsonProperty("InvoiceLineId")
  public void setInvoiceLineId(int invoiceLineId) {
    this.invoiceLineId = invoiceLineId;
  }

  public int getInvoiceLineId() {
    return this.invoiceLineId;
  }

  @JsonProperty("Quantity")
  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public int getQuantity() {
    return this.quantity;
  }

  @JsonProperty("TrackId")
  public void setTrackId(int trackId) {
    this.trackId = trackId;
  }

  public int getTrackId() {
    return this.trackId;
  }

  @JsonProperty("UnitPrice")
  public void setUnitPrice(float  unitPrice) {
    this.unitPrice = unitPrice;
  }

  public float  getUnitPrice() {
    return this.unitPrice;
  }


  public void setVersion(Date version) {
    this.version = version;
  }

  public Date getVersion() {
    return this.version;
  }

  public int getPrimaryKey(){
     return invoiceLineId;
  }
}
