package com.robocompany.robonitor;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.robocompany.robonitor.Activities.AboutActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityMain extends AppCompatActivity {


	RobotList robotList;

	ListView lv_robotlist;

	RobotListAdapter robotlistadapter;

	public FloatingActionButton.OnClickListener mScan = new FloatingActionButton.OnClickListener() {

		public void onClick(View v) {
			try {
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.setPackage("com.google.zxing.client.android");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, 0);
			} catch (ActivityNotFoundException e) {

				Context context = v.getContext();

				AlertDialog.Builder builder;

				builder = new AlertDialog.Builder(context);

				builder.setTitle(getString(R.string.barcode_scanner));
				builder.setMessage(getString(R.string.install_barcode));
				builder.setPositiveButton(getString(R.string.google_play), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.zxing.client.android"));
						startActivity(browserIntent);
					}
				});

				builder.setNegativeButton(getString(R.string.back), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

				AlertDialog alertDialog = builder.create();
				alertDialog.show();
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {

			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan
				String newcontent = contents.replace('\\', ' ');
				if (format.equals("QR_CODE")) {
					JSONObject obj = new JSONObject();
					try {
						obj = new JSONObject(newcontent);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					String servername = null;
					String serveraddr = null;
					int serverport = 0;
					String scankey = null;

					try {
						servername = obj.getString("name");
						serveraddr = obj.getString("server_addr");
						serverport = Integer.parseInt(obj.getString("server_port"));
						scankey = obj.getString("scan_key");

					} catch (JSONException e) {
						e.printStackTrace();
					}

					if (servername != null && serveraddr != null && validIP(serveraddr) && serverport != 0 && scankey != null) {

						Robot newrobot = new Robot(
								servername,
								serveraddr,
								serverport,
								scankey);
						robotList.add(newrobot);

						robotList.save_list(this.getApplicationContext());

						TextView tv_norobot = findViewById(R.id.tv_norobot);

						if(lv_robotlist.getAdapter().getCount() == 0){

							tv_norobot.setVisibility(View.VISIBLE);

						}
						else {
							tv_norobot.setVisibility(View.INVISIBLE);
						}

						new Check_state().execute(newrobot);
						//newrobot.chk_connection();

						robotlistadapter.notifyDataSetChanged();
					}
					else{
						Toast.makeText(getApplicationContext(),getString(R.string.qr_scann_format_error),Toast.LENGTH_SHORT).show();
					}
				}
				//TextView QRresult = findViewById(R.id.txv_QRresult);
				//QRresult.setText(format + contents);
			} else if (resultCode == RESULT_CANCELED) {

				//Toast.makeText(getApplicationContext(),"Operación cancelada",Toast.LENGTH_SHORT).show();

			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		//StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		//StrictMode.setThreadPolicy(policy);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		Context ctx = this.getApplicationContext();
		robotList = new RobotList();
		robotList = robotList.load_list(ctx);

		lv_robotlist = (ListView) findViewById(R.id.lv_robotlist);

		robotlistadapter = new RobotListAdapter(this, R.layout.robotlist_item, robotList);

		lv_robotlist.setAdapter(robotlistadapter);

		FloatingActionButton add = findViewById(R.id.add);

		TextView tv_norobot = findViewById(R.id.tv_norobot);

		if(lv_robotlist.getAdapter().getCount() == 0){

			tv_norobot.setVisibility(View.VISIBLE);

		}
		else {
			tv_norobot.setVisibility(View.INVISIBLE);
		}

		add.setOnClickListener(mScan);

		chechConections();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_about) {

			Intent i = new Intent(this, AboutActivity.class);

			startActivity(i);

		} else if (id == R.id.action_refresh) {

			chechConections();

		}

		return super.onOptionsItemSelected(item);
	}

	public class Check_state extends AsyncTask<Robot, Robot, Robot> {

		protected void onPreExecute() {
			robotlistadapter.notifyDataSetChanged();
		}

		@Override
		protected Robot doInBackground(Robot... rs) {

			Robot r = rs[0];

			r.chk_connection();

			return r;
		}

		protected void onPostExecute(Robot result) {
			robotlistadapter.notifyDataSetChanged();
		}


	}

	public void chechConections() {

		for (Robot r : robotList) {
			r.state = 1;

			new Check_state().execute(r);
		}

	}

	public static boolean validIP(String ip) {
		try {
			if (ip == null || ip.isEmpty()) {
				return false;
			}

			String[] parts = ip.split("\\.");
			if (parts.length != 4) {
				return false;
			}

			for (String s : parts) {
				int i = Integer.parseInt(s);
				if ((i < 0) || (i > 255)) {
					return false;
				}
			}
			return !ip.endsWith(".");
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

}