package com.one.framework.app.page.impl;

import static com.one.framework.app.navigation.INavigator.BUNDLE_ADD_TO_BACK_STACK;
import static com.one.framework.app.navigation.INavigator.BUNDLE_FORWARD_FRAGMENT_STYLE;
import static com.one.framework.model.NavigatorModel.CUSTOMER_SERVICE;
import static com.one.framework.model.NavigatorModel.FEEDBACK;
import static com.one.framework.model.NavigatorModel.INVITE;
import static com.one.framework.model.NavigatorModel.MY_TRAVEL;
import static com.one.framework.model.NavigatorModel.MY_WALLET;
import static com.one.framework.model.NavigatorModel.SETTING;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import com.one.framework.R;
import com.one.framework.adapter.AbsBaseAdapter;
import com.one.framework.adapter.impl.NavigatorOptionsAdapter;
import com.one.framework.adapter.impl.NavigatorOptionsGridAdapter;
import com.one.framework.app.base.BaseFragment;
import com.one.framework.app.login.UserProfile;
import com.one.framework.app.page.ISlideDrawer;
import com.one.framework.app.slide.InviteFragment;
import com.one.framework.app.slide.MyTripsFragment;
import com.one.framework.app.slide.MyWalletFragment;
import com.one.framework.app.slide.SettingsFragment;
import com.one.framework.app.web.WebActivity;
import com.one.framework.app.web.WebViewModel;
import com.one.framework.app.widget.NavigatorHeaderView;
import com.one.framework.app.widget.PullGridView;
import com.one.framework.app.widget.PullListView;
import com.one.framework.app.widget.PullScrollRelativeLayout;
import com.one.framework.app.widget.ScrollerLayout;
import com.one.framework.app.widget.base.IHeaderView;
import com.one.framework.app.widget.base.IItemClickListener;
import com.one.framework.app.widget.base.IMovePublishListener;
import com.one.framework.model.NavigatorModel;
import com.one.framework.utils.UIUtils;
import com.trip.taxi.utils.H5Page;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/4/16.
 */

public class NavigatorFragment extends BaseFragment implements IMovePublishListener, ISlideDrawer,
    IItemClickListener {

  private IHeaderView mHeaderView;
  private PullScrollRelativeLayout mPullRootLayout;
  private PullListView mNavigatorOptionsList;
  private AbsBaseAdapter mListAdapter;
  private AbsBaseAdapter mGridAdapter;
  // 通用
  private ScrollerLayout mGeneralLayout;
  // 默认平移高度
  private int mGeneralDefaultHeight;
  private ImageView mGridArrow;
  private PullGridView mOptionsGrid;

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.one_navigator_layout, null);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mHeaderView = (NavigatorHeaderView) view.findViewById(R.id.one_navigator_header_view);
    mPullRootLayout = view.findViewById(R.id.one_nav_pull_view_root);
    mNavigatorOptionsList = view.findViewById(android.R.id.list);
    mGeneralLayout = view.findViewById(R.id.one_navigator_general);
    mOptionsGrid = view.findViewById(R.id.one_navigator_grid_view);
    mGridArrow = view.findViewById(R.id.one_navigator_general_arrow);

    mPullRootLayout.setMoveListener(mNavigatorOptionsList);
    mPullRootLayout.setScrollView(mNavigatorOptionsList);

    mListAdapter = new NavigatorOptionsAdapter(getContext());
    mNavigatorOptionsList.setAdapter(mListAdapter);

    mGridAdapter = new NavigatorOptionsGridAdapter(getContext());
    mOptionsGrid.setAdapter(mGridAdapter);

    mGeneralLayout.setMoveListener(this);
    mListAdapter.setListData(createListOptions());
//    mGridAdapter.setListData(testDemo());

    mNavigatorOptionsList.setItemClickListener(this);
    mOptionsGrid.setItemClickListener(this);

    // default 通用布局向下平移
    int screeHeight = UIUtils.getScreenHeight(getContext());
    int navigatorHeaderHeight = getResources().getDimensionPixelOffset(R.dimen.one_general_header_height);
    int height = getResources().getDimensionPixelOffset(R.dimen.one_general_default_show_height);
    mGeneralDefaultHeight = screeHeight - height - navigatorHeaderHeight;
    mGeneralLayout.setTranslationY(mGeneralDefaultHeight);
    refreshHeader();
  }

  @Override
  protected boolean isAddLeftClick() {
    return false;
  }

  @Override
  public void setListOptions(List<NavigatorModel> listOptions) {
    mListAdapter.setListData(listOptions);
  }

  @Override
  public void setGridOptions(List<NavigatorModel> gridOptions) {
    mGridAdapter.setListData(gridOptions);
  }

  // 通用布局进行滚动
  @Override
  public void onMove(float offsetX, float offsetY) {
    if (mGeneralLayout.getTranslationY() < 0) {
      return;
    }
    float translationY = mGeneralLayout.getTranslationY() + offsetY + 0.5f;
    float scale = translationY / mGeneralLayout.getHeight();
    mGridArrow.setRotation((1f - scale) * 180);
    mGeneralLayout.setTranslationY(translationY);
  }

  @Override
  public void onUp(boolean bottom2Up, boolean isFling) {
    float translationY = mGeneralLayout.getTranslationY() + 0.5f;
    float scale = 1f - Math.abs(translationY / mGeneralLayout.getHeight());
    float to = isFling ? (bottom2Up ? 0 : mGeneralDefaultHeight)
        : scale >= 0.5f ? 0f : mGeneralDefaultHeight;
    int duration = (int) Math.abs(300 * scale);
    goonMove(translationY, to, duration);
  }

  private void goonMove(float from, final float to, long duration) {
    ValueAnimator translate = ValueAnimator.ofFloat(from, to);
    translate.setDuration(duration);
    translate.addUpdateListener(animation -> {
      float animValue = (Float) animation.getAnimatedValue();
      mGridArrow.setRotation(to == 0 ? 180 : 0);
      mGeneralLayout.setTranslationY(animValue);
    });
    translate.start();
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int position) {
    if (adapterView instanceof PullListView) {
      handleListViewClick(position);
    }
  }

  private void handleListViewClick(int position) {
    NavigatorModel model = (NavigatorModel) mListAdapter.getItem(position);
    switch (model.optionsType) {
      case MY_TRAVEL: {
        Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_FORWARD_FRAGMENT_STYLE, true);
        bundle.putBoolean(BUNDLE_ADD_TO_BACK_STACK, true);
        forward(MyTripsFragment.class, bundle);
        break;
      }
      case CUSTOMER_SERVICE: {
        WebViewModel webViewModel = new WebViewModel();
        webViewModel.url = H5Page.INSTANCE.serviceInfo(UserProfile.getInstance(getContext()).getUserId(), UserProfile.getInstance(getContext()).getTokenValue());
        webViewModel.rightTextResId = 0;
        webViewModel.rightIconResId = 0;
        webViewModel.jsMethod = "allOrder";
        Intent intent = new Intent(getActivity(), WebActivity.class);
        intent.putExtra(WebActivity.KEY_WEB_VIEW_MODEL, webViewModel);
        startActivity(intent);
        break;
      }
      case SETTING: {
        Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_FORWARD_FRAGMENT_STYLE, true);
        bundle.putBoolean(BUNDLE_ADD_TO_BACK_STACK, true);
        forward(SettingsFragment.class, bundle);
        break;
      }
      case MY_WALLET: { // 我的钱包
        Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_FORWARD_FRAGMENT_STYLE, true);
        bundle.putBoolean(BUNDLE_ADD_TO_BACK_STACK, true);
        forward(MyWalletFragment.class, bundle);
        break;
      }
      case INVITE: {
        Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_FORWARD_FRAGMENT_STYLE, true);
        bundle.putBoolean(BUNDLE_ADD_TO_BACK_STACK, true);
        forward(InviteFragment.class, bundle);
        break;
      }
      case FEEDBACK: {
        WebViewModel webViewModel = new WebViewModel();
        webViewModel.url = H5Page.INSTANCE.feedback(UserProfile.getInstance(getContext()).getUserId(), UserProfile.getInstance(getContext()).getTokenValue());
        webViewModel.rightTextResId = 0;
        webViewModel.rightIconResId = 0;
        Intent intent = new Intent(getActivity(), WebActivity.class);
        intent.putExtra(WebActivity.KEY_WEB_VIEW_MODEL, webViewModel);
        startActivity(intent);
        break;
      }
    }
  }

  private List<NavigatorModel> createListOptions() {
    List<NavigatorModel> models = new ArrayList<>();

    NavigatorModel myOrders = new NavigatorModel();
    myOrders.optionsInfo = getString(R.string.one_slide_my_trips);
    myOrders.optionsType = MY_TRAVEL;
    myOrders.optionsIconId = R.drawable.one_slide_my_trips;

    NavigatorModel myWallet = new NavigatorModel();
    myWallet.optionsInfo = getString(R.string.one_slide_my_wallet);
    myWallet.optionsType = MY_WALLET;
    myWallet.optionsIconId = R.drawable.one_slide_my_wallet;

    NavigatorModel customerService = new NavigatorModel();
    customerService.optionsInfo = getString(R.string.one_slide_customer_service);
    customerService.optionsType = CUSTOMER_SERVICE;
    customerService.optionsIconId = R.drawable.one_slide_customer_service;

    NavigatorModel general = new NavigatorModel();
    general.optionsInfo = getString(R.string.one_settings);
    general.optionsType = SETTING;
    general.optionsIconId = R.drawable.one_slide_setting;

    NavigatorModel invite = new NavigatorModel();
    invite.optionsInfo = getString(R.string.one_slide_invite_friend);
    invite.optionsType = INVITE;
    invite.optionsIconId = R.drawable.one_slide_invite;

    NavigatorModel feedback = new NavigatorModel();
    feedback.optionsInfo = getString(R.string.one_slide_feedback);
    feedback.optionsType = FEEDBACK;
    feedback.optionsIconId = R.drawable.one_slide_feedback;

    models.add(myOrders);
    models.add(myWallet);
    models.add(customerService);
    models.add(invite);
    models.add(feedback);
    models.add(general);
    return models;
  }

  @Override
  public void refreshHeader() {
    String phone = UserProfile.getInstance(getContext()).getMobile();
    if (!TextUtils.isEmpty(phone)) {
      mHeaderView.setHeaderTitle(phone.replace(phone.substring(3, 7), "****"));
    }
  }

  @Override
  public void recoveryDefault() {
    onUp(false, true);
  }
}
