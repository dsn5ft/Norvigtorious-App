package com.os.project.norvigtorious;

public enum Benchmark {
	
	STRING_SORTING("Sort 10,000 Strings Alphabetically"),
	READ_FROM_RAM("Read 1 MB From RAM"),
	READ_FROM_FLASH("Read 1 MB From Flash Memory"),
	PACKET_TO_EUROPE("Send Packet to Europe and Back")
	;
	
	final String name;
	
	Benchmark(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static Benchmark fromName(String name) {
		for(Benchmark benchmark : values()) {
			if(name.equals(benchmark.getName())) {
				return benchmark;
			}
		}
		throw new IllegalArgumentException("No Benchmark with name=" + name);
	}

}
