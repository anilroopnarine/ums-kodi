package net.pms.external.xbmc.folders;

import java.util.List;

import net.pms.dlna.virtual.VirtualFolder;

public abstract class ListFolder extends VirtualFolder {

	public ListFolder(String name) {
		super(name, null);
	}

	@Override
	public void discoverChildren() {
		for (VirtualFolder folder : getList()) {
			addChild(folder);
		}
	}

	public abstract List<VirtualFolder> getList();
}
