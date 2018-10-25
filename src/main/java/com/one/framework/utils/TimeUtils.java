package com.one.framework.utils;

import android.content.Context;
import android.text.TextUtils;
import com.one.framework.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {

  public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
  static final int TIME_CHANGE = 3600000;   //60*60*1000 用于把毫秒转换成小时
  static final int DAY_HOURS = 24;

  // date类型转换为String类型
  // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
  // data Date类型的时间
  public static String dateToString(Date data, String formatType) {
    if (TextUtils.isEmpty(formatType)) {
      formatType = PATTERN;
    }
    return new SimpleDateFormat(formatType).format(data);
  }

  // long类型转换为String类型
  // currentTime要转换的long类型的时间
  // formatType要转换的string类型的时间格式
  public static String longToString(long currentTime, String formatType) {
    if (TextUtils.isEmpty(formatType)) {
      formatType = PATTERN;
    }
    Date date = longToDate(currentTime, formatType); // long类型转成Date类型
    return dateToString(date, formatType); // date类型转成String
  }

  // string类型转换为date类型
  // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
  // HH时mm分ss秒，
  // strTime的时间格式必须要与formatType的时间格式相同
  public static Date stringToDate(String strTime, String formatType) {
    if (TextUtils.isEmpty(formatType)) {
      formatType = PATTERN;
    }
    SimpleDateFormat formatter = new SimpleDateFormat(formatType);
    Date date;
    try {
      date = formatter.parse(strTime);
    } catch (ParseException parse) {
      date = new Date(System.currentTimeMillis());
    }
    return date;
  }

  // long转换为Date类型
  // currentTime要转换的long类型的时间
  // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
  public static Date longToDate(long currentTime, String formatType) {
    if (TextUtils.isEmpty(formatType)) {
      formatType = PATTERN;
    }
    Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
    String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
    return stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
  }

  // string类型转换为long类型
  // strTime要转换的String类型的时间
  // formatType时间格式
  // strTime的时间格式和formatType的时间格式必须相同
  public static long stringToLong(String strTime, String formatType) {
    Date date = stringToDate(strTime, formatType); // String类型转成date类型
    if (date == null) {
      return 0;
    } else {
      return dateToLong(date); // date类型转成long类型
    }
  }

  // date类型转换为long类型
  // date要转换的date类型的时间
  public static long dateToLong(Date date) {
    return date.getTime();
  }

  /**
   * 将long型转化为 “明天 18:00"格式
   */
  public static String convertMillisToString(Context context, long time) {
    return convertMillisToString(context, time, false);
  }

  public static String convertMillisToString(Context context, long time, boolean forceBeijingZone) {
    return convertMillisToString(context, time, forceBeijingZone, true);
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

  public static boolean isInSameHour(Date date1, Date date2) {
    Calendar calendar1 = Calendar.getInstance();
    calendar1.setTime(date1);
    Calendar calendar2 = Calendar.getInstance();
    calendar2.setTime(date2);
    return isInSameHour(calendar1, calendar2);
  }

  /**
   * 将long型转化为 “明天 18:00"格式
   */
  public static String convertMillisToString(Context context, long time, boolean forceBeijingZone,
      boolean translate) {
    String dateString = "";
    Date date = new Date(time);
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    if (forceBeijingZone) {
      sdf.setTimeZone(getBeijingTimeZone());
    }
    switch (getDayDiff(System.currentTimeMillis(), time, forceBeijingZone)) {
      case 0:
        dateString = context.getString(R.string.one_dialog_data_picker_today) + " " + sdf.format(date);
        break;
      case 1:
        dateString = context.getString(R.string.one_dialog_data_picker_tomorrow) + " " + sdf.format(date);
        break;
      case 2:
        dateString = context.getString(R.string.one_dialog_data_picker_the_day_after_tomorrow) + " " + sdf.format(date);
        break;
      default:
        Date date2 = new Date(time);
        SimpleDateFormat sdf2 = null;
        if (translate) {
          sdf2 = new SimpleDateFormat("MM.dd HH:mm");
        } else {
          sdf2 = new SimpleDateFormat("MM月dd日 HH:mm");
        }
        if (forceBeijingZone) {
          sdf2.setTimeZone(getBeijingTimeZone());
        }
        dateString = sdf2.format(date2);
        break;
    }
    return dateString;
  }

  /**
   * 将long型转化为 “明天 18:00"格式
   */
  public static String convertSimpleMillis(Context context, long time, boolean forceBeijingZone) {
    String dateString = "";
    Date date = new Date(time);
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    if (forceBeijingZone) {
      sdf.setTimeZone(getBeijingTimeZone());
    }
    switch (getDayDiff(System.currentTimeMillis(), time, forceBeijingZone)) {
      case 0:
        dateString = context.getString(R.string.one_dialog_data_picker_today) + " " + sdf.format(date);
        break;
      case 1:
        dateString = context.getString(R.string.one_dialog_data_picker_tomorrow) + " " + sdf.format(date);
        break;
      case 2:
        dateString = context.getString(R.string.one_dialog_data_picker_the_day_after_tomorrow) + " " + sdf.format(date);
        break;
      default:
        Date date2 = new Date(time);
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日 HH:mm");
        if (forceBeijingZone) {
          sdf2.setTimeZone(getBeijingTimeZone());
        }
        dateString = sdf2.format(date2);
        break;
    }
    return dateString;
  }

  /**
   * 获取time距离现在的天数
   *
   * @param time 未来的时间
   */
  public static int getDayDiff(long time) {
    return getDayDiff(System.currentTimeMillis(), time);
  }

  public static int getDayDiff(long time1, long time2) {
    return getDayDiff(time1, time2, false);
  }


  public static int getDayDiff(long time1, long time2, boolean forceBeijingZone) {
    Date date1 = new Date(time1);
    Date date2 = new Date(time2);
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(date1);
    cal2.setTime(date2);

    if (forceBeijingZone) {
      cal1.setTimeZone(getBeijingTimeZone());
      cal2.setTimeZone(getBeijingTimeZone());
    }
    long ldate1 = date1.getTime() + cal1.get(Calendar.ZONE_OFFSET) + cal1.get(Calendar.DST_OFFSET);
    long ldate2 = date2.getTime() + cal2.get(Calendar.ZONE_OFFSET) + cal2.get(Calendar.DST_OFFSET);

    int hr1 = (int) (ldate1 / TIME_CHANGE); // 60*60*1000
    int hr2 = (int) (ldate2 / TIME_CHANGE);

    int days1 = hr1 / DAY_HOURS;
    int days2 = hr2 / DAY_HOURS;

    return days2 - days1;
  }

  public static TimeZone getBeijingTimeZone() {
    return TimeZone.getTimeZone("GMT+08");
  }
}
