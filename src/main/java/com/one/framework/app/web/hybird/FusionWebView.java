package com.one.framework.app.web.hybird;

import static android.webkit.WebSettings.LOAD_DEFAULT;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.ProgressBar;
import com.one.framework.app.web.BaseWebView.WebViewClientEx;
import com.one.framework.app.web.WebActivity;
import com.one.framework.app.web.js.WebViewJsBridge;

public class FusionWebView extends WebView {

  private static final String TAG = "FusionWebView";
  protected IHybridActivity mHybridContainer = new WebActivity();
  private ProgressBar mProgressBar;
  private boolean isShowProgressBar;

  public FusionWebView(Context context) {
    super(context);
    initWebView();
  }

  public FusionWebView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initWebView();
  }

  public FusionWebView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initWebView();
  }

  private void initWebView() {
    this.isShowProgressBar = false;
    WebSettings webSettings = this.getSettings();
    webSettings.setPluginState(PluginState.ON);
    webSettings.setJavaScriptEnabled(true);
    webSettings.setAllowFileAccess(false);
    webSettings.setUseWideViewPort(true);
    webSettings.setBuiltInZoomControls(false);
    webSettings.setDefaultTextEncodingName("UTF-8");
    webSettings.setDomStorageEnabled(true);
    webSettings.setCacheMode(LOAD_DEFAULT);
    webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
    if(VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      setWebContentsDebuggingEnabled(true);
    }
    if (VERSION.SDK_INT < 18) {
      webSettings.setRenderPriority(RenderPriority.HIGH);
    }

    addProgressView();
  }

  private void addProgressView() {
    try {
      this.mProgressBar = new ProgressBar(this.getContext());
      this.mProgressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 4, 0, 0));
      ClipDrawable d = new ClipDrawable(new ColorDrawable(Color.parseColor("#1665ff")), Gravity.TOP, ClipDrawable.HORIZONTAL);
      this.mProgressBar.setProgressDrawable(d);
      this.mProgressBar.setVisibility(View.GONE);
      this.addView(this.mProgressBar);
    } catch (Exception var2) {
      var2.printStackTrace();
    }

  }

  public void showLoadingDialog() {
    this.mProgressBar.setVisibility(View.VISIBLE);
    this.isShowProgressBar = true;
  }

  public void hideLoadingDialog() {
    if (this.mProgressBar != null) {
      this.mProgressBar.setVisibility(View.GONE);
      this.isShowProgressBar = false;
    }
  }

  public Object getExportModuleInstance(Class clazz) {
    return mHybridContainer.getExportModuleInstance(clazz);
  }

  public WebViewJsBridge getWebViewJsBridge() {
    return new WebViewClientEx(this);
  }
}
