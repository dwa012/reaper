package com.github.dwa012.reaper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {

  private String address; // Generated from DB => attribute: address, data-type: string
  private String city; // Generated from DB => attribute: city, data-type: string
  private String company; // Generated from DB => attribute: company, data-type: string
  private String country; // Generated from DB => attribute: country, data-type: string
  private int customerId; // Generated from DB => attribute: customerId, data-type: integer
  private String email; // Generated from DB => attribute: email, data-type: string
  private String fax; // Generated from DB => attribute: fax, data-type: string
  private String firstName; // Generated from DB => attribute: firstName, data-type: string
  private String lastName; // Generated from DB => attribute: lastName, data-type: string
  private String phone; // Generated from DB => attribute: phone, data-type: string
  private String postalCode; // Generated from DB => attribute: postalCode, data-type: string
  private String state; // Generated from DB => attribute: state, data-type: string
  private int supportRepId; // Generated from DB => attribute: supportRepId, data-type: integer
 private Date version;

  @JsonProperty("Address")
  public void setAddress(String address) {
    this.address = address;
  }

  public String getAddress() {
    return this.address;
  }

  @JsonProperty("City")
  public void setCity(String city) {
    this.city = city;
  }

  public String getCity() {
    return this.city;
  }

  @JsonProperty("Company")
  public void setCompany(String company) {
    this.company = company;
  }

  public String getCompany() {
    return this.company;
  }

  @JsonProperty("Country")
  public void setCountry(String country) {
    this.country = country;
  }

  public String getCountry() {
    return this.country;
  }

  @JsonProperty("CustomerId")
  public void setCustomerId(int customerId) {
    this.customerId = customerId;
  }

  public int getCustomerId() {
    return this.customerId;
  }

  @JsonProperty("Email")
  public void setEmail(String email) {
    this.email = email;
  }

  public String getEmail() {
    return this.email;
  }

  @JsonProperty("Fax")
  public void setFax(String fax) {
    this.fax = fax;
  }

  public String getFax() {
    return this.fax;
  }

  @JsonProperty("FirstName")
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getFirstName() {
    return this.firstName;
  }

  @JsonProperty("LastName")
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getLastName() {
    return this.lastName;
  }

  @JsonProperty("Phone")
  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getPhone() {
    return this.phone;
  }

  @JsonProperty("PostalCode")
  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getPostalCode() {
    return this.postalCode;
  }

  @JsonProperty("State")
  public void setState(String state) {
    this.state = state;
  }

  public String getState() {
    return this.state;
  }

  @JsonProperty("SupportRepId")
  public void setSupportRepId(int supportRepId) {
    this.supportRepId = supportRepId;
  }

  public int getSupportRepId() {
    return this.supportRepId;
  }


  public void setVersion(Date version) {
    this.version = version;
  }

  public Date getVersion() {
    return this.version;
  }

  public int getPrimaryKey(){
     return customerId;
  }
}
