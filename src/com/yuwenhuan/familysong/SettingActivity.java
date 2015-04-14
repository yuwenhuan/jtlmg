package com.yuwenhuan.familysong;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener {

	private static final int RESIZE_RATIO = 2;
	private static final int REQUEST_CODE = 1;
	private static final String TAG = "SettingActivity";
	private static final String FILENAMES[] = { "yeye", "nainai", "bobo",
			"shushu", "gugu", "waigong", "waipo", "jiujiu", "ayi" };
	private static final int RECOVER_RES_ID[] = { R.id.recoverYeye,
			R.id.recoverNainai, R.id.recoverBobo, R.id.recoverShushu,
			R.id.recoverGugu, R.id.recoverWaigong, R.id.recoverWaipo,
			R.id.recoverJiujiu, R.id.recoverAyi };
	private static final int IMAGE_RES_ID[] = { R.id.imageYeye,
			R.id.imageNainai, R.id.imageBobo, R.id.imageShushu, R.id.imageGugu,
			R.id.imageWaigong, R.id.imageWaipo, R.id.imageJiujiu, R.id.imageAyi };

	private ImageView imageYeye;
	private ImageView imageBobo;
	private ImageView imageNainai;
	private ImageView imageShushu;
	private ImageView imageGugu;
	private ImageView imageWaigong;
	private ImageView imageWaipo;
	private ImageView imageJiujiu;
	private ImageView imageAyi;
	private Button recoverYeye;
	private Button recoverNainai;
	private Button recoverBobo;
	private Button recoverShushu;
	private Button recoverWaipo;
	private Button recoverGugu;
	private Button recoverWaigong;
	private Button recoverJiujiu;
	private Button recoverAyi;
	private File path = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	private Bitmap[] mBitmap_array = new Bitmap[9];
	private Bitmap mBitmap;
	private ImageView[] myImageView = new ImageView[9];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		imageYeye = (ImageView) findViewById(R.id.imageYeye);
		imageNainai = (ImageView) findViewById(R.id.imageNainai);
		imageBobo = (ImageView) findViewById(R.id.imageBobo);
		imageShushu = (ImageView) findViewById(R.id.imageShushu);
		imageGugu = (ImageView) findViewById(R.id.imageGugu);
		imageWaigong = (ImageView) findViewById(R.id.imageWaigong);
		imageWaipo = (ImageView) findViewById(R.id.imageWaipo);
		imageJiujiu = (ImageView) findViewById(R.id.imageJiujiu);
		imageAyi = (ImageView) findViewById(R.id.imageAyi);
		for (int ii = 0; ii < IMAGE_RES_ID.length; ii++) {
			myImageView[ii] = (ImageView) findViewById(IMAGE_RES_ID[ii]);
		}

		imageYeye.setOnClickListener(this);
		imageNainai.setOnClickListener(this);
		imageBobo.setOnClickListener(this);
		imageShushu.setOnClickListener(this);
		imageGugu.setOnClickListener(this);
		imageWaigong.setOnClickListener(this);
		imageWaipo.setOnClickListener(this);
		imageJiujiu.setOnClickListener(this);
		imageAyi.setOnClickListener(this);

		recoverYeye = (Button) findViewById(R.id.recoverYeye);
		recoverNainai = (Button) findViewById(R.id.recoverNainai);
		recoverBobo = (Button) findViewById(R.id.recoverBobo);
		recoverShushu = (Button) findViewById(R.id.recoverShushu);
		recoverGugu = (Button) findViewById(R.id.recoverGugu);
		recoverWaigong = (Button) findViewById(R.id.recoverWaigong);
		recoverWaipo = (Button) findViewById(R.id.recoverWaipo);
		recoverJiujiu = (Button) findViewById(R.id.recoverJiujiu);
		recoverAyi = (Button) findViewById(R.id.recoverAyi);

		checkExternalImage();
	}

	private void checkExternalImage() {
		// check if external images exist
		Options option = new BitmapFactory.Options();
		option.inSampleSize = RESIZE_RATIO;
		for (int ii = 0; ii < FILENAMES.length; ii++) {
			String fileName = FILENAMES[ii];
			if (Util.fileExist(fileName + ".jpg")) {
				// external image exists
				Log.d(TAG, fileName + ".jpg exists.");
				File file = new File(path, FILENAMES[ii]+".jpg");
				mBitmap_array[ii] = BitmapFactory.decodeFile(file.getAbsolutePath(),
						option);
				myImageView[ii].setImageBitmap(mBitmap_array[ii]);
			} else {
				Log.d(TAG, fileName + ".jpg does NOT exist.");
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		for (int ii = 0; ii < myImageView.length; ii++) {
			if (myImageView[ii].getId() == v.getId()) {
				Log.d(TAG, "save " + FILENAMES[ii] + "jpg");
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(
						Intent.createChooser(
								intent,
								getResources().getString(
										R.string.chooser_prompt)), ii);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode < FILENAMES.length && resultCode == RESULT_OK) {
			Uri uri = data.getData();
			Log.e(TAG, uri.toString());
			Toast.makeText(getApplicationContext(), uri.toString(),
					Toast.LENGTH_LONG).show();
			try {
				InputStream stream = getContentResolver().openInputStream(uri);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(stream, null, options);
				int width = options.outWidth;
				int height = options.outHeight;
				Log.d(TAG, "image size" + width + "x" + height);
				stream.close();

				int dispW = getResources().getDisplayMetrics().widthPixels;
				int ratio = 1;
				while (dispW * ratio < width * 1.5)
					ratio = ratio * 2;
				Log.d(TAG, "sampling ratio:" + ratio);
				options.inJustDecodeBounds = false;
				options.inSampleSize = ratio;
				stream = getContentResolver().openInputStream(uri);
				Bitmap bm = BitmapFactory.decodeStream(stream, null, options);
				stream.close();

				if (mBitmap != null)
					mBitmap.recycle();
				mBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
						Bitmap.Config.ARGB_8888);
				Canvas c = new Canvas(mBitmap);
				c.drawBitmap(bm, 0, 0, null);
				bm.recycle();

				File path = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
				Log.d(TAG, "Path:" + path);
				path.mkdirs();
				File file = new File(path, FILENAMES[requestCode]+".jpg");
				FileOutputStream stream_out;
				try {
					stream_out = new FileOutputStream(file,false);
					mBitmap.compress(CompressFormat.JPEG, 90, stream_out);
					stream_out.close();
				} catch (Exception e) {
					Log.e(TAG, "saving error", e);
				}
			} catch (Exception e) {
				Log.e(TAG, "Stream opening", e);
			}
		}
		checkExternalImage();
		super.onActivityResult(requestCode, resultCode, data);
	}

}
