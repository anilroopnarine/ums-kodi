package net.pms.external.xbmc.folders;

import java.util.List;

import net.pms.dlna.RealFile;
import net.pms.dlna.virtual.VirtualFolder;

public abstract class ListFiles extends VirtualFolder {

	public ListFiles(String name) {
		super(name, null);
	}

	@Override
	public void discoverChildren() {
		for (RealFile folder : getList()) {
			addChild(folder);
		}
	}

	public abstract List<RealFile> getList();
}
