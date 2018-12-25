package com.one.framework.app.web;

import android.webkit.WebView;

public interface OverrideUrlLoader {
  boolean shouldOverrideUrlLoading(WebView view, String url);
}
