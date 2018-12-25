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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.app.widget.TripButton;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/12.
 */

public class BottomSheetDialog extends Dialog {

  private View mSubView;
  private View.OnClickListener mNegativeClickListener;
  private String mPositiveTxt;
  private int mPositiveColor;
  private String mDlgTitle;
  private View.OnClickListener mPositiveClickListener;
  protected ISelectResultListener mListener;

  protected FrameLayout mContentGroup;
  private TextView mBottomSheetTitle;
  protected TripButton mConfirm;

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

    initView();
  }

  public int getGravity() {
    return Gravity.BOTTOM;
  }

  private void initView() {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.one_bottom_dialog_layout, null);
    mContentGroup = (FrameLayout) view.findViewById(R.id.one_bottom_dlg_view_group);
    mBottomSheetTitle = (TextView) view.findViewById(R.id.one_bottom_dlg_title);
    ImageView cancel = (ImageView) view.findViewById(R.id.one_bottom_dlg_cancel);
    mConfirm = (TripButton) view.findViewById(R.id.one_bottom_dlg_confirm);
    mConfirm.setTripButtonText(TextUtils.isEmpty(mPositiveTxt) ? getContext().getString(R.string.one_confirm) : mPositiveTxt);
    mConfirm.setTripButtonTextColor(mPositiveColor == 0 ? Color.WHITE : mPositiveColor);
    mBottomSheetTitle.setText(mDlgTitle);
    if (mNegativeClickListener != null) {
      cancel.setOnClickListener(mNegativeClickListener);
    } else {
      cancel.setOnClickListener(v -> dismiss());
    }
    mConfirm.setOnClickListener(v -> {
      dismiss();
      if (mPositiveClickListener != null) {
        mPositiveClickListener.onClick(v);
      }
    });
    if (mSubView != null) {
      mContentGroup.addView(mSubView);
    }
    setContentView(view);
  }

  public BottomSheetDialog setBottomSheetTitle(String title) {
    mBottomSheetTitle.setText(title);
    return this;
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

    public Builder setDialogTitle(String text) {
      params.title = text;
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

    public Builder setPositiveButtonTextColor(int color) {
      params.positiveBtnColor = color;
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
    String title;
    int positiveBtnColor;
    String positiveBtnTxt;
    View.OnClickListener positiveClickListener;
    View.OnClickListener negativeClickListener;

    public DialogParams(Context context) {
      this.context = context;
    }

    void doRender(BottomSheetDialog dialog) {
      dialog.mSubView = view;
      dialog.mPositiveColor = positiveBtnColor;
      dialog.mPositiveTxt = positiveBtnTxt;
      dialog.mDlgTitle = title;
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
