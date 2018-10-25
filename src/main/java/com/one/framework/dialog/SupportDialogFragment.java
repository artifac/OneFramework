package com.one.framework.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.log.Logger;
import java.util.ArrayList;
import java.util.List;

public class SupportDialogFragment extends DialogFragment implements OnClickListener {

  private TextView dialogTitle;
  private TextView dialogMsg;
  private TextView dialogConfirm;
  private TextView dialogCancel;
  private TextView noTitleMsg;
  private View lineView;

  private View layout;

  private LinearLayout confirmLinearLayout;
  private LinearLayout cancelLayout;

  private OnDismissListener mOnDismissListener;
  private OnCancelListener mOnCancelListener;
  private LinearLayout mItemLayout;

  private ImageView mClose;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    Dialog dialog = getDialog();
    if (dialog == null) {
      return null;
    }
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
    return layout;
  }

  public static SupportDialogFragment instance(Context context) {
    SupportDialogFragment fragment = new SupportDialogFragment();
    fragment.init(context);
    return fragment;
  }

  public static SupportDialogFragment instance(View contentView) {
    SupportDialogFragment fragment = new SupportDialogFragment();
    fragment.layout = contentView;
    return fragment;
  }

  private void init(Context context) {
    LayoutInflater inflater = LayoutInflater.from(context);
    layout = inflater.inflate(R.layout.one_support_dialog_layout, null);
    initView(layout);
  }

  private void initView(View view) {
    dialogTitle = (TextView) view.findViewById(R.id.one_support_dialog_title);
    dialogMsg = (TextView) view.findViewById(R.id.one_support_dialog_message);
    dialogConfirm = (TextView) view.findViewById(R.id.one_support_dialog_confirm);
    dialogCancel = (TextView) view.findViewById(R.id.one_support_dialog_cancel);
    lineView = view.findViewById(R.id.one_support_dialog_line);
    confirmLinearLayout = (LinearLayout) view.findViewById(R.id.one_support_dialog_confirm_layout);
    cancelLayout = (LinearLayout) view.findViewById(R.id.one_support_dialog_cancel_layout);
    noTitleMsg = (TextView) view.findViewById(R.id.one_support_dialog_message_no_title);
    mItemLayout = (LinearLayout) view.findViewById(R.id.one_support_dialog_item_layout);
    mClose = view.findViewById(R.id.one_support_dialog_close);
    mClose.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.one_support_dialog_close) {
      dismiss();
    }
  }

  public void setDialogTitle(String title) {
    if (dialogTitle == null) {
      return;
    }
    if (!TextUtils.isEmpty(title)) {
      dialogTitle.setVisibility(View.VISIBLE);
      dialogTitle.setText(title);
    } else {
      dialogTitle.setVisibility(View.GONE);
    }
  }

  public void setDialogMsg(String msg, String title) {
    if (dialogMsg == null) {
      return;
    }
    if (TextUtils.isEmpty(title)) {
      dialogTitle.setVisibility(View.GONE);
      dialogMsg.setVisibility(View.GONE);
      if (!TextUtils.isEmpty(msg)) {
        noTitleMsg.setVisibility(View.VISIBLE);
        noTitleMsg.setText(msg);
      } else {
        noTitleMsg.setVisibility(View.GONE);
      }

    } else {
      dialogTitle.setVisibility(View.VISIBLE);
      noTitleMsg.setVisibility(View.GONE);
      if (!TextUtils.isEmpty(msg)) {
        dialogMsg.setVisibility(View.VISIBLE);
        dialogMsg.setText(msg);
      } else {
        dialogMsg.setVisibility(View.GONE);
      }
    }
  }

  public void setDialogConfirm(String confirm) {
    if (confirmLinearLayout == null
        || dialogConfirm == null) {
      return;
    }
    if (!TextUtils.isEmpty(confirm)) {
      confirmLinearLayout.setVisibility(View.VISIBLE);
      dialogConfirm.setText(confirm);
    } else {
      confirmLinearLayout.setVisibility(View.GONE);
    }
  }

  public void setDialogCancel(String cancel) {
    if (cancelLayout == null
        || dialogCancel == null) {
      return;
    }
    if (!TextUtils.isEmpty(cancel)) {
      cancelLayout.setVisibility(View.VISIBLE);
      dialogCancel.setText(cancel);
    } else {
      cancelLayout.setVisibility(View.GONE);
    }
  }

  public void updateLineView(String confirm, String cancel) {
    if (lineView == null) {
      return;
    }
    if (!TextUtils.isEmpty(confirm) && !TextUtils.isEmpty(cancel)) {
      lineView.setVisibility(View.VISIBLE);
    } else {
      lineView.setVisibility(View.GONE);
    }
  }

  private void updateSheetItemLayout(Context context, List<SheetItem> list) {
    if (list != null && list.size() > 0) {
      mItemLayout.removeAllViews();
      mItemLayout.setVisibility(View.VISIBLE);
      for (final SheetItem item : list) {
        if (item != null) {
          final TextView tv = new TextView(context);
          tv.setText(item.text);
          tv.setGravity(Gravity.CENTER);
          tv.setBackgroundResource(R.drawable.one_support_dialog_sheet_item_selector);
          tv.setTextColor(context.getResources().getColor(R.color.one_support_dialog_bg_text));
          tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
          tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
              context.getResources().getDimensionPixelSize(R.dimen.one_support_dialog_sheet_item_height)));
          tv.setOnClickListener(
              new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  dismiss();
                  if (item.clickListener != null) {
                    item.clickListener.onClick(tv);
                  }
                }
              });

          mItemLayout.addView(tv);
          View view = new View(context);
          view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
          view.setBackgroundColor(context.getResources().getColor(R.color.one_support_dialog_bg_div));
          mItemLayout.addView(view);
        }
      }
    } else {
      mItemLayout.removeAllViews();
      mItemLayout.setVisibility(View.GONE);
    }

  }

  public void setPositiveTextColor(int color) {
    if (color != -1 && dialogConfirm != null) {
      dialogConfirm.setTextColor(color);
    }
  }

  public void setPositiveBackground(int bgRes) {
    if (bgRes != -1) {
      confirmLinearLayout.setBackgroundResource(bgRes);
    }
  }

  public void setBottomCloseVisible(boolean visible) {
    mClose.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
  }

  public void setPositiveBgMargin(int left, int top, int right, int bottom) {
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) confirmLinearLayout.getLayoutParams();
    params.leftMargin = left;
    params.topMargin = top;
    params.rightMargin = right;
    params.bottomMargin = bottom;
    confirmLinearLayout.setLayoutParams(params);
  }

  public void setNegativeTextColor(int color) {

    if (color != -1 && dialogCancel != null) {
      dialogCancel.setTextColor(color);
    }

  }

  public void setPositiveClickListener(final View.OnClickListener listener) {
    if (confirmLinearLayout != null) {
      confirmLinearLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dismiss();
          if (listener != null) {
            listener.onClick(v);
          }

        }
      });

    }

  }

  public void setNegativeClickListener(final View.OnClickListener listener) {
    if (cancelLayout != null) {
      cancelLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dismiss();
          if (listener != null) {
            listener.onClick(v);
          }
        }
      });

    }
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

    public Builder setCancelable(boolean cancelable) {
      params.cancelable = cancelable;
      return this;
    }

    public Builder setTitle(String title) {
      params.mTitle = title;
      return this;
    }

    public Builder setMessage(String message) {
      params.mMessage = message;
      return this;
    }

    public Builder setOnDismissListener(OnDismissListener onDismissListener) {
      params.mOnDismissListener = onDismissListener;
      return this;
    }

    public Builder setOnCancelListener(OnCancelListener onCancelListener) {
      params.mOnCancelListener = onCancelListener;
      return this;
    }

    public Builder setPositiveButton(String text, View.OnClickListener listener) {
      params.mPositiveButtonText = text;
      params.mPositiveButtonListener = listener;
      return this;
    }

    public Builder addSheetItem(String text, View.OnClickListener listener) {
      if (!TextUtils.isEmpty(text)) {
        params.mSheetList.add(new SheetItem(text, listener));
      }
      return this;
    }

    public Builder setPositiveButton(String text) {
      params.mPositiveButtonText = text;
      return this;
    }

    public Builder setNegativeButton(String text, View.OnClickListener listener) {
      params.mNegativeButtonText = text;
      params.mNegativeButtonListener = listener;
      return this;
    }

    public Builder setNegativeButton(String text) {
      params.mNegativeButtonText = text;
      return this;
    }

    public Builder setPositiveButtonTextColor(int color) {
      params.positiveButtonTextColor = color;
      return this;
    }

    public Builder setPositiveButtonBackground(int bgRes) {
      params.positiveButtonBackground = bgRes;
      return this;
    }

    public Builder setVisibleClose(boolean visible) {
      params.bottomCloseVisible = visible;
      return this;
    }

    public Builder setPositiveBackgroundMargin(int left, int top, int right, int bottom) {
      params.positiveLeftMargin = left;
      params.positiveTopMargin = top;
      params.positiveRightMargin = right;
      params.positiveBottomMargin = bottom;
      return this;
    }

    public Builder setNegativeTextColor(int color) {
      params.negativeButtonTextColor = color;
      return this;
    }

    public Builder setNegativeText(String text) {
      params.mNegativeButtonText = text;
      return this;
    }

    public SupportDialogFragment create() {
      SupportDialogFragment dialog = SupportDialogFragment.instance(params.mContext);
      params.doRender(dialog);
      dialog.setDismissListener(params.mOnDismissListener);
      dialog.setOnCancelListener(params.mOnCancelListener);
      return dialog;
    }

  }

  public void setDismissListener(OnDismissListener onDismissListener) {
    this.mOnDismissListener = onDismissListener;
  }

  public void setOnCancelListener(OnCancelListener onCancelListener) {
    this.mOnCancelListener = onCancelListener;
  }


  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    removeContentViewFromParent();
    if (mOnDismissListener != null) {
      mOnDismissListener.onDismiss(this);
    }

  }

  @Override
  public void onCancel(DialogInterface dialog) {
    super.onCancel(dialog);
    if (mOnCancelListener != null) {
      mOnCancelListener.onCancel(this);
    }
  }

  @Override
  public void dismissAllowingStateLoss() {
    if (getFragmentManager() == null) {
      return;
    }
    super.dismissAllowingStateLoss();
  }

  @Override
  public void show(FragmentManager manager, String tag) {
    try {
      super.show(manager, tag);
    } catch (Exception e) {
      e.printStackTrace();
      Logger.e("ldx", "showDlgFragment Exception");
    }
  }

  @Override
  public int show(FragmentTransaction transaction, String tag) {
    int show = 0;
    try {
      show = super.show(transaction, tag);
    } catch (Exception e) {
    }
    return show;
  }

  public void showAllowingStateLoss(FragmentTransaction transaction, String tag) {
    try {
      transaction.add(this, tag);
      transaction.commitAllowingStateLoss();//注意这里使用commitAllowingStateLoss()
    } catch (Exception e) {
      Logger.e("ldx", "showAllowingStateLoss Exception");
    }

  }

  @Override
  public void dismiss() {
    if (getFragmentManager() == null) {
      return;
    }

    try {
      super.dismiss();
    } catch (Exception ignore) {
    }
  }

  private void removeContentViewFromParent() {
    if (getView() != null) {
      ViewParent parent = getView().getParent();
      if (parent != null) {
        if (parent instanceof ViewGroup) {
          ((ViewGroup) parent).removeView(getView());
        }
      }
    }
  }


  public interface OnCancelListener {

    void onCancel(SupportDialogFragment var1);
  }

  public interface OnDismissListener {

    void onDismiss(SupportDialogFragment var1);
  }


  public static class DialogParams {

    public Context mContext;

    public View view;

    public boolean cancelable;
    public String mTitle;
    public String mMessage;
    public String mPositiveButtonText;
    public String mNegativeButtonText;

    public int positiveButtonTextColor = -1;
    public int negativeButtonTextColor = -1;
    public int positiveButtonBackground = -1;

    public boolean bottomCloseVisible = false;

    public int positiveLeftMargin;
    public int positiveTopMargin;
    public int positiveRightMargin;
    public int positiveBottomMargin;

    public OnDismissListener mOnDismissListener;
    public OnCancelListener mOnCancelListener;

    public View.OnClickListener mPositiveButtonListener;

    public View.OnClickListener mNegativeButtonListener;

    public List<SheetItem> mSheetList = new ArrayList<>();

    public DialogParams(Context context) {
      this.mContext = context;
    }

    public void doRender(SupportDialogFragment fragment) {

      fragment.setCancelable(cancelable);
      fragment.setDialogTitle(mTitle);
      fragment.setDialogCancel(mNegativeButtonText);
      fragment.setDialogConfirm(mPositiveButtonText);
      fragment.updateSheetItemLayout(mContext, mSheetList);
      fragment.updateLineView(mPositiveButtonText, mNegativeButtonText);
      fragment.setDialogMsg(mMessage, mTitle);
      fragment.setNegativeTextColor(negativeButtonTextColor);
      fragment.setPositiveTextColor(positiveButtonTextColor);
      fragment.setPositiveClickListener(mPositiveButtonListener);
      fragment.setNegativeClickListener(mNegativeButtonListener);
      fragment.setPositiveBackground(positiveButtonBackground);
      fragment.setPositiveBgMargin(positiveLeftMargin, positiveTopMargin, positiveRightMargin, positiveBottomMargin);
      fragment.setBottomCloseVisible(bottomCloseVisible);
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


}
