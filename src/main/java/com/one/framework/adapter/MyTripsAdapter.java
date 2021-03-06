package com.one.framework.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.adapter.MyTripsAdapter.TripsHolder;
import com.one.framework.app.common.SrcCarType;
import com.one.framework.app.common.Status.OrderStatus;
import com.one.framework.net.model.Trip;
import com.one.framework.utils.TimeUtils;
import com.one.framework.utils.UIUtils;

/**
 * Created by ludexiang on 2018/6/21.
 */

public class MyTripsAdapter extends AbsBaseAdapter<Trip, TripsHolder> {

  public MyTripsAdapter(Context context) {
    super(context);
  }

  @Override
  protected TripsHolder createHolder() {
    return new TripsHolder();
  }

  @Override
  protected void initView(View view, TripsHolder holder) {
    holder.tripCellLayout = (LinearLayout) view.findViewById(R.id.one_trip_cell_layout);
    holder.tripTime = (TextView) view.findViewById(R.id.one_my_trips_time);
    holder.tripOrderType = view.findViewById(R.id.one_my_trips_order_type);
    holder.tripType = (TextView) view.findViewById(R.id.one_my_trips_type);
    holder.tripStart = (TextView) view.findViewById(R.id.one_my_trips_start);
    holder.tripEnd = (TextView) view.findViewById(R.id.one_my_trips_end);
    holder.tripStatus = (TextView) view.findViewById(R.id.one_my_trips_status);
  }

  @Override
  protected void bindData(Trip model, TripsHolder holder, int position) {
    if (position == 0) {
      FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.tripCellLayout.getLayoutParams();
      params.topMargin = UIUtils.dip2pxInt(mContext, 6);
      holder.tripCellLayout.setLayoutParams(params);
    }
    holder.tripTime.setText(TimeUtils.longToString(model.getTripStartTime(), "M月d日 HH:mm"));
    holder.tripType.setText(SrcCarType.getBizName(model.getTripType()));
    holder.tripStart.setText(model.getTripStart());
    holder.tripEnd.setText(model.getTripEnd());
    holder.tripOrderType.setText(model.getTripOrderType() == 1 ? mContext.getString(R.string.one_trip_order_type_now) : mContext.getString(R.string.one_trip_order_type_book));
//    holder.tripOrderType.setTextColor(model.getTripOrderType() == 1 ? Color.parseColor("#1665ff") : Color.parseColor("#f05b48"));
//    holder.tripOrderType.setBackgroundResource(model.getTripOrderType() == 1 ? R.drawable.one_trips_order_type
//        : R.drawable.one_trips_order_type_book);
    OrderStatus status = OrderStatus.fromStateCode(model.getTripStatus());
    switch (status) {
      case CONFIRMED_PRICE:
      case ARRIVED:
      case CANCELED_AUTOPAID: {
        holder.tripStatus.setTextColor(Color.parseColor("#f05b48"));
        break;
      }
      default: {
        holder.tripStatus.setTextColor(Color.parseColor("#c3c3c3"));
        break;
      }
    }
    holder.tripStatus.setText(OrderStatus.getStatusName(model.getTripStatus()));
  }

  @Override
  protected View createView() {
    return mInflater.inflate(R.layout.one_my_trips_item_layout, null);
  }

  class TripsHolder {
    LinearLayout tripCellLayout;
    TextView tripTime;
    TextView tripOrderType;
    TextView tripType;
    TextView tripStart;
    TextView tripEnd;
    TextView tripStatus;
  }
}
