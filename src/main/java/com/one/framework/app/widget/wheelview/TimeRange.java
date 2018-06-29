package com.one.framework.app.widget.wheelview;


import java.util.Date;

/**
 * Created by wpm on 2017/1/13.
 */

public class TimeRange {

  /**
   * startTime : 2017-01-13 15:05:25
   * endTime : 2017-01-13 17:00:00
   */

  private String startTime;
  private String endTime;

  public Date getStartTime() {
    return CommonWheelView.dateTimeFromStr(startTime);
  }

  public void setStartTime(Date start_time) {
    this.startTime = CommonWheelView.dateTimeToStr(start_time);
  }

  public Date getEndTime() {
    return CommonWheelView.dateTimeFromStr(endTime);
  }

  public void setEndTime(Date endTime) {
    this.endTime = CommonWheelView.dateTimeToStr(endTime);
  }
}
