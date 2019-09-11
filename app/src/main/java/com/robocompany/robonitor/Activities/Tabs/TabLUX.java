package com.robocompany.robonitor.Activities.Tabs;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.robocompany.robonitor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TabLUX extends Tab {

	ProgressBar pb_luxlevel;
	Map<String, Object> variables;

	TextView tv_percent;

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public void process(JSONObject sample, Date captime, int samplenumber) {
		try {

			//Load variables to hash map

			JSONArray vars = sample.getJSONArray("variables");

			for(int i = 0; i<vars.length(); i++){

				String var = vars.getString(i);
				Object value = sample.get(var);

				variables.put(var, value);

			}

			//Generate datapoints

			JSONArray sampledata = sample.getJSONArray("data");

			int min = Math.toIntExact(Math.round((Double) variables.get("min")));
			int max = Math.toIntExact(Math.round((Double) variables.get("max")));

			pb_luxlevel.setMin(min);
			pb_luxlevel.setMax(max);

			for(int i = 0; i< sampledata.length(); i++){

				Object p = sampledata.get(i);

				pb_luxlevel.setProgress(Math.toIntExact(Math.round((Double) p)));

				int range = max - min;
				int correctedStartValue = Math.toIntExact(Math.round((Double) p) - min);
				tv_percent.setText((correctedStartValue * 100) / range +" %");

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() {

		variables = new HashMap<>();

	}

	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.tab_lux,container,false);

		pb_luxlevel = view.findViewById(R.id.pb_luxlevel);
		tv_percent = view.findViewById(R.id.tv_percent);

		return view;
	}

}
