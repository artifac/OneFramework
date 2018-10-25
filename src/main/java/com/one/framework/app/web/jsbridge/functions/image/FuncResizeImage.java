package com.one.framework.app.web.jsbridge.functions.image;

import android.content.Intent;
import android.text.TextUtils;
import com.one.framework.app.web.WebActivity;
import com.one.framework.app.web.jsbridge.JavascriptBridge;
import org.json.JSONObject;

public class FuncResizeImage extends JavascriptBridge.Function {

  public static final int REQ_ALBUM_ACTIVITY = ImageHelper.REQ_ALBUM_ACTIVITY;
  public static final int REQ_CAMERA_ACTIVITY = ImageHelper.REQ_CAMERA_ACTIVITY;

  private WebActivity mWebActivity;
  private ImageHelper mImageHelper;

  public FuncResizeImage(WebActivity webActivity) {
    mWebActivity = webActivity;
  }

  @Override
  public JSONObject execute(JSONObject jsonObject) {

    if (jsonObject != null) {
      int width = jsonObject.optInt("width");
      int height = jsonObject.optInt("height");
      int quality = jsonObject.optInt("quality");

      mImageHelper = new ImageHelper(mWebActivity);
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
            }
          });
    }
    return null;
  }

  public void handleActivityResult(int requestCode, int resultCode, Intent data) {
    if (mImageHelper != null) {
      mImageHelper.handleActivityResult(requestCode, resultCode, data);
    }
  }

}
