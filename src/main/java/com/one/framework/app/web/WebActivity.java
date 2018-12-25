package com.one.framework.app.web;

import static com.one.framework.app.web.FileChooserManager.REQUEST_CODE_SELECT_PIC;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.app.login.UserProfile;
import com.one.framework.app.web.hybird.BaseHybridActivity;
import com.one.framework.app.web.hybird.FusionWebView;
import com.one.framework.app.web.jsbridge.functions.image.ImageHelper;
import com.one.framework.app.web.model.FusionBridgeModule;
import com.one.framework.app.web.tool.WebURLWriter;
import com.one.framework.log.Logger;
import com.one.framework.net.util.NetUtil;
import java.util.List;

/**
 * 该类为WEB基础类
 */
public class WebActivity extends BaseHybridActivity {

  public static final String ACTION_INTENT_BROADCAST_CLOSE = "action_intent_broadcast_close";
  public static final String KEY_WEB_VIEW_MODEL = "web_view_model";
  public static final String KEY_URL = "url";
  public static final String KEY_TITLE = "title";
  public static final String KEY_COUPON_ID = "getSelectedCouponID";

  private View mRootView;

  private WebTitleBar mWebTitleBar;
  private static BaseWebView mWebView;

  private View mErrorView;            //出错信息视图
  private ImageView mErrorImage;      //错误图片
  private TextView mErrorText;        //错误文案

  /**
   * 用来承载 url, title name 等信息
   */
  protected WebViewModel mFusionWebModel;

  protected FusionBridgeModule mJsBridge;

  private IWebViewClient mWebViewClient = null;

  private SHWebViewClient mSHWebViewClient = null;

  private ScreenOrientationMonitor mOrientationMonitor;

  private OverrideUrlLoaderSet mUrlOverriders = new OverrideUrlLoaderSet();

  private boolean mTitleByJs = false;
  private FileChooserManager mFileChooseManager;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    if (intent == null) {
      finishWithResultCodeCanceled();
      return;
    }

    if (intent.hasExtra(KEY_WEB_VIEW_MODEL)) {
      mFusionWebModel = (WebViewModel) intent.getSerializableExtra(KEY_WEB_VIEW_MODEL);
    } else if (intent.hasExtra(KEY_URL)) {
      mFusionWebModel = new WebViewModel();
      mFusionWebModel.url = intent.getStringExtra(KEY_URL);
      if (intent.hasExtra(KEY_TITLE)) {
        mFusionWebModel.title = intent.getStringExtra(KEY_TITLE);
        mFusionWebModel.canChangeWebViewTitle = false;
      }
    } else {
      Logger.e("ldx", "WebActivity can not get WebViewModel from extra data, exit.");
      finishWithResultCodeCanceled();
      return;
    }

    if (TextUtils.isEmpty(mFusionWebModel.url)) {
      finishWithResultCodeCanceled();
      return;
    }

    setContentView(R.layout.one_web_activity_layout);
    setupViews();

    registerBroadcastReceiver();
    mOrientationMonitor = new ScreenOrientationMonitor(WebActivity.this);
    mOrientationMonitor.onCreate();

    //如果子类截获, onCreate执行完毕, 交由子类继续执行
    if (!shouldInterceptOpenUrl()) {
      if (NetUtil.checkNetwork(WebActivity.this)) {
        openUrl(mFusionWebModel.url);
      } else {
        showErrorView(WebViewClient.ERROR_CONNECT, null, null);
      }
    }
  }

  private void setupViews() {
    mRootView = findViewById(R.id.root_view);
    mWebTitleBar = findViewById(R.id.web_title_bar);
    if (mFusionWebModel != null && !TextUtils.isEmpty(mFusionWebModel.title)) {
      mWebTitleBar.setTitleName(mFusionWebModel.title);
    }

    mWebTitleBar.setCloseVisible(false);
    mWebTitleBar.setRightVisible(mFusionWebModel.rightTextResId == -1 ? false : true);
    mWebTitleBar.setRightResId(mFusionWebModel.rightTextResId, Color.BLACK);
    mWebTitleBar.setRightImage(mFusionWebModel.rightIconResId);
    mWebTitleBar.setLeftImage(R.drawable.one_top_bar_back_selector);
    mWebTitleBar.setOnBackClickListener(mOnBackClickListener);
    mWebTitleBar.setOnCloseClickListener(mOnCloseClickListener);
    mWebTitleBar.setOnMoreClickListener(mOnRightClickListener);

    mErrorView = findViewById(R.id.web_error_view);
    mErrorImage = findViewById(R.id.web_error_image);
    mErrorText = findViewById(R.id.web_error_text);

    mWebView = findViewById(R.id.web_view);
    mWebView.setDownloadListener(new DownloadListener() {
      @Override
      public void onDownloadStart(String url, String userAgent, String contentDisposition,
          String mimeType, long contentLength) {
        try {
          Intent intent = new Intent(Intent.ACTION_VIEW);
          intent.addCategory(Intent.CATEGORY_BROWSABLE);
          intent.setData(Uri.parse(url));
          startActivity(intent);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    mWebView.requestFocus(View.FOCUS_DOWN);
    mWebView.setWebViewClient(new ToneWebViewClient(mWebView));
    mWebView.setWebViewSetting(mFusionWebModel);

    appendUserAgent(mWebView);
    mJsBridge = getFusionBridge();

    mFileChooseManager = new FileChooserManager(this);
    mFileChooseManager.setFileChooserListener(mWebView);

    addOverrideUrlLoader(new CommonUrlOverrider());
    addOverrideUrlLoader(new TicketUrlOverrider(this));
  }

  private void openUrl(String url) {
    url = appendQueryParams(url);
    onLoadUrl(url);
    mWebView.loadUrl(url);
  }


  //添加通用参数，及自定义参数
  protected String appendQueryParams(String url) {

    Uri uri = Uri.parse(url);

    //添加公共参数
    if (mFusionWebModel.isPostBaseParams || mFusionWebModel.isAddCommonParam) {
      List<Pair<String, String>> commonQueryList = WebURLWriter.combineBaseWebInfoAsPairList(this);
      uri = WebURLWriter.appendUriQuery(uri, commonQueryList);
    } else {
      if (url.contains("accesstoken") && !mFusionWebModel.isFromBuiness && !mFusionWebModel.isFromPaypal) {
        uri = WebURLWriter.replaceUriParameter(uri, "accesstoken", UserProfile.getInstance(this).getTokenValue());
      }
    }
//    }

    //添加自定义参数
    uri = WebURLWriter.appendEncodedUriQuery(uri, mFusionWebModel.customparams);

    return uri.toString();
  }

  private BroadcastReceiver mBroadcastReceiver = null;

  private void registerBroadcastReceiver() {
    IntentFilter mIntentFilter = new IntentFilter(ACTION_INTENT_BROADCAST_CLOSE);
    mBroadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        finishWithResultCodeOK();
      }
    };
    registerReceiver(mBroadcastReceiver, mIntentFilter);
  }


  public ScreenOrientationMonitor getScreenOrientationMonitor() {
    return mOrientationMonitor;
  }

  /****************************生命周期或系统回调方法****************************/

  @Override
  protected void onResume() {
    super.onResume();
    if (mWebView != null) {
      mWebView.onResume();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (mWebView != null) {
      mWebView.onPause();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
  }


  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (mFileChooseManager.isShowing()) {
        // 选择图片 弹出选择框之后 点击返回键 此处 REQUEST_CODE_SELECT_PIC || REQUEST_CODE_CAPTURE_PIC 任意 只是为了让其回调onReceiveValue(null)不阻塞
        mFileChooseManager.onActivityResult(REQUEST_CODE_SELECT_PIC, RESULT_CANCELED, null);
        mFileChooseManager.dismiss();
        return true;
      }
      goBack(true);
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (mWebView != null) {
      if (mWebView.getParent() != null) {
        ((ViewGroup) mWebView.getParent()).removeView(mWebView);
      }
      mWebView.removeAllViews();
      mWebView.destroy();
    }

    if (mBroadcastReceiver != null) {
      unregisterReceiver(mBroadcastReceiver);
      mBroadcastReceiver = null;
    }

    if (mOrientationMonitor != null) {
      mOrientationMonitor.onDestroy();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    /* 选择图片, From ImageHelper */
    if ((requestCode == ImageHelper.REQ_ALBUM_ACTIVITY || requestCode == ImageHelper.REQ_CAMERA_ACTIVITY)) {
      if (resultCode == RESULT_OK) {
        FusionBridgeModule fusionBridge = (FusionBridgeModule) mWebView
            .getExportModuleInstance(FusionBridgeModule.class);
        fusionBridge.handleChooseImageResult(requestCode, resultCode, data);
      }
    } else if ((requestCode == REQUEST_CODE_SELECT_PIC || requestCode == FileChooserManager.REQUEST_CODE_CAPTURE_PIC)) {
      mFileChooseManager.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
//    if (webViewToolDialog != null) {
//      webViewToolDialog.dismissH5Dialog();
//    }
    super.onConfigurationChanged(newConfig);
  }


  void finishWithResultCodeCanceled() {
    setResult(RESULT_CANCELED);
    finish();
  }

  private void finishWithResultCodeOK() {
    setResult(RESULT_OK, resultIntent);
    finish();
  }

  private Intent resultIntent;

  public void setResultIntent(Intent resultIntent) {
    this.resultIntent = resultIntent;
  }


  public void onHandleDialog(boolean isShow) {
    mOrientationMonitor.updateActivityOrientation(isShow);
  }

  /**
   * 展示出错信息
   */
  private void showErrorView(int errorCode, String description, String failingUrl) {
    mErrorView.setVisibility(View.VISIBLE);
//    if (errorCode == WebViewClient.ERROR_FILE_NOT_FOUND) {
//      mErrorImage.setImageResource(R.drawable.icon_webview_error_notfound);
//      mErrorText.setText(R.string.webview_error_notfound);
//
//      mErrorView.setOnClickListener(null);
//    } else if (errorCode == WebViewClient.ERROR_HOST_LOOKUP
//        || errorCode == WebViewClient.ERROR_CONNECT
//        || errorCode == WebViewClient.ERROR_PROXY_AUTHENTICATION) {
//      mErrorImage.setImageResource(R.drawable.icon_webview_error_connectfail);
//      mErrorText.setText(R.string.webview_error_connectfail);
//
//      mErrorView.setOnClickListener(onClickListenerReload);
//    } else if (errorCode == WebViewClient.ERROR_TIMEOUT) {
//      mErrorImage.setImageResource(R.drawable.icon_webview_error_busy);
//      mErrorText.setText(R.string.webview_error_busy);
//
//      mErrorView.setOnClickListener(null);
//    } else {
//      mErrorImage.setImageResource(R.drawable.icon_webview_error_connectfail)o;
//      mErrorText.setText(R.string.webview_error_connectfail);
//      mErrorView.setOnClickListener(onClickListenerReload);
//    }
  }


  public boolean goBack(boolean allowFinish) {
    hideEntrance();
    if (mErrorView != null) {
      mErrorView.setVisibility(View.GONE);
    }
    WebBackForwardList history = mWebView.copyBackForwardList();
    int index = -1;
    boolean canGoBack = false;

    String currentUrl = mWebView.getUrl();
    while (mWebView.canGoBackOrForward(index)) {

      if (TextUtils.equals(currentUrl, "about:blank") && !NetUtil.checkNetwork(this)) {
        canGoBack = true;
        finishWithResultCodeOK();
        break;
      }
      WebHistoryItem historyItem = history.getItemAtIndex(history.getCurrentIndex() + index);
      String url = historyItem == null ? null : historyItem.getUrl();
      if (url != null && !TextUtils.equals(url, currentUrl) && !TextUtils
          .equals(url, "about:blank")) {
        mWebView.goBackOrForward(index);
        canGoBack = true;
        break;
      }
      index--;
    }

    if (!canGoBack && allowFinish) {
      finishWithResultCodeOK();
    }

    return canGoBack;
  }


  public class ToneWebViewClient extends BaseWebView.WebViewClientEx {

    public ToneWebViewClient(FusionWebView webView) {
      super(webView);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      Logger.e("WebActivity", "InnerWebViewClient#shouldOverrideUrlLoading: " + url);
      boolean overrided = mUrlOverriders.shouldOverrideUrlLoading(view, url);
      if (overrided) {
        return true;
      }

      if (mSHWebViewClient != null) {
        if (mSHWebViewClient.shouldOverrideUrlLoading(view, url)) {
          return true;
        }
      }

      return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
      super.onPageStarted(view, url, favicon);
      hideEntrance();
      mWebTitleBar.setTitleName(mFusionWebModel.title);
      if (mWebViewClient != null) {
        mWebViewClient.onPageStarted(view, url, favicon);
      }
      mTitleByJs = false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);

      //返回和关闭按钮处理
      if (mWebView.canGoBack()) {
        mWebTitleBar.setCloseVisible(true);
      } else {
        mWebTitleBar.setCloseVisible(false);
      }

      if (mFusionWebModel.canChangeWebViewTitle && !mTitleByJs) {
        String webTitle = view.getTitle();
        if (!URLUtil.isNetworkUrl(webTitle)) {
          mWebTitleBar.setTitleName(view.getTitle());
        }
      }

      if (mFusionWebModel.rightTextResId != -1) {
        mWebTitleBar.setRightResId(mFusionWebModel.rightTextResId, Color.BLACK);
      }

      if (mWebViewClient != null) {
        mWebViewClient.onPageFinished(view, url);
      }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description,
        String failingUrl) {
      if (Build.VERSION.SDK_INT < 18) {
        view.clearView();
      } else {
        view.loadUrl("about:blank");
      }

      if (mWebViewClient != null) {
        mWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
      }

      showErrorView(errorCode, description, failingUrl);
    }
  }

  /**
   * 标题栏返回按钮的点击事件监听
   */
  private View.OnClickListener mOnBackClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      goBack(true);
    }
  };

  /**
   * 标题栏关闭按钮的点击事件监听
   */
  private View.OnClickListener mOnCloseClickListener = v -> finishWithResultCodeOK();

  private View.OnClickListener mOnRightClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      if (mFusionWebModel != null) {
        if (!TextUtils.isEmpty(mFusionWebModel.rightNextUrl)) {
          WebViewModel webViewModel = new WebViewModel();
          webViewModel.url = mFusionWebModel.rightNextUrl;
          Intent intent = new Intent(getActivity(), WebActivity.class);
          intent.putExtra(WebActivity.KEY_WEB_VIEW_MODEL, webViewModel);
          startActivity(intent);
        } else if (!TextUtils.isEmpty(mFusionWebModel.jsMethod)) {

          mJsBridge.callH5Method(mFusionWebModel.jsMethod, null);
        }
      }
    }
  };

  private View.OnClickListener onClickListenerReload = new View.OnClickListener() {

    private long mLastClickTime = 0;

    @Override
    public void onClick(final View v) {
      long currClickTime = System.currentTimeMillis();
      if ((currClickTime - mLastClickTime) < 3000) {
        return;
      }

      String url = mWebView.getUrl();

      if (TextUtils.equals(url, "about:blank")) {
        url = "";
        WebBackForwardList history = mWebView.copyBackForwardList();
        int index = -1;
        while (mWebView.canGoBackOrForward(index)) {
          int currentIndex = history.getCurrentIndex();

          WebHistoryItem historyItem = history.getItemAtIndex(currentIndex + index);
          String indexUrl = historyItem == null ? null : historyItem.getUrl();
          if (indexUrl != null && !indexUrl.equals("about:blank")) {
            url = indexUrl;
            break;
          }
          index--;
        }
      }

      if (!TextUtils.isEmpty(url)) {
        mWebView.loadUrl(url);
        mErrorView.setVisibility(View.GONE);
      } else {
        openUrl(mFusionWebModel.url);
        mErrorView.setVisibility(View.GONE);
      }

      mLastClickTime = currClickTime;
    }
  };


  /**********************对外或者子类开放接口***********************/


  public View getRootView() {
    return mRootView;
  }

  /**
   * 获取标题栏
   */
  public WebTitleBar getWebTitleBar() {
    return mWebTitleBar;
  }


  /**
   * 获取当前的 WebView 对象
   */
  public BaseWebView getWebView() {
    return mWebView;
  }


  /**
   * FusionBridge用来兼容老的Bridge注册机制
   */
  protected FusionBridgeModule getFusionBridge() {
    return (null == mWebView) ? null
        : (FusionBridgeModule) mWebView.getExportModuleInstance(FusionBridgeModule.class);
  }


  /**
   * 添加url截获, 注册进来的OverrideUrlLoader 会在WebViewClient shouldOverrideUrl中调用
   */
  protected void addOverrideUrlLoader(OverrideUrlLoader urlOverrider) {
    mUrlOverriders.addOverrideUrlLoader(urlOverrider);
  }

  /**
   * 用于设置WebViewClient，对外暴露相应方法
   */
  public void reSetWebViewClient(IWebViewClient mIWebViewClient) {
    this.mWebViewClient = mIWebViewClient;
  }

  public interface IWebViewClient {

    void onPageStarted(WebView view, String url, Bitmap favicon);

    void onPageFinished(WebView view, String url);

    void onReceivedError(WebView view, int errorCode, String description, String failingUrl);
  }

  public void shSetWebViewClient(SHWebViewClient shWebViewClient) {
    this.mSHWebViewClient = shWebViewClient;
  }

  public interface SHWebViewClient {

    boolean shouldOverrideUrlLoading(WebView view, String url);
  }


  //暴露给子类修改UA
  protected void appendUserAgent(FusionWebView webview) {
  }


  //提供一个入口,让子类接管后续的URL加载, 满足多样化需求
  protected boolean shouldInterceptOpenUrl() {
    return false;
  }

  //将要加载url
  protected void onLoadUrl(String url) {
  }


  public void cancelProgressDialog() {
    mWebView.hideLoadingDialog();
  }

  /**********************右上角更多按钮相关逻辑***********************/

  /**
   * 隐藏右上角的分享入口
   */
  protected void hideEntrance() {
    if (mFusionWebModel != null) {
      if (mFusionWebModel.rightTextResId != 0 || mFusionWebModel.rightIconResId != 0) {
        mWebTitleBar.setRightVisible(true);
      } else {
        mWebTitleBar.setRightVisible(false);
      }
    }

  }

}
