package com.one.framework.app.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import com.one.framework.log.Logger;
import com.one.framework.utils.UIUtils;

/**
 * Created by ludexiang on 2018/6/5.
 */

public class LoadingView extends View {

  private static final int CIRCLE = 360;

  private static final int LOADING_POINT = 0;
  private static final int LOADING_PROGRESS = 1;
  private static final int LOADING_PROGRESS_CIRCLE = 2;
  private static final int LOADING_PROGRESS_LINE = 3;
  private static final int LOADING_LINE_START_POSITION_LEFT = 0;
  private static final int LOADING_LINE_START_POSITION_CENTER = 1;
  private static final int LOADING_LINE_START_POSITION_RIGHT = 2;

  private int mLoadingType;
  private int mLoadingColor;
  private int mLoadingNormalSize;
  private int mLoadingCount;
  private int mLoadingSelectorSize;

  private int mLoadingProgressType;
  private int mLoadingProgressColor;
  private int mLoadingProgressWidth;
  private int mLoadingProgressLineStart;
  private float mLineLeftEnd;
  private float mLineCenter2Left;
  private float mLineCenter2Right;

  private int mLoadingPointId;
  private ValueAnimator mLoadingAnimator;
  private Thread mProgressAnimator;
  private final float mPointSpace;

  private RectF mProgressRectF = new RectF();
  private float mProgressSweep = 0f;
  private byte[] lock = new byte[0];
  private boolean isProgressFlag = true;
  private int repeatCount = 0;
  private int animationCount = 0;
  private int mWaitConfigTime;

  private int[] lineColors = {Color.parseColor("#f05b48"), Color.parseColor("#6beff4")};
  private boolean lineProgressBegin = false;
  private ValueAnimator mLineAnimator;
  private int mScreenWidth;

  private int mCurrentPaintColor;
  private int mLastPaintColor = -1;

  private int lineColorPosition = 1;

  private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mLastPaint = null;

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
    mLoadingProgressType = a.getInt(R.styleable.LoadingView_loading_progress_type, LOADING_PROGRESS_LINE);
    mLoadingProgressColor = a.getColor(R.styleable.LoadingView_loading_progress_color, Color.parseColor("#f05b48"));
    mLoadingProgressWidth = a.getDimensionPixelOffset(R.styleable.LoadingView_loading_progress_width, 5);
    mLoadingProgressLineStart = a.getInt(R.styleable.LoadingView_loading_line_start, LOADING_LINE_START_POSITION_LEFT);
    a.recycle();

    mPointSpace = TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());

    mScreenWidth = UIUtils.getScreenWidth(getContext());

    mLineCenter2Left = mLineCenter2Right = mScreenWidth / 2;
    mLastPaint = new Paint();
    mLastPaint.setAntiAlias(true);
    mLastPaint.setStyle(Paint.Style.FILL);
    mLastPaint.setStrokeWidth(mLoadingProgressWidth);
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
    switch (mLoadingType) {
      case LOADING_POINT: {
        mPaint.setColor(mLoadingColor);
        drawLoadingPoint(canvas);
        break;
      }
      case LOADING_PROGRESS: {
        // progress
        if (mLoadingProgressType == LOADING_PROGRESS_CIRCLE) { // 圆形进度条
          drawLoadingCircle(canvas);
        } else { // 线性进度条
          drawLoadingLine(canvas);
        }
        break;
      }
    }
  }

  private void drawLoadingLine(Canvas canvas) {
    if (mLastPaintColor != -1) {
      canvas.drawLine(0,0, mScreenWidth, 0, mLastPaint);
    }

    mPaint.setColor(lineProgressBegin ? lineColors[lineColorPosition] : mLoadingProgressColor);
    mPaint.setStyle(Style.FILL);
    mPaint.setAntiAlias(true);
    mPaint.setStrokeWidth(mLoadingProgressWidth);
    switch (mLoadingProgressLineStart) {
      case LOADING_LINE_START_POSITION_LEFT: {
        canvas.drawLine(0, 0, mLineLeftEnd, 0, mPaint);
        break;
      }
      case LOADING_LINE_START_POSITION_CENTER: {
        int centerX = UIUtils.getScreenWidth(getContext()) / 2;
        canvas.drawLine(centerX, 0, mLineCenter2Left, 0, mPaint);
        canvas.drawLine(centerX, 0, mLineCenter2Right, 0, mPaint);
        break;
      }
      case LOADING_LINE_START_POSITION_RIGHT: {
        // 暂时先不实现
        break;
      }
    }
  }

  public synchronized void updateProgressLine(float linePosition) {
    switch (mLoadingProgressLineStart) {
      case LOADING_LINE_START_POSITION_LEFT: {
        mLineLeftEnd = linePosition;
        postInvalidate();
        break;
      }
      case LOADING_LINE_START_POSITION_CENTER: {
        float centerX = UIUtils.getScreenWidth(getContext()) / 2;
        mLineCenter2Left = centerX - linePosition;
        mLineCenter2Right = centerX + linePosition;
        postInvalidate();
      }
    }
  }

  private void drawLoadingCircle(Canvas canvas) {
    mPaint.setStrokeWidth(mLoadingProgressWidth);
    mPaint.setColor(mLoadingProgressColor);
    mPaint.setStyle(Style.STROKE);
    canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);
    canvas.save();
    mPaint.setColor(Color.parseColor("#f05b48"));
    canvas.drawArc(mProgressRectF, 270, mProgressSweep, false, mPaint);
  }

  private void drawLoadingPoint(Canvas canvas) {
    float x = (getWidth() - mLoadingSelectorSize - (mLoadingCount - 1) * mLoadingNormalSize) * 1f / 2
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

  public void stop() {
    if (mLoadingAnimator != null && mLoadingAnimator.isRunning()) {
      mLoadingAnimator.cancel();
      mLoadingAnimator.removeAllUpdateListeners();
      mLoadingAnimator = null;
    }

    if (mLineAnimator != null && mLineAnimator.isRunning()) {
      mLineAnimator.cancel();
      mLineAnimator.removeAllUpdateListeners();
      mLineAnimator = null;
    }
    mLoadingPointId = 0;
    lineProgressBegin = false;
    mLastPaintColor = -1;
  }

  /**
   * 线性progress
   */
  public void startLineProgress() {
    if (mLineAnimator != null && mLineAnimator.isRunning()) {
      return;
    }

    lineProgressBegin = true;
    float to = 0f;
    if (mLoadingProgressLineStart == LOADING_LINE_START_POSITION_LEFT) {
      to = mScreenWidth;
    } else if (mLoadingProgressLineStart == LOADING_LINE_START_POSITION_CENTER) {
      to = mScreenWidth * 1f / 2;
    }
    mLineAnimator = ValueAnimator.ofFloat(0, to);
    mLineAnimator.setRepeatCount(ValueAnimator.INFINITE);
    mLineAnimator.setDuration(500);
    mLineAnimator.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float value = (float) animation.getAnimatedValue();
        if (mLoadingProgressLineStart == LOADING_LINE_START_POSITION_LEFT) {
          mLineLeftEnd = value;
        } else if (mLoadingProgressLineStart == LOADING_LINE_START_POSITION_CENTER) {
          mLineCenter2Left = mScreenWidth / 2 - value;
          mLineCenter2Right = mScreenWidth / 2 + value;
        }
        postInvalidate();
      }
    });
    mLineAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationRepeat(Animator animation) {
        super.onAnimationRepeat(animation);
        mLastPaintColor = lineColorPosition;
        mLastPaint.setColor(lineColors[mLastPaintColor]);
        lineColorPosition = lineColorPosition == 1 ? 0 : 1;
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);

      }
    });
    mLineAnimator.start();
  }

  /**
   * 设置等待时长 120s | 180s
   * @param time
   */
  public synchronized void setConfigWaitTime(int time) {
    mWaitConfigTime = time;
    isProgressFlag = true;
    progress();
  }

  public LoadingView setRepeatCount(int count) {
    repeatCount = count;
    isProgressFlag = true;
    return this;
  }

  private synchronized void progress() {
    if (mProgressAnimator != null && mProgressAnimator.isAlive()) {
      return;
    }

    float allStep = CIRCLE / mWaitConfigTime; // waitConfigTime 走360
    final float oneInterval = 1000 / allStep; // 因为step = 1 则 1s/allStep

    mProgressAnimator = new Thread(new Runnable() {
      @Override
      public void run() {
        while (mProgressSweep <= CIRCLE && isProgressFlag) {
          mProgressSweep += 1;
          postInvalidate();

          if (repeatCount == -1) {
            // INFINITE
            if (mProgressSweep >= CIRCLE) {
              mProgressSweep = 0;
            }
          } else {
            if (animationCount < repeatCount) {
              if (mProgressSweep >= CIRCLE) {
                mProgressSweep = 0;
                animationCount++;
              }
            }
          }
          synchronized (lock) {
            try {
              lock.wait((int) oneInterval);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      }
    });

    mProgressAnimator.start();
  }

  public synchronized void updateProgressSweep(int progressSweep) {
    mProgressSweep = progressSweep * CIRCLE / mWaitConfigTime;
    postInvalidate();
  }

  public void release() {
    isProgressFlag = false;
    if (mProgressAnimator != null) {
      mProgressAnimator.interrupt();
      mProgressAnimator = null;
    }
    mProgressSweep = 0;
  }
}
