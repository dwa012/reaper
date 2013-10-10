package com.github.dwa012.reaper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MediaType {

  private int mediaTypeId; // Generated from DB => attribute: mediaTypeId, data-type: integer
  private String name; // Generated from DB => attribute: name, data-type: string
 private Date version;

  @JsonProperty("MediaTypeId")
  public void setMediaTypeId(int mediaTypeId) {
    this.mediaTypeId = mediaTypeId;
  }

  public int getMediaTypeId() {
    return this.mediaTypeId;
  }

  @JsonProperty("Name")
  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }


  public void setVersion(Date version) {
    this.version = version;
  }

  public Date getVersion() {
    return this.version;
  }

  public int getPrimaryKey(){
     return mediaTypeId;
  }
}
