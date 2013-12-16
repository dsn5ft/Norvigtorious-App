package com.os.project.norvigtorious;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.MemoryFile;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class RunAllBenchmarks extends Activity {

	Context context = this;
	List<BenchmarkStatistic> myDeviceBenchmarkAverageTimes = new ArrayList<BenchmarkStatistic>();
	Map<Benchmark, Long> benchmarkAverageTimes = new HashMap<Benchmark, Long>();
	
	long benchmarkRepeatTimeMillis = 5000;
	double totalTimeMillis = benchmarkRepeatTimeMillis * (Benchmark.values().length - 2);
	long startTimeMillis;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.run_all_benchmarks);
		
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(Html.fromHtml("<u>" + title.getText() + "</u>"));
		
		for(Benchmark benchmark : Benchmark.values()) {
			benchmarkAverageTimes.put(benchmark, DataManager.getBenchmarkAverage(context, benchmark));
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
//					repeatBenchmark(new Runnable() {
//						public void run() {
//							findViewById();
//						}
//					});
//					
//					repeatBenchmark(new Runnable() {
//						public void run() {
//							setContentView();
//						}
//					});
					
					startTimeMillis = System.currentTimeMillis();
					
					repeatBenchmark(new Runnable() {
						public void run() {
							readFromRAM();
						}
					});
					
					repeatBenchmark(new Runnable() {
						public void run() {
							writeToRAM();
						}
					});
					
					repeatBenchmark(new Runnable() {
						public void run() {
							readFromFlash();
						}
					});
					
					repeatBenchmark(new Runnable() {
						public void run() {
							writeToFlash();
						}
					});
					
					repeatBenchmark(new Runnable() {
						public void run() {
							read1MBFromSDCard();
						}
					});
					
					repeatBenchmark(new Runnable() {
						public void run() {
							write1MBToSDCard();
						}
					});
					
					repeatBenchmark(new Runnable() {
						public void run() {
							encrypt1MBData();
						}
					});
					
					repeatBenchmark(new Runnable() {
						public void run() {
							decrypt1MBData();
						}
					});
					
					repeatBenchmark(new Runnable() {
						public void run() {
							stringSorting();
						}
					});
					
					repeatBenchmark(new Runnable() {
						public void run() {
							pingEurope();
						}
					});
					
					runOnUiThread(new Runnable() {
						public void run() {
							((TextView) findViewById(R.id.status)).setText("Testing Complete!");
							ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
							progressBar.setProgress(progressBar.getMax());
						}
					});					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void repeatBenchmark(Runnable runnable) {
		try {
			boolean firstRun = true;
			long start = System.currentTimeMillis();		
			while(System.currentTimeMillis() - start < benchmarkRepeatTimeMillis) {
				runnable.run();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
						progressBar.setProgress((int) ((double) (System.currentTimeMillis() - startTimeMillis) / totalTimeMillis * progressBar.getMax()));
					}
				});
				
				if(firstRun) {
					firstRun = false;
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
							scrollView.post(new Runnable() { 
							    public void run() { 
							    	scrollView.fullScroll(ScrollView.FOCUS_DOWN); 
							    } 
							}); 					
						}
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void addTimeResult(final Benchmark benchmark, final long nanoTime) {
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				HttpClient httpClient = new DefaultHttpClient();
//				HttpPost httpPost = new HttpPost("http://plato.cs.virginia.edu/~res6tq/norvigtorious/saveBenchmark.php");
//
//				try {
//					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//					nameValuePairs.add(new BasicNameValuePair("bench_type", "" + benchmark.getId()));
//					nameValuePairs.add(new BasicNameValuePair("model", Build.MODEL));
//					nameValuePairs.add(new BasicNameValuePair("manufacturer", Build.MANUFACTURER));
//					nameValuePairs.add(new BasicNameValuePair("bench_value", "" + nanoTime));
//					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//					httpClient.execute(httpPost);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
		
		boolean benchmarkInList = false;
		
		for(BenchmarkStatistic benchmarkStatistic : myDeviceBenchmarkAverageTimes) {
			if(benchmark == benchmarkStatistic.getBenchmark()) {
				benchmarkStatistic.incrementAverage(nanoTime);
				benchmarkInList = true;
			}
		}
		
		if(!benchmarkInList) {
			myDeviceBenchmarkAverageTimes.add(new BenchmarkStatistic(benchmark, 1, nanoTime));
		}

		String results = "";
		for(BenchmarkStatistic stat : myDeviceBenchmarkAverageTimes) {
			results += "<br><u>" + stat.getBenchmark().getName() + "</u>";
			results += "<br>My Device: &nbsp;" + String.format("%,d", stat.getAverage()) + " ns";
			results += "<br>Average: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + String.format("%,d", benchmarkAverageTimes.get(stat.getBenchmark())) + " ns<br>";
		}
		
		final Spanned finalResults = Html.fromHtml(results);
		
		runOnUiThread(new Runnable() {
			public void run() {
				((TextView) findViewById(R.id.results)).setText(finalResults);
				((TextView) findViewById(R.id.status)).setText(benchmark.getName() + "...");
			}
		});
		
		DataManager.updateBenchmarkAverage(context, benchmark, nanoTime);
	}

	public long findViewById() {
		long time = tryFindViewById();

		while (time == 0) {
			time = tryFindViewById();
		}

		addTimeResult(Benchmark.FIND_VIEW_BY_ID, time);
		return time;
	}

	public long tryFindViewById() {
		long start = System.nanoTime();
		View rootView = findViewById(android.R.id.content);
		long end = System.nanoTime();

		rootView.invalidate();

		return end - start;
	}

	public long setContentView() {
		long start = System.nanoTime();
		setContentView(R.layout.run_all_benchmarks);
		long end = System.nanoTime();

		addTimeResult(Benchmark.SET_CONTENT_VIEW, end - start);
		return end - start;
	}

	public long readFromRAM() throws RuntimeException {
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

			addTimeResult(Benchmark.READ_FROM_RAM, end - start);
			return end - start;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public long writeToRAM() throws RuntimeException {
		try {
			int len = 1048586;
			MemoryFile file = new MemoryFile("1 MB Test", len);

			byte[] buffer = new byte[len];
			for (int i = 0; i < len; i++) {
				buffer[i] = 7;
			}

			final long start = System.nanoTime();
			file.writeBytes(buffer, 0, 0, len);
			final long end = System.nanoTime();

			file.close();

			addTimeResult(Benchmark.WRITE_TO_RAM, end - start);
			return end - start;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public long readFromFlash() throws RuntimeException {
		InputStream is = null;
		try {
			is = context.getResources().getAssets().open("Megabyte File");
			int len = 1048586;
			byte[] buffer = new byte[len];
			is.read(buffer, 0, len);

			FileOutputStream outputStream = openFileOutput("Megabyte File", Context.MODE_PRIVATE);
			outputStream.write(buffer);
			outputStream.close();

			FileInputStream inputStream = openFileInput("Megabyte File");
			long start = System.nanoTime();
			inputStream.read(buffer, 0, len);
			long end = System.nanoTime();

			addTimeResult(Benchmark.READ_FROM_INTERNAL_STORAGE, end - start);
			return end - start;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (Exception e2) {
			}
		}
	}

	public long writeToFlash() throws RuntimeException {
		InputStream is = null;
		try {
			is = context.getResources().getAssets().open("Megabyte File");
			int len = 1048586;
			byte[] buffer = new byte[len];
			is.read(buffer, 0, len);

			FileOutputStream outputStream = openFileOutput("Megabyte File", Context.MODE_PRIVATE);
			long start = System.nanoTime();
			outputStream.write(buffer);
			long end = System.nanoTime();
			outputStream.close();

			addTimeResult(Benchmark.WRITE_TO_INTERNAL_STORAGE, end - start);
			return end - start;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (Exception e2) {
			}
		}
	}

	public long read1MBFromSDCard() throws RuntimeException {
		InputStream is = null;
		try {
			is = context.getResources().getAssets().open("Megabyte File");
			int len = 1048586;
			byte[] buffer = new byte[len];
			is.read(buffer, 0, len);

			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Megabyte File");

			FileOutputStream f = new FileOutputStream(file);
			f.write(buffer, 0, len);
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (Exception e2) {
			}
		}

		InputStream is1 = null;
		try {
			is1 = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Megabyte File"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(is1));
			int len = 1048586;
			char[] buffer = new char[len];
			long start = System.nanoTime();
			reader.read(buffer, 0, len);
			long end = System.nanoTime();

			addTimeResult(Benchmark.READ_FROM_SD_CARD, end - start);
			return end - start;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				is1.close();
			} catch (Exception e2) {
			}
		}
	}

	public long write1MBToSDCard() throws RuntimeException {
		InputStream is = null;
		try {
			is = context.getResources().getAssets().open("Megabyte File");
			int len = 1048586;
			byte[] buffer = new byte[len];
			is.read(buffer, 0, len);

			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Megabyte File");

			FileOutputStream f = new FileOutputStream(file);

			long start = System.nanoTime();
			f.write(buffer, 0, len);
			long end = System.nanoTime();

			addTimeResult(Benchmark.WRITE_TO_SD_CARD, end - start);
			return end - start;
		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (Exception e2) {
			}
		}
	}

	public long encrypt1MBData() throws RuntimeException {
		InputStream is = null;
		try {
			is = context.getResources().getAssets().open("Megabyte File");
			int len = 1048586;
			byte[] buffer = new byte[len];
			is.read(buffer, 0, len);

			byte[] keyStart = "this is a key".getBytes();
			KeyGenerator kgen = KeyGenerator.getInstance("AES");

			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(keyStart);
			kgen.init(128, sr);
			SecretKey skey = kgen.generateKey();
			byte[] key = skey.getEncoded();

			long start = System.nanoTime();
			encrypt(key, buffer);
			long end = System.nanoTime();

			addTimeResult(Benchmark.ENCRYPT_DATA, end - start);
			return end - start;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (Exception e2) {
			}
		}
	}

	public long decrypt1MBData() throws RuntimeException {
		InputStream is = null;
		try {
			is = context.getResources().getAssets().open("Megabyte File");
			int len = 1048586;
			byte[] buffer = new byte[len];
			is.read(buffer, 0, len);

			byte[] keyStart = "this is a key".getBytes();
			KeyGenerator kgen = KeyGenerator.getInstance("AES");

			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(keyStart);
			kgen.init(128, sr);
			SecretKey skey = kgen.generateKey();
			byte[] key = skey.getEncoded();

			byte[] encryptedData = encrypt(key, buffer);

			long start = System.nanoTime();
			decrypt(key, encryptedData);
			long end = System.nanoTime();

			addTimeResult(Benchmark.DECRYPT_DATA, end - start);
			return end - start;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (Exception e2) {
			}
		}
	}

	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	public long stringSorting() throws RuntimeException {
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

			addTimeResult(Benchmark.STRING_SORTING, end - start);
			return end - start;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public long pingEurope() throws RuntimeException {
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

				addTimeResult(Benchmark.PACKET_TO_EUROPE, (long) Double.parseDouble(average) * 1000000);
				return (long) Double.parseDouble(average) * 1000000;
			} finally {
				process.destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}

}