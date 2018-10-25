package com.one.framework.app.web.jsbridge.functions.image;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.one.framework.BuildConfig;
import com.one.framework.R;
import com.one.framework.utils.FileUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PicUploadActivity extends FragmentActivity {

  public static final String TAG = "PicUploadActivity";

  public static final String FINAL_PIC_MSG_KEY = "pic_msg_key";
  // js pass upload url key
  public static final String UPLOAD_URL_KEY = "UPLOAD_URL_KEY";
  // js other params key
  public static final String DATA_PARAMS_KEY = "DATA_PARAMS_KEY";

  public static final int REQUEST_CODE_SYSTEM_RESIZE_IMAGE = 104;

  private static ImageUploadCallback sImageUploadCallback;

  private static ImageCallback sImageCallback;

  // photo select listview
  private ListView mListView;
  private TextView mCancelTextView;
  // album request code
  private static final int REQ_ALBUM_ACTIVITY = 100;
  // camera request code
  private static final int REQ_CAMERA_ACTIVITY = 101;
  // crop request code
  private static final int REQ_CROP_ACTIVITY = 102;
  public static final String IMAGE_UNSPECIFIED = "image/*";
  // 上传地址
  private String mUploadUrl;
  // 不确定附带参数
  private String mOtherData;
  // 拍照文件
  private File mCameraFile;
  // 最终剪裁文件
  private File mOutPutFile;
  private String mOutPutFilePath;

  private RelativeLayout mUploadSelect;
  //
  private String type;

  private String width;
  private String height;
  private String quality = "";
  private boolean isNeedCut;
  private ProgressDialog progressDialog;

  private Handler mHandler;

  private String mImageType;
  private File mCropFile = null;

  public static void setImageUploadCallback(ImageUploadCallback callback) {
    sImageUploadCallback = callback;
  }

  public static void setImageCallback(ImageCallback callback) {
    sImageCallback = callback;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      mOutPutFilePath = savedInstanceState.getString("mOutPutFile");
    }
    mHandler = new Handler();
    initData();
  }

  @Override
  public void finish() {
    super.finish();
    sImageUploadCallback = null;
    sImageCallback = null;
  }

  private void initData() {
    Intent intent = getIntent();
    if (intent != null) {
      type = intent.getStringExtra("type");
      mUploadUrl = intent.getStringExtra(UPLOAD_URL_KEY);
      if (TextUtils.isEmpty(mUploadUrl)) {
//                finish();
      } else {
        Uri uri = Uri.parse(mUploadUrl);
        if (uri.isRelative()) {
          finish();
        }
      }
      if (BuildConfig.DEBUG) {
        Log.d("WebView", "url -> [" + mUploadUrl + "]");
      }
      mOtherData = intent.getStringExtra(DATA_PARAMS_KEY);
      width = intent.getStringExtra("width");
      height = intent.getStringExtra("height");
      quality = intent.getStringExtra("quality");
      isNeedCut = intent.getBooleanExtra("cut", false);
    }
    mOutPutFile = ImageFileConfig.getPhotoOutputFile();
    if (mOutPutFile != null) {
      mOutPutFilePath = mOutPutFile.getAbsolutePath();
    }
    if (type.equals("camera")) {

      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
          != PackageManager.PERMISSION_GRANTED || !isCameraCanUse()) {
        // 摄像头权限还未得到用户的同意
        if (sImageCallback != null) {
          sImageCallback.onPermissionFail();
        }
        finish();
      } else {
        // 摄像头权限以及有效，显示摄像头预览界面
        mCameraFile = ImageFileConfig.getPhotoOutputFile();
        dispatchTakePictureIntent();
      }


    } else if (type.equals("photo")) {
      intent = new Intent(Intent.ACTION_PICK, null);
      intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
      startActivityForResult(intent, REQ_ALBUM_ACTIVITY);
    } else {
      overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
      setContentView(R.layout.one_image_pick_dialog_layout);
      initView();
    }
  }

  private void initView() {
    mListView = (ListView) findViewById(R.id.pic_menu_list);
    mUploadSelect = (RelativeLayout) findViewById(R.id.bts_upload_rela);
    ArrayAdapter<String> mListAdapter = new ArrayAdapter<String>(
        this, R.layout.one_pic_upload_list, getResources().getStringArray(R.array.one_photos));
    mListView.setAdapter(mListAdapter);
    mListView.setOnItemClickListener(mItemClickListener);
    mCancelTextView = (TextView) findViewById(R.id.cancel_text);
    mCancelTextView.setOnClickListener(cancelClickListener);
  }

  public static boolean isCameraCanUse() {
    boolean canUse = true;
    Camera mCamera = null;
    try {
      mCamera = Camera.open(0);
      mCamera.setDisplayOrientation(90);
    } catch (Exception e) {
      canUse = false;
    }
    if (canUse) {
      mCamera.release();
      mCamera = null;
    }
    return canUse;
  }

  private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      Intent intent = null;
      switch (position) {
        case 0:
          if (ActivityCompat.checkSelfPermission(PicUploadActivity.this, Manifest.permission.CAMERA)
              != PackageManager.PERMISSION_GRANTED || !isCameraCanUse()) {
            // 摄像头权限还未得到用户的同意
            if (sImageCallback != null) {
              sImageCallback.onPermissionFail();
            }
            finish();
          } else {
            mCameraFile = ImageFileConfig.getPhotoOutputFile();
            dispatchTakePictureIntent();
          }
          break;
        case 1:
          intent = new Intent(Intent.ACTION_PICK, null);
          intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
          try {
            startActivityForResult(intent, REQ_ALBUM_ACTIVITY);
          } catch (Exception e) {
            e.printStackTrace();
          }

          break;
        default:
          break;
      }
    }
  };

  private View.OnClickListener cancelClickListener = new View.OnClickListener() {

    @Override
    public void onClick(View v) {
      if (sImageCallback != null) {
        sImageCallback.onCancel();
      }
      finish();
    }
  };

  private void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
      if (mCameraFile != null) {
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile));
        startActivityForResult(takePictureIntent, REQ_CAMERA_ACTIVITY);
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case REQ_CAMERA_ACTIVITY:
        if (resultCode == RESULT_OK) {
          if (mCameraFile != null && mCameraFile.length() > 0) {

            if (TextUtils.isEmpty(width) || TextUtils.isEmpty(height) || "0".equals(width) || "0"
                .equals(height)) {

              final Uri imageUri = Uri.fromFile(mCameraFile);

              if (isNeedCut) {
                startSystemCropActivity(imageUri);
                return;
              }
              handlePic(imageUri);

            } else {
              Intent intent = new Intent(this, CropActivity.class);
              intent.setData(Uri.fromFile(mCameraFile));
              intent.putExtra("width", width);
              intent.putExtra("height", height);
              intent.putExtra("output", mOutPutFilePath);
              startActivityForResult(intent, REQ_CROP_ACTIVITY);
            }
          } else {
            FileUtils.deleteFile(mCameraFile);
            if (sImageCallback != null) {
              sImageCallback.onFail();
            }
            finish();
          }
        } else {
          FileUtils.deleteFile(mCameraFile);
          if (sImageCallback != null) {
            if (ActivityCompat
                .checkSelfPermission(PicUploadActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || !isCameraCanUse()) {
              // 摄像头权限还未得到用户的同意
              sImageCallback.onPermissionFail();
            } else {
              sImageCallback.onCancel();
            }
          }
          finish();
        }
        break;
      case REQ_ALBUM_ACTIVITY:
        if (resultCode == RESULT_OK) {
          if (mOutPutFile == null) {
            finish();
          }

          if (TextUtils.isEmpty(width) || TextUtils.isEmpty(height) || "0".equals(width) || "0"
              .equals(height)) {

            final Uri imageUri = data.getData();
            String url = FileUtils.getPath(this, imageUri);
            File mSaveFile = FileUtils.getPhotoOutputFile();
            FileUtils.copyFile(url, mSaveFile.getAbsolutePath());
            if (isNeedCut) {
              startSystemCropActivity(Uri.fromFile(mSaveFile));
              return;
            }
            handlePic(imageUri);

          } else {
            if (data != null) {
              data.setClass(this, CropActivity.class);
              data.putExtra("width", width);
              data.putExtra("height", height);
              data.putExtra("output", mOutPutFilePath);
              startActivityForResult(data, REQ_CROP_ACTIVITY);
            }
          }

        } else {
          if (sImageCallback != null) {
            sImageCallback.onCancel();
          }
          finish();
        }
        break;
      case REQ_CROP_ACTIVITY:
        if (resultCode == RESULT_OK && data != null) {
          if (mUploadSelect != null) {
            mUploadSelect.setVisibility(View.GONE);
          }
          String finalPath = data.getStringExtra(CropActivity.CROP_PIC_PASS_KEY);

          if (!TextUtils.isEmpty(mUploadUrl)) {
            uploadPic(finalPath);
          } else {
            if (sImageUploadCallback != null) {
              sImageUploadCallback.onSuccess(bitmapToBase64(finalPath));
            }

            if (sImageCallback != null) {
              sImageCallback.onSuccess(bitmapToBase64(finalPath), mImageType);
            }
            finish();
          }

        } else {
          if (sImageCallback != null) {
            sImageCallback.onFail();
          }
          finish();
        }
        break;
      case RESULT_CANCELED:
        if (sImageCallback != null) {
          sImageCallback.onCancel();
        }
        finish();
        break;

      case REQUEST_CODE_SYSTEM_RESIZE_IMAGE:
        if (resultCode == RESULT_OK) {
          final Uri imageUri = Uri.fromFile(mCropFile);
          ;
          new Thread(new Runnable() {
            @Override
            public void run() {

              try {
                final String base64 = decodeSampledBitmap(imageUri, 600, 600);
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    if (sImageCallback != null) {
                      sImageCallback.onSuccess(base64, mImageType);
                    }
                    finish();
                  }
                });
              } catch (Exception e) {
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    if (sImageCallback != null) {
                      sImageCallback.onFail();
                    }
                    finish();
                  }
                });
              }
            }
          }).start();
        } else {
          if (sImageCallback != null) {
            sImageCallback.onCancel();
          }
          finish();
        }
        break;

      default:
        return;
    }
  }

  private void handlePic(final Uri imageUri) {
    new Thread(new Runnable() {
      @Override
      public void run() {

        try {
          final String base64 = decodeSampledBitmap(imageUri, 600, 600);
          mHandler.post(new Runnable() {
            @Override
            public void run() {
              if (sImageCallback != null) {
                sImageCallback.onSuccess(base64, mImageType);
              }
              finish();
            }
          });
        } catch (Exception e) {
          mHandler.post(new Runnable() {
            @Override
            public void run() {
              if (sImageCallback != null) {
                sImageCallback.onFail();
              }
              finish();
            }
          });
        }
      }
    }).start();
  }
//
//    private void startSystemCropActivity(Uri imageUri) {
//        Intent intent = new Intent();
//        intent.setAction("com.android.camera.action.CROP");
//        intent.setDataAndType(imageUri, "image/*");
//        intent.putExtra("return-data", false);  //是否返回bitmap
//        startActivityForResult(intent, REQUEST_CODE_SYSTEM_RESIZE_IMAGE);
//    }

  /**
   * 打开裁剪界面
   */
  private void startSystemCropActivity(Uri uri) {
    try {
      Uri targetUri = uri;
      Intent intent = new Intent("com.android.camera.action.CROP");

      String providerName = "com.one.framework.fileprovider";
//      if (!TextUtils.isEmpty(BuildConfig.PRODUCT_PREFIX)) {
//        providerName = providerName + "." + BuildConfig.PRODUCT_PREFIX;
//      }

      if (android.os.Build.VERSION.SDK_INT >= 24) {
        File file = new File(uri.getPath());
        targetUri = FileProvider.getUriForFile(this, providerName, file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(targetUri, "image/*");
      } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
        String url = FileUtils.getPath(this, uri);
        targetUri = Uri.fromFile(new File(url));
        intent.setDataAndType(targetUri, "image/*");
      } else {
        intent.setDataAndType(uri, "image/*");
      }
      mCropFile = FileUtils.getPhotoOutputFile();
      Uri saveUri = Uri.fromFile(mCropFile);
      intent.putExtra("crop", "true");
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
//            intent.putExtra("outputX", 300);
//            intent.putExtra("outputY", 300);
      intent.putExtra("scale", true);
      intent.putExtra("return-data", false);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);
      intent.putExtra("noFaceDetection", true);
      startActivityForResult(intent, REQUEST_CODE_SYSTEM_RESIZE_IMAGE);
    } catch (Exception e) {
    }
  }

  public int calculateInSampleSize(BitmapFactory.Options options,
      int reqWidth, int reqHeight) {
    // 源图片的高度和宽度
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;
    if (height > reqHeight || width > reqWidth) {
      // 计算出实际宽高和目标宽高的比率
      final int heightRatio = Math.round((float) height / (float) reqHeight);
      final int widthRatio = Math.round((float) width / (float) reqWidth);
      // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
      // 一定都会大于等于目标的宽和高。
      inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }
    return inSampleSize;
  }

  public String decodeSampledBitmap(Uri imageUri, int reqWidth, int reqHeight)
      throws FileNotFoundException {
    // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);
    // 调用上面定义的方法计算inSampleSize值
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    // 使用获取到的inSampleSize值再次解析图片
    options.inJustDecodeBounds = false;
    Bitmap bitmap = BitmapFactory
        .decodeStream(getContentResolver().openInputStream(imageUri), null, options);

    int degree = ExifUtils.getExifOrientation(PicUploadActivity.this, imageUri);
    if (degree != 0) {
      bitmap = rotateBitmap(bitmap, degree, true);
    }
    return bitmapToBase64(bitmap, options);
  }

  private Bitmap rotateBitmap(Bitmap source, int rotation, boolean recycle) {
    if (rotation == 0) {
      return source;
    }
    int w = source.getWidth();
    int h = source.getHeight();
    Matrix m = new Matrix();
    m.postRotate(rotation);
    Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, w, h, m, true);
    if (recycle) {
      source.recycle();
    }
    return bitmap;
  }

  private String bitmapToBase64(String path) {
    Bitmap b = BitmapFactory.decodeFile(path);
    ByteArrayOutputStream out = null;
    try {
      if (TextUtils.isEmpty(quality)) {
        quality = "100";
      }
      int qt = Integer.parseInt(quality);
      out = new ByteArrayOutputStream();

      b.compress(Bitmap.CompressFormat.JPEG, qt, out);
      out.flush();
      out.close();
      byte[] imgBytes = out.toByteArray();
      return Base64.encodeToString(imgBytes, Base64.CRLF);
    } catch (IOException e) {
      Log.d(TAG, e.toString());
    } catch (Exception e) {
      Log.d(TAG, e.toString());
    }
    return "";
  }

  private String bitmapToBase64(Bitmap b, BitmapFactory.Options options) {
    ByteArrayOutputStream out = null;
    try {
      if (TextUtils.isEmpty(quality)) {
        quality = "75";
      }
      int qt = Integer.parseInt(quality);
      out = new ByteArrayOutputStream();
      if (!TextUtils.isEmpty(mImageType) && mImageType.contains("jpeg")) {
        b.compress(Bitmap.CompressFormat.JPEG, qt, out);
        mImageType = "jpg";
      } else if (!TextUtils.isEmpty(mImageType) && mImageType.contains("png")) {
        mImageType = "png";
        b.compress(Bitmap.CompressFormat.PNG, qt, out);
      } else {
        b.compress(Bitmap.CompressFormat.JPEG, qt, out);
        String mimeType = options.outMimeType;
        if (!TextUtils.isEmpty(mimeType) && mimeType.contains("/")) {
          String[] splits = mimeType.split("/");
          mImageType = splits[1];
        } else {
          mImageType = "";
        }
      }
      out.flush();
      out.close();
      byte[] imgBytes = out.toByteArray();
      return Base64.encodeToString(imgBytes, Base64.CRLF);
    } catch (IOException e) {
      Log.d(TAG, e.toString());
    } catch (Exception e) {
      Log.d(TAG, e.toString());
    }
    return "";
  }

  private void showLoadigDalog() {
    try {

      if (progressDialog == null) {
        progressDialog = new ProgressDialog(this);
      }
      progressDialog.setMessage(getString(R.string.image_uploading));
      progressDialog.setCancelable(false);
      progressDialog.setCanceledOnTouchOutside(false);
      progressDialog.show();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void removeLoadingDialog() {
    if (progressDialog != null) {
      if (progressDialog.isShowing()) {
        try {
          progressDialog.dismiss();
        } catch (Exception ignore) {
        }
      }
      progressDialog = null;
    }
  }

  private void uploadPic(String path) {
    if (!TextUtils.isEmpty(path)) {
      showLoadigDalog();

//      RpcServiceFactory factory = DDRpcServiceHelper.getRpcServiceFactory();
//      ImageUploadService service = factory.newRpcService(ImageUploadService.class, mUploadUrl);
//      HashMap<String, Object> params = UploadParams.createParams(this, new File(path), mOtherData);
//      HashMap<String, String> queryParams = UploadParams.createQueryParams(this);
//
//      try {
//        service.uploadImage(queryParams, params, new RpcService.Callback<String>() {
//
//          @Override
//          public void onSuccess(String response) {
//            removeLoadingDialog();
//            FileUtil.deleteFile(mOutPutFile);
//
//            try {
//              JSONObject object = new JSONObject(response);
//              int errno = object.optInt("errno");
//              String errmsg = object.optString("errmsg");
//
//              if (errno == 0) {
//                if (sImageUploadCallback != null) {
//                  sImageUploadCallback.onSuccess(response);
//                }
//
//              } else {
//                ToastHelper.showShortError(PicUploadActivity.this, errmsg);
//              }
//            } catch (Exception e) {
//              e.printStackTrace();
//            }
//
//            finish();
//          }
//
//          @Override
//          public void onFailure(IOException e) {
//            removeLoadingDialog();
//            ToastHelper.showShortError(PicUploadActivity.this, R.string.image_upload_failed);
//            FileUtil.deleteFile(mOutPutFile);
//            finish();
//          }
//        });
//      } catch (UndeclaredThrowableException e) {
//        e.printStackTrace();
//      }
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString("mOutPutFile", mOutPutFilePath);
  }

  @Override
  public void onBackPressed() {
    if (sImageCallback != null) {
      sImageCallback.onCancel();
    }
    super.onBackPressed();
  }
}
