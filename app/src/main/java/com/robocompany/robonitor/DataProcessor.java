package com.robocompany.robonitor;

import com.robocompany.robonitor.Activities.Tabs.Tab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class DataProcessor {

	private List<Tab> TabClassList;
	private Date prevtime = new Date();

	public DataProcessor(List<Tab> tabClassList) {

		this.TabClassList = tabClassList;

	}

	public void process_sample(JSONObject sample){

		try {
			JSONArray sensor = sample.getJSONArray("sensors");

			Date captime = convertTime(sample.getInt("captime"));
			int samplenumber = sample.getInt("samplenumber");

			if(prevtime != captime) {

				prevtime = captime;

				for (int s : Sensor.List) {

					TabClassList.get(s).process(sensor.getJSONObject(s), captime, samplenumber);

				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();

		}

	}

	private static Date convertTime(int timestamp){

		java.util.Date time=new java.util.Date((long)timestamp/1000);
		return time;

	}
}
