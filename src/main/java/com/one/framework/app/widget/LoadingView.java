package com.one.framework.app.widget;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
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
 * Created by ludexiang on 2018/6/5.
 */

public class LoadingView extends View {

  private static final int LOADING_POINT = 0;
  private static final int LOADING_PROGRESS = 1;

  private int mLoadingType;
  private int mLoadingColor;
  private int mLoadingNormalSize;
  private int mLoadingCount;
  private int mLoadingSelectorSize;

  private int mLoadingPointId;
  private ValueAnimator mLoadingAnimator;
  private final float mPointSpace;

  private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  public LoadingView(Context context) {
    this(context, null);
  }

  public LoadingView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
    mLoadingType = a.getInt(R.styleable.LoadingView_loading_type, LOADING_POINT);
    mLoadingColor = a.getColor(R.styleable.LoadingView_loading_point_color, Color.GRAY);
    mLoadingNormalSize = a.getDimensionPixelSize(R.styleable.LoadingView_loading_point_normal_size, 5);
    mLoadingSelectorSize = a.getDimensionPixelSize(R.styleable.LoadingView_loading_point_selector_size, 8);
    mLoadingCount = a.getInt(R.styleable.LoadingView_loading_point_count, 3);
    a.recycle();

    mPointSpace = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());

    loading();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    mPaint.setColor(mLoadingColor);

    float x = (getWidth() - mLoadingSelectorSize - (mLoadingCount - 1) * mLoadingNormalSize) * 1f / 2 - mLoadingSelectorSize;
    float y = (getHeight() - mLoadingSelectorSize) * 1f / 2;

    for (int i = 0; i < mLoadingCount; i++) {
      float radius = mLoadingNormalSize;
      if (i == mLoadingPointId) {
        mPaint.setStrokeWidth(mLoadingSelectorSize);
        radius = mLoadingSelectorSize;
      } else {
        mPaint.setStrokeWidth(mLoadingNormalSize);
      }
      canvas.drawCircle(x + i * mPointSpace * 2, y, radius, mPaint);
    }
  }

  private void loading() {
    if (mLoadingAnimator != null && mLoadingAnimator.isRunning()) {
      return;
    }
    mLoadingAnimator = ValueAnimator.ofInt(0, mLoadingCount);
    mLoadingAnimator.setRepeatCount(-1);
    mLoadingAnimator.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        int curPoint = (int) animation.getAnimatedValue();
        mLoadingPointId = curPoint;
        postInvalidate();
      }
    });
    mLoadingAnimator.setDuration(600);
    mLoadingAnimator.start();
  }

  public void stop() {
    if (mLoadingAnimator != null && mLoadingAnimator.isRunning()) {
      mLoadingAnimator.cancel();
      mLoadingAnimator.removeAllUpdateListeners();
      mLoadingAnimator = null;
    }

    mLoadingPointId = 0;
  }
}
