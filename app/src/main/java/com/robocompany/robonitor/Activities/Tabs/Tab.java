package com.robocompany.robonitor.Activities.Tabs;

import android.support.v4.app.Fragment;

import org.json.JSONObject;

import java.util.Date;

public abstract class Tab extends Fragment {


	public abstract void process(JSONObject sample, Date captime, int samplenumber);
	public abstract void init();

}
