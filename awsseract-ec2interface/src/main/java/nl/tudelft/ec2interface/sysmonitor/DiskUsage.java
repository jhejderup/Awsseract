package nl.tudelft.ec2interface.sysmonitor;

import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.NfsFileSystem;
import org.hyperic.sigar.cmd.Shell;
import org.hyperic.sigar.cmd.SigarCommandBase;

public class DiskUsage extends SigarCommandBase {

    public DiskUsage(Shell shell) {
        super(shell);
    }

    public DiskUsage() {
        super();
    }

    public void output(String[] args) throws SigarException {

    }
    
	public DiskInfo getInfo() {
		try {

			FileSystem fs = (this.proxy.getFileSystemList())[0];
			FileSystemUsage usage;
			if (fs instanceof NfsFileSystem) {
				NfsFileSystem nfs = (NfsFileSystem) fs;
				if (!nfs.ping()) {
					println(nfs.getUnreachableMessage());
					return null;
				}
			}
			
			usage = this.sigar.getFileSystemUsage(fs.getDirName());
			DiskInfo dInfo = new DiskInfo();
			dInfo.setTotalDiskSpace((int) usage.getTotal() / 1024);
			dInfo.setUsedDiskSpace((int) (usage.getTotal() - usage.getFree()) / 1024);
			dInfo.setUsage(dInfo.getUsedDiskSpace() * 100 / dInfo.getTotalDiskSpace());
			
			return dInfo;
			
		} catch (SigarException e) {
			return null;
		}
	}

    public static void main(String[] args) throws Exception {
        new DiskUsage().getInfo();
    }
}
