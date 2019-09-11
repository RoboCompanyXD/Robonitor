package com.robocompany.robonitor.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.robocompany.robonitor.Activities.Tabs.Tab;
import com.robocompany.robonitor.Activities.Tabs.TabPageAdapter;
import com.robocompany.robonitor.DataProcessor;
import com.robocompany.robonitor.R;
import com.robocompany.robonitor.Robot;
import com.robocompany.robonitor.Sensor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class BioSensorMonitorActivity extends AppCompatActivity {

	List<Tab> TabClassList = new ArrayList<Tab>();

	JSONObject device;

	Robot currobot;

	DataProcessor dataProcessor = new DataProcessor(TabClassList);

	private AsyncTask<Robot, JSONObject, Robot> sp;

	private TabPageAdapter tabPageAdapter;

	private ProgressDialog progressDialog;

	private void setupViewPager(ViewPager viewPager, JSONArray jsonArray){

		TabPageAdapter adapter = new TabPageAdapter(getSupportFragmentManager());

		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				int sensor = jsonArray.optInt(i);

				String classname = Sensor.ClassNames[sensor];

				Class<?> c = Class.forName(classname);
				Constructor<?> cons = c.getConstructor();
				Tab object = (Tab) cons.newInstance();

				TabClassList.add(Sensor.List[i],object);

				adapter.addFragment(object,Sensor.TabNames[sensor]);

			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		//adapter.addFragment(new TabACC(), "ACC");
		//adapter.addFragment(new TabECG(), "ECG");
		//adapter.addFragment(new TabEDA(), "EDA");
		//adapter.addFragment(new TabEMG(), "EMG");
		//adapter.addFragment(new TabLUX(), "LUX");

		viewPager.setAdapter(adapter);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getString(R.string.getting_capture));
		progressDialog.setTitle(getString(R.string.downloading));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_biosensormonitor);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		Sensor.TabNames = new String[]{
				this.getResources().getString(R.string.tab_emg),
				this.getResources().getString(R.string.tab_eda),
				this.getResources().getString(R.string.tab_ecg),
				this.getResources().getString(R.string.tab_acc),
				this.getResources().getString(R.string.tab_lux)
		};

		Intent i = getIntent();
		Bundle b = i.getExtras();

		String robot_string = null;

		if (b != null) {
			//robot_string = (String) b.get("Robot");
			currobot = (Robot) b.get("Robot");
		}

		JSONArray sensorInfo = null;
		try {
			assert currobot != null;
			sensorInfo = new JSONObject(currobot.info).getJSONArray("sensorInfo");
			device = new JSONObject(currobot.device);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		tabPageAdapter = new TabPageAdapter(getSupportFragmentManager());

		ViewPager viewPager = findViewById(R.id.viewPager);

		assert sensorInfo != null;
		setupViewPager(viewPager,sensorInfo);

		TabLayout tabs = findViewById(R.id.tabs);
		tabs.setupWithViewPager(viewPager);

		Switch action_sw_samples = findViewById(R.id.action_sw_samples);

		action_sw_samples.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("WrongConstant")
			@Override
			public void onClick(View v) {

				Switch swsamples = (Switch) v;

				if (swsamples.isChecked()) {
					//enviar peticion samplestart

					Toast.makeText(swsamples.getContext(),getString(R.string.enable_monitor),1000).show();

					new startSampes().execute(currobot);

					sp = new SampleProcessor().execute(currobot);


				} else if (!swsamples.isChecked()) {
					//enviar peticion samplestop

					Toast.makeText(swsamples.getContext(),getString(R.string.disable_monitor),1000).show();

					new stopSampes().execute(currobot);

					sp.cancel(true);

				}
			}
		});

		for(Tab t:TabClassList){
			t.init();
		}
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

		//enviar peticion samplestop

		//Toast.makeText(R.menu.menu_biosensormonitor,getString(R.string.disable_monitor),1000).show();

		new stopSampes().execute(currobot);

		if(sp!=null) sp.cancel(true);

		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_biosensormonitor, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if(id == R.id.action_devinfo){

			Intent i = new Intent(this, DeviceInfoActivity.class);
			i.putExtra("bitalinoinfo",device.toString());

			startActivity(i);
		}
		else if(id == R.id.action_capture){


			progressDialog.show();

			new askImage().execute(currobot);
		}

		return super.onOptionsItemSelected(item);
	}

	public class SampleProcessor extends AsyncTask<Robot, JSONObject, Robot>{

		protected void onPreExecute() {
		}

		@Override
		protected Robot doInBackground(Robot... robots) {

			Robot r = robots[0];

			while(!isCancelled()){

				//JSONObject sample = r.get_sample();
				//JSONObject sample = r.ask_sample();
				publishProgress(r.ask_sample());
				//publishProgress(r.get_sample());

			}
			return r;
		}

		protected void onPostExecute (Robot result){

		}

		protected void onProgressUpdate(JSONObject... samples) {
			JSONObject sample = samples[0];

			dataProcessor.process_sample(sample);
		}

	}

	public class startSampes extends AsyncTask<Robot, Robot, Robot> {

		protected void onPreExecute() {
		}

		@Override
		protected Robot doInBackground(Robot... rs) {

			Robot r = rs[0];

			r.start_samples();

			return r;
		}

		protected void onPostExecute (Robot result){

		}

	}

	public class stopSampes extends AsyncTask<Robot, Robot, Robot> {

		protected void onPreExecute() {
		}

		@Override
		protected Robot doInBackground(Robot... rs) {

			Robot r = rs[0];

			r.stop_samples();

			return r;
		}

		protected void onPostExecute (Robot result){

		}


	}

	public class askImage extends AsyncTask<Robot, String, Robot> {

		protected void onPreExecute() {
		}

		@Override
		protected Robot doInBackground(Robot... rs) {

			Robot r = rs[0];

			String bm = r.ask_image();

			publishProgress(bm);

			return r;
		}

		protected void onPostExecute (Robot result){

		}

		@SuppressLint({"WrongThread", "WrongConstant"})
		@Override
		protected void onProgressUpdate(String... captures) {

			String capture = captures[0];

			if (capture == null){
				Toast.makeText(getApplicationContext(),getString(R.string.image_error),1000).show();
				progressDialog.dismiss();
			}

			else {

				byte[] decodedString = Base64.decode(capture, 0);
				Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

				Intent i = new Intent(getApplicationContext(), ShowcaptureActivity.class);

				try {
					File outputDir = getApplicationContext().getCacheDir(); // context being the Activity pointer
					File outputFile = File.createTempFile("capture-", null, outputDir);

					String tmp_path = outputFile.getAbsolutePath();

					FileOutputStream out = new FileOutputStream(tmp_path);

					bm.compress(Bitmap.CompressFormat.JPEG, 100, out);

					i.putExtra("capture",tmp_path);

					progressDialog.dismiss();
					startActivity(i);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			progressDialog.dismiss();

		}
	}

}
