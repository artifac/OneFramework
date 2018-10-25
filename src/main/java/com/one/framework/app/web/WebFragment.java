package com.one.framework.app.web;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.app.base.BaseFragment;
import com.one.framework.app.web.hybird.FusionWebView;
import com.one.framework.app.web.model.FusionBridgeModule;
import com.one.framework.net.util.NetUtil;
import com.one.framework.utils.CommonParamsUtil;

/**
 * 基础网页fragment
 */
public class WebFragment extends BaseFragment {

  /**
   * 参数名称
   */
  public static final String KEY_WEB_VIEW_URL = "web_view_url";
  public static final String KEY_WEB_VIEW_MODEL = "web_view_model";

  /**
   * 根view
   */
  private View mRootView;
  /**
   * 网页view
   */
  protected BaseWebView mWebView;
  /**
   * 网页参数信息
   */
  protected WebViewModel mWebViewModel;
  /**
   * 进度
   */
  protected LinearLayout mProgressLayout;

  private View mErrorView;            //出错信息视图
  private ImageView mErrorImage;      //错误图片
  private TextView mErrorText;        //错误文案

  /**
   * js回调
   */
  protected FusionBridgeModule mJsBridge;
  /**
   * 分享选择框的内容列表
   */
  private OverrideUrlLoaderSet mUrlOverriders = new OverrideUrlLoaderSet();

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.one_webview_fragment_layout, container, false);
    init();
    return mRootView;
  }

  private void init() {
    mWebView = (BaseWebView) mRootView.findViewById(R.id.web_view);
//        mWebView.setUpdateUIHandler(this);
    mProgressLayout = (LinearLayout) mRootView.findViewById(R.id.progress_view);

    mErrorView = mRootView.findViewById(R.id.web_error_view);
    mErrorImage = (ImageView) mRootView.findViewById(R.id.web_error_image);
    mErrorText = (TextView) mRootView.findViewById(R.id.web_error_text);

    initArgument();
    initWebView();
  }

  protected void initArgument() {
    Bundle bundle = getArguments();
    //防止后台没有配置url
    mWebViewModel = new WebViewModel();
    if (bundle != null) {
      if (bundle.containsKey(KEY_WEB_VIEW_MODEL)) {
        mWebViewModel = (WebViewModel) bundle.getSerializable(KEY_WEB_VIEW_MODEL);
      }

      if (bundle.containsKey(KEY_WEB_VIEW_URL)) {
        mWebViewModel.url = bundle.getString(KEY_WEB_VIEW_URL);
        mWebViewModel.isSupportCache = true;
      }
    }
  }


  private void initWebView() {
    if (TextUtils.isEmpty(mWebViewModel.url)) {
      return;
    }

    mWebView.setWebViewSetting(mWebViewModel);
    mWebView.setWebViewClient(new InnerWebViewClient(mWebView));

    //不显示滑动条
    mWebView.setVerticalScrollBarEnabled(false);
    mWebView.setHorizontalScrollBarEnabled(false);

    mJsBridge = mWebView.getFusionBridge();

    if (NetUtil.checkNetwork(getContext())) {
      openUrl(mWebViewModel.url);
    } else {
      showErrorView(WebViewClient.ERROR_CONNECT, null, null);
    }

  }

  private void openUrl(String url) {
    showProgress();

    String newUrl = url;
    if (mWebViewModel.isAddCommonParam) {
      newUrl = addCommonUrl(url);
    }
    mWebView.loadUrl(newUrl);
  }

  @NonNull
  private String addCommonUrl(String url) {
    if (TextUtils.isEmpty(url)) {
      return "";
    }

//    if (WebConfigStore.getInstance().isWhiteUrl(url, getActivity())) {
      //白名单域名添加公共参数和token
      if (url.endsWith("?")) {// 如果有？号，则直接附近参数
        url += CommonParamsUtil.createCommonParamString(getActivity());
      } else if (url.indexOf("?") > 1) {// 如果？号不是在最后，则在最后添加参数
        if (url.endsWith("&")) {
          url += CommonParamsUtil.createCommonParamString(getActivity());
        } else {
          url += "&" + CommonParamsUtil.createCommonParamString(getActivity());
        }
      } else {// 如果没有?号，则直接在最后面添加？号，并且添加参数
        url += "?" + CommonParamsUtil.createCommonParamString(getActivity());
      }
//    }
    return url;
  }

  protected void showProgress() {
    if (mProgressLayout.getVisibility() != View.VISIBLE) {
      mProgressLayout.setVisibility(View.VISIBLE);
    }
  }

  protected void hideProgress() {
    if (mProgressLayout.getVisibility() == View.VISIBLE) {
      mProgressLayout.setVisibility(View.GONE);
    }
  }


  /**
   * 展示出错信息
   */
  private void showErrorView(int errorCode, String description, String failingUrl) {
    mErrorView.setVisibility(View.VISIBLE);
    if (errorCode == WebViewClient.ERROR_FILE_NOT_FOUND) {
//            mErrorImage.setImageResource(R.drawable.icon_webview_error_notfound);
      mErrorText.setText(R.string.webview_error_notfound);

      mErrorView.setOnClickListener(null);
    } /*else if (errorCode == WebViewClient.ERROR_HOST_LOOKUP
                || errorCode == WebViewClient.ERROR_CONNECT || errorCode == WebViewClient.ERROR_PROXY_AUTHENTICATION) {
            mErrorImage.setImageResource(R.drawable.icon_webview_error_connectfail);
            mErrorText.setText(R.string.webview_error_connectfail);

            mErrorView.setOnClickListener(mReloadListener);
        } else if (errorCode == WebViewClient.ERROR_TIMEOUT) {
            mErrorImage.setImageResource(R.drawable.icon_webview_error_busy);
            mErrorText.setText(R.string.webview_error_busy);

            mErrorView.setOnClickListener(null);
        } else {
            mErrorImage.setImageResource(R.drawable.icon_webview_error_connectfail);
            mErrorText.setText(R.string.webview_error_connectfail);
            mErrorView.setOnClickListener(mReloadListener);
        }*/
  }

  private View.OnClickListener mReloadListener = new View.OnClickListener() {

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
        openUrl(mWebViewModel.url);
      }

      mLastClickTime = currClickTime;
    }
  };

  /**
   * 展示 loading dialog
   */
  protected void showProgressDialog(String message) {
  }

  protected class InnerWebViewClient extends BaseWebView.WebViewClientEx {

    public InnerWebViewClient(FusionWebView view) {
      super(view);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
      super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      view.getSettings().setBlockNetworkImage(false);
      hideProgress();
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description,
        String failingUrl) {
      super.onReceivedError(view, errorCode, description, failingUrl);
      hideProgress();
      if (Build.VERSION.SDK_INT < 18) {
        view.clearView();
      } else {
        view.loadUrl("about:blank");
      }
      showErrorView(errorCode, description, failingUrl);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      boolean overrided = mUrlOverriders.shouldOverrideUrlLoading(view, url);
      if (overrided) {
        return true;
      }
      return super.shouldOverrideUrlLoading(view, url);
    }
  }

  /**
   * 添加url截获, 注册进来的OverrideUrlLoader 会在WebViewClient shouldOverrideUrl中调用
   */
  protected void addOverrideUrlLoader(OverrideUrlLoader urlOverrider) {
    mUrlOverriders.addOverrideUrlLoader(urlOverrider);
  }
}
