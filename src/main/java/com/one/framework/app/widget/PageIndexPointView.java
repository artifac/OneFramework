package com.one.framework.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.one.framework.R;

/**
 * Created by ludexiang on 2018/8/24.
 * 支持圆形和矩形
 */

public class PageIndexPointView extends View {
  
  private static final int CYCLE = 0;
  private static final int RECT = 1;
  private final int WH;
  
  private int mPointType;
  private int mNormalColor;
  private int mSelectColor;
  private int mPointWidth;
  private int mPointHeight;
  private int mPointPadding;
  
  private Paint mPaint;
  
  private int mSize;
  
  private int mCurrentPosition;
  
  public PageIndexPointView(Context context) {
    this(context, null);
  }
  
  public PageIndexPointView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }
  
  public PageIndexPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    WH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
    
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PageIndexPoint);
    mPointType = a.getInt(R.styleable.PageIndexPoint_point_type, CYCLE);
    mNormalColor = a.getColor(R.styleable.PageIndexPoint_point_normal_color, Color.WHITE);
    mSelectColor = a.getColor(R.styleable.PageIndexPoint_point_select_color, Color.BLACK);
    mPointWidth = a.getDimensionPixelOffset(R.styleable.PageIndexPoint_point_width, WH);
    mPointHeight = a.getDimensionPixelOffset(R.styleable.PageIndexPoint_point_height, WH);
    mPointPadding = a.getDimensionPixelOffset(R.styleable.PageIndexPoint_point_padding, 0);
    a.recycle();
    
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint.setStyle(Paint.Style.FILL);
  }
  
  /**
   * 设置显示多少个point
   *
   * @param size
   */
  public void setSize(int size) {
    mSize = size;
    postInvalidate();
  }
  
  public void snapTo(int position) {
    mCurrentPosition = position;
    postInvalidate();
  }
  
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (mSize <= 1) {
      return;
    }
    if (mPointType == CYCLE || mPointWidth == mPointHeight) {
      // draw cycle
      drawCycle(canvas);
    } else if (mPointType == RECT) {
      // draw rect
      drawRect(canvas);
    }
  }
  
  private void drawCycle(Canvas canvas) {
    float centerX = (getWidth() - mPointWidth * mSize - mPointPadding * (mSize - 1)) / 2;
    float centerY = getHeight() / 2; // 圆心即高度的中心即可
    float radius = mPointWidth * 1f / 2;
    for (int i = 0; i < mSize; i++) {
      if (i == mCurrentPosition) {
        mPaint.setColor(mSelectColor);
      } else {
        mPaint.setColor(mNormalColor);
      }
      float x = centerX + i * mPointWidth + i * mPointPadding;
      canvas.drawCircle(x, centerY, radius, mPaint);
    }
  }
  
  private void drawRect(Canvas canvas) {
    float left = (getWidth() - mPointWidth * mSize - mPointPadding * (mSize - 1)) / 2;
    float top = (getHeight() - mPointHeight) / 2;
    float bottom = top + mPointHeight;
    for (int i = 0; i < mSize; i++) {
      if (i == mCurrentPosition) {
        mPaint.setColor(mSelectColor);
      } else {
        mPaint.setColor(mNormalColor);
      }
      float x = left + i * mPointWidth + i * mPointPadding;
      canvas.drawRect(x, top, x + mPointWidth, bottom, mPaint);
    }
  }
  
  public int getSize() {
    return mSize;
  }
}
