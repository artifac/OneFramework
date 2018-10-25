//package com.one.framework.download;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//
//public class CheckUpdateManager {
//
//
//  private ProgressDialog mWaitDialog;
//  private Context mContext;
//  private boolean mIsShowDialog;
//
//
//  public CheckUpdateManager(Context context, boolean showWaitingDialog) {
//    this.mContext = context;
//    mIsShowDialog = showWaitingDialog;
//    if (mIsShowDialog) {
//      mWaitDialog = DialogHelper.getProgressDialog(mContext);
//      mWaitDialog.setMessage("正在检查中...");
//      mWaitDialog.setCancelable(false);
//      mWaitDialog.setCanceledOnTouchOutside(false);
//    }
//  }
//
//
//  public void checkUpdate() {
//    if (mIsShowDialog) {
//      mWaitDialog.show();
//    }
//    OSChinaApi.checkUpdate(new TextHttpResponseHandler() {
//      @Override
//      public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//        if (mIsShowDialog) {
//          DialogHelper.getMessageDialog(mContext, "网络异常，无法获取新版本信息").show();
//        }
//      }
//
//      @Override
//      public void onSuccess(int statusCode, Header[] headers, String responseString) {
//        //此处省略若干代码
//        int curVersionCode = TDevice.getVersionCode(AppContext
//            .getInstance().getPackageName());
//        //version:服务器解析后实体bean
//        if (curVersionCode < version.getCode()) {
//          UpdateActivity.show((Activity) mContext, version);
//        } else {
//          if (mIsShowDialog) {
//            DialogHelper.getMessageDialog(mContext, "已经是新版本了").show();
//          }
//        }
//
//      }
//
//      @Override
//      public void onFinish() {
//        super.onFinish();
//        if (mIsShowDialog) {
//          mWaitDialog.dismiss();
//        }
//      }
//    });
//  }
//
//}
