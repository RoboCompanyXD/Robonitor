package com.robocompany.robonitor;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;

public class RobotList extends ArrayList<Robot> {

    private ArrayList<Robot> rootlist;

    RobotList() {
        this.rootlist = new ArrayList<>();

    }

    void save_list(Context ctx){

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(this);
        prefsEditor.putString("Robotlist", json);
        prefsEditor.apply();
    }

    RobotList load_list(Context ctx){

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        Gson gson = new Gson();

        String json = appSharedPrefs.getString("Robotlist", "");

        if(json == null || json.equals("")){
            SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
            prefsEditor.putString("Robotlist", "[]");
            prefsEditor.apply();
            json = appSharedPrefs.getString("Robotlist", "");
        }

        RobotList loaded = gson.fromJson(json, RobotList.class);

        this.rootlist = loaded;
        return loaded;

    }
}
