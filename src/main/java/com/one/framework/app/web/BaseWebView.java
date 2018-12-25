package com.one.framework.app.web;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Keep;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.one.framework.R;
import com.one.framework.app.web.hybird.FusionWebView;
import com.one.framework.app.web.hybird.FusionWebViewClient;
import com.one.framework.app.web.model.FusionBridgeModule;
import com.one.framework.dialog.SupportDialogFragment;

public class BaseWebView extends FusionWebView {

  /**
   * alert 框
   */
  private SupportDialogFragment mAlertDialogFragment;
  /**
   * FileChooser的监听
   */
  private FileChooserListener mFileChooserListener;

  public BaseWebView(Context context) {
    super(context);
    init(context);
  }

  public BaseWebView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public BaseWebView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context);
  }

  public FusionBridgeModule getFusionBridge() {
    return (FusionBridgeModule) getExportModuleInstance(FusionBridgeModule.class);
  }

  /**
   * 初始化
   *
   * @param context 应用上下文
   */
  private void init(Context context) {

    setWebViewClient(new WebViewClientEx(this));
    setWebChromeClient(new WebChromeClientEx(this));

//    if (Build.VERSION.SDK_INT >= 24) {
//      MultiLocaleStore.getInstance().getLocaleHelper().refreshAppLocale(getContext());
//    }
  }

  /**
   * 根据WebViewModel设置WebView的属性
   */
  public void setWebViewSetting(WebViewModel webViewModel) {
    WebSettings settings = getSettings();
    if (webViewModel == null || settings == null) {
      return;
    }
    if (webViewModel.isSupportCache) {
      settings.setCacheMode(WebSettings.LOAD_DEFAULT);
    } else {
      settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }
  }

  public void setFileChooserListener(FileChooserListener listener) {
    mFileChooserListener = listener;
  }

  /**
   * 弹出提示框
   *
   * @param message 提示消息
   */
  private void alert(String message) {
    Context context = getContext();
    if (context instanceof FragmentActivity) {
      FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

      SupportDialogFragment.Builder builder = new SupportDialogFragment.Builder(getContext());
      builder.setMessage(message)
          .setPositiveButton(getContext().getString(R.string.one_i_know),
              v -> mAlertDialogFragment.dismiss());
      mAlertDialogFragment = builder.create();

      try {
        mAlertDialogFragment.show(fragmentManager, null);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public class WebChromeClientEx extends /*FusionWebChromeClient*/ WebChromeClient {
    public WebChromeClientEx(FusionWebView webView) {

    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
      if (view instanceof BaseWebView) {
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
          ((BaseWebView) view).alert(message);
        }
      }
      result.confirm();
      return true;
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
      callback.invoke(origin, true, false);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
        FileChooserParams fileChooserParams) {
      if (mFileChooserListener != null) {
        mFileChooserListener.openFileChooserAboveL(filePathCallback);
      }
      return true;
    }

    @Keep
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
      if (mFileChooserListener != null) {
        mFileChooserListener.openFileChooser(uploadMsg);
      }
    }

    // For Android 3.0+
    @Keep
    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
      if (mFileChooserListener != null) {
        mFileChooserListener.openFileChooser(uploadMsg);
      }
    }

    //For Android 4.1
    @Keep
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
      if (mFileChooserListener != null) {
        mFileChooserListener.openFileChooser(uploadMsg);
      }
    }
  }


  public static class WebViewClientEx extends FusionWebViewClient {

    public WebViewClientEx(FusionWebView webView) {
      super(webView);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
      if (StrategyManager.getInstance().isUrlInWhiteList(url)) {
        url = StrategyManager.getInstance().appendToken(url);
      }
      super.onLoadResource(view, url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      view.getSettings().setBlockNetworkImage(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
      long startTime = System.currentTimeMillis();
      WebResourceResponse webResourceResponse = super.shouldInterceptRequest(view, request);
      return webResourceResponse;
    }
  }


  public interface FileChooserListener {

    void openFileChooser(ValueCallback<Uri> valueCallback);

    void openFileChooserAboveL(ValueCallback<Uri[]> filePathCallback);
  }

}
