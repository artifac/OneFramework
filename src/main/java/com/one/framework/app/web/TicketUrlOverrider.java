package com.one.framework.app.web;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.WebView;

public class TicketUrlOverrider implements OverrideUrlLoader {

  private WebActivity mWebActivity;

  public TicketUrlOverrider(WebActivity webActivity) {
    mWebActivity = webActivity;
  }

  @Override
  public boolean shouldOverrideUrlLoading(WebView view, String url) {
    final String TICKET_RESULT_SCHEMA = "dcq:";
    final String TICKET_ID = "dcq_id";
    if (url.startsWith(TICKET_RESULT_SCHEMA)) {

      if (url.contains(TICKET_ID)) {
        String ticketId = url
            .substring(url.indexOf(TICKET_ID) + TICKET_ID.length() + 1, url.length());
        if (!TextUtils.isEmpty(ticketId)) {
          finishWithResult(ticketId);
        } else {
          boolean canGoBack = mWebActivity.goBack(false);
          if (!canGoBack) {
            finishWithResult("");
          }
        }
      } else {
        mWebActivity.finishWithResultCodeCanceled();
      }

      return true;
    }

    return false;
  }


  private void finishWithResult(String ticketId) {
    final String KEY_TAXI_TICKET = "taxi_ticket";
    Intent intent = new Intent();
    intent.putExtra(KEY_TAXI_TICKET, ticketId);
    mWebActivity.setResult(Activity.RESULT_OK, intent);
    mWebActivity.finish();
  }
}
