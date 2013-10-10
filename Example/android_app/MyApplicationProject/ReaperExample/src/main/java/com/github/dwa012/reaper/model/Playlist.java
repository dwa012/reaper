package com.github.dwa012.reaper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Playlist {

  private String name; // Generated from DB => attribute: name, data-type: string
  private int playlistId; // Generated from DB => attribute: playlistId, data-type: integer
 private Date version;

  @JsonProperty("Name")
  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  @JsonProperty("PlaylistId")
  public void setPlaylistId(int playlistId) {
    this.playlistId = playlistId;
  }

  public int getPlaylistId() {
    return this.playlistId;
  }


  public void setVersion(Date version) {
    this.version = version;
  }

  public Date getVersion() {
    return this.version;
  }

  public int getPrimaryKey(){
     return playlistId;
  }
}
