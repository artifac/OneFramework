package com.one.framework.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.app.widget.base.IListItemView;
import com.one.framework.utils.UIUtils;

/**
 * Created by ludexiang on 2017/12/6.
 */

public class ListItemWithRightArrowLayout extends RelativeLayout implements IListItemView,
    View.OnClickListener {

  private Context mContext;
  private ShapeImageView mItemImg;
  private TextView mItemTitle;
  private ScriptTextView mRightTxt;
  private IClickCallback mClickCallback;

  public ListItemWithRightArrowLayout(Context context) {
    this(context, null);
  }

  public ListItemWithRightArrowLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ListItemWithRightArrowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mContext = context;

    View view = LayoutInflater.from(context).inflate(R.layout.one_list_item_with_arrow_layout, this, true);
    mItemImg = (ShapeImageView) view.findViewById(R.id.list_item_img_view);
    mItemTitle = (TextView) view.findViewById(R.id.list_item_info);
    mRightTxt = (ScriptTextView) view.findViewById(R.id.list_item_right_info);

    setClickable(true);
    setBackgroundDrawable(UIUtils.rippleDrawableRect(Color.WHITE, Color.parseColor("#e3e3e3")));
  }

  @Override
  public void setLeftImgVisible(boolean visible) {
    mItemImg.setVisibility(visible ? View.VISIBLE : View.GONE);
    LayoutParams layoutParams = (LayoutParams) mItemTitle.getLayoutParams();
    if (visible) {
      layoutParams.leftMargin = UIUtils.dip2pxInt(getContext(), 13);
    } else {
      layoutParams.leftMargin = UIUtils.dip2pxInt(getContext(), 20);
    }
  }

  @Override
  public void setImgUrl(String url, int defaultRes) {
    if (!TextUtils.isEmpty(url)) {
      mItemImg.setBorderWidth(1f);
      mItemImg.loadImageByUrl(this, url, "mocha");
    }
  }

  @Override
  public void setImgRes(int resId) {
    setLeftImgVisible(true);
    mItemImg.setImageResource(resId);
  }

  @Override
  public void setItemTitle(int strRes) {
    mItemTitle.setText(strRes);
  }

  @Override
  public void setItemTitle(CharSequence title) {
    mItemTitle.setText(title);
  }

  @Override
  public void setItemTitle(CharSequence title, int size) {
    mItemTitle.setText(title);
    mItemTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
  }

  @Override
  public void setRightTxt(CharSequence rightTxt) {
    mRightTxt.setText(rightTxt);
  }

  @Override
  public void setRightTxt(CharSequence rightTxt, int color) {
    mRightTxt.setText(rightTxt);
    mRightTxt.setTextColor(color);
  }

  @Override
  public void setRightTxt(CharSequence rightTxt, int color, int size) {
    mRightTxt.setText(rightTxt);
    mRightTxt.setTextColor(color);
    mRightTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
  }

  @Override
  public void setScriptTxt(CharSequence scriptTxt, int color, int size) {
    mRightTxt.setScriptText((String) scriptTxt, color, size);
  }

  @Override
  public void setClickCallback(IClickCallback callback) {
    /**
     * callback != null 则设置ClickListener
     */
    if (callback != null) {
      setOnClickListener(this);
      mClickCallback = callback;
    } else {
      setOnClickListener(null);
    }
  }

  @Override
  public void onClick(View view) {
    if (mClickCallback != null) {
      mClickCallback.callback();
    }
  }
}
