package com.one.framework.app.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.one.framework.app.style.ThemeStyles;
import com.one.framework.net.Api;
import com.one.framework.net.model.AppConfig;
import com.one.framework.net.model.Entrance;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.provider.HomeDataProvider;
import com.one.map.model.Address;
import com.one.map.view.IMapDelegate.CenterLatLngParams;
import com.one.map.view.IMapDelegate.IMapListener;
import java.util.List;

/**
 * Created by ludexiang on 2018/5/2.
 */

public abstract class BaseActivity extends FragmentActivity implements IMapListener {

  private String brandSmall = Build.BRAND.toLowerCase();

  private boolean isMiUi = true;
  private boolean isFlyme = true;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStatusColor(getStatusBarColor());

  }

  /**
   * 设置状态栏的颜色
   */
  protected int getStatusBarColor() {
    return Color.WHITE;
  }

  protected void setStatusColor(int color) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        checkLightStatusBarSupportCompat();
        setStatusBarColorCompat(color);
        getWindow().setStatusBarColor(color);
      } else {
        setStatusBarColorCompat(Color.parseColor("#191d21"));
      }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getWindow().getDecorView()
            .setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      }
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }
  }

  private void checkLightStatusBarSupportCompat() {
    if (brandSmall.contains("xiaomi") && isMiUi) {
      isMiUi = ThemeStyles.setMIUIStatusBarLightMode(this, true);
    } else if (brandSmall.contains("meizu") && isFlyme) {
      isFlyme = ThemeStyles.setFlymeStatusBarLightMode(this, true);
    }
  }

  public void setStatusBarColorCompat(int color) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      getWindow().setStatusBarColor(color);
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    // 移除所有 FragmentManager 保存的状态信息,防止 Activity 重建时自动恢复 Fragment 实例
    outState.remove("android:support:fragments");
    outState.remove("android:support:next_request_index");
    outState.remove("android:support:request_indicies");
    outState.remove("android:support:request_fragment_who");
  }

  protected void requestAppConfig() {
    Log.e("ldx", "AppDelegates >>>> appConfig ");
    Api.appConfig(new IResponseListener<AppConfig>() {
      @Override
      public void onSuccess(AppConfig appConfig) {
        com.one.framework.log.Logger.e("ldx", "appConfig success " + appConfig);
        HomeDataProvider.getInstance().saveCarType(appConfig.getCarTypes());
        List<Entrance> entrances = appConfig.getEntrance();
        HomeDataProvider.getInstance().saveEntrances(entrances);
      }

      @Override
      public void onFail(int errCod, String message) {
        com.one.framework.log.Logger.e("ldx", "appConfig fail ");
      }

      @Override
      public void onFinish(AppConfig appConfig) {
        com.one.framework.log.Logger.e("ldx", "appConfig finish ");
      }
    });
  }

  @Override
  public void onMapLoaded() {

  }

  @Override
  public void onMapMoveChange() {

  }

  @Override
  public void onMapMoveFinish(CenterLatLngParams params) {

  }

  @Override
  public void onMapGeo2Address(Address address) {

  }

  @Override
  public void onMapPoiAddresses(int type, List<Address> addresses) {

  }
}
