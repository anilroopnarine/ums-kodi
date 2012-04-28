package net.pms.external.xbmc.info;

import java.io.File;
import java.net.MalformedURLException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import net.pms.external.XBMCLog;

public class MediaFile {

	private File localFile;
	private SmbFile remoteFile;
	private boolean isRemote;

	public MediaFile(String filePath) {
		if (filePath.toLowerCase().indexOf("smb") != -1) {
			try {
				remoteFile = new SmbFile(filePath);
				isRemote = true;
			} catch (MalformedURLException e) {
				XBMCLog.error(e);
			}
		} else {
			localFile = new File(filePath);
			isRemote = false;
		}
		
	}

	public File getLocalFile() {
		return localFile;
	}

	public void setLocalFile(File localFile) {
		this.localFile = localFile;
	}

	public SmbFile getRemoteFile() {
		return remoteFile;
	}

	public void setRemoteFile(SmbFile remoteFile) {
		this.remoteFile = remoteFile;
	}

	public boolean isRemote() {
		return isRemote;
	}

	public void setRemote(boolean isRemote) {
		this.isRemote = isRemote;
	}

	public boolean exists() {
		try {
			return isRemote ? remoteFile.exists() : localFile.exists();
		} catch (SmbException e) {
			XBMCLog.error(e);
			return false;
		}
	}

	public String getName() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public boolean isDirectory() {
	    // TODO Auto-generated method stub
	    return false;
    }

	public MediaFile[] listFiles() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public boolean isFile() {
	    // TODO Auto-generated method stub
	    return false;
    }

	public boolean isHidden() {
	    // TODO Auto-generated method stub
	    return false;
    }

}
