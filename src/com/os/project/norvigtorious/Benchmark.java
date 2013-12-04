package com.os.project.norvigtorious;

public enum Benchmark {
	
	FIND_VIEW_BY_ID(1, "Find View By ID"),
	SET_CONTENT_VIEW(2, "Set Content View"),
	READ_FROM_RAM(3, "Read 1 MB From RAM"),
	WRITE_TO_RAM(4, "Write 1 MB To RAM"),
	READ_FROM_INTERNAL_STORAGE(5, "Read 1 MB From Internal Storage"),
	WRITE_TO_INTERNAL_STORAGE(6, "Write 1 MB To Internal Storage"),
	READ_FROM_SD_CARD(7, "Read 1 MB From SD Card"),
	WRITE_TO_SD_CARD(8, "Write 1 MB To SD Card"),
	ENCRYPT_DATA(9, "Encrypt 1 MB of Data"),
	DECRYPT_DATA(10, "Decrypt 1 MB of Data"),
	STRING_SORTING(11, "Sort 10,000 Strings Alphabetically"),
	PACKET_TO_EUROPE(12, "Send Packet to Europe and Back")
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
