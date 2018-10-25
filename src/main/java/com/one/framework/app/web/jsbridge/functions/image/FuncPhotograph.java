package com.one.framework.app.web.jsbridge.functions.image;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.one.framework.app.web.BaseWebView;
import com.one.framework.app.web.jsbridge.JavascriptBridge;
import java.lang.ref.WeakReference;
import org.json.JSONException;
import org.json.JSONObject;

public class FuncPhotograph extends JavascriptBridge.Function {

  public static final String TAG = "FuncPhotograph";

  private WeakReference<BaseWebView> webVieReference;

  public FuncPhotograph(WeakReference<BaseWebView> webVieReference) {
    this.webVieReference = webVieReference;
    setAutoCallbackJs(false);
  }

  @Override
  public JSONObject execute(JSONObject jsonObject) {
    final BaseWebView webView = webVieReference != null ? webVieReference.get() : null;
    final Context context = webView != null ? webView.getContext() : null;
    if (jsonObject == null || webView == null || context == null) {
      if (context != null) {
      }
      return null;
    }

    Intent intent = new Intent(context, PicUploadActivity.class);
    intent.putExtra("width", jsonObject.optString("width"));
    intent.putExtra("height", jsonObject.optString("height"));
    intent.putExtra("cut", jsonObject.optBoolean("cut"));

    String type = jsonObject.optString("type");
    String quality = jsonObject.optString("quality");

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
          getJsCallback().apply(result);
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
          getJsCallback().apply(result);
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
          getJsCallback().apply(result);
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
          getJsCallback().apply(result);
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
          getJsCallback().apply(result);
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
          getJsCallback().apply(result);
        } catch (JSONException e) {
          Log.d(TAG, e.toString());
        }
      }
    });
    context.startActivity(intent);
    return null;
  }
}
