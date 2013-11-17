package nl.tudelft.ec2interface.sysmonitor;

public class CpuInfo {

	private String CpuModel;
	private int userTime;
	private int sysTime;
	private int idleTime;
	private int usage;

	public String getCpuModel() {
		return CpuModel;
	}

	public void setCpuModel(String cpuModel) {
		CpuModel = cpuModel;
	}

	public int getUserTime() {
		return userTime;
	}

	public void setUserTime(int userTime) {
		this.userTime = userTime;
	}

	public int getSysTime() {
		return sysTime;
	}

	public void setSysTime(int sysTime) {
		this.sysTime = sysTime;
	}

	public int getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(int idleTime) {
		this.idleTime = idleTime;
	}

	public int getUsage() {
		return usage;
	}

	public void setUsage(int usage) {
		this.usage = usage;
	}

	@Override
	public String toString() {
		return "CpuInfo" + "\n"
				+ " CpuModel=" + CpuModel + "\n"
				+ " userTime=" + userTime + "%" + "\n"
				+ " sysTime=" + sysTime + "%" + "\n"
				+ " idleTime=" + idleTime + "%" + "\n"
				+ " usage=" + usage + "%";
	}

}
