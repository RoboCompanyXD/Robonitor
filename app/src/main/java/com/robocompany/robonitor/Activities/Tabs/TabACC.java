package com.robocompany.robonitor.Activities.Tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.robocompany.robonitor.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TabACC extends Tab {

	GraphView acc_graph;
	LineGraphSeries<DataPoint> s;
	Map<String, Object> variables;

	@Override
	public void process(JSONObject sample, Date captime, int samplenumber) {
		try {
			//Log.d(this.getClass().getName(),sample.toString());

			//Load variables to hash map

			JSONArray vars = sample.getJSONArray("variables");

			for(int i = 0; i<vars.length(); i++){

				String var = vars.getString(i);
				Object value = sample.get(var);

				variables.put(var, value);

			}

			JSONArray sampledata = sample.getJSONArray("data");

			for(int i = 0; i< sampledata.length(); i++){

				Object p = sampledata.get(i);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() {

		//s = new LineGraphSeries<>();
		variables = new HashMap<>();
		//datapointlist = new ArrayList<DataPoint>();

	}

	public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.tab_acc,container,false);

		acc_graph = view.findViewById(R.id.graph_acc);

		return view;
	}


}

