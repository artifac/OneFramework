package com.one.framework.app.slide;

import android.graphics.Color;
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
import com.one.framework.app.widget.LoadingView;
import com.one.framework.app.widget.PullListView;
import com.one.framework.app.widget.PullScrollRelativeLayout;
import com.one.framework.app.widget.base.IItemClickListener;
import com.one.framework.app.widget.base.IPullView.IPullCallback;
import com.one.framework.log.Logger;
import com.one.framework.net.Api;
import com.one.framework.net.model.MyTripsModel;
import com.one.framework.net.model.OrderDetail;
import com.one.framework.net.model.Trip;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.utils.UIUtils;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by ludexiang on 2018/6/21.
 */

public class MyTripsFragment extends BaseFragment implements IItemClickListener, IPullCallback {

  private PullScrollRelativeLayout mMoveParentLayout;
  private PullListView mListView;
  private LoadingView mRefreshLoadingView;
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
    mRefreshLoadingView = (LoadingView) view.findViewById(R.id.one_my_trips_refresh_loading);
    mListView = (PullListView) view.findViewById(android.R.id.list);
    mEmptyView = (EmptyView) view.findViewById(R.id.empty_view);

    mMoveParentLayout.setScrollView(mListView);
    mMoveParentLayout.setMoveListener(mListView);
    mAdapter = new MyTripsAdapter(getActivity());
    mListView.setAdapter(mAdapter);

    mTopbarView.setTitleBarBackground(Color.WHITE);
    mTopbarView.setLeft(R.drawable.one_top_bar_back_selector);
    mTopbarView.setTitle(R.string.one_slide_my_trips);
    mTopbarView.setTitleRight(0);

    mListView.setEmptyView(mEmptyView);
    loadData(pageOffset, false);
    mListView.setItemClickListener(this);
    mListView.setPullCallback(this);
  }

  private void loadData(int pageIndex, final boolean isRefresh) {
    Api.myTrips(UserProfile.getInstance(getContext()).getUserId(), pageIndex, 10, new IResponseListener<MyTripsModel>() {
      @Override
      public void onSuccess(MyTripsModel myTripsModel) {
        fillList(myTripsModel, isRefresh);
        mRefreshLoadingView.stop();
        mRefreshLoadingView.setVisibility(View.GONE);
      }

      @Override
      public void onFail(int errCode, String message) {
        noTrips();
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
          public void onFail(int errCode, String message) {
            Logger.e("ldx", "MyTripFragments tripDetail Fail " + errCode);
          }

          @Override
          public void onFinish(OrderDetail orderDetail) {
            mLoading.dismissDlg();
          }
        });
  }

  private void fillList(MyTripsModel model, boolean isRefresh) {
    if (!isRefresh) {
      mAdapter.setListData(model.getTripList());
    } else {
      mAdapter.refreshData(model.getTripList());
    }
    int count = mAdapter.getCount();
    showEmptyView(count);
  }

  @Override
  public void move(float x, float y) {
    if (y > 0) {
      mRefreshLoadingView.setVisibility(View.VISIBLE);
      mRefreshLoadingView.updateProgressLine(y * 2f);
    } else {
      mRefreshLoadingView.setVisibility(View.GONE);
    }
  }

  @Override
  public void up(float y) { // y ACTION_UP 所保存的值
    boolean canRefresh = y * 2 >= UIUtils.getScreenWidth(getContext()) / 2;
    if (canRefresh) {
      mRefreshLoadingView.startLineProgress();
      refreshData();
    } else {
      mRefreshLoadingView.stop();
      mRefreshLoadingView.setVisibility(View.GONE);
    }
  }

  private void refreshData() {
    loadData(0, true); // 刷新
  }

  private void showEmptyView(int count) {
    if (count == 0) {
      mEmptyView.setVisibility(View.VISIBLE);
      mEmptyView.setImgRes(R.drawable.one_trip_order_empty);
      mEmptyView.setTxtRes(R.string.one_trip_empty_msg);
    } else {
      mEmptyView.setVisibility(View.GONE);
    }
  }

  private void noTrips() {
    if (mAdapter.getCount() > 0) {
      return;
    }
    mAdapter.clear();
    showEmptyView(mAdapter.getCount());
  }
}
