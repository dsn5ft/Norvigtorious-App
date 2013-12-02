package com.os.project.norvigtorious;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Home extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		Button findViewById = (Button) findViewById(R.id.find_view_by_id);
		findViewById.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchBenchmarkSimple(Benchmark.FIND_VIEW_BY_ID);
			}
		});
		
		Button setContentView = (Button) findViewById(R.id.set_content_view);
		setContentView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchBenchmarkSimple(Benchmark.SET_CONTENT_VIEW);
			}
		});
		
		Button readFromRAM = (Button) findViewById(R.id.read_from_ram);
		readFromRAM.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchBenchmarkSimple(Benchmark.READ_FROM_RAM);
			}
		});
		
		Button readFromFlash = (Button) findViewById(R.id.read_from_flash);
		readFromFlash.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchBenchmarkSimple(Benchmark.READ_FROM_FLASH);
			}
		});
		
		Button sortStrings = (Button) findViewById(R.id.sort_strings);
		sortStrings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchBenchmarkSimple(Benchmark.STRING_SORTING);
			}
		});
		
		Button packetToEurope = (Button) findViewById(R.id.packet_to_europe);
		packetToEurope.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchBenchmarkSimple(Benchmark.PACKET_TO_EUROPE);
			}
		});
	}
	
	public void launchBenchmarkSimple(Benchmark benchmark) {
		Intent intent = new Intent(Home.this, com.os.project.norvigtorious.BenchmarkSimple.class);
		intent.putExtra("benchmark", benchmark.getName());
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

}
