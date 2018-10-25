package com.one.framework.app.web;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.ValueCallback;
import com.one.framework.R;
import com.one.framework.app.web.jsbridge.functions.image.BottomListMenu;
import com.one.framework.utils.FileUtils;
import java.io.File;

/**
 * WebActivity 文件选择管理类
 * Created by zhengtao on 17/8/3.
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
  }


  private BaseWebView.FileChooserListener mFileChooserListener = new BaseWebView.FileChooserListener() {

    @Override
    public void openFileChooser(ValueCallback<Uri> valueCallback) {
      mFileChooserCallback = valueCallback;
      showMenuDialog();
    }


    @Override
    public void openFileChooserAboveL(ValueCallback<Uri[]> filePathCallback) {
      uploadMessageAboveL = filePathCallback;
      showMenuDialog();
    }

    private void showMenuDialog() {
      BottomListMenu mAvatarMenu = new BottomListMenu(mActivity, mRootView,
          mActivity.getResources().getStringArray(R.array.one_photos));
      mAvatarMenu.setListMenuListener(new BottomListMenu.ListMenuListener() {
        @Override
        public void onItemSelected(int position, String itemStr) {
          if (position == 0) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mChosenFile = FileUtils.getPhotoOutputFile();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mChosenFile));
            mActivity.startActivityForResult(intent, REQUEST_CODE_CAPTURE_PIC);
          } else if (position == 1) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            try {
              mActivity.startActivityForResult(intent, REQUEST_CODE_SELECT_PIC);
            } catch (Exception e) {
            }
          }
        }
      });
      mAvatarMenu.setDismissListener(new BottomListMenu.OnDismissListener() {
        @Override
        public void dismiss() {
          mChosenFile = null;
          onActivityResult(REQUEST_CODE_CAPTURE_PIC, Activity.RESULT_OK, null);
        }
      });
      mAvatarMenu.showDialog();
    }
  };
}
