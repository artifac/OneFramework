package com.one.framework.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.one.framework.R;

public class DialogLoading extends Dialog {

  private LayoutInflater mInflater;
  private ImageView mDialogImg;
  private TextView mDialogTxt;
  private AnimationDrawable mAnimation;

  DialogLoading(Context context) {
    this(context, R.style.LoadingDialogTheme);
  }

  DialogLoading(Context context, int theme) {
    super(context, theme);
    mInflater = LayoutInflater.from(context);
    setViewLayout(R.layout.one_dialog_loading_layout);

    Window window = getWindow();
    if (window != null) {
      WindowManager.LayoutParams params = window.getAttributes();
      params.gravity = Gravity.CENTER;
      params.dimAmount = .7f;
      params.width = context.getResources().getDimensionPixelOffset(R.dimen.one_dialog_loading_wh);
      params.height = context.getResources().getDimensionPixelSize(R.dimen.one_dialog_loading_wh);
      window.setAttributes(params);
    }
    setCancelable(false);
  }

  public void setViewLayout(int layout) {
    View view = mInflater.inflate(layout, null);
    mDialogImg = (ImageView) view.findViewById(R.id.dialog_loading_img);
    mDialogTxt = (TextView) view.findViewById(R.id.dialog_loading_txt);

    setContentView(view);
    setCanceledOnTouchOutside(false);
    mAnimation = (AnimationDrawable) mDialogImg.getBackground();
    if (mAnimation != null && mAnimation.isRunning()) {
      mAnimation.stop();
    }

    if (mAnimation != null) {
      mAnimation.start();
    }
  }

  public void setDialogTxt(String txt) {
    mDialogTxt.setVisibility(View.VISIBLE);
    mDialogTxt.setText(txt);
  }

  public void setDialogTxt(int resId) {
    mDialogTxt.setVisibility(View.VISIBLE);
    mDialogTxt.setText(resId);
  }

  public void setDialogImg(int resId) {
    mDialogImg.setBackgroundResource(resId);
  }

  public void showDlg() {
    try {
      if (mAnimation != null && !mAnimation.isRunning()) {
        mAnimation.start();
      }
    } catch (Exception e) {

    }
    show();
  }

  /**
   * 隐藏动画
   */
  public void dismissDlg() {
    try {
      if (mAnimation != null && mAnimation.isRunning()) {
        mAnimation.stop();
      }
      dismiss();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static class Builder {

    private DialogParams params;

    public Builder(Context context) {
      params = new DialogParams(context);
    }

    public Builder setDlgInfo(String info) {
      params.info = info;
      return this;
    }

    public Builder setDlgInfo(@StringRes int resId) {
      params.infoResId = resId;
      return this;
    }

    public DialogLoading create() {
      DialogLoading loading = new DialogLoading(params.context);
      params.doRender(loading);
      return loading;
    }
  }

  static class DialogParams {

    Context context;
    String info;
    int infoResId;

    DialogParams(Context context) {
      this.context = context;
    }

    void doRender(DialogLoading loading) {
      if (infoResId != 0) {
        loading.setDialogTxt(infoResId);
      }
      if (!TextUtils.isEmpty(info)) {
        loading.setDialogTxt(info);
      }
    }
  }
}
