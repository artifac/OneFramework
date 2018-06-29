package com.one.framework.net;

import android.content.Context;
import com.one.framework.net.base.INetworkConfig;
import com.one.framework.utils.HttpsUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by ludexiang on 2018/6/8.
 */

public class NetworkConfig implements INetworkConfig {

  private Context mContext;
  private SSLSocketFactory mSslSocketFactory;
  private X509TrustManager mTrustManager;
  private IHeaderParams mHeaderParams;

  public NetworkConfig(Context context, IHeaderParams params, InputStream inputStream,
      String password, boolean isHttps) {
    mContext = context;
    mHeaderParams = params;
    if (isHttps) {
      initHttps(inputStream, password);
    }
  }

  @Override
  public void setUserPhone(String phone) {
    mHeaderParams.setLoginPhone(phone);
  }

  private void initHttps(InputStream inputStream, String password) {
    HttpsUtils.SslParams params = HttpsUtils.getSslSocketFactory(inputStream, password);
    mSslSocketFactory = params.sslSocketFactory;
    mTrustManager = params.trustManager;
  }

  @Override
  public SSLSocketFactory getSslSocketFactory() {
    return mSslSocketFactory;
  }

  @Override
  public X509TrustManager getTrustManager() {
    return mTrustManager;
  }

  @Override
  public HostnameVerifier getHostnameVerifier() {
    return new HostnameVerifier() {
      @Override
      public boolean verify(String hostname, SSLSession session) {
        return true;
      }
    };
  }

  @Override
  public long getTimeout() {
    return 30;
  }

  @Override
  public List<NetInterceptor> getInterceptors() {
    return Collections.singletonList(new NetInterceptor(mHeaderParams));
  }
}
