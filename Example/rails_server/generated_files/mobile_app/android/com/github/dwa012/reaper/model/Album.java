package com.github.dwa012.reaper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Album {

  private int albumId; // Generated from DB => attribute: albumId, data-type: integer
  private int artistId; // Generated from DB => attribute: artistId, data-type: integer
  private String title; // Generated from DB => attribute: title, data-type: string
 private Date version;

  @JsonProperty("AlbumId")
  public void setAlbumId(int albumId) {
    this.albumId = albumId;
  }

  public int getAlbumId() {
    return this.albumId;
  }

  @JsonProperty("ArtistId")
  public void setArtistId(int artistId) {
    this.artistId = artistId;
  }

  public int getArtistId() {
    return this.artistId;
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
     return albumId;
  }
}
