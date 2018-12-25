package com.one.framework.app.base;

import android.Manifest;
import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.igexin.sdk.PushManager;
import com.one.framework.R;
import com.one.framework.app.service.GetuiIntentService;
import com.one.framework.app.service.GetuiService;
import com.one.framework.app.style.ThemeStyles;
import com.one.framework.dialog.SupportDialogFragment;
import com.one.framework.log.Logger;
import com.one.framework.net.Api;
import com.one.framework.net.model.AppConfig;
import com.one.framework.net.model.Entrance;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.provider.HomeDataProvider;
import com.one.framework.push.PushMessageManager;
import com.one.framework.push.PushMsgModel;
import com.one.framework.utils.UIUtils;
import com.one.map.model.Address;
import com.one.map.view.IMapDelegate.CenterLatLngParams;
import com.one.map.view.IMapDelegate.IMapListener;
import java.util.List;

/**
 * Created by ludexiang on 2018/5/2.
 */

public abstract class BaseActivity extends FragmentActivity implements IMapListener {

  private static final int REQUEST_PERMISSION = 0;
  private static final int CAMERA_PERMISSION = 1;
  private String brandSmall = Build.BRAND.toLowerCase();

  private SupportDialogFragment mPermissionDlg;

  private boolean isMiUi = true;
  private boolean isFlyme = true;
  private SupportDialogFragment logoutDlg;

  private Handler pushHandler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      PushMsgModel model = (PushMsgModel) msg.obj;
      switch (msg.what) {
        case PushMessageManager.LOGOUT: {
          showLogOutDlg(model);
          break;
        }
      }
    }
  };

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStatusColor(getStatusBarColor());

    formatInput();
    cameraPermission();
    initPush();
  }

  private void formatInput() {
    Logger.e("ldx", "-------------------------------------");
    StringBuilder builder = new StringBuilder();
    DisplayMetrics metrics = getResources().getDisplayMetrics();
    builder.append("width >>>>>>").append(metrics.widthPixels).append("\n")
        .append("height >>>>>>>").append(metrics.heightPixels).append("\n")
        .append("density >>>>>>").append(metrics.density).append("\n")
        .append("densityDip >>>>>>").append(metrics.densityDpi).append("\n")
        .append("scaledDensity >>>>>>").append(metrics.scaledDensity);
    Logger.e("ldx", "format input >> \n" + builder.toString());
    Logger.e("ldx", "-------------------------------------");
  }

  private void initPush() {
    PackageManager pkgManager = getPackageManager();

    // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
    boolean sdCardWritePermission =
        pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName())
            == PackageManager.PERMISSION_GRANTED;

    // read phone state用于获取 imei 设备信息
    boolean phoneSatePermission =
        pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName())
            == PackageManager.PERMISSION_GRANTED;

    if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermission || !phoneSatePermission) {
      requestPermission();
    } else {
      PushManager.getInstance().initialize(getApplicationContext(), GetuiService.class);
    }

    GetuiIntentService pushService = new GetuiIntentService();
    pushService.setHandler(pushHandler);
    PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), pushService.getClass());
  }

  private void requestPermission() {
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSION);
  }

  public void cameraPermission() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
      //权限发生了改变 true  //  false 小米
      PackageManager manager = getPackageManager();
      if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
        SupportDialogFragment.Builder permissonBuilder = new SupportDialogFragment.Builder(this)
            .setTitle("相机")
            .setMessage(UIUtils.appName(this) + "想要访问相机权限")
            .setPositiveButton("同意", v -> {
              mPermissionDlg.dismiss();
              // 请求授权
              ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
            })
            .setPositiveButtonTextColor(Color.parseColor("#02040d"))
            .setNegativeButton(getString(R.string.cancel), v -> {
              mPermissionDlg.dismiss();
            });
        mPermissionDlg = permissonBuilder.create();
        mPermissionDlg.show(getSupportFragmentManager(), "");
      } else {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    if (requestCode == REQUEST_PERMISSION) {
      if ((grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
          && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
        PushManager.getInstance().initialize(getApplicationContext(), GetuiService.class);
      } else {
        Logger.e("ldx",
            "We highly recommend that you need to grant the special permissions before initializing the SDK, otherwise some "
                + "functions will not work");
        PushManager.getInstance().initialize(this.getApplicationContext(), GetuiService.class);
      }
    } else if (requestCode == CAMERA_PERMISSION) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
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

  private void showLogOutDlg(PushMsgModel model) {
    SupportDialogFragment.Builder builder = new SupportDialogFragment.Builder(this)
        .setTitle(model.getTitle())
        .setMessage(model.getDescription())
        .setNegativeTextColor(Color.parseColor("#02040d"))
        .setNegativeButton(getString(R.string.iknow), v -> {
          logoutDlg.dismiss();
        });
    logoutDlg = builder.create();
    logoutDlg.setCancelable(false);
    logoutDlg.show(getSupportFragmentManager(), "");
  }
}
