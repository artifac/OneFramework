package com.one.framework.app.widget;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
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
  private static final int LOADING_PROGRESS_CIRCLE = 2;
  private static final int LOADING_PROGRESS_LINE = 3;

  private int mLoadingType;
  private int mLoadingColor;
  private int mLoadingNormalSize;
  private int mLoadingCount;
  private int mLoadingSelectorSize;

  private int mLoadingProgressType;
  private int mLoadingProgressColor;
  private int mLoadingProgressWidth;

  private int mLoadingPointId;
  private ValueAnimator mLoadingAnimator;
  private Thread mProgressAnimator;
  private final float mPointSpace;


  private boolean isRunning = false;
  private RectF mProgressRectF = new RectF();
  private float mProgressSweep = 0f;
  private byte[] lock = new byte[0];
  private boolean isProgressFlag = true;
  private int repeatCount = 0;
  private int animationCount = 0;
  private int mWaitConfigTime;

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
    mLoadingNormalSize = a
        .getDimensionPixelSize(R.styleable.LoadingView_loading_point_normal_size, 5);
    mLoadingSelectorSize = a
        .getDimensionPixelSize(R.styleable.LoadingView_loading_point_selector_size, 8);
    mLoadingCount = a.getInt(R.styleable.LoadingView_loading_point_count, 3);
    mLoadingProgressType = a
        .getInt(R.styleable.LoadingView_loading_progress_type, LOADING_PROGRESS_LINE);
    mLoadingProgressColor = a
        .getColor(R.styleable.LoadingView_loading_progress_color, Color.parseColor("#f05b48"));
    mLoadingProgressWidth = a
        .getDimensionPixelOffset(R.styleable.LoadingView_loading_progress_width, 5);
    a.recycle();

    mPointSpace = TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    mProgressRectF.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    mPaint.setStrokeCap(Cap.ROUND);
    if (mLoadingType == LOADING_POINT) {
      mPaint.setColor(mLoadingColor);
      float x =
          (getWidth() - mLoadingSelectorSize - (mLoadingCount - 1) * mLoadingNormalSize) * 1f / 2
              - mLoadingSelectorSize;
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
    } else {
      // progress
      if (mLoadingProgressType == LOADING_PROGRESS_CIRCLE) {
        mPaint.setStrokeWidth(mLoadingProgressWidth);
        mPaint.setColor(mLoadingProgressColor);
        mPaint.setStyle(Style.STROKE);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);
        canvas.save();
        mPaint.setColor(Color.parseColor("#f05b48"));
        canvas.drawArc(mProgressRectF, 270, mProgressSweep, false, mPaint);
      }
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
    isRunning = true;
  }

  @Override
  public void setVisibility(int visibility) {
    if (mLoadingType == LOADING_POINT) {
      if (visibility == View.VISIBLE) {
        loading();
      } else {
        stop();
      }
    }
    super.setVisibility(visibility);
  }

  private void stop() {
    if (mLoadingAnimator != null && mLoadingAnimator.isRunning()) {
      mLoadingAnimator.cancel();
      mLoadingAnimator.removeAllUpdateListeners();
      mLoadingAnimator = null;
    }

    isRunning = false;
    mLoadingPointId = 0;
  }

  public synchronized void setConfigWaitTime(int time) {
    mWaitConfigTime = time;
    progress();
  }

  public LoadingView setRepeatCount(int count) {
    repeatCount = count;
    return this;
  }

  private synchronized void progress() {
    if (mProgressAnimator != null && mProgressAnimator.isAlive()) {
      return;
    }

    mProgressAnimator = new Thread(new Runnable() {
      @Override
      public void run() {
        while (mProgressSweep <= 360 && isProgressFlag) {
          mProgressSweep += 1;
          postInvalidate();

          if (repeatCount == -1) {
            // INFINITE
            if (mProgressSweep >= 360) {
              mProgressSweep = 0;
            }
          } else {
            if (animationCount < repeatCount) {
              if (mProgressSweep >= 360) {
                mProgressSweep = 0;
                animationCount++;
              }
            }
          }
          synchronized (lock) {
            try {
              lock.wait(mWaitConfigTime > 10 ? (int) (2.5f * mWaitConfigTime) : mWaitConfigTime);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      }
    });

    mProgressAnimator.start();
  }

  public void release() {
    isProgressFlag = false;
    if (mProgressAnimator != null) {
      mProgressAnimator.interrupt();
      mProgressAnimator = null;
    }
  }
}
