package com.one.framework.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.app.widget.wheelview.CommonWheelView;
import com.one.framework.app.widget.wheelview.TimeRange;
import com.one.framework.app.widget.wheelview.WheelView;
import com.one.framework.utils.TimeUtils;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/12.
 */

public class DataPickerDialog extends BottomSheetDialog {

  private WheelView mTime;
  private WheelView mHour;
  private WheelView mMinute;

  private static int timePosition;
  private static int hourPosition;
  private static int minutePosition;
  private static boolean isSameDay = true;
  private static boolean isSameHour = true;

  private int mTimeRangeDays;



  public DataPickerDialog(@NonNull Context context, int timeRange) {
    super(context);
    mTimeRangeDays = timeRange;
    initView(context);
    setItems();
  }

  private void initView(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.one_data_picker_dialog_layout, null);
    //日期滚轮
    mTime = (WheelView) view.findViewById(R.id.one_data_picker_time);
    //小时滚轮
    mHour = (WheelView) view.findViewById(R.id.one_data_picker_hour);
    //分钟滚轮
    mMinute = (WheelView) view.findViewById(R.id.one_data_picker_minute);

    TextView confirm = (TextView) view.findViewById(R.id.one_bottom_dlg_confirm);
    TextView cancel = (TextView) view.findViewById(R.id.one_bottom_dlg_cancel);

    cancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });

    confirm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
        timePosition = mTime.getSelectedPosition();
        hourPosition = mHour.getSelectedPosition();
        minutePosition = mMinute.getSelectedPosition();

        int timePosition = mTime.getSelectedPosition();
        String hourSelected = mHour.getSelectedItem();
        int hourIndex = hourSelected.lastIndexOf(getContext().getString(R.string.one_dialog_data_picker_hour));
        String minuteSelected = mMinute.getSelectedItem();
        int minuteIndex = minuteSelected.lastIndexOf(getContext().getString(R.string.one_dialog_data_picker_minute));

        int hour = Integer.parseInt(hourSelected.substring(0, hourIndex));
        int minute = Integer.parseInt(minuteSelected.substring(0, minuteIndex));
        long time = getLongTime(timePosition, hour, minute);
        Date currTime = new Date(System.currentTimeMillis());
        Date selectTime = new Date(time);
        isSameDay = TimeUtils.isInSameDay(currTime, selectTime);
        isSameHour = TimeUtils.isInSameHour(currTime, selectTime);
        String showTime = new StringBuffer().append(mTime.getSelectedItem()).append(" ").append("{").append(hour).append(":").append(minuteSelected.substring(0, minuteIndex)).append("}").toString();
        if (mListener != null) {
          mListener.onTimeSelect(time, showTime);
        }
      }
    });

    setContentView(view);
  }

  private void setItems() {
    long bookingTime = 20 * 60 * 1000;
    final TimeRange timeRange = getTimeRange();
    mTime.setItems(CommonWheelView.buildDays(getContext(), timeRange), timePosition);
    mHour.setItems(CommonWheelView.buildHourListStart(getContext(), timeRange, bookingTime, isSameDay), hourPosition);
    /** 10 表示间隔10分钟 */
    mMinute.setItems(CommonWheelView.buildMinuteListStart(getContext(), timeRange, 10, bookingTime, isSameDay, isSameHour), minutePosition);

    //联动逻辑效果
    mTime.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(int index, String item) {
        List hourStrList = CommonWheelView.buildHoursByDay(getContext(), mTime, timeRange);
        int newIndexHour = hourStrList.indexOf(mHour.getSelectedItem());
        mHour.setItems(hourStrList, newIndexHour);
        List minStrList = CommonWheelView
            .buildMinutesByDayHour(getContext(), mTime, mHour, timeRange);
        int newIndexMin = minStrList.indexOf(mMinute.getSelectedItem());
        mMinute.setItems(minStrList, newIndexMin);
      }
    });
    mHour.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(int index, String item) {
        List minStrList = CommonWheelView.buildMinutesByDayHour(getContext(), mTime, mHour, timeRange);
        int newIndexMin = minStrList.indexOf(mMinute.getSelectedItem());
        mMinute.setItems(minStrList, newIndexMin);
      }
    });
  }

  private TimeRange getTimeRange() {
    Calendar calendarStart = Calendar.getInstance();
    Calendar calendarEnd = Calendar.getInstance();
    calendarEnd.add(Calendar.DAY_OF_YEAR, mTimeRangeDays);
    TimeRange timeRange = new TimeRange();
    timeRange.setStartTime(calendarStart.getTime());
    timeRange.setEndTime(calendarEnd.getTime());
    return timeRange;
  }

  @Override
  public void show() {
    if (isShowing()) {
      return;
    }
    super.show();
  }

  private long getLongTime(int timePosition, int hour, int minute) {
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH) + timePosition;
    StringBuffer buffer = new StringBuffer();
    String time = buffer.append(year).append("-").append(month).append("-").append(day).append(" ").append(hour).append(":").append(minute).append(":").append(0).toString();
    return TimeUtils.stringToLong(time, "");
  }

  public static void reset() {
    timePosition = 0;
    hourPosition = 0;
    minutePosition = 0;
    isSameDay = true;
    isSameHour = true;
  }
}
