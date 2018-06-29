package com.one.framework.app.slide;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.one.framework.R;
import com.one.framework.adapter.AbsBaseAdapter;
import com.one.framework.adapter.MyTripsAdapter;
import com.one.framework.app.base.BaseFragment;
import com.one.framework.app.common.SrcCarType;
import com.one.framework.app.login.UserProfile;
import com.one.framework.app.widget.EmptyView;
import com.one.framework.app.widget.PullListView;
import com.one.framework.app.widget.PullScrollRelativeLayout;
import com.one.framework.app.widget.base.IItemClickListener;
import com.one.framework.log.Logger;
import com.one.framework.net.Api;
import com.one.framework.net.model.MyTripsModel;
import com.one.framework.net.model.OrderDetail;
import com.one.framework.net.model.Trip;
import com.one.framework.net.response.IResponseListener;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by ludexiang on 2018/6/21.
 */

public class MyTripsFragment extends BaseFragment implements IItemClickListener {

  private PullScrollRelativeLayout mMoveParentLayout;
  private PullListView mListView;
  private AbsBaseAdapter mAdapter;
  private EmptyView mEmptyView;

  private int pageOffset = 0;

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mLoading.showDlg();
    View view = inflater.inflate(R.layout.one_my_trips_layout, null);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mMoveParentLayout = (PullScrollRelativeLayout) view.findViewById(R.id.one_my_trips_parent_layout);
    mListView = (PullListView) view.findViewById(android.R.id.list);
    mEmptyView = (EmptyView) view.findViewById(R.id.empty_view);

    mMoveParentLayout.setScrollView(mListView);
    mMoveParentLayout.setMoveListener(mListView);
    mAdapter = new MyTripsAdapter(getActivity());
    mListView.setAdapter(mAdapter);

    mTopbarView.setLeft(R.drawable.one_top_bar_back_selector);
    mTopbarView.setTitle(R.string.one_slide_my_trips);
    mListView.setEmptyView(mEmptyView);
    loadData(pageOffset);
    mListView.setItemClickListener(this);
  }

  private void loadData(int pageIndex) {
    Api.myTrips(UserProfile.getInstance(getContext()).getUserId(), pageIndex, 10, new IResponseListener<MyTripsModel>() {
      @Override
      public void onSuccess(MyTripsModel myTripsModel) {
        fillList(myTripsModel);
      }

      @Override
      public void onFail(int errCode, MyTripsModel myTripsModel) {

      }

      @Override
      public void onFinish(MyTripsModel myTripsModel) {
        mLoading.dismissDlg();
      }
    });
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int position) {
    mLoading.showDlg();
    Trip currentTrip = (Trip) mAdapter.getItem(position);
    Api.tripDetail(currentTrip.getTripType(), currentTrip.getOrderId(),
        new IResponseListener<OrderDetail>() {
          @Override
          public void onSuccess(OrderDetail orderDetail) {
            SrcCarType carType = SrcCarType.getCarType(orderDetail.getBizType());
            if (mActivity != null && mActivity.get() != null) {
              mActivity.get().onItemClick(carType);
            }
            EventBus.getDefault().post(orderDetail);
          }

          @Override
          public void onFail(int errCode, OrderDetail orderDetail) {
            Logger.e("ldx", "MyTripFragments tripDetail Fail " + errCode);
          }

          @Override
          public void onFinish(OrderDetail orderDetail) {
            mLoading.dismissDlg();
          }
        });
  }

  private void fillList(MyTripsModel model) {
    mAdapter.setListData(model.getTripList());
    int count = mAdapter.getCount();
    if (count == 0) {
      mEmptyView.setVisibility(View.VISIBLE);
      mEmptyView.setImgRes(R.drawable.one_trip_order_empty);
      mEmptyView.setTxtRes(R.string.one_trip_empty_msg);
    } else {
      mEmptyView.setVisibility(View.GONE);
    }
  }
}
