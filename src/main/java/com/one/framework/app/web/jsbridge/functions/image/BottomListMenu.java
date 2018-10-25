package com.one.framework.app.web.jsbridge.functions.image;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.utils.UIUtils;

public class BottomListMenu {

  private OnDismissListener mDismissListener;

  public interface ListMenuListener {

    void onItemSelected(int position, String itemStr);
  }

  private Activity mContext;
  private View mParent;
  private PopupWindow mPopupWindow;

  private View mContentView;
  private TextView mCancelView;
  private ListView mListMenu;
  private ArrayAdapter<String> mListAdapter;

  private ListMenuListener mListMenuListener;

  public BottomListMenu(Activity context, View parentView, String[] strArray) {
    mContext = context;
    mParent = parentView;

    mContentView = View.inflate(mContext, R.layout.one_pic_bottom_list_menu, null);

    mCancelView = (TextView) mContentView.findViewById(R.id.cancel_text);
    mCancelView.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        onCancel();
      }
    });
    mListMenu = (ListView) mContentView.findViewById(R.id.menu_list);
    mListAdapter = new ArrayAdapter<String>(mContext, R.layout.one_bottom_list_menu_item, strArray);
    mListMenu.setAdapter(mListAdapter);
    mListMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view,
          int position, long id) {
        dismiss();
        if (mListMenuListener != null) {
          mListMenuListener.onItemSelected(position, mListAdapter.getItem(position));
        }
      }
    });

    mPopupWindow = newSelectPopupWindow(mContentView);
  }

  private PopupWindow newSelectPopupWindow(View view) {
    PopupWindow popupWindow = new PopupWindow(view, UIUtils.getScreenWidth(view.getContext()),
        ActionBar.LayoutParams.WRAP_CONTENT, true);
    popupWindow.setFocusable(true);
    popupWindow.setBackgroundDrawable(new BitmapDrawable());
    popupWindow.setOutsideTouchable(true);
    popupWindow.update();

    return popupWindow;
  }

  public void dismiss() {
    if (mPopupWindow != null && mPopupWindow.isShowing()) {
      mPopupWindow.dismiss();
    }
  }

  public void showDialog() {
    if (mPopupWindow != null && !mPopupWindow.isShowing()) {
      mPopupWindow.showAtLocation(mParent, Gravity.BOTTOM, 0, 0);
    }
  }

  public void setListMenuListener(ListMenuListener listener) {
    mListMenuListener = listener;
  }

  public void onCancel() {
    dismiss();
    if (mDismissListener != null) {
      mDismissListener.dismiss();
    }
  }

  public interface OnDismissListener {

    public void dismiss();
  }

  public void setDismissListener(OnDismissListener listener) {
    mDismissListener = listener;
  }
}

