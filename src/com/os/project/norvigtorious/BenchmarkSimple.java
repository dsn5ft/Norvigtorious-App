package com.os.project.norvigtorious;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.MemoryFile;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BenchmarkSimple extends Activity {

	Context context = this;
	Benchmark benchmark;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.benchmark_simple);

		initialize();
		
		executeBenchmark();
	}
	
	public void initialize() {
		benchmark = Benchmark.fromId(getIntent().getExtras().getInt("benchmark_id"));

		TextView title = (TextView) findViewById(R.id.name);
		title.setText(Html.fromHtml("<u>" + benchmark.getName() + "</u>"));

		Button recalculate = (Button) findViewById(R.id.recalculate);
		recalculate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				executeBenchmark();
			}
		});
	}

	public void executeBenchmark() {
		((ProgressBar) findViewById(R.id.spinner)).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.results)).setText("");

		switch (benchmark) {
		case FIND_VIEW_BY_ID:
			findViewById();
			break;
		case SET_CONTENT_VIEW:
			setContentView();
			break;
		case READ_FROM_RAM:
			readFromRAM();
			break;
		case READ_FROM_FLASH:
			readFromFlash();
			break;
		case STRING_SORTING:
			stringSorting();
			break;
		case PACKET_TO_EUROPE:
			pingEurope();
			break;

		default:
			break;
		}
	}
	
	public void setTimeResult(final long nanoTime) {
		runOnUiThread(new Runnable() {
			public void run() {
				((ProgressBar) findViewById(R.id.spinner)).setVisibility(View.GONE);
				((TextView) findViewById(R.id.results)).setText(String.format("%,d", nanoTime) + " ns!");
				DataManager.updateBenchmarkAverage(context, benchmark, nanoTime);
			}
		});
	}
	
	public void findViewById() {
		long time = tryFindViewById();
		
		while(time == 0) {
			time = tryFindViewById();
		}
		
		setTimeResult(time);
	}
	
	public long tryFindViewById() {
		long start = System.nanoTime();
		View rootView = findViewById(android.R.id.content);
		long end = System.nanoTime();
		
		rootView.invalidate();

		return end - start;
	}
	
	public void setContentView() {
		long start = System.nanoTime();
		setContentView(R.layout.benchmark_simple);
		long end = System.nanoTime();
		
		initialize();
		
		setTimeResult(end - start);
	}

	public void stringSorting() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					List<String> list = new ArrayList<String>();

					Random random = new Random();
					String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
					for (int i = 0; i < 10000; i++) {
						String string = "";
						for (int j = 0; j < 10; j++) {
							string += letters.charAt(random.nextInt(letters.length()));
						}
						list.add(string);
					}

					final long start = System.nanoTime();
					Collections.sort(list);
					final long end = System.nanoTime();

					setTimeResult(end - start);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void readFromRAM() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					int len = 1048586;
					MemoryFile file = new MemoryFile("1 MB Test", len);

					byte[] buffer = new byte[len];
					for (int i = 0; i < len; i++) {
						buffer[i] = 7;
					}

					file.writeBytes(buffer, 0, 0, len);

					final long start = System.nanoTime();
					file.readBytes(buffer, 0, 0, len);
					final long end = System.nanoTime();

					file.close();

					setTimeResult(end - start);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void readFromFlash() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				long count = 0;
				long sum = 0;
				for (int i = 0; i < 10; i++) {
					long val = readMegabyteFlash();
					if (val != -1) {
						sum += val;
						count++;
					}
				}

				setTimeResult(sum / count);
			}
		}).start();
	}

	public long readMegabyteFlash() {
		InputStream is = null;
		try {
			is = context.getResources().getAssets().open("UVa Buildings");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			int len = 1048586;
			char[] buffer = new char[len];
			long start = System.nanoTime();
			reader.read(buffer, 0, len);
			long end = System.nanoTime();
			return end - start;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				is.close();
			} catch (Exception e2) {
			}
		}
	}

	public void pingEurope() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Process process = new ProcessBuilder().command("/system/bin/ping", "-c 5", "www.ox.ac.uk").redirectErrorStream(true).start();

					try {
						BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

						String full = "";
						String line = null;
						while ((line = in.readLine()) != null) {
							full = full + "\n" + line;
						}

						String[] temp = full.split("/");
						final String average = temp[temp.length - 3];
						
						setTimeResult((long) Double.parseDouble(average) * 1000000);
					} finally {
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