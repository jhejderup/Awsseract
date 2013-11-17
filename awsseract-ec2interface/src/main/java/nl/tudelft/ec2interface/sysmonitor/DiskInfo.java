package nl.tudelft.ec2interface.sysmonitor;

public class DiskInfo {

	private int totalDiskSpace;
	private int usedDiskSpace;
	private int usage;

	public int getTotalDiskSpace() {
		return totalDiskSpace;
	}

	public void setTotalDiskSpace(int totalDiskSpace) {
		this.totalDiskSpace = totalDiskSpace;
	}

	public int getUsedDiskSpace() {
		return usedDiskSpace;
	}

	public void setUsedDiskSpace(int usedDiskSpace) {
		this.usedDiskSpace = usedDiskSpace;
	}

	public int getUsage() {
		return usage;
	}

	public void setUsage(int usage) {
		this.usage = usage;
	}

	@Override
	public String toString() {
		return "DiskInfo" + "\n"
				+ " totalDiskSpace=" + totalDiskSpace + "MB" + "\n"
				+ " usedDiskSpace=" + usedDiskSpace + "MB" + "\n"
				+ " usage=" + usage + "%";
	}

}
