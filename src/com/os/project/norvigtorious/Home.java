package com.os.project.norvigtorious;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Home extends Activity {
	
	Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
				
		TextView heading = (TextView) findViewById(R.id.benchmarks);
		heading.setText(Html.fromHtml("<u>" + heading.getText() + "</u>"));
				
		Button viewStatistics = (Button) findViewById(R.id.view_statistics);
		viewStatistics.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, com.os.project.norvigtorious.Webview.class);
				intent.putExtra("url", "http://plato.cs.virginia.edu/~res6tq/norvigtorious/benchmarkStats/home");
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
			}
		});
		
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
		
		Button writeToRAM = (Button) findViewById(R.id.write_to_ram);
		writeToRAM.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchBenchmarkSimple(Benchmark.WRITE_TO_RAM);
			}
		});
		
		Button readFromFlash = (Button) findViewById(R.id.read_from_flash);
		readFromFlash.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchBenchmarkSimple(Benchmark.READ_FROM_INTERNAL_STORAGE);
			}
		});
		
		Button writeToFlash = (Button) findViewById(R.id.write_to_flash);
		writeToFlash.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchBenchmarkSimple(Benchmark.WRITE_TO_INTERNAL_STORAGE);
			}
		});
		
		Button readFromSDCard = (Button) findViewById(R.id.read_from_sd_card);
		readFromSDCard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchBenchmarkSimple(Benchmark.READ_FROM_SD_CARD);
			}
		});
		
		Button writeToSDCard = (Button) findViewById(R.id.write_to_sd_card);
		writeToSDCard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchBenchmarkSimple(Benchmark.WRITE_TO_SD_CARD);
			}
		});
		
		Button sortStrings = (Button) findViewById(R.id.sort_strings);
		sortStrings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchBenchmarkSimple(Benchmark.STRING_SORTING);
			}
		});
		
		Button encryptData = (Button) findViewById(R.id.encrypt_data);
		encryptData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchBenchmarkSimple(Benchmark.ENCRYPT_DATA);
			}
		});
		
		Button decryptData = (Button) findViewById(R.id.decrypt_data);
		decryptData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchBenchmarkSimple(Benchmark.DECRYPT_DATA);
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
		intent.putExtra("benchmark_id", benchmark.getId());
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			new AlertDialog.Builder(context)
				.setTitle("About")
				.setMessage("Developed by..." +
						"\n\nDaniel Nizri\nAlex Fabian\nRenee Seaman\nCasey Silver" +
						"\n\nInspired by Peter Norvig's \"Teach Yourself Programming in Ten Years\"")
				.setNeutralButton("OK", null)
				.setIcon(R.drawable.icon_small)
				.show();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
