package com.os.project.norvigtorious;

public class BenchmarkStatistic {
	
	private long count;
	private long average;
	private String name;
	
	public BenchmarkStatistic(long count, long average, String name) {
		this.count = count;
		this.average = average;
		this.name = name;
	}
	
	public long getCount() {
		return count;
	}
	
	public long getAverage() {
		return average;
	}
	
	public String getName() {
		return name;
	}
}
