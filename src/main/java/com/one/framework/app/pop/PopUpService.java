package com.one.framework.app.pop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.utils.UIUtils;
import java.util.List;

/**
 * Created by ludexiang on 2017/12/25.
 */

public class PopUpService {

  private LayoutInflater mInflater;
  private View mView;
  private Context mContext;
  private PopupWindow mPopwindow;
  private int fromYDelta;

  private PopUpService(Context context, @PopType int type) {
    mContext = context.getApplicationContext();
    mInflater = LayoutInflater.from(mContext);
    if (type == PopType.MATCH) {
      mView = mInflater.inflate(R.layout.one_pop_window_match, null);
      mPopwindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    } else if (type == PopType.WRAP) {
      mView = mInflater.inflate(R.layout.one_pop_window_wrap, null);
      mPopwindow = new PopupWindow(mView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    mPopwindow.setAnimationStyle(R.style.PopWindowAnim);
    mPopwindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    mPopwindow.setOutsideTouchable(true);
  }


  public static final PopUpService instance(Context context, @PopType int type) {
    return PopFactory.instance(context, type);
  }

  private static final class PopFactory {

    private static PopUpService sPopService;

    public static PopUpService instance(Context context, @PopType int type) {
      if (sPopService == null) {
        synchronized (PopUpService.class) {
          sPopService = new PopUpService(context, type);
        }
      }
      return sPopService;
    }

    public static void release() {
      sPopService = null;
    }
  }

  public PopUpService setItems(final List<PopTabItem> items, final ITabItemClickListener listener) {
    if (items == null || items.isEmpty()) {
      return this;
    }

    int separatorH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f,
        mContext.getResources().getDisplayMetrics());
    int paddingTB = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f,
        mContext.getResources().getDisplayMetrics());
    int paddingLR = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f,
        mContext.getResources().getDisplayMetrics());
    Rect rect = new Rect();

    LinearLayout itemsLayout = (LinearLayout) mView.findViewById(R.id.pop_window_item_parent_layout);
    if (itemsLayout.getChildCount() > 0) {
      itemsLayout.removeAllViews();
    }
    int maxWidth = 0;
    for (final PopTabItem item : items) {
      TextView it = new TextView(mContext);
      it.setText(item.tab);
      it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
      it.setTextColor(Color.parseColor("#999ba1"));
      it.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          if (listener != null) {
            listener.onTabClick(items.indexOf(item));
          }
          dismiss();
        }
      });
      it.setBackgroundResource(R.drawable.one_pop_item_selector);
      it.setPadding(paddingLR, paddingTB, paddingLR, paddingTB);
      it.getPaint().getTextBounds(item.tab, 0, item.tab.length(), rect);
      if (rect.width() > maxWidth) {
        maxWidth = rect.width();
      }
      it.setCompoundDrawablePadding(UIUtils.dip2pxInt(mContext, 5));
      it.setCompoundDrawablesWithIntrinsicBounds(item.itemIcon, 0, 0, 0);
      LayoutParams itParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
      itemsLayout.addView(it, itParam);
      if (items.indexOf(item) == items.size() - 1) {
        break;
      }
      View view = new View(mContext);
      LayoutParams sepPar = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, separatorH);
      sepPar.rightMargin = sepPar.leftMargin = paddingTB;
      view.setBackgroundColor(Color.parseColor("#d3d3d3"));
      itemsLayout.addView(view, sepPar);
    }
    mView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
    mPopwindow.setWidth(mView.getMeasuredWidth());
    fromYDelta = -mView.getMeasuredHeight() - 50;
    return this;
  }

  /**
   * 在view下展示 默认向上移动 20px
   */
  public void showAsDropDown(View view) {
    if (mPopwindow != null && !mPopwindow.isShowing()) {
//      mView.startAnimation(AnimationUtil.createInAnimation(mContext));
      mPopwindow.showAsDropDown(view, 0, -30);
    }
  }

  public void dismiss() {
    if (mPopwindow != null && mPopwindow.isShowing()) {
//      mPopwindow.setOnDismissListener(new OnDismissListener() {
//        @Override
//        public void onDismiss() {
//        }
//      });
//      mView.startAnimation(AnimationUtil.createOutAnimation(mContext));
//      mView.postDelayed(new Runnable() {
//        @Override
//        public void run() {
//          mPopwindow.dismiss();
//        }
//      }, AnimationUtil.ANIMATION_OUT_TIME);
      mPopwindow.dismiss();
      PopFactory.release();
      mPopwindow = null;
    }
  }


  public boolean isShowing() {
    return mPopwindow.isShowing();
  }
}
