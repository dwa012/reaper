package com.github.dwa012.reaper.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dwa012.reaper.model.Track;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TrackWrapper {

	private java.util.ArrayList<String> errors;
	private int count;
	private String version;
	private java.util.ArrayList<Track> results;

	public TrackWrapper(){
		results = new java.util.ArrayList<Track>();
	}

	@JsonProperty("items")
	public java.util.ArrayList<Track> getResults() {
		return results;
	}

	@JsonProperty("items")
	public void setResults(java.util.ArrayList<Track> results) {
		this.results = results;
	}

	@JsonProperty("version")
	public String getVersion() {
		return version;
	}

	@JsonProperty("version")
	public void setVersion(String version) {
		this.version = version;
	}

	@JsonProperty("errors")
	public final java.util.ArrayList<String> getErrors() {
		return errors;
	}

	@JsonProperty("errors")
	public final void setErrors(java.util.ArrayList<String> errors) {
		this.errors = errors;
	}

	@JsonProperty("count")
	public final int getCount() {
		return count;
	}

	@JsonProperty("count")
	public final void setCount(int count) {
		this.count = count;
	}
}
