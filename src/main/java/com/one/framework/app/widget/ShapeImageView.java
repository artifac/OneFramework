package com.one.framework.app.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.one.framework.R;
import com.one.framework.log.Logger;
import com.one.framework.utils.UIUtils;

public class ShapeImageView extends AppCompatImageView {

  private Context mContext;

  /**
   * android.widget.ImageView
   */
  public static final int TYPE_NONE = 0;
  /**
   * 圆形
   */
  public static final int TYPE_CIRCLE = 1;
  /**
   * 圆角矩形
   */
  public static final int TYPE_ROUNDED_RECT = 2;

  private static final int DEFAULT_TYPE = TYPE_NONE;
  private static final int DEFAULT_BORDER_COLOR = Color.TRANSPARENT;
  private static final int DEFAULT_BORDER_WIDTH = 0;
  private static final int DEFAULT_RECT_ROUND_RADIUS = 0;
  private IClickListener mListener;

  private int mType;
  private int mBorderColor;
  private int mBorderWidth;
  private int mRectRoundRadius;

  private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private boolean isFullRadius = true;
  private float[] mSpecialRadii;

  public ShapeImageView(Context context) {
    this(context, null);
  }

  public ShapeImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ShapeImageView(Context context, AttributeSet attrs, int defaultValue) {
    super(context, attrs, defaultValue);
    mContext = context;

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShapeImageView);
    mType = a.getInt(R.styleable.ShapeImageView_imageType, DEFAULT_TYPE);
    mBorderColor = a.getColor(R.styleable.ShapeImageView_borderColor, DEFAULT_BORDER_COLOR);
    mBorderWidth = a.getDimensionPixelSize(R.styleable.ShapeImageView_borderWidth,
        UIUtils.dip2pxInt(mContext, DEFAULT_BORDER_WIDTH));
    mRectRoundRadius = a.getDimensionPixelSize(R.styleable.ShapeImageView_rectRoundRadius,
        UIUtils.dip2pxInt(mContext, DEFAULT_RECT_ROUND_RADIUS));
    a.recycle();

    mSpecialRadii = new float[]{
        mRectRoundRadius, mRectRoundRadius,
        mRectRoundRadius, mRectRoundRadius,
        mRectRoundRadius, mRectRoundRadius,
        mRectRoundRadius, mRectRoundRadius
    };

  }

  /**
   * 上面圆角 下面直角
   */
  public void setSpecialRadius(boolean fullRadius) {
    isFullRadius = fullRadius;
    if (!isFullRadius) {
      mSpecialRadii = new float[]{
          mRectRoundRadius, mRectRoundRadius, // 左上
          mRectRoundRadius, mRectRoundRadius, // 右上
          0, 0,// 右下
          0, 0 // 左下
      };
    }
    postInvalidate();
  }

  private Bitmap getBitmap(Drawable drawable) {
    if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    } else if (drawable instanceof ColorDrawable) {
      Rect rect = drawable.getBounds();
      int width = rect.right - rect.left;
      int height = rect.bottom - rect.top;
      int color = ((ColorDrawable) drawable).getColor();
      Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);
      canvas.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
      return bitmap;
    }
    return null;
  }

  private Bitmap round(Bitmap bitmap, int width, int height, int radius, boolean recycleSource) {
    if (width != 0 && height != 0 && radius > 0 && bitmap != null) {
      Bitmap ret = null;

      try {
        ret = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      } catch (OutOfMemoryError var10) {

      }

      if (ret == null) {
        return null;
      } else {
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, width, height);
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xff424242);
        if (mType == TYPE_ROUNDED_RECT) {
          canvas.drawRoundRect(rectF, (float) radius, (float) radius, paint);
        } else if (mType == TYPE_CIRCLE) {
          int min = Math.min(width, height);
          canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        }
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        if (bitmap.isRecycled()) {
          return null;
        }
        canvas.drawBitmap(bitmap, rect, rect, paint);
        if (recycleSource) {
          clear(bitmap);
        }

        return ret;
      }
    } else {
      return bitmap;
    }
  }

  public static void clear(Bitmap bitmap) {
    if (bitmap != null && Build.VERSION.SDK_INT < 14) {
      bitmap.recycle();
    }
  }

  protected void onDraw(Canvas canvas) {
    Drawable drawable = getDrawable();
    if (null != drawable && isFullRadius) {
      Bitmap bmp = getBitmap(drawable);
      if (bmp == null) {
        drawWithType(canvas);
        super.onDraw(canvas);
        drawBorder(canvas, this.getWidth(), this.getHeight());
        return;
      }

      Bitmap bitmap = resizeBitmap(bmp);
      if (bitmap == null) {
        return;
      }

      Bitmap b = round(bitmap, this.getWidth(), this.getHeight(),
          mType == TYPE_CIRCLE ? getWidth() / 2
              : mType == TYPE_ROUNDED_RECT
                  ? mRectRoundRadius : 0, false);
      if (b == null) {
        return;
      }
      ScaleType type = getScaleType();
      if (type == ScaleType.CENTER) {
        super.onDraw(canvas);
        return;
      }
      Rect rect = new Rect(0, 0, getWidth(), getHeight());
      mPaint.reset();
      mPaint.setAntiAlias(true);
      mPaint.setFilterBitmap(true);
      mPaint.setDither(true);
      canvas.drawBitmap(b, rect, rect, this.mPaint);
    } else {
      Path path = new Path();
      path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), mSpecialRadii, Direction.CW);
      path.close();
      canvas.clipPath(path);
      super.onDraw(canvas);
    }

    drawBorder(canvas, this.getWidth(), this.getHeight());
  }

  private void drawWithType(Canvas canvas) {
    Path path = new Path();
    if (mType == TYPE_CIRCLE) {
      path.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, Direction.CW);
    } else {
      RectF rect = new RectF(0, 0, getWidth(), getHeight());
      path.addRoundRect(rect, mRectRoundRadius, mRectRoundRadius, Direction.CW);
    }

    path.close();
    canvas.clipPath(path);
  }

  private void drawBorder(Canvas canvas, int width, int height) {
    if (this.mBorderWidth != 0) {
      Paint mBorderPaint = new Paint();
      mBorderPaint.setStyle(Paint.Style.STROKE);
      mBorderPaint.setAntiAlias(true);
      mBorderPaint.setColor(this.mBorderColor);
      mBorderPaint.setStrokeWidth((float) this.mBorderWidth);
      canvas.drawCircle((float) (width >> 1), (float) (height >> 1), (float) (width >> 1),
          mBorderPaint);
    }
  }


  public void setBorderWidth(float border) {
    mBorderWidth = UIUtils.dip2pxInt(mContext, border);
    postInvalidate();
  }

  private Bitmap resizeBitmap(Bitmap bit) {
    Bitmap BitmapOrg = bit;
    int width = bit.getWidth();
    int height = bit.getHeight();
    if (width > 0 && height > 0) {
      int newWidth = this.getWidth();
      int newHeight = this.getHeight();
      float scaleWidth = (float) newWidth / (float) width;
      float scaleHeight = (float) newHeight / (float) height;
      Matrix matrix = new Matrix();
      matrix.postScale(scaleWidth, scaleHeight);
      Bitmap resizedBitmap = null;
      try {
        resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
      } catch (Exception var12) {
      }
      return resizedBitmap;
    } else {
      return null;
    }
  }

  public void loadImageByUrl(final ViewGroup parent, final String imgUrl, final String schema) {
    loadImageByUrl(parent, imgUrl, schema, true);
  }

  public void loadImageByUrl(final ViewGroup parent, final String imgUrl, final String schema,
      final boolean clickable) {
    if (TextUtils.isEmpty(imgUrl) || TextUtils.isEmpty(schema)) {
      return;
    }
    if (!TextUtils.isEmpty(imgUrl) && !TextUtils.isEmpty(schema)) {
      if (parent != null) {
        parent.setVisibility(View.VISIBLE);
      }
      setEnabled(false);
      if (mContext == null || ((Activity) mContext).isFinishing()) {
        return;
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && ((Activity) mContext)
          .isDestroyed()) {
        return;
      }
      try {
        Glide.with(mContext).load(imgUrl).listener(new RequestListener<Drawable>() {
          @Override
          public boolean onLoadFailed(@Nullable GlideException e, Object model,
              Target<Drawable> target,
              boolean isFirstResource) {
            return false;
          }

          @Override
          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
              DataSource dataSource, boolean isFirstResource) {
            if (resource != null) {
              setImageDrawable(resource);
            }
            if (clickable) {
              setEnabled(true);
              setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                  if (mListener != null) {
//                  mListener.onClick(WebUtils.link(schema));
                  }
                }
              });
            }
            return true;
          }
        }).into(this);
      } catch (Exception e) {
        Logger.e("ldx", "occur exception");
      }

    }
  }

  public void setListener(IClickListener listener) {
    mListener = listener;
  }

  @Keep
  public interface IClickListener {

    void onClick(String schema);
  }

}
