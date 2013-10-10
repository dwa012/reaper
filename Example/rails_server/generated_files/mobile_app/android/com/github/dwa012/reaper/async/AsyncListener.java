package com.github.dwa012.reaper.async;

import java.util.List;

public interface AsyncListener<Element> {
  public void retrievalFinished(List<Element> items);
}
