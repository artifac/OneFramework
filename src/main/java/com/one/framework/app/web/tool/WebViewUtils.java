package com.one.framework.app.web.tool;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author xianchaohua
 */

public class WebViewUtils {
    /**
     * 判断是否安装支付宝
     *if(checkAlipayAppInstalled(mActivity))
     * @param context
     * @return
     */
    public static boolean checkAlipayAppInstalled(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.eg.android.AlipayGphone", 0);
            if (packageInfo != null)
                return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
