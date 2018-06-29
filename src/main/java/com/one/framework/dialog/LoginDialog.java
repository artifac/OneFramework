package com.one.framework.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.one.framework.R;
import com.one.framework.utils.UIUtils;

/**
 * Created by ludexiang on 2018/6/15.
 */

public class LoginDialog extends Dialog {

  private int margin;

  public LoginDialog(@NonNull Context context) {
    this(context, R.style.ActionSheetDialogStyle);
  }

  public LoginDialog(@NonNull Context context, int themeResId) {
    super(context, themeResId);
    margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
        context.getResources().getDisplayMetrics());
    init();
  }

  protected final void init() {
    //获取当前Activity所在的窗体
    Window dialogWindow = this.getWindow();
    //设置Dialog从窗体底部弹出
    dialogWindow.setGravity(Gravity.CENTER);
    //获得窗体的属性
    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
    dialogWindow.getDecorView().setPadding(0, 0, 0, 0); //消除边距

    lp.width = UIUtils.getScreenWidth(getContext()) - margin * 2;   //设置宽度充满屏幕
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//       将属性设置给窗体
    dialogWindow.setAttributes(lp);
  }

  public static class Builder {

    LoginDialog.DialogParams params;

    public Builder(Context context) {
      params = new LoginDialog.DialogParams(context);
    }

    public LoginDialog.Builder setContentView(View view) {
      params.view = view;
      return this;
    }

    public Builder setOutsideHide(boolean isHide) {
      params.outSideHide = isHide;
      return this;
    }

    public LoginDialog create() {
      LoginDialog dialog = new LoginDialog(params.context);
      params.doRender(dialog);
      return dialog;
    }
  }

  public static class DialogParams {

    Context context;
    View view;
    boolean outSideHide;

    public DialogParams(Context context) {
      this.context = context;
    }

    private void doRender(LoginDialog dialog) {
      dialog.setCanceledOnTouchOutside(outSideHide);
      dialog.setContentView(view);
    }
  }
}
