package main;

import interfaces.ClientTasks;
import tools.AverageNumber;

public class ClientData {
	
	private final String NAME;
	private final String IP;
	private final String LOCAL_IP;
	
	private long lastUpdateTime = System.currentTimeMillis();
	private AverageNumber ups = new AverageNumber(0, 10);
	
	private final ClientTasks tasks = new ClientTasks();
	
	
	public ClientData(String name, String ip, String local_ip) {
		this.NAME = name;
		this.IP = ip;
		this.LOCAL_IP = local_ip;
	}
	
	public void update() {
		ups.add(1000d / getUpdateTimeDuration());
		this.lastUpdateTime = System.currentTimeMillis();
	}
	
	public long getUpdateTimeDuration() {
		return System.currentTimeMillis() - lastUpdateTime;
	}
	
	public boolean equals(String id) {
		return id.equals(this.toString());
	}
	
	@Override
	public String toString() {
		return NAME + "," + IP + "," + LOCAL_IP;
	}
	
	public ClientTasks getTasks() {
		return tasks;
	}
	public String getLocalIp() {
		return LOCAL_IP;
	}
	public String getName() {
		return NAME;
	}
	public String getIp() {
		return IP;
	}
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
	public double getAverageUPS() {
		return ups.getAverage();
	}
	
}
