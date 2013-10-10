package com.github.dwa012.reaper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Artist {

  private int artistId; // Generated from DB => attribute: artistId, data-type: integer
  private String name; // Generated from DB => attribute: name, data-type: string
 private Date version;

  @JsonProperty("ArtistId")
  public void setArtistId(int artistId) {
    this.artistId = artistId;
  }

  public int getArtistId() {
    return this.artistId;
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
     return artistId;
  }
}
