package com.one.framework.app.web;

import android.webkit.WebView;

/**
 * @author xianchaohua
 */

public interface OverrideUrlLoader {
    boolean shouldOverrideUrlLoading(WebView view, String url);
}
