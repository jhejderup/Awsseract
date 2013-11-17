package nl.tudelft.ec2interface.sysmonitor;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class SystemUsageInfo {

	private String workerId;
	private String instanceId;
	private Timestamp timestamp;
	private CpuInfo cpuInfo;
	private MemoryInfo memoryInfo;
	private DiskInfo diskInfo;
	
	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public CpuInfo getCpuInfo() {
		return cpuInfo;
	}

	public void setCpuInfo(CpuInfo cpuInfo) {
		this.cpuInfo = cpuInfo;
	}

	public MemoryInfo getMemoryInfo() {
		return memoryInfo;
	}

	public void setMemoryInfo(MemoryInfo memoryInfo) {
		this.memoryInfo = memoryInfo;
	}

	public DiskInfo getDiskInfo() {
		return diskInfo;
	}

	public void setDiskInfo(DiskInfo diskInfo) {
		this.diskInfo = diskInfo;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "SystemUsageInfo (" + displayTime(timestamp) + ") " + "\n"
				+ cpuInfo + "\n"
				+ memoryInfo + "\n"
				+ diskInfo + "\n"
				+ "\n";
	}
	
	public String displayTime(Timestamp timestamp)
	{
		Calendar amsterdam = Calendar.getInstance();
		amsterdam.setTimeInMillis(timestamp.getTime());
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Amsterdam"));
		return dateFormat.format(amsterdam.getTime());
	}

}
