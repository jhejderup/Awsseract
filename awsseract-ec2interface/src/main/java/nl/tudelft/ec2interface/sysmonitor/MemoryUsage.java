package nl.tudelft.ec2interface.sysmonitor;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Swap;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.cmd.Shell;
import org.hyperic.sigar.cmd.SigarCommandBase;

public class MemoryUsage extends SigarCommandBase {

    public MemoryUsage(Shell shell) {
        super(shell);
    }

    public MemoryUsage() {
        super();
    }
    
    public void output(String[] args) throws SigarException {
    }
    
    public MemoryInfo getInfo()
    {
        try {
        	MemoryInfo mInfo = new MemoryInfo();
			Mem mem   = this.sigar.getMem();
			mInfo.setTotalSystemMemory((int) (mem.getTotal()/1024/1024));
			mInfo.setUsedSystemMemory((int) (mem.getUsed()/1024/1024));
			mInfo.setActualSystemMemory((int) (mem.getActualUsed()/1024/1024));
			mInfo.setUsage(mInfo.getUsedSystemMemory()*100/mInfo.getTotalSystemMemory());
			mInfo.setActualUsage(mInfo.getActualSystemMemory()*100/mInfo.getTotalSystemMemory());

			return mInfo;
        } catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }

    public static void main(String[] args) throws Exception {
        new MemoryUsage().getInfo();
    }
}
