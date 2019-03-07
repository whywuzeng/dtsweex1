package com.weex.plugins.navigator;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * Created by zhangjiacheng on 2018/5/29.
 */

public class MDMUtil {

	public static boolean appIsInstalled(Context mContext, String packageName) {
		boolean installed = false;
		PackageInfo packageInfo = null;
		try {
			packageInfo = mContext.getPackageManager().getPackageInfo(
					packageName, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo != null) {
			installed = true;
		}
		return installed;
	}

}
