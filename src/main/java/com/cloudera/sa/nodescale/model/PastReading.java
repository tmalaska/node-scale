package com.cloudera.sa.nodescale.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PastReading {
  Long time;
  Integer reading;

  public PastReading() {}

  public PastReading(Long time, Integer reading) {
    this.time = time;
    this.reading = reading;
  }

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public Integer getReading() {
    return reading;
  }

  public void setReading(Integer reading) {
    this.reading = reading;
  }
}
