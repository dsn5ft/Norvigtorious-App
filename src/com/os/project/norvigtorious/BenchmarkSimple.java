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
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.MemoryFile;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
		((RelativeLayout) findViewById(R.id.results_border)).setVisibility(View.GONE);

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
		case WRITE_TO_RAM:
			writeToRAM();
			break;
		case READ_FROM_INTERNAL_STORAGE:
			readFromFlash();
			break;
		case WRITE_TO_INTERNAL_STORAGE:
			writeToFlash();
			break;
		case READ_FROM_SD_CARD:
			read1MBFromSDCard();
			break;
		case WRITE_TO_SD_CARD:
			write1MBToSDCard();
			break;
		case ENCRYPT_DATA:
			encrypt1MBData();
			break;
		case DECRYPT_DATA:
			decrypt1MBData();
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
		new Thread(new Runnable() {
			
			@Override
			public void run() {
			    HttpClient httpClient = new DefaultHttpClient();
			    HttpPost httpPost = new HttpPost("http://plato.cs.virginia.edu/~res6tq/norvigtorious/saveBenchmark.php");

			    try {
			        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			        nameValuePairs.add(new BasicNameValuePair("bench_type", "" + benchmark.getId()));
			        nameValuePairs.add(new BasicNameValuePair("model", Build.MODEL));
			        nameValuePairs.add(new BasicNameValuePair("manufacturer", Build.MANUFACTURER));
			        nameValuePairs.add(new BasicNameValuePair("bench_value", "" + nanoTime));
			        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			        httpClient.execute(httpPost);
			    } catch (Exception e) {
			    	e.printStackTrace();
			    }
			}
		}).start();
		
		runOnUiThread(new Runnable() {
			public void run() {
				((ProgressBar) findViewById(R.id.spinner)).setVisibility(View.GONE);
				((RelativeLayout) findViewById(R.id.results_border)).setVisibility(View.VISIBLE);
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
	
	public void writeToRAM() {
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

					final long start = System.nanoTime();
					file.writeBytes(buffer, 0, 0, len);
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
			        
					setTimeResult(end - start);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (Exception e2) {
					}
				}
			}
		}).start();
	}
	
	public void writeToFlash() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
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
			        
					setTimeResult(end - start);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (Exception e2) {
					}
				}				
			}
		}).start();
	}
	
	public void read1MBFromSDCard() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				InputStream is = null;
				try {
					is = context.getResources().getAssets().open("Megabyte File");
					int len = 1048586;
					byte[] buffer = new byte[len];
					is.read(buffer, 0, len);
					
					File file = new File("/storage/sdcard1/Megabyte File");

					FileOutputStream f = new FileOutputStream(file);
					f.write(buffer, 0, len);
				} catch (final Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							((ProgressBar) findViewById(R.id.spinner)).setVisibility(View.GONE);
							Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();					
						}
					});
				} finally {
					try {
						is.close();
					} catch (Exception e2) {
					}
				}
				
				InputStream is1 = null;
				try {
					is1 = new FileInputStream(new File("/storage/sdcard1/Megabyte File"));
					BufferedReader reader = new BufferedReader(new InputStreamReader(is1));
					int len = 1048586;
					char[] buffer = new char[len];
					long start = System.nanoTime();
					reader.read(buffer, 0, len);
					long end = System.nanoTime();
					
					setTimeResult(end - start);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						is1.close();
					} catch (Exception e2) {
					}
				}
			}
		}).start();
	}
	
	public void write1MBToSDCard() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				InputStream is = null;
				try {
					is = context.getResources().getAssets().open("Megabyte File");
					int len = 1048586;
					byte[] buffer = new byte[len];
					is.read(buffer, 0, len);
					
					File file = new File("/storage/sdcard1/Megabyte File");

					FileOutputStream f = new FileOutputStream(file);
					
					long start = System.nanoTime();
					f.write(buffer, 0, len);
					long end = System.nanoTime();
					
					setTimeResult(end - start);
				} catch (final Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							((ProgressBar) findViewById(R.id.spinner)).setVisibility(View.GONE);
							Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();					
						}
					});
				} finally {
					try {
						is.close();
					} catch (Exception e2) {
					}
				}				
			}
		}).start();
	}

	public void encrypt1MBData() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
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

					setTimeResult(end - start);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (Exception e2) {
					}
				}
			}
		}).start();
	}
	
	public void decrypt1MBData() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
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
			        decrypt(key,encryptedData);
					long end = System.nanoTime();
					
					setTimeResult(end - start);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (Exception e2) {
					}
				}
			}
		}).start();
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
    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
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