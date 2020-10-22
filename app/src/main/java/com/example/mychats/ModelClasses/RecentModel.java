package com.example.mychats.ModelClasses;

public class RecentModel {
  private long time;

    RecentModel(){}

     RecentModel(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
