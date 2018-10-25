package com.one.framework.app.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.one.framework.R;
import com.one.framework.app.widget.base.IOptionView;
import com.one.framework.utils.PreferenceUtil;
import java.util.ArrayList;
import java.util.List;

public class OptionsView extends View implements IOptionView {

  private int mViewType;
  private int mReferenceId; // string-array
  private int mMinWidth; // single item min width
  private int mMinHeight;
  private List<String> mOptions = new ArrayList<String>();
  private float mOuterRadius;
  private float mInnerRadius;
  private int mBgColor;
  private int mFgColor;
  private int mDefaultTxtColor;
  private int mSelectTxtColor;
  private int mTextSize;
  private RectF mOuterRect = new RectF();
  private RectF mInnerRect = new RectF();
  private Paint mTxtPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 文案
  private int mCurrentPosition;
  private int mLastPosition;
  private float mChangeLeft;
  private int mPadding;

  private int mLastX;
  private int mLastY;
  private RectF[] mTxtRect;
  private String mSettingKey;
  private ValueAnimator mTranslateX;
  private Context mContext;
  private IOptionChange mListener;

  private boolean isToggle;
  private int toggleOnColor;
  private int toggleOffColor;

  public OptionsView(Context context) {
    this(context, null);
  }

  public OptionsView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public OptionsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mContext = context;
    Resources res = context.getResources();
    mMinWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 67, res.getDisplayMetrics());
    mMinHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, res.getDisplayMetrics());
    mPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, res.getDisplayMetrics());

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OptionsView);
    mViewType = a.getInt(R.styleable.OptionsView_options_type, 0); // default 0 -> rect 1 - > oval
    mReferenceId = a.getResourceId(R.styleable.OptionsView_options_op, 0);
    isToggle = a.getBoolean(R.styleable.OptionsView_options_toggle, false);
    toggleOnColor = a.getColor(R.styleable.OptionsView_options_toggle_on_bg, 0);
    toggleOffColor = a.getColor(R.styleable.OptionsView_options_toggle_off_bg, 0);
    mMinHeight = a.getDimensionPixelOffset(R.styleable.OptionsView_options_min_height, mMinHeight);
    mMinWidth = a.getDimensionPixelOffset(R.styleable.OptionsView_options_min_width, mMinWidth);
    mOuterRadius = a.getDimensionPixelOffset(R.styleable.OptionsView_options_bg_outer_radius, 0);
    mInnerRadius = a.getDimensionPixelOffset(R.styleable.OptionsView_options_bg_inner_radius, 0);
    mBgColor = a.getColor(R.styleable.OptionsView_options_background_color, 0);
    mFgColor = a.getColor(R.styleable.OptionsView_options_foreground_color, 0);
    mDefaultTxtColor = a.getColor(R.styleable.OptionsView_options_text_default_color, 0);
    mSelectTxtColor = a.getColor(R.styleable.OptionsView_options_text_selected_color, 0);
    mTextSize = a.getDimensionPixelSize(R.styleable.OptionsView_options_text_size, 0);
    mSettingKey = a.getString(R.styleable.OptionsView_options_op_key);
    int defaultSelect = a.getInteger(R.styleable.OptionsView_options_default_select, -1); // 顺路单有默认选择值
    a.recycle();

    if (TextUtils.isEmpty(mSettingKey)) {
      throw new IllegalArgumentException("Setting key or mReferenceId must be have value");
    }

    if (mReferenceId != 0) {
      String[] ops = res.getStringArray(mReferenceId);
      for (String op : ops) {
        mOptions.add(op);
      }
    }
    mTxtRect = new RectF[isToggle? 2 : mOptions.size()];
    mOuterRect.set(0, 0, isToggle ? 2 * mMinWidth : mOptions.size() * mMinWidth, mMinHeight);

    for (int i = 0; i < mTxtRect.length; i++) {
      mTxtRect[i] = new RectF();
      mTxtRect[i].set(i * mMinWidth, 0, i * mMinWidth + mMinWidth, mMinHeight);
    }

    int currentSelect = PreferenceUtil.instance(mContext).getInt(mSettingKey); // default return -1
    mLastPosition = mCurrentPosition = currentSelect;
    if (defaultSelect != -1 && mCurrentPosition == -1) { // 默认属性有个选择
      mLastPosition = mCurrentPosition = defaultSelect;
      if (mListener != null) {
        mListener.onChange(mSettingKey, mCurrentPosition);
      }
    } else {
      mLastPosition = mCurrentPosition = currentSelect == -1 ? 0 : currentSelect;
    }
    mChangeLeft = mCurrentPosition * mMinWidth;
    mInnerRect.set(mChangeLeft, 0, mChangeLeft + mMinWidth, mMinHeight);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    bgPaint.setStyle(Paint.Style.FILL);
    bgPaint.setDither(true);
    bgPaint.setColor(mBgColor);
    if (mViewType == 0) {
      if (isToggle) {
        bgPaint.setColor(mCurrentPosition == 0 ? toggleOffColor : toggleOnColor);
        canvas.drawRoundRect(mOuterRect, mOuterRadius, mOuterRadius, bgPaint);
      } else {
        canvas.drawRoundRect(mOuterRect, mOuterRadius, mOuterRadius, bgPaint);
      }
    } else {
      canvas.drawOval(mOuterRect, bgPaint);
    }

    int fbRestore = canvas.save();
    Paint fgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    fgPaint.setColor(mFgColor);
    fgPaint.setStyle(Paint.Style.FILL);
    RectF inner = new RectF(mChangeLeft + mPadding, mPadding, mChangeLeft + mMinWidth - mPadding,
        mMinHeight - mPadding);
    if (mViewType == 0) {
      if (isToggle) {
        Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setColor(Color.parseColor("#10999999"));
        shadowPaint.setStyle(Style.FILL);
        canvas.drawCircle(mChangeLeft + inner.width() / 2, inner.height() / 2, inner.width() / 2 + 4, shadowPaint);
      }
      canvas.drawRoundRect(inner, mInnerRadius, mInnerRadius, fgPaint);
    } else {
      canvas.drawOval(inner, fgPaint);
    }
    canvas.restoreToCount(fbRestore);

    int txRestore = canvas.save();
    drawTxt(canvas);
    canvas.restoreToCount(txRestore);
  }

  private void drawTxt(Canvas canvas) {
    mTxtPaint.setTextSize(mTextSize);
    mTxtPaint.setTextAlign(Paint.Align.CENTER);
    Paint.FontMetricsInt metrics = mTxtPaint.getFontMetricsInt();
    for (int i = 0; i < mOptions.size(); i++) {
      if (i == mCurrentPosition) {
        mTxtPaint.setColor(mSelectTxtColor);
      } else {
        mTxtPaint.setColor(mDefaultTxtColor);
      }
      float baseline = (getMeasuredHeight() - metrics.bottom + metrics.top) / 2 - metrics.top;
      canvas.drawText(mOptions.get(i), i * mMinWidth + mTxtRect[i].width() / 2, baseline, mTxtPaint);
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (mTranslateX != null && mTranslateX.isRunning()) {
      return true;
    }
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN: {
        mLastX = (int) (event.getX() + 0.5);
        mLastY = (int) (event.getY() + 0.5);
        return true;
      }
      case MotionEvent.ACTION_MOVE: {
        return true;
      }
      case MotionEvent.ACTION_UP: {
        handleUp(event);
        return super.onTouchEvent(event);
      }
    }
    return true;
  }

  private void handleUp(MotionEvent event) {
    int currentX = (int) (event.getX() + 0.5);
    int currentY = (int) (event.getY() + 0.5);

    for (int i = 0; i < mTxtRect.length; i++) {
      if (mTxtRect[i].contains(currentX, currentY)) {
        mCurrentPosition = i;
        break;
      }
      continue;
    }
    if (mLastPosition == mCurrentPosition) {
      return;
    }
    translate();
  }

  private void translate() {
    mTranslateX = ValueAnimator.ofFloat(0f, 1f);
    mTranslateX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float value = (Float) valueAnimator.getAnimatedValue();
        if (mCurrentPosition > mLastPosition) {
          mChangeLeft = mInnerRect.left + value * mMinWidth * (mCurrentPosition - mLastPosition);
        } else {
          mChangeLeft = mInnerRect.left - value * mMinWidth * (mLastPosition - mCurrentPosition);
        }
        postInvalidate();
      }
    });
    mTranslateX.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        mInnerRect.setEmpty();
        mInnerRect.set(mChangeLeft, 0, mChangeLeft + mMinWidth, mMinHeight);
        mLastPosition = mCurrentPosition;
        if (mListener != null) {
          mListener.onChange(mSettingKey, mCurrentPosition);
        }
      }
    });
    mTranslateX.setInterpolator(new AccelerateDecelerateInterpolator());
    mTranslateX.setDuration(300);
    mTranslateX.start();
  }

  @Override
  public void setState(int state) {
    mCurrentPosition = state;
    if (mLastPosition == mCurrentPosition) {
      return;
    }
    translate();
  }

  /**
   * 表单所用
   * @return
   */
  @Override
  public int getState() {
    if (mCurrentPosition == 0) {
      return IOptionView.NOW;
    } else if (mCurrentPosition == 1) {
      return IOptionView.BOOKING;
    }
    return IOptionView.NOW;
  }

  @Override
  public void setOptionChange(IOptionChange l) {
    mListener = l;
  }

  public void save() {
    PreferenceUtil.instance(mContext).putInt(mSettingKey, mCurrentPosition);
  }

  @Override
  public int getSavePosition() {
    return PreferenceUtil.instance(mContext).getInt(mSettingKey);
  }

  @Override
  public int getPosition() {
    return mCurrentPosition;
  }

  @Override
  public View getView() {
    return this;
  }
}
