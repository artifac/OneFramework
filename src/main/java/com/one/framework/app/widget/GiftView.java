package com.one.framework.app.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.request.target.SimpleTarget;
import com.one.framework.R;

public class GiftView extends android.support.v7.widget.AppCompatTextView {

  private LinearGradient mLinearGradient;
  private Context mContext;
  private int[] mColors = new int[]{Color.parseColor("#FFFF5322"), Color.parseColor("#FFFAB161")};
  private float[] mPosition = new float[]{0.785f, .925f};
  private Paint mPaint;
  private String mGift;
  private RectF mRectF;
  private int mViewWidth;
  private int mViewHeight;
  private float mOffset;
  private float mTextSize;
  private float mRectLeft;
  private float mTxtWidth;
  private int mTxtColor;
  private IPopShowListener mListener;
  private String mIconUrl;
  private int mPadding;
  private float vOffset;
  private float aOffset;
  private Bitmap mIcon;
  private int nineTopPadding;

  public GiftView(Context context) {
    this(context, null);
  }

  public GiftView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public GiftView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    mContext = context.getApplicationContext();
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GiftView);
    mViewWidth = a.getDimensionPixelOffset(R.styleable.GiftView_width, 0);
    mViewHeight = a.getDimensionPixelOffset(R.styleable.GiftView_height, 0);
    mTextSize = a.getDimensionPixelSize(R.styleable.GiftView_gift_text_size, 0);
    mTxtColor = Color.WHITE;
    a.recycle();
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint.setStyle(Style.FILL);
    mPaint.setColor(Color.LTGRAY);

    // 需测量文案的宽度
    mPadding = (int) TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
    vOffset = TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
    nineTopPadding = (int) TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.5f, getResources().getDisplayMetrics());
  }

  /**
   * 渐变颜色
   */
  public void setGradientColors(int[] colors) {
    mColors = colors;
    postInvalidate();
  }

  /**
   * 展示的文案
   */
  public void setGiftText(String gift, String color) {
    mGift = gift;
    if (!TextUtils.isEmpty(color) && color.startsWith("#")) {
      mTxtColor = Color.parseColor(color);
    }
    mOffset = getRectWidth();
    postInvalidate();
  }

  public void setGiftIcon(String url) {
    mIconUrl = url;
    if (!TextUtils.isEmpty(url)) {
//      Glide.with(mContext).load(url).asBitmap()
//          .encoder(new BitmapEncoder(Bitmap.CompressFormat.PNG, 100))
//          .into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap resource,
//                GlideAnimation<? super Bitmap> glideAnimation) {
//              mIcon = resource;
//              postInvalidate();
//            }
//          });
    }
  }

  public void setTimeOut(int timeOut) {
    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
      @Override
      public void run() {
        start();
      }
    }, timeOut * 1000);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    // 文案宽度
    mTxtWidth = getRectWidth();
    // 其实宽度
    mRectLeft = getMeasuredWidth() - mViewWidth;
    drawRect(canvas);

    if (!TextUtils.isEmpty(mGift)) {
      int restore = canvas.save();
      Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
      txtPaint.setTextSize(mTextSize);
      txtPaint.setTextAlign(Paint.Align.CENTER);
      txtPaint.setColor(mTxtColor);
      Path path = new Path();
      path.moveTo(mRectF.left, mRectF.centerY());
      path.lineTo(mRectF.right, mRectF.centerY());
      // 50 水平偏移量
      // 10 垂直偏移量
      canvas.drawTextOnPath(mGift, path, mPadding, vOffset, txtPaint);
      canvas.restoreToCount(restore);
    }

    Bitmap bmp = mIcon != null && !mIcon.isRecycled() ? mIcon
        : BitmapFactory.decodeResource(mContext.getResources(), R.drawable.share_gift_view_icon);
    if (bmp != null && bmp.getWidth() >= mViewWidth && bmp.getHeight() >= mViewHeight && !bmp
        .isRecycled()) {
      Matrix matrix = new Matrix();
      matrix.postScale(0.8f, 0.8f);
      bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
    }
    Paint iconPaint = new Paint();
    float left = (mViewWidth - bmp.getWidth()) / 2;
    float top = (mRectF.height() - bmp.getHeight()) / 2;
    canvas.drawBitmap(bmp, mRectF.left + left, top, iconPaint);
  }

  /**
   */
  private void drawRect(Canvas canvas) {
    if (mColors != null) {
      mLinearGradient = new LinearGradient(0, getMeasuredWidth(), getMeasuredWidth(), 0, mColors,
          mPosition, TileMode.REPEAT);
      mPaint.setShader(mLinearGradient);
    }
    mRectF = new RectF(mRectLeft - mOffset - mPadding, 0, mRectLeft + mViewWidth - mPadding,
        mViewHeight);
    Rect rect = new Rect((int) mRectF.left - mViewWidth / 3, 0, (int) mRectF.right + mViewWidth / 3,
        getMeasuredHeight() - nineTopPadding);
    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gift_view_background);
    byte[] chunk = bitmap.getNinePatchChunk();
    boolean result = NinePatch.isNinePatchChunk(chunk);
    NinePatchDrawable patchy = new NinePatchDrawable(getResources(), bitmap, chunk, rect, "");
    patchy.setBounds(rect);

    patchy.draw(canvas);
    canvas.drawRoundRect(mRectF, mViewWidth / 2, mViewHeight / 2, mPaint);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      float x = event.getX();
      float y = event.getY();
      if (mRectF != null && mRectF.contains(x, y)) {
        return true;
      }
    }
    switch (event.getAction()) {
      case MotionEvent.ACTION_UP: {
        float x = event.getX();
        float y = event.getY();
        if (mRectF.contains(x, y)) {
          if (mListener != null) {
            mListener.onPopShow(true);
          }
          setVisibility(INVISIBLE);
        }
        return true;
      }
    }
    return super.onTouchEvent(event);
  }


  @Override
  public void setVisibility(int visibility) {
    if (visibility == VISIBLE) {
      setAlpha(1f);
    }
    super.setVisibility(visibility);
  }

  /**
   * 收起动画
   */
  private void start() {
    if (getVisibility() != View.VISIBLE) {
      return;
    }
    ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f);
    animator.setDuration(300);
    animator.setInterpolator(new AccelerateInterpolator());
    animator.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        aOffset = (float) animation.getAnimatedValue();
        mOffset = mOffset * aOffset;
        mPosition = new float[]{.7465f, 1f};
        mTxtColor = blendColors(mTxtColor, Color.TRANSPARENT, animation.getAnimatedFraction());
        postInvalidate();
      }
    });
    animator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        setVisibility(VISIBLE);
      }

      @Override
      public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);

      }
    });
    animator.start();
  }

  public void setIPopShow(IPopShowListener listener) {
    mListener = listener;
  }

  private float getRectWidth() {
    if (!TextUtils.isEmpty(mGift)) {
      TextPaint paint = getPaint();
      return paint.measureText(mGift) + mPadding;
    }
    return 0;
  }

  /**
   * 颜色变化
   */
  private int blendColors(int bottom, int top, float ratio) {
    float rr = Math.max(0F, Math.min(ratio, 1F));
    float inverseRatio = 1f - rr;
    float r = Color.red(top) * rr + Color.red(bottom) * inverseRatio;
    float g = Color.green(top) * rr + Color.green(bottom) * inverseRatio;
    float b = Color.blue(top) * rr + Color.blue(bottom) * inverseRatio;
    float a = Color.alpha(top) * rr + Color.alpha(bottom) * inverseRatio;
    return Color.argb((int) a, (int) r, (int) g, (int) b);
  }

  public float getViewWidth() {
    if (mRectF == null) {
      return 0;
    }
    return mRectF.width();
  }

  public interface IPopShowListener {

    void onPopShow(boolean isReverse);
  }

  public void release() {
    if (mIcon != null) {
      mIcon.recycle();
      mIcon = null;
    }
  }
}
