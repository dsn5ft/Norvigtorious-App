package com.os.project.norvigtorious;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class DataManager extends Activity {
	
	public final static String FILE_NAME = "benchmark_statistics";

	public static long getBenchmarkCount(Context context, Benchmark benchmark) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		return sharedPrefs.getLong("count_" + benchmark.getId(), 0);
	}
	
	public static long getBenchmarkAverage(Context context, Benchmark benchmark) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		return sharedPrefs.getLong("average_" + benchmark.getId(), 0);
	}
	
	public static void updateBenchmarkAverage(Context context, Benchmark benchmark, long time) {
		long average = getBenchmarkAverage(context, benchmark);
		long count = getBenchmarkCount(context, benchmark);
		long sum = average * count;
		
		long newSum = sum + time;
		long newCount = count + 1;
		long newAverage = newSum / newCount;

		SharedPreferences sharedPrefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putLong("count_" + benchmark.getId(), newCount);
		editor.putLong("average_" + benchmark.getId(), newAverage);
		editor.commit();
	}
	
	public static void clearAllStatistics(Context context) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.clear();
		editor.commit();
	}
	
	public static void clearBenchmarkStatistics(Context context, Benchmark benchmark) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putLong("count_" + benchmark.getId(), 0);
		editor.putLong("average_" + benchmark.getId(), 0);
		editor.commit();
	}

}