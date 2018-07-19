package com.one.framework.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.one.framework.R;

/**
 * Created by ludexiang on 2018/5/3.
 */

public class ProgressView extends View {

  private int mProgressFrom;
  private int mProgressTo;
  private int mProgressWidth;
  private int mProgressBgColor;
  private int mProgressRadius;
  private Paint mPaint;
  private RectF mRectF = new RectF();
  private int mProgressDefaultColor = Color.parseColor("#f05b48");

  /**
   * 填充颜色
   */
  private int[] mProgressForeground;
  private float[] mProgressPosition = new float[]{0.5f, 0.895f};

  private float mSweepAngle;
  private Object lock = new Object();
  private Paint mTickPaint;
  // 刻度RectF
  private RectF mTickRectF = new RectF();

  private int mMaxTickCount;
  private int mCurrentProgress = 70;

  private float[] mPathPos = new float[2];
  private float[] mPathTan = new float[2];

  public ProgressView(Context context) {
    this(context, null);
  }

  public ProgressView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
    mProgressFrom = a.getInteger(R.styleable.ProgressView_progress_from, 160);
    mProgressTo = a.getInteger(R.styleable.ProgressView_progress_to, 0);
    mProgressWidth = a.getDimensionPixelOffset(R.styleable.ProgressView_progress_width, 5);
    mProgressBgColor = a.getColor(R.styleable.ProgressView_progress_bg, Color.GRAY);
    mProgressRadius = a.getDimensionPixelOffset(R.styleable.ProgressView_progress_radius, 0);
    mMaxTickCount = a.getInteger(R.styleable.ProgressView_progress_max_tick, 100);
    a.recycle();

    mProgressForeground = getResources().getIntArray(R.array.progress_colors);
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint.setStrokeWidth(mProgressWidth);
    mPaint.setStyle(Style.STROKE);
    mPaint.setColor(mProgressBgColor);
    // 👇的方法可以设置两边圆角
//    mPaint.setStrokeCap(Cap.ROUND);

    mTickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mTickPaint.setStyle(Style.STROKE);
    mTickPaint.setColor(mProgressBgColor);
    new Thread(new Runnable() {
      @Override
      public void run() {
        float sweepAngle = (mCurrentProgress * 1f / mMaxTickCount) * mProgressTo;
        while (mSweepAngle < sweepAngle) {
          mSweepAngle += 2;
          postInvalidate();

          synchronized (lock) {
            try {
              lock.wait(2000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }).start();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

//    tick 背景
    mTickRectF.set(0, 0, getWidth(), getHeight());
    float degree = 1; // 每一个刻度的角度
    // mProgressTo被mMaxTickCount平分 因为degree = 1 故减去 degree 则表示tick间间距
    float space = mProgressTo * 1f / mMaxTickCount - degree;
    for (int i = 0; i <= mMaxTickCount; i++) {
      if (i == 0 || i == mMaxTickCount) {
        mTickPaint.setStrokeWidth(mProgressWidth + mProgressRadius);
      } else if (i % 10 == 0) {
        mTickPaint.setStrokeWidth(mProgressWidth + 10);
      } else {
        mTickPaint.setStrokeWidth(mProgressWidth);
      }
      canvas.drawArc(mTickRectF, mProgressFrom + i * (degree + space), degree, false, mTickPaint);
    }
    // progress
    mRectF.set(mProgressRadius, mProgressRadius, getWidth() - mProgressRadius, getHeight() - mProgressRadius);
    Path path = new Path();
    path.addArc(mRectF, mProgressFrom, mProgressTo);
    canvas.drawPath(path, mPaint); // 背景

    int restoreCount = canvas.save();
    // 前景
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setStrokeWidth(mProgressWidth);
    paint.setStyle(Style.STROKE);
//    paint.setStrokeCap(Cap.SQUARE);
    float centerX = getWidth() * 1f / 2;
    float centerY = getHeight() * 1f / 2;
    SweepGradient gradient = new SweepGradient(centerX, centerY, mProgressForeground, mProgressPosition);
    //旋转 不然是从0度开始渐变
    Matrix matrix = new Matrix();
    matrix.postRotate(-mProgressFrom);
    gradient.setLocalMatrix(matrix);
    paint.setShader(gradient);

    canvas.drawArc(mRectF, mProgressFrom, mSweepAngle, false, paint);

    // 画当前刻度刻度随着当前角度走
    int index = (int) ((mSweepAngle * 1f / mProgressTo) * mMaxTickCount);
    for (int i = 0; i <= index; i++) {
      if (i == 0 || i == mMaxTickCount) {
        paint.setStrokeWidth(mProgressWidth + mProgressRadius);
      } else if (i % 10 == 0) {
        paint.setStrokeWidth(mProgressWidth + 10);
      } else {
        paint.setStrokeWidth(mProgressWidth);
      }
      canvas.drawArc(mTickRectF, mProgressFrom + i * (degree + space), degree, false, paint);
    }
    canvas.restoreToCount(restoreCount);

    // draw 当前进度
    Paint p = new Paint();
    p.setStrokeWidth(mProgressWidth);
    p.setColor(Color.WHITE);
    p.setStyle(Style.FILL);
    p.setAntiAlias(true);
    Path circlePath = new Path();
    circlePath.addArc(mRectF, mProgressFrom, mProgressTo);
    PathMeasure circleMeasure = new PathMeasure(circlePath, false);
    circleMeasure.getPosTan(circleMeasure.getLength() * (mSweepAngle / mProgressTo), mPathPos, mPathTan);
//    canvas.drawPath(circlePath, p);
//    canvas.drawPoint();
//    canvas.drawCircle();

    canvas.drawCircle(mPathPos[0], mPathPos[1], 30f, p);

    p.setStrokeWidth(8f);
    p.setColor(Color.RED);
    p.setStyle(Style.STROKE);
    canvas.drawCircle(mPathPos[0], mPathPos[1], 25f, p);

    synchronized (lock) {
      lock.notifyAll();
    }
  }

  public void setProgress(int progress) {
    mCurrentProgress = progress;
    postInvalidate();
  }
}
