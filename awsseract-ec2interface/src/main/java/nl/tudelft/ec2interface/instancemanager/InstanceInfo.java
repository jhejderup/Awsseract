package nl.tudelft.ec2interface.instancemanager;

import com.amazonaws.services.ec2.model.Instance;

public class InstanceInfo {

	private String id;
	private String publicIP;
	private String status;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPublicIP() {
		return publicIP;
	}
	
	public void setPublicIP(String publicIP) {
		this.publicIP = publicIP;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public static InstanceInfo ReadInfo(Instance instance)
	{
		InstanceInfo iInfo = new InstanceInfo();
		
		iInfo.setId(instance.getInstanceId());
		iInfo.setPublicIP(instance.getPublicDnsName());
		iInfo.setStatus(instance.getState().getName());
		
		return iInfo;
	}

	@Override
	public String toString() {
		return "InstanceInfo [id=" + id + ", publicIP=" + publicIP
				+ ", status=" + status + "]";
	}
	
}
