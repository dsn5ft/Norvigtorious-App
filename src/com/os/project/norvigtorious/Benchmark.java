package com.os.project.norvigtorious;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Benchmark extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.benchmark);
		
		if(getIntent().getExtras().getString("benchmark").equals("europe")) {
			pingEurope();
		}
	}
	
	public void pingEurope() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Process process = new ProcessBuilder()
					.command("/system/bin/ping", "-c 5", "www.ox.ac.uk")
					.redirectErrorStream(true)
					.start();

					try {
						BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

						String full = "";
						String line = null;
						while ((line = in.readLine()) != null) {  
							full = full + "\n" + line;
						}
						
						String[] temp = full.split("/");
						final String average = temp[temp.length - 3];
						
						runOnUiThread(new Runnable() {
							public void run() {
								((ProgressBar) findViewById(R.id.spinner)).setVisibility(View.GONE);
								((TextView) findViewById(R.id.results)).setText(average + " ms!");							}
						});
					}
					finally {
						process.destroy();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		}).start();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}

}