package com.robocompany.robonitor.Activities.Tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabPageAdapter extends FragmentPagerAdapter {

	private final List<Fragment> fragmentList = new ArrayList<>();
	private final List<String> fragmentTitleList = new ArrayList<>();


	public TabPageAdapter(FragmentManager fm) {
		super(fm);
	}

	public void addFragment(Fragment fragment, String title){
		fragmentList.add(fragment);
		fragmentTitleList.add(title);
	}

	public CharSequence getPageTitle(int position){
		return fragmentTitleList.get(position);
	}

	@Override
	public Fragment getItem(int position) {
		return fragmentList.get(position);
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}

	public Fragment getBy(String sensor){

		return fragmentList.get(fragmentTitleList.indexOf(sensor));


	}
}
