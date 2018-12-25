package com.one.framework.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import com.one.framework.R;
import com.one.framework.app.widget.wheelview.CommonWheelView;
import com.one.framework.app.widget.wheelview.WheelView;
import com.one.framework.log.Logger;

public class TimePickerDialog extends BottomSheetDialog implements OnClickListener {

  private ImageView mClose;
  private WheelView mFromTime;
  private WheelView mToTime;
  private Button mConfirm;

  public static int sFromPosition = 23;
  public static int sToPosition = 5;

  public TimePickerDialog(@NonNull Context context) {
    super(context);
    initView(context);
    setItems();
  }

  private void initView(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.one_time_picker_dialog_layout, null);
    mClose = view.findViewById(R.id.one_time_picker_close);
    mFromTime = view.findViewById(R.id.one_time_picker_time_from);
    mToTime = view.findViewById(R.id.one_time_picker_to);
    mConfirm = view.findViewById(R.id.one_time_picker_confirm);

    mClose.setOnClickListener(this);

    setContentView(view);

    mConfirm.setOnClickListener(v -> {
      dismiss();
      sFromPosition = mFromTime.getSelectedPosition();
      sToPosition = mToTime.getSelectedPosition();

      String fromTime = mFromTime.getSelectedItem();
      String toTime = mToTime.getSelectedItem();

      Logger.e("ldx", "mFromTime " + fromTime + " toTime " + toTime);
      if (mListener != null) {
        mListener.onTimeSelect(0, fromTime, toTime);
      }
    });
  }

  private void setItems() {
    mFromTime.setItems(CommonWheelView.buildHourList(getContext()), sFromPosition);
    mToTime.setItems(CommonWheelView.buildHourList(getContext()), sToPosition);
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.one_time_picker_close) {
      dismiss();
    }
  }
}
