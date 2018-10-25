package com.one.framework.app.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Button;
import com.one.framework.R;
import com.one.framework.utils.UIUtils;

@SuppressLint("AppCompatCustomView")
public class TripButton extends Button {
  
  private static final int NORMAL = 0;

  private Context mContext;
  
  private int mRadius;
  private int mEnableColor;
  private int mDisableColor;
  private CharSequence mText;
  private float mTextSize;
  private int mTextColor;
  private Paint mPaint;
  private RectF mRectF;
  private final int[] intAArray;
  private Paint.FontMetrics mMetrics;
  private int mStrokeColor;
  private float mStokeWidth;
  private int mStyle;
  private int mBackground;
  
  public TripButton(Context context) {
    this(context, null);
  }
  
  public TripButton(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }
  
  public TripButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mContext = context;
    TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TripButton);
    mRadius = array.getDimensionPixelOffset(R.styleable.TripButton_btn_radius, 6);
    mBackground = array.getResourceId(R.styleable.TripButton_btn_background, 0);
    mEnableColor = array.getColor(R.styleable.TripButton_enable_color, 0);
    mDisableColor = array.getColor(R.styleable.TripButton_disable_color, 0);
    mTextColor = array.getColor(R.styleable.TripButton_text_color, Color.WHITE);
    mTextSize = array.getDimensionPixelSize(R.styleable.TripButton_text_size, 0);
    mText = array.getString(R.styleable.TripButton_text);
    mStrokeColor = array.getColor(R.styleable.TripButton_stroke_color, 0);
    mStokeWidth = array.getDimensionPixelOffset(R.styleable.TripButton_stroke_width, 0);
    mStyle = array.getInt(R.styleable.TripButton_text_style, NORMAL);
    array.recycle();
    
    if (TextUtils.isEmpty(mText)) {
      mText = getText();
    }
    if (mTextSize == 0) {
      mTextSize = getTextSize();
    }
    intAArray = new int[]{mRadius, mRadius, mRadius, mRadius};
    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setStyle(Style.FILL);
    mPaint.setDither(true);
  
    setRippleColor(mEnableColor, mDisableColor);
  }
  
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    if (mStrokeColor == 0) {
      heightSize -= mRadius / 2;
    }
    setMeasuredDimension(widthSize, heightSize);
    mRectF = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if (mEnableColor != 0 && mDisableColor != 0) {
      if (enabled) {
        setRippleColor(mEnableColor, mDisableColor);
      } else {
        setRippleColor(mDisableColor, mDisableColor);
      }
    } else {
      setBackgroundResource(mBackground);
    }
//    invalidate();
  }
  
  public void setRippleColor(int color, int maskColor) {
    if (color != 0 && maskColor != 0) {
      setBackgroundDrawable(UIUtils.rippleDrawableRounded(color, maskColor, intAArray));
    } else {
      setBackgroundResource(mBackground);
    }
  }
  
  public void setTripButtonTextColor(int textColor) {
    mTextColor = textColor;
    postInvalidate();
  }
  
  public void setTripButtonText(String text) {
    if (text == null) {
      return;
    }
    mText = text;
    postInvalidate();
  }

  public void setTripButtonText(int text) {
    if (text == 0) {
      return;
    }
    mText = mContext.getString(text);
    postInvalidate();
  }
  
  public String getTripButtonText() {
    return mText.toString();
  }
  
  public void setStrokeColor(int color) {
    mStrokeColor = color;
    postInvalidate();
  }
  
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (TextUtils.isEmpty(mText)) {
      return;
    }
    mPaint.setTypeface(mStyle == NORMAL ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);
    mPaint.setColor(mTextColor);
    mPaint.setTextSize(mTextSize);
    mMetrics = mPaint.getFontMetrics(); // 一定要在setTextSize()之后
    mPaint.setTextAlign(Paint.Align.CENTER);
    canvas.drawText((String) mText, getWidth() / 2, (getHeight() - mMetrics.ascent - mMetrics.leading - mMetrics.descent) / 2, mPaint);
    if (mStrokeColor != 0) {
      Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
      paint.setAntiAlias(true);
      paint.setDither(true);
      paint.setColor(mStrokeColor);
      paint.setStyle(Style.STROKE);
      paint.setStrokeWidth(mStokeWidth);
      RectF strokeRect = new RectF(mRectF.left + mRadius / 2f, mRectF.top + mRadius / 2f, mRectF.right - mRadius / 2f, mRectF.bottom - mRadius / 2f);
      canvas.drawRoundRect(strokeRect, mRadius, mRadius, paint);
    }
  }
  
  private float getTextWidth() {
    TextPaint paint = getPaint();
    return paint.measureText((String)mText);
  }
}