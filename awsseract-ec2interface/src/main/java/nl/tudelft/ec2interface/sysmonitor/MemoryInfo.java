package nl.tudelft.ec2interface.sysmonitor;

public class MemoryInfo {

	private int totalSystemMemory;
	private int usedSystemMemory;
	private int actualSystemMemory;
	private int usage;
	private int actualUsage;

	public int getTotalSystemMemory() {
		return totalSystemMemory;
	}

	public void setTotalSystemMemory(int totalSystemMemory) {
		this.totalSystemMemory = totalSystemMemory;
	}

	public int getUsedSystemMemory() {
		return usedSystemMemory;
	}

	public void setUsedSystemMemory(int usedSystemMemory) {
		this.usedSystemMemory = usedSystemMemory;
	}
	
	public int getUsage() {
		return usage;
	}

	public void setUsage(int usage) {
		this.usage = usage;
	}

	public int getActualSystemMemory() {
		return actualSystemMemory;
	}

	public void setActualSystemMemory(int actualSystemMemory) {
		this.actualSystemMemory = actualSystemMemory;
	}

	public int getActualUsage() {
		return actualUsage;
	}

	public void setActualUsage(int actualUsage) {
		this.actualUsage = actualUsage;
	}

	@Override
	public String toString() {
		return "MemoryInfo " + "\n"
				+ " totalSystemMemory=" + totalSystemMemory + "MB" + "\n"
				+ " usedSystemMemory=" + usedSystemMemory + "MB" + "\n"
				+ " usage=" + usage + "%" + "\n"
				+ " actualSystemMemory=" + actualSystemMemory + "MB" + "\n"
				+ " actualUsage=" + actualUsage + "%";
	}
	
	

}
