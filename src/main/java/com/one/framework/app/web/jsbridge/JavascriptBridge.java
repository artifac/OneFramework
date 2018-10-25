package com.one.framework.app.web.jsbridge;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.WebView;
import com.one.framework.app.web.BaseWebView;
import com.one.framework.app.web.jsbridge.functions.FunGetH5Cache;
import com.one.framework.app.web.jsbridge.functions.FunSetH5Cache;
import com.one.framework.app.web.jsbridge.functions.FuncClearCache;
import com.one.framework.app.web.jsbridge.functions.FuncGetContacts;
import com.one.framework.app.web.jsbridge.functions.FuncGetLocationInfo;
import com.one.framework.app.web.jsbridge.functions.FuncGetUserInfo;
import com.one.framework.app.web.jsbridge.functions.FuncHideProgressHUD;
import com.one.framework.app.web.jsbridge.functions.FuncPageRefresh;
import com.one.framework.app.web.jsbridge.functions.FuncShowProgressHUD;
import com.one.framework.app.web.jsbridge.functions.image.FuncImageLiteratureReview;
import com.one.framework.app.web.jsbridge.functions.image.FuncImageLiteratureReviewPhotoLibrary;
import com.one.framework.app.web.jsbridge.functions.image.FuncImageLiteratureReviewTakeCamera;
import com.one.framework.app.web.jsbridge.functions.image.FuncPhotograph;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 实现 js 与 native 的交互接口
 */
public class JavascriptBridge {

  /**
   * 使用该对象的 WebView
   */
  private WeakReference<BaseWebView> webViewReference;

  /**
   * 默认的 callback 函数名称
   */
  private static final String DEFAULT_CALLBACK_FUNC_NAME = "one.trip._callback";

  /**
   * 应用上下文
   */
  private Context mContext;

  private Activity mActivity;

  /**
   * <cmd, function> map
   */
  private HashMap<String, Function> mFunctionMap;

  /**
   * JS 代码中全局代码时间接收函数名
   */
  private String eventHandlerFuncName;

  public JavascriptBridge(BaseWebView webView) {
    webViewReference = new WeakReference<>(webView);
    mContext = webView.getContext();
    if (mContext instanceof Activity) {
      mActivity = (Activity) webView.getContext();
    }
    init();
  }

  public JavascriptBridge(Activity activity, BaseWebView webView) {
    webViewReference = new WeakReference<>(webView);
    mContext = webView.getContext();
    mActivity = activity;
    init();
  }

  /**
   * 检查 js 传过来的数据是否符合 bridge 协议
   *
   * @param data js 传递过来的数据
   * @return {@code true} 如果符合新的 bridge 协议， {@code false} 反之
   */
  public static boolean matchBridgeProtocol(JSONObject data) {
    if (data == null) {
      return false;
    }

    // 可能也没有 params 字段
    if (data.has("cmd") && data.has("id")/* && data.has("params")*/) {
      return true;
    }

    return false;
  }

  /**
   * 处理从 js 收到的数据
   *
   * @param webView js 所在的 WebView
   * @param data 从 js 传过来的 Json 数据
   */
  public static void onGetDataFromJs(WebView webView, JSONObject data) {
    String cmd = data.optString("cmd");
    JSONObject params = data.optJSONObject("params");
    //修复前端JSBridge库返回空参数不统一问题
    if (params != null && "{}".equals(params.toString())) {
      params = null;
    }
    String id = data.optString("id");
    String callback = data.optString("callback", DEFAULT_CALLBACK_FUNC_NAME);
    JsCallback jsCallback = new JsCallback(webView, id, callback);
    JsFunctionHandler.callHandler(webView, cmd, params, jsCallback);
  }

  /**
   * 添加一个 js 接口
   *
   * @param cmd js 接口对应的方法 id
   * @param function cmd 对应的方法实现
   */
  public void addFunction(String cmd, Function function) {
    if (mFunctionMap.containsKey(cmd)) {
      return;
    }
    mFunctionMap.put(cmd, function);
  }

  /**
   * 移除一个js接口
   */
  public void deleteFunction(String cmd) {
    mFunctionMap.remove(cmd);
  }

  /**
   * 获取 cmd 对应的 function
   *
   * @param cmd {@link #mFunctionMap} 中的 key
   * @return cmd 对应的 function
   */
  public Function getFunction(String cmd) {
    return mFunctionMap.get(cmd);
  }

  /**
   * 清空集合
   */
  public void clearAllFunctions() {
    if (mFunctionMap != null && mFunctionMap.size() > 0) {
      mFunctionMap.clear();
    }
  }

  /**
   * 初始化
   */
  private void init() {
    mFunctionMap = new HashMap<>();
    addCommonFunctions();
  }

  /**
   * 添加一些通用的方法
   */
  private void addCommonFunctions() {
    /* 测试 */
    addFunction("js_bridge_test", new Function() {
      @Override
      public JSONObject execute(JSONObject params) {
        JSONObject result = new JSONObject();
        try {
          result.put("test_key", "test_value");
        } catch (JSONException e) {
          e.printStackTrace();
        }
        return result;
      }
    });

    addFunction("initConfig", new Function() {
      @Override
      public JSONObject execute(JSONObject params) {
        String handler = params.optString("handler", null);
        if (!TextUtils.isEmpty(handler)) {
          setEventHandlerFuncName(handler);
        }
        return null;
      }
    });

    /* 获取用户信息 */
    addFunction("getUserInfo", new FuncGetUserInfo(mContext));
    /* 获取位置信息 */
    addFunction("getLocationInfo", new FuncGetLocationInfo(mContext));
    /* 获取本地的联系人列表 */
    addFunction("getContacts", new FuncGetContacts(mContext));
    /* 刷新当前的页面 */
    addFunction("page_refresh", new FuncPageRefresh(webViewReference));
    addFunction("refreshPage", new FuncPageRefresh(webViewReference));      //新版js参数
    /* 展示 loading 框 */
    addFunction("showProgressHUD", new FuncShowProgressHUD(webViewReference));
    /* 隐藏 loading 框 */
    addFunction("hideProgressHUD", new FuncHideProgressHUD(webViewReference));
    /* 图片上传 */
    addFunction("callbackImageLiteratureReview", new FuncImageLiteratureReview(webViewReference));
    addFunction("callbackImageLiteratureReviewTakeCamera",
        new FuncImageLiteratureReviewTakeCamera(webViewReference));
    addFunction("callbackImageLiteratureReviewPhotoLibrary",
        new FuncImageLiteratureReviewPhotoLibrary(webViewReference));
    //把数据存入本地
    addFunction("setH5Cache", new FunSetH5Cache(mContext));
    //从缓存中取数据
    addFunction("getH5Cache", new FunGetH5Cache(mContext));
    //清除缓存
    addFunction("clearCache", new FuncClearCache(mContext, webViewReference.get()));
    //拍照
    addFunction("photograph", new FuncPhotograph(webViewReference));
  }

  /**
   * java 调用H5的方法
   *
   * @param methodName 调用H5的方法名
   * @param json 传入的json参数 如果json==null表示调用h5的js方法不包含参数
   * @author liuwei
   */
  public void callH5Method(String methodName, String json) {
    WebView webView = webViewReference.get();
    if (webView != null) {
      String jsUrl = "";
      if (json == null) {
        jsUrl = "javascript:" + methodName + "()";
      } else {
        jsUrl = "javascript:" + methodName + "(" + json + ")";
      }
      webView.loadUrl(jsUrl);
    } else {
    }
  }

  /**
   * 设置 JS 全局事件接收函数名
   */
  public void setEventHandlerFuncName(String eventHandlerFuncName) {
    this.eventHandlerFuncName = eventHandlerFuncName;
  }

  /**
   * 清除 JS 全局事件接收函数
   */
  public void clearEventHandlerFunc() {
    setEventHandlerFuncName(null);
  }

  /**
   * 发送 JS 接收的全局事件
   *
   * stringer.key("id").value(getId());
   * stringer.key("eventname").value(eventName);
   * stringer.key("errno").value(getErrno());
   * stringer.key("errmsg").value(getErrmsg());
   * stringer.key("result").value(getResult());
   */
  public boolean sendJSEvent(WebView webView, JSONObject event) {
    String eventHandler = eventHandlerFuncName;
    if (TextUtils.isEmpty(eventHandler)) {
      return false;
    } else {
      callJavascriptFunc(webView, eventHandler, event.toString());
      return true;
    }
  }

  /**
   * java 调用H5的方法
   *
   * @param funcName 调用H5的方法名
   * @param json 传入的json参数 如果json==null表示调用h5的js方法不包含参数
   * @author liuwei
   */
  public static void callJavascriptFunc(WebView webView, String funcName, String json) {
    if (webView != null) {
      String jsUrl;
      if (TextUtils.isEmpty(json)) {
        jsUrl = "javascript: try {" + funcName + "(); } catch(e) {}";
      } else {
        jsUrl = "javascript: try {" + funcName + "(" + json + "); } catch(e) {}";
      }
      webView.loadUrl(jsUrl);
    } else {
    }
  }

  abstract public static class Function {

    /**
     * 是否需要自动回调 js
     */
    private boolean autoCallbackJs = true;
    /**
     * js callback 的包装，兼容
     */
    private JsCallbackWraper jsCallbackWraper;

    /**
     * 获取是否自动回调 js
     *
     * @return 是否自动回调 js
     */
    public boolean isAutoCallbackJs() {
      return autoCallbackJs;
    }

    /**
     * 设置自动回调 js
     *
     * @param autoCallbackJs 是否需要自动回调 js
     */
    public void setAutoCallbackJs(boolean autoCallbackJs) {
      this.autoCallbackJs = autoCallbackJs;
    }

    /**
     * 获取 js callback
     *
     * @return js callback wraper
     */
    public JsCallbackWraper getJsCallback() {
      return jsCallbackWraper;
    }

    /**
     * 设置 js callback
     *
     * @param jsCallback js callback
     */
    public void setJsCallback(JsCallback jsCallback) {
      if (jsCallback == null) {
        return;
      }
      jsCallbackWraper = new JsCallbackWraper(jsCallback);
    }

    /**
     * 方法的具体实现
     *
     * @param params js 传递过来的参数
     * @return 需要返回给 js 的数据，如果设置了{@link #autoCallbackJs} 为{@code false}，如要通过调用
     * {@link #jsCallbackWraper} 的
     * {@link com.one.framework.app.web.jsbridge.JsCallbackWraper#apply(JSONObject)} 方法来把
     * 结果返回给 js
     */
    abstract public JSONObject execute(JSONObject params);

    /**
     * 为了兼容部分老的代码;当{@link #execute(JSONObject)}返回 null 时,改方法才会生效
     */
    @Deprecated
    public String _execute(JSONObject params) {
      return null;
    }

    ;
  }

}
