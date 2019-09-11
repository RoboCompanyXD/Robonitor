package com.robocompany.robonitor.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.robocompany.robonitor.R;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceInfoActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_info);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		TextView tv_version = findViewById(R.id.tv_version);
		TextView tv_mac = findViewById(R.id.tv_mac);

		String bitalinoinfo_string = getIntent().getStringExtra("bitalinoinfo");

		try {
			JSONObject bitalinoinfo = new JSONObject(bitalinoinfo_string);

			tv_version.setText(String.valueOf(bitalinoinfo.get("version")));
			tv_mac.setText(String.valueOf(bitalinoinfo.get("macAddress")));

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
