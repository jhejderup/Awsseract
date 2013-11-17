package nl.tudelft.ec2interface.sysmonitor;

import java.io.IOException;
import java.sql.Timestamp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class SystemUsage {
	
	private String configFile;
	
	public SystemUsage(String configFile)
	{
		setConfigFile(configFile);
	}
	
	public SystemUsage()
	{
		setConfigFile("conf/");
	}

	public static void main(String[] args)
	{
		SystemUsageInfo info = new SystemUsage("conf/").getInfo();
		System.out.println(new SystemUsage("conf/").ToJson(info));
		System.out.println(new SystemUsage("conf/").FromJson(new SystemUsage("conf/").ToJson(info)));
	}
	
	public SystemUsageInfo getInfo()
	{
		System.setProperty("java.library.path",configFile);
		SystemUsageInfo sInfo = new SystemUsageInfo();
		sInfo.setTimestamp(new Timestamp(System.currentTimeMillis()));
		sInfo.setCpuInfo(new CpuUsage().getInfo());
		sInfo.setMemoryInfo(new MemoryUsage().getInfo());
		sInfo.setDiskInfo(new DiskUsage().getInfo());
		return sInfo;
	}
	
	public String ToJson(SystemUsageInfo sInfo)
	{
		try {
			
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(sInfo);
			return json;
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
			
		}
		
	}
	
	public SystemUsageInfo FromJson(String jsonString)
	{

		try {
			
			SystemUsageInfo sInfo = new ObjectMapper().readValue(jsonString, SystemUsageInfo.class);
			return sInfo;
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	
	
	
}
