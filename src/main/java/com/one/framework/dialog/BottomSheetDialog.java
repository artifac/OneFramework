package com.one.framework.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.one.framework.R;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/12.
 */

public class BottomSheetDialog extends Dialog {

  private View mSubView;
  private String mNegativeTxt;
  private View.OnClickListener mNegativeClickListener;
  private int mNegativeColor;
  private String mPositiveTxt;
  private int mPositiveColor;
  private View.OnClickListener mPositiveClickListener;
  protected ISelectResultListener mListener;

  public BottomSheetDialog(@NonNull Context context) {
    this(context, R.style.ActionSheetDialogStyle);
  }

  public BottomSheetDialog(@NonNull Context context, int themeResId) {
    super(context, themeResId);
    init();
  }

  protected void init() {
    //获取当前Activity所在的窗体
    Window dialogWindow = this.getWindow();
    //设置Dialog从窗体底部弹出
    dialogWindow.setGravity(getGravity());
    //获得窗体的属性
    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
    dialogWindow.getDecorView().setPadding(0, 0, 0, 0); //消除边距

    lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
    lp.y = 0;//设置Dialog距离底部的距离
//       将属性设置给窗体
    dialogWindow.setAttributes(lp);
  }

  public int getGravity() {
    return Gravity.BOTTOM;
  }

  private void initView() {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.one_bottom_dialog_layout, null);
    FrameLayout group = (FrameLayout) view.findViewById(R.id.one_bottom_dlg_view_group);
    TextView cancel = (TextView) view.findViewById(R.id.one_bottom_dlg_cancel);
    TextView confirm = (TextView) view.findViewById(R.id.one_bottom_dlg_confirm);
    confirm.setText(TextUtils.isEmpty(mPositiveTxt) ? getContext().getString(R.string.one_confirm)
        : mPositiveTxt);
    confirm.setTextColor(mPositiveColor == 0 ? Color.parseColor("#f05b48") : mPositiveColor);
    cancel.setText(TextUtils.isEmpty(mNegativeTxt) ? getContext().getString(R.string.one_cancel)
        : mNegativeTxt);
    cancel.setTextColor(mNegativeColor == 0 ? Color.parseColor("#191d21") : mNegativeColor);
    if (mNegativeClickListener != null) {
      cancel.setOnClickListener(mNegativeClickListener);
    } else {
      cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dismiss();
        }
      });
    }
    confirm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
        if (mPositiveClickListener != null) {
          mPositiveClickListener.onClick(v);
        }
      }
    });
    group.addView(mSubView);

    setContentView(view);
  }

  @Override
  public void show() {
    super.show();
  }

  public static class Builder {

    DialogParams params;

    public Builder(Context context) {
      params = new DialogParams(context);
    }

    public Builder setContentView(View view) {
      params.view = view;
      return this;
    }

    public Builder setPositiveButton(String text, View.OnClickListener listener) {
      params.positiveBtnTxt = text;
      params.positiveClickListener = listener;
      return this;
    }

    public Builder addSheetItem(String text, View.OnClickListener listener) {
      if (!TextUtils.isEmpty(text)) {
        params.items.add(new SheetItem(text, listener));
      }
      return this;
    }

    public Builder setPositiveButton(String text) {
      params.positiveBtnTxt = text;
      return this;
    }

    public Builder setNegativeButton(String text, View.OnClickListener listener) {
      params.negativeBtnTxt = text;
      params.negativeClickListener = listener;
      return this;
    }

    public Builder setNegativeButton(String text) {
      params.negativeBtnTxt = text;
      return this;
    }

    public Builder setPositiveButtonTextColor(int color) {
      params.positiveBtnColor = color;
      return this;
    }

    public Builder setNegativeTextColor(int color) {
      params.negativeBtnColor = color;
      return this;
    }

    public Builder setNegativeText(String text) {
      params.negativeBtnTxt = text;
      return this;
    }

    public BottomSheetDialog create() {
      BottomSheetDialog dialog = new BottomSheetDialog(params.context);
      params.doRender(dialog);
      return dialog;
    }
  }

  public static class DialogParams {

    Context context;
    View view;
    List<SheetItem> items;
    int positiveBtnColor;
    String positiveBtnTxt;
    int negativeBtnColor;
    View.OnClickListener positiveClickListener;
    String negativeBtnTxt;
    View.OnClickListener negativeClickListener;

    public DialogParams(Context context) {
      this.context = context;
    }

    void doRender(BottomSheetDialog dialog) {
      dialog.mSubView = view;
      dialog.mNegativeColor = negativeBtnColor;
      dialog.mPositiveColor = positiveBtnColor;
      dialog.mPositiveTxt = positiveBtnTxt;
      dialog.mNegativeTxt = negativeBtnTxt;
      dialog.mNegativeClickListener = negativeClickListener;
      dialog.mPositiveClickListener = positiveClickListener;
      dialog.initView();
    }
  }

  public static class SheetItem {

    public String text;
    public View.OnClickListener clickListener;

    public SheetItem(@NonNull String text, View.OnClickListener clickListener) {
      this.text = text;
      this.clickListener = clickListener;
    }
  }

  public <T> T setSelectResultListener(ISelectResultListener listener) {
    mListener = listener;
    return (T) this;
  }

  public interface ISelectResultListener {
    void onTimeSelect(long time, String ...showTime);
  }
}
