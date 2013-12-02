package com.os.project.norvigtorious;

public enum Benchmark {
	
	FIND_VIEW_BY_ID(1, "Find View By ID"),
	SET_CONTENT_VIEW(2, "Set Content View"),
	STRING_SORTING(3, "Sort 10,000 Strings Alphabetically"),
	READ_FROM_RAM(4, "Read 1 MB From RAM"),
	READ_FROM_FLASH(5, "Read 1 MB From Flash Memory"),
	PACKET_TO_EUROPE(6, "Send Packet to Europe and Back")
	;
	
	final int id;
	final String name;
	
	Benchmark(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public static Benchmark fromId(int id) {
		for(Benchmark benchmark : values()) {
			if(id == benchmark.getId()) {
				return benchmark;
			}
		}
		throw new IllegalArgumentException("No Benchmark with id=" + id);
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
