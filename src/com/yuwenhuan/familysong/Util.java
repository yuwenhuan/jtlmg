package com.yuwenhuan.familysong;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public final class Util {

	private static final String TAG = "Util";

	public static boolean fileExist(String fileName) {
		// check if pictures have been saved to external storage
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		Log.d(TAG,path.toString());
		File file = new File(path, fileName);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}
}
