package com.one.framework.app.web.jsbridge.functions.image;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import com.one.framework.R;
import com.one.framework.utils.FileUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.InvalidParameterException;

public class ImageHelper {

  public static final int REQ_ALBUM_ACTIVITY = 100;
  public static final int REQ_CAMERA_ACTIVITY = 101;
  private static final String IMAGE_UNSPECIFIED = "image/*";

  private Activity mAcContext;
  private int mTargetWidth;
  private int mTargetHeight;
  private int mTargetQuality;

  private boolean mNeedResize = true;

  private IImg2StrListener mImage2StringResultListener;

  private File mOutPutFile;

  public ImageHelper(Activity ac) {
    if (ac == null) {
      throw new InvalidParameterException("the param should not be null");
    }
    mAcContext = ac;
  }

  public void handleActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case REQ_CAMERA_ACTIVITY:
        handleTakePicResult(requestCode, resultCode, data);
        break;
      case REQ_ALBUM_ACTIVITY:
        handleSelectPicResult(requestCode, resultCode, data);
        break;

      default:
        return;
    }
  }

  private BottomListMenu mAvatarMenu;

  public void handleImageChoose(int width, int height, int quality, IImg2StrListener l) {
    if (width > 0 && height > 0 && quality > 0 && quality <= 100 && l != null) {
      mTargetHeight = height;
      mTargetWidth = width;
      mTargetQuality = quality;
      mNeedResize = true;
      mImage2StringResultListener = l;

      showBottomMenu();
    }
  }

  public void handleImageChoose(IImg2StrListener listener) {
    if (listener != null) {

      mNeedResize = false;
      mImage2StringResultListener = listener;

      showBottomMenu();
    }
  }

  private void showBottomMenu() {
    if (mAvatarMenu == null) {
      mAvatarMenu = new BottomListMenu(mAcContext, mAcContext.findViewById(android.R.id.content),
          mAcContext.getResources().getStringArray(R.array.one_photos));
      mAvatarMenu.setListMenuListener(new BottomListMenu.ListMenuListener() {
        @Override
        public void onItemSelected(int position, String itemStr) {
          if (position == 0) {
            dispatchPickPictureIntent();
          } else if (position == 1) {
            dispatchTakePictureIntent();
          }
        }
      });
    }
    mAvatarMenu.showDialog();
  }

  /**
   * 拍照
   */
  private void dispatchTakePictureIntent() {
    mOutPutFile = FileUtils.getPhotoOutputFile(mAcContext.getPackageName());
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(mAcContext.getPackageManager()) != null) {
      if (mOutPutFile != null) {
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mOutPutFile));
        mAcContext.startActivityForResult(takePictureIntent, REQ_CAMERA_ACTIVITY);
      }
    } else {
      deleteTmpOutputFile();
    }
  }

  /**
   * 相册
   */
  private void dispatchPickPictureIntent() {
    Intent pickPictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
    pickPictureIntent.setType(IMAGE_UNSPECIFIED);
    pickPictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
    if (pickPictureIntent.resolveActivity(mAcContext.getPackageManager()) != null) {
      mAcContext.startActivityForResult(pickPictureIntent, REQ_ALBUM_ACTIVITY);
    }
  }

  protected void handleTakePicResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK) {
      if (mOutPutFile != null /* && mOutPutFile.length() > 0 */) {
        Uri uri = Uri.fromFile(mOutPutFile);
        if (uri != null) {
          processImgAndCallback(uri);
        } else {
          deleteTmpOutputFile();
        }
      }
    } else {
      deleteTmpOutputFile();
    }
  }

  protected void handleSelectPicResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK) {
      if (data != null && data.getData() != null) {
        Uri uri = data.getData();
        if (uri != null) {
          processImgAndCallback(uri);
        }
      }
    }
  }

  private void processImgAndCallback(Uri uri) {
    Bitmap bitmap;
    if (mNeedResize) {
      bitmap = resizeImageIfNecessary(uri);
    } else {
      bitmap = loadOriginImage(uri);
    }
    String result = encodeBitmapToString(bitmap);
    if (mImage2StringResultListener != null) {
      mImage2StringResultListener.onResult(result);
    }
    deleteTmpOutputFile();
    recycleBmp(bitmap);
  }


  private Bitmap loadOriginImage(final Uri uri) {
    Bitmap originBitmap = null;
    try {
      originBitmap = BitmapFactory.decodeStream(mAcContext.getContentResolver().openInputStream(uri));
    } catch (Exception e) {
      recycleBmp(originBitmap);
    } finally {
      deleteTmpOutputFile();
    }
    return originBitmap;
  }


  private Bitmap resizeImageIfNecessary(final Uri uri) {

    Bitmap srcBitmap = null;
    try {
      BitmapFactory.Options opts = new BitmapFactory.Options();
      opts.inJustDecodeBounds = true;

      BitmapFactory
          .decodeStream(mAcContext.getContentResolver().openInputStream(uri), null, opts);
      opts.inSampleSize = computeSampleSize(opts, -1, mTargetHeight * mTargetWidth);

      opts.inJustDecodeBounds = false;
      srcBitmap = BitmapFactory
          .decodeStream(mAcContext.getContentResolver().openInputStream(uri), null, opts);
    } catch (Exception e) {
      recycleBmp(srcBitmap);
    } finally {
      deleteTmpOutputFile();
    }

    return srcBitmap;
  }

  private String encodeBitmapToString(Bitmap bitmap) {
    String str = "";
    if (bitmap != null) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.JPEG, mTargetQuality, baos);
      byte[] b = baos.toByteArray();
      str = encodeByBase64(b);
    }
    return str;
  }

  public String encodeByBase64(byte[] data) {
    String base64 = "";
    if (data != null && data.length > 0) {
      try {
        base64 = Base64.encodeToString(data, Base64.NO_WRAP);
      } catch (Exception e) {
      }
    }
    return base64;
  }

  private void recycleBmp(Bitmap bitmap) {
    if (bitmap != null && !bitmap.isRecycled()) {
      bitmap.recycle();
      bitmap = null;
    }
  }

  private void deleteTmpOutputFile() {
    FileUtils.deleteFile(mOutPutFile);
    mOutPutFile = null;
  }

  private int computeSampleSize(BitmapFactory.Options options, int minSideLength,
      int maxNumOfPixels) {
    int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

    int roundedSize;
    if (initialSize <= 8) {
      roundedSize = 1;
      while (roundedSize < initialSize) {
        roundedSize <<= 1;
      }
    } else {
      roundedSize = (initialSize + 7) / 8 * 8;
    }

    return roundedSize;
  }

  private int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,
      int maxNumOfPixels) {
    double w = options.outWidth;
    double h = options.outHeight;

    int lowerBound =
        (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
    int upperBound = (minSideLength == -1) ? 128 : (int) Math
        .min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

    if (upperBound < lowerBound) {
      return lowerBound;
    }

    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
      return 1;
    } else if (minSideLength == -1) {
      return lowerBound;
    } else {
      return upperBound;
    }
  }


  public interface IImg2StrListener {

    public void onResult(String result);
  }
}

