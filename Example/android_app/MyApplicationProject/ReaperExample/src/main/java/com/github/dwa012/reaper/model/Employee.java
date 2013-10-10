package com.github.dwa012.reaper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {

  private String address; // Generated from DB => attribute: address, data-type: string
  private Date birthDate; // Generated from DB => attribute: birthDate, data-type: datetime
  private String city; // Generated from DB => attribute: city, data-type: string
  private String country; // Generated from DB => attribute: country, data-type: string
  private String email; // Generated from DB => attribute: email, data-type: string
  private int employeeId; // Generated from DB => attribute: employeeId, data-type: integer
  private String fax; // Generated from DB => attribute: fax, data-type: string
  private String firstName; // Generated from DB => attribute: firstName, data-type: string
  private Date hireDate; // Generated from DB => attribute: hireDate, data-type: datetime
  private String lastName; // Generated from DB => attribute: lastName, data-type: string
  private String phone; // Generated from DB => attribute: phone, data-type: string
  private String postalCode; // Generated from DB => attribute: postalCode, data-type: string
  private int reportsTo; // Generated from DB => attribute: reportsTo, data-type: integer
  private String state; // Generated from DB => attribute: state, data-type: string
  private String title; // Generated from DB => attribute: title, data-type: string
 private Date version;

  @JsonProperty("Address")
  public void setAddress(String address) {
    this.address = address;
  }

  public String getAddress() {
    return this.address;
  }

  @JsonProperty("BirthDate")
  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public Date getBirthDate() {
    return this.birthDate;
  }

  @JsonProperty("City")
  public void setCity(String city) {
    this.city = city;
  }

  public String getCity() {
    return this.city;
  }

  @JsonProperty("Country")
  public void setCountry(String country) {
    this.country = country;
  }

  public String getCountry() {
    return this.country;
  }

  @JsonProperty("Email")
  public void setEmail(String email) {
    this.email = email;
  }

  public String getEmail() {
    return this.email;
  }

  @JsonProperty("EmployeeId")
  public void setEmployeeId(int employeeId) {
    this.employeeId = employeeId;
  }

  public int getEmployeeId() {
    return this.employeeId;
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

  @JsonProperty("HireDate")
  public void setHireDate(Date hireDate) {
    this.hireDate = hireDate;
  }

  public Date getHireDate() {
    return this.hireDate;
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

  @JsonProperty("ReportsTo")
  public void setReportsTo(int reportsTo) {
    this.reportsTo = reportsTo;
  }

  public int getReportsTo() {
    return this.reportsTo;
  }

  @JsonProperty("State")
  public void setState(String state) {
    this.state = state;
  }

  public String getState() {
    return this.state;
  }

  @JsonProperty("Title")
  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return this.title;
  }


  public void setVersion(Date version) {
    this.version = version;
  }

  public Date getVersion() {
    return this.version;
  }

  public int getPrimaryKey(){
     return employeeId;
  }
}
