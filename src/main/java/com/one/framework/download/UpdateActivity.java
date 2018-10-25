//package com.one.framework.download;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.text.Html;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.TextView;
//import com.one.framework.R;
//import com.one.framework.app.base.BaseActivity;
//import com.one.framework.download.AppUpgradeManager.Version;
//
//public class UpdateActivity extends BaseActivity implements View.OnClickListener,
//    EasyPermissions.PermissionCallbacks {
//
//  TextView mTextUpdateInfo;
//  private Version mVersion;
//  private static final int RC_EXTERNAL_STORAGE = 0x04;//存储权限
//
//  public static void show(Activity activity, Version version) {
//    Intent intent = new Intent(activity, UpdateActivity.class);
//    intent.putExtra("version", version);
//    activity.startActivityForResult(intent, 0x01);
//  }
//
//  @Override
//  protected int getContentView() {
//    return 0;//R.layout.activity_update;
//  }
//
//  @SuppressWarnings("deprecation")
//  protected void initData() {
//    setTitle("");
//    getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//    mVersion = (Version) getIntent().getSerializableExtra("version");
//    mTextUpdateInfo.setText(Html.fromHtml(mVersion.getMessage()));
//  }
//
//  @Override
//  public void onClick(View v) {
//    switch (v.getId()) {
//      case R.id.btn_update:
//        if (!TDevice.isWifiOpen()) {
//          DialogHelper.getConfirmDialog(this, "当前非wifi环境，是否升级？", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//              requestExternalStorage();
//            }
//          }, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//              finish();
//            }
//          }).show();
//        } else {
//          requestExternalStorage();
//
//        }
//        break;
//      case R.id.btn_close:
//        finish();
//        break;
//    }
//
//  }
//
//  @AfterPermissionGranted(RC_EXTERNAL_STORAGE)
//  public void requestExternalStorage() {
//    if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//
//      AppUpgradeManager.getInstance(this, mVersion).startDown();
//      finish();
//    } else {
////      EasyPermissions.requestPermissions(this, "需要开启对您手机的存储权限才能下载安装", RC_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
//    }
//  }
//
//  @Override
//  public void onPermissionsGranted(int requestCode, List<String> perms) {
//
//  }
//
//  @Override
//  public void onPermissionsDenied(int requestCode, List<String> perms) {
//    //当权限窗口不能弹出式调用-用户勾选了不再提醒
//    if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//      DialogHelper.getConfirmDialog(UpdateActivity.this, "温馨提示", "需要开启对您手机的存储权限才能下载安装，是否现在开启", "去开启", "取消", true, new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//          startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
//        }
//      }, new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//          finish();
//        }
//      }).show();
//    } else {
//      finish();
//    }
//  }
//
//
//
//  @Override
//  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//  }
//
//}
