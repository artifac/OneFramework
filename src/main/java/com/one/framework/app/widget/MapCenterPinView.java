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
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import com.one.framework.R;
import com.one.framework.app.widget.base.IMapCenterPinView;
import com.one.map.IMap;
import com.one.map.location.LocationProvider;
import com.one.map.model.Address;
import com.one.map.model.LatLng;

/**
 * Created by ludexiang on 2018/6/1.
 * 地图中心点View GEO
 */

public class MapCenterPinView extends AppCompatImageView implements IMapCenterPinView {

  private int mPinLineWidth;
  private int mPinLineColor;
  private int mPinLineHeight;

  private int mPinCircleSize;
  private int mPinCircleColor;
  private int mPinInnerCircleColor;

  private RectF outterRect = new RectF();
  private float lineYChange;
  private byte[] lock = new byte[0];
  private float circleSweepAngle;
  private float innerCircleRadius = 10;
  private boolean changeRadiusColor = false;

  private IMap mMap;

  private ValueAnimator mRadius;
  private Handler mHandler;

  private boolean isReverseGeo = true;

  public MapCenterPinView(Context context) {
    this(context, null);
  }

  public MapCenterPinView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MapCenterPinView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MapCenterPinView);
    mPinLineWidth = a.getDimensionPixelOffset(R.styleable.MapCenterPinView_pin_width, 5);
    mPinLineHeight = a.getDimensionPixelOffset(R.styleable.MapCenterPinView_pin_height, 30);
    mPinLineColor = a.getColor(R.styleable.MapCenterPinView_pin_color, Color.GREEN);
    mPinCircleSize = a.getDimensionPixelOffset(R.styleable.MapCenterPinView_pin_circle_size, 70);
    mPinCircleColor = a.getColor(R.styleable.MapCenterPinView_pin_circle_color, Color.GREEN);
    mPinInnerCircleColor = a
        .getColor(R.styleable.MapCenterPinView_pin_inner_circle_color, Color.WHITE);
    a.recycle();

    HandlerThread heightChange = new HandlerThread("HEIGHT_CHANGE");
    heightChange.start();
    mHandler = new Handler(heightChange.getLooper()) {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
          case 0: {
            while (lineYChange < mPinLineHeight) {
              lineYChange += 1;
              postInvalidate();
              synchronized (lock) {
                try {
                  lock.wait(10);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              }
            }
            toggleArc();
            break;
          }
          case 1: {
            if (mRadius != null && mRadius.isRunning()) {
              mRadius.cancel();
              mRadius.removeAllListeners();
              mRadius.removeAllUpdateListeners();
              mRadius = null;
            }
            changeRadiusColor = false;
            innerCircleRadius = 10;
            postInvalidate();
            break;
          }
        }
      }
    };
    mHandler.sendEmptyMessage(0);
  }

  @Override
  public void setMap(IMap map) {
    mMap = map;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
//    canvas.drawBitmap();

    Paint paint = new Paint();
    paint.setColor(mPinLineColor);
    paint.setAntiAlias(true);
    paint.setStrokeCap(Cap.SQUARE);
    paint.setStrokeWidth(mPinLineWidth);
    float lineStartY = getHeight();
    float lineStopY = lineStartY - lineYChange;
    canvas.drawLine(getWidth() / 2, lineStartY, getWidth() / 2, lineStopY, paint);
    if (lineYChange == mPinLineHeight) {
      drawCircle(canvas);
    }
  }

  private void drawCircle(Canvas canvas) {
    outterRect.set(0, 0, mPinCircleSize, mPinCircleSize);

    Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    p.setColor(changeRadiusColor ? mPinInnerCircleColor : mPinCircleColor);
    p.setStyle(Style.FILL);
    canvas.drawArc(outterRect, 90f, circleSweepAngle, true, p);

    p.setColor(changeRadiusColor ? mPinCircleColor : mPinInnerCircleColor);
    canvas.drawCircle(outterRect.centerX(), outterRect.centerY(), innerCircleRadius, p);
  }

  private void toggleArc() {
    ValueAnimator sweepAngle = ValueAnimator.ofFloat(10f, 360f);
    sweepAngle.setDuration(500);
    sweepAngle.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        circleSweepAngle = (Float) animation.getAnimatedValue();
        postInvalidate();
      }
    });
    sweepAngle.start();
  }

  private void toggleLoading() {
    if (mRadius != null && mRadius.isRunning()) {
      return;
    }
    mRadius = ValueAnimator.ofFloat(0f, outterRect.width() / 2);
    mRadius.setDuration(400);
    mRadius.setRepeatCount(-1);
    mRadius.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        innerCircleRadius = (Float) animation.getAnimatedValue();
        postInvalidate();
      }
    });
    mRadius.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationRepeat(Animator animation) {
        super.onAnimationRepeat(animation);
        changeRadiusColor = !changeRadiusColor;
      }
    });
    mRadius.start();
  }

  /**
   * move map toggle pin jump anim
   * 地址翻转，通过LatLng 翻转出地址
   */
  @Override
  public void reverseGeoAddress(LatLng latLng) {
    if (isReverseGeo) {
      toggleLoading();
      mMap.geo2Address(latLng);
      Address address = LocationProvider.getInstance().getLocation();
      if (address != null) {
        mMap.poiNearByWithCity(address.mAdrLatLng, address.mCity);
      }
    }
  }

  @Override
  public void stop() {
    mHandler.sendEmptyMessageDelayed(1, 1000);
  }

  @Override
  public void isToggleLoading(boolean reverse) {
    isReverseGeo = reverse;
  }

  @Override
  public void hide(boolean isHide) {
    setVisibility(isHide ? View.GONE : View.VISIBLE);
    isReverseGeo = !isHide;
  }

  @Override
  public View getView() {
    return this;
  }
}
