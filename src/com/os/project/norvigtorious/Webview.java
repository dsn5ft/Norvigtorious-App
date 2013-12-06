package com.os.project.norvigtorious;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

@SuppressLint("NewApi")
public class Webview extends Activity {
	
	Context context = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		Bundle b = getIntent().getExtras();
		String url = b.getString("url");
		
		WebView webView = (WebView) findViewById(R.id.webview);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setBuiltInZoomControls(true);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			webView.getSettings().setDisplayZoomControls(false);
		}
		
		final ProgressDialog dialog = new ProgressDialog(Webview.this);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setIcon(R.drawable.icon_small);
		dialog.setTitle("Loading...");
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				try {
					dialog.show();
				} catch (Exception e) {
				}

				super.onPageStarted(view, url, favicon);
			}
		});

		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				try {
					dialog.setProgress(progress);

					if (progress == 100) {
						dialog.dismiss();
					}
				} catch (Exception e) {
				}
			}
		});

		webView.loadUrl(url);
		
		Button viewMyStatistics = (Button) findViewById(R.id.view_my_stats);
		viewMyStatistics.setOnClickListener(new OnClickListener() {
			
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
					
					statistics += "<br><b><u>" + stat.getName() + "</u></b><br>";
					statistics += String.format("%,d", average) + " ns<br>";
//					statistics += "Count: " + count + "<br>";
				}
				
				new AlertDialog.Builder(context)
				.setTitle("Benchmark Statistics")
				.setMessage(Html.fromHtml(statistics))
				.setNeutralButton("OK", null)
				.setIcon(R.drawable.icon_small)
				.show();				
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}
}