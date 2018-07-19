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

  private long mStartTime;

  public Date getStartTime() {
    return CommonWheelView.dateTimeFromStr(startTime);
  }

  public void setStartTime(Date start_time) {
    mStartTime = start_time.getTime();
    this.startTime = CommonWheelView.dateTimeToStr(start_time);
  }

  public Date getBookingMinuteStart(long bookingTime) {
    long time = mStartTime + bookingTime;
    Date date = new Date();
    date.setTime(time);
    return CommonWheelView.dateTimeFromStr(CommonWheelView.dateTimeToStr(date));
  }

  public Date getEndTime() {
    return CommonWheelView.dateTimeFromStr(endTime);
  }

  public void setEndTime(Date endTime) {
    this.endTime = CommonWheelView.dateTimeToStr(endTime);
  }
}
