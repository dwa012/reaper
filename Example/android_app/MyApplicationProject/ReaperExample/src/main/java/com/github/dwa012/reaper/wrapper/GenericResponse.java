package com.github.dwa012.reaper.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GenericResponse {

    private ArrayList<String> errros;
    private ArrayList<String> messages;

    public GenericResponse() {
        errros = new ArrayList<String>();
        messages = new ArrayList<String>();
    }

    @JsonProperty("errors")
    public ArrayList<String> getErrros() {
        return errros;
    }

    public void setErrros(ArrayList<String> errros) {
        this.errros = errros;
    }

    @JsonProperty("Messages")
    public ArrayList<String> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }
}
