package com.one.framework.app.slide;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.one.framework.R;
import com.one.framework.app.base.BaseFragment;
import com.one.framework.app.slide.presenter.SettingPresenter;
import com.one.framework.app.widget.ListItemWithRightArrowLayout;
import com.one.framework.app.widget.PullScrollRelativeLayout;
import com.one.framework.app.widget.PullScrollView;
import com.one.framework.app.widget.TripButton;
import com.one.framework.dialog.SupportDialogFragment;
import com.one.framework.net.model.Trip;

/**
 * Created by ludexiang on 2018/6/29.
 */

public class SettingsFragment extends BaseFragment implements ISettingView, OnClickListener {

  private PullScrollRelativeLayout mRootView;
  private PullScrollView mScrollView;
  private ListItemWithRightArrowLayout mAccount;
  private ListItemWithRightArrowLayout mNormalAddress;
  private ListItemWithRightArrowLayout mUrgentConnect;
  private ListItemWithRightArrowLayout mClearCache;
  private ListItemWithRightArrowLayout mUserEdu;
  private ListItemWithRightArrowLayout mAppEvaluate;
  private ListItemWithRightArrowLayout mLawer;
  private ListItemWithRightArrowLayout mAbout;
  private TripButton mLogout;
  private SettingPresenter mPresenter;

  private SupportDialogFragment mLogoutDlg;

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.one_settings_layout, container, false);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mPresenter = new SettingPresenter(getContext(),this);

    mRootView = (PullScrollRelativeLayout) view.findViewById(R.id.settings_root_layout);
    mScrollView = (PullScrollView) view.findViewById(R.id.settings_scroll_view);
    mAccount = (ListItemWithRightArrowLayout) view.findViewById(R.id.one_account_safe);
    mNormalAddress = (ListItemWithRightArrowLayout) view.findViewById(R.id.one_normal_address);
    mUrgentConnect = (ListItemWithRightArrowLayout) view.findViewById(R.id.one_urgent_connect);
    mClearCache = (ListItemWithRightArrowLayout) view.findViewById(R.id.one_clear_cache);
    mUserEdu = (ListItemWithRightArrowLayout) view.findViewById(R.id.one_user_edu);
    mAppEvaluate = (ListItemWithRightArrowLayout) view.findViewById(R.id.one_evaluate);
    mLawer = (ListItemWithRightArrowLayout) view.findViewById(R.id.one_lawer_private);
    mAbout = (ListItemWithRightArrowLayout) view.findViewById(R.id.one_about);
    mLogout = (TripButton) view.findViewById(R.id.one_logout);

    mAccount.setItemTitle(R.string.one_settings_account);
    mNormalAddress.setItemTitle(R.string.one_settings_normal_adr);
    mUrgentConnect.setItemTitle(R.string.one_settings_urgent_connect);
    mClearCache.setItemTitle(R.string.one_settings_clear_cache);
    mUserEdu.setItemTitle(R.string.one_settings_user_edu);
    mAppEvaluate.setItemTitle(R.string.one_settings_evaluate);
    mLawer.setItemTitle(R.string.one_settings_lawer_private);
    mAbout.setItemTitle(R.string.one_settings_about);

    mTopbarView.setLeft(R.drawable.one_top_bar_back_selector);
    mTopbarView.setTitle(R.string.one_settings);

    mRootView.setScrollView(mScrollView);
    mRootView.setMoveListener(mScrollView);

    mLogout.setOnClickListener(this);
    mNormalAddress.setOnClickListener(this);
  }

  @Override
  public void logout() {
    mLoading.dismissDlg();
    mTopbarView.popBackListener();
    finishSelf();
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.one_logout) {
      showLogoutDlg();
    } else if (id == R.id.one_normal_address) {

    }
  }

  private void showLogoutDlg() {
    SupportDialogFragment.Builder builder = new SupportDialogFragment.Builder(getActivity())
        .setMessage(getString(R.string.one_logout_message))
        .setNegativeButton(getString(R.string.one_cancel), new OnClickListener() {
          @Override
          public void onClick(View v) {
            mLogoutDlg.dismiss();
          }
        })
        .setPositiveButton(getString(R.string.one_confirm), new OnClickListener() {
          @Override
          public void onClick(View v) {
            mLoading.showDlg();
            mPresenter.doLogout();
          }
        })
        .setPositiveButtonTextColor(Color.parseColor("#A3D2E4"));
    mLogoutDlg = builder.create();
    mLogoutDlg.show(getFragmentManager(), "");
  }
}
