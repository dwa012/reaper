package com.github.dwa012.reaper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Track {

  private int albumId; // Generated from DB => attribute: albumId, data-type: integer
  private int bytes; // Generated from DB => attribute: bytes, data-type: integer
  private String composer; // Generated from DB => attribute: composer, data-type: string
  private int genreId; // Generated from DB => attribute: genreId, data-type: integer
  private int mediaTypeId; // Generated from DB => attribute: mediaTypeId, data-type: integer
  private int milliseconds; // Generated from DB => attribute: milliseconds, data-type: integer
  private String name; // Generated from DB => attribute: name, data-type: string
  private int trackId; // Generated from DB => attribute: trackId, data-type: integer
  private float  unitPrice; // Generated from DB => attribute: unitPrice, data-type: decimal
 private Date version;

  @JsonProperty("AlbumId")
  public void setAlbumId(int albumId) {
    this.albumId = albumId;
  }

  public int getAlbumId() {
    return this.albumId;
  }

  @JsonProperty("Bytes")
  public void setBytes(int bytes) {
    this.bytes = bytes;
  }

  public int getBytes() {
    return this.bytes;
  }

  @JsonProperty("Composer")
  public void setComposer(String composer) {
    this.composer = composer;
  }

  public String getComposer() {
    return this.composer;
  }

  @JsonProperty("GenreId")
  public void setGenreId(int genreId) {
    this.genreId = genreId;
  }

  public int getGenreId() {
    return this.genreId;
  }

  @JsonProperty("MediaTypeId")
  public void setMediaTypeId(int mediaTypeId) {
    this.mediaTypeId = mediaTypeId;
  }

  public int getMediaTypeId() {
    return this.mediaTypeId;
  }

  @JsonProperty("Milliseconds")
  public void setMilliseconds(int milliseconds) {
    this.milliseconds = milliseconds;
  }

  public int getMilliseconds() {
    return this.milliseconds;
  }

  @JsonProperty("Name")
  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
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
     return trackId;
  }
}
