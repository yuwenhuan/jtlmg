package com.yuwenhuan.familysong;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final float DURATION = 3.5f; // the time to change lyric1 to
	// lyric2 (in seconds)
	private static final int TIMER_INTERVAL = 1; // in seconds
	private static final String TAG = "MainActivity";
	private static final String FILENAMES[] = { "yeye", "nainai", "bobo",
			"shushu", "gugu", "waigong", "waipo", "jiujiu", "ayi" };
	private static final int START_TIME[] = { 6, 13, 20, 27, 34, 45, 52, 59, 66 };
	private static final int IMAGE_RES_ID[] = { R.drawable.yy, R.drawable.nn,
			R.drawable.bb, R.drawable.ss, R.drawable.gg, R.drawable.wg,
			R.drawable.wp, R.drawable.jj, R.drawable.ay };
	private static final int LYRIC1_RES_ID[] = { R.string.lyric_yeye1,
			R.string.lyric_nainai1, R.string.lyric_bobo1,
			R.string.lyric_shushu1, R.string.lyric_gugu1,
			R.string.lyric_waigong1, R.string.lyric_waipo1,
			R.string.lyric_jiujiu1, R.string.lyric_ayi1 };
	private static final int LYRIC2_RES_ID[] = { R.string.lyric_yeye2,
			R.string.lyric_nainai2, R.string.lyric_bobo2,
			R.string.lyric_shushu2, R.string.lyric_gugu2,
			R.string.lyric_waigong2, R.string.lyric_waipo2,
			R.string.lyric_jiujiu2, R.string.lyric_ayi2 };

	private MediaPlayer music;
	private ImageButton playButton;
	private ImageButton pauseButton;
	private ImageButton stopButton;
	private Timer myTimer;
	private TimerTask timerTask;
	private ImageView imageView;
	private TextView text;
	private int status = 0; // 0-stop,1-play,2-pause
	private File path = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	private Bitmap mBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		playButton = (ImageButton) findViewById(R.id.play);
		pauseButton = (ImageButton) findViewById(R.id.pause);
		stopButton = (ImageButton) findViewById(R.id.stop);
		imageView = (ImageView) findViewById(R.id.image);
		text = (TextView) findViewById(R.id.text);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	private void CopyImagesFromResourceToStorage(int image_num) {
		Bitmap bm = BitmapFactory.decodeResource(getResources(),
				IMAGE_RES_ID[image_num]);
		try {
			File path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			Log.d(TAG, "Path:" + path);
			path.mkdirs();
			File file = new File(path, FILENAMES[image_num] + ".jpg");
			FileOutputStream stream_out;
			stream_out = new FileOutputStream(file, false);
			bm.compress(CompressFormat.JPEG, 90, stream_out);
			stream_out.close();
		} catch (Exception e) {
			Log.e(TAG, "saving error", e);
		}
	}

	@Override
	protected void onResume() {
		// check if file existing on external storage
		for (int ii = 0; ii < FILENAMES.length; ii++) {
			String fileName = FILENAMES[ii];
			if (Util.fileExist(fileName + ".jpg")) {
				Log.d(TAG, fileName + ".jpg exists.");
			} else {
				Log.d(TAG, fileName + ".jpg does not exists.");
				CopyImagesFromResourceToStorage(ii);
			}
		}

		music = MediaPlayer.create(getApplicationContext(), R.raw.music);
		music.setLooping(true);
		music.start();
		playMusic();
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (music != null) {
			music.stop();
			music.reset();
			music.release();
		}
		status = 0; // stop
		stopTimer();

		super.onPause();
	}

	public void play(View v) {
		if (status == 0) {
			// stop
			try {
				music.prepare();
				music.start();
				playMusic();
			} catch (Exception e) {
				Log.d("MainActivity",
						"Play button:prepare media player failed.", e);
			}
		} else if (status == 2) {
			// pause
			music.start();
			playMusic();
		}

		return;
	}

	private void playMusic() {
		status = 1; // set status to play
		playButton.setEnabled(false);
		pauseButton.setEnabled(true);
		stopButton.setEnabled(true);

		// start the timer
		myTimer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						float curSecond = music.getCurrentPosition() / 1000;
						// find out which pic to show
						int ii = -1;
						if (curSecond >= START_TIME[START_TIME.length - 1]) {
							// show last picture
							ii = START_TIME.length - 1;
						} else {
							while (curSecond > START_TIME[ii + 1])
								ii++;
						}
						if (ii == -1) {
							// show cover
							if (curSecond <= TIMER_INTERVAL + 0.5) {
								imageView.setImageResource(R.drawable.cover);
								text.setText(R.string.song_name);
							}
						} else {
							// show image number ii
							if (curSecond < START_TIME[ii] + TIMER_INTERVAL
									+ 0.5) {

								// show the image in the external storage
								File file = new File(path, FILENAMES[ii]
										+ ".jpg");
								if (mBitmap != null)
									mBitmap.recycle();
								mBitmap = BitmapFactory.decodeFile(file
										.getAbsolutePath());
								imageView.setImageBitmap(mBitmap);

								text.setText(LYRIC1_RES_ID[ii]);
							} else if (curSecond > START_TIME[ii] + DURATION
									&& curSecond < START_TIME[ii] + DURATION
											+ TIMER_INTERVAL + 0.5) {
								text.setText(LYRIC2_RES_ID[ii]);
							}
						}
					}
				});
			}
		};
		myTimer.schedule(timerTask, 4000, TIMER_INTERVAL * 1000);
	}

	private void stopTimer() {
		if (myTimer != null) {
			myTimer.cancel();
			myTimer = null;
		}
	}

	public void pause(View v) {
		music.pause();
		status = 2; // set status to pause
		playButton.setEnabled(true);
		pauseButton.setEnabled(false);
		stopButton.setEnabled(true);
		return;
	}

	public void stop(View v) {
		music.stop();
		status = 0; // set status to stop
		playButton.setEnabled(true);
		pauseButton.setEnabled(false);
		stopButton.setEnabled(false);
		stopTimer();
		imageView.setImageResource(R.drawable.cover);
		text.setText(R.string.song_name);
		return;
	}

	public void setting(View v) {
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
	}
}
