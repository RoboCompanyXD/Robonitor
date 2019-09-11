package com.robocompany.robonitor.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.robocompany.robonitor.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;

public class ShowcaptureActivity extends AppCompatActivity {

	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showcapture);

		String tmp_path = getIntent().getStringExtra("capture");

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		bitmap = BitmapFactory.decodeFile(tmp_path, options);

		ImageView iv_capture = findViewById(R.id.iv_capture);

		iv_capture.setImageBitmap(bitmap);

		//scaleImage(iv_capture, bitmap);

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_showcapture, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		if(id == R.id.save_capture){

			if(isStoragePermissionGranted()) {

				if (saveImage(bitmap) == 0) {
					Toast.makeText(getApplicationContext(), R.string.image_saved, 1000).show();
				} else {
					Toast.makeText(getApplicationContext(), R.string.image_save_error, 1000).show();
				}

			}
		}

		return super.onOptionsItemSelected(item);
	}

	private int saveImage(Bitmap finalBitmap) {

		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd_hh.mm.ss");
		String image_name = dateFormat.format(date);

		String root = Environment.getExternalStorageDirectory().toString()+"/Robonitor";
		File myDir = new File(root);
		myDir.mkdirs();
		String fname = "Robonitor-" + image_name+ ".jpg";
		File file = new File(myDir, fname);
		if (file.exists()) file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}


	public  boolean isStoragePermissionGranted() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
					== PackageManager.PERMISSION_GRANTED) {

				return true;
			} else {

				Toast.makeText(getApplicationContext(), R.string.image_save_error, 1000).show();
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
				return false;
			}
		}
		else {

			return true;
		}
	}

	private void scaleImage(ImageView view, Bitmap bitmap) throws NoSuchElementException {

		int width = 0;

		try {
			width = bitmap.getWidth();
		} catch (NullPointerException e) {
			throw new NoSuchElementException("Can't find bitmap on given view/drawable");
		}

		int height = bitmap.getHeight();
		int bounding = dpToPx(250);

		// Determine how much to scale: the dimension requiring less scaling is
		// closer to the its side. This way the image always stays inside your
		// bounding box AND either x/y axis touches it.
		float xScale = ((float) bounding) / width;
		float yScale = ((float) bounding) / height;
		float scale = (xScale <= yScale) ? xScale : yScale;

		// Create a matrix for the scaling and add the scaling data
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		// Create a new bitmap and convert it to a format understood by the ImageView
		Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		width = scaledBitmap.getWidth(); // re-use
		height = scaledBitmap.getHeight(); // re-use
		BitmapDrawable result = new BitmapDrawable(scaledBitmap);

		// Apply the scaled bitmap
		view.setImageDrawable(result);

		// Now change ImageView's dimensions to match the scaled image
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
		params.width = width;
		params.height = height;
		view.setLayoutParams(params);

	}

	private int dpToPx(int dp) {
		float density = getApplicationContext().getResources().getDisplayMetrics().density;
		return Math.round((float)dp * density);
	}
}

