package com.robocompany.robonitor.Activities.Tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robocompany.robonitor.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Date;

public class TabEDA extends Tab {

	@Override
	public void process(JSONObject sample, Date captime, int samplenumber) {

		//Log.d(this.getClass().getName(),sample.toString());

	}

	@Override
	public void init() {

	}

	public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.tab_eda,container,false);
		return view;
	}

}
