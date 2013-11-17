package nl.tudelft.ec2interface.instancemanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

public class EC2Interface {

	private AmazonEC2 ec2;

	public static void main(String[] args) throws IOException {
		EC2Interface ec2 = new EC2Interface("conf/AwsCredentials.properties");
		String instanceId = ec2.runNewInstance("ami-028eb847");
		//System.out.println(ec2.getInstanceInfo(instanceId));
		
		//master
		ec2.configureInstance("noip", instanceId, "conf/remoteConfigureMaster.sh", "conf/joseph_wing.pem");
		
		//worker
		//ec2.configureInstance("ec2-54-219-179-208.us-west-1.compute.amazonaws.com", instanceId, "conf/remoteConfigureWorker.sh", "conf/joseph_wing.pem");
		
		
		
		//System.out.println(ec2.getInstanceList().toString());
		//ec2.terminateInstance(instanceId);
		//ec2.terminateInstances(new ArrayList<String>(){{add("i-b8e4a1e3");add("i-5f879804");}});
	}
	
	public EC2Interface(String credentialsFilePath) {
		
		// Create the AmazonEC2Client object so we can call various APIs.
		try {
			AWSCredentials credentials = new PropertiesCredentials(new FileInputStream(credentialsFilePath));
			ec2 = new AmazonEC2Client(credentials);
			Region usWest1 = Region.getRegion(Regions.US_WEST_1);
			ec2.setRegion(usWest1);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public String runNewInstance(String imageId)
	{
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

		runInstancesRequest
				.withImageId(imageId)
				.withInstanceType("t1.micro")
				.withMinCount(1)
				.withMaxCount(1)
				.withKeyName("joseph_wing")
				.withSecurityGroups("sg_testinstance1")
				.withPlacement(new Placement("us-west-1c"));

		RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);
		List<Instance> instances = runInstancesResult.getReservation().getInstances();
		
		String instanceId = instances.get(0).getInstanceId();
		System.out.println("A ARunInstance request has been sent. Find it with the instance id " + instanceId);
		
		return instanceId;
	}
	
	public void terminateInstance(String instanceId)
	{
		ArrayList<String> instanceIds = new ArrayList<String>();
		instanceIds.add(instanceId);
		terminateInstances(instanceIds);
	}
	
	public void terminateInstances(ArrayList<String> instanceIds)
	{
		System.out.println("A TerminateInstance request has been sent for instances with id " + instanceIds);
		
		try {
        	// Terminate instances.
        	TerminateInstancesRequest terminateRequest = new TerminateInstancesRequest(instanceIds);
        	ec2.terminateInstances(terminateRequest);
    	} catch (AmazonServiceException e) {
    		// Write out any exceptions that may have occurred.
           System.out.println("Error terminating instances");
           System.out.println("Caught Exception: " + e.getMessage());
           System.out.println("Reponse Status Code: " + e.getStatusCode());
           System.out.println("Error Code: " + e.getErrorCode());
           System.out.println("Request ID: " + e.getRequestId());
        }
	}
	
	public InstanceInfo getInstanceInfo(String instanceId)
	{
		InstanceInfo iInfo = null;
		
		DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest().withInstanceIds(instanceId);
		DescribeInstancesResult describeInstancesResult = ec2.describeInstances(describeInstancesRequest);
		List<Reservation> list = describeInstancesResult.getReservations();
		
		for (Reservation res : list) {
			List<Instance> instancelist = res.getInstances();

			for (Instance instance : instancelist) {
				iInfo = InstanceInfo.ReadInfo(instance);
			}
		}

		return iInfo;
	}
	
	public ArrayList<String> getInstanceList()
	{
		ArrayList<String> instanceIds = new ArrayList<String>();
		
		DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
		DescribeInstancesResult describeInstancesResult = ec2.describeInstances(describeInstancesRequest);
		List<Reservation> list = describeInstancesResult.getReservations();
		
		for (Reservation res : list) {
			List<Instance> instancelist = res.getInstances();

			for (Instance instance : instancelist) {
				if(instance.getKeyName().equals("joseph_wing"))
					instanceIds.add(instance.getInstanceId());
			}
		}
		return instanceIds;
	}
	
	public void configureInstance(String masterPublicIP, String instanceId, String scriptFilePath, String keyFilePath)
	{
		boolean configured = false;
		
		System.out.println("Start applying configuration to instance " + instanceId);
		
		do {
			try {
				
				InstanceInfo iInfo = getInstanceInfo(instanceId);
				if (iInfo.getStatus().equals("running")) {
					System.out.println("ssh -i joseph_wing.pem ubuntu@"+iInfo.getPublicIP());
					System.out.println("-->SSH Connection to IP " + iInfo.getPublicIP() + ", the configuration process will be terminated after 10 minutes.");
					
					execConfigurationTask(new InstanceConfigurator(masterPublicIP, iInfo.getId(), iInfo.getPublicIP(), scriptFilePath, keyFilePath));
					configured = true;
				}
				else
				{
					System.out.println("-->Instance " + instanceId + " is not running yet, try again in 60 seconds : " + getInstanceInfo(instanceId));
				}
				
				Thread.sleep(60 * 1000);
			} catch (AmazonServiceException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (!configured);
		
		System.out.println("-->Presumably instance " +  instanceId + " is correctly configured after 10 minutes");
		
	}
	
	private void execConfigurationTask(InstanceConfigurator iConfigurator)
	{
		try {
    	Set<Callable<String>> callables = new HashSet<Callable<String>>();
    	callables.add(iConfigurator);
    	
    	ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.invokeAll(callables,10, TimeUnit.MINUTES);
    	executor.shutdown();
    	
    	} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
	}
	
	public static class InstanceConfigurator implements Callable<String>
    {
        private String selfPublicIP;
        private String selfInstanceID;
        private String scriptFilePath;
        private String keyFilePath;
        private String masterPublicIP;
        
        public InstanceConfigurator (String masterPublicIP, String selfInstanceID, String selfPublicIP, String scriptFilePath, String keyFilePath)
        {
        	this.masterPublicIP = masterPublicIP;
        	this.selfInstanceID = selfInstanceID;
            this.selfPublicIP = selfPublicIP;
            this.scriptFilePath = scriptFilePath;
            this.keyFilePath = keyFilePath;
        }

        @Override
        public String call() throws Exception {

    		try {
    			final ProcessBuilder pb = new ProcessBuilder("/bin/sh", scriptFilePath, selfPublicIP , keyFilePath, masterPublicIP, selfPublicIP, selfInstanceID);
    			pb.directory(new File("."));
    			pb.redirectErrorStream(true);
    			final Process p = pb.start();
    			//final int processStatus = p.waitFor();
    			
    		    InputStream is = p.getInputStream();
    			InputStreamReader isr = new InputStreamReader(is);
    			BufferedReader br = new BufferedReader(isr);
    			String line;
    			while ((line = br.readLine()) != null) {
    			  System.out.println(line);
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            return "success";
        }
    }

}
