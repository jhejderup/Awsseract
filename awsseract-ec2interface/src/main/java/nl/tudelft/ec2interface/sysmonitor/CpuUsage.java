package nl.tudelft.ec2interface.sysmonitor;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarLoader;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.cmd.Shell;
import org.hyperic.sigar.cmd.SigarCommandBase;

public class CpuUsage extends SigarCommandBase {

    public CpuUsage(Shell shell) {
        super(shell);
    }

    public CpuUsage() {
        super();
    }

    public void output(String[] args) throws SigarException {

    }

    public CpuInfo getInfo()
    {
    	
		try {
			
			org.hyperic.sigar.CpuInfo info;
			info = (this.sigar.getCpuInfoList())[0];
			CpuPerc cpu = this.sigar.getCpuPerc();
	        
	        CpuInfo cInfo = new CpuInfo();
	        cInfo.setCpuModel(info.getVendor() + " " + info.getModel());
	        cInfo.setUserTime((int) Math.round(cpu.getUser() * 100));
	        cInfo.setSysTime((int) Math.round(cpu.getSys() * 100));
	        cInfo.setIdleTime((int) Math.round(cpu.getIdle() * 100));
	        cInfo.setUsage(100-cInfo.getIdleTime());
	        
	        return cInfo;
	        
		} catch (SigarException e) {
			e.printStackTrace();
			return null;
		}
        
    }
    
    public static void main(String[] args) throws Exception {
        new CpuUsage().getInfo();
    }
}
