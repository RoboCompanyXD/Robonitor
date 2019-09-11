package com.robocompany.robonitor;

import com.robocompany.robonitor.Activities.Tabs.TabACC;
import com.robocompany.robonitor.Activities.Tabs.TabECG;
import com.robocompany.robonitor.Activities.Tabs.TabEDA;
import com.robocompany.robonitor.Activities.Tabs.TabEMG;
import com.robocompany.robonitor.Activities.Tabs.TabLUX;

public class Sensor {

	private static final int EMG  = 0;
	private static final int EDA  = 1;
	private static final int ECG  = 2;
	private static final int ACC  = 3;
	private static final int LUX  = 4;

	public static int[] List = {EMG, EDA, ECG, ACC, LUX};
	public static String[] ClassNames = {ClassName.EMG, ClassName.EDA, ClassName.ECG, ClassName.ACC, ClassName.LUX};
	public static String[] Names = {Name.EMG, Name.EDA, Name.ECG, Name.ACC, Name.LUX};

	public static String[] TabNames;

	public class Name{

		static final String EMG  = "EMG";
		static final String EDA  = "EDA";
		static final String LUX  = "LUX";
		static final String ECG  = "ECG";
		static final String ACC  = "ACC";

	}

	public static class ClassName{
		static final String EMG = TabEMG.class.getName();
		static final String EDA = TabEDA.class.getName();
		static final String LUX = TabLUX.class.getName();
		static final String ECG = TabECG.class.getName();
		static final String ACC = TabACC.class.getName();
	}

}
