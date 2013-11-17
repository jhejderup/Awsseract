package nl.tudelft.ec2interface.taskmonitor;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import nl.tudelft.ec2interface.sysmonitor.SystemUsageInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class TaskInfo {

	private String uuid;
	private String masterId;
	private String workerId;
	private Timestamp receiveTime;
	private Timestamp transferTime;
	private Timestamp startTime;
	private Timestamp finishTime;
	private int taskSize;
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}

	public Timestamp getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Timestamp receiveTime) {
		this.receiveTime = receiveTime;
	}

	public Timestamp getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(Timestamp transferTime) {
		this.transferTime = transferTime;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Timestamp finishTime) {
		this.finishTime = finishTime;
	}
	
	public int getTaskSize() {
		return taskSize;
	}

	public void setTaskSize(int taskSize) {
		this.taskSize = taskSize;
	}

	public String displayTime(Timestamp timestamp)
	{
		Calendar amsterdam = Calendar.getInstance();
		amsterdam.setTimeInMillis(timestamp.getTime());
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Amsterdam"));
		return dateFormat.format(amsterdam.getTime());
	}

	public String ToJson(TaskInfo tInfo)
	{
		try {
			
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(tInfo);
			return json;
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
			
		}
		
	}
	
	public TaskInfo FromJson(String jsonString)
	{

		try {
			
			TaskInfo tInfo = new ObjectMapper().readValue(jsonString, TaskInfo.class);
			return tInfo;
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	
	@Override
	public String toString() {
		return "TaskInfo " + "\n"
				+ " uuid=" + uuid + "\n"
				+ " masterId=" + masterId + "\n"
				+ " workerId=" + workerId + "\n"
				+ " receiveTime=" + receiveTime + "\n"
				+ " transferTime=" + transferTime + "\n"
				+ " startTime=" + startTime + "\n"
				+ " finishTime=" + finishTime + "\n"
				+ " taskSize=" + taskSize;
	}

	public static void main(String[] args)
	{
		Timestamp t = new Timestamp(System.currentTimeMillis());
		System.out.println(new TaskInfo().displayTime(t));
	}
}
