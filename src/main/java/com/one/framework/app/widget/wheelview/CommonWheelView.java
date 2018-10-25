package com.one.framework.app.widget.wheelview;

import android.content.Context;
import com.one.framework.R;
import com.one.framework.utils.TimeUtils;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CommonWheelView {

  private static final long BOOK_TIME = 20 * 60 * 1000; // 20min

  public static final Date dateFromCommonStr(String stringDate) {
    return TimeUtils.stringToDate(stringDate, "yyyy-MM-dd");
  }

  public static final String timeToStr(Date time) {
    return TimeUtils.dateToString(time, "HH:mm");
  }

  public static final Date timeFromCNStr(String stringTime) {
    SimpleDateFormat sdf = new SimpleDateFormat("H点m分");
    Date time = null;
    try {
      time = sdf.parse(stringTime);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return time;
  }

  public static final Date dateTimeFromStr(String stringDateTime) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date time = null;
    try {
      time = sdf.parse(stringDateTime);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return time;
  }

  public static final String dateTimeToStr(Date dateTime) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.format(dateTime);
  }

  public static final Date dateTimeFromCustomStr(String date, String time) {
    Date dateTime = new Date();
    Calendar calendar = Calendar.getInstance();
    //设置day
    if (date.equals("今天")) {

    } else if (date.equals("明天")) {
      calendar.add(Calendar.DATE, 1);
    } else if (date.equals("后天")) {
      calendar.add(Calendar.DATE, 2);
    } else {
      dateTime = dateFromCommonStr(date);
      calendar.setTime(dateTime);
    }

    //设置小时分钟
    Date timeFormat = timeFromCNStr(time);
    try {
      calendar.set(Calendar.HOUR_OF_DAY, timeFormat.getHours());
      calendar.set(Calendar.MINUTE, timeFormat.getMinutes());
      dateTime = calendar.getTime();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dateTime;
  }

  public static String showScore(float score) {
    DecimalFormat df = new DecimalFormat("0.0");
    return df.format(score);
  }


  /**
   * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
   */
  public static int dip2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }


  public static ArrayList<String> buildDays(Context context, TimeRange timeRange) {
    Calendar calendarStart = Calendar.getInstance();
    calendarStart.setTime(timeRange.getStartTime());
    Calendar calendarEnd = Calendar.getInstance();
    calendarEnd.setTime(timeRange.getEndTime());

    calendarStart.add(Calendar.MINUTE, 10);//分钟需要取整，1月1日23:55 则从 1月2日00:00开始

    ArrayList<String> daysList = new ArrayList<>();
    while (calendarStart.before(calendarEnd)) {
      Date date = calendarStart.getTime();
      if (isInSameDay(timeRange.getStartTime(), date)) {
        daysList.add(TimeUtils.dateToString(date, context.getString(R.string.one_dialog_data_today_format)));
      } else {
        daysList.add(
            TimeUtils.dateToString(date, context.getString(R.string.one_dialog_data_day_format)));
      }
      calendarStart.add(Calendar.DAY_OF_MONTH, 1);
    }
    //如果循环后开始的日期等于结束的日期，则把结束的日期也加上，如果不等于，说明已经加过了
    if (isInSameDay(calendarStart, calendarEnd)) {
      Date date = calendarEnd.getTime();
      daysList.add(TimeUtils.dateToString(date, context.getString(R.string.one_dialog_data_day_format)));
    }
    return daysList;
  }

  public static ArrayList buildHoursByDay(Context context, WheelView wheelViewDay, TimeRange timeRange) {
    if (wheelViewDay.getSelectedPosition() == 0) {
      return buildHourListStart(context, timeRange, BOOK_TIME, true);
    } else if (wheelViewDay.getSelectedPosition() == wheelViewDay.getSize() - 1) {
      return buildHourListEnd(context, timeRange);
    } else {
      return buildNormalHourList(context);
    }
  }

  public static ArrayList buildMinutesByDayHour(Context context, WheelView wheelViewDay, WheelView wheelViewHour,
      TimeRange timeRange) {
    if (wheelViewDay.getSelectedPosition() == 0 && wheelViewHour.getSelectedPosition() == 0) {
      return buildMinuteListStart(context, timeRange, 10, BOOK_TIME, true, true);
    } else if (wheelViewDay.getSelectedPosition() == wheelViewDay.getSize() - 1 &&
        wheelViewHour.getSelectedPosition() == wheelViewHour.getSize() - 1) {
      return buildMinuteListEnd(context, timeRange);
    } else {
      return buildNormalMinuteList(context);
    }

  }

  public static ArrayList buildHourListStart(Context context, TimeRange timeRange, long bookingTimeSpace, boolean isSameDay) {
    Date dateStart = timeRange.getBookingMinuteStart(bookingTimeSpace);
    Calendar calendarStart = Calendar.getInstance();
    calendarStart.setTime(dateStart);
    calendarStart.add(Calendar.MINUTE, 10);//分钟需要取整，5.55则从6:00开始

    int hourStart = calendarStart.get(Calendar.HOUR_OF_DAY);
    int min = calendarStart.get(Calendar.MINUTE);
    ArrayList hourList = new ArrayList<>();

    //需要判断起止时间是否为同一天，如果不在同一天，第一天小时范围为n-23
    Date dateEnd = timeRange.getEndTime();
    Calendar calendarEnd = Calendar.getInstance();
    calendarEnd.setTime(dateEnd);
    int hourEnd;
    if (isInSameDay(calendarStart, calendarEnd)) {
      hourEnd = calendarEnd.get(Calendar.HOUR_OF_DAY);
    } else {
      hourEnd = 23;
    }

    if (isSameDay) {
      for (int i = hourStart; i <= hourEnd; i++) {
        hourList.add(String.format(context.getString(R.string.one_dialog_data_picker_hour_format), i));
      }
    } else {
      return buildNormalHourList(context);
    }

    return hourList;
  }

  public static ArrayList buildNormalHourList(Context context) {
    ArrayList hourList = new ArrayList<>();

    for (int i = 0; i < 24; i++) {
      hourList.add(String.format(context.getString(R.string.one_dialog_data_picker_hour_format), i));
    }

    return hourList;
  }

  public static ArrayList buildHourListEnd(Context context, TimeRange timeRange) {
    Date dateEnd = timeRange.getEndTime();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(dateEnd);

    int hourEnd = 23;//calendar.get(Calendar.HOUR_OF_DAY);

    ArrayList hourList = new ArrayList<>();

    for (int i = 0; i <= hourEnd; i++) {
      hourList.add(String.format(context.getString(R.string.one_dialog_data_picker_hour_format), i));
    }

    return hourList;
  }

  public static ArrayList buildHourList(Context context) {
    ArrayList hourList = new ArrayList<>();

    for (int i = 0; i <= 23; i++) {
      hourList.add(String.format(context.getString(R.string.one_time_picker_format), i) + ":00");
    }

    return hourList;
  }

  /**
   *
   * @param context
   * @param timeRange
   * @param minuteSpace 分钟间隔
   * @return
   */
  public static ArrayList buildMinuteListStart(Context context, TimeRange timeRange, int minuteSpace, long bookingTimeSpace, boolean isSameDay, boolean isSameHour) {
    Date dateStart = timeRange.getBookingMinuteStart(bookingTimeSpace);
    Calendar calendarStart = Calendar.getInstance();
    calendarStart.setTime(dateStart);
    calendarStart.add(Calendar.MINUTE, minuteSpace);//分钟需要取整，5.55则从6:00开始

    int minStart = (calendarStart.get(Calendar.MINUTE) / minuteSpace) * minuteSpace;//取整

    Calendar calendarEnd = Calendar.getInstance();
    calendarEnd.setTime(timeRange.getEndTime());

    int minEnd;
    if (isInSameHour(calendarStart, calendarEnd)) {
      minEnd = (calendarEnd.get(Calendar.MINUTE) / minuteSpace) * minuteSpace;
    } else {
      minEnd = 50;
    }
    ArrayList minList = new ArrayList<>();

    if (isSameDay && isSameHour) {
      for (int i = minStart; i <= minEnd; i += minuteSpace) {
        minList.add(String.format(context.getString(R.string.one_dialog_data_picker_minute_format), i)
                + context.getString(R.string.one_dialog_data_picker_minute));
      }
    } else {
      return buildNormalMinuteList(context);
    }

    return minList;
  }

  public static ArrayList buildMinuteListEnd(Context context, TimeRange timeRange) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(timeRange.getEndTime());
    int minEnd = (calendar.get(Calendar.MINUTE) / 10) * 10;
    ArrayList minList = new ArrayList<>();
    for (int i = 0; i <= minEnd; i += 10) {
      minList.add(String.format(context.getString(R.string.one_dialog_data_picker_minute_format), i)
        + context.getString(R.string.one_dialog_data_picker_minute));
    }
    return minList;
  }

  public static ArrayList buildNormalMinuteList(Context context) {
    ArrayList minuteList = new ArrayList<>();
    for (int i = 0; i < 60; i += 10) {
      minuteList.add(String.format(context.getString(R.string.one_dialog_data_picker_minute_format), i)
        + context.getString(R.string.one_dialog_data_picker_minute));
    }
    return minuteList;
  }

  //判断两个日期是否在同一天
  public static boolean isInSameDay(Calendar calendar1, Calendar calendar2) {
    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
        && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
        && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
  }

  public static boolean isInSameDay(Date date1, Date date2) {
    Calendar calendar1 = Calendar.getInstance();
    calendar1.setTime(date1);
    Calendar calendar2 = Calendar.getInstance();
    calendar2.setTime(date2);
    return isInSameDay(calendar1, calendar2);
  }

  //判断两个日期是否位于同一小时
  public static boolean isInSameHour(Calendar calendar1, Calendar calendar2) {
    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
        && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
        && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
        && calendar1.get(Calendar.HOUR_OF_DAY) == calendar2.get(Calendar.HOUR_OF_DAY);
  }
}
