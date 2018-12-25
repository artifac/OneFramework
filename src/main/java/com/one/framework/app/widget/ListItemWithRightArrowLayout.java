package com.one.framework.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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
  private ImageView mArrow;
  private View mSeparator;

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
    mItemImg = view.findViewById(R.id.list_item_img_view);
    mItemTitle = view.findViewById(R.id.list_item_info);
    mRightTxt = view.findViewById(R.id.list_item_right_info);
    mArrow = view.findViewById(R.id.list_item_right_arrow);
    mSeparator = view.findViewById(R.id.list_item_separator);

    setClickable(true);
    setBackgroundDrawable(UIUtils.rippleDrawableRect(Color.WHITE, Color.parseColor("#e3e3e3")));

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ListItemWithRightArrowLayout);
    int itemTitle = a.getResourceId(R.styleable.ListItemWithRightArrowLayout_itemTitle, -1);
    int itemColor = a.getColor(R.styleable.ListItemWithRightArrowLayout_itemTitleColor, -1);
    int itemSize = a.getDimensionPixelSize(R.styleable.ListItemWithRightArrowLayout_itemTitleSize, -1);
    int itemMargin = a.getDimensionPixelOffset(R.styleable.ListItemWithRightArrowLayout_itemMargin, -1);
    boolean showArrow = a.getBoolean(R.styleable.ListItemWithRightArrowLayout_itemShowArrow, true);
    String itemRightTitle = a.getString(R.styleable.ListItemWithRightArrowLayout_itemRightTitle);
    int itemRightUnit = a.getResourceId(R.styleable.ListItemWithRightArrowLayout_itemRightUnit, -1);
    int itemRightUnitColor = a.getColor(R.styleable.ListItemWithRightArrowLayout_itemRightUnitColor, Color.parseColor("#999ba1"));
    int itemRightUnitSize = a.getInt(R.styleable.ListItemWithRightArrowLayout_itemRightUnitSize, -1);
    int itemRightColor = a.getColor(R.styleable.ListItemWithRightArrowLayout_itemRightTitleColor, Color.parseColor("#999ba1"));
    a.recycle();

    if (itemTitle != -1 && itemColor != -1 && itemSize != -1 && itemMargin != -1) {
      setItemTitle(itemTitle, itemSize, itemColor, false);
      setLRMargin(itemMargin);
    }

    if (!TextUtils.isEmpty(itemRightTitle)) {
      setRightTxt(itemRightTitle, itemRightColor);
    }
    if (itemRightUnit != -1) {
      setScriptTxt(getResources().getString(itemRightUnit), itemRightUnitColor, itemRightUnitSize);
    }
    setArrowVisible(showArrow);
  }

  @Override
  public void setLRMargin(int margin) {
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mItemTitle.getLayoutParams();
    params.leftMargin = params.rightMargin = margin;
    mItemTitle.setLayoutParams(params);

    RelativeLayout.LayoutParams arrowParams = (RelativeLayout.LayoutParams) mArrow
        .getLayoutParams();
    arrowParams.rightMargin = margin;
    mArrow.setLayoutParams(arrowParams);
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
    setImgRes(resId, ScaleType.FIT_XY);
  }

  @Override
  public void setImgRes(int resId, ScaleType scaleType) {
    setLeftImgVisible(true);
    mItemImg.setImageResource(resId);
    mItemImg.setScaleType(scaleType);
  }

  @Override
  public void setItemTitle(int strRes) {
    mItemTitle.setText(strRes);
  }

  @Override
  public void setItemTitle(int strRes, int color) {
    mItemTitle.setText(strRes);
    mItemTitle.setTextColor(color);
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
  public void setItemTitle(CharSequence title, int size, int color) {
    setItemTitle(title, size, color, true);
  }

  @Override
  public void setItemTitle(int title, int size, int color, boolean useSPUnit) {
    setItemTitle(getResources().getString(title), size, color, useSPUnit);
  }

  @Override
  public void setItemTitle(CharSequence title, int size, int color, boolean useSPUnit) {
    mItemTitle.setText(title);
    if (useSPUnit) {
      mItemTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    } else {
      mItemTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }
    mItemTitle.setTextColor(color);
  }

  @Override
  public void setItemTitle(CharSequence title, int size, boolean bold, int color) {
    mItemTitle.setText(title);
    mItemTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    mItemTitle.setTypeface(bold ? Typeface.defaultFromStyle(Typeface.BOLD)
        : Typeface.defaultFromStyle(Typeface.NORMAL));
    mItemTitle.setTextColor(color);
  }

  @Override
  public void setRightTxt(CharSequence rightTxt) {
    mRightTxt.setText(rightTxt);
  }

  @Override
  public void setRightTxt(int resId) {
    mRightTxt.setText(resId);
  }

  @Override
  public void setItemColor(int color) {
    mItemTitle.setTextColor(color);
  }

  @Override
  public void setRightColor(int color) {
    mRightTxt.setTextColor(color);
  }

  @Override
  public void setRightTxt(CharSequence rightTxt, int color) {
    mRightTxt.setText(rightTxt);
    mRightTxt.setTextColor(color);
  }

  @Override
  public void setRightTxt(CharSequence rightTxt, int size, int color) {
    mRightTxt.setText(rightTxt);
    mRightTxt.setTextColor(color);
    mRightTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
  }

  @Override
  public void setScriptTxt(CharSequence scriptTxt, int color, int size) {
    mRightTxt.setScriptText((String) scriptTxt, color, size);
  }

  /**
   * 设置separator color
   */
  @Override
  public void setSeparatorColor(int color) {

  }

  @Override
  public void setArrowVisible(boolean visible) {
    mArrow.setVisibility(visible ? View.VISIBLE : View.GONE);
    if (!visible) {
      RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRightTxt.getLayoutParams();
      params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
      mRightTxt.setLayoutParams(params);
    }
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
      mClickCallback.callback(view.getId());
    }
  }
}
