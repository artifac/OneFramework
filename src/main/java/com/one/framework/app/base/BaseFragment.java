package com.one.framework.app.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.one.framework.log.Logger;

/**
 * Created by ludexiang on 2018/6/21.
 */

public abstract class BaseFragment extends BizEntranceFragment {

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return onCreateViewImpl(inflater, container, savedInstanceState);
  }

  protected abstract View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Logger.e("ldx", "onActivityCreated mTopBarView >> " + mTopbarView);
    if (mTopbarView != null) {
      mTopbarView.hideRightImage(true);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }
}
