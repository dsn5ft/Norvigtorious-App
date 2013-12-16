package com.os.project.norvigtorious;

public class BenchmarkStatistic {
	
	private Benchmark benchmark;
	private long count;
	private long average;
	
	public BenchmarkStatistic(Benchmark benchmark, long count, long average) {
		this.benchmark = benchmark;
		this.count = count;
		this.average = average;
	}
	
	public Benchmark getBenchmark() {
		return benchmark;
	}
	
	public long getCount() {
		return count;
	}
	
	public void setCount(long count) {
		this.count = count ;
	}
	
	public long getAverage() {
		return average;
	}
	
	public void incrementAverage(long time) {
		long sum = average * count;
		
		long newSum = sum + time;
		long newCount = count + 1;
		average = newSum / newCount;
		count = newCount;
	}

}
