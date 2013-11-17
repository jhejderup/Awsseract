package nl.tudelft.ec2interface.instancemanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import nl.tudelft.ec2interface.sysmonitor.SystemUsageInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class RemoteActorInfo {
	
	private String publicIP;
	private String actorPath;
	private String selfPublicIP;
	private String selfInstanceID;
	
	public String getPublicIP() {
		return publicIP;
	}

	public void setPublicIP(String publicIP) {
		this.publicIP = publicIP;
	}

	public String getActorPath() {
		return actorPath;
	}

	public void setActorPath(String actorPath) {
		this.actorPath = actorPath;
	}

	public String ToJson(RemoteActorInfo maInfo)
	{
		try {
			
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(maInfo);
			return json;
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
			
		}
		
	}
	
	public RemoteActorInfo FromJson(String jsonString)
	{
		try {
			
			RemoteActorInfo maInfo = new ObjectMapper().readValue(jsonString, RemoteActorInfo.class);
			return maInfo;
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getSelfPublicIP() {
		return selfPublicIP;
	}

	public void setSelfPublicIP(String selfPublicIP) {
		this.selfPublicIP = selfPublicIP;
	}

	public String getSelfInstanceID() {
		return selfInstanceID;
	}

	public void setSelfInstanceID(String selfInstanceID) {
		this.selfInstanceID = selfInstanceID;
	}

	public RemoteActorInfo setInfo(String publicIP, String actorPath, String selfPublicIP, String selfInstanceID)
	{
		RemoteActorInfo maInfo = new RemoteActorInfo();
		maInfo.setPublicIP(publicIP);
		maInfo.setActorPath(actorPath);
		maInfo.setSelfPublicIP(selfPublicIP);
		maInfo.setSelfInstanceID(selfInstanceID);
		return maInfo;
	}
	
	public RemoteActorInfo getInfoFromFile(String filePath)
	{
		try {
			String jsonString = new Scanner(new File(filePath)).useDelimiter("\\Z").next();
			return new RemoteActorInfo().FromJson(jsonString);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args)
	{
		RemoteActorInfo maInfo = new RemoteActorInfo().setInfo("127.0.0.1:2552", "akka.tcp://MasterNode@127.0.0.1:2552/user/masterActor", "selfPublicIP", "selfInstanceID");
		
		String maInfoString = maInfo.ToJson(maInfo).toString();
		
		System.out.println(maInfoString);
		System.out.println(maInfo.FromJson(maInfoString).getPublicIP());
		System.out.println(maInfo.FromJson(maInfoString).getActorPath());
		System.out.println(maInfo.FromJson(maInfoString).getSelfPublicIP());
		System.out.println(maInfo.FromJson(maInfoString).getSelfInstanceID());
		System.out.println(new RemoteActorInfo().getInfoFromFile("conf/masterInfo").getPublicIP());
		
	}

}
