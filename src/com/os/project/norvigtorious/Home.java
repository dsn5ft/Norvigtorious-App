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

public class Home extends Activity {
	
	Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
				
		Button viewStatistics = (Button) findViewById(R.id.view_statistics);
		viewStatistics.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				List<BenchmarkStatistic> statsList = new ArrayList<BenchmarkStatistic>();
				
				for(Benchmark benchmark : Benchmark.values()) {
					long average = DataManager.getBenchmarkAverage(context, benchmark);
					long count = DataManager.getBenchmarkCount(context, benchmark);
					
					BenchmarkStatistic newStat = new BenchmarkStatistic(count, average, benchmark.getName());
					
					if(newStat.getCount() == 0) {
						statsList.add(newStat);
					}
					else {
						int index = 0;
						
						for(BenchmarkStatistic stat : statsList) {
							if(stat.getCount() == 0) {
								break;
							}
							
							if(newStat.getAverage() > stat.getAverage()) {
								index++;
							}
						}
						
						statsList.add(index, newStat);
					}
				}
				
				String statistics = "";
				
				for(BenchmarkStatistic stat : statsList) {
					long average = stat.getAverage();
					long count = stat.getCount();
					
					statistics += "<b><u>" + stat.getName() + "</u></b><br>";
					statistics += "Average: " + String.format("%,d", average) + " ns<br>";
					statistics += "Count: " + count + "<br><br>"; 
				}
				
				new AlertDialog.Builder(context)
				.setTitle("Benchmark Statistics")
				.setMessage(Html.fromHtml(statistics))
				.setNeutralButton("OK", null)
				.setIcon(R.drawable.icon_small)
				.show();
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
				.setMessage("Developed by...\n\nDaniel Nizri\nAlex Fabian\nRenee Seaman\nCasey Silver")
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
