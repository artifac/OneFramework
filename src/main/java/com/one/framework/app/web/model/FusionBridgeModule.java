package com.one.framework.app.web.model;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import com.one.framework.MainActivity;
import com.one.framework.app.web.WebActivity;
import com.one.framework.app.web.WebViewModel;
import com.one.framework.app.web.hybird.FusionWebView;
import com.one.framework.app.web.hybird.IHybridActivity;
import com.one.framework.app.web.js.CallbackFunction;
import com.one.framework.app.web.js.ExportNamespace;
import com.one.framework.app.web.js.JsInterface;
import com.one.framework.app.web.js.WebViewJsBridge;
import com.one.framework.app.web.jsbridge.functions.image.ImageCallback;
import com.one.framework.app.web.jsbridge.functions.image.ImageHelper;
import com.one.framework.app.web.jsbridge.functions.image.ImageUploadCallback;
import com.one.framework.app.web.jsbridge.functions.image.PicUploadActivity;
import com.one.framework.utils.UIThreadHandler;
import com.one.map.location.LocationProvider;
import com.one.map.model.Address;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * //获取App相关信息，如系统信息、设备信息、用户信息、以及地理位置等信息
 */
public class FusionBridgeModule extends BaseHybridModule {

  //定位信息
  public static final String P_LAT = "lat";
  public static final String P_LNG = "lng";
  public static final String P_CITY_ID = "city_id";
  public static final String P_AREA = "area";

  //更新UI KEY
  public static final String UI_TARGET_WEB_TITLE = "web_title";

  private Activity mContext;
  private WebViewJsBridge mJavascriptBridge;
  private FusionWebView mFusionWebView;

  private Map<String, Function> handlerMap = new HashMap<>();

  public FusionBridgeModule(IHybridActivity hybridContainer) {
    super(hybridContainer);
    mFusionWebView = hybridContainer.getWebView();
    mContext = hybridContainer.getActivity();
    mJavascriptBridge = hybridContainer.getWebView().getWebViewJsBridge();
  }


  @JsInterface("callHandler")
  public Object callHandler(String handlerName, JSONObject handlerParams,
      CallbackFunction callbackFunction) {

    Object returnValue = null;

    Function function = handlerMap.get(handlerName);
    if (function != null) {
      function.setJsCallback(callbackFunction);
      returnValue = function.execute(handlerParams);
      if (returnValue != null) {
        callbackFunction.onCallBack(returnValue);
      }
    } else {
      Method method = tryFindEqualMethod(handlerName);
      if (method != null) {
        try {
          returnValue = method.invoke(this, handlerParams, callbackFunction);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }
    return returnValue;
  }


  public void addFunction(String handlerName, Function handlerFunc) {
    handlerMap.put(handlerName, handlerFunc);
  }

  public static abstract class Function {

    private CallbackFunction jsCallback;

    public void setJsCallback(CallbackFunction callback) {
      jsCallback = callback;
    }

    public CallbackFunction getJsCallback() {
      return jsCallback;
    }

    public abstract JSONObject execute(JSONObject jsonObject);
  }

  /**
   * java 调用H5的方法(兼容老容器)
   *
   * @param methodName 调用H5的方法名
   * @param json 传入的json参数 如果json==null表示调用h5的js方法不包含参数
   * @author liuwei
   */
  public void callH5Method(String methodName, String json) {
    String jsUrl;
    if (json == null) {
      jsUrl = "javascript:" + methodName + "()";
    } else {
      jsUrl = "javascript:" + methodName + "(" + json + ")";
    }
    mJavascriptBridge.executeJs(jsUrl);
  }


  /**
   * 尝试查找和注册Handler同等功能的FusionModule方法
   */
  private Method tryFindEqualMethod(String handlerName) {
//    ExportNamespace exportModule = mJavascriptBridge.getExportModule(EXPROTNAME);
//    return exportModule.getTargetMethod(handlerName);
    return null;
  }


  @JsInterface("getLocationInfo")
  public JSONObject getLocationInfo(JSONObject param, CallbackFunction callback) {

    JSONObject locationInfo = new JSONObject();
    try {
      String lng = "";
      String lat = "";
      String cid = "";
      String area = "";
      Address address = LocationProvider.getInstance().getLocation();
      if (address != null) {
        lng = address.mAdrLatLng.longitude + "";
        lat = address.mAdrLatLng.latitude + "";
      }
      locationInfo.put(P_LNG, lng);
      locationInfo.put(P_LAT, lat);
      locationInfo.put(P_CITY_ID, cid);
      locationInfo.put(P_AREA, area);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    if (callback != null) {
      callback.onCallBack(locationInfo);
    }
    return locationInfo;
  }

  @JsInterface("openPage")
  public void openPage(JSONObject params, CallbackFunction callback) {
    final String target = params.optString("target", "");
    final String url = params.optString("url", "");
    switch (target) {
      case "self":
        mFusionWebView.loadUrl(url);
        break;
      case "native":
        Intent nativeIntent = new Intent(Intent.ACTION_VIEW);
        nativeIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        nativeIntent.setData(Uri.parse(url));
        mContext.startActivity(nativeIntent);
        break;
      case "order":
        Uri uri = Uri.parse(url);
        final String orderId = uri.getQueryParameter("orderId");
        final String businessId = uri.getQueryParameter("sid");
        if (TextUtils.isEmpty(businessId)) {
//                    ToastHelper.showShortError(mContext, R.string.history_record_item_no_product);
        } else {
          Intent requestIntent = new Intent();
          requestIntent.setAction("com.one.trip.ON_THE_WAY");
          requestIntent.setData(Uri.parse("one://" + businessId + "/ontheway"));
          requestIntent.putExtra("orderId", orderId);
          LocalBroadcastManager.getInstance(mContext).sendBroadcast(requestIntent);

          //解决行程中界面被覆盖的问题
          Intent startMainIntent = new Intent(mContext, MainActivity.class);
          startMainIntent.setData(Uri.parse("OneTravel://" + businessId));
          mContext.startActivity(startMainIntent);
        }
        break;
      default:
        String title = params.optString("title");
        WebViewModel model = new WebViewModel();
        model.url = url;
        model.title = title;
        Intent fusionIntent = new Intent(mContext, WebActivity.class);
        fusionIntent.putExtra(WebActivity.KEY_WEB_VIEW_MODEL, model);
        mContext.startActivity(fusionIntent);
    }
    if (callback != null) {
      callback.onCallBack(new JSONObject());
    }
  }

  @JsInterface({"closePage", "web_page_close", "page_close"})
  public void closeContainerActivity(JSONObject param, CallbackFunction callbackFunction) {
    mContext.finish();
    if (callbackFunction != null) {
      callbackFunction.onCallBack(new JSONObject());
    }
  }


  @JsInterface({"refreshPage", "page_refresh"})
  public void refreshPage(JSONObject param, CallbackFunction callback) {
    if (mFusionWebView != null) {
      mFusionWebView.reload();
    }
    if (callback != null) {
      callback.onCallBack(new JSONObject());
    }
  }

  private ImageHelper mImageHelper;

  @JsInterface("resizeImage")
  public void resizeImage(JSONObject jsonObject, final CallbackFunction callback) {
    final WebActivity mWebActivity;
    if (mContext instanceof WebActivity) {
      mWebActivity = (WebActivity) mContext;
    } else {
      throw new RuntimeException("Can't be invoked in any activity except WebActivity");
    }

    if (jsonObject != null) {
      int width = jsonObject.optInt("width");
      int height = jsonObject.optInt("height");
      int quality = jsonObject.optInt("quality");

      mImageHelper = new ImageHelper(mContext);
      mImageHelper.handleImageChoose(width, height, quality,
          new ImageHelper.IImg2StrListener() {

            @Override
            public void onResult(String result) {
              if (!TextUtils.isEmpty(result)) {
                String url = "javascript:resultBackFromJava('" + result + "')";
                if (mWebActivity.getWebView() != null) {
                  mWebActivity.getWebView().loadUrl(url);
                }
              }
              if (callback != null) {
                callback.onCallBack(new JSONObject());
              }
            }
          });
    }
  }

  public void handleChooseImageResult(int requestCode, int resultCode, Intent data) {
    if (mImageHelper != null) {
      mImageHelper.handleActivityResult(requestCode, resultCode, data);
    }
  }

  @JsInterface("openNativeWebPage")
  public void openNativeWebPage(JSONObject params, CallbackFunction callbackFunction) {
    if (params != null) {
      String url = params.optString("url");
      String title = params.optString("title");
      WebViewModel model = new WebViewModel();
      model.url = url;
      model.title = title;
      Intent intent = new Intent(mContext, WebActivity.class);
      if (!(mContext instanceof Activity)) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      }
      intent.putExtra(WebActivity.KEY_WEB_VIEW_MODEL, model);
      mContext.startActivity(intent);
      callbackFunction.onCallBack(new JSONObject());
    }
  }

  @JsInterface("callbackImageLiteratureReview")
  public void callbackImageLiteratureReview(JSONObject params,
      final CallbackFunction callbackFunction) {
    if (params != null) {
      final String callback = params.optString("callback");
      if (TextUtils.isEmpty(callback)) {
        //由于历史原因，ofo使用该分支
        mImageHelper = new ImageHelper(mContext);
        mImageHelper.handleImageChoose(new ImageHelper.IImg2StrListener() {
          @Override
          public void onResult(String result) {
            if (!TextUtils.isEmpty(result) && callbackFunction != null) {
              JSONObject imageJson = new JSONObject();
              try {
                imageJson.put("data", result);
              } catch (JSONException e) {
                e.printStackTrace();
              }
              callbackFunction.onCallBack(imageJson);
            }
          }
        });
      } else {
        Intent intent = new Intent(mContext, PicUploadActivity.class);
        intent.putExtra(PicUploadActivity.UPLOAD_URL_KEY, params.optString("url"));
        intent.putExtra(PicUploadActivity.DATA_PARAMS_KEY, params.toString());
        intent.putExtra("width", params.optString("outputWidth"));
        intent.putExtra("height", params.optString("outputHeight"));
        intent.putExtra("type", "");

        PicUploadActivity.setImageUploadCallback(new ImageUploadCallback() {
          @Override
          public void onSuccess(String msg) {
            String finalMsg = "{\"data\":" + msg + "}";
            mFusionWebView.loadUrl("javascript:" + callback + "('" + finalMsg + "');");
          }
        });
        mContext.startActivity(intent);
        callbackFunction.onCallBack(new JSONObject());
      }

    }
  }

  @JsInterface("callbackImageLiteratureReviewTakeCamera")
  public void callbackImageLiteratureReviewTakeCamera(JSONObject params,
      CallbackFunction callbackFunction) {
    if (params != null) {
      final String callback = params.optString("callback");
      Intent intent = new Intent(mContext, PicUploadActivity.class);
      intent.putExtra(PicUploadActivity.UPLOAD_URL_KEY, params.optString("url"));
      intent.putExtra(PicUploadActivity.DATA_PARAMS_KEY, params.toString());
      intent.putExtra("width", params.optString("outputWidth"));
      intent.putExtra("height", params.optString("outputHeight"));
      intent.putExtra("type", "camera");

      PicUploadActivity.setImageUploadCallback(new ImageUploadCallback() {
        @Override
        public void onSuccess(String msg) {
          String finalMsg = "{\"data\":" + msg + "}";
          mFusionWebView.loadUrl("javascript:" + callback + "('" + finalMsg + "');");
        }
      });
      mContext.startActivity(intent);
      callbackFunction.onCallBack(new JSONObject());
    }
  }

  @JsInterface("callbackImageLiteratureReviewPhotoLibrary")
  public void callbackImageLiteratureReviewPhotoLibrary(JSONObject params,
      CallbackFunction callbackFunction) {
    if (params != null) {
      final String callback = params.optString("callback");
      Intent intent = new Intent(mContext, PicUploadActivity.class);
      intent.putExtra(PicUploadActivity.UPLOAD_URL_KEY, params.optString("url"));
      intent.putExtra(PicUploadActivity.DATA_PARAMS_KEY, params.toString());
      intent.putExtra("width", params.optString("outputWidth"));
      intent.putExtra("height", params.optString("outputHeight"));
      intent.putExtra("type", "photo");

      PicUploadActivity.setImageUploadCallback(new ImageUploadCallback() {
        @Override
        public void onSuccess(String msg) {
          String finalMsg = "{\"data\":" + msg + "}";
          mFusionWebView.loadUrl("javascript:" + callback + "('" + finalMsg + "');");
        }
      });
      mContext.startActivity(intent);
      callbackFunction.onCallBack(new JSONObject());
    }
  }


  @JsInterface("photograph")
  public void photograph(JSONObject params, final CallbackFunction callbackFunction) {
    final String TAG = "FuncPhotograph";

    Intent intent = new Intent(mContext, PicUploadActivity.class);
    intent.putExtra("width", params.optString("width"));
    intent.putExtra("height", params.optString("height"));
    intent.putExtra("cut", params.optBoolean("cut"));

    String type = params.optString("type");
    String quality = params.optString("quality");

    if ("photograph".equals(type)) {
      type = "camera";
    } else if ("album".equals(type)) {
      type = "photo";
    } else if ("choice".equals(type)) {
      type = "";
    }

    intent.putExtra("type", type);
    intent.putExtra("quality", quality);

    PicUploadActivity.setImageUploadCallback(new ImageUploadCallback() {
      @Override
      public void onSuccess(String msg) {
        JSONObject result = new JSONObject();
        try {
          result.put("photograph_result", 0);
          result.put("image", msg);
          callbackFunction.onCallBack(result);
        } catch (JSONException e) {
          Log.d(TAG, e.toString());
        }

      }
    });

    PicUploadActivity.setImageCallback(new ImageCallback() {
      @Override
      public void onSuccess(String image) {
        JSONObject result = new JSONObject();
        try {
          result.put("photograph_result", 0);
          result.put("image", image);
          callbackFunction.onCallBack(result);
        } catch (JSONException e) {
          Log.d(TAG, e.toString());
        }
      }

      @Override
      public void onSuccess(String image, String imageType) {
        JSONObject result = new JSONObject();
        try {
          result.put("photograph_result", 0);
          result.put("image", image);
          result.put("type", imageType);
          callbackFunction.onCallBack(result);
        } catch (JSONException e) {
          Log.d(TAG, e.toString());
        }
      }

      @Override
      public void onFail() {
        JSONObject result = new JSONObject();
        try {
          result.put("photograph_result", 1);
          result.put("image", "");
          result.put("type", "");
          callbackFunction.onCallBack(result);
        } catch (JSONException e) {
          Log.d(TAG, e.toString());
        }
      }

      @Override
      public void onCancel() {
        JSONObject result = new JSONObject();
        try {
          result.put("photograph_result", 2);
          result.put("image", "");
          result.put("type", "");
          callbackFunction.onCallBack(result);
        } catch (JSONException e) {
          Log.d(TAG, e.toString());
        }
      }

      @Override
      public void onPermissionFail() {
        JSONObject result = new JSONObject();
        try {
          result.put("photograph_result", -1);
          result.put("image", "");
          result.put("type", "");
          callbackFunction.onCallBack(result);
        } catch (JSONException e) {
          Log.d(TAG, e.toString());
        }
      }
    });
    mContext.startActivity(intent);

  }

  @JsInterface("toBizEntranceFromCoupon")
  public void toBizEntranceFromCoupon(JSONObject params, final CallbackFunction callbackFunction) {
    // one://dache/entrance
    String uri = params.optString("navi_url", "");
    String biz = "Taxi";
    if (!TextUtils.isEmpty(uri)) {
      biz = Uri.parse(uri).getHost();
    }

    final String host = biz;

    // 退回首页并且切换业务线
    UIThreadHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        Intent switchIntent = new Intent(mContext, MainActivity.class);
        switchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        switchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(switchIntent);
      }
    }, 200);

    UIThreadHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        Intent switchIntent = new Intent();
        switchIntent.setData(Uri.parse("OneReceiver://" + host + "/entrance"));
        switchIntent.setAction("com.xiaojukeji.action.SWITCH_CONTEXT");
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(switchIntent);
      }
    }, 300);
  }


  private <T> T loadExportModule(Class<T> clz) {
    return (T) mFusionWebView.getExportModuleInstance(clz);
  }
}
