package com.one.framework.app.web;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.webkit.ValueCallback;
import com.one.framework.R;
import com.one.framework.app.web.jsbridge.functions.image.BottomListMenu;
import com.one.framework.app.web.jsbridge.functions.image.BottomListMenu.OnDismissListener;
import com.one.framework.log.Logger;
import com.one.framework.utils.FileUtils;
import java.io.File;

/**
 * WebActivity 文件选择管理类
 */

public class FileChooserManager {

  public static final int REQUEST_CODE_SELECT_PIC = 1005;
  public static final int REQUEST_CODE_CAPTURE_PIC = 1006;

  private static final int RESULT_OK = Activity.RESULT_OK;

  private WebActivity mActivity;
  private View mRootView;
  /**
   * 选中的文件
   */
  private File mChosenFile;
  /**
   * 文件选择的回调
   */
  private ValueCallback<Uri> mFileChooserCallback;
  /**
   * 文件选择的回调（适配Lollipop以上版本）
   */
  private ValueCallback<Uri[]> uploadMessageAboveL;

  private BottomListMenu mAvatarMenu;


  public FileChooserManager(WebActivity webActivity) {
    mActivity = webActivity;
    mRootView = webActivity.getRootView();
  }


  public void setFileChooserListener(BaseWebView baseWebView) {
    baseWebView.setFileChooserListener(mFileChooserListener);
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    /* 文件(图片)选择 */
    if (requestCode == REQUEST_CODE_SELECT_PIC || requestCode == REQUEST_CODE_CAPTURE_PIC) {
      if (uploadMessageAboveL != null) {
        handleFileChooseResultAboveL(requestCode, resultCode, data);
      } else {
        handleFileChooseResult(requestCode, resultCode, data);
      }
      return;
    }
  }

  /**
   * 处理文件(图片)选择结果
   */
  private void handleFileChooseResult(int requestCode, int resultCode, Intent data) {
    Uri results = null;
    if (requestCode == REQUEST_CODE_SELECT_PIC && resultCode == RESULT_OK) {
      String dataString = data.getDataString();
      if (dataString != null) {
        results = Uri.parse(dataString);
      }
    }

    if (requestCode == REQUEST_CODE_CAPTURE_PIC && resultCode == RESULT_OK) {
      if (mChosenFile != null) {
        results = Uri.fromFile(mChosenFile);
      }

    }
    if (mFileChooserCallback != null) {
      mFileChooserCallback.onReceiveValue(results);
    }
    mFileChooserCallback = null;
    mChosenFile = null;
  }


  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void handleFileChooseResultAboveL(int requestCode, int resultCode, Intent data) {
    if (uploadMessageAboveL == null) {
      return;
    }
    Uri[] results = null;
    if (requestCode == REQUEST_CODE_SELECT_PIC && resultCode == Activity.RESULT_OK) {
      if (data != null) {
        String dataString = data.getDataString();
        ClipData clipData = data.getClipData();
        if (clipData != null) {
          results = new Uri[clipData.getItemCount()];
          for (int i = 0; i < clipData.getItemCount(); i++) {
            ClipData.Item item = clipData.getItemAt(i);
            results[i] = item.getUri();
          }
        }
        if (dataString != null) {
          results = new Uri[]{Uri.parse(dataString)};
        }
      }
    }

    if (requestCode == REQUEST_CODE_CAPTURE_PIC && resultCode == RESULT_OK) {
      if (mChosenFile != null) {
        results = new Uri[]{Uri.fromFile(mChosenFile)};
      }
    }
    uploadMessageAboveL.onReceiveValue(results);
    uploadMessageAboveL = null;
    mChosenFile = null;
  }


  private BaseWebView.FileChooserListener mFileChooserListener = new BaseWebView.FileChooserListener() {

    @Override
    public void openFileChooser(ValueCallback<Uri> valueCallback) {
      mFileChooserCallback = valueCallback;
      showMenuDialog();
    }

    /**
     * above L invoke this method
     * @param filePathCallback
     */
    @Override
    public void openFileChooserAboveL(ValueCallback<Uri[]> filePathCallback) {
      uploadMessageAboveL = filePathCallback;
      showMenuDialog();
    }
  };

  /**
   * native choose image method
   */
  private void showMenuDialog() {
    mAvatarMenu = new BottomListMenu(mActivity, mRootView, mActivity.getResources().getStringArray(R.array.one_photos));
    mAvatarMenu.setListMenuListener((position, itemStr) -> {
      if (position == 0) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mChosenFile = FileUtils.getPhotoOutputFile(mActivity.getPackageName());
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        Uri uri;
        if (VERSION.SDK_INT >= Build.VERSION_CODES.N){
          uri = FileProvider.getUriForFile(mActivity.getApplicationContext(), "com.one.trip.FileProvider", mChosenFile);
        } else {
          uri = Uri.fromFile(mChosenFile);
        }
        Logger.e("ldx",  "uri >> " + uri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mActivity.startActivityForResult(intent, REQUEST_CODE_CAPTURE_PIC);
      } else if (position == 1) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        try {
          mActivity.startActivityForResult(intent, REQUEST_CODE_SELECT_PIC);
        } catch (Exception e) {
        }
      }
    });
    mAvatarMenu.setDismissListener(new OnDismissListener() {
      @Override
      public void dismiss() {
        mChosenFile = null;
        onActivityResult(REQUEST_CODE_SELECT_PIC, Activity.RESULT_CANCELED, null);
      }
    });
    mAvatarMenu.showDialog();
  }

  public void dismiss() {
    if (mAvatarMenu != null && mAvatarMenu.isShowing()) {
      mAvatarMenu.dismiss();
    }
  }

  public boolean isShowing() {
    return mAvatarMenu != null && mAvatarMenu.isShowing();
  }
}
